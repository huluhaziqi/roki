package com.robam.rokipad.service;

import android.content.Context;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.plat.pojos.device.DeviceGuid;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.plat.pojos.device.IDevice;
import com.legent.services.AbsService;
import com.legent.services.TaskService;
import com.robam.common.Utils;
import com.robam.common.io.device.RokiDeviceFactory;
import com.robam.common.pojos.device.fan.AbsFan;

/**
 * Created by sylar on 15/7/24.
 */
public class AppService extends AbsService {

    private static AppService instance = new AppService();

    synchronized public static AppService getInstance() {
        return instance;
    }

    long userId;

    private AppService() {
    }

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);

        Plat.deviceService.setPolltingPeriod(2000, 2000);
        onProbeGuid();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Subscribe
    public void onEvent(UserLoginEvent event) {
        userId = event.pojo.id;
        onBindOwner();
    }

    @Subscribe
    public void onEvent(UserLogoutEvent event) {
        userId = 0;
    }

    // -------------------------------------------------------------------------------
    // private
    // -------------------------------------------------------------------------------


    private void onProbeGuid() {

        Plat.commander.getDevice(DeviceGuid.ZeroGuid, new Callback<DeviceInfo>() {

            @Override
            public void onSuccess(DeviceInfo devInfo) {
//                Fan9700 fan = new Fan9700(devInfo);
                IDevice dev = RokiDeviceFactory.generateModel(devInfo);
                Plat.deviceService.add(dev);
                onBindOwner();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();

                TaskService.getInstance().postUiTask(new Runnable() {
                    public void run() {
                        onProbeGuid();
                    }
                }, 1000);
            }
        });

    }

    private void onBindOwner() {

        if (!Plat.accountService.isLogon())
            return;

        AbsFan fan = Utils.getDefaultFan();
        if (fan == null)
            return;

        Plat.commander.setOwnerId(fan.getID(), userId, null);
        Plat.deviceService.bindDevice(userId, fan.getID(), fan.getName(), true, null);

    }

}
