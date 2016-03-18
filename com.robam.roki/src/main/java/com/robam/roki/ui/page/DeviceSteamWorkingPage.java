package com.robam.roki.ui.page;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback2;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.plat.events.RecipeShowEvent;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.popoups.PopoupHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.SteamAlarmEvent;
import com.robam.common.events.SteamOvenStatusChangedEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.roki.R;
import com.robam.roki.model.DeviceWorkMsg;
import com.robam.roki.ui.Helper;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.dialog.SteamOvenCancelWorkDialog;
import com.robam.roki.ui.dialog.SteamOvenCountDownDialog;
import com.robam.roki.ui.dialog.SteamOvenSensorBrokeDialog;
import com.robam.roki.ui.dialog.SteamOvenWarningDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rosicky on 15/12/28.
 */
public class DeviceSteamWorkingPage extends BasePage {

    static final int Start = 4;
    static final int Pause = 0;
    static final int Working = 1;
    static final int Done = 2;
    static final int CountDown = 3;
    static final int Reset = 5;
    static final int ReturnHome = 6;
    static final int PollStatusChanged = 7;
    static final int RefreshTime = 8;

    AbsSteamoven steam;
    @InjectView(R.id.relCurTem)
    RelativeLayout relCurTem;
    @InjectView(R.id.relCurTime)
    RelativeLayout relCurTime;
    @InjectView(R.id.txtTemSet)
    TextView txtTemSet;
    @InjectView(R.id.txtTimeSet)
    TextView txtTimeSet;

    private short mode;
    private short preTime;
    private short preTemp;
    private String type = "";
    private int lastTime;
    private boolean canCountDown = false;
    private boolean fromSetting = false;

    private SteamOvenWarningDialog dlg = null;

    private Animation circleRotate = null;

    View contentView;
    @InjectView(R.id.txtCurrentTem)
    TextView txtCurrentTem;
    @InjectView(R.id.txtCurrentTime)
    TextView txtCurrentTime;
    @InjectView(R.id.imgCurVolumn)
    ImageView imgCurVolumn;
    @InjectView(R.id.imgSpinCircle)
    ImageView imgSpinCircle;
    @InjectView(R.id.imgCurTem)
    ImageView imgCurTem;
    @InjectView(R.id.imgCurTime)
    ImageView imgCurTime;
    @InjectView(R.id.imgContent)
    ImageView imgContent;
    @InjectView(R.id.txtWarning)
    TextView txtWarning;
    @InjectView(R.id.imgPause)
    ImageView imgPause;
    @InjectView(R.id.imgDone)
    ImageView imgDone;
    @InjectView(R.id.workType1)
    TextView workType1;
    @InjectView(R.id.workType2)
    TextView workType2;
    @InjectView(R.id.imgReturn)
    ImageView imgReturn;
    @InjectView(R.id.txtRecipe)
    TextView imgRecipe;

