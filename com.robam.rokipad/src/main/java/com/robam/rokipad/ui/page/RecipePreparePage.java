package com.robam.rokipad.ui.page;

import com.legent.utils.graphic.ImageUtils;
import com.robam.rokipad.ui.view.RecipePrepareView;

public class RecipePreparePage extends AbsRecipeDetailPage {

	@Override
	protected void loadDynamicView() {
		if (book.preStep != null) {
			ImageUtils.displayImage(book.preStep.imageUrl, imgRecipe);
			RecipePrepareView view = new RecipePrepareView(cx, book);
			pnlDynamic.addView(view);
		}
	}

}
