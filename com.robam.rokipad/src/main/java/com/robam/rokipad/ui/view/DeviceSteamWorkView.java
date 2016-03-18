package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;
import com.google.common.primitives.Shorts;
import com.legent.Callback2;
import com.legent.VoidCallback;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.ui.ext.popoups.PopoupHelper;
import com.legent.utils.EventUtils;
import com.legent.utils.TimeUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.events.DeviceSteamSwitchViewEvent;
import com.robam.common.events.SteamAlarmEvent;
import com.robam.common.events.SteamOvenStatusChangedEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamMode;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.ui.Helper;
import com.robam.rokipad.ui.dialog.SteamAlarmDoorAndWaterDialog;
import com.robam.rokipad.ui.dialog.SteamCountDownDialog;
import com.robam.rokipad.ui.dialog.SteamOvenCancelWorkDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/8.
 */
public class DeviceSteamWorkView extends FrameLayout {

    DeviceWorkMsg msg;
    AbsSteamoven steam;
    short time;
    short temp;
    SteamAlarmDoorAndWaterDialog dlg = null;
    short preStatus = -1;

    private Map<String, Short> cookbookMap = new HashMap<String, Short>();

    static final int Start = 4;
    static final int Pause = 0;
    static final int Working = 1;
    static final int Done = 2;
    static final int CountDown = 3;
    static final int Reset = 5;
    static final int ReturnHome = 6;
    static final int PollStatusChanged = 7;
    static final int RefreshTime = 8;

