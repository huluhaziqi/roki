package com.robam.common.pojos.device.Sterilizer;

/**
 * Created by zhiayuanyi on 2016/3/29.
 */
public class SteriSmartParams {
    /**
     * 是否开启隔几日消毒[1BYTE]
     */
    public boolean IsInternalDays = true;
    /**
     * 定时消毒间隔天数
     */
    public short InternalDays = 3;
    /**
     * •	是否开启每周消毒
     */
    public boolean IsWeekSteri = false;
    /**
     * 周几消毒
     */
    public short  WeeklySteri_week = 3;

    /**
     * 峰谷电时间
     */
    public short PVCTime = 20;

}
