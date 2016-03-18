package com.robam.common.pojos.device.microwave;

/**
 * Created by WZTCM on 2015/12/17.
 */
public interface MicroWaveStatus {

    /**
     * 关机
     */
    short Off = 0;

    /**
     * 待机
     */
    short Wait = 1;

    /**
     * 开机（启动）
     */
    short On = 2;

    /**
     * 暂停
     */
    short Pause = 3;
}
