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
    SteamOvenDecider steamOvenDecider = new SteamOvenDecider();
    MicroWaveDecider microWaveDecider = new MicroWaveDecider();
    OvenDecider ovenDecider = new OvenDecider();

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
        } else if (Utils.isSterilizer(devGuid)){
            res = steriDecider.getPairsKey(msg);
        } else if (Utils.isSteam(devGuid)) {
            res = steamOvenDecider.getPairsKey(msg);
        } else if (Utils.isMicroWave(devGuid)) {
            res = microWaveDecider.getPairsKey(msg);
        } else if (Utils.isOven(devGuid)) {
            res = ovenDecider.getPairsKey(msg);
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
            addPairsKey(MsgKeys.GetSteriPVConfig_Req,MsgKeys.GetSteriPVConfig_Rep);
            addPairsKey(MsgKeys.SetSteriPVConfig_Req,MsgKeys.SetSteriPVConfig_Rep);

        }
    }

    class SteamOvenDecider extends AbsMsgSyncDecider {
        @Override
        protected void initBiMap() {
            addPairsKey(MsgKeys.setSteamTemp_Req, MsgKeys.setSteamTemp_Rep);
            addPairsKey(MsgKeys.setSteamMode_Req, MsgKeys.setSteamMode_Rep);
            addPairsKey(MsgKeys.setSteamProMode_Req, MsgKeys.setSteamProMode_Rep);
            addPairsKey(MsgKeys.GetSteamOvenStatus_Req, MsgKeys.GetSteamOvenStatus_Rep);
            addPairsKey(MsgKeys.setSteamStatus_Req, MsgKeys.setSteamStatus_Rep);
            addPairsKey(MsgKeys.setSteamTime_Req, MsgKeys.setSteamTime_Rep);
        }
    }

    class MicroWaveDecider extends AbsMsgSyncDecider {
        @Override
        protected void initBiMap() {
            addPairsKey(MsgKeys.setMicroWaveStatus_Req, MsgKeys.setMicroWaveStates_Rep);
            addPairsKey(MsgKeys.setMicroWaveKindsAndHeatCold_Req, MsgKeys.setMicroWaveKindsAndHeatCold_Rep);
            addPairsKey(MsgKeys.setMicroWaveProModeHeat_Req, MsgKeys.setMicroWaveProModeHeat_Rep);
            addPairsKey(MsgKeys.setMicroWaveLight_Req, MsgKeys.setMicroWaveLight_Rep);
            addPairsKey(MsgKeys.getMicroWaveStatus_Req, MsgKeys.getMicroWaveStatus_Rep);
        }
    }

    class OvenDecider extends AbsMsgSyncDecider {
        @Override
        protected void initBiMap() {
            addPairsKey(MsgKeys.setOvenStatusControl_Req, MsgKeys.setOvenStatusControl_Rep);
            addPairsKey(MsgKeys.setOvenQuickHeat_Req, MsgKeys.setOvenQuickHeat_Rep);
            addPairsKey(MsgKeys.setOvenAirBaking_Req, MsgKeys.setOvenAirBaking_Rep);
            addPairsKey(MsgKeys.setOvenAirBarbecue_Req, MsgKeys.setOvenAirBarbecue_Rep);
            addPairsKey(MsgKeys.setOvenBarbecue_Req, MsgKeys.setOvenBarbecue_Rep);
            addPairsKey(MsgKeys.setOvenBottomHeat_Req, MsgKeys.setOvenBottomHeat_Rep);
            addPairsKey(MsgKeys.setOvenSpitRotateLightControl_Req, MsgKeys.setOvenSpitRotateLightControl_Rep);
            addPairsKey(MsgKeys.setOvenStrongBarbecue_Req, MsgKeys.setOvenStrongBarbecue_Rep);
            addPairsKey(MsgKeys.setOvenBottomHeat_Req, MsgKeys.setOvenBottomHeat_Rep);
            addPairsKey(MsgKeys.setOvenUnfreeze_Req, MsgKeys.setOvenUnfreeze_Rep);
            addPairsKey(MsgKeys.setOvenToast_Req, MsgKeys.setOvenToast_Rep);
            addPairsKey(MsgKeys.getOvenStatus_Req, MsgKeys.getOvenStatus_Rep);
        }
    }
}
