package com.legent.plat.pojos.device;

import android.content.Context;

import com.google.common.base.Strings;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.plat.events.DeviceStatusChangedEvent;
import com.legent.plat.io.device.DeviceCommander;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.io.device.msg.MsgCallback;
import com.legent.plat.pojos.dictionary.DeviceType;
import com.legent.plat.services.DeviceTypeManager;
import com.legent.pojos.AbsKeyPojo;
import com.legent.utils.EventUtils;
import com.legent.utils.LogUtils;


abstract public class AbsDevice extends AbsKeyPojo<String> implements IDevice {

    protected final static int MAX_ERROR_COUNT = 4;

    protected String id, bid, name;
    protected int ver;
    protected boolean isConnected;
    protected boolean valid;
    protected int errorCountOnCheck;
    protected IDevice parent;
    protected DeviceCommander dc = Plat.commander;

    public AbsDevice(SubDeviceInfo devInfo) {
        this.valid = true;
        this.id = devInfo.guid;
        this.bid = devInfo.bid;
        this.name = devInfo.name;
        this.ver = (short) devInfo.ver;

        if (Strings.isNullOrEmpty(this.name)) {
            this.name = DeviceTypeManager.getInstance().getDeviceType(id).getName();
        }

        initStatus();
    }

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        dc.initIO(id);
    }

    @Override
    public void dispose() {
        super.dispose();
        dc.disposeIO(id);
    }

    // -------------------------------------------------------------------------------
    // IDevice
    // -------------------------------------------------------------------------------

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DeviceGuid getGuid() {
        return DeviceGuid.newGuid(id);
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceTypeManager.getInstance().getDeviceType(id);
    }

    @Override
    public String getBid() {
        return bid;
    }

    @Override
    public int getVersion() {
        return ver;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void setConnected(boolean connected) {
        errorCountOnCheck = connected ? 0 : errorCountOnCheck;
        if (isConnected == connected)
            return;

        isConnected = connected;
        if (!isConnected) {
            initStatus();
        }

        postEvent(new DeviceConnectionChangedEvent(this, isConnected));

        if (Plat.LOG_FILE_ENABLE) {
            LogUtils.logFIleWithTime(String.format("post DeviceConnectionChangedEvent:%s", isConnected));
        }
    }


    @Override
    public boolean getValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public IDevice getParent() {
        return parent;
    }

    @Override
    public void setParent(IDevice parent) {
        this.parent = parent;
    }

    @Override
    public void onPolling() {
    }

    @Override
    public void onCheckConnection() {
        if (isConnected) {
            errorCountOnCheck++;
            if (errorCountOnCheck >= MAX_ERROR_COUNT) {
                setConnected(false);
            }
        }
    }

    @Override
    public void onReceivedMsg(Msg msg) {
        setConnected(true);
    }

    @Override
    public void onStatusChanged() {
        postEvent(new DeviceStatusChangedEvent(this));
    }

    // -------------------------------------------------------------------------------
    // protected
    // -------------------------------------------------------------------------------

    /**
     * 设备状态参数重置
     */
    protected void initStatus() {
    }


    final protected Msg newReqMsg(short msgKey) {
        Msg msg = Msg.newRequestMsg(msgKey, id);
        return msg;
    }

    final protected void sendMsg(Msg reqMsg, MsgCallback callback) {
        dc.asyncSend(id, reqMsg, callback);
    }

    final protected String getSrcUser() {
        long id = getCurrentUserId();
        String userId = String.valueOf(id);

        userId = Strings.padEnd(userId, 10, '\0');
        return userId;

    }

    final protected long getCurrentUserId() {
        return Plat.accountService.getCurrentUserId();
    }

    final protected void postEvent(Object event) {
        EventUtils.postEvent(event);
    }

    // -------------------------------------------------------------------------------
    // private
    // -------------------------------------------------------------------------------

}
