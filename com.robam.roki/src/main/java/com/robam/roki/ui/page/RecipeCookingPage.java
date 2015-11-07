package com.robam.roki.ui.page;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.common.collect.Lists;
import com.legent.Callback;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.adapters.ExtPageAdapter;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.CookStep;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.roki.R;
import com.robam.roki.service.CookTaskService;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.dialog.CookCompletedDialog;
import com.robam.roki.ui.view.RecipeCookingView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by sylar on 15/6/14.
 */
public class RecipeCookingPage extends BasePage {

    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.imgReturn)
    ImageView imgBack;


    Recipe book;
    int stepIndex;
    ExtPageAdapter adapter;
    CookTaskService cts = CookTaskService.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_recipe_cooking, container, false);
        ButterKnife.inject(this, view);

        imgBack.setVisibility(View.INVISIBLE);

        Bundle bd = getArguments();
        long bookId = bd.getLong(PageArgumentKey.BookId);
        stepIndex = bd.getInt(PageArgumentKey.RecipeStepIndex, 0);

        adapter = new ExtPageAdapter();
        pager.setAdapter(adapter);
        initData(bookId);


        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @OnClick(R.id.imgReturn)
    public void onClickBack() {
        if (stepIndex > 0) {
            pager.setCurrentItem(stepIndex - 1);
        }
    }

    @OnClick(R.id.imgClose)
    public void onClickClose() {
        onCookingExit();
    }

    @OnPageChange(R.id.pager)
    public void onPageSelected(int position) {

        if (position > stepIndex) {
            onCookingNext();
        } else if (position < stepIndex) {
            onCookingBack();
        }

        stepIndex = position;
        imgBack.setVisibility(stepIndex != 0 ? View.VISIBLE : View.INVISIBLE);
        adapter.getPage(position).invalidate();
    }

    void initData(long bookId) {

        ProgressDialogHelper.setRunning(cx, true);
        CookbookManager.getInstance().getCookbookById(bookId,
                new Callback<Recipe>() {

                    @Override
                    public void onSuccess(Recipe cookbook) {
                        ProgressDialogHelper.setRunning(cx, false);
                        initPages(cookbook);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ProgressDialogHelper.setRunning(cx, false);
                        ToastUtils.showThrowable(t);
                    }
                });

    }

    void initPages(Recipe cookbook) {
        book = cookbook;
        List<View> views = Lists.newArrayList();
        List<CookStep> list = book.getCookSteps();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                RecipeCookingView view = new RecipeCookingView(cx, book, i, pageNextCallback);
                views.add(view);
            }
        }

        adapter.loadViews(views);
        pager.setCurrentItem(stepIndex, true);
    }


    UIListeners.CookingNextCallback pageNextCallback = new UIListeners.CookingNextCallback() {

        @Override
        public void onClickNext() {
            boolean isLast = stepIndex + 1 == adapter.getCount();
            if (isLast) {
                onCookingFinished();
            } else {
                pager.setCurrentItem(stepIndex + 1, true);
            }
        }
    };

    void onCookingBack() {
        cts.back();
    }

    void onCookingNext() {
        cts.next();
    }

    void onCookingPause() {
        cts.pause();
    }

    void onCookingStop() {
        cts.stop();
    }

    void onCookingFinished() {
        CookCompletedDialog dlg = CookCompletedDialog.show(cx, book.id);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onCookingStop();
            }
        });
    }

    void onCookingExit() {

        boolean isLast = stepIndex + 1 == adapter.getCount();
        if (isLast && cts.getRemainTime() <= 0) {
            onCookingFinished();
        } else {
            String title = "正在烧菜中,是否确定退出？";
            DialogHelper.newDialog_OkCancel(cx, title, null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        onCookingStop();
                    }
                }
            }).show();
        }
    }

}
