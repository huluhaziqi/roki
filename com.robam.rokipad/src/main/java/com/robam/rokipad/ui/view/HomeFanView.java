package com.robam.rokipad.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.common.base.Preconditions;
import com.legent.VoidCallback;
import com.legent.ui.ext.utils.OnTouchListenerWithAntiShake;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.device.fan.Fan9700;
import com.robam.rokipad.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

public class HomeFanView extends FrameLayout {

    @InjectView(R.id.fanLight)
    FanLightView fanLight;

    @InjectViews({R.id.level1, R.id.level2, R.id.level3, R.id.level6})
    List<FanLevelView> fanLevels;

    int level;
    AbsFan fan;

    public HomeFanView(Context cx) {
        super(cx);
        init(cx, null);
    }

    public HomeFanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeFanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_home_fan,
                this, true);

        if (!isInEditMode()) {
            ButterKnife.inject(this, view);

            fanLight.setOnTouchListener(new OnTouchListenerWithAntiShake(fanLight, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickLight((FanLightView) v);
                }
            }));


            for (FanLevelView levelView : fanLevels) {
                levelView.setOnTouchListener(new OnTouchListenerWithAntiShake(levelView, new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickLevel((FanLevelView) view);
                    }
                }));
            }

            fanLevels.get(0).setLevel(1);
            fanLevels.get(1).setLevel(2);
            fanLevels.get(2).setLevel(3);
            fanLevels.get(3).setLevel(6);

            fan = Utils.getDefaultFan();

        }

    }

    void setData(AbsFan fan) {
        this.fan = fan;
    }

    void refresh() {
        if (fan == null)
            return;

        setPowerView(fan.light);
        setLevelView(fan.level);
    }

    synchronized void onClickLevel(final FanLevelView view) {
        if (!checkDevice())
            return;

        final short level = !view.isSelected() ? view.getLevel() : 0;

        fan.setFanLevel(level, new VoidCallback() {

            @Override
            public void onSuccess() {
                setLevelView(level);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void onClickLight(final FanLightView view) {
        if (!checkDevice())
            return;

        final boolean power = !view.power();
        fan.setFanLight(!view.power(), new VoidCallback() {

            @Override
            public void onSuccess() {
                setPowerView(power);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void setPowerView(final boolean power) {
        fanLight.setPower(power);
    }

    void setLevelView(final short level) {
        if(level == this.level)return;
        this.level = level;

//        fanWind.setLevel(fan.level);
        for (FanLevelView flv : fanLevels) {
            flv.setSelected(false);
        }

        FanLevelView flv = null;
        switch (level) {
            case 1:
                flv = fanLevels.get(0);
                break;
            case 2:
                flv = fanLevels.get(1);
                break;
            case 3:
                flv = fanLevels.get(2);
                break;
            case 6:
                flv = fanLevels.get(3);
                break;

            default:
                break;
        }

        if (flv != null) {
            flv.setSelected(true);
        }

    }

    boolean checkDevice() {
        try {
            Resources r = getResources();
            Preconditions.checkNotNull(fan,
                    r.getString(R.string.dev_invalid_error));
            Preconditions.checkState(fan.isConnected(),
                    r.getString(R.string.fan_invalid_error));
            return true;
        } catch (Exception e) {
            ToastUtils.showException(e);
            return false;
        }
    }

}
