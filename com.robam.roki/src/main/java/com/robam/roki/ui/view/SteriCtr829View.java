package com.robam.roki.ui.view;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.legent.VoidCallback;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.dialog.SteriStopWorkingDialog;
import com.robam.roki.ui.dialog.SteriStovingDialog;
import com.robam.roki.ui.dialog.SteriTimeDialog;
import com.robam.common.pojos.device.Sterilizer.ISterilizer;
import com.robam.common.pojos.device.Sterilizer.Steri829;
import com.robam.common.pojos.device.Sterilizer.SteriStatus;
import com.robam.roki.R;
import com.robam.roki.ui.SterilizerAnimationUtil;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.dialog.CountDownDialog;
import com.robam.roki.ui.dialog.SteriWarnDialog;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhaiyuanyi on 15/10/20.
 */
public class SteriCtr829View extends FrameLayout implements UIListeners.ISteriCtrView {
    Steri829 steri;
    SterilizerAnimationUtil animationUtil;
    //警告弹框资源图片
    int[] imgs = {R.mipmap.img_steri_wran_door, R.mipmap.img_steri_wran_tem};
    @InjectView(R.id.tv_order_btn)
    TextView order;
    @InjectView(R.id.tv_stoving_btn)
    TextView stoving;
    @InjectView(R.id.tv_clean_btn)
    TextView clean;
    @InjectView(R.id.tv_sterilizer_btn)
    TextView sterilizer;
    @InjectView(R.id.rl_tem)
    RelativeLayout rlTem;
    @InjectView(R.id.tv_steri_tem)
    TextView tv_steri_tem;
    @InjectView(R.id.sterilizer_switch)
    CheckBox steriSwitch;
    @InjectView(R.id.tv_steri)
    TextView tvSteriRunning;
    @InjectView(R.id.tv_steri_time_hour)
    TextView txtHour;
    @InjectView(R.id.tv_steri_time_minute)
    TextView txtMinute;
    @InjectView(R.id.tv_steri_time_point)
    TextView txtPoint;
    @InjectView(R.id.ll_empty)
    LinearLayout imgEmpty;
    @InjectView(R.id.ll_animation)
    LinearLayout llAnimation;

    @InjectView(R.id.rl_germ)
    RelativeLayout rlGerm;
    @InjectView(R.id.tv_steri_germ)
    TextView tv_steri_germ;

    @InjectView(R.id.rl_hum)
    RelativeLayout rlHum;
    @InjectView(R.id.tv_steri_hum)
    TextView tv_steri_hum;

