package com.robam.roki.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.Callback;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.ui.ext.views.TagCloudView;
import com.legent.utils.api.PreferenceUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.io.cloud.RokiRestHelper;
import com.robam.common.pojos.Advert;
import com.robam.common.pojos.MaintainInfo;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.common.services.StoreService;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.dialog.LostRecipeDialog;
import com.robam.roki.ui.dialog.MaintainHomeDialog;
import com.robam.roki.ui.page.RecipeSearchPage;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HomeRecipeView extends FrameLayout implements UIListeners.IRefresh {

    final static String Prefs_Maintain_First = "Maintain_First";
    final static String Prefs_LostRecipe_First = "LostRecipe_First";

    ColorStateList tagTextColor;

    @InjectView(R.id.divTop_recipe_home)
    View divTop;

    @InjectView(R.id.tagView)
    TagCloudView tagView;

    @InjectView(R.id.imgEmoji)
    ImageView emoji;
    private ImageView recipe;

    public HomeRecipeView(Context context) {
        super(context);
        init(context, null);
    }

    void init(Context cx, AttributeSet attrs) {
        tagTextColor = cx.getResources().getColorStateList(R.color.home_recipe_tag_text_color_selector);

        View view = LayoutInflater.from(cx).inflate(R.layout.view_home_recipe,
                this, true);
        LostRecipeDialog.newInstance(cx);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            initTitleData();
            setGesture(divTop);
            setGesture(emoji);
            //popoupMaintainOnlyFirst();
            popoupLostrecipeOnlyFirst();
        }

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onRefresh();
    }


    @OnClick(R.id.imgSeasoning)
    public void onClickSeasoning() {
        UIService.getInstance().postPage(PageKey.RecipeSeasoning);
    }

    @OnClick({R.id.imgHistory, R.id.imgEmoji})
    public void onClickHistory() {
        UIService.getInstance().postPage(PageKey.RecipeHistory);
    }

    @OnClick(R.id.imgSearch)
    public void onClickSearch() {
        onSearch();
    }

    @Override
    public void onRefresh() {
        final Context cx = getContext();
        ProgressDialogHelper.setRunning(cx, true);
        CookbookManager.getInstance().getRecommendCookbooks(new Callback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                ProgressDialogHelper.setRunning(cx, false);
                onLoadTags(recipes);
            }

            @Override
            public void onFailure(Throwable t) {
                ProgressDialogHelper.setRunning(cx, false);
                ToastUtils.showThrowable(t);
            }
        });
    }

    private void initTitleData() {
        StoreService.getInstance().getHomeTitleForMob(new Callback<List<Advert.MobAdvert>>() {
            @Override
            public void onSuccess(List<Advert.MobAdvert> mobAdverts) {
                ImageUtils.displayImage(mobAdverts.get(0).getImgUrl(), emoji, ImageUtils.defaultOptions);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    void onLoadTags(List<Recipe> list) {
        if (list == null || list.size() == 0) return;

        List<TextView> views = Lists.newArrayList();

        for (Recipe r : list) {
            views.add(getTextView(r));
        }

        tagView.setCloudTags(views);
    }

    void onSearch() {
        RecipeSearchPage.show();
    }

    TextView getTextView(final Recipe recipe) {

        TextView txt = new TextView(getContext());
        txt.setTag(recipe);
        txt.setTextColor(tagTextColor);
        txt.setTextSize(22f);
        txt.setText(recipe.name);

        txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe r = (Recipe) v.getTag();
                onClickRecipe(r);
            }
        });

        return txt;
    }

    void onClickRecipe(Recipe r) {
        if (r != null) {
            Bundle bd = new Bundle();
            bd.putLong(PageArgumentKey.BookId, r.id);
            UIService.getInstance().postPage(PageKey.RecipeBanner, bd);
        }
    }

    //----------------------------------------------------保养服务

    void setGesture(View rootView) {
        final GestureDetector gd = new GestureDetector(getContext(), new GestureCallback());
        rootView.setClickable(true);
        emoji.setClickable(true);
        rootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        });
        emoji.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        });
    }

    void popoupMaintainOnlyFirst() {

        postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = PreferenceUtils.getBool(Prefs_Maintain_First, true);
                if (isFirst) {
                    MaintainHomeDialog.show(getContext());
                    PreferenceUtils.setBool(Prefs_Maintain_First, false);
                }
            }
        }, 500);

    }

    void popoupLostrecipeOnlyFirst() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = PreferenceUtils.getBool(Prefs_LostRecipe_First, true);
                if (isFirst) {
                    LostRecipeDialog.show(getContext());
                    PreferenceUtils.setBool(Prefs_LostRecipe_First, false);
                }

            }
        }, 500);
    }

    void onMaintain() {
        RokiRestHelper.queryMaintain(new Callback<MaintainInfo>() {
            @Override
            public void onSuccess(MaintainInfo mi) {
                if (mi == null || mi.postTime == 0) {
                    MaintainHomeDialog.show(getContext());
                } else {
                    ToastUtils.showShort("您已预约过本服务！请前往【个人】栏查看。");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                MaintainHomeDialog.show(getContext());
            }
        });
    }

    class GestureCallback extends GestureDetector.SimpleOnGestureListener {

        private static final int FLING_MIN_DISTANCE = 50;
        private static final int FLING_MIN_VELOCITY = 0;


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float distance = Math.abs(e1.getY() - e2.getY());
            if (distance > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY) {

                if (velocityY > 0) {
                    //下滑
                    // onMaintain();
                    LostRecipeDialog.show(getContext());
                } else {
                    //上滑;

                }

                return true;
            }

            return false;
        }

    }
}
