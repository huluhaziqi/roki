package com.robam.common.pojos.device.microwave;

import com.legent.VoidCallback;
import com.robam.common.pojos.device.IPauseable;

/**
 * Created by WZTCM on 2015/12/17.
 */
public interface iMicroWave extends IPauseable {
    /**
     * model of fan
     */
    String getMicroWaveMode();

    /**
     * 微波炉开关命令
     * @param status
     * @param callback
     */
    void setMicroWaveState(short status, VoidCallback callback);

    /**
     * 设置微波炉品类及解冻加热
     * @param mode
     * @param weight
     * @param voidCallback
     */
    void setMicroWaveKindsAndHeatCold(short mode, int weight, VoidCallback voidCallback);

    /**
     * 设置微波炉专业模式加热
     * @param mode
     * @param time
     * @param power
     * @param callback
     */
    void setMicroWaveProModeHeat(short mode, int time, short power, VoidCallback callback);

    /**
     * 设置微波炉照明灯开/关
     * @param state
     * @param callback
     */
    void setMicroWaveLight(short state, VoidCallback callback);

    /**
     * 查询微波炉状态
     * @param callback
     */
    void getMicroWaveStatus(VoidCallback callback);



}
