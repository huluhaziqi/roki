package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.plat.events.DeviceStatusChangedEvent;
import com.legent.utils.EventUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WZTCM on 2016/1/14.
 */
public class SteamFloatCircleImageView extends FrameLayout {

    AbsSteamoven steam;

    @InjectView(R.id.txtTitle)
    TextView txtTitle;
    @InjectView(R.id.txtTime)
    TextView txtTime;
    @InjectView(R.id.rel)
    RelativeLayout rel;

    public SteamFloatCircleImageView(Context context) {
        super(context);
        init(context, null);
    }

    public SteamFloatCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SteamFloatCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_float_circle, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        steam = Utils.getDefaultSteam();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventUtils.regist(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventUtils.unregist(this);
    }

    @Subscribe
    public void onEvent(DeviceStatusChangedEvent event) {
        if (steam.status == SteamStatus.Off) {
            rel.setVisibility(View.GONE);
        }
    }


}