    @InjectView(R.id.txt_warn_water)
    TextView txtWarnWater;
    @InjectView(R.id.txtTemp)
    TextView txtTemp;
    @InjectView(R.id.imgTemp)
    ImageView imgTemp;
    @InjectView(R.id.imgVol)
    ImageView imgVol;
    @InjectView(R.id.relWorkMsg)
    RelativeLayout relWorkMsg;
    @InjectView(R.id.txtTime)
    TextView txtTime;
    @InjectView(R.id.imgTime)
    ImageView imgTime;
    @InjectView(R.id.fraContent)
    FrameLayout fraContent;
    @InjectView(R.id.imgSteamCircle)
    ImageView imgSteamCircle;
    @InjectView(R.id.imgSteamWorkContent)
    ImageView imgSteamWorkContent;
    @InjectView(R.id.imgSteamPauseWork)
    ImageView imgSteamPauseWork;
    @InjectView(R.id.imgSteamDoneWork)
    ImageView imgSteamDoneWork;
    @InjectView(R.id.txtTempSet)
    TextView txtTempSet;
    @InjectView(R.id.txtTimeSet)
    TextView txtTimeSet;
    @InjectView(R.id.txtVolSet)
    TextView txtVolSet;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Start:
                    steam.setSteamWorkMode((short) 0, temp, time, (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            txtTemp.setClickable(false);
                            txtTime.setClickable(false);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });
                    break;
                case Pause:
                    steam.setSteamStatus(SteamStatus.Pause, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            imgTemp.setVisibility(View.VISIBLE);
                            imgTime.setVisibility(View.VISIBLE);
                            imgSteamPauseWork.setVisibility(View.VISIBLE);
                            txtTemp.setClickable(true);
                            txtTime.setClickable(true);
                            imgSteamCircle.clearAnimation();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });
                    break;
                case Working:
                    steam.setSteamStatus(SteamStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            imgTime.setVisibility(View.GONE);
                            imgTemp.setVisibility(View.GONE);
                            imgSteamPauseWork.setVisibility(View.GONE);
                            txtTime.setClickable(false);
                            txtTemp.setClickable(false);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });
                    break;
                case Done:
                    imgSteamDoneWork.setVisibility(View.VISIBLE);
                    imgSteamWorkContent.setVisibility(View.GONE);
                    // 10min 后自动关机
                    handler.sendEmptyMessageDelayed(ReturnHome, 3 * 1000);
                    break;
                case CountDown:
                    break;
                case Reset:
                    final DeviceWorkMsg message = (DeviceWorkMsg) msg.obj;
                    steam.setSteamWorkTemp(Short.valueOf(message.getTemperature()), new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            txtTempSet.setText(message.getTemperature());
                            steam.setSteamWorkTime(Short.valueOf(message.getTime()), new VoidCallback() {
                                @Override
                                public void onSuccess() {
                                    txtTimeSet.setText(message.getTime());
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    ToastUtils.showThrowable(t);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });
                    break;
                case ReturnHome:
                    imgSteamCircle.clearAnimation();
                    EventUtils.postEvent(new DeviceSteamSwitchViewEvent(0, null));
                    break;
                case PollStatusChanged:
                    txtTemp.setText(String.valueOf(steam.temp));
                    int min = steam.time / 60;
                    int sec = steam.time % 60;
                    if (sec > 0) {
                        min++;
                    }
                    txtTime.setText(String.valueOf(min));
                    txtTempSet.setText(String.valueOf(steam.tempSet));
                    txtTimeSet.setText(String.valueOf(steam.timeSet));
                    if (preStatus == SteamStatus.Working &&
                            steam.status != SteamStatus.Pause &&
                            steam.status != SteamStatus.Working &&
                            steam.status != SteamStatus.Off &&
                            steam.status != SteamStatus.AlarmStatus) {
                        handler.sendEmptyMessage(Done);
                        break;
                    }
                    preStatus = steam.status;
                    if (steam.status == SteamStatus.Pause) {
                        imgTemp.setVisibility(View.VISIBLE);
                        imgTime.setVisibility(View.VISIBLE);
                        imgSteamPauseWork.setVisibility(View.VISIBLE);
                        txtTemp.setClickable(true);
                        txtTime.setClickable(true);
                        imgSteamCircle.clearAnimation();
                    } else if (steam.status == SteamStatus.Working) {
                        if (dlg != null) {
                            dlg.dismiss();
                            dlg = null;
                        }
                        imgTemp.setVisibility(View.GONE);
                        imgTime.setVisibility(View.GONE);
                        imgSteamPauseWork.setVisibility(View.GONE);
                        txtTemp.setClickable(false);
                        txtTime.setClickable(false);
                    } else if (steam.status == SteamStatus.Off) {
                        EventUtils.postEvent(new DeviceSteamSwitchViewEvent(0, null));
                    }
                    break;
                case RefreshTime:
                    txtTime.setText(TimeUtils.sec2clock(steam.time));
            }
        }
    };


    public DeviceSteamWorkView(Context context, DeviceWorkMsg msg) {
        super(context);
        this.msg = msg;
        init(context, null);
    }

    public DeviceSteamWorkView(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceSteamWorkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceSteamWorkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.page_device_steam_work, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        EventUtils.regist(this);
        initViews();
    }

    private void initViews() {
        steam = Utils.getDefaultSteam();
        cookbookMap.put("肉类", (short) 2);
        cookbookMap.put("蔬菜", (short) 7);
        cookbookMap.put("水蒸蛋", (short) 4);
        cookbookMap.put("海鲜", (short) 3);
        cookbookMap.put("糕点", (short) 5);
        cookbookMap.put("面条", (short) 8);
        cookbookMap.put("蹄筋", (short) 6);
        cookbookMap.put("解冻", (short) 9);
        cookbookMap.put("自洁", (short) 10);
        cookbookMap.put("杀菌", (short) 11);
        if (msg != null) {
            temp = Short.valueOf(msg.getTemperature());
            time = Short.valueOf(msg.getTime());
            txtTempSet.setText(msg.getTemperature());
            txtTimeSet.setText(msg.getTime());
            imgSteamWorkContent.setImageResource(getContentResId(cookbookMap.get(msg.getType())));
        } else {
            steam = Utils.getDefaultSteam();
        }
        checkDoorState();
    }

    private void checkDoorState() {
        if (steam.doorState == AbsSteamoven.Steam_Door_Open) {
            SteamAlarmDoorAndWaterDialog.show(getContext(), SteamAlarmDoorAndWaterDialog.ALARM_DOOR);
        } else {
            readyToWork();
        }
    }

    private void readyToWork() {
        if (steam.status == SteamStatus.On || steam.status == SteamStatus.Wait) {
            SteamCountDownDialog.show(getContext(), 4);
        }
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
            case SteamMode.CLEAN:
                resId = R.mipmap.img_steam_work_content;
                break;
            case SteamMode.STERILIZER:
                resId = R.mipmap.img_steam_work_content;
                break;
            default:
                break;
        }
        return resId;
    }

    /**
     * ==========================================================================
     * onClick 点击代码段
     */

    @OnClick(R.id.imgSteamWorkContent)
    public void onClickContent() {
        if (steam.status == SteamStatus.Working) {
            handler.sendEmptyMessage(Pause);
        } else if (steam.status == SteamStatus.Pause) {
            handler.sendEmptyMessage(Working);
        } else if (steam.status == SteamStatus.AlarmStatus) {

        }
    }

    @OnClick({R.id.txtTime, R.id.txtTemp})
    public void onClickReset() {
        DeviceWorkMsg msg = new DeviceWorkMsg();
        String s = "肉类";
        short type = steam.mode;
        if (type == 2) {
            s = "肉类";
        } else if (type == 3) {
            s = "海鲜";
        } else if (type == 4) {
            s = "水蒸蛋";
        } else if (type == 5) {
            s = "糕点";
        } else if (type == 6) {
            s = "蹄筋";
        } else if (type == 7) {
            s = "蔬菜";
        } else if (type == 8) {
            s = "面条";
        } else if (type == 9) {
            s = "解冻";
        }
        msg.setType(s);
        PopupWindow pop = Helper.newResetTwoWheelPickerPAD(getContext(), new Callback2<DeviceWorkMsg>() {
            @Override
            public void onCompleted(final DeviceWorkMsg message) {
                Message msg = Message.obtain();
                msg.what = Reset;
                msg.obj = message;
                handler.sendMessage(msg);
            }
        });

        PopoupHelper.show(DeviceSteamWorkView.this, pop, Gravity.CENTER);
    }

    @OnClick(R.id.relSwitch)
    public void onClickSwitch() {
        String s = "结束蒸汽炉工作";
        String s1 = " ";
        final SteamOvenCancelWorkDialog.Listener l = new SteamOvenCancelWorkDialog.Listener() {
            @Override
            public void onClick() {
                steam.setSteamStatus(SteamStatus.Off, new VoidCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ToastUtils.showThrowable(t);
                    }
                });
                EventUtils.postEvent(new DeviceSteamSwitchViewEvent(0, null));
            }
        };
        SteamOvenCancelWorkDialog.show(getContext(), s, s1, l);
    }


    /**
     * ==========================================================================
     * onEvent 事件代码段
     *
     */

    /**
     * 报警事件
     *
     * @param event
     */
    @Subscribe
    public void onEvent(SteamAlarmEvent event) {
        switch (event.alarmId) {
            case AbsSteamoven.Event_Steam_Alarm_ok:
                if (dlg != null) {
                    dlg.dismiss();
                }
                imgVol.setImageResource(R.mipmap.img_steam_work_water_has);
                handler.sendEmptyMessage(Working);
                break;
            case AbsSteamoven.Event_Steam_Alarm_door:
                showDialog(SteamAlarmDoorAndWaterDialog.ALARM_DOOR);
                break;
            case AbsSteamoven.Event_Steam_Alarm_water:
                imgVol.setImageResource(R.mipmap.img_steam_work_water_has_not);
                showDialog(SteamAlarmDoorAndWaterDialog.ALARM_WATER);
                break;
            case AbsSteamoven.Event_Steam_Alarm_temp:
                break;
        }
    }

    private void showDialog(int type) {
        if (dlg == null) {
            dlg = new SteamAlarmDoorAndWaterDialog(getContext());
            Window win = dlg.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.alpha = 0.6f;
            win.setAttributes(lp);
            dlg.setCanceledOnTouchOutside(false);
            imgSteamPauseWork.setVisibility(View.VISIBLE);
            dlg.setUI(type);
            dlg.show();
        }
    }

    @Subscribe
    public void onEvent(SteamOvenStatusChangedEvent event) {
        if (steam == null || !Objects.equal(steam.getID(), event.pojo.getID())) {
            return;
        }

        handler.sendEmptyMessage(PollStatusChanged);
    }

    @Subscribe
    public void onEvent(DeviceConnectionChangedEvent event) {
        if (steam == null || !Objects.equal(steam.getID(), event.device.getID()))
            return;

        EventUtils.postEvent(new DeviceSteamSwitchViewEvent(0, null));
    }
}
