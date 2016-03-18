package com.robam.common.pojos.device.Oven;

/**
 * Created by linxiaobin on 2015/12/27.
 */
public interface OvenStatus {
    /**
     * 待机
     */
    short Wait = 0;
    /**
     * 关机
     */
    short Off = 1;
    /**
     * 开机
     */
    short On = 2;
    /**
     * 暂停
     */
    short Pause = 3;
    /**
     * 运行
     */
    short Working = 4;

    short SelfTest = 5;
    /**
     * 报警状态
     */
    short AlarmStatus = 6;

}
