package com.robam.common.events;

import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.device.fan.IFan;

public class FanLevelEvent {
    public AbsFan fan;
    public short level;

    public FanLevelEvent(AbsFan fan, short level) {
        this.fan = fan;
        this.level = level;
    }
}
