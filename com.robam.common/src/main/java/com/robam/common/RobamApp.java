package com.robam.common;

import com.legent.plat.Plat;
import com.legent.plat.PlatApp;
import com.legent.ui.UIService;
import com.legent.utils.LogUtils;
import com.legent.utils.api.ResourcesUtils;
import com.robam.common.pojos.dictionary.AppExtendDic;
import com.robam.common.services.AdvertManager;
import com.robam.common.services.NotifyService;
import com.robam.common.services.StoreService;

abstract public class RobamApp extends PlatApp {

    protected static final String SERVICE_HOST = "112.124.101.32";

    abstract public NotifyService getNotifyService();

    @Override
    protected void init() {
        super.init();

        initPlat();

        AppExtendDic.init(this);
        StoreService.getInstance().init(this);
        AdvertManager.getInstance().init(this);
        NotifyService.getInstance().init(this);

        startUI();
    }

    protected void initPlat() {
        Plat.serverOpt.set(getServiceHost());
        Plat.commander.setAsyncLogEnable(true);// 1104 zhaiyuanyi
        Plat.commander.setSyncLogEnable(true);

        Plat.LOG_FILE_ENABLE = false;
        if (Plat.LOG_FILE_ENABLE) {
            LogUtils.logFIleWithTime("\n\n");
            LogUtils.logFIleWithTime("=============================================");
            LogUtils.logFIleWithTime("App 启动");
            LogUtils.logFIleWithTime("=============================================\n\n");
        }
    }

    protected void startUI() {
        String uiConfig = ResourcesUtils.raw2String(R.raw.ui);
        UIService.getInstance().loadConfig(uiConfig);
    }

    protected String getServiceHost() {
        return SERVICE_HOST;
    }

}
