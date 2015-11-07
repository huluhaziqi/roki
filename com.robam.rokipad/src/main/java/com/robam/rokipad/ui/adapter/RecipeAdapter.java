package com.robam.rokipad.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.Lists;
import com.robam.common.pojos.Recipe;
import com.robam.rokipad.ui.view.RecipeGridItemView;

import java.util.List;

public class RecipeAdapter extends BaseAdapter {

	public static final int Model_Normal = 0;
	public static final int Model_Today = 1;
	public static final int Model_Favority = 2;

	private Context cx;
	private int modelType;
	private List<Recipe> list = Lists.newArrayList();

	public RecipeAdapter(Context cx, int modelType) {
		this.cx = cx;
		this.modelType = modelType;
	}

	public void loadData(List<Recipe> books) {
		this.list.clear();
		notifyDataSetChanged();

		if (books != null && books.size() > 0) {
			this.list.addAll(books);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			vh.view = new RecipeGridItemView(cx);
			convertView = vh.view;
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Recipe book = list.get(position);
		vh.view.setBook(book, modelType, position == 0 || position == 2);

		return convertView;
	}

	static class ViewHolder {
		RecipeGridItemView view;
	}
}
