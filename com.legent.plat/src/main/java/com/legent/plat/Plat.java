package com.legent.plat;

import android.app.Application;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.legent.Callback2;
import com.legent.ContextIniter;
import com.legent.VoidCallback2;
import com.legent.io.channels.IChannel;
import com.legent.plat.io.cloud.CloudHelper;
import com.legent.plat.io.device.DeviceCommander;
import com.legent.plat.io.device.IAppMsgMarshaller;
import com.legent.plat.io.device.IAppMsgSyncDecider;
import com.legent.plat.io.device.IAppNoticeReceiver;
import com.legent.plat.io.device.mqtt.MqttChannel;
import com.legent.plat.pojos.device.DeviceGuid;
import com.legent.plat.pojos.device.IDeviceFactory;
import com.legent.plat.pojos.dictionary.AppDic;
import com.legent.plat.pojos.dictionary.PlatDic;
import com.legent.plat.pojos.dictionary.ServerOpt;
import com.legent.plat.services.AccountService;
import com.legent.plat.services.CommonService;
import com.legent.plat.services.DeviceService;
import com.legent.plat.services.account.IAppOAuthService;
import com.legent.services.RestfulService;

public class Plat {


    static public boolean LOG_FILE_ENABLE = false;

    static public Application app;
    static public String appType;
    static public String appGuid;

    //    static public IChannel channel;
    static public IDeviceFactory deviceFactory;
    static public IAppMsgMarshaller appMsgMarshaller;
    static public IAppMsgSyncDecider appMsgSyncDecider;
    static public IAppNoticeReceiver appNoticeReceiver;
    static public IAppOAuthService appOAuthService;


    //-------------------------------------------------------------------------------------
    static public CommonService commonService = CommonService.getInstance();
    static public AccountService accountService = AccountService.getInstance();
    static public DeviceService deviceService = DeviceService.getInstance();
    //    static public DeviceCommander commander = DeviceCommander.getInstance();
    static public ServerOpt serverOpt = new ServerOpt();

    static public DeviceCommander dcMqtt, dcSerial;
    //-------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    static public boolean isValidAppGuid() {
        return (!Strings.isNullOrEmpty(Plat.appGuid)
                && !Objects.equal(Plat.appGuid, DeviceGuid.ZeroGuid));
    }


    static public <T> T getCustomApi(Class<T> apiClazz) {
        return CloudHelper.getRestfulApi(apiClazz);
    }


//    static public void init(Application app, int appDicResid, VoidCallback2 callback) {
//        Plat.app = app;
//        ContextIniter.init(app);
//
//        AppDic dic = AppDic.load(app, appDicResid);
//        Plat.appType = dic.commOpt.appType;
//        Plat.serverOpt = dic.serverOpt;
//
//        Plat.channel = dic.getAppChannel();
//        Plat.deviceFactory = dic.getDeviceFactory();
//        Plat.appMsgMarshaller = dic.getAppMsgMarshaller();
//        Plat.appMsgSyncDecider = dic.getAppMsgSyncDecider();
//        Plat.appNoticeReceiver = dic.getAppNoticeReceiver();
//
//        init(callback);
//    }


    // for mobile
    static public void init(Application app, String appType,
                            IDeviceFactory deviceFactory,
                            IAppMsgMarshaller deviceMsgMarshaller,
                            IAppMsgSyncDecider syncDecider,
                            IAppNoticeReceiver noticeReceiver,
                            VoidCallback2 callback) {

        init(app, appType, deviceFactory, deviceMsgMarshaller, syncDecider, noticeReceiver,
                MqttChannel.getInstance(), null, callback);
    }

//    static public void init(Application app,
//                            String appType,
//                            IDeviceFactory deviceFactory,
//                            IAppMsgMarshaller msgMarshaller,
//                            IAppMsgSyncDecider syncDecider,
//                            IAppNoticeReceiver noticeReceiver,
//                            IChannel channel,
//                            VoidCallback2 callback) {
//
//        // -------------------------------------------------------------------------------
//        Plat.app = app;
//        Plat.appType = appType;
//        ContextIniter.init(app);
//        PlatDic.loadPlatDic(app);
//
//        Plat.deviceFactory = deviceFactory;
//        Plat.appMsgMarshaller = msgMarshaller;
//        Plat.appMsgSyncDecider = syncDecider;
//        Plat.appNoticeReceiver = noticeReceiver;
//        Plat.channel = channel != null ? channel : MqttChannel.getInstance();
//
//        init(callback);
//    }

    // for pad
    static public void init(Application app,
                            String appType,
                            IDeviceFactory deviceFactory,
                            IAppMsgMarshaller msgMarshaller,
                            IAppMsgSyncDecider syncDecider,
                            IAppNoticeReceiver noticeReceiver,
                            IChannel chMqtt,
                            IChannel chSerial,
                            VoidCallback2 callback) {
        Plat.app = app;
        Plat.appType = appType;
        ContextIniter.init(app);
        PlatDic.loadPlatDic(app);

        Plat.deviceFactory = deviceFactory;
        Plat.appMsgMarshaller = msgMarshaller;
        Plat.appMsgSyncDecider = syncDecider;
        Plat.appNoticeReceiver = noticeReceiver;

        dcMqtt = new DeviceCommander(chMqtt);
        if (chSerial != null) {
            dcSerial = new DeviceCommander(chSerial);
        }

        init(callback);
    }

    static private void init(final VoidCallback2 callback) {
        RestfulService.getInstance().setDefaultHost(
                serverOpt.getRestfulBaseUrl());

        commonService.getAppGuid(new Callback2<String>() {
            @Override
            public void onCompleted(String guid) {
                Plat.appGuid = guid;
//                commander.init(app);
                if (dcMqtt != null) {
                    dcMqtt.init(app);
                }
                if (dcSerial != null) {
                    dcSerial.init(app);
                }

                commonService.init(app);
                accountService.init(app);
                deviceService.init(app);

                if (callback != null) {
                    callback.onCompleted();
                }
            }
        });
    }

}
