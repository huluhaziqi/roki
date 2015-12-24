package com.robam.common;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.legent.plat.Plat;
import com.legent.plat.pojos.device.IDevice;
import com.legent.plat.pojos.dictionary.DeviceType;
import com.legent.plat.services.DeviceTypeManager;
import com.robam.common.pojos.device.IRokiFamily;
import com.robam.common.pojos.device.Stove;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.device.fan.IFan;

import java.util.List;

public class Utils {

    static public boolean hasRokiDevice() {
        return getDefaultFan() != null;
    }

    static public AbsFan getDefaultFan() {
        AbsFan fan = Plat.deviceService.getDefault();
        return fan;
    }

    static public Stove getDefaultStove() {
        AbsFan fan = (AbsFan)getDefaultFan();
        Stove stove = null;
        if (fan != null) {
            //stove = fan.getChildByDeviceType(IRokiFamily.R9W70);
            stove =fan.getChild();
        }
        return stove;
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
                ||DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R9B39);
    }

    static public boolean isFan(String guid) {//判断是否为烟机 by zhaiyuanyi
        return DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R8700)
                || DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9700)
                ||DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R8229);
    }
    static public boolean isSterilizer(String guid){//判断是否为消毒柜 by zhaiyuanyi
        return DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.RR829);
    }
    static public byte whichFan(String guid){
        if (DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R9700)||
                DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R8700))
        {
            return 1;
        }else if (DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R8229))
        {
            return 2;
        }else
        {
            return  0;
        }
    }
    static public byte whichStove(String guid){
        if (DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R9W70)){
            return 1;
        }else if (DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R9B39)){
            return 2;
        }else{
            return 0;
        }
    }

}
