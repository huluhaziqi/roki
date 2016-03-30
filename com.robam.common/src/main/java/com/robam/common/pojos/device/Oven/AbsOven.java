package com.robam.common.pojos.device.Oven;

import android.util.Log;

import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.io.RCMsgCallbackWithVoid;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.plat.pojos.device.AbsDeviceHub;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.utils.LogUtils;
import com.robam.common.events.OvenAlarmEvent;
import com.robam.common.events.OvenLightResetEvent;
import com.robam.common.events.OvenRunModeResetEvent;
import com.robam.common.events.OvenSpitRotateResetEvent;
import com.robam.common.events.OvenStatusChangedEvent;
import com.robam.common.events.OvenSwitchControlResetEvent;
import com.robam.common.events.OvenTempResetEvent;
import com.robam.common.events.OvenTimeResetEvent;
import com.robam.common.io.device.MsgKeys;
import com.robam.common.io.device.MsgParams;
import com.robam.common.io.device.TerminalType;

/**
 * Created by linxiaobin on 2015/12/27.
 */
public class AbsOven extends AbsDevice implements IOven {

//    10	烤箱开关控制事件
//    11	烤箱烧烤运行模式调整件
//    12	烤箱烤叉旋转事件调整
//    13	烤箱灯光调整事件
//    14	烤箱运行温度调整事件
//    15	烤箱运行时间调整事件

    static final public short Event_Oven_Switch_Control_Reset = 10;
    static final public short Event_Oven_Run_Mode_Reset = 11;
    static final public short Event_Oven_Spit_Rotate_Reset = 12;
    static final public short Event_Oven_Light_Reset = 13;
    static final public short Event_Oven_Temp_Reset = 14;
    static final public short Event_Oven_Time_Reset = 15;

    static final public short Event_Oven_Alarm_ok = 0xff;
    static final public short Event_Oven_Alarm_Senor_Short = 00;
    static final public short Event_Oven_Alarm_Senor_Open = 01;

    public short status;
    public short runP;
    public short alarm = 0 ;
    public short temp; // 当前温度
    public short time; // 当前剩余时
    public short light;//灯光控制
    public short revolve;//烤叉旋转

    public short setTemp;//
    public short setTime;//

    protected short terminalType = TerminalType.getType();

    public AbsOven(DeviceInfo devInfo) {
        super(devInfo);
    }

