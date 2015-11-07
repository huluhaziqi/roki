package com.robam.rokipad.ui.page;

import com.legent.Callback;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.rokipad.ui.adapter.RecipeAdapter;

import java.util.List;

public class RecipeRecommendPage extends AbsRecipeGridPage {

	@Override
	protected void initData() {

		ProgressDialogHelper.setRunning(cx, true);

		CookbookManager.getInstance().getRecommendCookbooks(
				new Callback<List<Recipe>>() {

					@Override
					public void onSuccess(List<Recipe> result) {
						loadBooks(result, RecipeAdapter.Model_Normal);
						ProgressDialogHelper.setRunning(cx, false);
					}

					@Override
					public void onFailure(Throwable t) {
						ProgressDialogHelper.setRunning(cx, false);
						ToastUtils.showThrowable(t);
					}
				});
	}

}
