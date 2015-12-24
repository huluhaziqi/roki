package com.robam.rokipad.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.legent.VoidCallback;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.NumberDialog;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.Stove.Stove.StoveHead;
import com.robam.common.pojos.device.Stove.StoveStatus;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class StoveHeadView extends FrameLayout {

    final int levelImg[] = {R.mipmap.ic_stove_gear_off,
            R.mipmap.ic_stove_gear1, R.mipmap.ic_stove_gear2,
            R.mipmap.ic_stove_gear3, R.mipmap.ic_stove_gear4,
            R.mipmap.ic_stove_gear5, R.mipmap.ic_stove_gear6,
            R.mipmap.ic_stove_gear7, R.mipmap.ic_stove_gear8,
            R.mipmap.ic_stove_gear9, R.mipmap.ic_stove_gear_off};


    @InjectView(R.id.divHeadView)
    View divHeadView;

    @InjectView(R.id.switch_left)
    ImageView imgPowerLeft;

    @InjectView(R.id.switch_right)
    ImageView imgPowerRight;

    @InjectView(R.id.img_stove_left)
    ImageView imgLevelLeft;

    @InjectView(R.id.img_stove_right)
    ImageView imgLevelRight;

    @InjectView(R.id.img_stove_gear)
    ImageView imgLevel;

    @InjectView(R.id.img_clock)
    ImageView imgTimer;

    @InjectView(R.id.txtTime)
    TextView txtTime;

    @InjectView(R.id.clockView)
    View clockView;

    public StoveHeadView(Context context) {
        super(context);
        init(context, null);
    }

    public StoveHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StoveHeadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    StoveHead head;
    Stove stove;

    boolean isValid = true;
    int level;

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_stove, this,
                true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            if (attrs != null) {
                TypedArray ta = cx.obtainStyledAttributes(attrs,
                        R.styleable.StoveHeadView);
                boolean isLeft = ta.getBoolean(
                        R.styleable.StoveHeadView_isLeft, false);
                ta.recycle();

                imgPowerLeft.setVisibility(isLeft ? VISIBLE : GONE);
                imgPowerRight.setVisibility(!isLeft ? VISIBLE : GONE);

                initViewStatus();
            }
        }
    }

    void setData(StoveHead head) {
        this.head = head;
        this.stove = head.parent;
    }

    void initViewStatus() {
        setValid(false);
        imgLevel.setImageResource(R.mipmap.ic_stove_invalid);
        setCounddownTime(0);
    }

    void refresh() {

        boolean valid = head != null && stove.isConnected();

        if (!valid) {
            initViewStatus();
        } else if (stove.isLock) {
            setValid(false);

            imgPowerLeft.setEnabled(true);
            imgPowerRight.setEnabled(true);

            refreshLevel(head.level);
            setCounddownTime(head.time);
        } else {
            switch (head.status) {
                case StoveStatus.Off:
                    setValid(false);

                    imgPowerLeft.setEnabled(true);
                    imgPowerRight.setEnabled(true);
                    imgLevel.setImageResource(R.mipmap.ic_stove_invalid);
                    setCounddownTime(0);
                    break;
                case StoveStatus.StandyBy:
                    setValid(true);
                    refreshLevel(0);
                    setCounddownTime(0);
                    clockView.setEnabled(false);
                    break;
                case StoveStatus.Working:
                    setValid(true);
                    refreshLevel(head.level);
                    setCounddownTime(head.time);
                    break;

                default:
                    break;
            }
        }
    }

    void setValid(boolean valid) {
        this.isValid = valid;

        imgPowerLeft.setEnabled(stove != null && stove.isConnected() && !stove.isLock);
        imgPowerRight.setEnabled(stove != null && stove.isConnected() && !stove.isLock);
        imgLevelLeft.setEnabled(valid);
        imgLevelRight.setEnabled(valid);
        clockView.setEnabled(valid);
        //
        imgLevelLeft.setSelected(valid);
        imgLevelRight.setSelected(valid);
        imgTimer.setSelected(valid);

        //
        txtTime.setSelected(valid);
        divHeadView.setSelected(valid);
    }

    void refreshLevel(int level) {
        if (level == this.level) return;
        this.level = level;

        ImageUtils.displayImage(ImageUtils.fromDrawable(levelImg[level]),
                imgLevel);
    }

    void setCounddownTime(int time) {
        String strTime = UiHelper.second2String(time);
        if (Objects.equal(strTime, txtTime.getText().toString())) return;

        txtTime.setText(strTime);
    }


    //==============================================================================================================================

    @OnClick({R.id.switch_left, R.id.switch_right})
    public void onClickPower() {
        onSetStatus();
    }

    @OnClick(R.id.img_stove_left)
    public void onClickLeftLevel() {
        if (head.status == StoveStatus.StandyBy) {
            onSetLevel(Stove.PowerLevel_5);
        } else {
            short level = head.level;
            if (level > 1) {
                level--;
                onSetLevel(level);
            }
        }
    }

    @OnClick(R.id.img_stove_right)
    public void onClickRightLevel() {
        if (head.status == StoveStatus.StandyBy) {
            onSetLevel(Stove.PowerLevel_5);
        } else {
            short level = head.level;
            if (level < 9) {
                level++;
                onSetLevel(level);
            }
        }
    }

    @OnClick(R.id.clockView)
    public void onClickClock() {

        boolean isCoutDown = head.time > 0;
        if (isCoutDown) {
            String message = "确定要取消倒计时吗？";
            DialogHelper.newDialog_OkCancel(getContext(), null, message,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dlg, int witch) {
                            if (witch == DialogInterface.BUTTON_POSITIVE) {
                                onSetShutdown(0);
                            }
                        }
                    }).show();
        } else {

            String title = "设置倒计时";
            NumberDialog.show(getContext(), title, 0, 99, 0, new NumberDialog.NumberSeletedCallback() {
                @Override
                public void onNumberSeleted(int i) {
                    onSetShutdown(i * 60);
                }
            });
        }
    }

    void onSetStatus() {
        if (!checkDevice())
            return;

        short status = head.status == StoveStatus.Off ? StoveStatus.StandyBy
                : StoveStatus.Off;
        stove.setStoveStatus(false, head.ihId, status, new VoidCallback() {

            @Override
            public void onSuccess() {
                refresh();
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void onSetLevel(short level) {
        if (!checkDevice())
            return;

        stove.setStoveLevel(false, head.ihId, level, new VoidCallback() {

            @Override
            public void onSuccess() {
                refresh();
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void onSetShutdown(int seconds) {
        if (!checkDevice())
            return;

        stove.setStoveShutdown(head.ihId, (short) seconds, new VoidCallback() {

            @Override
            public void onSuccess() {
                refresh();
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    boolean checkDevice() {
        try {
            Resources r = getResources();
            Preconditions.checkNotNull(head,
                    r.getString(R.string.dev_invalid_error));
            Preconditions.checkState(stove.isConnected(),
                    r.getString(R.string.stove_invalid_error));
            return true;
        } catch (Exception e) {
            ToastUtils.showException(e);
            return false;
        }
    }

    //==============================================================================================================================

}
