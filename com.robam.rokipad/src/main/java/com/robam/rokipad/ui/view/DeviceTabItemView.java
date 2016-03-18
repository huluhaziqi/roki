package com.robam.rokipad.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.common.eventbus.Subscribe;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rosicky on 16/1/2.
 */
public class DeviceTabItemView extends FrameLayout {

    public final static int STEAM_VIEW = 0;
    public final static int OVEN_VIEW = 1;


    @InjectView(R.id.imgTab)
    ImageView imgTab;
    @InjectView(R.id.linTab)
    FrameLayout linTab;

    public DeviceTabItemView(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceTabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceTabItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_device_tab_item,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            if (attrs != null) {
                TypedArray ta = context.obtainStyledAttributes(attrs,
                        R.styleable.DeviceTabItem);
                int imgResid = ta.getResourceId(R.styleable.DeviceTabItem_imgSource, 0);
                ta.recycle();

                imgTab.setImageResource(imgResid);
            }
        }
    }

    public void setIsOn(int view, boolean isOn) {
        switch (view) {
            case 0:
                imgTab.setImageResource(isOn ? R.mipmap.img_tab_steam_on : R.mipmap.img_tab_steam_off);
                break;
            case 1:
                imgTab.setImageResource(isOn ? R.mipmap.img_oven_on : R.mipmap.img_oven_off);
                break;
            default:
                break;
        }
    }

    public void setImgTab(int resId) {
        imgTab.setImageResource(resId);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        imgTab.setSelected(selected);
        linTab.setSelected(selected);
    }

}
