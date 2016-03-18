package com.robam.rokipad.ui.view;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback2;
import com.legent.events.ConnectionModeChangedEvent;
import com.legent.services.AbsTask;
import com.legent.services.ConnectivtyService;
import com.legent.services.TaskService;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.EventUtils;
import com.legent.utils.api.PreferenceUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.api.WifiUtils;
import com.legent.utils.api.WifiUtils.WifiScanner;
import com.robam.common.PrefsKey;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.dialog.WifiAuthDialog;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class WifiListView extends FrameLayout {

	static public interface OnWifiSetCallback {
		void onCompleted(String ssid, String pwd);
	}

	@InjectView(R.id.wifiList)
	ListView wifiList;

	Context cx;
	Adapter adapter;
	OnWifiSetCallback callback;
	WifiScanner scanner;

	public WifiListView(Context context) {
		super(context);
		init(context, null);
	}

	public WifiListView(Context context, OnWifiSetCallback callback) {
		super(context);
		this.callback = callback;
		init(context, null);
	}

	public WifiListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public WifiListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		scanner = WifiUtils.startScan(cx, adapter.callabck);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		if (scanner != null) {
			scanner.stopScanning();
			scanner = null;
		}
	}

	void init(Context cx, AttributeSet attrs) {

		this.cx = cx;
		View view = LayoutInflater.from(cx).inflate(R.layout.view_wifi_list,
				this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);

			adapter = new Adapter();
			wifiList.setAdapter(adapter);
			wifiList.setVerticalScrollBarEnabled(true);
		}
	}

	@OnItemClick(R.id.wifiList)
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (adapter.isConnected(position))
			return;

		final ScanResult sr = (ScanResult) adapter.getItem(position);
		WifiAuthDialog.show(cx, sr.SSID, new Callback2<String>() {

			@Override
			public void onCompleted(String pwd) {
				TaskService.getInstance().postAsyncTask(
						new WifiAuthTask(sr, pwd));
			}
		});
	}

	public class WifiAuthTask extends AbsTask<Void> {

		ScanResult sr;
		String ssid, pwd;

		public WifiAuthTask(ScanResult sr, String pwd) {
			this.sr = sr;
			this.ssid = sr.SSID;
			this.pwd = pwd;
		}

		@Subscribe
		public void onEvent(ConnectionModeChangedEvent event) {
			if (!isRunning)
				return;

			if (event.connectionMode != ConnectivtyService.ConnectionMode_Wifi)
				return;

			String curSsid = WifiUtils.getCurrentSsid(cx);
			if (!Objects.equal(curSsid, ssid))
				return;

			notifySelfOnSuccess(null);
		}

		@Override
		public void onPreExecute() {
			adapter.notifyDataSetChanged();
			ProgressDialogHelper.setRunning(cx, true);
		}

		@Override
		public void onSuccess(Void result) {
			PreferenceUtils.setString(PrefsKey.Ssid, ssid);
			PreferenceUtils.setString(ssid, pwd);

			if (callback != null) {
				callback.onCompleted(ssid, pwd);
			}
			ProgressDialogHelper.setRunning(cx, false);
		}

		@Override
		public void onFailure(Throwable t) {
			ProgressDialogHelper.setRunning(cx, false);
			ToastUtils.showShort("连接超时!");
		}

		@Override
		public Void doInBackground(Object... params) throws Exception {
			EventUtils.regist(this);
			try {
				int cipherType = WifiUtils.getCipherType(sr);
				WifiConfiguration cfg = WifiUtils.CreateWifiInfo(cx, ssid, pwd,
						cipherType);

				WifiUtils.addNetwork(cx, cfg);
				waitSelf(1000 * 30);

			} finally {
				EventUtils.unregist(this);
			}
			return null;
		}
	}

	class Adapter extends BaseAdapter {

		List<ScanResult> list = Lists.newArrayList();
		String currentSsid;

		public boolean isConnected(int position) {
			ScanResult sr = list.get(position);
			if (sr == null)
				return false;

			boolean isConnected = Objects.equal(sr.SSID, currentSsid);
			return isConnected;
		}

		WifiUtils.WifiScanCallback callabck = new WifiUtils.WifiScanCallback() {

			@Override
			public void onScanWifi(List<ScanResult> scanList) {
				list.clear();
				if (scanList != null) {
					list.addAll(scanList);
				}
				currentSsid = WifiUtils.getCurrentSsid(cx);
				notifyDataSetChanged();
			}
		};

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
						R.layout.view_wifi_list_item, parent, false);
				vh = new ViewHolder(convertView);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			ScanResult sr = list.get(position);
			vh.showWifiInfo(sr, isConnected(position));

			return convertView;
		}

		class ViewHolder {

			@InjectView(R.id.txtSsid)
			TextView txtSsid;

			@InjectView(R.id.imgLock)
			ImageView imgLock;

			@InjectView(R.id.imgWifi)
			ImageView imgWifi;

			@InjectView(R.id.imgSelect)
			ImageView imgSelect;

			public ViewHolder(View view) {
				ButterKnife.inject(this, view);
			}

			public void showWifiInfo(ScanResult sr, boolean isConnected) {
				txtSsid.setText(sr.SSID);
				imgWifi.setSelected(isConnected);

				imgSelect.setVisibility(isConnected ? VISIBLE : INVISIBLE);
				boolean isEncrypted = WifiUtils.isEncrypted(sr);
				imgLock.setVisibility(isEncrypted ? VISIBLE : GONE);

				txtSsid.setTextColor(cx.getResources().getColor(R.color.balck));
			}
		}
	}

}
