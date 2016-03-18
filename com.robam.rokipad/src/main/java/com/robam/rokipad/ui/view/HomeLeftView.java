package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.legent.utils.api.DisplayUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeLeftView extends FrameLayout {

    final static float SCALE = 375F / 800F;
    @InjectView(R.id.titleView)
    HomeTitleView titleView;

    public HomeLeftView(Context context) {
        super(context);
        init(context, null);
    }

    public HomeLeftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeLeftView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @InjectView(R.id.deviceView)
    HomeLeftDeviceView deviceView;

    @InjectView(R.id.advertView)
    HomeLeftAdvertView advertView;

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_home_left,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            int deviceViewHeight = computeHeight();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) deviceView
                    .getLayoutParams();
            lp.height = deviceViewHeight;
            deviceView.setLayoutParams(lp);
            titleView.setPnlLeft(false);
        }
    }

    private int computeHeight() {
        int screenHeight = DisplayUtils.getScreenHeightPixels(getContext());
        // - (int) DisplayUtils.getActionBarHeight(getContext());
        int deviceViewHeight = (int) (screenHeight * SCALE);
        return deviceViewHeight;
    }

}
