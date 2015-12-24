package com.robam.rokipad.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.legent.plat.Plat;
import com.legent.services.ScreenPowerService;
import com.legent.ui.UIService;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.events.FanStatusChangedEvent;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.device.fan.Fan9700;
import com.robam.common.pojos.device.fan.FanStatus;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.dictionary.StoveAlarm;
import com.robam.common.services.NotifyService;
import com.robam.common.services.StoveAlarmManager;
import com.robam.rokipad.ui.dialog.FanClearConfirmDialog;

/**
 * Created by sylar on 15/6/21.
 */
public class PadNotifyService extends NotifyService {


    static PadNotifyService instance = new PadNotifyService();

    synchronized public static PadNotifyService getInstance() {
        return instance;
    }

    private PadNotifyService() {
    }

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_SHUTDOWN);

        cx.registerReceiver(shutdownBroaddcastReceiver, mIntentFilter);
    }

    @Override
    public void dispose() {
        super.dispose();
        cx.unregisterReceiver(shutdownBroaddcastReceiver);
    }

    //-----------------------------------------------------------------------------------


    @Override
    public void onEvent(FanStatusChangedEvent event) {
        super.onEvent(event);
        onFanStatusChanged(event.pojo);
    }

    @Override
    protected void onFanNeedClean(AbsFan fan) {
        Activity atv = UIService.getInstance().getTop().getActivity();
        if (atv == null) {
            ToastUtils.showShort("油烟机需要清洗了！");
            return;
        }

        FanClearConfirmDialog.show(atv, (Fan9700)fan);
    }

    @Override
    protected void onStoveAlarmEvent(Stove stove, StoveAlarm alarm) {
        short id = alarm.getID();
        if (id == StoveAlarmManager.Key_None || id < 0)
            return;

        if (id <= 6 || id == 8 || id == 9 || id == 13) {
            ToastUtils.showShort(alarm.getName());
        }
    }

    @Override
    protected void onFanPowerEvent(AbsFan fan, boolean power) {
        if (power) {
            wakeup();
        } else {
            gotoSleep();
        }
    }

    @Override
    protected void onFanLevelEvent(AbsFan fan, short level) {

    }

    @Override
    protected void onFanLightEvent(AbsFan fan, boolean power) {
        super.onFanLightEvent(fan, power);
        if (power) {
            wakeup();
        } else {
        }
    }


    //-----------------------------------------------------------------------------------

    PowerManager.WakeLock wakeLock;

    void wakeup() {
        if (ScreenPowerService.getInstance().isScreenOn()) return;
       callOemToolbox();

        //ScreenPowerService.getInstance().wakeup();
    }

    void gotoSleep() {
        if (!ScreenPowerService.getInstance().isScreenOn()) return;
        callOemToolbox();
        //ScreenPowerService.getInstance().gotoSleep();
    }

    void callOemToolbox() {   // comment by zhaiyuanyi :套用CVTE唤醒app
        String action = "com.cvte.androidsystemtoolbox.action.SYSTEM_SLEEP";
        Intent intent = new Intent(action);
        cx.sendBroadcast(intent);
    }

    void onFanStatusChanged(AbsFan fan) {

        boolean isRunning = fan.level > Fan9700.PowerLevel_0 || fan.light;
        if (isRunning) {
            lock();
        } else {
            unlock();
        }
    }

    void lock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) Plat.app.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Fan");

            wakeLock.setReferenceCounted(false); // 获取锁时加上这句代码
            wakeLock.acquire(); // 打开烟机时获取锁，使其到达待机的时候屏幕只变暗，不待机
        }

    }

    void unlock() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }


    //TODO ------------------------------ 关机广播

    BroadcastReceiver shutdownBroaddcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeDevice();
        }

        private void closeDevice() {
            try {
                AbsFan fan = Utils.getDefaultFan();
                if (fan != null) {
                    fan.setFanStatus(FanStatus.Off, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //TODO ------------------------------ 关机广播
}
