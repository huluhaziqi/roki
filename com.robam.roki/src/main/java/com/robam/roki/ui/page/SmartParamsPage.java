package com.robam.roki.ui.page;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.plat.pojos.device.AbsDeviceHub;
import com.legent.plat.pojos.device.IDevice;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ListDialog;
import com.legent.ui.ext.views.CheckBoxView;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.io.cloud.RokiRestHelper;
import com.robam.common.pojos.device.IRokiFamily;
import com.robam.common.pojos.device.SmartParams;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.device.fan.Fan8229;
import com.robam.common.pojos.device.fan.IFan;
import com.robam.common.ui.dialog.TimeSetDialog;
import com.robam.roki.R;
import com.robam.roki.ui.view.EmojiEmptyView;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/14.
 */
public class SmartParamsPage extends HeadPage implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    static final String[] week = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    static final String[] days = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    static final String[] minutes = {"1", "2", "3", "4", "5"};

    @InjectView(R.id.emptyView)
    EmojiEmptyView emptyView;
    @InjectView(R.id.mainView)
    ScrollView mainView;

    @InjectView(R.id.chkIsPowerLinkage)
    CheckBoxView chkIsPowerLinkage;
    @InjectView(R.id.txtShutdownDelay)
    TextView txtShutdownDelay;
    @InjectView(R.id.chkIsShutdownLinkage)
    CheckBoxView chkIsShutdownLinkage;
    @InjectView(R.id.chkIsLevelLinkage)
    CheckBoxView chkIsLevelLinkage;
    @InjectView(R.id.txtTimingVentilationPeriod)
    TextView txtTimingVentilationPeriod;
    @InjectView(R.id.chkIsTimingVentilation)
    CheckBoxView chkIsTimingVentilation;
    @InjectView(R.id.txtWeeklyVentilationDate_Week)
    TextView txtWeeklyVentilationDateWeek;
    @InjectView(R.id.txtWeeklyVentilationDate_Time)
    TextView txtWeeklyVentilationDateTime;
    @InjectView(R.id.chkIsWeeklyVentilation)
    CheckBoxView chkIsWeeklyVentilation;
    @InjectView(R.id.chkIsNoticClean)
    CheckBoxView chkIsNoticClean;
    @InjectView(R.id.chkIsCleanBy360)
    CheckBoxView chkIsCleanBy360;

    AbsFan fan;

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.page_smart_params, viewGroup, false);
        ButterKnife.inject(this, view);
            fan = Utils.getDefaultFan();

        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.txtRestore)
    public void onClickRestore() {

        DialogHelper.newDialog_OkCancel(cx, "确定将智能设定恢复成出厂默认设置？", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    recovery();
                }
            }
        }).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton chkBox, boolean isChecked) {
        try {
            if (chkBox == chkIsCleanBy360) {
                setSmartParamsOn360();
            } else {
                setSmartParams();
            }
        } catch (Exception e) {
            ToastUtils.showException(e);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtShutdownDelay:
                showListSelectDialog(txtShutdownDelay, minutes);
                break;
            case R.id.txtTimingVentilationPeriod:
                showListSelectDialog(txtTimingVentilationPeriod, days);
                break;
            case R.id.txtWeeklyVentilationDate_Week:
                showListSelectDialog(txtWeeklyVentilationDateWeek, week);
                break;
            case R.id.txtWeeklyVentilationDate_Time:
                showTimeSetDialog();
                break;

            default:
                break;
        }
    }

    void initData() {

        boolean isEmpty = fan == null;
        switchView(isEmpty);
        if (isEmpty) return;

            fan.getSmartConfig(new Callback<SmartParams>() {

                @Override
                public void onSuccess(SmartParams smartParams) {
                    refresh(smartParams);
                    setListener();
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                    setListener();
                }
            });



        RokiRestHelper.getSmartParams360(fan.getID(), new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean status) {
                chkIsCleanBy360.setChecked(status);
                setListenerOn360();
            }

            @Override
            public void onFailure(Throwable t) {
                setListenerOn360();
                ToastUtils.showShort("获取360燃气卫士配置失败");
            }
        });

    }


    void refresh(SmartParams smartParams) {
        if (smartParams == null) return;

        chkIsPowerLinkage.setChecked(smartParams.IsPowerLinkage);
        chkIsLevelLinkage.setChecked(smartParams.IsLevelLinkage);
        chkIsShutdownLinkage.setChecked(smartParams.IsShutdownLinkage);
        chkIsTimingVentilation.setChecked(smartParams.IsTimingVentilation);
        chkIsWeeklyVentilation.setChecked(smartParams.IsWeeklyVentilation);
        chkIsNoticClean.setChecked(smartParams.IsNoticClean);

        txtShutdownDelay.setText(String.valueOf(smartParams.ShutdownDelay));
        txtTimingVentilationPeriod.setText(String.valueOf(smartParams.TimingVentilationPeriod));
        if (smartParams.WeeklyVentilationDate_Week >= 1 && smartParams.WeeklyVentilationDate_Week <= 7) {
            txtWeeklyVentilationDateWeek.setText(week[smartParams.WeeklyVentilationDate_Week - 1]);
        }

        showWeeklyVentilationDate(smartParams.WeeklyVentilationDate_Hour,
                smartParams.WeeklyVentilationDate_Minute);
    }

    void setListener() {
        if(!this.isAdded())return;

        chkIsPowerLinkage.setOnCheckedChangeListener(this);
        chkIsLevelLinkage.setOnCheckedChangeListener(this);
        chkIsShutdownLinkage.setOnCheckedChangeListener(this);
        chkIsTimingVentilation.setOnCheckedChangeListener(this);
        chkIsWeeklyVentilation.setOnCheckedChangeListener(this);
        chkIsNoticClean.setOnCheckedChangeListener(this);

        txtShutdownDelay.setOnClickListener(this);
        txtTimingVentilationPeriod.setOnClickListener(this);
        txtWeeklyVentilationDateWeek.setOnClickListener(this);
        txtWeeklyVentilationDateTime.setOnClickListener(this);
    }

    void setListenerOn360() {
        chkIsCleanBy360.setOnCheckedChangeListener(this);
    }

    private void recovery() {
        refresh(new SmartParams());
        setSmartParams();
        chkIsCleanBy360.setChecked(false);
    }


    void setSmartParams() {
        if (fan == null) {
            return;
        }


        SmartParams sp = new SmartParams();
        sp.IsPowerLinkage = chkIsPowerLinkage.isChecked();
        sp.IsLevelLinkage = chkIsLevelLinkage.isChecked();
        sp.IsShutdownLinkage = chkIsShutdownLinkage.isChecked();
        sp.IsTimingVentilation = chkIsTimingVentilation.isChecked();
        sp.IsWeeklyVentilation = chkIsWeeklyVentilation.isChecked();
        sp.IsNoticClean = chkIsNoticClean.isChecked();

        sp.ShutdownDelay = Short.parseShort(txtShutdownDelay.getText().toString());
        sp.TimingVentilationPeriod = Short.parseShort(txtTimingVentilationPeriod.getText().toString());
        int weekDayIndex = Arrays.asList(week).indexOf(txtWeeklyVentilationDateWeek.getText().toString());
        sp.WeeklyVentilationDate_Week = (short) (weekDayIndex + 1);
        sp.WeeklyVentilationDate_Hour = Short.parseShort(txtWeeklyVentilationDateTime.getTag(R.id.tag_weekly_ventilation_date_hour).toString());
        sp.WeeklyVentilationDate_Minute = Short.parseShort(txtWeeklyVentilationDateTime.getTag(R.id.tag_weekly_ventilation_date_minute).toString());

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

    void setSmartParamsOn360() {
        RokiRestHelper.setSmartParams360(fan.getID(), chkIsCleanBy360.isChecked(), new VoidCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showShort("设置360燃气卫士配置失败");
            }
        });
    }


    private void showTimeSetDialog() {
        TimeSetDialog.show(cx, "设置时间", 12, 30, 23, 59,
                new TimeSetDialog.TimeSeletedCallback() {

                    @Override
                    public void onTimeSeleted(int hour, int minute) {
                        showWeeklyVentilationDate(hour, minute);
                        setSmartParams();
                    }

                });
    }

    void showListSelectDialog(final View parent, String[] content) {
        String title = null;
        switch (parent.getId()) {
            case R.id.txtShutdownDelay:
                title = "设置延时时间（分钟）";
                break;
            case R.id.txtTimingVentilationPeriod:
                title = "设置天数（天）";
                break;
            case R.id.txtWeeklyVentilationDate_Week:
                title = "设置周几";
                break;
        }

        List<String> list = Arrays.asList(content);
        ListDialog.show(cx, title, list, Gravity.CENTER, new ListDialog.ListItemSelectedCallback() {
            @Override
            public void onItemSelected(int i, Object obj) {
                String content = (String) obj;
                ((TextView) parent).setText(content);
                setSmartParams();
            }
        });

    }

    void showWeeklyVentilationDate(int hour, int minute) {
        txtWeeklyVentilationDateTime.setText(String.format("%02d:%02d", hour, minute));
        txtWeeklyVentilationDateTime.setTag(R.id.tag_weekly_ventilation_date_hour, hour);
        txtWeeklyVentilationDateTime.setTag(R.id.tag_weekly_ventilation_date_minute, minute);
    }

    void switchView(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mainView.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
    }


}