    @Override
    public void onPolling() {
        try {
            Msg reqMsg = newReqMsg(MsgKeys.getOvenStatus_Req);
            reqMsg.putOpt(MsgParams.TerminalType, terminalType);   // 控制端类型区分
            sendMsg(reqMsg, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged() {
        if (Plat.LOG_FILE_ENABLE) {
            LogUtils.logFIleWithTime(String.format("Oven onStatusChanged. isConnected:%s level:%s", isConnected, status));
        }

        postEvent(new OvenStatusChangedEvent(AbsOven.this));
    }

    @Override
    public void onReceivedMsg(Msg msg) {
        super.onReceivedMsg(msg);

        try {
            int key = msg.getID();
            Log.e("key",String.valueOf(key));
            switch (key) {
                case MsgKeys.Oven_Noti:
                    // TODO 处理事件
                    short eventId = (short) msg.optInt(MsgParams.EventId);
                    short eventParam = (short) msg.optInt(MsgParams.EventParam);
                    Log.e("eventId",String.valueOf(eventId));

                    switch (eventId) {
                        case Event_Oven_Switch_Control_Reset:
                            postEvent(new OvenSwitchControlResetEvent(AbsOven.this, 1 == eventParam));
                            break;
                        case Event_Oven_Run_Mode_Reset:
                            postEvent(new OvenRunModeResetEvent(AbsOven.this, eventParam));
                            break;
                        case Event_Oven_Spit_Rotate_Reset:
                            postEvent(new OvenSpitRotateResetEvent(AbsOven.this, eventParam));
                            break;
                        case Event_Oven_Light_Reset:
                            postEvent(new OvenLightResetEvent(AbsOven.this, eventParam));
                        case Event_Oven_Temp_Reset:
                            postEvent(new OvenTempResetEvent(AbsOven.this, eventParam));
                            break;
                        case Event_Oven_Time_Reset:
                            postEvent(new OvenTimeResetEvent(AbsOven.this, eventParam));
                            break;
                    }

                    break;
                case MsgKeys.OvenAlarm_Noti:
                    short alarmId= (short) msg.optInt(MsgParams.AlarmId);
                    postEvent(new OvenAlarmEvent(AbsOven.this, alarmId));
                case MsgKeys.getOvenStatus_Rep:
                    AbsOven.this.status = (short) msg.optInt(MsgParams.OvenStatus);
                    AbsOven.this.runP = (short) msg.optInt(MsgParams.OvenRunP);
                    AbsOven.this.alarm = (short) msg.optInt(MsgParams.OvenAlarm);
                    AbsOven.this.revolve = (short) msg.optInt(MsgParams.OvenRevolve);
                    AbsOven.this.temp = (short) msg.optInt(MsgParams.OvenTemp);
                    AbsOven.this.light = (short) msg.optInt(MsgParams.OvenLight);
                    AbsOven.this.time = (short) msg.optInt(MsgParams.OvenTime);
                    AbsOven.this.setTemp = (short)msg.optInt(MsgParams.OvenSetTemp);
                    AbsOven.this.setTime = (short)msg.optInt(MsgParams.OvenSetTime);
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
    public String getOvenModel() {
        return null;
    }

    public void setOvenWorkTime(final short time, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenStatusControl_Req);//待修改
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTime, time);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.time = time;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenWorkTemp(final short temp, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenStatusControl_Req);////待修改
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, temp);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = temp;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenStatusControl(final short status, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenStatusControl_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenStatus, status);
            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.status = status;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenQuickHeating(final short setTime, final short setTemp,short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenQuickHeat_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, setTemp);
            msg.putOpt(MsgParams.OvenTime, setTime);
            msg.putOpt(MsgParams.OvenPreFlag, preflag);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = setTemp;
                    AbsOven.this.time = (short)(setTime*60);
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenAirBaking(final short time, final short temp, short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenAirBaking_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, temp);
            msg.putOpt(MsgParams.OvenTime, time);
            msg.putOpt(MsgParams.OvenPreFlag, 0);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = temp;
                    AbsOven.this.time = (short)(time*60);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenToast(final short time, final short temp, short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenToast_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, temp);
            msg.putOpt(MsgParams.OvenTime, time);
            msg.putOpt(MsgParams.OvenPreFlag, 0);


            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = temp;
                    AbsOven.this.time = (short)(time*60);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenBottomHeating(final short setTime, final short setTemp, short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenBottomHeat_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, setTemp);
            msg.putOpt(MsgParams.OvenTime, setTime);
            msg.putOpt(MsgParams.OvenPreFlag, 0);


            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = setTemp;
                    AbsOven.this.time = (short)(setTime*60);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenUnfreeze(final short setTime, final short setTemp, short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenUnfreeze_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, setTemp);
            msg.putOpt(MsgParams.OvenTime, setTime);
            msg.putOpt(MsgParams.OvenPreFlag, 0);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = setTemp;
                    AbsOven.this.time = (short)(setTime*60);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenAirBarbecue(final short setTime, final short setTemp, short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenAirBarbecue_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, setTemp);
            msg.putOpt(MsgParams.OvenTime, setTime);
            msg.putOpt(MsgParams.OvenPreFlag, 0);


            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = setTemp;
                    AbsOven.this.time = (short)(setTime*60);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenBarbecue(final short setTime, final short setTemp, short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenBarbecue_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, setTemp);
            msg.putOpt(MsgParams.OvenTime, setTime);
            msg.putOpt(MsgParams.OvenPreFlag, 0);


            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = setTemp;
                    AbsOven.this.time = (short)(setTime*60);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenStrongBarbecue(final short setTime, final short setTemp, short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenStrongBarbecue_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenTemp, setTemp);
            msg.putOpt(MsgParams.OvenTime, setTime);
            msg.putOpt(MsgParams.OvenPreFlag, 0);


            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.temp = setTemp;
                    AbsOven.this.time = (short)(setTime*60);
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenSpitRotateLightControl(final short revolve, final short light, short preflag, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenSpitRotateLightControl_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenRevolve, revolve);
            msg.putOpt(MsgParams.OvenLight, light);
            msg.putOpt(MsgParams.OvenPreFlag, 0);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.revolve = revolve;
                    AbsOven.this.light = light;
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOvenStatus(final short status, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setOvenStatusControl_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.OvenStatus, status);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.status = status;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getOvenStatus(VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.getOvenStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsOven.this.status = (short) resMsg.optInt(MsgParams.OvenStatus);
                    AbsOven.this.runP = (short) resMsg.optInt(MsgParams.OvenMode);
                    AbsOven.this.alarm = (short) resMsg.optInt(MsgParams.OvenAlarm);
                    AbsOven.this.temp = (short) resMsg.optInt(MsgParams.OvenTemp);
                    AbsOven.this.time = (short) resMsg.optInt(MsgParams.OvenTime);
                    AbsOven.this.light = (short) resMsg.optInt(MsgParams.OvenLight);
                    AbsOven.this.revolve = (short) resMsg.optInt(MsgParams.OvenRevolve);
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}