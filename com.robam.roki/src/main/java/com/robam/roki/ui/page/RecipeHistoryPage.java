package com.robam.roki.ui.page;

import android.view.View;
import android.widget.ImageView;

import com.legent.Callback;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.roki.R;
import com.robam.roki.ui.view.RecipeGridView;

import java.util.List;

/**
 * Created by sylar on 15/6/14.
 */
public class RecipeHistoryPage extends AbsRecipeGridPage {


    int start = 0;
    int limit = 100;

    @Override
    protected void initData() {
        regsitRightView();
        ProgressDialogHelper.setRunning(cx, true);
        CookbookManager.getInstance().getGroundingRecipes(start, limit, new Callback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                ProgressDialogHelper.setRunning(cx, false);
                loadBooks(RecipeGridView.Model_History, recipes);
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
        return "还没有往期菜谱";
    }


    void onSearch() {
        RecipeSearchPage.show();
    }


    void regsitRightView() {
        ImageView img = TitleBar.newTitleIconView(cx, R.mipmap.ic_recipe_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch();
            }
        });

        titleBar.replaceRight(img);
    }
}
