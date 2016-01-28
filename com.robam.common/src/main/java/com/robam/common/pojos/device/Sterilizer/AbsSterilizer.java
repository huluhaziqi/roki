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

    static final public short Event_NO_Alarm =0xff;         //无报警
    static final public short Event_Gate_Alarm =0x00;       //门控报警
    static final public short Event_light_Alarm =0x01;      //紫外线灯管不工作或上层传感器不良
    static final public short Event_TempSensor_Alarm =0x02;      //温度传感器不良

    public short status;



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
            LogUtils.logFIleWithTime(String.format("Sterilizer onStatusChanged. isConnected:%s", isConnected));
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
                case MsgKeys.GetFanStatus_Rep:
                    AbsSterilizer.this.status = (short) msg.optInt(MsgParams.SteriStatus);

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
            Msg msg = newReqMsg(MsgKeys.GetFanStatus_Req);
            msg.put(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsSterilizer.this.status = (short) resMsg.optInt(MsgParams.FanStatus);

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSteriStatus(final short status, final VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetFanStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.FanStatus, status);

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

}
