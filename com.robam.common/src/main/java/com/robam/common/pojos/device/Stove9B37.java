package com.robam.common.pojos.device;

import com.legent.plat.pojos.device.SubDeviceInfo;

/**
 * Created by zhaiyuanyi on 15/12/4.
 */
public class Stove9B37 extends Stove {
    public Stove9B37(SubDeviceInfo devInfo) {
        super(devInfo);
    }

    @Override
    public String getStoveModel() {
        return IRokiFamily.R9B37;
    }
}