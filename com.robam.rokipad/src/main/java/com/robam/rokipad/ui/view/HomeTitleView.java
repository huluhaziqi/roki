package com.robam.rokipad.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.events.ConnectionModeChangedEvent;
import com.legent.plat.events.DeviceConnectedNoticEvent;
import com.legent.plat.events.DeviceSelectedEvent;
import com.legent.plat.pojos.device.IDevice;
import com.legent.services.TaskService;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.NumberDialog;
import com.legent.ui.ext.utils.OnTouchListenerWithAntiShake;
import com.legent.utils.EventUtils;
import com.legent.utils.api.WifiUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.service.CookTaskService;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.UIEvents;
import com.robam.rokipad.ui.page.SettingPage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

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

    // left
    @InjectView(R.id.imgFan)
    ImageView imgFan;

    @InjectViews({R.id.imgStove, R.id.imgStove1, R.id.imgStove2})
    List<ImageView> imgStoves;

    // right
    @InjectView(R.id.img_pause)
    ImageView imgPause;

    @InjectView(R.id.img_setting)
    ImageView imgSetting;

    @InjectView(R.id.img_clock)
    ImageView imgClock;

    @InjectView(R.id.tvCountdownTime)
    TextView txtCountdownTime;

    @InjectView(R.id.layout_count_down)
    View countdownView;

    //
    List<View> deviceViews = Lists.newArrayList();
    //
    boolean isCoutDown = false;
    CountDownTimer timer = null;

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

            imgPause.setOnTouchListener(new OnTouchListenerWithAntiShake(imgPause, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTitlePause();
                }
            }));

            imgClock.setOnTouchListener(new OnTouchListenerWithAntiShake(imgClock, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStartClock();
                }
            }));

            countdownView.setOnTouchListener(new OnTouchListenerWithAntiShake(countdownView, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStopClock();
                }
            }));

            imgSetting.setOnTouchListener(new OnTouchListenerWithAntiShake(imgSetting, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSetting();
                }
            }));

            refresh();
        }

    }

    @Subscribe
    public void onEvent(ConnectionModeChangedEvent event) {
        refreshWifi();
    }

    @Subscribe
    public void onEvent(DeviceSelectedEvent event) {
        refreshDevice();
    }

    @Subscribe
    public void onEvent(DeviceConnectedNoticEvent event) {
        refreshDevice();
    }

    void refresh() {
        refreshWifi();
        refreshDevice();
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

    private void refreshDevice() {
        deviceViews.clear();
        for (View view : imgStoves) {
            view.setVisibility(GONE);
        }

        imgFan.setTag(null);
        deviceViews.add(imgFan);

        AbsFan fan = Utils.getDefaultFan();
        if (fan != null) {
            imgFan.setTag(fan);

            Stove stove = Utils.getDefaultStove();
            List<Stove> stoves = Lists.newArrayList();
            if (stove != null) {
                stoves.add(stove);
            }

            if (stoves.size() > 0) {
                View view;
                for (int i = 0; i < Math.min(stoves.size(), imgStoves.size()); i++) {
                    view = imgStoves.get(i);
                    view.setVisibility(VISIBLE);
                    view.setTag(stoves.get(i));
                    deviceViews.add(view);
                }
            }
        }

        for (View view : deviceViews) {
            view.setOnTouchListener(new OnTouchListenerWithAntiShake(view, deviceClickListener));
        }

        onClickDeviceIcon(imgFan);
    }

    OnClickListener deviceClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            onClickDeviceIcon(view);
        }
    };

    void onClickDeviceIcon(View view) {
        Object tag = view.getTag();

        for (View v : deviceViews) {
            v.setSelected(false);
        }
        view.setSelected(true);

        EventUtils.postEvent(new UIEvents.HomeDeviceSelectedEvent(
                (IDevice) tag));
    }

    // -------------------------------------------------------left end

    // -------------------------------------------------------right start
    void onTitlePause() {
        CookTaskService.getInstance().pause();
    }

    void onSetting() {
        UIService.getInstance().postPage(PageKey.Setting);
    }

    void onStartClock() {
        if (!isCoutDown) {
            String title = "设置倒计时";
            NumberDialog.show(getContext(), title, 30, 90, 60,
                    new NumberDialog.NumberSeletedCallback() {

                        @Override
                        public void onNumberSeleted(int value) {
                            imgClock.setVisibility(View.GONE);
                            countdownView.setVisibility(View.VISIBLE);
                            startCountDown(value);
                        }
                    });
        }
    }

    void onStopClock() {
        if (isCoutDown) {
            String message = "确定要取消倒计时吗？";
            DialogHelper.newDialog_OkCancel(getContext(), null, message,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dlg, int witch) {
                            if (witch == DialogInterface.BUTTON_POSITIVE) {
                                cancelCountDown();
                            }
                        }
                    }).show();
        }
    }

    private void cancelCountDown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        onTimeShutDown();
    }

    private void startCountDown(int time) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        isCoutDown = true;
        timer = new CountDownTimer(time * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int time = (int) millisUntilFinished / 1000;
                setTextTime(time);
            }

            @Override
            public void onFinish() {
                onTimeShutDown();
                showFinishDialog();
            }
        };
        timer.start();
    }

    private void onTimeShutDown() {
        isCoutDown = false;

        this.post(new Runnable() {

            @Override
            public void run() {
                imgClock.setVisibility(View.VISIBLE);
                countdownView.setVisibility(View.GONE);
            }
        });

    }

    private void showFinishDialog() {

        this.post(new Runnable() {

            @Override
            public void run() {
                String message = "倒计时结束";
                DialogHelper.newOKDialog(getContext(), null, message, null)
                        .show();
            }
        });

    }

    private void setTextTime(int time) {

        final String strTime = UiHelper.second2String(time);

        this.post(new Runnable() {

            @Override
            public void run() {
                txtCountdownTime.setText(strTime);
            }
        });

    }

    // -------------------------------------------------------right end
}
