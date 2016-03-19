package com.legent.plat.io.device.msg;

import com.legent.io.msgs.AbsMsg;
import com.legent.plat.Plat;
import com.legent.plat.io.device.mqtt.IMqttMsg;
import com.legent.plat.io.device.mqtt.Topic;
import com.legent.plat.pojos.device.DeviceGuid;

public class Msg extends AbsMsg implements IMqttMsg {

	protected DeviceGuid source, target;
	protected boolean incoming;

	private Msg(int msgKey) {
		this.msgKey = msgKey;
	}

	@Override
	public String getTopic() {
		return getTag();
	}

	/**
	 * 获取消息的方向
	 * 
	 * @return true-收到的消息 false-发出去的消息
	 */
	public boolean isIncoming() {
		return incoming;
	}

	public DeviceGuid getSource() {
		return source;
	}

	public DeviceGuid getTarget() {
		return target;
	}

	public DeviceGuid getDeviceGuid() {
		return incoming ? source : target;
	}

    /**
     * 构造一个待发送的消息
     */
	static public Msg newRequestMsg(int msgKey, String targetGuid) {
		Msg msg = new Msg(msgKey);
		msg.incoming = false;
		msg.source = getAppGuid();
		msg.target = DeviceGuid.newGuid(targetGuid);
		msg.setTag(Topic.newUnicastTopic(targetGuid).toString());
		return msg;
	}

	/**
	 * 构造一个接收的消息
	 */
	static public Msg newIncomingMsg(int msgKey, String sourceGuid) {
		Msg msg = new Msg(msgKey);
		msg.incoming = true;
		msg.source = DeviceGuid.newGuid(sourceGuid);
		msg.target = getAppGuid();
		msg.setTag(Topic.newUnicastTopic(sourceGuid).toString());
		return msg;
	}

	static DeviceGuid getAppGuid() {
		return DeviceGuid.newGuid(Plat.appGuid);
	}
}
