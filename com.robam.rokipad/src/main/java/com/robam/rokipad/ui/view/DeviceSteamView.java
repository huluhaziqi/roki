package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback2;
import com.legent.VoidCallback;
import com.legent.ui.ext.popoups.PopoupHelper;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.events.DeviceSteamSwitchViewEvent;
import com.robam.common.events.DeviceTabStatusChangedEvent;
import com.robam.common.events.SteamOvenStatusChangedEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.ui.Helper;
import com.robam.rokipad.ui.dialog.SteamCleanAndSterDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rosicky on 16/1/2.
 */
public class DeviceSteamView extends FrameLayout {


    @InjectView(R.id.txtClean)
    TextView txtClean;
    @InjectView(R.id.relClean)
    RelativeLayout relClean;
    @InjectView(R.id.txtSterilizer)
    TextView txtSterilizer;
    @InjectView(R.id.relSterilizer)
    RelativeLayout relSterilizer;
    @InjectView(R.id.txtContent)
    TextView txtContent;
    @InjectView(R.id.imgContentOn)
    ImageView imgContentOn;
    @InjectView(R.id.relContent)
    RelativeLayout relContent;
    @InjectView(R.id.relMain)
    RelativeLayout relMain;

    AbsSteamoven steam;
    short status = 1;
    @InjectView(R.id.relSwitch)
    ImageView relSwitch;

    private Map<String, Short> cookbookMap = new HashMap<String, Short>();

    public DeviceSteamView(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceSteamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceSteamView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.page_device_steam,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        EventUtils.regist(this);
        initData();
    }

    private void initData() {
        steam = Utils.getDefaultSteam();
        cookbookMap.put("肉类", (short) 2);
        cookbookMap.put("蔬菜", (short) 7);
        cookbookMap.put("水蒸蛋", (short) 4);
        cookbookMap.put("海鲜", (short) 3);
        cookbookMap.put("糕点", (short) 5);
        cookbookMap.put("面条", (short) 8);
        cookbookMap.put("蹄筋", (short) 6);
        cookbookMap.put("解冻", (short) 9);
    }

    @OnClick(R.id.relSwitch)
    public void onClickSwitch() {
        short status = (steam.status == SteamStatus.Off) ? SteamStatus.On : SteamStatus.Off;
        setStatus(status);
    }

    private void setStatus(short status) {
        if (!checkConnection()) {
            return;
        }

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

    private void onRefresh() {

        if (steam == null)
            return;

        boolean isOn = steam.isConnected() && steam.status != SteamStatus.Off;
        setSwitch(isOn);
        EventUtils.postEvent(new DeviceTabStatusChangedEvent(steam.getID(), isOn));
    }

    public void setSwitch(boolean isOn) {
        imgContentOn.setVisibility(isOn ? View.VISIBLE : View.GONE);
        relContent.setBackgroundDrawable(getResources().getDrawable(isOn ? R.mipmap.img_steam_on : R.mipmap.img_steam_content_close));
        txtContent.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.c19));

        relClean.setBackgroundDrawable(getResources().getDrawable(isOn ? R.mipmap.img_steam_clean_on : R.mipmap.img_steam_clean_no));
        txtClean.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.c19));

        relSterilizer.setBackgroundDrawable(getResources().getDrawable(isOn ? R.mipmap.img_steam_sterilizer_on : R.mipmap.img_steam_sterilize_no));
        txtSterilizer.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.c19));

        relSwitch.setImageResource(isOn ? R.mipmap.img_switch_on : R.mipmap.img_switch_close);
    }

    private boolean checkConnection() {
        if (!steam.isConnected()) {
            ToastUtils.showShort(R.string.steam_invalid_error);
            relSwitch.setImageResource(R.mipmap.img_switch_disconnect);
            return false;
        } else {
            return true;
        }
    }

    // -------------------------------------------专业模式按钮 --------------------------------------
    @OnClick(R.id.relContent)
    public void onClickContent() {
        if (steam.status != SteamStatus.Off) {
            PopupWindow pop = Helper.newSteamThreePickerPAD(getContext(), new Callback2<DeviceWorkMsg>() {
                @Override
                public void onCompleted(final DeviceWorkMsg deviceWorkMsg) {
                    short time = Short.valueOf(deviceWorkMsg.getTime());
                    short temp = Short.valueOf(deviceWorkMsg.getTemperature());
                    short mode = Short.valueOf(cookbookMap.get(deviceWorkMsg.getType()));
                    steam.setSteamWorkMode(mode, temp, time, (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("msg", deviceWorkMsg);
                            EventUtils.postEvent(new DeviceSteamSwitchViewEvent(1, bundle));
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                }
            });
            PopoupHelper.show(DeviceSteamView.this, pop, Gravity.CENTER);
        }
    }

    //------------------------------------------- 自洁模式按钮 --------------------------------------
    @OnClick(R.id.relClean)
    public void onClickClean() {
        if (steam.status != SteamStatus.Off) {
            DeviceWorkMsg msg = new DeviceWorkMsg();
            final Bundle bundle = new Bundle();
            msg.setType("自洁");
            msg.setTime(String.valueOf(45));
            msg.setTemperature(String.valueOf(120));
            bundle.putSerializable("msg", msg);
            SteamCleanAndSterDialog.show(getContext(), msg, new SteamCleanAndSterDialog.ClickListener() {
                @Override
                public void onClick() {
                    steam.setSteamProMode((short) 45, (short) 120, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceSteamSwitchViewEvent(1, bundle));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            Log.e("stove", t.toString());
                            ToastUtils.showThrowable(t);
                        }
                    });
                }
            });
        }
    }

    //------------------------------------------- 杀菌模式按钮 --------------------------------------
    @OnClick(R.id.relSterilizer)
    public void onClickSterilizer() {
        if (steam.status != SteamStatus.Off) {
            DeviceWorkMsg msg = new DeviceWorkMsg();
            final Bundle bundle = new Bundle();
            msg.setType("杀菌");
            msg.setTime(String.valueOf(60));
            msg.setTemperature(String.valueOf(120));
            bundle.putSerializable("msg", msg);
            SteamCleanAndSterDialog.show(getContext(), msg, new SteamCleanAndSterDialog.ClickListener() {
                @Override
                public void onClick() {
                    steam.setSteamProMode((short) 60, (short) 120, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceSteamSwitchViewEvent(1, bundle));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });
                }
            });
        }
    }

    @Subscribe
    public void onEvent(SteamOvenStatusChangedEvent event) {
        if (event.pojo == null) {
            return;
        }
        if (event.pojo instanceof AbsSteamoven) {
            if (event.pojo.status == SteamStatus.Off) {
                setSwitch(false);
            } else if (event.pojo.status == SteamStatus.On) {
                setSwitch(true);
            }
        } else {
            return;
        }
    }
}