    boolean doneWork = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Start:  // 从设置页进入，设置专业模式，成功则开始转圈，不倒计时
                    steam.setSteamWorkMode(mode, preTemp, preTime, (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            relCurTem.setClickable(false);
                            relCurTime.setClickable(false);
                            if (circleRotate == null) {
                                circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_steamoven_circle_rotate);
                                LinearInterpolator lin = new LinearInterpolator();
                                circleRotate.setInterpolator(lin);
                            }
                            imgSpinCircle.startAnimation(circleRotate);
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
                            imgCurTem.setVisibility(View.VISIBLE);
                            imgCurTime.setVisibility(View.VISIBLE);
                            txtWarning.setVisibility(View.GONE);
                            imgPause.setVisibility(View.VISIBLE);
                            relCurTem.setClickable(true);
                            relCurTime.setClickable(true);
                            imgSpinCircle.clearAnimation();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });
                    break;
                case Working:
                    steam.setSteamStatus(SteamStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            imgCurTem.setVisibility(View.GONE);
                            imgCurTime.setVisibility(View.GONE);
                            txtWarning.setVisibility(View.VISIBLE);
                            imgPause.setVisibility(View.GONE);
                            relCurTem.setClickable(false);
                            relCurTime.setClickable(false);
                            if (circleRotate == null) {
                                circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_steamoven_circle_rotate);
                                LinearInterpolator lin = new LinearInterpolator();
                                circleRotate.setInterpolator(lin);
                            }
                            imgSpinCircle.startAnimation(circleRotate);

                            handler.sendEmptyMessage(CountDown); // 设置运行状态成功后开始倒计时
                            canCountDown = true;
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                    break;
                case Done:
                    imgDone.setVisibility(View.VISIBLE);
                    txtWarning.setVisibility(View.GONE);
                    imgContent.setVisibility(View.GONE);
                    imgSpinCircle.clearAnimation();
                    imgSpinCircle.setVisibility(View.GONE);
                    workType2.setText("已完成");
                    workType2.setVisibility(View.VISIBLE);
                    // 10min后自动关机
                    handler.sendEmptyMessageDelayed(ReturnHome, 3 * 1000);
                    break;
                case CountDown:
                    break;
                case Reset:
                    final DeviceWorkMsg message = (DeviceWorkMsg) msg.obj;
                    steam.setSteamWorkTemp(Short.valueOf(message.getTemperature()), new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            steam.setSteamWorkTime(Short.valueOf(message.getTime()), new VoidCallback() {
                                @Override
                                public void onSuccess() {
                                    txtCurrentTime.setText(message.getTime());
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
                    imgSpinCircle.clearAnimation();
                    UIService.getInstance().returnHome();
                    break;
                case PollStatusChanged:
                    txtCurrentTem.setText(String.valueOf(steam.temp));
                    int min = steam.time / 60;
                    int sec = steam.time % 60;
                    if (sec > 0) {
                        min++;
                    }
                    txtCurrentTime.setText(String.valueOf(min));
                    txtTemSet.setText(String.valueOf(steam.tempSet));
                    txtTimeSet.setText(String.valueOf(steam.timeSet));
                    if (min == 0 && sec == 0) {
                        doneWork = true;
                        handler.sendEmptyMessage(Done);
                        break;
                    }
                    if (steam.status == SteamStatus.Pause) {
                        imgCurTem.setVisibility(View.VISIBLE);
                        imgCurTime.setVisibility(View.VISIBLE);
                        txtWarning.setVisibility(View.GONE);
                        imgPause.setVisibility(View.VISIBLE);
                        relCurTem.setClickable(true);
                        relCurTime.setClickable(true);
                        imgSpinCircle.clearAnimation();
                        circleRotate = null;
                    } else if (steam.status == SteamStatus.Working) {
                        if (dlg != null) {
                            dlg.dismiss();
                            dlg = null;
                        }
                        imgCurVolumn.setImageResource(R.mipmap.img_steam_oven_volumn_has);
                        imgCurTem.setVisibility(View.GONE);
                        imgCurTime.setVisibility(View.GONE);
                        txtWarning.setVisibility(View.VISIBLE);
                        imgPause.setVisibility(View.GONE);
                        relCurTem.setClickable(false);
                        relCurTime.setClickable(false);
                        if (circleRotate == null) {
                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_steamoven_circle_rotate);
                            LinearInterpolator lin = new LinearInterpolator();
                            circleRotate.setInterpolator(lin);
                            imgSpinCircle.startAnimation(circleRotate);
                        }
                        if (steam.time < lastTime) {
                            if (!canCountDown) {
                                handler.sendEmptyMessage(CountDown);
                                canCountDown = true;
                            }
                        } else {
                            lastTime = steam.time;
                            canCountDown = false;
                        }
                    } else if (steam.status == SteamStatus.Off) {
                        UIService.getInstance().returnHome();
                    } else if (steam.status == SteamStatus.Wait) {
                    }
                    break;
                case RefreshTime:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bd = getArguments();
        String guid = bd == null ? null : bd.getString(PageArgumentKey.Guid);
        DeviceWorkMsg msg = bd == null ? null : (DeviceWorkMsg) bd.getSerializable("msg");
        if (msg != null) {
            preTime = Short.valueOf(msg.getTime());
            preTemp = Short.valueOf(msg.getTemperature());
            mode = msg.getMode();
            type = msg.getType();
        }
        steam = Plat.deviceService.lookupChild(guid);

        contentView = inflater.inflate(R.layout.page_device_steamoven_work,
                container, false);
        ButterKnife.inject(this, contentView);

        // ------- 检测到蒸汽炉处于暂停或工作状态（从首页进入）
        if (steam.status == SteamStatus.Pause || steam.status == SteamStatus.Working) {
            initView();
        }
        // ------- 检测到蒸汽炉处于开机但没工作状态（从设置页进入）
        if (steam.status == SteamStatus.On) {
            restoreView();
        }
        checkDoorState();
        return contentView;
    }

    private void initView() {
        int min = steam.time / 60;
        int sec = steam.time % 60;
        if (sec > 0) {
            min++;
        }
        txtCurrentTime.setText(String.valueOf(min));
        if (steam.status == SteamStatus.Pause) {
            imgCurTem.setVisibility(View.VISIBLE);
            imgCurTime.setVisibility(View.VISIBLE);
            txtWarning.setVisibility(View.GONE);
            imgPause.setVisibility(View.VISIBLE);
            relCurTem.setClickable(true);
            relCurTime.setClickable(true);
            imgSpinCircle.clearAnimation();
        } else {
            imgCurTem.setVisibility(View.GONE);
            imgCurTime.setVisibility(View.GONE);
            txtWarning.setVisibility(View.VISIBLE);
            imgPause.setVisibility(View.GONE);
            relCurTem.setClickable(false);
            relCurTime.setClickable(false);
            if (circleRotate == null) {
                circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_steamoven_circle_rotate);
                LinearInterpolator lin = new LinearInterpolator();
                circleRotate.setInterpolator(lin);
            }
            imgSpinCircle.startAnimation(circleRotate);
        }
    }

    private void restoreView() {
        txtTemSet.setText(preTemp + "");
        txtTimeSet.setText(preTime + "");
        if (type.equals("自洁") || type.equals("杀菌")) {
            workType1.setVisibility(View.GONE);
            workType2.setText(type);
            workType2.setVisibility(View.VISIBLE);
            imgContent.setImageResource(type.equals("自洁") ? R.mipmap.img_steamoven_clean : R.mipmap.img_steamoven_sterilize);
            imgContent.setClickable(false);
            imgSpinCircle.setClickable(false);
        } else {
            fromSetting = true;
            workType1.setText(type);
            imgContent.setImageResource(getRes(mode));
        }
        handler.sendEmptyMessage(Start);
    }

    int getRes(short mode) {
        int res = -1;
        switch (mode) {
            case 2:
                res = R.mipmap.img_steam_content_meat;
                break;
            case 3:
                res = R.mipmap.img_steam_content_seafood;
                break;
            case 4:
                res = R.mipmap.img_steam_content_egg;
                break;
            case 5:
                res = R.mipmap.img_steam_content_cake;
                break;
            case 6:
                res = R.mipmap.img_steam_content_tijin;
                break;
            case 7:
                res = R.mipmap.img_steam_content_vege;
                break;
            case 8:
                res = R.mipmap.img_steam_content_noddle;
                break;
            case 9:
                res = R.mipmap.img_steam_content_unfreeze;
                break;
            default:
                break;
        }
        return res;
    }

    private void checkDoorState() {
        if (steam.doorState == AbsSteamoven.Steam_Door_Open) {
            showDialog(null, AbsSteamoven.Steam_Door_Open);
        } else {
            readyToWork();
        }
    }

    private void readyToWork() {
        lastTime = 0;
        if (steam.status == SteamStatus.On || steam.status == SteamStatus.Wait) {
            SteamOvenCountDownDialog.show(getContext(), 4);
        }
    }

    // ------------------------------------------  弹出对话框 ------------------------------

    private void showDialog(String s, short type) {
        if (dlg == null) {
            dlg = new SteamOvenWarningDialog(cx);
            Window win = dlg.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.alpha = 0.6f;
            win.setAttributes(lp);
//            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            imgSpinCircle.clearAnimation();
            imgPause.setVisibility(View.VISIBLE);
            dlg.setText(s);
            dlg.changeImg(type);
            dlg.show();
        }
    }

    private void showAfterBuyDialog() {
        SteamOvenSensorBrokeDialog dlg = new SteamOvenSensorBrokeDialog(cx);
        Window win = dlg.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.6f;
        win.setAttributes(lp);
        dlg.setCancelable(false);
        dlg.show();
    }

    /**
     * ========================================================
     * onClick代码段，响应各种点击事件
     */


    @OnClick({R.id.relCurTem, R.id.relCurTime})
    public void OnClickReset() {
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
        PopupWindow pop = Helper.newSteamOvenTwoSettingPicker(cx, new Callback2<DeviceWorkMsg>() {
            @Override
            public void onCompleted(final DeviceWorkMsg message) {
                Message msg = Message.obtain();
                msg.what = Reset;
                msg.obj = message;
                handler.sendMessage(msg);
            }
        }, msg);

        PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
    }

    @OnClick(R.id.imgContent)
    public void onClickContent() {
        if (steam.status == SteamStatus.Working) {
            handler.sendEmptyMessage(Pause);
        } else if (steam.status == SteamStatus.Pause) {
            handler.sendEmptyMessage(Working);
        } else if (steam.status == SteamStatus.AlarmStatus) {

        }
    }

    @OnClick(R.id.imgSwitch1)
    public void onClickSwitch() {
        String s = "结束蒸汽炉工作";
        String s1 = " ";
        SteamOvenCancelWorkDialog.Listener listener = new SteamOvenCancelWorkDialog.Listener() {
            @Override
            public void onClick() {
                steam.setSteamStatus(SteamStatus.Off, new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        if (fromSetting) {
                            UIService.getInstance().returnHome();
                        } else {
                            UIService.getInstance().returnHome();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ToastUtils.showThrowable(t);
                    }
                });
            }
        };
        SteamOvenCancelWorkDialog.show(getContext(), s, s1, listener);
    }

    @OnClick(R.id.imgReturn)
    public void onClickReturn() {
        UIService.getInstance().returnHome();
    }

    @OnClick(R.id.txtRecipe)
    public void onClickRecipe() {
        // 跳转菜谱
        postEvent(new RecipeShowEvent());
        UIService.getInstance().popBack();
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
                imgCurVolumn.setImageResource(R.mipmap.img_steam_oven_volumn_has);
                handler.sendEmptyMessage(Working);
                break;
            case AbsSteamoven.Event_Steam_Alarm_door:
                showDialog(null, AbsSteamoven.Steam_Door_Open);
                break;
            case AbsSteamoven.Event_Steam_Alarm_water:
                imgCurVolumn.setImageResource(R.mipmap.img_steam_alarm_water);
                showDialog(null, AbsSteamoven.Event_Steam_Alarm_water);
                break;
            case AbsSteamoven.Event_Steam_Alarm_temp:
                showAfterBuyDialog();
                break;
        }
    }

    @Subscribe
    public void onEvent(SteamOvenStatusChangedEvent event) {
        if (steam == null || !Objects.equal(steam.getID(), event.pojo.getID())) {
            return;
        }

        if (doneWork) {
            return;
        }
        handler.sendEmptyMessage(PollStatusChanged);
    }

    @Subscribe
    public void onEvent(DeviceConnectionChangedEvent event) {
        if (steam == null || !Objects.equal(steam.getID(), event.device.getID()))
            return;

        UIService.getInstance().popBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 4:
                UIService.getInstance().returnHome();
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
