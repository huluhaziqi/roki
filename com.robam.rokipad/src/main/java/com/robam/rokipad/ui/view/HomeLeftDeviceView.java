package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.common.eventbus.Subscribe;
import com.legent.plat.events.DeviceConnectedNoticEvent;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.plat.pojos.device.IDevice;
import com.legent.utils.EventUtils;
import com.robam.common.Utils;
import com.robam.common.events.FanStatusChangedEvent;
import com.robam.common.events.StoveStatusChangedEvent;
import com.robam.common.pojos.device.fan.Fan9700;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.UIEvents;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeLeftDeviceView extends FrameLayout {

	public HomeLeftDeviceView(Context context) {
		super(context);
		init(context, null);
	}

	public HomeLeftDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HomeLeftDeviceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@InjectView(R.id.fanView)
	HomeFanView fanView;

	@InjectView(R.id.stoveView)
	HomeStoveView stoveView;

	void init(Context cx, AttributeSet attrs) {

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_home_left_device, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		EventUtils.regist(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		EventUtils.unregist(this);
	}

	@Subscribe
	public void onEvent(UIEvents.HomeDeviceSelectedEvent event) {
		IDevice device = event.device;
		boolean isFan = device == null || (device instanceof Fan9700);
		fanView.setVisibility(isFan ? VISIBLE : GONE);
		stoveView.setVisibility(!isFan ? VISIBLE : GONE);

		if (device != null) {
			if (isFan) {
				fanView.setData((Fan9700) device);
			} else {

//				stoveView.setData((Stove) device);
				stoveView.setData(Utils.getDefaultStove());
//				stoveView.setData((Stove)(((Fan9700)device).getChild()));
			}
		}
	}

	@Subscribe
	public void onEvent(DeviceConnectedNoticEvent event) {

		refresh();
	}

	@Subscribe
	public void onEvent(DeviceConnectionChangedEvent event) {

		refresh();
	}

	@Subscribe
	public void onEvent(FanStatusChangedEvent event) {

		fanView.refresh();
	}

	@Subscribe
	public void onEvent(StoveStatusChangedEvent event) {

		stoveView.refresh();
	}

	void refresh() {
		fanView.refresh();
		stoveView.refresh();
	}

}
