package com.robam.roki.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.legent.VoidCallback;
import com.legent.ui.ext.dialogs.NumberDialog;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.device.Stove.IStove;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.Stove.Stove9B37;
import com.robam.common.pojos.device.Stove.StoveStatus;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.UIListeners;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhaiyuanyi on 15/12/4.
 */
public class StoveCtr9b37View extends FrameLayout implements UIListeners.IStoveCtrView {
    final static String HINT_1 = "请在灶具上\n按”电源键“开火";
    final static String HINT_2 = "火力开启后\n才能打开计时关火";
    final static String HINT_3 ="火力已关，为了安全，\n请到灶具前复位按钮";
    Stove stove;
    @InjectView(R.id.stove_fire_left)
    ImageView stove_fire_left;

    @InjectView(R.id.divClockLeft)
    LinearLayout divClockLeft;
    @InjectView(R.id.txtClockLeft)
    TextView txtClockLeft;

    @InjectView(R.id.stove_fire_right)
    ImageView stove_fire_right;

    @InjectView(R.id.divClockRight)
    LinearLayout divClockRight;
    @InjectView(R.id.txtClockRight)
    TextView txtClockRight;

    @InjectView(R.id.powerLeftView)
    StoveSwitchView powerLeftView;
    @InjectView(R.id.powerRightView)
    StoveSwitchView powerRightView;

    @InjectView(R.id.txtHint)
    TextView txtHint;




    public StoveCtr9b37View(Context context) {
        super(context);
        init(context,null);
    }

    void init(Context cx,AttributeSet attrs){
        View view = LayoutInflater.from(cx).inflate(R.layout.view_9b37,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void attachStove(IStove stove) {
        Preconditions.checkState(stove instanceof Stove, "attachFan error:not 9B37");
        this.stove = (Stove) stove;
    }

    @Override
    public void onRefresh() {
        if (stove==null)
            return;
        refreshHead(stove.leftHead);
        refreshHead(stove.rightHead);

    }
    @OnClick({R.id.divClockLeft, R.id.divClockRight})
    public void onClickClock(View v) {

        final Stove.StoveHead head;
        if (v.getId() == R.id.divClockLeft)
            head = stove.leftHead;
        else
            head = stove.rightHead;

        Preconditions.checkNotNull(head);

        if (!checkConnection()) return;
        if (head.status ==StoveStatus.Off) {
            showHint(HINT_2);
            return;
        }
        NumberDialog.show(getContext(), "设置倒计时", 0, 99, 0, new NumberDialog.NumberSeletedCallback() {
            @Override
            public void onNumberSeleted(int i) {
                int seconds = 60 * i;
                setTimeShutDown(head, (short) seconds);
            }
        });

    }

    @OnClick({R.id.powerLeftView, R.id.powerRightView})
    public void onClickPowerView(View v){
        final Stove.StoveHead head;
        if (v.getId()==R.id.powerLeftView)
            head = stove.leftHead;
        else
            head = stove.rightHead;
        Preconditions.checkNotNull(head);

        if (!checkConnection()) return;
        if (head.status == StoveStatus.Off) {
            showHint(HINT_1);
            return;
        }
        setStatus(head);
    }

    public void refreshHead(Stove.StoveHead head) {

        if (head == null || !stove.isConnected()||head.status==StoveStatus.Off) {
            setViewValid(head, false);
            showStaus(head, false);
        } else {
            setViewValid(head, false);
            if (head.status!=StoveStatus.Off){
                setViewValid(head,true);
            }else{
                setViewValid(head,false);
            }
            showStaus(head, true);

        }
    }
    void setViewValid(final Stove.StoveHead head, boolean isValid) {

        if (head.isLeft()) {
            stove_fire_left.setSelected(isValid);
            divClockLeft.setSelected(isValid);
            txtClockLeft.setSelected(isValid);
            powerLeftView.setStatus(isValid);

        } else {
            stove_fire_right.setSelected(isValid);
            divClockRight.setSelected(isValid);
            txtClockRight.setSelected(isValid);
            powerRightView.setStatus(isValid);
        }

        showCountdownTime(head, (short) 0);
    }
    void showStaus(Stove.StoveHead head, boolean valid) {


        if (valid) {
            boolean isOn = head.status != StoveStatus.Off;
            showCountdownTime(head, head.time);

        } else {
            showCountdownTime(head, (short) 0);
        }
    }

    void showCountdownTime(Stove.StoveHead head, short time) {
        String strTime = time <= 0 ? "未定时" : UiHelper.second2String(time);

        if (head.isLeft())
            txtClockLeft.setText(strTime);
        else
            txtClockRight.setText(strTime);
    }
    void setTimeShutDown(final Stove.StoveHead head, final short seconds) {

        stove.setStoveShutdown(head.ihId, seconds, new VoidCallback() {

            @Override
            public void onSuccess() {
                showCountdownTime(head, seconds);
                ToastUtils.showShort("倒计时已设置");
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }
    void setStatus(final Stove.StoveHead head) {

        if (!checkConnection()) return;
        if (!checkIsPowerOn(head)) return;
        short status = (head.status == StoveStatus.Off) ? StoveStatus.StandyBy
                : StoveStatus.Off;

        stove.setStoveStatus(false, head.ihId, status, new VoidCallback() {

            @Override
            public void onSuccess() {
                refreshHead(head);
                showHint(HINT_3);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }


    void showHint(String hint) {
        txtHint.setText(hint);
        txtHint.setVisibility(View.VISIBLE);
        txtHint.postDelayed(new Runnable() {
            @Override
            public void run() {
                //if (isVisible()) {
                txtHint.setVisibility(View.GONE);
                // }
            }
        }, 1500);

    }

    boolean checkIsPowerOn(Stove.StoveHead head) {
        if (head.status != StoveStatus.Off)
            return true;
        else {
            showHint(HINT_1);
            return false;
        }
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

