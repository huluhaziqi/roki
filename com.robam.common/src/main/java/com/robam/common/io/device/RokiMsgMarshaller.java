package com.robam.common.io.device;

import com.google.common.collect.Lists;
import com.legent.plat.io.device.AbsPlatProtocol;
import com.legent.plat.io.device.IAppMsgMarshaller;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.io.device.msg.MsgUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove.Stove.StoveHead;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class RokiMsgMarshaller implements IAppMsgMarshaller {

	static public final int BufferSize = AbsPlatProtocol.BufferSize;
	static public final ByteOrder BYTE_ORDER = AbsPlatProtocol.BYTE_ORDER;

	@Override
	public byte[] marshal(Msg msg)throws Exception {

		boolean bool;
		byte b;
		String str;
		short s;

		boolean isStoveMsg = isStoveMsg(msg);
		boolean isFanMsg = isFanMsg(msg);
		boolean isSterilizerMsg = isSterilizer(msg);
		int key = msg.getID();

		ByteBuffer buf = ByteBuffer.allocate(BufferSize).order(BYTE_ORDER);

		if (isStoveMsg) {
			// 电磁灶
			switch (key) {

			case MsgKeys.GetStoveStatus_Req:
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(b);

				break;
			case MsgKeys.SetStoveStatus_Req:
				//
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(b);
				//
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				//
				bool = msg.optBoolean(MsgParams.IsCook);
				buf.put(bool ? (byte) 1 : (byte) 0);
				//
				b = (byte)msg.optInt(MsgParams.IhId);
				buf.put(b);
				//
				b = (byte)msg.optInt(MsgParams.IhStatus);
				buf.put(b);

				break;
			case MsgKeys.SetStoveLevel_Req:
				//
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(b);
				//
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				//
				bool = msg.optBoolean(MsgParams.IsCook);
				buf.put(bool ? (byte) 1 : (byte) 0);
				//
				b = (byte)msg.optInt(MsgParams.IhId);
				buf.put(b);
				//
				b = (byte)msg.optInt(MsgParams.IhLevel);
				buf.put(b);
				break;
			case MsgKeys.SetStoveShutdown_Req:
				//
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(b);

				//
				b = (byte)msg.optInt(MsgParams.IhId);
				buf.put(b);

				//
				s = (short)msg.optInt(MsgParams.IhTime);
				buf.putShort(s);
				break;
			case MsgKeys.SetStoveLock_Req:
				//
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(b);

				//
				bool = msg.optBoolean(MsgParams.StoveLock);
				buf.put(bool ? (byte) 1 : (byte) 0);

				break;

			default:
				break;
			}
		} else if (isFanMsg){
			// 油烟机

			switch (key) {

			case MsgKeys.GetFanStatus_Req:
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(b);
				break;
			case MsgKeys.SetFanStatus_Req:
				//
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));
				//
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				//
				b = (byte)msg.optInt(MsgParams.FanStatus);
				buf.put(b);

				break;
			case MsgKeys.SetFanLevel_Req:
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));
				//
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				//
				b = (byte)msg.optInt(MsgParams.FanLevel);
				buf.put(b);
				break;
			case MsgKeys.SetFanLight_Req:
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));

				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				//
				bool = msg.optBoolean(MsgParams.FanLight);
				buf.put(bool ? (byte) 1 : (byte) 0);
				break;
			case MsgKeys.SetFanAllParams_Req:
				//
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));
				//
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				//
				b = (byte)msg.optInt(MsgParams.FanLevel);
				buf.put(b);
				//
				bool = msg.optBoolean(MsgParams.FanLight);
				buf.put(bool ? (byte) 1 : (byte) 0);
				break;
			case MsgKeys.RestFanCleanTime_Req:
				//
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));
				//
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				//
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				break;
			case MsgKeys.RestFanNetBoard_Req:
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));
				break;
			case MsgKeys.SetFanTimeWork_Req:
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));
				//增加 userid
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());

				b = (byte)msg.optInt(MsgParams.FanLevel);
				buf.put(b);
				//
				b = (byte)msg.optInt(MsgParams.FanTime);
				buf.put(b);
				break;
			case MsgKeys.GetSmartConfig_Req:
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));
				break;
			case MsgKeys.SetSmartConfig_Req:
				b = (byte)msg.optInt(MsgParams.TerminalType);
				buf.put(MsgUtils.toByte(b));
				//
				str = msg.optString(MsgParams.UserId);
				buf.put(str.getBytes());
				//
				bool = msg.optBoolean(MsgParams.IsPowerLinkage);
				buf.put(bool ? (byte) 1 : (byte) 0);
				//
				bool = msg.optBoolean(MsgParams.IsLevelLinkage) ;
				buf.put(bool ? (byte) 1 : (byte) 0);
				//
				bool = msg.optBoolean(MsgParams.IsShutdownLinkage) ;
				buf.put(bool ? (byte) 1 : (byte) 0);
				//
				b = (byte)msg.optInt(MsgParams.ShutdownDelay);
				buf.put(b);
				//
				bool = msg.optBoolean(MsgParams.IsNoticClean) ;
				buf.put(bool ? (byte) 1 : (byte) 0);
				//
				bool = msg.optBoolean(MsgParams.IsTimingVentilation) ;
				buf.put(bool ? (byte) 1 : (byte) 0);
				//
				b = (byte)msg.optInt(MsgParams.TimingVentilationPeriod);
				buf.put(b);
				//
				bool = msg.optBoolean(MsgParams.IsWeeklyVentilation) ;
				buf.put(bool ? (byte) 1 : (byte) 0);
				//
				b = (byte)msg.optInt(MsgParams.WeeklyVentilationDate_Week);
				buf.put(b);
				//
				b = (byte)msg.optInt(MsgParams.WeeklyVentilationDate_Hour);
				buf.put(b);
				//
				b = (byte)msg.optInt(MsgParams.WeeklyVentilationDate_Minute);
				buf.put(b);
				break;

			default:
				break;
			}

		}else if (isSterilizerMsg){
			switch (key){
				case MsgKeys.SetSteriPowerOnOff_Req:
					//
//					b = (byte)msg.optInt(MsgParams.TerminalType);
//					buf.put(MsgUtils.toByte(b));
					//
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					//
					b = (byte)msg.optInt(MsgParams.SteriStatus);
					buf.put(b);
					break;
				case MsgKeys.SetSteriReserveTime_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte)msg.optInt(MsgParams.SteriReserveTime);
					buf.put(b);
					break;
				case MsgKeys.SetSteriDrying_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte)msg.optInt(MsgParams.SteriDryingTime);
					buf.put(b);
					break;
				case MsgKeys.SetSteriClean_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte)msg.optInt(MsgParams.SteriCleanTime);
					buf.put(b);
					break;
				case MsgKeys.SetSteriDisinfect_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte)msg.optInt(MsgParams.SteriDisinfectTime);
					buf.put(b);
					break;
				case MsgKeys.SetSteriLock_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte)msg.optInt(MsgParams.SteriLock);
					buf.put(b);
					break;
				case MsgKeys.GetSteriParam_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					break;
				case MsgKeys.GetSteriStatus_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					break;
				case MsgKeys.GetSteriPVConfig_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					break;
				case MsgKeys.SetSteriPVConfig_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte)msg.optInt(MsgParams.SteriSwitchDisinfect);
					buf.put(b);
					b = (byte)msg.optInt(MsgParams.SteriInternalDisinfect);
					buf.put(b);
					b = (byte)msg.optInt(MsgParams.SteriSwitchWeekDisinfect);
					buf.put(b);
					b = (byte)msg.optInt(MsgParams.SteriWeekInternalDisinfect);
					buf.put(b);
					b = (byte)msg.optInt(MsgParams.SteriPVDisinfectTime);
					buf.put(b);
					break;
				default:
					break;
			}
		} else if (isSteamMsg(msg)) {
			switch (key) {
				case MsgKeys.setSteamTime_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.SteamTime);
					buf.put(b);
					break;
				case MsgKeys.setSteamTemp_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.SteamTemp);
					buf.put(b);
					break;
				case MsgKeys.setSteamMode_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.SteamMode);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.SteamTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.SteamTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.PreFlag);
					buf.put(b);
					break;
				case MsgKeys.setSteamProMode_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.SteamTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.SteamTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.PreFlag);
					buf.put(b);
					break;
				case MsgKeys.GetSteamOvenStatus_Req:
					break;
				case MsgKeys.setSteamStatus_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.SteamStatus);
					buf.put(b);
					break;
			}
		} else if (isMicroWaveMsg(msg)) {
			switch (key) {
				case MsgKeys.setMicroWaveStatus_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.MicroWaveStatus);
					buf.put(b);
					break;
				case MsgKeys.setMicroWaveKindsAndHeatCold_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.MicroWaveMode);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.MicroWaveWeight);
					buf.put(b);
					break;
				case MsgKeys.setMicroWaveProModeHeat_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.MicroWaveMode);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.MicroWaveTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.MicroWavePower);
					buf.put(b);
					break;
				case MsgKeys.setMicroWaveLight_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.MicroWaveLight);
					buf.put(b);
					break;
				case MsgKeys.getMicroWaveStatus_Req:
					break;
			}
		} else if (isOvenMsg(msg)) {
			switch (key) {
				case MsgKeys.setOvenStatusControl_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.OvenStatus);
					buf.put(b);
					break;
				case MsgKeys.setOvenQuickHeat_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());

					b = (byte) msg.optInt(MsgParams.OvenTemp);
					buf.put(b);

					b = (byte) msg.optInt(MsgParams.OvenTime);
					buf.put(b);

					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.setOvenAirBaking_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte) msg.optInt(MsgParams.OvenTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.setOvenToast_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte) msg.optInt(MsgParams.OvenTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.setOvenBottomHeat_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte) msg.optInt(MsgParams.OvenTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.setOvenUnfreeze_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte) msg.optInt(MsgParams.OvenTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.setOvenAirBarbecue_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte) msg.optInt(MsgParams.OvenTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.setOvenBarbecue_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte) msg.optInt(MsgParams.OvenTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.setOvenStrongBarbecue_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte) msg.optInt(MsgParams.OvenTemp);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenTime);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.setOvenSpitRotateLightControl_Req:
					str = msg.optString(MsgParams.UserId);
					buf.put(str.getBytes());
					b = (byte) msg.optInt(MsgParams.OvenRevolve);
					buf.put(b);

					b = (byte) msg.optInt(MsgParams.OvenLight);
					buf.put(b);
					b = (byte) msg.optInt(MsgParams.OvenPreFlag);
					buf.put(b);
					break;
				case MsgKeys.getOvenStatus_Req:
					break;
				default:
					break;
			}
		}

		//
		byte[] data = new byte[buf.position()];
		System.arraycopy(buf.array(), 0, data, 0, data.length);
		buf.clear();
		buf = null;
		return data;
	}

	@Override
	public void unmarshal(Msg msg, byte[] payload)throws Exception {

		boolean isStoveMsg = isStoveMsg(msg);
		int key = msg.getID();
		int offset = 0;


		if (isStoveMsg) {
			// 电磁灶
			switch (key) {
			case MsgKeys.GetStoveStatus_Rep:
				int num = MsgUtils.getShort(payload[offset++]);
				msg.putOpt(MsgParams.IhNum, num);
				msg.putOpt(MsgParams.StoveLock,
						MsgUtils.getShort(payload[offset++]) == 1);

				List<StoveHead> list = Lists.newArrayList();
				for (short i = 0; i < num; i++) {
					StoveHead head = new StoveHead(i);
					head.status = MsgUtils.getShort(payload[offset++]);
					head.level = MsgUtils.getShort(payload[offset++]);
					head.time = MsgUtils.getShort(payload, offset);
					offset += 2;
					head.alarmId = MsgUtils.getShort(payload[offset++]);

					list.add(head);
				}

				msg.putOpt(MsgParams.StoveHeadList, list);
				break;
			case MsgKeys.SetStoveStatus_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.IhId,
						MsgUtils.getShort(payload[offset++]));

				break;
			case MsgKeys.SetStoveLevel_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.IhId,
						MsgUtils.getShort(payload[offset++]));

				break;
			case MsgKeys.SetStoveShutdown_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.IhId,
						MsgUtils.getShort(payload[offset++]));

				break;
			case MsgKeys.SetStoveLock_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;

			// -------------------------------------------------------------------------------
			// 通知类
			// -------------------------------------------------------------------------------

			case MsgKeys.StoveAlarm_Noti:
				msg.putOpt(MsgParams.IhId,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.AlarmId,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.StoveEvent_Noti:
				msg.putOpt(MsgParams.IhId,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.EventId,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.EventParam,
						MsgUtils.getShort(payload[offset++]));
				break;
//			case MsgKeys.StoveTemp_Noti:        //灶具温度参数上报
//				msg.putOpt(MsgParams.Temp_Param1,
//						MsgUtils.getShort(payload[offset++]));
//				msg.putOpt(MsgParams.Temp_Param2,
//						MsgUtils.getShort(payload[offset++]));
//				break;


			default:
				break;
			}
		} else if (isFanMsg(msg)){
			// 油烟机

			switch (key) {			// 8700/9700为通用烟机
			case MsgKeys.GetFanStatus_Rep:
				msg.putOpt(MsgParams.FanStatus,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.FanLevel,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.FanLight,
						MsgUtils.getShort(payload[offset++]) == 1);
				msg.putOpt(MsgParams.NeedClean,
						MsgUtils.getShort(payload[offset++]) == 1);
				if (Utils.whichFan(msg.getDeviceGuid().getGuid())==2){			//烟机8229

				msg.putOpt(MsgParams.FanTime,
						MsgUtils.getShort(payload[offset++]));  //增加定时时间参数读取 byzhaiyuanyi
				}
				break;
			case MsgKeys.SetFanStatus_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.SetFanLevel_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.SetFanLight_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.SetFanAllParams_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.RestFanCleanTime_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.RestFanNetBoard_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.SetFanTimeWork_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.GetSmartConfig_Rep:
				msg.putOpt(MsgParams.IsPowerLinkage,
						1 == MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.IsLevelLinkage,
						1 == MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.IsShutdownLinkage,
						1 == MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.ShutdownDelay,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.IsNoticClean,
						1 == MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.IsTimingVentilation,
						1 == MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.TimingVentilationPeriod,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.IsWeeklyVentilation,
						1 == MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.WeeklyVentilationDate_Week,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.WeeklyVentilationDate_Hour,
						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.WeeklyVentilationDate_Minute,
						MsgUtils.getShort(payload[offset++]));
				break;
			case MsgKeys.SetSmartConfig_Rep:
				msg.putOpt(MsgParams.RC,
						MsgUtils.getShort(payload[offset++]));
				break;

			// -------------------------------------------------------------------------------
			// 通知类
			// -------------------------------------------------------------------------------

			case MsgKeys.FanEvent_Noti:
				msg.putOpt(MsgParams.EventId,
						MsgUtils.getShort(payload[offset++]));
//				msg.putOpt(MsgParams.TerminalType,
//						MsgUtils.getShort(payload[offset++]));
				msg.putOpt(MsgParams.EventParam,
						MsgUtils.getShort(payload[offset++]));
				break;

			default:
				break;
			}

		}else if (isSterilizer(msg)){
			switch (key){
				case MsgKeys.SetSteriPowerOnOff_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SetSteriReserveTime_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SetSteriDrying_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SetSteriClean_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SetSteriDisinfect_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SetSteriLock_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.GetSteriParam_Rep:
					msg.putOpt(MsgParams.SteriParaTem,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriParaHum,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriParaGerm,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriParaOzone,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.GetSteriStatus_Rep:
					msg.putOpt(MsgParams.SteriStatus,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriLock,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriWorkLeftTimeL,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriWorkLeftTimeH,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriAlarmStatus,
						MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.GetSteriPVConfig_Rep:
					msg.putOpt(MsgParams.SteriSwitchDisinfect,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriInternalDisinfect,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriSwitchWeekDisinfect,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriWeekInternalDisinfect,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteriPVDisinfectTime,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SetSteriPVConfig_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				// -------------------------------------------------------------------------------
				// 通知类
				// -------------------------------------------------------------------------------
				case MsgKeys.SteriAlarm_Noti:
					msg.putOpt(MsgParams.SteriAlarmStatus,MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SteriEvent_Noti:
					msg.putOpt(MsgParams.EventId,MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.EventParam,MsgUtils.getShort(payload[offset++]));
					break;
			}

		} else if (isOvenMsg(msg)) {
			switch (key) {
				case MsgKeys.setOvenStatusControl_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenQuickHeat_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenAirBaking_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenAirBarbecue_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenToast_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenBottomHeat_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenUnfreeze_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenStrongBarbecue_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenBarbecue_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setOvenSpitRotateLightControl_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.getOvenStatus_Rep:
					msg.putOpt(MsgParams.OvenStatus,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.OvenAlarm,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.OvenRunP,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.OvenTemp,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.OvenRevolve,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.OvenTime,
							MsgUtils.getShort(payload, offset++));
					offset++;
					msg.putOpt(MsgParams.OvenLight,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.OvenSetTemp,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.OvenSetTime,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.OvenAlarm_Noti:
					msg.putOpt(MsgParams.AlarmId,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.Oven_Noti:
					msg.putOpt(MsgParams.EventId,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.EventParam,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.UserId,
							MsgUtils.getString(payload, offset++, 10));
					break;
			}
		} else if (isSteamMsg(msg)) {
			switch (key) {
				case MsgKeys.setSteamTime_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setSteamTemp_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setSteamMode_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setSteamProMode_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.GetSteamOvenStatus_Rep:
					msg.putOpt(MsgParams.SteamLock,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteamStatus,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteamAlarm,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteamMode,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteamTemp,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteamTime, MsgUtils.getShort(payload, offset++));
					offset++;
					msg.putOpt(MsgParams.SteamDoorState,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteamTempSet,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.SteamTimeSet,
							MsgUtils.getShort(payload[offset++]));

					break;
				case MsgKeys.setSteamStatus_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SteamOvenAlarm_Noti:
					msg.putOpt(MsgParams.AlarmId,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.SteamOven_Noti:
					msg.putOpt(MsgParams.EventId,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.EventParam,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.UserId,
							MsgUtils.getString(payload, offset++, 10));
					break;
			}
		} else if (isMicroWaveMsg(msg)) {
			switch (key) {
				case MsgKeys.setMicroWaveStates_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setMicroWaveKindsAndHeatCold_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setMicroWaveProModeHeat_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.setMicroWaveLight_Rep:
					msg.putOpt(MsgParams.RC,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.getMicroWaveStatus_Rep:
					msg.putOpt(MsgParams.MicroWaveStatus,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.MicroWaveLight,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.MicroWaveMode,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.MicroWavePower,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.MicroWaveWeight,
							MsgUtils.getInt(payload, offset++));
					offset++;
					msg.putOpt(MsgParams.MicroWaveTime,
							MsgUtils.getInt(payload, offset++));
					offset++;
					msg.putOpt(MsgParams.MicroWaveDoorState,
							MsgUtils.getShort(payload[offset++]));
					break;
				case MsgKeys.MicroWave_Noti:
					msg.putOpt(MsgParams.EventId,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.EventParam,
							MsgUtils.getShort(payload[offset++]));
					msg.putOpt(MsgParams.UserId,
							MsgUtils.getString(payload, offset, 10));
					break;
			}
		}
	}

	private boolean isStoveMsg(Msg msg) {
		return Utils.isStove(msg.getDeviceGuid().getGuid());
	}
	private  boolean isFanMsg(Msg msg){  //判断是否为油烟机 by zhaiyuanyi 20151120
		return Utils.isFan(msg.getDeviceGuid().getGuid());
	}
	private boolean isSterilizer(Msg msg){//判断是否为消毒柜 by zhaiyuanyi 20151120
		return Utils.isSterilizer(msg.getDeviceGuid().getGuid());
	}
	private boolean isSteamMsg(Msg msg) {
		return Utils.isSteam(msg.getDeviceGuid().getGuid());
	}

	private boolean isMicroWaveMsg(Msg msg) {
		return Utils.isMicroWave(msg.getDeviceGuid().getGuid());
	}

	private boolean isOvenMsg(Msg msg) {  //判断是否为油烟机 by linxiaobin 20151220
		return Utils.isOven(msg.getDeviceGuid().getGuid());
	}
}
