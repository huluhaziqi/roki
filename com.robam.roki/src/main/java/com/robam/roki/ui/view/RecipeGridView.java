package com.robam.roki.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.legent.ui.ext.adapters.ExtBaseAdapter;
import com.robam.common.pojos.AbsRecipe;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Recipe3rd;

import java.util.List;

public class RecipeGridView extends StaggeredGridView {
    public static final int Model_Search = 0;
    public static final int Model_Favority = 1;
    public static final int Model_History = 2;

    int modelType;
    Adapter adapter;

    Ordering<AbsRecipe> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<AbsRecipe, Comparable>() {
        @Override
        public Comparable apply(AbsRecipe absRecipe) {
            return absRecipe.collectCount;
        }
    }).reverse();

    public RecipeGridView(Context cx) {
        super(cx);
        init(cx, null);
    }

    public RecipeGridView(Context cx, AttributeSet attrs) {
        super(cx, attrs);
        init(cx, attrs);
    }

    public RecipeGridView(Context cx, AttributeSet attrs, int defStyle) {
        super(cx, attrs, defStyle);
        init(cx, attrs);
    }

    protected void init(Context cx, AttributeSet attrs) {
    }


    public void loadData(int modelType, List<Recipe> books, List<Recipe3rd> books3rd) {
        this.modelType = modelType;
        if (adapter == null) {
            adapter = new Adapter();
            this.setAdapter(adapter);
        }

        List<AbsRecipe> list = Lists.newArrayList();
        if (books != null) {
            books = ordering.sortedCopy(books);
            for (Recipe r : books) {
                list.add(r);
            }
        }
        if (books3rd != null) {
            books3rd = ordering.sortedCopy(books3rd);
            for (Recipe3rd r : books3rd) {
                list.add(r);
            }
        }

        adapter.loadData(list);
        this.smoothScrollToPosition(0);
    }

    class Adapter extends ExtBaseAdapter<AbsRecipe> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder(new RecipeGridItemVIew(getContext(), modelType));
                convertView = vh.view;
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            AbsRecipe book = list.get(position);
            vh.showData(book, position == 0);
            return convertView;
        }

        class ViewHolder {
            RecipeGridItemVIew view;

            ViewHolder(RecipeGridItemVIew view) {
                this.view = view;
                view.setTag(this);
            }

            void showData(AbsRecipe book, boolean isSmallSize) {
                view.showData(book, isSmallSize);
            }
        }
    }

}
