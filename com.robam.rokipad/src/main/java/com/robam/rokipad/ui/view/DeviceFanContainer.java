package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.legent.utils.EventUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rosicky on 16/2/27.
 */
public class DeviceFanContainer extends FrameLayout {

    @InjectView(R.id.titleView)
    HomeTitleView titleView;
    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;
    @InjectView(R.id.relMain)
    RelativeLayout relMain;
    @InjectView(R.id.fraIcon)
    FrameLayout fraIcon;

    public DeviceFanContainer(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceFanContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceFanContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_fan_container, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        titleView.setPnlLeft(false);
        relMain.removeAllViews();
        HomeFanView v = new HomeFanView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        v.setLayoutParams(lp);
        relMain.addView(v);
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
}