    @InjectView(R.id.rl_ozone)
    RelativeLayout rlOzone;
    @InjectView(R.id.tv_steri_ozone)
    TextView tv_steri_ozone;
    @InjectView(R.id.rl_running)
    RelativeLayout rlRunning;
    @InjectView(R.id.rl_switch)
    RelativeLayout rlSwitch;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String s = msg.what % 2 == 0 ? "" : ":";
            txtPoint.setText(s);
        }
    };

    public SteriCtr829View(Context context) {
        super(context);
        init(context, null);
    }

    public SteriCtr829View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_829,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            animationUtil = new SterilizerAnimationUtil(cx, rlTem, rlHum, rlGerm, rlOzone);
            animationUtil.setAnimation();
            setPoint();
        }
    }

    @Override
    public void attachSteri(ISterilizer steri) {
        Preconditions.checkState(steri instanceof Steri829, "attachFan error:not 829");
        this.steri = (Steri829) steri;
    }

    @Override
    public void onRefresh() {
        if (steri == null)
            return;
        //断开连接
        if (!steri.isConnected()) {
            steriSwitch.setClickable(false);
        } else {
            steriSwitch.setClickable(true);
        }
        //消毒柜警告状态判断
        //JudgeWran();
        //设置控制按钮状态
        setRunningState();
        //设置消毒时间
        setRunningTime();
        initData();
    }

    //电源开关
    @OnClick(R.id.sterilizer_switch)
    public void onClickSwitch(View v) {
        boolean selected = ((CheckBox) v).isChecked();
        setBtnSelected(selected);
        setStatus(selected);
    }

    //预约消毒控制
    @OnClick(R.id.tv_order_btn)
    public void onClickOrderRunning() {
        if (steri.status == 1) {
            onStartOrderClock();
        }
    }

    //烘干控制
    @OnClick(R.id.tv_stoving_btn)
    public void onClickStoveRunning() {
        if (steri.status == 1) {
            onStartDryingClock();
        }
    }

    //快洁控制
    @OnClick(R.id.tv_clean_btn)
    public void onClickCleanRunning() {
        if (steri.status == 1) {
            steri.setSteriClean((short) 60, new VoidCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(Throwable t) {
                }
            });
        }
    }

    //消毒控制
    @OnClick(R.id.tv_sterilizer_btn)
    public void onClickSteriRunning() {
        if (steri.status == 1) {
            steri.setSteriDisinfect((short) 150, new VoidCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(Throwable t) {
                }
            });
        }
    }

    //停止工作
    @OnClick(R.id.fl_running)
    public void onClickStopWorking() {
        String str = "结束工作";
        if (steri.status == 2)
            str = "结束消毒";
        if (steri.status == 3)
            str = "结束保洁";
        if (steri.status == 4)
            str = "结束烘干";
        if (steri.status == 5)
            str = "结束预约";
        SteriStopWorkingDialog.show(getContext(), str, new UIListeners.StopworkCallback() {
            @Override
            public void callBack() {
                setStatus(true);
            }
        });
    }

    //停止工作并断开电源
    @OnClick(R.id.sterilizer_switch_run)
    public void onClickTakeOff() {
        String str = "结束工作";
        if (steri.status == 2)
            str = "关闭电源，\n消毒工作将停止。";
        if (steri.status == 3)
            str = "关闭电源，\n保洁工作将停止。";
        if (steri.status == 4)
            str = "关闭电源，\n烘干工作将停止。";
        if (steri.status == 5)
            str = "关闭电源，\n预约工作将停止。";
        SteriStopWorkingDialog.show(getContext(), str, new UIListeners.StopworkCallback() {
            @Override
            public void callBack() {
                setStatus(false);
            }
        });
    }

    private void initData() {
        short temp = steri.temp;
        short germ = steri.germ;
        short hum = steri.hum;
        short ozone = steri.ozone;
        tv_steri_tem.setText(String.valueOf(temp));
        tv_steri_germ.setText(String.valueOf(germ));
        tv_steri_hum.setText(String.valueOf(hum));
        tv_steri_ozone.setText(String.valueOf(ozone));
    }

    private void JudgeWran() {
        if (steri.AlarmStautus == 0) {
            SteriWarnDialog.show(getContext(), "消毒柜门没关好", imgs[0]);
        }
        if (steri.AlarmStautus == 1) {
            SteriWarnDialog.show(getContext(), "消毒柜故障", "*可能是紫外线灯管或上层传感器出现问题", imgs[1], new UIListeners.StopworkCallback() {
                @Override
                public void callBack() {
                    //一键售后处理
                }
            });
        }
        if (steri.AlarmStautus == 2) {
            SteriWarnDialog.show(getContext(), "无法显示消毒状况", "*温度传感器出现问题", imgs[1], new UIListeners.StopworkCallback() {
                @Override
                public void callBack() {
                    //一键售后处理
                }
            });
        }
    }

    private void setRunningState() {
        if (steri.status == 2 || steri.status == 3 || steri.status == 4 || steri.status == 5) {
            llAnimation.setVisibility(VISIBLE);
            imgEmpty.setVisibility(GONE);
            rlRunning.setVisibility(VISIBLE);
            rlSwitch.setVisibility(GONE);
            if (steri.status == 2)
                tvSteriRunning.setText("消毒中");
            if (steri.status == 3)
                tvSteriRunning.setText("快洁中");
            if (steri.status == 4)
                tvSteriRunning.setText("烘干中");
            if (steri.status == 5)
                tvSteriRunning.setText("预约消毒中");
            if (PageKey.STERI_CD_FLAG) {
                CountDownDialog.start((Activity) getContext());
                PageKey.STERI_CD_FLAG = false;
            }
        } else {
            llAnimation.setVisibility(GONE);
            imgEmpty.setVisibility(VISIBLE);
            rlRunning.setVisibility(GONE);
            rlSwitch.setVisibility(VISIBLE);
            if (steri.status == 1) {
                setBtnSelected(true);
                steriSwitch.setChecked(true);
            } else {
                setBtnSelected(false);
                steriSwitch.setChecked(false);
            }
            PageKey.STERI_CD_FLAG = true;
        }
    }

    private void setRunningTime() {
        int time = 0;
        if (steri.status == 5) {
            time = (steri.work_left_time_l + steri.work_left_time_h) * 60;
        } else {
            time = steri.work_left_time_l + steri.work_left_time_h;
        }
        String hour = time / 60 < 10 ? "0" + time / 60 : String.valueOf(time / 60);
        String minute = time % 60 == 0 ? "00" : time % 60 < 10 ? "0" + String.valueOf(time % 60) : String.valueOf(time % 60);
        txtHour.setText(hour);
        txtMinute.setText(minute);
    }

    public void setBtnSelected(boolean btnSelected) {
        order.setSelected(btnSelected);
        stoving.setSelected(btnSelected);
        clean.setSelected(btnSelected);
        sterilizer.setSelected(btnSelected);
        if (sterilizer.isSelected()) {
            sterilizer.setTextColor(Color.parseColor("#ffffff"));
        } else {
            sterilizer.setTextColor(Color.parseColor("#575757"));
        }
    }

    public void setStatus(boolean witchStatus) {
        short status = witchStatus ? SteriStatus.On : SteriStatus.Off;
        steri.setSteriPower(status, new VoidCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    //设置预约倒计时
    void onStartOrderClock() {
        String title = "预约消毒";
        String timeUnit = "小时";
        SteriTimeDialog.show(getContext(), title, timeUnit, 0, 24, 12, new SteriTimeDialog.NumberSeletedCallback() {
            @Override
            public void onNumberSeleted(int value) {
                steri.SetSteriReserveTime((short) value, new VoidCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });
            }
        });
    }

    //设置烘干模式
    void onStartDryingClock() {
//        String title = "烘干模式";
//        String timeUnit = "分钟";
//        SteriTimeDialog.show(getContext(), title, timeUnit, 0, 225, 120, new SteriTimeDialog.NumberSeletedCallback() {
//            @Override
//            public void onNumberSeleted(int value) {
//                steri.setSteriDrying((short) value, new VoidCallback() {
//                    @Override
//                    public void onSuccess() {
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                    }
//                });
//            }
//        });
        SteriStovingDialog.show(getContext(), new UIListeners.SteriStoveCallback() {
            @Override
            public void callBack(int time) {
                steri.setSteriDrying((short) time, new VoidCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });
            }
        });
    }

    //时间显示闪烁
    private void setPoint() {
        Timer timer = new Timer();
        MyTask myTask = new MyTask();
        timer.schedule(myTask, 0, 1000);
    }

    class MyTask extends TimerTask {
        //时间显示中间两点闪烁标志位
        int i = 0;

        @Override
        public void run() {
            if (rlRunning.getVisibility() == GONE)
                return;
            Message message = handler.obtainMessage();
            message.what = i;
            handler.sendMessage(message);
            i++;
        }
    }
}

