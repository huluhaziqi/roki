package com.robam.common.pojos.device;

import com.legent.plat.pojos.device.SubDeviceInfo;

/**
 * Created by zhaiyuanyi on 15/10/16.
 */
public class Stove9B12 extends Stove{
    public Stove9B12(SubDeviceInfo devInfo) {
        super(devInfo);
    }

    @Override
    public String getStoveModel() {
        return IRokiFamily.R9B12;
    }
}
