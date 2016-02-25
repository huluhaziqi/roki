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
					break;
				case MsgKeys.GetSteriStatus_Req:
					break;
				case MsgKeys.GetSteriPVConfig_Req:
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
					break;
				case MsgKeys.SteriEvent_Noti:

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
}
