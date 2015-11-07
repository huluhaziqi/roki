package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.legent.ui.ext.HeadPage;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Recipe3rd;
import com.robam.roki.R;
import com.robam.roki.ui.view.EmojiEmptyView;
import com.robam.roki.ui.view.RecipeGridView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

abstract public class AbsRecipeGridPage extends HeadPage {

    @InjectView(R.id.emptyView)
    EmojiEmptyView emptyView;

    @InjectView(R.id.gridView)
    RecipeGridView gridView;

    @Override
    public View onCreateContentView(LayoutInflater inflater,
                                    ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.abs_page_recipe_grid, container,
                false);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            emptyView.setText(getTextWhenEmpty());
            initData();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    protected void initData() {
    }

    protected String getTextWhenEmpty() {
        return null;
    }

    protected void loadBooks(int modelType, List<Recipe> books) {
        List<Recipe3rd> books3rd = Lists.newArrayList();
        loadBooks(modelType, books, books3rd);
    }

    protected void loadBooks(int modelType, List<Recipe> books, List<Recipe3rd> books3rd) {
        switchView((books == null || books.size() == 0) && (books3rd == null || books3rd.size() == 0));
        gridView.loadData(modelType, books, books3rd);
    }

    void switchView(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        gridView.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
    }

}
