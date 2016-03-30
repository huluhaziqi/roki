package com.robam.common.pojos.device.Steamoven;

import android.util.Log;

import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.io.RCMsgCallbackWithVoid;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.utils.LogUtils;
import com.robam.common.events.SteamAlarmEvent;
import com.robam.common.events.SteamCookbookResetEvent;
import com.robam.common.events.SteamOvenStatusChangedEvent;
import com.robam.common.events.SteamPowerEvent;
import com.robam.common.events.SteamTempResetEvent;
import com.robam.common.events.SteamTimeResetEvent;
import com.robam.common.io.device.MsgKeys;
import com.robam.common.io.device.MsgParams;
import com.robam.common.io.device.TerminalType;

/**
 * Created by Rosicky on 15/12/14.
 */
public class AbsSteamoven extends AbsDevice implements ISteamoven {


    static final public short Event_Steam_Power = 10;
    static final public short Event_Steam_Time_Reset = 11;
    static final public short Event_Steam_Temp_Reset = 12;
    static final public short Event_Steam_Cookbook_Reset = 13;

    static final public short Event_Steam_Alarm_ok = 0;
    static final public short Event_Steam_Alarm_water = 1;
    static final public short Event_Steam_Alarm_temp = 2;
    static final public short Event_Steam_Alarm_door = 3;

    static final public short Steam_Door_Open = 0;
    static final public short Steam_Door_Close = 1;


    public short childLock = 0;
    public short status = SteamStatus.Off;
    public short tempSet = -1;
    public short timeSet = -1;
    public short mode = 0;
    public short alarm = 0;
    public short temp = 0; // 当前温度
    public short time = 0; // 当前剩余时间
    public short doorState = 0;

    protected short terminalType = TerminalType.getType();

    public AbsSteamoven(DeviceInfo devInfo) {
        super(devInfo);
    }

    @Override
    public void onPolling() {
        try {
            Msg reqMsg = newReqMsg(MsgKeys.GetSteamOvenStatus_Req);
            reqMsg.putOpt(MsgParams.TerminalType, terminalType);   // 控制端类型区分
            sendMsg(reqMsg, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged() {
        if (Plat.LOG_FILE_ENABLE) {
            LogUtils.logFIleWithTime(String.format("SteamOven onStatusChanged. isConnected:%s level:%s", isConnected, status));
        }

        postEvent(new SteamOvenStatusChangedEvent(AbsSteamoven.this));
    }

    @Override
    public void onReceivedMsg(Msg msg) {
        super.onReceivedMsg(msg);

        try {
            int key = msg.getID();
            Log.e("key",String.valueOf(key));
            switch (key) {
                case MsgKeys.SteamOven_Noti:
                    // TODO 处理事件

                    short eventId = (short) msg.optInt(MsgParams.EventId);
                    short eventParam = (short) msg.optInt(MsgParams.EventParam);

                    switch (eventId) {
                        case Event_Steam_Power:
                            postEvent(new SteamPowerEvent(AbsSteamoven.this, 1 == eventParam));
                            break;
                        case Event_Steam_Time_Reset:
                            postEvent(new SteamTimeResetEvent(AbsSteamoven.this, eventParam));
                            break;
                        case Event_Steam_Temp_Reset:
                            postEvent(new SteamTempResetEvent(AbsSteamoven.this, eventParam));
                            break;
                        case Event_Steam_Cookbook_Reset:
                            postEvent(new SteamCookbookResetEvent(AbsSteamoven.this, eventParam));
                            break;
                    }

                    break;
                case MsgKeys.SteamOvenAlarm_Noti:
                    short alarmId = (short) msg.optInt(MsgParams.AlarmId);
                    postEvent(new SteamAlarmEvent(this, alarmId));
                case MsgKeys.GetSteamOvenStatus_Rep:
                    AbsSteamoven.this.status = (short) msg.optInt(MsgParams.SteamStatus);
                    AbsSteamoven.this.mode = (short) msg.optInt(MsgParams.SteamMode);
                    AbsSteamoven.this.alarm = (short) msg.optInt(MsgParams.SteamAlarm);
                    AbsSteamoven.this.doorState = (short) msg.optInt(MsgParams.SteamDoorState);
                    AbsSteamoven.this.temp = (short) msg.optInt(MsgParams.SteamTemp);
                    AbsSteamoven.this.time = (short) msg.optInt(MsgParams.SteamTime);
                    AbsSteamoven.this.tempSet = (short) msg.optInt(MsgParams.SteamTempSet);
                    AbsSteamoven.this.timeSet = (short) msg.optInt(MsgParams.SteamTimeSet);
                    onStatusChanged();

                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------------
    // ISteamOven
    // -------------------------------------------------------------------------------


    @Override
    public void pause() {

    }

    @Override
    public void restore() {

    }

    @Override
    public String getSteamovenModel() {
        return null;
    }

    @Override
    public void setSteamWorkTime(final short time, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setSteamTime_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteamTime, time);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSteamoven.this.time = (short)(((short) 60) * time);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSteamWorkTemp(final short temp, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setSteamTemp_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteamTemp, temp);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSteamoven.this.temp = temp;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSteamWorkMode(final short cookbook, final short temp, final short time, short preFlag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setSteamMode_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteamTemp, temp);
            msg.putOpt(MsgParams.SteamTime, time);
            msg.putOpt(MsgParams.SteamMode, cookbook);
            msg.putOpt(MsgParams.PreFlag, 0);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSteamoven.this.time = (short)(((short) 60) * time);
                    AbsSteamoven.this.temp = temp;
                    AbsSteamoven.this.mode = cookbook;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSteamProMode(final short time, final short temp, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setSteamProMode_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteamTemp, temp);
            msg.putOpt(MsgParams.SteamTime, time);
            msg.putOpt(MsgParams.PreFlag, 0);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSteamoven.this.time = (short)(((short) 60) * time);
                    AbsSteamoven.this.temp = temp;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getSteamStatus(VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.GetSteamOvenStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSteamoven.this.status = (short) resMsg.optInt(MsgParams.SteamStatus);
                    AbsSteamoven.this.mode = (short) resMsg.optInt(MsgParams.SteamMode);
                    AbsSteamoven.this.alarm = (short) resMsg.optInt(MsgParams.SteamAlarm);
                    AbsSteamoven.this.temp = (short) resMsg.optInt(MsgParams.SteamTemp);
                    AbsSteamoven.this.time = (short) resMsg.optInt(MsgParams.SteamTime);
                    AbsSteamoven.this.doorState = (short) resMsg.optInt(MsgParams.SteamDoorState);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSteamStatus(final short status, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setSteamStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteamStatus, status);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSteamoven.this.status = status;
//                    AbsSteamoven.this.tempSet = (short) resMsg.optInt(MsgParams.SteamTempSet);
//                    AbsSteamoven.this.timeSet = (short) resMsg.optInt(MsgParams.SteamTimeSet);
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
