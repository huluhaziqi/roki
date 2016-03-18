package com.robam.rokipad.service;

import android.content.Context;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.plat.pojos.device.DeviceGroupInfo;
import com.legent.plat.pojos.device.DeviceGuid;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.plat.pojos.device.IDevice;
import com.legent.plat.services.DeviceService;
import com.legent.services.AbsService;
import com.legent.services.TaskService;
import com.robam.common.Utils;
import com.robam.common.io.device.RokiDeviceFactory;
import com.robam.common.pojos.device.fan.AbsFan;

import java.util.List;

/**
 * Created by sylar on 15/7/24.
 */
public class AppService extends AbsService {

    private static AppService instance = new AppService();

    synchronized public static AppService getInstance() {
        return instance;
    }

    long userId;
    DeviceService ds;

    private AppService() {
    }

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);

        ds = Plat.deviceService;
        if (Plat.accountService.isLogon()) {
            onLogin();
        }
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
        onLogin();
        onBindOwner();
    }

    @Subscribe
    public void onEvent(UserLogoutEvent event) {
        userId = 0;
        ds.clear();
    }

    // -------------------------------------------------------------------------------
    // private
    // -------------------------------------------------------------------------------


    private void onProbeGuid() {

        Plat.dcSerial.getDevice(DeviceGuid.ZeroGuid, new Callback<DeviceInfo>() {

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

        Plat.dcSerial.setOwnerId(fan.getID(), userId, null);
        Plat.deviceService.bindDevice(userId, fan.getID(), fan.getName(), true, null);

    }

    private void onLogin() {
        ds.clear();
        onLoadGroup();
        onLoadDevice();
    }

    private void onLoadGroup() {

        ds.getDeviceGroups(userId, new Callback<List<DeviceGroupInfo>>() {

            @Override
            public void onSuccess(List<DeviceGroupInfo> groups) {
                if (groups != null) {
                    ds.batchAddGroup(groups);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void onLoadDevice() {

        ds.getDevices(userId, new Callback<List<DeviceInfo>>() {

            @Override
            public void onSuccess(List<DeviceInfo> result) {
                if (result != null) {
                    List<IDevice> devices = Lists.newArrayList();
                    IDevice defaultFan = null;

                    for (DeviceInfo deviceInfo : result) {
                        boolean isFan = Utils.isFan(deviceInfo.guid);
                        if (isFan && defaultFan != null) {
                            continue;
                        }

                        IDevice dev = RokiDeviceFactory.generateModel(deviceInfo);
                        devices.add(dev);

                        if (isFan) {
                            defaultFan = dev;
                        }
                    }
                    ds.batchAdd(devices);

                    if (defaultFan != null) {
                        ds.setDefault(defaultFan);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }

}
