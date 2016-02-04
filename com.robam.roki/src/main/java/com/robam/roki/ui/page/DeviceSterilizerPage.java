package com.robam.roki.ui.page;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.ui.ext.HeadPage;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.SteriStatusChangedEvent;
import com.robam.common.pojos.device.Sterilizer.AbsSterilizer;
import com.robam.common.pojos.device.Sterilizer.ISterilizer;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.view.SteriCtr829View;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeviceSterilizerPage extends HeadPage {
    AbsSterilizer sterilizer;
    UIListeners.ISteriCtrView ctrView;
    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;
    @InjectView(R.id.fl_sterilizer_main)
    FrameLayout sterilizerMain;
    private boolean isOn;

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bd = getArguments();
        String guid = bd == null ? null : bd.getString(PageArgumentKey.Guid);
        isOn = bd == null ? null : bd.getBoolean("layout");
        sterilizer = Plat.deviceService.lookupChild(guid);

        View view = inflater.inflate(R.layout.page_device_sterilizer, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    //------------------------------------------------------------------------- ui event---------------------------------------------------------------------

    @Subscribe
    public void onEvent(DeviceConnectionChangedEvent event) {
        if (sterilizer == null || !Objects.equal(sterilizer.getID(), event.device.getID()))
            return;
        disconnectHintView.setVisibility(event.isConnected ? View.INVISIBLE : View.VISIBLE);
    }

    @Subscribe
    public void onEvent(SteriStatusChangedEvent event) {
        if (sterilizer == null || !Objects.equal(sterilizer.getID(), event.pojo.getID()))
            return;
        onRefresh();
    }

    void initView() {
        disconnectHintView.setVisibility(View.INVISIBLE);
        //if (DeviceTypeManager.getInstance().isInDeviceType(sterilizer.getGuid(), IRokiFamily.RR829)) {
        //增加829的view
        ctrView = new SteriCtr829View(cx);
        //}
        setCtrView();
        Preconditions.checkNotNull(ctrView, "invalid fan, no matched view");
        ctrView.attachSteri((ISterilizer) sterilizer);
        sterilizerMain.addView((View) ctrView);
        //onRefresh();
    }

    private void setCtrView() {
        CheckBox cbSwitch = (CheckBox) ((View) ctrView).findViewById(R.id.sterilizer_switch);
        TextView tvOrder = (TextView) ((View) ctrView).findViewById(R.id.tv_order_btn);
        TextView tvStoving = (TextView) ((View) ctrView).findViewById(R.id.tv_stoving_btn);
        TextView tvClean = (TextView) ((View) ctrView).findViewById(R.id.tv_clean_btn);
        TextView tvSterilizer = (TextView) ((View) ctrView).findViewById(R.id.tv_sterilizer_btn);
        cbSwitch.setChecked(isOn);
        tvOrder.setSelected(isOn);
        tvStoving.setSelected(isOn);
        tvClean.setSelected(isOn);
        tvSterilizer.setSelected(isOn);
        if (tvSterilizer.isSelected()) {
            tvSterilizer.setTextColor(Color.parseColor("#ffffff"));
        } else {
            tvSterilizer.setTextColor(Color.parseColor("#575757"));
        }
    }

    void onRefresh() {
        disconnectHintView.setVisibility(sterilizer.isConnected() ? View.INVISIBLE : View.VISIBLE);
        if (sterilizer == null)
            return;
        ctrView.onRefresh();
    }


    boolean checkConnection() {
        if (!sterilizer.isConnected()) {
            ToastUtils.showShort(R.string.steri_invalid_error);
            return false;
        } else {
            return true;
        }
    }
}
