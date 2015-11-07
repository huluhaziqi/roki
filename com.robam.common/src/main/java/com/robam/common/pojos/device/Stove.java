package com.robam.common.pojos.device;

import com.google.common.base.Preconditions;
import com.legent.VoidCallback;
import com.legent.plat.io.RCMsgCallbackWithVoid;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.plat.pojos.device.SubDeviceInfo;
import com.robam.common.events.StoveAlarmEvent;
import com.robam.common.events.StoveLevelEvent;
import com.robam.common.events.StovePowerEvent;
import com.robam.common.events.StoveStatusChangedEvent;
import com.robam.common.events.StoveTempEvent;
import com.robam.common.io.device.MsgKeys;
import com.robam.common.io.device.MsgParams;
import com.robam.common.io.device.TerminalType;
import com.robam.common.services.StoveAlarmManager;

import java.util.List;

public class Stove extends AbsDevice implements IStove {

    static final public short PowerLevel_0 = 0;
    static final public short PowerLevel_1 = 1;
    static final public short PowerLevel_2 = 2;
    static final public short PowerLevel_3 = 3;
    static final public short PowerLevel_4 = 4;
    static final public short PowerLevel_5 = 5;
    static final public short PowerLevel_6 = 6;
    static final public short PowerLevel_7 = 7;
    static final public short PowerLevel_8 = 8;
    static final public short PowerLevel_9 = 9;

    static final public short Event_Power = 11;
    static final public short Event_Level = 12;

    public StoveHead leftHead, rightHead;
    public boolean isLock = false;
    protected short terminalType = TerminalType.getType();


    public Stove(SubDeviceInfo devInfo) {
        super(devInfo);
        leftHead = new StoveHead(StoveHead.LEFT_ID);
        leftHead.parent = this;

        rightHead = new StoveHead(StoveHead.RIGHT_ID);
        rightHead.parent = this;
    }

    public StoveHead getHeadById(int headId) {
        return headId == StoveHead.LEFT_ID ? leftHead : rightHead;
    }

    // -------------------------------------------------------------------------------
    // IDevice
    // -------------------------------------------------------------------------------

