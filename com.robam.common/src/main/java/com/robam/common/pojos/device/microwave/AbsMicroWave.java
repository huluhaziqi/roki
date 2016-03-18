package com.robam.common.pojos.device.microwave;

import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.io.RCMsgCallbackWithVoid;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.utils.LogUtils;
import com.robam.common.events.MicroWaveHeadModeEvent;
import com.robam.common.events.MicroWavePowerEvent;
import com.robam.common.events.MicroWaveStatusChangedEvent;
import com.robam.common.events.MicroWaveSwitchEvent;
import com.robam.common.events.MicroWaveTimeEvent;
import com.robam.common.events.MicroWaveWeightEvent;
import com.robam.common.io.device.MsgKeys;
import com.robam.common.io.device.MsgParams;
import com.robam.common.io.device.TerminalType;

/**
 * Created by WZTCM on 2015/12/17.
 */
public class AbsMicroWave extends AbsDevice implements iMicroWave {

    public static final short Event_MicroWave_Switch = 10;   //微波炉运行状态调整事件
    public static final short Event_MicroWave_HeatMode = 11; //微波炉加热模式调整事件（参考状态查询）
    public static final short Event_MicroWave_Weight = 12;   //微波炉重量调整事件
    public static final short Event_MicroWave_Power = 13;    //微波炉功率调整事件
    public static final short Event_MicroWave_Time = 14;     //微波率加热时间调整事件

    short state; // 开关状态
    short light; // 照明灯状态
    short mode;  // 模式
    short power; // 功率
    int   weight;// 重量
    int time;  // 工作时间
    short doorState;// 门阀状态

    protected short terminalType = TerminalType.getType();

    public AbsMicroWave(DeviceInfo devInfo) {
        super(devInfo);
    }

    @Override
    public void onPolling() {
        try {
            Msg reqMsg = newReqMsg(MsgKeys.getMicroWaveStatus_Req);
            reqMsg.putOpt(MsgParams.TerminalType, terminalType);   // 控制端类型区分
            sendMsg(reqMsg, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged() {
        super.onStatusChanged();
        if (Plat.LOG_FILE_ENABLE) {
            LogUtils.logFIleWithTime(String.format("MicroWave onStatusChanged. isConnected:%s level:%s", isConnected, state));
        }

        postEvent(new MicroWaveStatusChangedEvent(AbsMicroWave.this));
    }

    @Override
    public void onReceivedMsg(Msg msg) {
        super.onReceivedMsg(msg);

        try {
            int key = msg.getID();
            switch (key) {
                case MsgKeys.MicroWave_Noti:
                    short eventId = (short) msg.optInt(MsgParams.EventId);
                    short eventParam = (short) msg.optInt(MsgParams.EventParam);

                    switch (eventId) {
                        case Event_MicroWave_Switch:
                            postEvent(new MicroWaveSwitchEvent(AbsMicroWave.this, eventParam));
                            break;
                        case Event_MicroWave_HeatMode:
                            postEvent(new MicroWaveHeadModeEvent(AbsMicroWave.this, eventParam));
                            break;
                        case Event_MicroWave_Weight:
                            postEvent(new MicroWaveWeightEvent(AbsMicroWave.this, eventParam));
                            break;
                        case Event_MicroWave_Power:
                            postEvent(new MicroWavePowerEvent(AbsMicroWave.this, eventParam));
                            break;
                        case Event_MicroWave_Time:
                            postEvent(new MicroWaveTimeEvent(AbsMicroWave.this, eventParam));
                            break;
                        default:
                            break;
                    }

                    break;

                case MsgKeys.getMicroWaveStatus_Rep:
                    this.state = (short) msg.optInt(MsgParams.MicroWaveStatus);
                    this.light = (short) msg.optInt(MsgParams.MicroWaveLight);
                    this.mode = (short) msg.optInt(MsgParams.MicroWaveMode);
                    this.power= (short) msg.optInt(MsgParams.MicroWavePower);
                    this.weight = msg.optInt(MsgParams.MicroWaveWeight);
                    this.time = msg.optInt(MsgParams.MicroWaveTime);
                    this.doorState = (short) msg.optInt(MsgParams.MicroWaveDoorState);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------- iMicroWave -----------------------------------------


    @Override
    public void pause() {

    }

    @Override
    public void restore() {

    }

    @Override
    public String getMicroWaveMode() {
        return null;
    }

    @Override
    public void setMicroWaveState(final short status, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setMicroWaveStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.MicroWaveStatus, status);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsMicroWave.this.state = status;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMicroWaveKindsAndHeatCold(final short mode, final int weight, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setMicroWaveStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.MicroWaveMode, mode);
            msg.putOpt(MsgParams.MicroWaveWeight, weight);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsMicroWave.this.mode = mode;
                    AbsMicroWave.this.weight = weight;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMicroWaveProModeHeat(final short mode, final int time, final short power, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setMicroWaveStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.MicroWaveMode, mode);
            msg.putOpt(MsgParams.MicroWaveTime, time);
            msg.putOpt(MsgParams.MicroWavePower, power);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsMicroWave.this.mode = mode;
                    AbsMicroWave.this.time = time;
                    AbsMicroWave.this.power = power;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMicroWaveLight(final short state, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.setMicroWaveStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.MicroWaveLight, state);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    AbsMicroWave.this.light = state;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getMicroWaveStatus(VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.RestFanNetBoard_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
