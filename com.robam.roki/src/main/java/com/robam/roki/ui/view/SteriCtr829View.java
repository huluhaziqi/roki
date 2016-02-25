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
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.device.Sterilizer.AbsSterilizer;
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
        if (steri.status == 2 || steri.status == 3 || steri.status == 4 || steri.status == 5) {
            rlRunning.setVisibility(VISIBLE);
            rlSwitch.setVisibility(GONE);
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
        short temp = steri.temp;
        short germ = steri.germ;
        short hum = steri.hum;
        short ozone = steri.ozone;
        tv_steri_tem.setText(String.valueOf(temp));
        tv_steri_germ.setText(String.valueOf(germ));
        tv_steri_hum.setText(String.valueOf(hum));
        tv_steri_ozone.setText(String.valueOf(ozone));

    }

    @OnClick(R.id.sterilizer_switch)
    public void onClickSwitch(View v) {
        boolean selected = ((CheckBox) v).isChecked();
        setBtnSelected(selected);
        setStatus(selected);
    }

    @OnClick(R.id.tv_order_btn)
    public void onClickSteriOrder() {
        if (steri.status == 1) {
            onStartOrderClock();
        } else {
            onStopOrderClock();
        }
    }

    @OnClick({R.id.tv_sterilizer_btn, R.id.tv_stoving_btn, R.id.tv_clean_btn})
    public void onClickSteriRunning() {
        if (sterilizer.isSelected()) {
            steri.setSteriDisinfect((short) 20, new VoidCallback() {
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
//        final short currentFanlevel = fan.level;
        String title = "预约消毒";
        NumberDialog.show(getContext(), title, 0, 24, 12,
                new NumberDialog.NumberSeletedCallback() {

                    @Override
                    public void onNumberSeleted(final int value) {
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

    //停止预约倒计时
    void onStopOrderClock() {

        String message = "确定要关闭倒计时吗？";
//        final short currentFanlevel = fan.level;
        DialogHelper.newDialog_OkCancel(getContext(), null, message,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dlg, int witch) {
                        if (witch == DialogInterface.BUTTON_POSITIVE) {
                            steri.SetSteriReserveTime((short) 0, new VoidCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFailure(Throwable t) {

                                }
                            });
                        }
                    }
                }).show();
    }


    //开始消毒
    void onStartDisinfect() {

    }

    //停止消毒
    void onStopDisinfectClock() {

    }


    //设置烘干倒计时
    void onStartDryingClock() {
//        final short currentFanlevel = fan.level;
        String title = "设置倒计时";
        NumberDialog.show(getContext(), title, 60, 80, 60,
                new NumberDialog.NumberSeletedCallback() {

                    @Override
                    public void onNumberSeleted(final int value) {
//                        fan.setFanTimeWork(currentFanlevel, (short) value, new VoidCallback() {
//                            @Override
//                            public void onSuccess() {
//                                //changeClockViewStatus((short)value);
//                            }
//
//                            @Override
//                            public void onFailure(Throwable t) {
//                                ToastUtils.showThrowable(t);
//                            }
//                        });
                    }
                });
    }

    //停止烘干倒计时
    void onStopDryingClock() {

        String message = "确定要关闭倒计时吗？";
//        final short currentFanlevel = fan.level;
        DialogHelper.newDialog_OkCancel(getContext(), null, message,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dlg, int witch) {
//                        if (witch == DialogInterface.BUTTON_POSITIVE) {
//                            fan.setFanTimeWork(currentFanlevel, (short) 0, new VoidCallback() {
//                                @Override
//                                public void onSuccess() {
//
//                                }
//
//                                @Override
//                                public void onFailure(Throwable t) {
//
//                                }
//                            });
//                        }
                    }
                }).show();
    }

    //开始快洁
    void onStartClean() {

    }

    //停止快洁
    void onStopClean() {

    }


}
