package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.SmartParams;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.ui.dialog.TimeSetDialog;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.dialog.SettingPopWindow;

import java.util.Arrays;

public class SettingSmartConfigView extends FrameLayout implements
        OnClickListener, OnCheckedChangeListener {

    static final boolean Default_IsPowerLinkage = true;
    static final boolean Default_IsLevelLinkage = false;
    static final boolean Default_IsShutdownLinkage = true;
    static final boolean Default_SmartDailyEnable = false;
    static final boolean Default_SmartWeeklyEnable = false;
    static final boolean Default_ClearHintEnable = true;
    static final int Default_ShutDownDelay = 1;
    static final int Default_TimingVentilationPeriod = 3;
    static final int Default_WeeklyVentilationDate_Week = 1;
    static final int Default_WeeklyVentilationDate_Hour = 12;
    static final int Default_WeeklyVentilationDate_Minute = 30;

    static final String[] week = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    static final String[] days = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    static final String[] minutes = {"1", "2", "3", "4", "5"};

    TextView tvTime, tvWeek, tvClose, tvDate;
    CheckBox cbSwitch, cbLevel, cbTime, cbAir, cbClose, cbHint;
    Button btnRecovery;

    AbsFan fan;

    public SettingSmartConfigView(Context context) {
        super(context);
        init(context, null);
    }

    public SettingSmartConfigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingSmartConfigView(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context cx, AttributeSet attrs) {
        fan = Utils.getDefaultFan();
        View root = LayoutInflater.from(cx).inflate(
                R.layout.view_setting_smart_config, this, true);
        tvWeek = (TextView) root.findViewById(R.id.tv_week);
        tvTime = (TextView) root.findViewById(R.id.tv_time);
        tvClose = (TextView) root.findViewById(R.id.tv_close);
        tvDate = (TextView) root.findViewById(R.id.tv_date);
        cbSwitch = (CheckBox) root.findViewById(R.id.cb_switch);
        cbLevel = (CheckBox) root.findViewById(R.id.cb_level);
        cbClose = (CheckBox) root.findViewById(R.id.cb_close);
        cbAir = (CheckBox) root.findViewById(R.id.cb_air);
        cbTime = (CheckBox) root.findViewById(R.id.cb_time);
        cbHint = (CheckBox) root.findViewById(R.id.cb_hint);
        btnRecovery = (Button) root.findViewById(R.id.btn_recover);

        btnRecovery.setOnClickListener(this);
        initData();
    }

    private void initData() {
        if (fan == null) {
            recovery();
            setListener();
            return;
        }

        fan.getSmartConfig(new Callback<SmartParams>() {

            @Override
            public void onSuccess(SmartParams smartParams) {
                refresh(smartParams);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
                setListener();
            }
        });
    }

    void refresh(SmartParams smartParams) {
        if (smartParams == null) return;

        cbSwitch.setChecked(smartParams.IsPowerLinkage);
        cbLevel.setChecked(smartParams.IsLevelLinkage);
        cbTime.setChecked(smartParams.IsShutdownLinkage);
        tvTime.setText(String.valueOf(smartParams.ShutdownDelay));
        cbHint.setChecked(smartParams.IsNoticClean);

        cbAir.setChecked(smartParams.IsTimingVentilation);
        tvClose.setText(String.valueOf(smartParams.TimingVentilationPeriod));

        cbClose.setChecked(smartParams.IsWeeklyVentilation);
        if (smartParams.WeeklyVentilationDate_Week > 0 && smartParams.WeeklyVentilationDate_Week < week.length) {
            tvWeek.setText(week[smartParams.WeeklyVentilationDate_Week - 1]);
        }
        showWeeklyVentilationDate(smartParams.WeeklyVentilationDate_Hour,
                smartParams.WeeklyVentilationDate_Minute);

        setListener();
    }

    void setListener() {
        cbSwitch.setOnCheckedChangeListener(this);
        cbLevel.setOnCheckedChangeListener(this);
        cbTime.setOnCheckedChangeListener(this);
        tvTime.setOnClickListener(this);
        cbHint.setOnCheckedChangeListener(this);

        tvWeek.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        cbAir.setOnCheckedChangeListener(this);
        cbClose.setOnCheckedChangeListener(this);
    }

    private void recovery() {
        cbSwitch.setChecked(Default_IsPowerLinkage);
        cbLevel.setChecked(Default_IsLevelLinkage);
        cbTime.setChecked(Default_IsShutdownLinkage);
        cbAir.setChecked(Default_SmartDailyEnable);
        cbClose.setChecked(Default_SmartWeeklyEnable);
        cbHint.setChecked(Default_ClearHintEnable);
        tvTime.setText(String.valueOf(Default_ShutDownDelay));
        tvClose.setText(String.valueOf(Default_TimingVentilationPeriod));
        tvWeek.setText(week[Default_WeeklyVentilationDate_Week - 1]);
        showWeeklyVentilationDate(Default_WeeklyVentilationDate_Hour,
                Default_WeeklyVentilationDate_Minute);

        setSmartParams();
    }

    void setSmartParams() {
        if (fan == null) {
            return;
        }

        SmartParams sp = new SmartParams();
        sp.IsPowerLinkage = cbSwitch.isChecked();
        sp.IsLevelLinkage = cbLevel.isChecked();
        sp.IsShutdownLinkage = cbTime.isChecked();
        sp.IsNoticClean = cbHint.isChecked();
        sp.ShutdownDelay = Short.parseShort(tvTime.getText().toString());

        sp.IsTimingVentilation = cbAir.isChecked();
        sp.TimingVentilationPeriod = Short.parseShort(tvClose.getText().toString());
        sp.IsWeeklyVentilation = cbClose.isChecked();

        int weekDayIndex = Arrays.asList(week).indexOf(tvWeek.getText().toString());
        sp.WeeklyVentilationDate_Week = (short) (weekDayIndex + 1);
        sp.WeeklyVentilationDate_Hour = Short.parseShort(tvDate.getTag(R.id.tag_weekly_ventilation_date_hour).toString());
        sp.WeeklyVentilationDate_Minute = Short.parseShort(tvDate.getTag(R.id.tag_weekly_ventilation_date_minute).toString());

        fan.setSmartConfig(sp, new VoidCallback() {

            @Override
            public void onSuccess() {
                ToastUtils.showShort("设置成功");
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean arg1) {
        try {
            switch (v.getId()) {
                case R.id.cb_time:
                    setSmartParams();
                    break;
                case R.id.cb_air:
                    setSmartParams();
                    break;
                case R.id.cb_close:
                    setSmartParams();
                    break;
                case R.id.cb_switch:
                    if (!cbSwitch.isChecked()) {
                        cbLevel.setChecked(false);
                    }
                    cbLevel.setEnabled(cbSwitch.isChecked());

                    setSmartParams();
                    break;
                case R.id.cb_level:
                    setSmartParams();
                    break;
                case R.id.cb_hint:
                    setSmartParams();
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            ToastUtils.showException(e);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_week:
                showPopWindow(v, week);
                break;
            case R.id.tv_close:
                showPopWindow(v, days);
                break;
            case R.id.tv_time:
                showPopWindow(v, minutes);
                break;
            case R.id.tv_date:
                setTime();
                break;
            case R.id.btn_recover:
                recovery();
                break;

            default:
                break;
        }
    }

    private void setTime() {
        TimeSetDialog.show(getContext(), "设置时间", 12, 30, 23, 59,
                new TimeSetDialog.TimeSeletedCallback() {

                    @Override
                    public void onTimeSeleted(int hour, int minute) {
                        showWeeklyVentilationDate(hour, minute);
                        setSmartParams();
                    }

                });
    }

    private void showPopWindow(View parent, String[] content) {

        SettingPopWindow.show(getContext(), parent, content,
                new SettingPopWindow.ItemSelectedCallback() {

                    @Override
                    public void onItemSelected(View parent, int position,
                                               Object obj) {
                        String content = (String) obj;
                        ((TextView) parent).setText(content);

                        switch (parent.getId()) {
                            case R.id.tv_time:
                                setSmartParams();
                                break;
                            case R.id.tv_close:
                                setSmartParams();
                                break;
                            case R.id.tv_week:
                                setSmartParams();
                                break;

                            default:
                                break;
                        }

                    }
                });

    }


    void showWeeklyVentilationDate(int hour, int minute) {
        tvDate.setText(String.format("%02d:%02d", hour, minute));
        tvDate.setTag(R.id.tag_weekly_ventilation_date_hour, hour);
        tvDate.setTag(R.id.tag_weekly_ventilation_date_minute, minute);
    }

}
