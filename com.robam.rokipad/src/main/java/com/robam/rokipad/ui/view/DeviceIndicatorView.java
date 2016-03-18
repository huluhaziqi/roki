package com.robam.rokipad.ui.view;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.plat.Plat;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.utils.EventUtils;
import com.robam.common.Utils;
import com.robam.common.events.DeviceTabStatusChangedEvent;
import com.robam.common.events.OvenStatusChangedEvent;
import com.robam.common.events.SteamOvenStatusChangedEvent;
import com.robam.common.pojos.device.Oven.AbsOven;
import com.robam.common.pojos.device.Oven.OvenStatus;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.common.pojos.device.Sterilizer.AbsSterilizer;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.rokipad.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rosicky on 16/1/2.
 */
public class DeviceIndicatorView extends FrameLayout {

    @InjectView(R.id.itemFan)
    DeviceTabItemView itemFan;
    @InjectView(R.id.itemStove)
    DeviceTabItemView itemStove;
    @InjectView(R.id.itemSteam)
    DeviceTabItemView itemSteam;
    @InjectView(R.id.itemWave)
    DeviceTabItemView itemWave;
    @InjectView(R.id.itemSterilizer)
    DeviceTabItemView itemSterilizer;
    @InjectView(R.id.itemOven)
    DeviceTabItemView itemOven;

    List<DeviceTabItemView> tabs = Lists.newArrayList();

    public interface OnTabSelectedCallback {
        void onTabSelected(int tabIndex);
    }

    OnTabSelectedCallback callback;

    public DeviceIndicatorView(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_device_tab,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            buildViews();
            refreshOrder();
        }
    }

    private void buildViews() {
        List<AbsDevice> devices = Plat.deviceService.queryDevices();
        if (devices != null && devices.size() != 0) {
            for (AbsDevice device : devices) {
                if (device instanceof AbsFan) {

                } else if (device instanceof AbsSteamoven) {

                } else if (device instanceof AbsSterilizer) {

                }
            }
        }
        tabs.add(itemFan);
        tabs.add(itemStove);
        tabs.add(itemSteam);
        tabs.add(itemOven);
        tabs.add(itemSterilizer);
        tabs.add(itemWave);

    }

    public void selectTab(final int tabIndex) {
        if (tabIndex < 0 || tabIndex >= tabs.size())
            return;

        this.post(new Runnable() {
            @Override
            public void run() {
                tabs.get(tabIndex).performClick();
            }
        });

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

    @OnClick({R.id.itemFan, R.id.itemStove, R.id.itemSteam, R.id.itemWave, R.id.itemSterilizer, R.id.itemOven})
    public void onClickTabs(DeviceTabItemView view) {
        int index = tabs.indexOf(view);

        for (DeviceTabItemView tab : tabs) {
            tab.setSelected(false);
        }
        view.setSelected(true);

        if (callback != null) {
            callback.onTabSelected(index);
        }
    }

    public void setOnTabSelectedCallback(OnTabSelectedCallback callback) {
        this.callback = callback;
    }

    @Subscribe
    public void onEvent(DeviceTabStatusChangedEvent event) {
        DeviceTabItemView view = chooseItem(event.id);
        if (event.id.equals(Utils.getDefaultSteam().getID())) {
            view.setIsOn(DeviceTabItemView.STEAM_VIEW, event.isOn);
        } else if (event.id.equals(Utils.getDefaultOven().getID())){
            view.setIsOn(DeviceTabItemView.OVEN_VIEW, event.isOn);
        }
    }

    @Subscribe
    public void onEvent(SteamOvenStatusChangedEvent event) {
        if (event.pojo == null) {
            return;
        }
        if (event.pojo instanceof AbsSteamoven) {
            if (event.pojo.status == SteamStatus.Off) {
                itemSteam.setIsOn(DeviceTabItemView.STEAM_VIEW, false);
            } else {
                itemSteam.setIsOn(DeviceTabItemView.STEAM_VIEW, true);
            }
        } else  {
            return;
        }
    }

    @Subscribe
    public void onEvent(OvenStatusChangedEvent event) {
        if (event.pojo == null) {
            return;
        }
        if (event.pojo instanceof AbsOven) {
            if (event.pojo.status == OvenStatus.Off) {
                itemOven.setIsOn(DeviceTabItemView.OVEN_VIEW, false);
            } else {
                itemOven.setIsOn(DeviceTabItemView.OVEN_VIEW, true);
            }
        } else  {
            return;
        }
    }

    private DeviceTabItemView chooseItem(String id) {
        if (id.equals(Utils.getDefaultSteam().getID())) {
            return itemSteam;
        } else if (id.equals(Utils.getDefaultOven().getID())) {
            return itemOven;
        }
        return null;
    }


    /**
     * 更新位置
     */
    private void refreshOrder() {

    }

}
