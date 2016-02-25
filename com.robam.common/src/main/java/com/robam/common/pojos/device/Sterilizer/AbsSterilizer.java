package com.robam.common.pojos.device.Sterilizer;

import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.io.RCMsgCallbackWithVoid;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.pojos.device.AbsDeviceHub;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.utils.LogUtils;
import com.robam.common.events.SteriAlarmEvent;
import com.robam.common.events.SteriStatusChangedEvent;
import com.robam.common.io.device.MsgKeys;
import com.robam.common.io.device.MsgParams;
import com.robam.common.io.device.TerminalType;

/**
 * Created by zhaiyuanyi on 15/11/19.
 */
abstract public class AbsSterilizer extends AbsDeviceHub implements ISterilizer {

    public short status;
    public boolean isChildLock;
    public boolean isDoorLock;
    public short AlarmStautus;
    public short SteriReserveTime;
    public short SteriDrying;
    public  short SteriCleanTime;
    public short SteriDisinfectTime,work_left_time_l,work_left_time_h;
    public short temp,hum,germ,ozone;
    public boolean SwitchDisinfect,SwitchWeekDisinfect;
    public short InternalDisinfect,WeekInternalDisinfect,PVDisinfectTime;

    protected short terminalType = TerminalType.getType();

    public AbsSterilizer(DeviceInfo devInfo) {
        super(devInfo);
    }


    // -------------------------------------------------------------------------------
    // IDevice
    // -------------------------------------------------------------------------------
    @Override
    public void onPolling(){
     try {

         Msg reqMsg = newReqMsg(MsgKeys.GetSteriStatus_Req);
         reqMsg.putOpt(MsgParams.TerminalType, terminalType);   // 控制端类型区分
         sendMsg(reqMsg, null);

     }catch (Exception e){
         e.printStackTrace();
     }
    }

    @Override
    public void onStatusChanged(){
        if (Plat.LOG_FILE_ENABLE){
            LogUtils.logFIleWithTime(String.format("Sterilizer onStatusChanged. isConnected:%s;work_left_time_l:%s,", isConnected,work_left_time_l));
        }
        postEvent(new SteriStatusChangedEvent(AbsSterilizer.this));
    }

