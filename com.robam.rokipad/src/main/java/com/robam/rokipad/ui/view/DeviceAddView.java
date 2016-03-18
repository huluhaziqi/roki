package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.legent.utils.EventUtils;
import com.robam.common.events.DeviceLinkEvent;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/11.
 */
public class DeviceAddView extends FrameLayout {

    @InjectView(R.id.imgAdd)
    ImageView imgAdd;
    @InjectView(R.id.txtMsg)
    TextView txtMsg;
    @InjectView(R.id.txtMsg2)
    TextView txtMsg2;
    @InjectView(R.id.imgTag)
    ImageView imgTag;

    public DeviceAddView(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceAddView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceAddView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_device_add, this, true);
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

    public void setMsg(String s, String s1) {
        txtMsg.setText(s);
        txtMsg2.setText(s1);
    }

    @OnClick(R.id.imgAdd)
    public void onClickImg() {
        EventUtils.postEvent(new DeviceLinkEvent());
    }
}
