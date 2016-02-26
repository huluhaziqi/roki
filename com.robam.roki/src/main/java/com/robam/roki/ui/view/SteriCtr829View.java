package com.robam.roki.ui.view;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.legent.VoidCallback;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.NumberDialog;
import com.robam.roki.ui.dialog.SteriStopWorkingDialog;
import com.robam.roki.ui.dialog.SteriTimeDialog;
import com.robam.common.pojos.device.Sterilizer.ISterilizer;
import com.robam.common.pojos.device.Sterilizer.Steri829;
import com.robam.common.pojos.device.Sterilizer.SteriStatus;
import com.robam.roki.R;
import com.robam.roki.ui.SterilizerAnimationUtil;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.dialog.CountDownDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhaiyuanyi on 15/10/20.
 */
public class SteriCtr829View extends FrameLayout implements UIListeners.ISteriCtrView {
    Steri829 steri;
    SterilizerAnimationUtil animationUtil;
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
    @InjectView(R.id.tv_steri_time)
    TextView tvRunningTime;

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
        setRunningState();
        setRunningTime();
        initData();
    }

    @OnClick(R.id.sterilizer_switch)
    public void onClickSwitch(View v) {
        boolean selected = ((CheckBox) v).isChecked();
        setBtnSelected(selected);
        setStatus(selected);
    }

    @OnClick(R.id.tv_order_btn)
    public void onClickOrderRunning() {
        if (steri.status == 1) {
            onStartOrderClock();
        }
    }

    @OnClick(R.id.tv_stoving_btn)
    public void onClickStoveRunning() {
        if (steri.status == 1) {
            onStartDryingClock();
        }
    }

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
            CountDownDialog.start((Activity) getContext());
        }
    }

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
            CountDownDialog.start((Activity) getContext());
        }
    }

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

    private void setRunningState() {
        if (steri.status == 2 || steri.status == 3 || steri.status == 4 || steri.status == 5) {
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
        } else {
            if (steri.status == 1) {
                setBtnSelected(true);
                steriSwitch.setChecked(true);
            } else {
                setBtnSelected(false);
                steriSwitch.setChecked(false);
            }
            rlRunning.setVisibility(GONE);
            rlSwitch.setVisibility(VISIBLE);
        }
    }

    private void setRunningTime() {
        int time = 0;
        if (steri.status == 3) {
            time = steri.work_left_time_l + steri.work_left_time_h;
        } else {
            time = steri.work_left_time_l;
        }
        int hour = time / 60;
        String minute = time % 60 == 0 ? "00" : String.valueOf(time % 60);
        tvRunningTime.setText("0" + hour + ":" + minute);
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
        String title = "烘干模式";
        String timeUnit = "分钟";
        SteriTimeDialog.show(getContext(), title, timeUnit, 0, 225, 120, new SteriTimeDialog.NumberSeletedCallback() {
            @Override
            public void onNumberSeleted(int value) {
                steri.setSteriDrying((short) value, new VoidCallback() {
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
}
