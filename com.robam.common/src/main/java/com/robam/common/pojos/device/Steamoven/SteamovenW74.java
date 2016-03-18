package com.robam.common.pojos.device.Steamoven;

import com.legent.plat.pojos.device.DeviceInfo;
import com.robam.common.pojos.device.IRokiFamily;

/**
 * Created by Rosicky on 15/12/15.
 */
public class SteamovenW74 extends AbsSteamoven implements ISteamovenW74 {
    public SteamovenW74(DeviceInfo devInfo) {
        super(devInfo);
    }

    @Override
    public String getSteamovenModel() {
        return IRokiFamily.RR209;
    }
}
