package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback2;
import com.legent.VoidCallback;
import com.legent.events.ConnectionModeChangedEvent;
import com.legent.plat.Plat;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.popoups.PopoupHelper;
import com.legent.utils.TimeUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.SteamAlarmEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamMode;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.ui.Helper;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.dialog.SteamAlarmDoorAndWaterDialog;
import com.robam.rokipad.ui.dialog.SteamCountDownDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2015/12/19.
 */
public class DeviceSteamWorkingPage extends HeadPage {

    static final int START_THREE_SEC_COUNTDOWN = 0;
    static final int START_WORK = 1;
    static final int START_PAUSE = 2;
    static final int DONE_WORK = 3;
    static final int EXIT_PAGE = 4;
    static final int REFRESH_UI = 5;

    static final int RESET_DIALOG = 6;
    static final int STEAM_OFF = 7;
    static final int ALARM_DOOR = 8;
    static final int ALARM_WATER = 9;
    static final int ALARM_SENSOR = 10;

    AbsSteamoven steam;

    private int remainTime;

    private Animation circleRotate = null;

    View contentView;
    @InjectView(R.id.txtTemp)
    TextView txtTemp;
    @InjectView(R.id.txtTime)
    TextView txtTime;
    @InjectView(R.id.imgVol)
    ImageView imgVol;
    @InjectView(R.id.relSwitch)
    RelativeLayout relSwitch;
    @InjectView(R.id.imgSteamCircle)
    ImageView imgCircle;
    @InjectView(R.id.imgSteamPauseWork)
    ImageView imgPause;
    @InjectView(R.id.imgSteamDoneWork)
    ImageView imgSteamDoneWork;
    @InjectView(R.id.imgTemp)
    ImageView imgTemp;
    @InjectView(R.id.imgTime)
    ImageView imgTime;
    @InjectView(R.id.imgSteamWorkContent)
    ImageView imgWorkContent;

