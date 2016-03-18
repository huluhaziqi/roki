package com.robam.rokipad.ui.page;

import com.legent.utils.graphic.ImageUtils;
import com.robam.rokipad.ui.view.RecipeIntroduceView;

public class RecipeIntroducePage extends AbsRecipeDetailPage {

	@Override
	protected void loadDynamicView() {
		ImageUtils.displayImage(book.imgLarge, imgRecipe);
		
		RecipeIntroduceView view = new RecipeIntroduceView(cx, book);
		pnlDynamic.addView(view);
	}

}
