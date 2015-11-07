package com.robam.rokipad.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

@SuppressLint("InflateParams")
public class SettingPopWindow extends PopupWindow {

	public static interface ItemSelectedCallback {
		void onItemSelected(View parent, int position, Object obj);
	}

	public static SettingPopWindow show(Context cx, View parent, String[] list,
			ItemSelectedCallback callback) {

		SettingPopWindow pop = new SettingPopWindow(cx, parent, list, callback);
		int[] location = new int[2];
		parent.getLocationOnScreen(location);

		int x = location[0] + parent.getWidth() / 2 - pop.getWidth() / 2;
		int y = location[1] + parent.getHeight();

		pop.showAtLocation(parent, Gravity.NO_GRAVITY, x, y);

		return pop;
	}

	@InjectView(R.id.popList)
	ListView popList;

	Context cx;
	Adapter adapter;
	View parent;
	ItemSelectedCallback callback;

	public SettingPopWindow(Context cx, View parent, String[] list,
			ItemSelectedCallback callback) {
		this.cx = cx;
		this.parent = parent;
		this.callback = callback;

		View view = LayoutInflater.from(cx).inflate(R.layout.view_setting_pop,
				null);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			adapter = new Adapter(list);
			popList.setAdapter(adapter);
		}

		this.setContentView(view);
		this.setWidth((int) cx.getResources().getDimension(
				R.dimen.setting_pop_width));
		this.setHeight((int) cx.getResources().getDimension(
				R.dimen.recipe_pop_height));
		this.setFocusable(true);
		this.setOutsideTouchable(false);
		this.setBackgroundDrawable(new BitmapDrawable());
	}

	@OnItemClick(R.id.popList)
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long itemId) {

		Object obj = adapter.getItem(position);
		dismiss();

		if (callback != null) {
			callback.onItemSelected(parent, position, obj);
		}
	}

	public class Adapter extends BaseAdapter {
		String[] list;

		public Adapter(String[] list) {
			this.list = list;
		}

		public int getCount() {
			return list.length;
		}

		public Object getItem(int position) {
			return list[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder vh = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(cx).inflate(
						R.layout.view_setting_pop_item, parent, false);
				vh = new ViewHolder(convertView);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.showValue(list[position]);

			return convertView;
		}

		class ViewHolder {

			@InjectView(R.id.txtValue)
			TextView txtValue;

			public ViewHolder(View view) {
				ButterKnife.inject(this, view);
			}

			public void showValue(String value) {
				txtValue.setText(value);
			}
		}
	}
}