    boolean on = true;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_THREE_SEC_COUNTDOWN :
                    SteamCountDownDialog.show(getContext(), 4);
                    break;
                case START_WORK:
                    setStart();
                    break;
                case START_PAUSE:
                    setPause();
                    break;
                case DONE_WORK:
                    setDone();
                    break;
                case EXIT_PAGE:
                    break;
                case REFRESH_UI:
                    if (on) {
                        remainTime--;
                        refreshUI();
                        if (remainTime > 0) {
                            handler.sendEmptyMessageDelayed(REFRESH_UI, 1000);
                        } else {
                            handler.sendEmptyMessage(DONE_WORK);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bd = getArguments();
        String guid = bd == null ? null : bd.getString(PageArgumentKey.Guid);
        steam = Plat.deviceService.lookupChild(guid);

        contentView = inflater.inflate(R.layout.page_device_steam_work,
                container, false);
        ButterKnife.inject(this, contentView);
        init();
        return contentView;
    }

    private void init() {
//        if (steam == null) {
//            return;
//        } else {
//            remainTime = (Integer.valueOf(steam.time) * 60);
//            txtTemp.setText(steam.temp + "°C");
//            txtTime.setText(TimeUtils.sec2clock((long) steam.time));
//              imgWorkContent.setImageResource(getContentResId(steam.mode));
//            imgVol.setImageResource(steam.alarm == 1 ? R.mipmap.img_steam_work_water_has_not : R.mipmap.img_steam_work_water_has);
//
//            checkDoorState();
//        }

        // ----------------- test codes ----------------
        Bundle bd = getArguments();
        DeviceWorkMsg msg = (DeviceWorkMsg) bd.getSerializable("msg");
        remainTime = (Integer.valueOf(msg.getTime()) * 60);
//        remainTime = 10;
        txtTemp.setText(msg.getTemperature() + "°C");
        txtTime.setText(TimeUtils.sec2clock(Long.valueOf(msg.getTime()) * 60));
//        imgVol.setImageResource(steam.alarm == 1 ? R.mipmap.img_steam_work_water_has_not : R.mipmap.img_steam_work_water_has);

        checkDoorState();
        // ---------------------------------------------
    }

    private int getContentResId(short type) {
        int resId = 0;
        switch (type) {
            case SteamMode.CAKE:
                resId = R.mipmap.img_steam_work_cake;
                break;
            case SteamMode.EGG:
                resId = R.mipmap.img_steam_work_egg;
                break;
            case SteamMode.MEAT:
                resId = R.mipmap.img_steam_work_meat;
                break;
            case SteamMode.NODDLE:
                resId = R.mipmap.img_steam_work_noddle;
                break;
            case SteamMode.SEAFOOD:
                resId = R.mipmap.img_steam_work_seafood;
                break;
            case SteamMode.TIJIN:
                resId = R.mipmap.img_steam_work_tijin;
                break;
            case SteamMode.UNFREEZE:
                resId = R.mipmap.img_steam_work_unfreeze;
                break;
            case SteamMode.VEGE:
                resId = R.mipmap.img_steam_work_vege;
                break;
            default:
                break;
        }
        return resId;
    }

    private void checkDoorState() {
//        if (steam.doorState == AbsSteamoven.Steam_Door_Open) {
//            // 门是开着的，暂停画面，弹出对话框
//            setPause();
//            showDialog();
//        } else {
//            // 开始倒计时
//            handler.sendEmptyMessage(START_THREE_SEC_COUNTDOWN);
//            handler.sendEmptyMessageDelayed(START_WORK, 3000);
//        }

        // ------------------ test code ----------------
        handler.sendEmptyMessage(START_THREE_SEC_COUNTDOWN);
        handler.sendEmptyMessageDelayed(START_WORK, 3000);
        // ---------------------------------------------
    }

    private void setPause() {
        if (steam != null) {
            steam.setSteamStatus(SteamStatus.Pause, new VoidCallback() {
                @Override
                public void onSuccess() {
                    imgPause.setVisibility(View.VISIBLE);
                    imgCircle.setVisibility(View.GONE);
                    imgTemp.setVisibility(View.VISIBLE);
                    imgTime.setVisibility(View.VISIBLE);
                    imgTemp.setClickable(true);
                    imgTime.setClickable(true);
                    imgCircle.clearAnimation();
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }

        // ------------------ test code ----------------
        imgPause.setVisibility(View.VISIBLE);
        imgCircle.setVisibility(View.GONE);
        imgTemp.setVisibility(View.VISIBLE);
        imgTime.setVisibility(View.VISIBLE);
        imgTemp.setClickable(true);
        imgTime.setClickable(true);
        imgCircle.clearAnimation();
        // ------------------------------------------------
    }

    private void setStart() {
        if (steam != null) {
            steam.setSteamStatus(SteamStatus.Working, new VoidCallback() {
                @Override
                public void onSuccess() {
                    imgTemp.setVisibility(View.GONE);
                    imgTime.setVisibility(View.GONE);
                    if (circleRotate == null) {
                        circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_steamoven_circle_rotate);
                        LinearInterpolator lin = new LinearInterpolator();
                        circleRotate.setInterpolator(lin);
                    }
                    imgCircle.startAnimation(circleRotate);
                    handler.sendEmptyMessage(REFRESH_UI);
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }

        // ------------------ test code ----------------
        imgTemp.setVisibility(View.GONE);
        imgTime.setVisibility(View.GONE);
        if (circleRotate == null) {
            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_steamoven_circle_rotate);
            LinearInterpolator lin = new LinearInterpolator();
            circleRotate.setInterpolator(lin);
        }
        imgCircle.setVisibility(View.VISIBLE);
        imgPause.setVisibility(View.GONE);
        imgCircle.startAnimation(circleRotate);
        handler.sendEmptyMessage(REFRESH_UI);
//        startTimer();
        // ---------------------------------------------
    }

    private void refreshUI() {

//        txtTime.setText(TimeUtils.sec2clock(steam.time));
//        txtTemp.setText(String.valueOf(steam.temp));

        txtTime.setText(TimeUtils.sec2clock(remainTime));
    }

    private void setDone() {
        imgCircle.clearAnimation();
        imgCircle.setVisibility(View.GONE);
        imgSteamDoneWork.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(EXIT_PAGE, 10 * 60 * 1000);
    }

    private void showDialog(int type) {
        if (type == ALARM_DOOR || type == ALARM_WATER || type == STEAM_OFF) {
            SteamAlarmDoorAndWaterDialog.show(getContext(), type);
        } else if (type == RESET_DIALOG) {
            PopupWindow pop = Helper.newResetTwoWheelPickerPAD(getContext(), new Callback2<DeviceWorkMsg>() {
                @Override
                public void onCompleted(DeviceWorkMsg deviceWorkMsg) {
                    ToastUtils.show(deviceWorkMsg.toString(), Toast.LENGTH_SHORT);
                }
            });
            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }


    // ------------------------------------------ OnClick ----------------------------------------
    @OnClick(R.id.fraContent)
    public void onClickFraContent() {
//        if (steam != null) {
//            if (steam.status == SteamStatus.Working) {
//                setPause();
//            } else if (steam.status == SteamStatus.Pause) {
//                setStart();
//            }
//        }

        // -----------------------------------test code ----------------------------------
        if (on) {
            handler.sendEmptyMessage(START_PAUSE);
        } else {
            handler.sendEmptyMessage(START_WORK);
        }
        on = !on;
    }

    @OnClick({R.id.imgTemp, R.id.imgTime})
    public void onClickReset() {
        showDialog(RESET_DIALOG);
    }

    @OnClick(R.id.relSwitch)
    public void onClickSwitch() {
        /*
        if (steam != null) {
            showDialog(STEAM_OFF);
        }
        */

        showDialog(STEAM_OFF);
    }


    // ---------------------------------------- OnEvent -------------------------------------------

    @Override
    public void onEvent(ConnectionModeChangedEvent event) {
        super.onEvent(event);
    }

    @Subscribe
    public void onEvent(SteamAlarmEvent event) {
        switch (event.alarmId) {
            case AbsSteamoven.Event_Steam_Alarm_ok:
                break;
            case AbsSteamoven.Event_Steam_Alarm_door:
                setPause();
                showDialog(ALARM_DOOR);
                break;
            case AbsSteamoven.Event_Steam_Alarm_water:
                setPause();
                showDialog(ALARM_WATER);
                break;
            case AbsSteamoven.Event_Steam_Alarm_temp:
                setPause();
                break;
        }
    }
}
