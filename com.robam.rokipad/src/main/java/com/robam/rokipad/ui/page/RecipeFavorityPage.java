package com.robam.rokipad.ui.page;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.FavorityBookRefreshEvent;
import com.robam.common.io.cloud.Reponses.CookbooksResponse;
import com.robam.common.services.CookbookManager;
import com.robam.rokipad.ui.adapter.RecipeAdapter;

public class RecipeFavorityPage extends AbsRecipeGridPage {

	@Override
	protected void initData() {

		ProgressDialogHelper.setRunning(cx, true);

		CookbookManager.getInstance().getFavorityCookbooks(
				new Callback<CookbooksResponse>() {

					@Override
					public void onSuccess(CookbooksResponse result) {
						loadBooks(result.cookbooks, RecipeAdapter.Model_Favority);
						ProgressDialogHelper.setRunning(cx, false);
					}

					@Override
					public void onFailure(Throwable t) {
						ProgressDialogHelper.setRunning(cx, false);
						ToastUtils.showThrowable(t);
					}
				});
	}
	
	@Subscribe
	public void onEvent(FavorityBookRefreshEvent event) {
		initData();
	}

}
