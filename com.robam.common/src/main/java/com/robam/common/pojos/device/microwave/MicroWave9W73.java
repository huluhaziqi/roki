package com.robam.common.pojos.device.microwave;

import com.legent.plat.pojos.device.DeviceInfo;
import com.robam.common.pojos.device.IRokiFamily;

/**
 * Created by WZTCM on 2015/12/17.
 */
public class MicroWave9W73 extends AbsMicroWave implements iMicroWave9W73 {
    public MicroWave9W73(DeviceInfo devInfo) {
        super(devInfo);
    }

    @Override
    public String getMicroWaveMode() {
        return IRokiFamily.R9W73;
    }
}
