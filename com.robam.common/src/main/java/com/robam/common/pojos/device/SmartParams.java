package com.robam.common.pojos.device;

/**
 * Created by sylar on 15/6/10.
 */
public class SmartParams {


    /**
     * 烟机电磁灶开关联动(0关，1开)[1Byte]
     */
    public boolean IsPowerLinkage = true;

    /**
     * 烟机档位联动开关（0关，1开）[1Byte]
     */
    public boolean IsLevelLinkage;

    /**
     * 电磁灶关机后烟机延时关机开关（0关，1开）[1Byte]
     */
    public boolean IsShutdownLinkage = true;


    /**
     * 电磁灶关机后烟机延时关机时间（延时时间，单位分钟，1~5分钟）[1Byte]
     */
    public short ShutdownDelay = 1;


    /**
     * 油烟机清洗提示开关[1Byte], 0不提示，1提示
     */
    public boolean IsNoticClean = true;

    /**
     * 是否开启定时通风[1BYTE]
     */
    public boolean IsTimingVentilation;
    /**
     * 定时通风间隔时间[1BYTE],单位天
     */
    public short TimingVentilationPeriod = 3;

    /**
     * 是否开启每周通风[1BYTE]
     */
    public boolean IsWeeklyVentilation;

    /**
     * 每周通风的时间--周几
     */
    public short WeeklyVentilationDate_Week = 1;

    /**
     * 每周通风的时间--小时
     */
    public short WeeklyVentilationDate_Hour = 12;

    /**
     * 每周通风的时间--分钟
     */
    public short WeeklyVentilationDate_Minute = 30;

}
