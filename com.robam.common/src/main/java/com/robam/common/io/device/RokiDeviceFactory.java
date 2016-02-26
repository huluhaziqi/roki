package com.robam.common.io.device;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.plat.pojos.device.AbsDeviceHub;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.plat.pojos.device.IDevice;
import com.legent.plat.pojos.device.IDeviceFactory;
import com.legent.plat.pojos.device.SubDeviceInfo;
import com.legent.plat.services.DeviceTypeManager;
import com.robam.common.pojos.device.IRokiFamily;
import com.robam.common.pojos.device.Sterilizer.Steri829;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.Stove.Stove9B39;
import com.robam.common.pojos.device.Stove.Stove9B37;
import com.robam.common.pojos.device.fan.Fan8229;
import com.robam.common.pojos.device.fan.Fan8700;
import com.robam.common.pojos.device.fan.Fan9700;

/**
 * Created by sylar on 15/8/10.  edited by zhaiyuanyi 15/10/16
 */
public class RokiDeviceFactory implements IDeviceFactory {

    @Override
    public IDevice generate(SubDeviceInfo deviceInfo) {
        return generateModel(deviceInfo);
    }

    public static IDevice generateModel(SubDeviceInfo deviceInfo) {//产生设备model：／8700/9700/8229...
        Preconditions.checkNotNull(deviceInfo, "deviceInfo is null!");
        Preconditions.checkState(!Strings.isNullOrEmpty(deviceInfo.guid), "guid is null or empty");

        String guid = deviceInfo.guid;
        if (deviceInfo instanceof DeviceInfo) {
            // is HubDevice
            DeviceInfo devInfo = (DeviceInfo) deviceInfo;
            if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9700)) {
                return new Fan9700(devInfo);
            } else if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R8700)) {
                return new Fan8700(devInfo);
            } else if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R8229)) {
                return new Fan8229(devInfo);
            } else if (DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.RR829)){
                return new Steri829(devInfo);
            }else{
                //TODO add log here ....
                return new AbsDeviceHub(devInfo) {
                };
            }
        } else {
            // is subDevice/chileDevice
            if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9W70)) {
                return new Stove(deviceInfo);
            } else if (DeviceTypeManager.getInstance().isInDeviceType(guid, IRokiFamily.R9B39)) {
                return new Stove9B39(deviceInfo);
            } else if (DeviceTypeManager.getInstance().isInDeviceType(guid,IRokiFamily.R9B37)){
                return new Stove9B37(deviceInfo);
            }else {
                //TODO add log here ....
                return null;
            }
        }
    }


}
