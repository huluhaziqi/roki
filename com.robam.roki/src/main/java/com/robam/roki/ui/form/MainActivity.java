package com.robam.roki.ui.form;

import android.app.Activity;
import android.content.Intent;

import com.google.common.eventbus.Subscribe;
import com.legent.events.AppVisibleEvent;
import com.legent.plat.events.UserLogoutEvent;
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

}
