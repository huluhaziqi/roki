package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.plat.events.RecipeShowEvent;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.SteamOvenStatusChangedEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.model.DeviceWorkMsg;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.dialog.SteamOvenStartWorkDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rosicky on 15/12/9.
 */
public class DeviceSteamOvenPage extends HeadPage {

    AbsSteamoven steam;

    @InjectView(R.id.imgContent)
    ImageView imgContent;
    @InjectView(R.id.txtContent)
    TextView txtContext;
    @InjectView(R.id.relClean)
    RelativeLayout relClean;
    @InjectView(R.id.txtClean)
    TextView txtClean;
    @InjectView(R.id.relSterilize)
    RelativeLayout relSterilize;
    @InjectView(R.id.txtSterilize)
    TextView txtSterilize;
    @InjectView(R.id.imgSwitch)
    ImageView imgSwitch;
    @InjectView(R.id.txtSwitch)
    TextView txtSwitch;
    @InjectView(R.id.linSwitch)
    RelativeLayout linSwitch;
    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;
    @InjectView(R.id.relContent)
    RelativeLayout relContent;
    @InjectView(R.id.imgLeanline)
    ImageView imgLeanLine;
    @InjectView(R.id.imgContentCircle)
    ImageView imgContentCircle;

    View contentView;
    @InjectView(R.id.llNoticeStart)
    LinearLayout llNoticeStart;

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        Bundle bd = getArguments();
        String guid = bd == null ? null : bd.getString(PageArgumentKey.Guid);
        steam = Plat.deviceService.lookupChild(guid);

        contentView = inflater.inflate(R.layout.page_device_steamoven,
                container, false);
        ButterKnife.inject(this, contentView);
        disconnectHintView.setVisibility(View.INVISIBLE);
//        onRefresh();
        setPageTitle("");
        return contentView;
    }

    private void initView() {

    }


    @OnClick(R.id.linSwitch)
    public void onClickSwitch() {
        short status = (steam.status == SteamStatus.Off) ? SteamStatus.On : SteamStatus.Off;
        llNoticeStart.setVisibility(steam.status == SteamStatus.Off ? View.VISIBLE : View.GONE);
        setStatus(status);
    }

    private void setStatus(short status) {
        if (!checkConnection()) {
            return;
        }
//
        steam.setSteamStatus(status, new VoidCallback() {
            @Override
            public void onSuccess() {
                onRefresh();
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });

    }

    @OnClick(R.id.relClean)
    public void onClickClean() {
        if (steam.status != SteamStatus.Off) {
            DeviceWorkMsg msg = new DeviceWorkMsg();
            msg.setType("自洁");
            msg.setTime(String.valueOf(45));
            msg.setTemperature(String.valueOf(100));
            SteamOvenStartWorkDialog.show(getContext(), msg, steam != null ? steam : null);
        } else {
            llNoticeStart.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.relSterilize)
    public void onClickSterilize() {
        if (steam.status != SteamStatus.Off) {
            DeviceWorkMsg msg = new DeviceWorkMsg();
            msg.setType("杀菌");
            msg.setTime(String.valueOf(60));
            msg.setTemperature(String.valueOf(100));
            SteamOvenStartWorkDialog.show(getContext(), msg, steam != null ? steam : null);
        } else {
            llNoticeStart.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.imgContent)
    public void onClickContext() {
        if (steam.status != SteamStatus.Off) {
            if (UiHelper.checkAuthWithDialog(getContext(), PageKey.UserLogin)) {
                Bundle bundle = new Bundle();
                bundle.putString(PageArgumentKey.Guid, steam.getID());
                UIService.getInstance().postPage(PageKey.DeviceSteamOvenSetting, bundle);
            }
        } else {
            llNoticeStart.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.txtRecipe)
    public void onClickRecipe() {
        postEvent(new RecipeShowEvent());
        UIService.getInstance().popBack();
    }

    private void onRefresh() {
        disconnectHintView.setVisibility(steam != null && !steam.isConnected()
                ? View.VISIBLE
                : View.INVISIBLE);

        if (steam == null)
            return;
//
        boolean isOn = steam.isConnected() && steam.status != SteamStatus.Off;
        setSwitch(isOn);
    }

    private void setSwitch(boolean isOn) {
        imgContent.setImageResource(isOn ? R.mipmap.img_steamoven_work : R.mipmap.img_steamoven_unopen1);
        txtContext.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.c19));
        relClean.setBackgroundDrawable(getResources().getDrawable(isOn ? R.mipmap.img_steamoven_circle_open_small : R.mipmap.img_steamoven_circle_close));
        txtClean.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.c19));
        relSterilize.setBackgroundDrawable(getResources().getDrawable(isOn ? R.mipmap.img_steamoven_circle_open_small : R.mipmap.img_steamoven_circle_close));
        txtSterilize.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.c19));
        imgSwitch.setImageResource(isOn ? R.mipmap.img_switch_yellow : R.mipmap.img_steamoven_switch_open);
        txtSwitch.setTextColor(getResources().getColor(isOn ? R.color.home_bg : R.color.c14));
        txtSwitch.setText(isOn ? "已开启" : "已关闭");
        imgLeanLine.setImageResource(isOn ? R.mipmap.img_steamoven_leanline_yellow : R.mipmap.img_steamoven_leanline_white);
        imgContentCircle.setVisibility(isOn ? View.VISIBLE : View.GONE);
    }

    private boolean checkConnection() {
        if (!steam.isConnected()) {
            ToastUtils.showShort(R.string.steam_invalid_error);
            return false;
        } else {
            return true;
        }
    }

    @Subscribe
    public void onEvent(DeviceConnectionChangedEvent event) {
        if (steam == null || !Objects.equal(steam.getID(), event.device.getID()))
            return;

        disconnectHintView.setVisibility(event.isConnected ? View.GONE : View.VISIBLE);
    }

    @Subscribe
    public void onEvent(SteamOvenStatusChangedEvent event) {
        if (steam == null || !Objects.equal(steam.getID(), event.pojo.getID()))
            return;

        switch (steam.status) {
            case SteamStatus.Off:
                setSwitch(false);
                break;
            case SteamStatus.On:
                setSwitch(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
