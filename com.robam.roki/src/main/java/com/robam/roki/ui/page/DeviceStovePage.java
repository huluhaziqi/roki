package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectionChangedEvent;

import com.legent.plat.events.RecipeShowEvent;
import com.legent.plat.services.DeviceTypeManager;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.StoveStatusChangedEvent;
import com.robam.common.pojos.device.IRokiFamily;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.roki.R;
import com.robam.roki.ui.FormKey;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.view.StoveCtr9B39View;
import com.robam.roki.ui.view.StoveCtr9b37View;
import com.robam.roki.ui.view.StoveCtr9w70View_new;

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
    @InjectView(R.id.txtStoveReceipe)
    TextView txtStoveReceipe;

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

        }else if (DeviceTypeManager.getInstance().isInDeviceType(stove.getGuid(),IRokiFamily.R9B39)){
            //增加9B39的view
            ctrView = new StoveCtr9B39View(cx);
        }else if (DeviceTypeManager.getInstance().isInDeviceType(stove.getGuid(),IRokiFamily.R9B37)){
            //增加9b37的view
            ctrView = new StoveCtr9b37View(cx);
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

    @OnClick(R.id.txtStoveReceipe)
    public void onClicktxtStoveReceipe(){
        postEvent(new RecipeShowEvent());
        UIService.getInstance().popBack();
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
