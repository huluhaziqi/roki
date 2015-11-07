package com.robam.roki.ui.page;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.FavorityBookCleanEvent;
import com.robam.common.events.FavorityBookRefreshEvent;
import com.robam.common.io.cloud.Reponses.CookbooksResponse;
import com.robam.common.services.CookbookManager;
import com.robam.roki.ui.view.RecipeGridView;

/**
 * Created by sylar on 15/6/14.
 */
public class RecipeFavorityPage extends AbsRecipeGridPage {

    @Subscribe
    public void onEvent(FavorityBookRefreshEvent event) {
        initData();
    }

    @Subscribe
    public void onEvent(FavorityBookCleanEvent event) {
        initData();
    }

    @Override
    protected void initData() {
        regsitRightView();

        ProgressDialogHelper.setRunning(cx, true);
        CookbookManager.getInstance().getFavorityCookbooks(
                new Callback<CookbooksResponse>() {

                    @Override
                    public void onSuccess(CookbooksResponse result) {
                        loadBooks(RecipeGridView.Model_Favority, result.cookbooks, result.cookbooks3rd);
                        ProgressDialogHelper.setRunning(cx, false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ProgressDialogHelper.setRunning(cx, false);
                        ToastUtils.showThrowable(t);
                    }
                });
    }


    @Override
    protected String getTextWhenEmpty() {
        return "还没有收藏呢";
    }

    void onClear() {
        DialogHelper.newDialog_OkCancel(cx, "确认清空我的收藏？", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ProgressDialogHelper.setRunning(cx, true);
                    CookbookManager.getInstance().delteAllFavorityCookbooks(new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ProgressDialogHelper.setRunning(cx, false);
                            ToastUtils.showShort("清空成功");
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ProgressDialogHelper.setRunning(cx, false);
                            ToastUtils.showThrowable(t);
                        }
                    });
                }
            }
        }).show();

    }


    void regsitRightView() {

        TextView txtView = TitleBar.newTitleTextView(cx, "清空", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
            }
        });

        titleBar.replaceRight(txtView);
    }
}
