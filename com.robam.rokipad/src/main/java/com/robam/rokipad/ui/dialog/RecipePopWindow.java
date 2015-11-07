package com.robam.rokipad.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.legent.Callback;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.Group;
import com.robam.common.services.CookbookManager;
import com.robam.rokipad.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class RecipePopWindow extends PopupWindow {

	public static interface CategorySelectedCallback {
		void onCategorySelected(int position, Group group);
	}

	public static RecipePopWindow show(Context cx, View parent,
			int seletedIndex, CategorySelectedCallback callback) {

		RecipePopWindow pop = new RecipePopWindow(cx, seletedIndex, callback);
		int[] location = new int[2];
		parent.getLocationOnScreen(location);

		int x = location[0] + parent.getWidth() / 2 - pop.getWidth() / 2;
		int y = location[1]
				+ parent.getHeight()
				- (int) cx.getResources().getDimension(
						R.dimen.pop_triangle_height);
		pop.showAtLocation(parent, Gravity.NO_GRAVITY, x, y);

		return pop;
	}

	@InjectView(R.id.pop_list)
	ListView popList;

	Context cx;
	Adapter adapter;
	CategorySelectedCallback callback;

	@SuppressLint("InflateParams")
	public RecipePopWindow(Context cx, int seletedIndex,
			CategorySelectedCallback callback) {

		this.cx = cx;
		this.callback = callback;

		View view = LayoutInflater.from(cx).inflate(R.layout.view_recipe_pop,
				null);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			initData(seletedIndex);
		}

		this.setWidth((int) cx.getResources().getDimension(
				R.dimen.recipe_pop_width));
		this.setHeight((int) cx.getResources().getDimension(
				R.dimen.recipe_pop_height));
		this.setFocusable(true);
		this.setOutsideTouchable(false);
		this.setBackgroundDrawable(cx.getResources().getDrawable(
				R.drawable.shape_transparent));

		setContentView(view);
	}

	void initData(final int seletedIndex) {
		CookbookManager.getInstance().getGroupsWithoutHome(
				new Callback<List<Group>>() {

					@Override
					public void onSuccess(List<Group> result) {
						adapter = new Adapter(result, seletedIndex);
						popList.setAdapter(adapter);
					}

					@Override
					public void onFailure(Throwable t) {
						ToastUtils.showThrowable(t);
					}
				});
	}

	@OnItemClick(R.id.pop_list)
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long itemId) {
		adapter.setSelectItem(position);
		this.dismiss();
		
		Group group = (Group) adapter.getItem(position);
		if (callback != null) {
			callback.onCategorySelected(position, group);
		}
	}

	public class Adapter extends BaseAdapter {
		List<Group> list;
		int selectItem = 0;

		public Adapter(List<Group> list, int seletedIndex) {
			this.list = list;
			this.selectItem = seletedIndex;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = LayoutInflater.from(cx).inflate(
						R.layout.view_recipe_pop_item, parent, false);

				vh = new ViewHolder(convertView);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			vh.tvName.setText(list.get(position).name);
			vh.imgCheck.setVisibility(position == selectItem ? View.VISIBLE
					: View.INVISIBLE);

			return convertView;
		}

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
			notifyDataSetChanged();
		}

		class ViewHolder {

			@InjectView(R.id.tv_name)
			TextView tvName;

			@InjectView(R.id.img_check)
			ImageView imgCheck;

			public ViewHolder(View view) {
				if (!view.isInEditMode()) {
					ButterKnife.inject(this, view);
				}
			}
		}
	}
}
