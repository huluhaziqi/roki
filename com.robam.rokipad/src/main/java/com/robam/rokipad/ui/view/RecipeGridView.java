package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.etsy.android.grid.StaggeredGridView;
import com.robam.common.pojos.Recipe;
import com.robam.rokipad.ui.adapter.RecipeAdapter;

import java.util.List;

public class RecipeGridView extends StaggeredGridView {

	protected RecipeAdapter adapter;

	public RecipeGridView(Context cx) {
		super(cx);
	}

	public RecipeGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RecipeGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	protected void init(Context cx, AttributeSet attrs) {
	}

	public void loadData(final List<Recipe> books, int modelType) {
		if (adapter == null) {
			adapter = new RecipeAdapter(getContext(), modelType);
			this.setAdapter(adapter);
		}

		adapter.loadData(books);
	}

}
