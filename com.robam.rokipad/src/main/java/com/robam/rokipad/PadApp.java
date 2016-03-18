package com.robam.rokipad;

import com.legent.VoidCallback2;
import com.legent.plat.Plat;
import com.legent.plat.io.device.mqtt.MqttChannel;
import com.robam.common.RobamApp;
import com.robam.common.io.device.RokiDeviceFactory;
import com.robam.common.io.device.RokiMsgMarshaller;
import com.robam.common.io.device.RokiMsgSyncDecider;
import com.robam.common.io.device.RokiNoticeReceiver;
import com.robam.common.services.NotifyService;
import com.robam.rokipad.io.SerialChannel;
import com.robam.rokipad.service.AppService;
import com.robam.rokipad.service.PadNotifyService;

public class PadApp extends RobamApp {

    static public final String APP_TYPE = "RKPAD";

    @Override
    public NotifyService getNotifyService() {
        return PadNotifyService.getInstance();
    }

    @Override
    protected void initPlat() {
        super.initPlat();
        Plat.init(this, APP_TYPE,
                new RokiDeviceFactory(),
                new RokiMsgMarshaller(),
                new RokiMsgSyncDecider(),
                new RokiNoticeReceiver(),
                MqttChannel.getInstance(),
                new SerialChannel(),
                new VoidCallback2() {
                    @Override
                    public void onCompleted() {
                        AppService.getInstance().init(PadApp.this);
                    }
                });


    }
}
