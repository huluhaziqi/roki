package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.dao.DaoHelper;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.views.ExtScrollView;
import com.legent.utils.api.DisplayUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.events.FavorityBookRefreshEvent;
import com.robam.common.events.TodayBookRefreshEvent;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.Helper;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.dialog.CookbookShareDialog;
import com.robam.roki.ui.view.RecipeDetailAlbumView;
import com.robam.roki.ui.view.RecipeDetailCookstepView;
import com.robam.roki.ui.view.RecipeDetailMaterialsView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/14.
 */
public class RecipeDetailPage extends BasePage {

    public static void show(long recipeId) {
        Bundle bd = new Bundle();
        bd.putLong(PageArgumentKey.BookId, recipeId);
        UIService.getInstance().postPage(PageKey.RecipeDetail, bd);
    }

    @InjectView(R.id.scrollView)
    ExtScrollView scrollView;
    @InjectView(R.id.titleView)
    View titleView;
    @InjectView(R.id.divTop)
    View divTop;
    @InjectView(R.id.divPrepare)
    View divPrepare;
    @InjectView(R.id.imgBookImage)
    ImageView imgBookImage;
    @InjectView(R.id.txtRecipeName)
    TextView txtRecipeName;
    @InjectView(R.id.txtStepCount)
    TextView txtStepCount;
    @InjectView(R.id.txtCookTime)
    TextView txtCookTime;
    @InjectView(R.id.txtCollectCount)
    TextView txtCollectCount;
    @InjectView(R.id.txtBookDesc)
    TextView txtBookDesc;
    @InjectView(R.id.imgPrestepImage)
    ImageView imgPrestepImage;
    @InjectView(R.id.materialsView)
    RecipeDetailMaterialsView materialsView;
    @InjectView(R.id.txtPrestepDesc)
    TextView txtPrestepDesc;
    @InjectView(R.id.cookstepView)
    RecipeDetailCookstepView cookstepView;
    @InjectView(R.id.albumView)
    RecipeDetailAlbumView albumView;
    @InjectView(R.id.imgToday)
    ImageView imgToday;
    @InjectView(R.id.imgFavority)
    ImageView imgFavority;

    Recipe book;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        long bookId = getArguments().getLong(PageArgumentKey.BookId);
        book = CookbookManager.getInstance().getRecipeById(bookId);

        View view = inflater.inflate(R.layout.page_recipe_detail, container, false);
        ButterKnife.inject(this, view);
        titleView.setAlpha(0.6f);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(scrollChangedListener);

        computeHeight();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    void initData() {

        if (book.isNewest()) {
            onRefresh();
        } else {
            CookbookManager.getInstance().getCookbookById(book.id, new Callback<Recipe>() {
                @Override
                public void onSuccess(Recipe recipe) {
                    book = recipe;
                    onRefresh();
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        }
    }

    void onRefresh() {
        Preconditions.checkNotNull(book);

        title = book.name;
        txtBookDesc.setText(book.desc);
        ImageUtils.displayImage(book.imgLarge, imgBookImage);

        if (book.preStep != null) {
            txtPrestepDesc.setText(book.preStep.desc);
            ImageUtils.displayImage(book.preStep.imageUrl, imgPrestepImage);
        }

        imgToday.setSelected(book.isToday);
        imgFavority.setSelected(book.isFavority);
        txtRecipeName.setText(book.name);
        txtStepCount.setText(String.valueOf(book.getCookSteps().size()));
        txtCookTime.setText(String.valueOf(book.needTime / 60));
        txtCollectCount.setText(String.valueOf(book.collectCount));

        materialsView.loadData(book);
        cookstepView.loadData(book);
        albumView.loadData(book.id);

        scrollView.smoothScrollTo(0, 0);
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

        DaoHelper.refresh(book);
        imgFavority.setSelected(book.isFavority);
        txtCollectCount.setText(String.valueOf(book.collectCount));
    }

    @OnClick(R.id.imgBack)
    public void onClickBack() {
        UIService.getInstance().popBack();
    }

    @OnClick(R.id.imgToday)
    public void onClickTrolley() {
        if (UiHelper.checkAuthWithDialog(cx, PageKey.UserLogin))
            UiHelper.onToday(book);
    }

    @OnClick(R.id.imgFavority)
    public void onClickFavority() {
        if (UiHelper.checkAuthWithDialog(cx, PageKey.UserLogin))
            UiHelper.onFavority(book);
    }

    @OnClick(R.id.imgShare)
    public void onClickShare() {
        CookbookShareDialog.show(cx, book);

    }

    @OnClick(R.id.txtConfirm)
    public void onClickStartCook(View view) {
        Helper.startCook(cx, book);
    }


    void computeHeight() {

        //
        int screenWidth = DisplayUtils.getScreenWidthPixels(cx);
        int height = (int) (screenWidth * 280 / 320f);

        ViewGroup.LayoutParams lp = divTop.getLayoutParams();
        lp.height = height;
        divTop.setLayoutParams(lp);

        height = (int) (screenWidth * 885 / 975f);
        lp = divPrepare.getLayoutParams();
        lp.height = height;
        divPrepare.setLayoutParams(lp);
    }

    ViewTreeObserver.OnScrollChangedListener scrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {

        @Override
        public void onScrollChanged() {

            if (scrollView == null || scrollView.getChildCount() == 0) return;

            View contentView = scrollView.getChildAt(0);
            int contentHeight = contentView.getHeight();
            int parentHeight = scrollView.getHeight();
            int scrollY = scrollView.getScrollY();

            float per = scrollY * 1f / (contentHeight - parentHeight);
            float alpha = 0.6f + per * 0.4f;

//            Log.d("roki", String.format("contentHeight:%s parentHeight:%s scrollY:%s, alpha:%s",
//                    contentHeight, parentHeight, scrollY, alpha));
            titleView.setAlpha(alpha);
        }
    };
}
