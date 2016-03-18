package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.legent.utils.EventUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WZTCM on 2016/1/11.
 */
public class DeviceDeviceContainer extends FrameLayout {

    @InjectView(R.id.titleView)
    HomeTitleView titleView;
    @InjectView(R.id.relMain)
    RelativeLayout relMain;
    @InjectView(R.id.fraIcon)
    FrameLayout fraIcon;

    public DeviceDeviceContainer(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceDeviceContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceDeviceContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_device_container, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        titleView.setPnlLeft(false);
        initData();
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

    private void initData() {
    }

}
