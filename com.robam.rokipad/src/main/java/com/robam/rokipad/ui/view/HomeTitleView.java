package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.events.ConnectionModeChangedEvent;
import com.legent.services.TaskService;
import com.legent.ui.UIService;
import com.legent.ui.ext.utils.OnTouchListenerWithAntiShake;
import com.legent.utils.EventUtils;
import com.legent.utils.api.WifiUtils;
import com.robam.rokipad.R;
import com.robam.rokipad.service.CookTaskService;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.page.SettingPage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeTitleView extends FrameLayout {

    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",
            Locale.getDefault());

    // middle
    @InjectView(R.id.pnlMiddle)
    View pnlMiddle;

    @InjectView(R.id.tv_time)
    TextView txtTime;

    @InjectView(R.id.img_wifi)
    ImageView imgWifi;

    // right
    @InjectView(R.id.img_recipe)
    ImageView imgRecipe;
    @InjectView(R.id.img_setting)
    ImageView imgSetting;
    @InjectView(R.id.img_search)
    ImageView imgSearch;

    //
    List<View> deviceViews = Lists.newArrayList();
    //
    boolean isCoutDown = false;
    CountDownTimer timer = null;
    @InjectView(R.id.img_left)
    ImageView imgLeft;
    @InjectView(R.id.pnlRight)
    LinearLayout pnlRight;
    @InjectView(R.id.pnlLeft)
    LinearLayout pnlLeft;

    public HomeTitleView(Context context) {
        super(context);
        init(context, null);
    }

    public HomeTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
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

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_home_title,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            startTitleTimeUpdateTask();
            pnlMiddle.setOnTouchListener(new OnTouchListenerWithAntiShake(pnlMiddle, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onToSetWifi();
                }
            }));

            imgSetting.setOnTouchListener(new OnTouchListenerWithAntiShake(imgSetting, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSetting();
                }
            }));
            imgSearch.setOnTouchListener(new OnTouchListenerWithAntiShake(imgSearch, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIService.getInstance().postPage(PageKey.RecipeSearch);
                }
            }));
            refresh();
        }

    }

    public void setPnlLeft(boolean visible) {
        pnlLeft.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setPnlMiddle(boolean visible) {
        pnlMiddle.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setPnlRight(boolean visible) {
        pnlRight.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setRightIcon1(boolean visible) {
        imgRecipe.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setRightIcon2(boolean visible) {
        imgSetting.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setRightIcon3(boolean visible) {
        imgSearch.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    public void onEvent(ConnectionModeChangedEvent event) {
        refreshWifi();
    }

    @Subscribe
    void refresh() {
        refreshWifi();
    }

    // -------------------------------------------------------middle start

    void onToSetWifi() {
        Bundle bundle = new Bundle();
        bundle.putInt(PageArgumentKey.SettingItem, SettingPage.ITEM_INDEX_WIFI);
        UIService.getInstance().postPage(PageKey.Setting, bundle);
    }

    private void refreshWifi() {
        boolean isConnected = WifiUtils.isWifiConnected(getContext());
        imgWifi.setSelected(isConnected);
    }

    private void startTitleTimeUpdateTask() {

        TaskService.getInstance().scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                txtTime.post(new Runnable() {

                    @Override
                    public void run() {
                        txtTime.setText(sdf.format(Calendar.getInstance()
                                .getTime()));
                    }
                });

            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    // -------------------------------------------------------middle end

    // -------------------------------------------------------left start

//    private void refreshDevice() {
//        deviceViews.clear();
//        for (View view : imgStoves) {
//            view.setVisibility(GONE);
//        }
//
//        imgFan.setTag(null);
//        deviceViews.add(imgFan);
//
//        AbsFan fan = Utils.getDefaultFan();
//        if (fan != null) {
//            imgFan.setTag(fan);
//
//            Stove stove = Utils.getDefaultStove();
//            List<Stove> stoves = Lists.newArrayList();
//            if (stove != null) {
//                stoves.add(stove);
//            }
//
//            if (stoves.size() > 0) {
//                View view;
//                for (int i = 0; i < Math.min(stoves.size(), imgStoves.size()); i++) {
//                    view = imgStoves.get(i);
//                    view.setVisibility(VISIBLE);
//                    view.setTag(stoves.get(i));
//                    deviceViews.add(view);
//                }
//            }
//        }
//
//        for (View view : deviceViews) {
//            view.setOnTouchListener(new OnTouchListenerWithAntiShake(view, deviceClickListener));
//        }
//
//        onClickDeviceIcon(imgFan);
//    }

//    OnClickListener deviceClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            onClickDeviceIcon(view);
//        }
//    };
//
//    void onClickDeviceIcon(View view) {
//        Object tag = view.getTag();
//
//        for (View v : deviceViews) {
//            v.setSelected(false);
//        }
//        view.setSelected(true);
//
//        EventUtils.postEvent(new UIEvents.HomeDeviceSelectedEvent(
//                (IDevice) tag));
//    }

    // -------------------------------------------------------left end

    // -------------------------------------------------------right start
    void onTitlePause() {
        CookTaskService.getInstance().pause();
    }

    void onSetting() {
        UIService.getInstance().postPage(PageKey.Setting);
    }


    // -------------------------------------------------------right end
}
