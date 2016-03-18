package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.legent.ui.ext.HeadPage;
import com.robam.common.pojos.Recipe;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.view.RecipeGridView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

abstract public class AbsRecipeGridPage extends HeadPage {

	@InjectView(R.id.recipeView)
	protected RecipeGridView recipeView;

	@InjectView(R.id.imgTop)
	protected ImageView imgTop;

	@Override
	public View onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_recipe_grid, container,
				false);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			initData();
		}
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.imgTop)
	public void onClickToTop() {
		if (recipeView == null)
			return;

		recipeView.smoothScrollToPosition(0);
	}

	protected void initData() {
	}

	protected void loadBooks(List<Recipe> books, int modelType) {
		if (recipeView == null)
			return;

		recipeView.loadData(books, modelType);
	}

}
