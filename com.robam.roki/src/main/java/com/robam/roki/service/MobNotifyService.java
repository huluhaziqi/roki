package com.robam.roki.service;

import android.app.Activity;
import android.bluetooth.BluetoothClass;

import com.legent.plat.pojos.dictionary.DeviceType;
import com.legent.plat.services.DeviceTypeManager;
import com.legent.ui.UIService;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.device.IRokiFamily;
import com.robam.common.pojos.device.Stove.Stove;
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
        String guid =dev.getGuid().getGuid();

        switch (id) {
            case StoveAlarmManager.E_0:
                if (stove.getStoveModel().equals(IRokiFamily.R9W70)){  //Key_InnerError:
                    DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_4_InnerError);
                }else if (stove.getStoveModel().equals(IRokiFamily.R9B39)){ //定时Ok了，
                    DeviceNoticDialog.show(atv,dev,DeviceNoticDialog.Notic_Type_6_TimeOver_9B39);
                }else if (stove.getStoveModel().equals(IRokiFamily.R9B37)){ //定时Ok了，记得复位旋钮
                    DeviceNoticDialog.show(atv,dev,DeviceNoticDialog.Notic_Type_7_TimeOver_9B37);
                }

                break;
            case StoveAlarmManager.E_1:
                if (stove.getStoveModel().equals(IRokiFamily.R9W70)) {  //Key_WithoutPan:
                    DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_3_WithoutPan);
                }else if (stove.getStoveModel().equals(IRokiFamily.R9B39)){ //点火失败，请检查气源，重新点火
                    DeviceNoticDialog.show(atv,dev,DeviceNoticDialog.Notic_Type_8_FireFailure);
                }else if (stove.getStoveModel().equals(IRokiFamily.R9B37)){ //点火失败，请检查气源，重新点火
                    DeviceNoticDialog.show(atv,dev,DeviceNoticDialog.Notic_Type_8_FireFailure);
                }

                break;
            case StoveAlarmManager.E_2:
                if (DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R9W70)){  //Key_IGBT_Open
                    DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_2_RebootHint);
                }else if (stove.getStoveModel().equals(IRokiFamily.R9B39)){ //突然熄火了，请重新开火
                    DeviceNoticDialog.show(atv,dev,DeviceNoticDialog.Notic_Type_9_FireOver_Unexpect);
                }else if (stove.getStoveModel().equals(IRokiFamily.R9B37)){ //突然熄火了，请重新开火
                    DeviceNoticDialog.show(atv,dev,DeviceNoticDialog.Notic_Type_9_FireOver_Unexpect);
                }
                break;
            case StoveAlarmManager.E_3://Key_Voltage_High:
            case StoveAlarmManager.E_4://Key_Voltage_Low:
            case StoveAlarmManager.E_5://Key_Voltage_Low:
            case StoveAlarmManager.E_8://Key_Voltage_Low:
                if (DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R9W70)){  //有点小故障请联系售后
                    DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_1_Voltage);
                }else if (stove.getStoveModel().equals(IRokiFamily.R9B39)){ //有点小故障请联系售后
                    DeviceNoticDialog.show(atv,dev,DeviceNoticDialog.Notic_Type_10_InnerError);
                }else if (stove.getStoveModel().equals(IRokiFamily.R9B37)){ //有点小故障，请复位旋钮，联系售后
                    DeviceNoticDialog.show(atv,dev,DeviceNoticDialog.Notic_Type_11_InnerError_9B37);
                }

                break;

            default:
                DeviceNoticDialog.show(atv, dev, DeviceNoticDialog.Notic_Type_2_RebootHint);
        }

    }

}