    @Override
    public void onPolling() {
        try {
            Msg reqMsg = newReqMsg(MsgKeys.GetStoveStatus_Req);
            reqMsg.putOpt(MsgParams.TerminalType, terminalType);
            sendMsg(reqMsg, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onReceivedMsg(Msg msg) {
        super.onReceivedMsg(msg);

        int key = msg.getID();
        switch (key) {
            case MsgKeys.StoveAlarm_Noti:
                short ihId = (short) msg.optInt(MsgParams.IhId);
                short alarmId = (short) msg.optInt(MsgParams.AlarmId);
                postEvent(new StoveAlarmEvent(this, getHeadById(ihId), alarmId));
                break;
            case MsgKeys.StoveEvent_Noti:
                short eventId = (short) msg.optInt(MsgParams.EventId);
                short eventParam = (short) msg.optInt(MsgParams.EventParam);

                switch (eventId) {
                    case Event_Power:
                        postEvent(new StovePowerEvent(this, 1 == eventParam));
                        break;
                    case Event_Level:
                        postEvent(new StoveLevelEvent(this, eventParam));
                        break;
                }

                break;
//            //温度事件上报处理
//            case MsgKeys.StoveTemp_Noti:
//                short temp1 = (short) msg.optInt(MsgParams.Temp_Param1);
//                short temp2 = (short) msg.optInt(MsgParams.Temp_Param2);
//                postEvent(new StoveTempEvent(this,temp1,temp2));
//
//                break;
            case MsgKeys.GetStoveStatus_Rep:
                Stove.this.isLock = msg.optBoolean(MsgParams.StoveLock);
                List<StoveHead> heads = (List<StoveHead>) msg.opt(MsgParams.StoveHeadList);
                if (heads != null) {
                    onGetHeads(heads);
                }
                break;

            default:
                break;
        }
    }

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    @Override
    public void pause() {
        leftHead.pause();
        rightHead.pause();
    }

    @Override
    public void restore() {
        leftHead.restore();
        rightHead.restore();
    }

    @Override
    public void getStoveStatus(VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.GetStoveStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {

                    Stove.this.isLock = resMsg.optBoolean(MsgParams.StoveLock);
                    List<StoveHead> heads = (List<StoveHead>) resMsg.opt(MsgParams.StoveHeadList);
                    if (heads != null) {
                        onGetHeads(heads);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setStoveStatus(boolean isCook, short ihId,
                               final short ihStatus, VoidCallback callback) {
        checkHeadId(ihId);

        try {
            Msg msg = newReqMsg(MsgKeys.SetStoveStatus_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.IsCook, isCook);
            msg.putOpt(MsgParams.IhId, ihId);
            msg.putOpt(MsgParams.IhStatus, ihStatus);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    short id = (short)resMsg.optInt(MsgParams.IhId);
                    StoveHead head = getHeadById(id);
                    head.status = ihStatus;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setStoveLevel(boolean isCook, short ihId, final short ihLevel,
                              VoidCallback callback) {
        checkHeadId(ihId);


        //若灶具已经关机,先开机，再设置档位
        if (ihLevel > Stove.PowerLevel_0) {
            StoveHead stoveHead = getHeadById(ihId);
            if (stoveHead.status != StoveStatus.Working
                    || stoveHead.level == Stove.PowerLevel_0) {
                setStoveStatus(isCook, ihId, StoveStatus.Working, null);
            }
        }

        try {
            Msg msg = newReqMsg(MsgKeys.SetStoveLevel_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.UserId, getSrcUser());
            msg.putOpt(MsgParams.IsCook, isCook);
            msg.putOpt(MsgParams.IhId, ihId);
            msg.putOpt(MsgParams.IhLevel, ihLevel);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    short id = (short)resMsg.optInt(MsgParams.IhId);
                    StoveHead head = getHeadById(id);
                    head.level = ihLevel;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStoveShutdown(short ihId, final short ihTime,
                                 VoidCallback callback) {
        checkHeadId(ihId);

        try {
            Msg msg = newReqMsg(MsgKeys.SetStoveShutdown_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.IhId, ihId);
            msg.putOpt(MsgParams.IhTime, ihTime);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    short id = (short)resMsg.optInt(MsgParams.IhId);
                    StoveHead head = getHeadById(id);
                    head.time = ihTime;

                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStoveLock(final boolean isLock, VoidCallback callback) {
        try {
            Msg msg = newReqMsg(MsgKeys.SetStoveLock_Req);
            msg.putOpt(MsgParams.TerminalType, terminalType);
            msg.putOpt(MsgParams.StoveLock, isLock);

            sendMsg(msg, new RCMsgCallbackWithVoid(callback) {
                protected void afterSuccess(Msg resMsg) {
                    Stove.this.isLock = isLock;
                    onStatusChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------------
    // protected
    // -------------------------------------------------------------------------------

    @Override
    protected void initStatus() {
        super.initStatus();
        isLock = false;
        if (leftHead != null) {
            leftHead.initStatus();
        }
        if (rightHead != null) {
            rightHead.initStatus();
        }
    }

    @Override
    public void onStatusChanged() {
        postEvent(new StoveStatusChangedEvent(this));
    }

    // -------------------------------------------------------------------------------
    // private
    // -------------------------------------------------------------------------------

    private void onGetHeads(List<StoveHead> heads) {
        if (heads == null)
            return;

        if (heads.size() >= 1) {
            leftHead.updateStatus(heads.get(0));
        }
        if (heads.size() >= 2) {
            rightHead.updateStatus(heads.get(1));
        }

        onStatusChanged();
    }

    private void checkHeadId(short headId) {
        Preconditions.checkArgument(headId == StoveHead.LEFT_ID
                || headId == StoveHead.RIGHT_ID);
    }

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    static public class StoveHead {

        final static public short LEFT_ID = 0;
        final static public short RIGHT_ID = 1;

        public Stove parent;
        public short ihId;
        public short status;
        public short level;
        public short time;
        public short alarmId;

        private short savedLevel;

        public StoveHead(short id) {
            ihId = id;
            initStatus();
        }

        public void updateStatus(StoveHead head) {
            this.status = head.status;
            this.level = head.level;
            this.time = head.time;
            this.alarmId = head.alarmId;
        }

        public boolean isLeft() {
            return ihId == LEFT_ID;
        }

        public boolean isRight() {
            return ihId == RIGHT_ID;
        }

        void initStatus() {
            status = StoveStatus.Off;
            level = 0;
            time = 0;
            alarmId = StoveAlarmManager.getInstance().getDefault().getID();
        }

        public void pause() {
            savedLevel = (status == StoveStatus.StandyBy ? PowerLevel_0 : level);
            parent.setStoveLevel(true, ihId, PowerLevel_0, null);
        }

        public void restore() {
            parent.setStoveLevel(true, ihId, savedLevel, null);
            savedLevel = PowerLevel_0;
        }

        @Override
        public String toString() {
            return String.format(
                    "{id=%s,status=%s,level=%s,time=%s,alarmId=%s}", ihId,
                    status, level, time, alarmId);
        }
    }

}
