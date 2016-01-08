package com.robam.roki.ui.form;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.events.AppVisibleEvent;
import com.legent.plat.Plat;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.plat.pojos.User;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.plat.pojos.device.IDevice;
import com.legent.ui.UIService;
import com.legent.ui.ext.BaseActivity;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.roki.ui.FormKey;
import com.robam.roki.ui.PageKey;

public class MainActivity extends BaseActivity {

    static public void start(Activity atv) {
        atv.startActivity(new Intent(atv, MainActivity.class));
        atv.finish();
    }

    @Override
    protected String createFormKey() {
        return FormKey.MainForm;
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventUtils.postEvent(new AppVisibleEvent(true));
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventUtils.postEvent(new AppVisibleEvent(false));
    }

    @Override
    protected void onKeyDown_Back() {
        String pageKey = UIService.getInstance().getTop().getCurrentPageKey();
        if (PageKey.RecipeCooking.equals(pageKey)) {
            //防止烧菜中按返回键退出烧菜
        } else {
            super.onKeyDown_Back();
        }

    }

    @Subscribe
    public void onEvent(UserLogoutEvent event) {
        UIService.getInstance().returnHome();
    }

    @Override
    protected void showTipWhenExit() {
        ToastUtils.showShort("再按一次返回键退出程序");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final User owner = Plat.accountService.getCurrentUser();
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bd = data.getExtras();
                    String sn = bd.getString("result");
                    ToastUtils.showLong(sn);
                    Plat.deviceService.getDeviceBySn(sn, new Callback<DeviceInfo>() {
                        @Override
                        public void onSuccess(DeviceInfo deviceInfo) {
                            Plat.deviceService.bindDevice(owner.getID(),deviceInfo.getID(),deviceInfo.getName(),true,null);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.show("绑定失败",2000);
                        }
                    });

                }
                break;
        }
    }

}
