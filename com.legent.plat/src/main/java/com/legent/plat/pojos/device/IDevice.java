package com.legent.plat.pojos.device;


import com.legent.IDispose;
import com.legent.IKeyName;
import com.legent.Initialization;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.pojos.dictionary.DeviceType;

/**
 * 设备对象模型
 * Created by sylar on 15/7/22.
 */
public interface IDevice extends IKeyName<String>,Initialization, IDispose {

    /**
     * 获取设备唯一标识对象
     */
    DeviceGuid getGuid();

    /**
     * 获取设备类型
     */
    DeviceType getDeviceType();

    /**
     * 业务编码（厂商定制）
     */
    String getBid();

    /**
     * 固件版本
     */
    int getVersion();

    /**
     * 获取设备连接状态
     */
    boolean isConnected();

    /**
     * 设置设备连接状态
     */
    void setConnected(boolean isConnected);

    /**
     * 获取设备有效性
     */
    boolean getValid();

    /**
     * 设置有效性
     */
    void setValid(boolean valid);

    /**
     * 获取父级
     */
    IDevice getParent();

    /**
     * 设置父级
     */
    void setParent(IDevice parent);

    /**
     * 当轮询设备时
     */
    void onPolling();

    /**
     * 当检查链路时
     */
    void onCheckConnection();

    /**
     * 当设备状态变更时
     */
    void onStatusChanged();

    /**
     * 当收到通知消息或非同步消息时
     */
    void onReceivedMsg(Msg msg);
}