    @Override
    public void onReceivedMsg(Msg msg){
        super.onReceivedMsg(msg);
        try {
            int key = msg.getID();
            switch (key) {
                case MsgKeys.SteriAlarm_Noti:
                    short alarmId = (short) msg.optInt(MsgParams.AlarmId);
                    postEvent(new SteriAlarmEvent(this,alarmId));
                    break;
                case MsgKeys.GetSteriStatus_Rep:
                    AbsSterilizer.this.status = (short) msg.optInt(MsgParams.SteriStatus);
                    AbsSterilizer.this.isChildLock = (boolean) msg.optBoolean(MsgParams.SteriLock);
                    AbsSterilizer.this.work_left_time_l = (short) msg.optInt(MsgParams.SteriWorkLeftTimeL);
                    AbsSterilizer.this.work_left_time_h = (short) msg.optInt(MsgParams.SteriWorkLeftTimeH);
                    AbsSterilizer.this.AlarmStautus = (short) msg.optInt(MsgParams.SteriAlarmStatus);

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
    // ISterilizer
    // -------------------------------------------------------------------------------
    @Override
    public void pause() {

    }

    @Override
    public void restore() {

    }

    @Override
    public String getSterilizerModel() {
        return null;
    }

    @Override
    public void getSteriStatus(VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.GetSteriStatus_Req);
            msg.put(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.status = (short) resMsg.optInt(MsgParams.SteriStatus);
                    AbsSterilizer.this.isChildLock = (boolean) resMsg.optBoolean(MsgParams.SteriLock);
                    AbsSterilizer.this.work_left_time_l = (short) resMsg.optInt(MsgParams.SteriWorkLeftTimeL);
                    AbsSterilizer.this.work_left_time_h = (short) resMsg.optInt(MsgParams.SteriWorkLeftTimeH);
                    AbsSterilizer.this.AlarmStautus = (short) resMsg.optInt(MsgParams.SteriAlarmStatus);
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSteriPower(final short status, final VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetSteriPowerOnOff_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteriStatus, status);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.status = status;
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SetSteriReserveTime(final short SteriReserveTime, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetSteriReserveTime_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteriReserveTime, SteriReserveTime);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.SteriReserveTime = SteriReserveTime;
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setSteriDrying(final short SteriDrying, VoidCallback voidCallback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetSteriDisinfect_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteriDryingTime, SteriDrying);

            sendMsg(msg, new RCMsgCallbackWithVoid(voidCallback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.SteriDrying = SteriDrying;
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setSteriClean(final short SteriCleanTime, VoidCallback voidCallback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetSteriClean_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteriCleanTime, SteriCleanTime);

            sendMsg(msg, new RCMsgCallbackWithVoid(voidCallback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.SteriCleanTime = SteriCleanTime;
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setSteriDisinfect(final short SteriDisinfectTime, VoidCallback voidCallback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetSteriDisinfect_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteriDisinfectTime, SteriDisinfectTime);

            sendMsg(msg, new RCMsgCallbackWithVoid(voidCallback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.SteriDisinfectTime = SteriDisinfectTime;
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setSteriLock(final boolean isChildLock, VoidCallback voidCallback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetSteriLock_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.SteriLock, isChildLock);

            sendMsg(msg, new RCMsgCallbackWithVoid(voidCallback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.isChildLock = isChildLock;
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void querySteriParm(VoidCallback voidCallback) {
        try {
            Msg msg = newReqMsg(MsgKeys.GetSteriParam_Req);
            msg.put(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(voidCallback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.temp = (short) resMsg.optInt(MsgParams.SteriParaTem);
                    AbsSterilizer.this.hum = (short) resMsg.optInt(MsgParams.SteriParaHum);
                    AbsSterilizer.this.germ = (short) resMsg.optInt(MsgParams.SteriParaGerm);
                    AbsSterilizer.this.ozone = (short) resMsg.optInt(MsgParams.SteriParaOzone);
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getSteriPVConfig(VoidCallback voidCallback) {
        try {
            Msg msg = newReqMsg(MsgKeys.GetSteriPVConfig_Req);
            msg.put(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(voidCallback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.SwitchDisinfect = (boolean) resMsg.optBoolean(MsgParams.SteriSwitchDisinfect);
                    AbsSterilizer.this.InternalDisinfect = (short) resMsg.optInt(MsgParams.SteriInternalDisinfect);
                    AbsSterilizer.this.SwitchWeekDisinfect = (boolean) resMsg.optBoolean(MsgParams.SteriSwitchWeekDisinfect);
                    AbsSterilizer.this.WeekInternalDisinfect = (short) resMsg.optInt(MsgParams.SteriWeekInternalDisinfect);
                    AbsSterilizer.this.PVDisinfectTime = (short) resMsg.optInt(MsgParams.SteriPVDisinfectTime);
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setSteriPVConfig(short SteriSwitchDisinfect, short SteriInternalDisinfect,
                                 short SteriSwitchWeekDisinfect, short SteriWeekInternalDisinfect, short SteriPVDisinfectTime, VoidCallback voidCallback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetSteriPVConfig_Req);
            msg.put(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(voidCallback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.SwitchDisinfect = (boolean) resMsg.optBoolean(MsgParams.SteriSwitchDisinfect);
                    AbsSterilizer.this.InternalDisinfect = (short) resMsg.optInt(MsgParams.SteriInternalDisinfect);
                    AbsSterilizer.this.SwitchWeekDisinfect = (boolean) resMsg.optBoolean(MsgParams.SteriSwitchWeekDisinfect);
                    AbsSterilizer.this.InternalDisinfect = (short) resMsg.optInt(MsgParams.SteriWeekInternalDisinfect);
                    AbsSterilizer.this.PVDisinfectTime = (short) resMsg.optInt(MsgParams.SteriPVDisinfectTime);
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
