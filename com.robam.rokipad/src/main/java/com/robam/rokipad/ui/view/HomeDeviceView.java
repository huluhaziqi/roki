package com.robam.rokipad.ui.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.utils.EventUtils;
import com.robam.common.events.DeviceAddEvent;
import com.robam.rokipad.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rosicky on 16/1/2.
 */
public class HomeDeviceView extends FrameLayout {

    final static float SCALE = 375F / 800F;
    final static int TAB_Fan = 1;
    final static int TAB_Stove = 2;
    final static int TAB_Steam = 3;
    final static int TAB_Wave = 6;
    final static int TAB_Sterilizer = 5;
    final static int TAB_Oven = 4;

    private Adapter adapter;

    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.indicator)
    DeviceIndicatorView indicator;

    public HomeDeviceView(Context context) {
        super(context);
        init(context, null);
    }

    public HomeDeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeDeviceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_home_device,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            adapter = new Adapter();
            pager.setAdapter(adapter);
            pager.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            pager.setOffscreenPageLimit(adapter.getCount());
            indicator.setOnTabSelectedCallback(callback);
            indicator.selectTab(0);
            pager.setCurrentItem(0);
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

    DeviceIndicatorView.OnTabSelectedCallback callback = new DeviceIndicatorView.OnTabSelectedCallback() {
        @Override
        public void onTabSelected(int tabIndex) {
            pager.setCurrentItem(tabIndex);
            switch (tabIndex) {
                case TAB_Fan:
                    break;
                case TAB_Stove:
                    break;
                case TAB_Steam:
                    break;
                case TAB_Wave:
                    break;
                case TAB_Sterilizer:
                    break;
                case TAB_Oven:
                    break;
                default:
                    break;
            }
        }
    };

    @Subscribe
    public void onEvent(DeviceAddEvent event) {
        if (event.deviceName.equals("消毒柜")) {
            pager.setCurrentItem(TAB_Sterilizer);
        }
    }

    class Adapter extends PagerAdapter {
        List<View> views = Lists.newArrayList();

        public Adapter() {
            DeviceFanContainer fanContainer = new DeviceFanContainer(getContext());
            DeviceStoveContainer stoveContainer = new DeviceStoveContainer(getContext());
            DeviceSteamContainer steamContainer = new DeviceSteamContainer(getContext());
            DeviceOvenContainer ovenContainer = new DeviceOvenContainer(getContext());
            DeviceDeviceContainer deviceContainer = new DeviceDeviceContainer(getContext());
            DeviceDeviceContainer deviceContainer1 = new DeviceDeviceContainer(getContext());
            views.add(fanContainer);
            views.add(stoveContainer);
            views.add(steamContainer);
            views.add(ovenContainer);
            views.add(deviceContainer);
            views.add(deviceContainer1);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = views.get(position);
            container.addView(view);

            return view;
        }

    }
}
