package com.robam.roki.ui.view;


import android.app.Activity;
import android.content.Context;
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
    @InjectView(R.id.rl_germ)
    RelativeLayout rlGerm;
    @InjectView(R.id.rl_hum)
    RelativeLayout rlHum;
    @InjectView(R.id.rl_ozone)
    RelativeLayout rlOzone;

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

    }

    @OnClick(R.id.sterilizer_switch)
    public void onClickSwitch(View v) {
        boolean selected = ((CheckBox) v).isChecked();
        setBtnSelected(selected);
        setStatus(selected);
    }

    @OnClick(R.id.tv_sterilizer_btn)
    public void onClickSteri() {
        if (sterilizer.isSelected()) {
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
        steri.setSteriStatus(status, new VoidCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
