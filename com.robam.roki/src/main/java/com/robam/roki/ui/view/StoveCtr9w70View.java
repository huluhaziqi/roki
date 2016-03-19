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
import com.robam.common.pojos.device.Stove.StoveStatus;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.UIListeners;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhaiyuanyi on 15/11/25.
 */
public class StoveCtr9w70View extends FrameLayout implements UIListeners.IStoveCtrView {
    final static String HINT_1 = "为了安全\n请在灶具上开启";
    final static String HINT_2 = "火力开启后\n才能打开计时关火";
    Stove stove;
    @InjectView(R.id.txtClockLeft)
    TextView txtClockLeft;
    @InjectView(R.id.txtLevelLeft)
    TextView txtLevelLeft;
    @InjectView(R.id.imgUpLeft)
    ImageView imgUpLeft;
    @InjectView(R.id.imgDownLeft)
    ImageView imgDownLeft;
    @InjectView(R.id.divHeadLeft)
    LinearLayout divHeadLeft;
    @InjectView(R.id.txtClockRight)
    TextView txtClockRight;
    @InjectView(R.id.txtLevelRight)
    TextView txtLevelRight;
    @InjectView(R.id.imgUpRight)
    ImageView imgUpRight;
    @InjectView(R.id.imgDownRight)
    ImageView imgDownRight;
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
    @InjectView(R.id.powerLeftView)
    DeviceSwitchView powerLeftView;
    @InjectView(R.id.powerRightView)
    DeviceSwitchView powerRightView;
    @InjectView(R.id.divPower)
    LinearLayout divPower;

    public StoveCtr9w70View(Context cx){
        super(cx);
        init(cx, null);

    }

    public StoveCtr9w70View(Context cx, AttributeSet attrs) {
        super(cx, attrs);
    }

    void init(Context cx,AttributeSet attrs){
        View view = LayoutInflater.from(cx).inflate(R.layout.view_9w70,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
    }


    @Override
    public void attachStove(IStove stove) {
        Preconditions.checkState(stove instanceof Stove, "attachFan error:not 9W70");
        this.stove = (Stove) stove;
    }

    @Override
    public void onRefresh() {
        if (stove==null)
            return;;
        refreshLock(stove != null && stove.isLock);
        refreshHead(stove.leftHead);
        refreshHead(stove.rightHead);

    }

    @OnClick(R.id.txtHint)
    public void onClickHint() {
        txtHint.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgLock)
    public void onClickLock() {
        setLock();
    }


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

    @OnClick({R.id.powerLeftView, R.id.powerRightView})
    public void onClickSwitch(View v) {
        final Stove.StoveHead head;
        if (v.getId() == R.id.powerLeftView)
            head = stove.leftHead;
        else
            head = stove.rightHead;

        Preconditions.checkNotNull(head);

        setStatus(head);
    }

    @OnClick({R.id.imgUpLeft, R.id.imgUpRight})
    public void onClickUp(View v) {

        final Stove.StoveHead head;
        if (v.getId() == R.id.imgUpLeft)
            head = stove.leftHead;
        else
            head = stove.rightHead;

        Preconditions.checkNotNull(head);

        if (head.status == StoveStatus.StandyBy
                || head.status == StoveStatus.Off) {
            setLevel(head, 5);
        } else {
            int level = head.level;
            if (level < 9) {
                level++;
                setLevel(head, level);
            }
        }
    }

    @OnClick({R.id.imgDownLeft, R.id.imgDownRight})
    public void onClickDown(View v) {
        final Stove.StoveHead head;
        if (v.getId() == R.id.imgDownLeft)
            head = stove.leftHead;
        else
            head = stove.rightHead;

        Preconditions.checkNotNull(head);

        if (head.status == StoveStatus.StandyBy
                || head.status == StoveStatus.Off) {
            setLevel(head, 5);
        } else {
            int level = head.level;
            if (level > 1) {
                level--;
                setLevel(head, level);
            }
        }
    }

    //------------------------------------------------------------------------- ui event---------------------------------------------------------------------

    public void refreshHead(Stove.StoveHead head) {

        if (head == null || !stove.isConnected()) {
            setViewValid(head, false);
            setViewEnable(head, false);
            showStaus(head, false);
        } else {
            setViewValid(head, true);
            showStaus(head, true);

            if (stove.isLock) {
                setViewEnable(head, false);

                if (head.isLeft())
                    powerLeftView.setEnabled(true);
                else
                    powerRightView.setEnabled(true);
            } else {
                setViewEnable(head, true);
            }
        }
    }

    void setViewValid(final Stove.StoveHead head, boolean isValid) {

        if (head.isLeft()) {
            imgUpLeft.setSelected(isValid);
            imgDownLeft.setSelected(isValid);
            txtClockLeft.setSelected(isValid);
            txtLevelLeft.setSelected(isValid);
        } else {
            imgUpRight.setSelected(isValid);
            imgDownRight.setSelected(isValid);
            txtClockRight.setSelected(isValid);
            txtLevelRight.setSelected(isValid);
        }

        showLevel(head, 0);
        showCountdownTime(head, (short) 0);
    }

    void setViewEnable(final Stove.StoveHead head, boolean isEnable) {
        if (head.isLeft()) {
            imgUpLeft.setEnabled(isEnable);
            imgDownLeft.setEnabled(isEnable);
            txtClockLeft.setEnabled(isEnable);
            powerLeftView.setEnabled(isEnable);
        } else {
            imgUpRight.setEnabled(isEnable);
            imgDownRight.setEnabled(isEnable);
            txtClockRight.setEnabled(isEnable);
            powerRightView.setEnabled(isEnable);
        }
    }

    void showStaus(Stove.StoveHead head, boolean valid) {

        DeviceSwitchView switchView = powerLeftView;
        if (head.isRight()) {
            switchView = powerRightView;
        }

        if (valid) {
            boolean isOn = head.status != StoveStatus.Off;
            switchView.setStatus(isOn);
            showLevel(head, head.level);
            showCountdownTime(head, head.time);
            showLockView(stove.isLock);
        } else {
            switchView.setStatus(false);
            showLevel(head, 0);
            showCountdownTime(head, (short) 0);
            showLockView(false);
        }
    }

    void showLevel(Stove.StoveHead head, int level) {
        String str = "--";
        if (level >= 1 && level <= 9) {
            str = String.valueOf(level);
        }
        if (head.isLeft())
            txtLevelLeft.setText(str);
        else
            txtLevelRight.setText(str);
    }

    void showCountdownTime(Stove.StoveHead head, short time) {
        String strTime = time <= 0 ? "计时" : UiHelper.second2String(time);

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
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void setLevel(final Stove.StoveHead head, int level) {
        if (!checkConnection()) return;
        if (!checkIsPowerOn(head)) return;

        stove.setStoveLevel(false, head.ihId, (short) level,
                new VoidCallback() {

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

    void setLock() {

        if (!checkConnection())
            return;

        stove.setStoveLock(!stove.isLock, new VoidCallback() {

            @Override
            public void onSuccess() {
                onRefresh();
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
            //   }
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
