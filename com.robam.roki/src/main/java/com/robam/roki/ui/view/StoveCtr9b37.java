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
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.device.IStove;
import com.robam.common.pojos.device.Stove;
import com.robam.common.pojos.device.Stove9B12;
import com.robam.common.pojos.device.Stove9B37;
import com.robam.common.pojos.device.StoveStatus;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.UIListeners;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhaiyuanyi on 15/12/4.
 */
public class StoveCtr9b37 extends FrameLayout implements UIListeners.IStoveCtrView {
    final static String HINT_1 = "为了安全\n请在灶具上开启";
    final static String HINT_2 = "火力开启后\n才能打开计时关火";
    Stove9B37 stove;

    @InjectView(R.id.txtClockLeft)
    TextView txtClockLeft;

    @InjectView(R.id.txtLevelLeft)
    TextView txtLevelLeft;

    @InjectView(R.id.divHeadLeft)
    LinearLayout divHeadLeft;

    @InjectView(R.id.txtClockRight)
    TextView txtClockRight;
    @InjectView(R.id.txtLevelRight)
    TextView txtLevelRight;

    @InjectView(R.id.divHeadRight)
    LinearLayout divHeadRight;

    @InjectView(R.id.divStoveHead)
    LinearLayout divStoveHead;
    @InjectView(R.id.divLock)
    RelativeLayout divLock;
    @InjectView(R.id.txtHint)
    TextView txtHint;
    @InjectView(R.id.imgLock)
    ImageView imgLock;



    public StoveCtr9b37(Context context) {
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
        Preconditions.checkState(stove instanceof Stove9B12, "attachFan error:not 9B12");
        this.stove = (Stove9B37) stove;
    }

    @Override
    public void onRefresh() {
        if (stove==null)
            return;;
        refreshLock(stove != null && stove.isLock);
        refreshHead(stove.leftHead);
        refreshHead(stove.rightHead);

    }
    public void refreshHead(Stove.StoveHead head) {

        if (head == null || !stove.isConnected()) {
            setViewValid(head, false);
            //setViewEnable(head, false);
            showStaus(head, false);
        } else {
            setViewValid(head,false);
            if (stove.leftHead.level>=1&&stove.leftHead.level<=5
                    ||stove.rightHead.level>=1&&stove.rightHead.level<=5){
                setViewValid(head,true);
            }else{
                setViewValid(head,false);
            }
            showStaus(head, true);
//
//            if (stove.isLock) {
//                setViewEnable(head, false);
//            } else {
//                setViewEnable(head, true);
//            }
        }
    }
    void setViewValid(final Stove.StoveHead head, boolean isValid) {

        if (head.isLeft()) {

            txtClockLeft.setSelected(isValid);
            txtLevelLeft.setSelected(isValid);
        } else {

            txtClockRight.setSelected(isValid);
            txtLevelRight.setSelected(isValid);
        }

        showLevel(head, 0);
        showCountdownTime(head, (short) 0);
    }
    void showStaus(Stove.StoveHead head, boolean valid) {


        if (valid) {
            boolean isOn = head.status != StoveStatus.Off;
            showLevel(head, head.level);
            showCountdownTime(head, head.time);
            showLockView(stove.isLock);
        } else {
            showLevel(head, 0);
            showCountdownTime(head, (short) 0);
            showLockView(false);
        }
    }
    void showLevel(Stove.StoveHead head, int level) {
        String str = "--";
        if (level >= 1 && level <= 9) {
            str = String.valueOf(level);
            if (head.isLeft())
                txtLevelLeft.setText("P" + str);
            else
                txtLevelRight.setText("P" + str);
        } else {
            if (head.isLeft())
                txtLevelLeft.setText(str);
            else
                txtLevelRight.setText(str);
        }
    }
    void showCountdownTime(Stove.StoveHead head, short time) {
        String strTime = time <= 0 ? "未定时" : UiHelper.second2String(time);

        if (head.isLeft())
            txtClockLeft.setText(strTime);
        else
            txtClockRight.setText(strTime);
    }
    void setStatus(final Stove.StoveHead head) {

        if (!checkConnection()) return;
        short status = (head.status == StoveStatus.Off) ? StoveStatus.StandyBy
                : StoveStatus.Off;

        stove.setStoveStatus(false, head.ihId, status, new VoidCallback() {

            @Override
            public void onSuccess() {
                refreshHead(head);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void refreshLock(boolean isLock) {
        showLockView(isLock);
        imgLock.setSelected(isLock);
    }

    void showLockView(boolean visible) {
        divLock.setVisibility(visible ? View.VISIBLE : View.GONE);
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
