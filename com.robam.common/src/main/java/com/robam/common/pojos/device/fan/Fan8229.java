package com.robam.common.pojos.device.fan;

import com.legent.plat.pojos.device.DeviceInfo;
import com.robam.common.pojos.device.IRokiFamily;


/**
 * Created by zhaiyuanyi on 15/10/19.
 */
public class Fan8229 extends AbsFan implements IFan8229 {
    public Fan8229(DeviceInfo devInfo) {
        super(devInfo);
    }

    @Override
    public String getFanModel() {

        return IRokiFamily.R8229;
    }

}
