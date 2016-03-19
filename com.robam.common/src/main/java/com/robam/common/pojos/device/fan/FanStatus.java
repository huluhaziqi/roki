package com.robam.common.pojos.device.fan;

public interface FanStatus {
    /**
     * 关机
     */
    short Off = 0;

    /**
     * 开机
     */
    short On = 1;

    /**
     * 延时关机
     */
    short DelayShutdown = 2;

}