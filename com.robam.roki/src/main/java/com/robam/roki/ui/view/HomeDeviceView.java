package com.robam.roki.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectedNoticEvent;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.plat.events.DeviceSelectedEvent;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.ui.UIService;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.EventUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.roki.R;
import com.robam.roki.service.AppService;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.UIListeners;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeDeviceView extends FrameLayout implements UIListeners.IRefresh {

    @InjectView(R.id.titleView)
    TitleBar titleBar;
    @InjectView(R.id.addView)
    DeviceAddView addView;
    @InjectView(R.id.fanView)
    DeviceItemView fanView;
    @InjectView(R.id.stoveView)
    DeviceItemView stoveView;
    @InjectView(R.id.devicesLayout)
    LinearLayout devicesLayout;
    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;
    @InjectView(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView pullRefreshScrollview;

    public HomeDeviceView(Context context) {
        super(context);
        init(context, null);
    }

    public HomeDeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeDeviceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
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
    public void onEvent(UserLoginEvent event) {
        onRefresh();
    }

    @Subscribe
    public void onEvent(UserLogoutEvent event) {
        onRefresh();
    }

    @Subscribe
    public void onEvent(DeviceConnectedNoticEvent event) {
        onRefresh();
    }

    @Subscribe
    public void onEvent(DeviceSelectedEvent event) {
        onRefresh();
    }

    @Subscribe
    public void onEvent(DeviceConnectionChangedEvent event) {

        String guid = event.device.getID();
        if (Utils.isFan(guid)) {
            onRefreshConnection();
        }
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_home_device, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            setTitleBar();
            disconnectHintView.setVisibility(View.GONE);
            pullRefreshScrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            pullRefreshScrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                    refreshWhenPull();
                }
            });


            onRefresh();
        }
    }

    void setTitleBar() {
        titleBar.setTitle("厨电");

        TextView icon = TitleBar.newTitleTextView(getContext(), "售后", new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIService.getInstance().postPage(PageKey.SaleService);
            }
        });

        titleBar.replaceRight(icon);
    }

    void refreshWhenPull() {

        AppService.getInstance().init(Plat.app);
        pullRefreshScrollview.onRefreshComplete();
    }

    @Override
    public void onRefresh() {

        onRefreshConnection();

        AbsFan fan = Utils.getDefaultFan();
        boolean hasDevice = (fan != null);
        addView.setVisibility(!hasDevice ? VISIBLE : GONE);
        devicesLayout.setVisibility(hasDevice ? VISIBLE : GONE);

        if (!hasDevice) return;

        fanView.loadData(fan);

        Stove stove = Utils.getDefaultStove();
        boolean hasStove = stove != null;
        stoveView.setVisibility(hasStove ? VISIBLE : GONE);
        if (hasStove) {
            stoveView.loadData(stove);
        }
    }

    void onRefreshConnection() {
        AbsFan fan = Utils.getDefaultFan();
        boolean isDisconnected = fan != null && !fan.isConnected();
        disconnectHintView.setVisibility(isDisconnected
                ? View.VISIBLE
                : View.GONE);
    }

}
