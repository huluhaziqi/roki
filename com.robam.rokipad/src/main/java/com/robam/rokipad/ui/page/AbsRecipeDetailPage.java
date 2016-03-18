package com.robam.rokipad.ui.page;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.FavorityBookRefreshEvent;
import com.robam.common.events.TodayBookRefreshEvent;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.service.CookTaskService;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.PageKey;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

abstract public class AbsRecipeDetailPage extends BasePage {

    @InjectView(R.id.imgBack)
    protected ImageView imgBack;

    @InjectView(R.id.imgPause)
    protected ImageView imgPause;

    @InjectView(R.id.imgExit)
    protected ImageView imgExit;

    @InjectView(R.id.imgToday)
    protected ImageView imgToday;

    @InjectView(R.id.imgFavority)
    protected ImageView imgFavority;

    @InjectView(R.id.imgRecipe)
    protected ImageView imgRecipe;

    @InjectView(R.id.txtRecipe)
    protected TextView txtRecipe;

    @InjectView(R.id.pnlDynamic)
    protected FrameLayout pnlDynamic;

    protected Recipe book;

    abstract protected void loadDynamicView();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Bundle bd = getArguments();
        long bookId = bd.getLong(PageArgumentKey.BookId);

        View view = inflater.inflate(R.layout.frame_recipe_detail, container,
                false);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            initData(bookId);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Subscribe
    public void onEvent(TodayBookRefreshEvent event) {
        if (event.bookId != book.id)
            return;

        book.isToday = event.flag;
        imgToday.setSelected(book.isToday);
    }

    @Subscribe
    public void onEvent(FavorityBookRefreshEvent event) {
        if (event.bookId != book.id)
            return;

        book.isFavority = event.flag;
        imgFavority.setSelected(book.isFavority);
    }

    @OnClick(R.id.imgBack)
    public void onTitleBack() {
        UIService.getInstance().popBack();
    }

    @OnClick(R.id.imgPause)
    public void onTitlePause() {
        CookTaskService.getInstance().pause();
    }

    @OnClick(R.id.imgExit)
    public void onExit() {
        String message = "是否确定退出？";
        DialogHelper.newDialog_OkCancel(cx, null, message,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dlg, int witch) {
                        if (witch == DialogInterface.BUTTON_POSITIVE) {
                            onTitleBack();
                        }
                    }
                }).show();
    }

    @OnClick(R.id.imgToday)
    public void onToday() {

        if (UiHelper.checkAuth(PageKey.UserLogin))
            UiHelper.onToday(book);
    }

    @OnClick(R.id.imgFavority)
    public void onFavority() {

        if (UiHelper.checkAuth(PageKey.UserLogin))
            UiHelper.onFavority(book);
    }

    protected void initData(long bookId) {

        ProgressDialogHelper.setRunning(cx, true);
        CookbookManager.getInstance().getCookbookById(bookId,
                new Callback<Recipe>() {

                    @Override
                    public void onSuccess(Recipe book) {
                        loadData(book);
                        ProgressDialogHelper.setRunning(cx, false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ProgressDialogHelper.setRunning(cx, false);
                        ToastUtils.showThrowable(t);
                    }
                });
    }

    protected void loadData(Recipe book) {
        this.book = book;
        txtRecipe.setText(book.name);
        imgToday.setSelected(book.isToday);
        imgFavority.setSelected(book.isFavority);

        loadDynamicView();
    }

}
