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
import com.robam.common.pojos.device.Stove.Stove9B39;
import com.robam.common.pojos.device.Stove.StoveStatus;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.UIListeners;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhaiyuanyi on 15/11/27.
 */
public class StoveCtr9B39View extends FrameLayout implements UIListeners.IStoveCtrView {
    final static String HINT_1 = "为了安全\n请在灶具上开启";
    final static String HINT_2 = "火力开启后\n才能打开计时关火";
    Stove9B39 stove;
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



    public StoveCtr9B39View(Context cx){
        super(cx);
        init(cx, null);

    }

    public StoveCtr9B39View(Context cx, AttributeSet attrs) {
        super(cx, attrs);
    }

    void init(Context cx,AttributeSet attrs){
        View view = LayoutInflater.from(cx).inflate(R.layout.view_9b39,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void attachStove(IStove stove) {
        Preconditions.checkState(stove instanceof Stove9B39, "attachFan error:not 9B39");
        this.stove = (Stove9B39) stove;
    }

    @Override
    public void onRefresh() {
        if (stove==null)
            return;;
        refreshLock(stove != null && stove.isLock);
        refreshHead(stove.leftHead);
        refreshHead(stove.rightHead);
    }

//    @OnClick(R.id.txtHint)
//    public void onClickHint() {
//        txtHint.setVisibility(View.GONE);
//    }
//
//    @OnClick(R.id.imgLock)
//    public void onClickLock() {
//        if (stove.getStoveModel().equals(IRokiFamily.R9B39)) {                   //灶具为R9B39，童锁功能取消;by zhaiyuanyi 20151120
//            ToastUtils.showShort("不允许远程控制");
//            return;
//        }
//        //setLock();
//    }

    @OnClick({R.id.txtClockLeft, R.id.txtClockRight})
    public void onClickClock(View v) {

        final Stove.StoveHead head;
        if (v.getId() == R.id.txtClockLeft)
            head = stove.leftHead;
        else
            head = stove.rightHead;

        Preconditions.checkNotNull(head);

        if (!checkConnection()) return;
        if (head.level < Stove.PowerLevel_1) {
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

    public void refreshHead(Stove.StoveHead head) {

        if (head == null || !stove.isConnected()) {
            setViewValid(head, false);
            //setViewEnable(head, false);
            showStaus(head, false);
        } else {
            setViewValid(head,false);
            if (stove.leftHead.level>=1&&stove.leftHead.level<=5
                    ||stove.rightHead.level>=1&&stove.rightHead.level<=5){
                if (stove.leftHead.level>=1&&stove.leftHead.level<=5){
                    txtClockLeft.setSelected(true);
                    txtLevelLeft.setSelected(true);
                }else {
                    txtClockLeft.setSelected(false);
                    txtLevelLeft.setSelected(false);
                }
                if (stove.rightHead.level>=1&&stove.rightHead.level<=5){
                    txtClockRight.setSelected(true);
                    txtLevelRight.setSelected(true);
                }else{
                    txtClockRight.setSelected(false);
                    txtLevelRight.setSelected(false);
                }
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

    void setViewEnable(final Stove.StoveHead head, boolean isEnable) {
        if (head.isLeft()) {

            txtClockLeft.setEnabled(isEnable);
        } else {

            txtClockRight.setEnabled(isEnable);
        }
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

    boolean checkConnection() {
        if (!stove.isConnected()) {
            ToastUtils.showShort(R.string.fan_invalid_error);
            return false;
        } else {
            return true;
        }
    }







}
