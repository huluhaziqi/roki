package com.robam.common.events;

import com.legent.events.AbsEvent;
import com.robam.common.pojos.device.Oven.AbsOven;

/**
 * Created by linxiaobin on 2015/12/27.
 */
public class OvenSpitRotateResetEvent extends AbsEvent {
    public AbsOven oven;
    public short rotate;


    public OvenSpitRotateResetEvent(AbsOven oven, short rotate) {
        super(oven);
        this.oven = oven;
        this.rotate = rotate;
    }
}

