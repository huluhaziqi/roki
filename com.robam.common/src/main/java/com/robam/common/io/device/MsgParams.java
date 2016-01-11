package com.robam.common.io.device;

public interface MsgParams {

    /**
     * 回应码 1B 0-成功，1-失败
     */
    String RC = "RC";

    /**
     * • 控制端类型[1Byte]，参考编码表
     */
    String TerminalType = "TerminalType";

    /**
     * 用户编码[10Byte]
     */
    String UserId = "UserId";

    /**
     * • 是否菜谱烧菜[1Byte]，0不是，1是
     */
    String IsCook = "IsCook";

    /**
     * 报警码[1Byte]
     */
    String AlarmId = "AlarmId";

    /**
     * 事件码[1Byte]
     */
    String EventId = "EventId";

    /**
     * 事件参数 1Byte
     */
    String EventParam = "EventParam";

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    /**
     * 童锁状态[1Byte]，{0：解锁，1上锁}
     */
    String StoveLock = "StoveLock";

    /**
     * 炉头ID[1Byte]（0左，1右）
     */
    String IhId = "IhId";

    /**
     * • 炉头Num[1Byte], 炉头的数量
     */
    String IhNum = "IhNum";

    /**
     * 炉头信息列表，数量=IhNum
     */
    String StoveHeadList = "StoveHeadList";

    /**
     * 炉头工作状态[1Byte]（0关，1待机，2工作中）
     */
    String IhStatus = "IhStatus";

    /**
     * 炉头功率等级[1Byte]（0-9档）
     */
    String IhLevel = "IhLevel";

    /**
     * 炉头定时关机时间[2BYTE]（0-6000，单位：秒）
     */
    String IhTime = "IhTime";

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    /**
     * 烟机电磁灶开关联动 联动参数:灶具开，烟机开（0关，1开） 1B
     */
    String IsPowerLinkage = "IsPowerLinkage";

    /**
     * 烟机档位联动开关 联动参数:烟机档位是否联动（0关，1开） 1B
     */
    String IsLevelLinkage = "IsLevelLinkage";

    /**
     * 电磁灶关机后烟机延时关机 联动参数:电磁灶关机后，烟机是否延时关机（0关，1开） 1B
     */
    String IsShutdownLinkage = "IsShutdownLinkage";

    /**
     * 电磁灶关机后烟机延时关机时间 联动参数:电磁灶关机后烟机延时关机时间（延时时间，单位分钟，1~5分钟） 1B
     */
    String ShutdownDelay = "ShutdownDelay";

    /**
     * 油烟机清洗提示开关 [1Byte], 0不提示，1提示
     */
    String IsNoticClean = "IsNoticClean";

    /**
     * 是否开启定时通风[1BYTE]
     */
    String IsTimingVentilation = "IsTimingVentilation";

    /**
     * 定时通风间隔时间[1BYTE],单位天
     */
    String TimingVentilationPeriod = "TimingVentilationPeriod";


    /**
     * 是否开启每周通风[1BYTE]
     */
    String IsWeeklyVentilation = "IsWeeklyVentilation";

    /**
     * 每周通风的时间--周几
     */
    String WeeklyVentilationDate_Week = "WeeklyVentilationDate_Week";

    /**
     * 每周通风的时间--小时
     */
    String WeeklyVentilationDate_Hour = "WeeklyVentilationDate_Hour";

    /**
     * 每周通风的时间--分钟
     */
    String WeeklyVentilationDate_Minute = "WeeklyVentilationDate_Minute";

    /**
     * 工作状态[1Byte]（0关机，1开机）
     */
    String FanStatus = "FanStatus";

    /**
     * 功率等级[1Byte]（0、1、2、3、6档）
     */
    String FanLevel = "FanLevel";

    /**
     * 灯开关［1Byte］（0关，1开）
     */
    String FanLight = "FanLight";

    /**
     * 是否需要清洗［1Byte］（0不需要，1需要）
     */
    String NeedClean = "NeedClean";

    /**
     * 油烟机定时工作 定时时间，[1Byte]（单位：分钟）
     */
    String FanTime = "FanTime";

    /**
     *灶具传过来的2个温度参数
     */
    String Temp_Param1 = "Temp_Param1";
    String Temp_Param2 = "Temp_Param2";
    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------
    /**
     * 消毒柜工作状态[1Byte]（0关机，1开机）
     */
    String SteriStatus = "SteriStatus";
}