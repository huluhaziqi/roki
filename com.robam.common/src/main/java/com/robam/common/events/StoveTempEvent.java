package com.robam.common.events;

import com.robam.common.pojos.device.Stove.Stove;

/**
 * Created by zhaiyuanyi on 15/10/30.
 */
public class StoveTempEvent {
    public Stove stove;
    public short temp1;
    public short temp2;
    public StoveTempEvent(Stove stove, short temp1,short temp2){
        this.stove= stove;
        this.temp1 = temp1;
        this.temp2 = temp2;
    }
}
