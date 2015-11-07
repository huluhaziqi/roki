package com.robam.roki.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.legent.Callback;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.ui.ext.views.DelayEditText;
import com.legent.utils.api.SoftInputUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.io.cloud.Reponses.*;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Recipe3rd;
import com.robam.common.services.CookbookManager;
import com.robam.roki.R;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.view.EmojiEmptyView;
import com.robam.roki.ui.view.RecipeGridView;
import com.robam.roki.ui.view.RecipeSearchOptionView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecipeSearchPage extends BasePage {

    public static void show() {
        UIService.getInstance().postPage(PageKey.RecipeSearch);
    }

    @InjectView(R.id.edtSearch)
    DelayEditText edtSearch;
    @InjectView(R.id.emptyView)
    EmojiEmptyView emptyView;
    @InjectView(R.id.gridView)
    RecipeGridView gridView;
    @InjectView(R.id.divWithoutResult)
    View divWithoutResult;
    @InjectView(R.id.optHot)
    RecipeSearchOptionView optHot;
    @InjectView(R.id.optHistory)
    RecipeSearchOptionView optHistory;
    @InjectView(R.id.divWithResult)
    View divWithResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_recipe_search, container,
                false);
        ButterKnife.inject(this, view);

        emptyView.setText(null);
        edtSearch.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        edtSearch.setOnTextChangedCallback(txtChangedCallback);
        toggleSoftInput();
        switchDiv(false);
        initData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.txtCancel)
    public void onClickCancel() {
        UIService.getInstance().popBack();
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

    void switchDiv(boolean isSearch) {
        divWithoutResult.setVisibility(isSearch ? View.GONE : View.VISIBLE);
        divWithResult.setVisibility(!isSearch ? View.GONE : View.VISIBLE);
    }

    void switchView(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        gridView.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
    }

    private void toggleSoftInput() {
        SoftInputUtils.show(cx, edtSearch);
    }


    void onSearchWord(String word) {
        boolean isSearch = !Strings.isNullOrEmpty(word);


        if (isSearch) {
            CookbookManager.getInstance().saveHistoryKeysForCookbook(word);

            ProgressDialogHelper.setRunning(cx, true);
            CookbookManager.getInstance().getCookbooksByName(word,
                    new Callback<CookbooksResponse>() {

                        @Override
                        public void onSuccess(CookbooksResponse result) {
                            ProgressDialogHelper.setRunning(cx, false);
                            loadBooks(RecipeGridView.Model_Search, result.cookbooks, result.cookbooks3rd);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ProgressDialogHelper.setRunning(cx, false);
                            ToastUtils.showThrowable(t);
                        }
                    });
        }
    }

    protected void loadBooks(int modelType, List<Recipe> books) {
        List<Recipe3rd> books3rd = Lists.newArrayList();
        loadBooks(modelType, books, books3rd);
    }

    protected void loadBooks(int modelType, List<Recipe> books, List<Recipe3rd> books3rd) {
        switchDiv(true);
        switchView((books == null || books.size() == 0) && (books3rd == null || books3rd.size() == 0));
        gridView.loadData(modelType, books, books3rd);
    }

    DelayEditText.OnTextChangedCallback txtChangedCallback = new DelayEditText.OnTextChangedCallback() {
        @Override
        public void onTextChanged(String s) {
            onSearchWord(s);
        }

        @Override
        public void onTextChangedWithoutDelay(String s) {

        }
    };


}
