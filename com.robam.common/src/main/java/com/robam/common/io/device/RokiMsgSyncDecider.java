package com.robam.common.io.device;

import com.legent.io.msgs.IMsg;
import com.legent.io.senders.AbsMsgSyncDecider;
import com.legent.plat.io.device.IAppMsgSyncDecider;
import com.legent.plat.io.device.msg.Msg;
import com.robam.common.Utils;

public class RokiMsgSyncDecider implements IAppMsgSyncDecider {


    FanDecider fanDecider = new FanDecider();
    StoveDecider stoveDecider = new StoveDecider();
    SteriDecider steriDecider = new SteriDecider();

    @Override
    public long getSyncTimeout() {
        return 2000;
    }

    @Override
    public int getPairsKey(IMsg msg) {
        int res = 0;
        Msg m = (Msg) msg;
        String devGuid = m.getDeviceGuid().getGuid();
        if (Utils.isFan(devGuid)) {
            res = fanDecider.getPairsKey(msg);
        } else if (Utils.isStove(devGuid)) {
            res = stoveDecider.getPairsKey(msg);
        }else if (Utils.isSterilizer(devGuid)){
            res = steriDecider.getPairsKey(msg);
        }

        return res;
    }

    class FanDecider extends AbsMsgSyncDecider {
        @Override
        protected void initBiMap() {
            addPairsKey(MsgKeys.GetFanStatus_Req, MsgKeys.GetFanStatus_Rep);
            addPairsKey(MsgKeys.SetFanStatus_Req, MsgKeys.SetFanStatus_Rep);
            addPairsKey(MsgKeys.SetFanLevel_Req, MsgKeys.SetFanLevel_Rep);
            addPairsKey(MsgKeys.SetFanLight_Req, MsgKeys.SetFanLight_Rep);
            addPairsKey(MsgKeys.SetFanAllParams_Req, MsgKeys.SetFanAllParams_Rep);
            addPairsKey(MsgKeys.RestFanCleanTime_Req, MsgKeys.RestFanCleanTime_Rep);
            addPairsKey(MsgKeys.RestFanNetBoard_Req, MsgKeys.RestFanNetBoard_Rep);
            addPairsKey(MsgKeys.SetFanTimeWork_Req, MsgKeys.SetFanTimeWork_Rep);
            addPairsKey(MsgKeys.GetSmartConfig_Req, MsgKeys.GetSmartConfig_Rep);
            addPairsKey(MsgKeys.SetSmartConfig_Req, MsgKeys.SetSmartConfig_Rep);
        }
    }

    class StoveDecider extends AbsMsgSyncDecider {
        @Override
        protected void initBiMap() {
            addPairsKey(MsgKeys.GetStoveStatus_Req, MsgKeys.GetStoveStatus_Rep);
            addPairsKey(MsgKeys.SetStoveStatus_Req, MsgKeys.SetStoveStatus_Rep);
            addPairsKey(MsgKeys.SetStoveLevel_Req, MsgKeys.SetStoveLevel_Rep);
            addPairsKey(MsgKeys.SetStoveShutdown_Req, MsgKeys.SetStoveShutdown_Rep);
            addPairsKey(MsgKeys.SetStoveLock_Req, MsgKeys.SetStoveLock_Rep);
        }
    }
    class SteriDecider extends AbsMsgSyncDecider{

        @Override
        protected void initBiMap() {
            addPairsKey(MsgKeys.SetSteriPowerOnOff_Req,MsgKeys.SetSteriPowerOnOff_Rep);
            addPairsKey(MsgKeys.SetSteriReserveTime_Req,MsgKeys.SetSteriReserveTime_Rep);
            addPairsKey(MsgKeys.SetSteriDrying_Req,MsgKeys.SetSteriDrying_Rep);
            addPairsKey(MsgKeys.SetSteriClean_Req,MsgKeys.SetSteriClean_Rep);
            addPairsKey(MsgKeys.SetSteriDisinfect_Req,MsgKeys.SetSteriDisinfect_Rep);
            addPairsKey(MsgKeys.SetSteriLock_Req,MsgKeys.SetSteriLock_Rep);
            addPairsKey(MsgKeys.GetSteriParam_Req,MsgKeys.GetSteriParam_Rep);
            addPairsKey(MsgKeys.GetSteriStatus_Req,MsgKeys.GetSteriStatus_Rep);
        }
    }
}
