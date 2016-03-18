package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.events.ConnectionModeChangedEvent;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.api.WifiUtils;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.form.GuideActivity;
import com.robam.rokipad.ui.view.WifiListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GuideWifiPage extends HeadPage {

	@InjectView(R.id.btnNext)
	Button btnNext;

	@InjectView(R.id.wifiListView)
	WifiListView wifiView;

	boolean isSkip;

	@Override
	protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle bundle)  {
		View view = inflater.inflate(R.layout.frame_guide_wifi, container,
				false);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			refresh();
		}
		TextView v = TitleBar.newTitleTextView(cx, "跳过", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				GuideActivity.onGuideOver(activity, false);
			}
		});
		getTitleBar().replaceRight(v);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@Subscribe
	public void onEvent(ConnectionModeChangedEvent event) {
		refresh();
	}

	@OnClick(R.id.btnNext)
	public void onClick() {
		boolean isConnected = WifiUtils.isWifiConnected(cx);
		if (!isConnected) {
			GuideActivity.onGuideOver(activity, false);
		} else {
			UIService.getInstance().postPage(PageKey.GuideLogin);
		}
	}

	void refresh() {
		boolean isConnected = WifiUtils.isWifiConnected(cx);
		isSkip = !isConnected;
		btnNext.setText(isSkip ? R.string.active_skip : R.string.active_next);
	}

}
