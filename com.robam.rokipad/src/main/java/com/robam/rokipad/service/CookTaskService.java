package com.robam.rokipad.service;

import android.content.Context;
import android.os.Bundle;

import com.legent.ui.UIService;
import com.robam.common.services.AbsCookTaskService;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.PageKey;

public class CookTaskService extends AbsCookTaskService {

	static private CookTaskService instance = new CookTaskService();

	static public synchronized CookTaskService getInstance() {
		return instance;
	}

	private CookTaskService() {
	}

	@Override
	protected void onShowCookingView() {
		Bundle bd = new Bundle();
		bd.putLong(PageArgumentKey.BookId, book.id);
		bd.putInt(PageArgumentKey.RecipeStepIndex, stepIndex);
		UIService.getInstance().postPage(PageKey.RecipeCooking, bd);
	}

	@Override
	protected void onStoped() {
		super.onStoped();
		FloatingService.pauseIfExisted();
		if (!isMinimized) {
			UIService.getInstance().returnHome();
		} else {
			UIService.getInstance().returnHome();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		FloatingService.pauseIfExisted();
	}

	@Override
	protected void onRestore() {
		super.onRestore();
		FloatingService.showIfExisted();
	}

	@Override
	protected void onMinimize() {
		UIService.getInstance().returnHome();

		Context cx = UIService.getInstance().getTop().getActivity();
		FloatingService.start(cx, book.id);
	}

	@Override
	protected void onMaximize() {
		onShowCookingView();
	}
}
