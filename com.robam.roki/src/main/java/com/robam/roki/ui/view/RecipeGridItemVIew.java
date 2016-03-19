package com.robam.roki.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.legent.Callback;
import com.legent.VoidCallback2;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.DisplayUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.pojos.AbsRecipe;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Recipe3rd;
import com.robam.common.pojos.RecipeProvider;
import com.robam.common.services.CookbookManager;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.page.RecipeDetailPage;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class RecipeGridItemVIew extends FrameLayout {
    final float Small_Height_Scale = 251 / 640f;
    final float Middle_Height_Scale = 320 / 640f;

    @InjectView(R.id.imgDish)
    ImageView imgDish;
    @InjectView(R.id.imgSrcLogo)
    ImageView imgSrcLogo;
    @InjectView(R.id.edtName)
    TextView txtName;
    @InjectView(R.id.txtCollectCount)
    TextView txtCollectCount;

    Context cx;
    AbsRecipe book;
    int modelType;
    int smallHeight, middleHeight;

    public RecipeGridItemVIew(Context context, int modelType) {
        super(context);
        computeHeight();
        this.modelType = modelType;
        init(context);
    }

    @OnClick(R.id.layout)
    public void onClickDetail() {
        Bundle bd = new Bundle();
        bd.putLong(PageArgumentKey.BookId, book.id);

        if (book.isRoki()) {
            onShowDetail((Recipe) book);
        } else {
            Recipe3rd r = (Recipe3rd) book;
            bd.putString(PageArgumentKey.Url, r.detailUrl);
            bd.putString(PageArgumentKey.WebTitle, r.name);
            UIService.getInstance().postPage(PageKey.RecipeDetail3rd, bd);
        }
    }

    @OnLongClick(R.id.layout)
    public boolean onLongClick(View view) {

        switch (modelType) {
            case RecipeGridView.Model_Favority:
                onDelete(new VoidCallback2() {
                    @Override
                    public void onCompleted() {
                        UiHelper.onFavority(book);
                    }
                });
                return true;
            default:
                return true;
        }

    }

    void init(Context cx) {
        this.cx = cx;
        View view = LayoutInflater.from(cx).inflate(R.layout.view_recipe_grid_item,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
    }

    void computeHeight() {
        int screenWidth = DisplayUtils.getScreenWidthPixels(getContext());
        smallHeight = (int) (screenWidth * Small_Height_Scale);
        middleHeight = (int) (screenWidth * Middle_Height_Scale);
    }

    void showData(AbsRecipe book, final boolean isSmallSize) {
        this.book = book;
        imgDish.getLayoutParams().height = isSmallSize ? smallHeight : middleHeight;
        txtName.setText(book.name);
        imgDish.setImageDrawable(null);
        txtCollectCount.setText(String.format("收藏（%s）", book.collectCount));

        imgSrcLogo.setImageDrawable(null);
        imgDish.setImageDrawable(null);
        if (book.isRoki()) {
            Recipe b = (Recipe) book;
            ImageUtils.displayImage(isSmallSize ? b.imgSmall : b.imgMedium, imgDish);
            imgSrcLogo.setImageResource(R.mipmap.ic_recipe_roki_logo);
        } else {
            Recipe3rd b = (Recipe3rd) book;

            if (!Strings.isNullOrEmpty(b.imgUrl)) {
                ImageUtils.displayImage(b.imgUrl.trim(), imgDish);
            }

            RecipeProvider provider = b.getProvider();
            if (provider != null) {
                ImageUtils.displayImage(provider.logoUrl, imgSrcLogo);
            }
        }
    }

    void onDelete(final VoidCallback2 callback) {
        DialogHelper.newDialog_OkCancel(getContext(), "确认删除此项?", null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (callback != null) {
                                if (UiHelper.checkAuthWithDialog(getContext(), PageKey.UserLogin)) {
                                    callback.onCompleted();
                                }
                            }
                        }
                    }
                }
        ).show();
    }

    void onShowDetail(Recipe recipe) {
        if (recipe.isNewest()) {
            RecipeDetailPage.show(recipe.id);
        } else {
            ProgressDialogHelper.setRunning(cx, true);
            CookbookManager.getInstance().getCookbookById(recipe.id, new Callback<Recipe>() {
                @Override
                public void onSuccess(Recipe recipe) {
                    ProgressDialogHelper.setRunning(cx, false);
                    RecipeDetailPage.show(recipe.id);
                }

                @Override
                public void onFailure(Throwable t) {
                    ProgressDialogHelper.setRunning(cx, false);
                    ToastUtils.showThrowable(t);
                }
            });
        }
    }
}
