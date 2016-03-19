package com.robam.roki.ui.page;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.CookMomentsRefreshEvent;
import com.robam.common.pojos.CookAlbum;
import com.robam.common.services.CookbookManager;
import com.robam.roki.R;
import com.robam.roki.ui.view.EmojiEmptyView;
import com.robam.roki.ui.view.MomentGridView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sylar on 15/6/14.
 */
public class RecipeCookMomentsPage extends HeadPage {

    @InjectView(R.id.emptyView)
    EmojiEmptyView emptyView;
    @InjectView(R.id.gridView)
    MomentGridView gridView;

    @Override
    public View onCreateContentView(LayoutInflater inflater,
                                    ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_recipe_cook_moments, container,
                false);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            regsitRightView();

            emptyView.setText("还没有晒过呢");
            initData();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Subscribe
    public void onEvent(CookMomentsRefreshEvent event) {
        initData();
    }

    protected void initData() {

        ProgressDialogHelper.setRunning(cx, true);

        CookbookManager.getInstance().getMyCookAlbums(new Callback<List<CookAlbum>>() {
            @Override
            public void onSuccess(List<CookAlbum> cookAlbums) {
                ProgressDialogHelper.setRunning(cx, false);
                switchView(cookAlbums == null || cookAlbums.size() == 0);
                gridView.loadData(cookAlbums);
            }

            @Override
            public void onFailure(Throwable t) {
                ProgressDialogHelper.setRunning(cx, false);
                ToastUtils.showThrowable(t);
            }
        });
    }

    void switchView(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        gridView.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
    }

    void regsitRightView() {

        TextView txtView = TitleBar.newTitleTextView(cx, "清空", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.newDialog_OkCancel(cx, "确认清空所有厨艺照片？", null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            ProgressDialogHelper.setRunning(cx, true);
                            CookbookManager.getInstance().clearMyCookAlbums(new VoidCallback() {
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
        });

        titleBar.replaceRight(txtView);
    }

}