package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.legent.ui.ext.views.TitleBar;
import com.robam.common.events.FavorityBookRefreshEvent;
import com.robam.common.pojos.Recipe3rd;
import com.robam.common.services.CookbookManager;
import com.robam.common.ui.UiHelper;
import com.robam.roki.ui.PageArgumentKey;

/**
 * Created by sylar on 15/7/6.
 */
public class RecipeDetail3rdPage extends WebClientPage {

    Recipe3rd recipe;

    @Override
    protected void initPage(Bundle bd) {
        super.initPage(bd);
        long id = bd.getLong(PageArgumentKey.BookId);
        recipe = CookbookManager.getInstance().getRecipe3rdById(id);
        Preconditions.checkNotNull(recipe, "recipe3rd is null");

        regsitRightView();
    }

    @Subscribe
    public void onEvent(FavorityBookRefreshEvent event) {
        if (event.bookId == recipe.id) {
            recipe.isFavority = event.flag;
            regsitRightView();
        }
    }


    void regsitRightView() {

        if (recipe == null) return;

        TextView txtView = TitleBar.newTitleTextView(cx, recipe.isFavority ? "取消收藏" : "收藏", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavority();
            }
        });

        titleBar.replaceRight(txtView);
    }


    void onFavority() {
        if (recipe == null) return;

        UiHelper.onFavority(recipe);
    }

}
