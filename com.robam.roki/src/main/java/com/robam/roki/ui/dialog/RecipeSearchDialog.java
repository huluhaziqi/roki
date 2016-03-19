package com.robam.roki.ui.dialog;

import android.app.Activity;
import android.view.View;

import com.google.common.base.Strings;
import com.legent.Callback;
import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.api.ViewUtils;
import com.robam.common.services.CookbookManager;
import com.robam.roki.R;
import com.robam.roki.ui.view.RecipeSearchOptionView;
import com.robam.roki.ui.view.RecipeSearchTitleView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecipeSearchDialog extends AbsDialog {

    public static interface OnSearchCallback {
        void onSearchWord(String word);
    }

    static public RecipeSearchDialog show(Activity activity,
                                          OnSearchCallback callback) {
        RecipeSearchDialog dlg = new RecipeSearchDialog(activity, callback);
        dlg.show();
        return dlg;
    }

    @InjectView(R.id.title)
    RecipeSearchTitleView title;

    @InjectView(R.id.optHot)
    RecipeSearchOptionView optHot;

    @InjectView(R.id.optHistory)
    RecipeSearchOptionView optHistory;

    OnSearchCallback callback;

    private RecipeSearchDialog(Activity activity, OnSearchCallback callback) {
        super(activity, R.style.Theme_Dialog_FullScreen);
        ViewUtils.setFullScreen(activity, this);
        this.callback = callback;
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_search_recipe;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
        RecipeSearchTitleView.OnCancelSearchCallback titleCallback = new RecipeSearchTitleView.OnCancelSearchCallback() {

            @Override
            public void onCancel() {
                RecipeSearchDialog.this.dismiss();
            }

            @Override
            public void onWordChanged(String word) {
                onSearchWord(word);
            }
        };

        title.setOnCancelSearchCallback(titleCallback);

        initData();
    }

    void initData() {

        final RecipeSearchOptionView.OnWordSelectedCallback optCallback = new RecipeSearchOptionView.OnWordSelectedCallback() {

            @Override
            public void onWordSelected(String word) {
                onSearchWord(word);
            }
        };

        List<String> words = CookbookManager.getInstance().getCookingHistory();
        optHistory.loadData(words, optCallback);

        CookbookManager.getInstance().getHotKeysForCookbook(
                new Callback<List<String>>() {

                    @Override
                    public void onSuccess(List<String> result) {
                        optHot.loadData(result, optCallback);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        optHot.setVisibility(View.GONE);
                    }
                });

    }

    void onSearchWord(String word) {
        if (Strings.isNullOrEmpty(word))
            return;

        RecipeSearchDialog.this.dismiss();

        if (callback != null) {
            callback.onSearchWord(word);
        }
    }

}
