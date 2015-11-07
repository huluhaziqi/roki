package com.robam.roki.service;

import android.app.Activity;

import com.legent.ui.UIService;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.device.Stove;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.dictionary.StoveAlarm;
import com.robam.common.services.NotifyService;
import com.robam.common.services.StoveAlarmManager;
import com.robam.roki.ui.dialog.DeviceNoticDialog;

/**
 * Created by sylar on 15/6/21.
 */
public class MobNotifyService extends NotifyService {


    static MobNotifyService instance = new MobNotifyService();

    synchronized public static MobNotifyService getInstance() {
        return instance;
    }

    private MobNotifyService() {
    }

    @Override
    protected void onFanNeedClean(AbsFan fan) {
        Activity atv = UIService.getInstance().getTop().getActivity();
        if (atv == null) {
            ToastUtils.showShort("油烟机需要清洗了！");
            return;
        }

        DeviceNoticDialog.show(atv, fan, DeviceNoticDialog.Notic_Type_5_CleanNotic);
    }

    @Override
    protected void onStoveAlarmEvent(Stove stove, StoveAlarm alarm) {
        short id = alarm.getID();
        if (id == StoveAlarmManager.Key_None || id < 0)
            return;

        Activity atv = UIService.getInstance().getTop().getActivity();
        if (atv == null) {
            ToastUtils.showShort(alarm.getName());
            return;
        }


        AbsFan dev = (AbsFan)stove.getParent();

        switch (id) {
            case StoveAlarmManager.Key_WithoutPan:
                DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_3_WithoutPan);
                break;
            case StoveAlarmManager.Key_Voltage_High:
            case StoveAlarmManager.Key_Voltage_Low:
                DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_1_Voltage);
                break;
            case StoveAlarmManager.Key_InnerError:
                DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_4_InnerError);
                break;
            default:
                DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_2_RebootHint);
        }

    }
}
