package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.legent.utils.EventUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/11.
 */
public class DeviceMicroContainer extends FrameLayout {

    @InjectView(R.id.titleView)
    HomeTitleView titleView;
    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;
    @InjectView(R.id.relMain)
    RelativeLayout relMain;
    @InjectView(R.id.fraIcon)
    FrameLayout fraIcon;

    public DeviceMicroContainer(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceMicroContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceMicroContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_micro_container, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
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

    @OnClick(R.id.imgAdd)
    public void onClickImg() {

    }
}
