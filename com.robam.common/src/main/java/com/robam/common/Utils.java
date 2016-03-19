package com.robam.common;

import com.google.common.base.Objects;
import com.legent.plat.Plat;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.plat.pojos.device.IDevice;
import com.legent.plat.pojos.dictionary.DeviceType;
import com.legent.plat.services.DeviceTypeManager;
import com.robam.common.pojos.device.IRokiFamily;
import com.robam.common.pojos.device.Oven.AbsOven;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Sterilizer.AbsSterilizer;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.fan.AbsFan;

import java.util.List;

public class Utils {

    static public boolean hasRokiDevice() {
        return getDefaultFan() != null;
    }

    static public AbsFan getDefaultFan() {
        List<AbsDevice> list = Plat.deviceService.queryDevices();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            for (int i = 0; i < list.size(); i++) {
                AbsDevice device = list.get(i);
                if (device instanceof AbsFan) {
                    return (AbsFan) device;
                }
            }
            return null;
        }
    }

    static public AbsSteamoven getDefaultSteam() {
        List<AbsDevice> list = Plat.deviceService.queryDevices();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            for (int i = 0; i < list.size(); i++) {
                AbsDevice device = list.get(i);
                if (device instanceof AbsSteamoven) {
                    return (AbsSteamoven) device;
                }
            }
            return null;
        }
    }

    static public Stove getDefaultStove() {
        AbsFan fan = (AbsFan) getDefaultFan();
        Stove stove = null;
        if (fan != null) {
            stove = fan.getChildByDeviceType(IRokiFamily.R9W70);
//            stove = fan.getChild();
        }
        return stove;
    }

    static public AbsOven getDefaultOven() {
        List<AbsDevice> list = Plat.deviceService.queryDevices();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            for (int i = 0; i < list.size(); i++) {
                AbsDevice device = list.get(i);
                if (device instanceof AbsOven) {
                    return (AbsOven) device;
                }
            }
            return null;
        }
    }

//    static public Stove getDefaultStove() {
//        AbsFan fan = (AbsFan) getDefaultFan();
//        Stove stove = null;
//        if (fan != null) {
//            //stove = fan.getChildByDeviceType(IRokiFamily.R9W70);
//            stove = fan.getChild();
//        }
//        return stove;
//    }

    static public AbsSterilizer getDefaultSterilizer() {
        List<IDevice> list = Plat.deviceService.queryAll();
        for (IDevice device : list) {
            if (device instanceof AbsSterilizer)
                return (AbsSterilizer) device;
        }
        return null;
    }

    static public String getDeviceModel(DeviceType dt) {
        String res = dt.getID();
        return res.substring(1);
    }

    static public boolean isMobApp() {
        boolean isMob = Objects.equal(Plat.app.getPackageName(),
                "com.robam.roki");
        return isMob;
    }


    static public boolean isStove(String guid) {//判断是否为灶具 by zhaiyuanyi
        return DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9W70)
                || DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9B39)
                || DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9B37);
    }

    static public boolean isFan(String guid) {//判断是否为烟机 by zhaiyuanyi
        return DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R8700)
                || DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9700)
                || DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R8229);
    }

    static public boolean isSterilizer(String guid) {//判断是否为消毒柜 by zhaiyuanyi
        return DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.RR829);
    }

    static public boolean isSteam(String guid) {//判断是否为蒸汽炉 by Rosicky
        return DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.RR209);
    }

    static public boolean isMicroWave(String guid) {//判断是否为微波炉 by Rosicky
        return DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9W73);
    }

    static public boolean isOven(String guid) {//判断是否为电烤箱 by Linxiaobin
        return DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.RR039);
    }

    static public byte whichFan(String guid) {
        if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9700) ||
                DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R8700)) {
            return 1;
        } else if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R8229)) {
            return 2;
        } else {
            return 0;
        }
    }

    static public byte whichStove(String guid) {
        if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9W70)) {
            return 1;
        } else if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9B39)) {
            return 2;
        } else {
            return 0;
        }
    }

}
