package com.robam.roki;

import com.legent.VoidCallback2;
import com.legent.plat.Plat;
import com.legent.utils.sharesdk.ShareSdkOAuthService;
import com.robam.common.RobamApp;
import com.robam.common.io.device.RokiDeviceFactory;
import com.robam.common.io.device.RokiMsgMarshaller;
import com.robam.common.io.device.RokiMsgSyncDecider;
import com.robam.common.io.device.RokiNoticeReceiver;
import com.robam.common.services.NotifyService;
import com.robam.roki.service.AppService;
import com.robam.roki.service.MobNotifyService;

public class MobApp extends RobamApp {

    static public final String APP_TYPE = "RKDRD";

    @Override
    public NotifyService getNotifyService() {
        return MobNotifyService.getInstance();
    }

    @Override
    protected void initPlat() {
        super.initPlat();

        Plat.appOAuthService = new ShareSdkOAuthService();
        Plat.init(this, APP_TYPE,
                new RokiDeviceFactory(),
                new RokiMsgMarshaller(),
                new RokiMsgSyncDecider(),
                new RokiNoticeReceiver(),
                new VoidCallback2() {
                    @Override
                    public void onCompleted() {
                        AppService.getInstance().init(MobApp.this);
                    }
                });

    }
}
