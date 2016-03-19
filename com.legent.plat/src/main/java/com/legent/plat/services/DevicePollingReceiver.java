package com.legent.plat.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.legent.plat.Plat;
import com.legent.plat.pojos.device.IDevice;
import com.legent.plat.pojos.device.IDeviceHub;
import com.legent.utils.LogUtils;
import com.legent.utils.api.AlarmUtils;

import java.util.List;

/**
 * Created by sylar on 15/7/25.
 */
public class DevicePollingReceiver extends BroadcastReceiver {

    static public Intent getIntent(Context cx) {
        Intent intent = new Intent(cx, DevicePollingReceiver.class);
        intent.setAction(DevicePollingReceiver.class.getName());
        return intent;
    }

    @Override
    public void onReceive(Context cx, Intent i) {

        try {
            onPolling();
        } catch (Exception e) {
            LogUtils.logFIleWithTime("轮询出错:" + e.getMessage());
        }

        if (Build.VERSION.SDK_INT >= 19) {
            /** API 19 后的 AlarmManager 不再提供精准闹钟
             *
             * From API level 19, all repeating alarms are inexact—that is, if our application targets KitKat or above, our repeat alarms will be inexact even if we use setRepeating.
             If we really need exact repeat alarms, we can use setExact instead, and schedule the next alarm while handling the current one.
             *
             * **/
            AlarmUtils.startPollingWithBroadcast(cx,
                    DevicePollingReceiver.getIntent(cx),
                    Plat.deviceService.getPollingPeriod(),
                    Plat.deviceService.getPollingTaskId());
        }

    }

    void onPolling() {


        if (Plat.LOG_FILE_ENABLE) {
            LogUtils.logFIleWithTime("开始轮询");
        }

        List<IDevice> devices = Plat.deviceService.queryAll();
        if (devices != null && devices.size() > 0) {
            for (IDevice dev : devices) {

                onPolling(dev);

                if (dev instanceof IDeviceHub) {
                    IDeviceHub hub = (IDeviceHub) dev;
                    List<IDevice> children = hub.getChildren();
                    if (children != null && children.size() > 0) {
                        for (IDevice device : children) {
                            onPolling(device);
                        }
                    }
                }
            }
        }
    }

    void onPolling(IDevice device) {
        device.onPolling();
        device.onCheckConnection();
    }

}
