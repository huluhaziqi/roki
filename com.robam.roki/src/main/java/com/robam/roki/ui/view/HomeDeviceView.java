package com.robam.roki.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectedNoticEvent;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.plat.events.DeviceSelectedEvent;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.plat.pojos.User;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.ui.UIService;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.qrcode.ScanQrActivity;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.service.AppService;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.UIListeners;

import java.util.List;

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

        ImageView icon_scan = TitleBar.newTitleIconView(getContext(), R.mipmap.ic_device_scan, new OnClickListener() {
            private final static int SCANNIN_GREQUEST_CODE = 1;

            @Override
            public void onClick(View view) {
                if (UiHelper.checkAuthWithDialog(getContext(), PageKey.UserLogin)) {
                    Activity atv = UIService.getInstance().getTop().getActivity();
                    Intent intent = new Intent();
                    intent.setClass(atv, ScanQrActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    atv.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                }

            }
        });
        titleBar.replaceLeft(icon_scan);
    }

    void refreshWhenPull() {
        AppService.getInstance().init(Plat.app);
        pullRefreshScrollview.onRefreshComplete();
    }

    @Override
    public void onRefresh() {
        onRefreshConnection();
//        long userid = Plat.accountService.getCurrentUserId();
//        Plat.deviceService.getDevices(userid, new Callback<List<DeviceInfo>>() {
//            @Override
//            public void onSuccess(List<DeviceInfo> deviceInfos) {
//                int listsize = deviceInfos.size();
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//            }
//        });
        onFanViewFresh();
    }

    void onRefreshConnection() {
        AbsFan fan = Utils.getDefaultFan();
        boolean isDisconnected = fan != null && !fan.isConnected();
        disconnectHintView.setVisibility(isDisconnected
                ? View.VISIBLE
                : View.GONE);
    }
    public void onFanViewFresh(){
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

}
