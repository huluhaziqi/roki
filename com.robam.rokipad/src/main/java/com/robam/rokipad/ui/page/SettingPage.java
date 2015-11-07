package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.ui.ext.HeadPage;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.view.SettingAboutView;
import com.robam.rokipad.ui.view.SettingAccountView;
import com.robam.rokipad.ui.view.SettingDeviceView;
import com.robam.rokipad.ui.view.SettingSaleServiceView;
import com.robam.rokipad.ui.view.SettingSmartConfigView;
import com.robam.rokipad.ui.view.WifiListView;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class SettingPage extends HeadPage {

	public static final int ITEM_INDEX_DEVICE = 0;
	public static final int ITEM_INDEX_ACCOUNT = 1;
	public static final int ITEM_INDEX_WIFI = 2;
	public static final int ITEM_INDEX_SMART = 3;
	public static final int ITEM_INDEX_SERVICE = 4;
	public static final int ITEM_INDEX_ABOUT = 5;

	@InjectView(R.id.itemList)
	ListView itemList;

	@InjectView(R.id.pnlMain)
	FrameLayout pnlMain;

	int index = -1;
	Adapter adapter;
	Map<Integer, View> mapViews = Maps.newHashMap();

	@Override
	public View onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {

		Bundle bd = getArguments();
		int index = bd == null ? 0 : bd.getInt(PageArgumentKey.SettingItem);

		View view = inflater.inflate(R.layout.frame_setting, container, false);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			adapter = new Adapter();
			itemList.setAdapter(adapter);

			itemList.performItemClick(itemList.getChildAt(index), index,
					itemList.getItemIdAtPosition(index));
		}
		return view;
	}

	@OnItemClick(R.id.itemList)
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (index == position)
			return;
		index = position;
		adapter.setSelectedIndex(position);

		refesh();
	}

	@Subscribe
	public void onEvent(UserLoginEvent event) {
		refesh();
	}

	@Subscribe
	public void onEvent(UserLogoutEvent event) {
		refesh();
	}

	void refesh() {

		View view = null;

		switch (index) {
		case ITEM_INDEX_DEVICE:
			view = new SettingDeviceView(cx);
			break;
		case ITEM_INDEX_ACCOUNT:
			view = new SettingAccountView(cx);
			break;
		case ITEM_INDEX_WIFI:
			view = new WifiListView(cx, wifiSetCallback);
			break;
		case ITEM_INDEX_SMART:
			view = new SettingSmartConfigView(cx);
			break;
		case ITEM_INDEX_SERVICE:
			view = new SettingSaleServiceView(cx);
			break;
		case ITEM_INDEX_ABOUT:
			view = new SettingAboutView(cx);
			break;

		default:
			break;
		}

		if (view != null) {
			mapViews.put(index, view);
			showView(view);
		}

	}

	void showView(View view) {
		pnlMain.removeAllViews();
		if (view == null)
			return;
		pnlMain.addView(view);
	}

	WifiListView.OnWifiSetCallback wifiSetCallback = new WifiListView.OnWifiSetCallback() {

		@Override
		public void onCompleted(String ssid, String pwd) {

			AbsFan fan = Utils.getDefaultFan();
			if (fan == null)
				return;

			Plat.commander.setWifiParam(
					fan.getID(), ssid, pwd, new VoidCallback() {

						@Override
						public void onSuccess() {
							ToastUtils.showShort("路由设置成功。");
						}

						@Override
						public void onFailure(Throwable t) {
							ToastUtils.showThrowable(t);
						}
					});
		}
	};

	class SettingItem {
		int index;
		int imgResid;
		String title;

		public SettingItem(int index, String title, int imgResid) {
			this.index = index;
			this.title = title;
			this.imgResid = imgResid;
		}

		public SettingItem(int index, int titleResid, int imgResid) {
			this(index, cx.getString(titleResid), imgResid);
		}
	}

	class Adapter extends BaseAdapter {

		int selectIndex;
		List<SettingItem> list = buildItems();

		List<SettingItem> buildItems() {
			List<SettingItem> list = Lists.newArrayList();
			list.add(new SettingItem(0, "设备管理",
					R.drawable.ic_setting_item_device_selector));
			list.add(new SettingItem(1, "账户管理",
					R.drawable.ic_setting_item_account_selector));
			list.add(new SettingItem(2, "联网设置",
					R.drawable.ic_setting_item_wifi_selector));
			list.add(new SettingItem(3, "智能设置",
					R.drawable.ic_setting_item_smart_selector));
			list.add(new SettingItem(4, "售后服务",
					R.drawable.ic_setting_item_sale_selector));
			list.add(new SettingItem(5, "关于软件",
					R.drawable.ic_setting_item_about_selector));

			return list;
		}

		public void setSelectedIndex(int index) {
			this.selectIndex = index;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(cx).inflate(
						R.layout.view_setting_item, parent, false);
				vh = new ViewHolder(convertView);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			SettingItem item = list.get(position);
			vh.showItem(item, selectIndex == position);

			return convertView;
		}

		class ViewHolder {

			@InjectView(R.id.imgItem)
			ImageView imgItem;

			@InjectView(R.id.imgFlag)
			ImageView imgFlag;

			@InjectView(R.id.txtItem)
			TextView txtItem;

			View view;

			public ViewHolder(View view) {
				this.view = view;
				ButterKnife.inject(this, view);
			}

			public void showItem(SettingItem item, boolean isSelected) {

				imgItem.setImageResource(item.imgResid);
				imgItem.setSelected(isSelected);

				txtItem.setText(item.title);
				int colorResid = isSelected ? R.color.setting_text_pressed
						: R.color.setting_text_normal;
				txtItem.setTextColor(cx.getResources().getColor(colorResid));

				imgFlag.setVisibility(isSelected ? View.VISIBLE : View.GONE);
			}
		}

	}

}
