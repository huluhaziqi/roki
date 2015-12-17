package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectionChangedEvent;

import com.legent.plat.services.DeviceTypeManager;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.dialogs.NumberDialog;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.events.StoveStatusChangedEvent;
import com.robam.common.events.StoveTempEvent;
import com.robam.common.pojos.device.IRokiFamily;
import com.robam.common.pojos.device.Stove;
import com.robam.common.pojos.device.StoveStatus;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.view.DeviceSwitchView;
import com.robam.roki.ui.view.StoveCtr9b12View;
import com.robam.roki.ui.view.StoveCtr9b37;
import com.robam.roki.ui.view.StoveCtr9w70View;
import com.robam.roki.ui.view.StoveCtr9w70View_new;
import com.squareup.okhttp.internal.Util;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DeviceStovePage extends HeadPage {

    final static String HINT_1 = "为了安全\n请在灶具上开启";
    final static String HINT_2 = "火力开启后\n才能打开计时关火";

    Stove stove;
    UIListeners.IStoveCtrView ctrView;
    @InjectView(R.id.imgHead)
    ImageView imgHead;
    @InjectView(R.id.txtTitle)
    TextView txtTitle;
    @InjectView(R.id.txtDesc)
    TextView txtDesc;
    @InjectView(R.id.divMain)
    FrameLayout divMain;

    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;

    @InjectView(R.id.attention)
    LinearLayout attention;


    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        Bundle bd = getArguments();
        String guid = bd == null ? null : bd.getString(PageArgumentKey.Guid);
        stove = Plat.deviceService.lookupChild(guid);

        View view = layoutInflater.inflate(R.layout.page_device_stoveh, viewGroup, false);
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
        if (stove == null || !Objects.equal(stove.getID(), event.device.getID()))
            return;

        disconnectHintView.setVisibility(event.isConnected ? View.INVISIBLE : View.VISIBLE);
    }


    @Subscribe
    public void onEvent(StoveStatusChangedEvent event) {
        if (stove == null || !Objects.equal(stove.getID(), event.pojo.getID()))
            return;

        onRefresh();
    }
    void initView(){
        disconnectHintView.setVisibility(View.INVISIBLE);
        if (DeviceTypeManager.getInstance().isInDeviceType(stove.getGuid(),IRokiFamily.R9W70)) {

            //增加9w70的view
            //ctrView = new StoveCtr9w70View(cx);
            ctrView = new StoveCtr9w70View_new(cx);


        }else if (DeviceTypeManager.getInstance().isInDeviceType(stove.getGuid(),IRokiFamily.R9B12)){
            //增加9b12的view
            ctrView = new StoveCtr9b12View(cx);
        }else if (DeviceTypeManager.getInstance().isInDeviceType(stove.getGuid(),IRokiFamily.R9B37)){
            //增加9b37的view
            ctrView = new StoveCtr9b37(cx);
        }

        ctrView.attachStove(stove);
        divMain.addView((View) ctrView);

        onRefresh();

    }



    //------------------------------------------------------------------------- ui event---------------------------------------------------------------------


    void onRefresh() {
        disconnectHintView.setVisibility(stove.isConnected() ? View.INVISIBLE : View.VISIBLE);
        if (stove==null)
            return;
        ctrView.onRefresh();

    }


    boolean checkConnection() {
        if (!stove.isConnected()) {
            ToastUtils.showShort(R.string.fan_invalid_error);
            return false;
        } else {
            return true;
        }
    }

}