package com.legent.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.common.eventbus.Subscribe;
import com.legent.events.ActivityResultOnPageEvent;
import com.legent.events.ConnectionModeChangedEvent;
import com.legent.plat.PlatApp;
import com.legent.services.ConnectivtyService;
import com.legent.services.TaskService;
import com.legent.utils.EventUtils;
import com.legent.utils.api.MemoryUtils;

public abstract class AbsActivity extends FragmentActivity implements IForm {

    public final static String WillShowPageKey = "WillShowPageKey";

    protected PlatApp app;
    protected String formKey;
    protected boolean isExit = false;

    abstract protected String createFormKey();

    @Override
    public String getFormKey() {
        return formKey;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Log.i("app", "Activity onCreate:" + this.getClass().getSimpleName());
        EventUtils.regist(this);

        Bundle bd = getIntent().getExtras();
        String pageKey = null;
        if (bd != null) {
            pageKey = bd.getString(WillShowPageKey);
        }

        formKey = createFormKey();
        app = (PlatApp) getApplication();
        app.addActivity(this);

        requestWindowFeature();
        setContentView();
        attachActivity(pageKey);
        initOnCreate();

        if (savedState != null) {
            restoreState(savedState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIService.getInstance().setTopActivity(formKey);
        Log.d("app", "top key:" + formKey);
    }

    @Override
    protected void onDestroy() {
        EventUtils.unregist(this);
        UIService.getInstance().detachActivity(formKey);

        View rootView = getWindow().getDecorView().findViewById(
                android.R.id.content);
        MemoryUtils.disposeView(rootView);
        app.removeActivity(this);

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        EventUtils.postEvent(new ActivityResultOnPageEvent(requestCode,
                resultCode, data));
    }

    @Subscribe
    public void onEvent(ConnectionModeChangedEvent event) {

        int mode = event.connectionMode;
        switch (mode) {

            case ConnectivtyService.ConnectionMode_Broken:
                onConnectionBroken();
                break;
            case ConnectivtyService.ConnectionMode_Wifi:
                onConnectedByWifi();
                break;
            case ConnectivtyService.ConnectionMode_Mobil:
                onConnectedByMobil();
                break;

            default:
                break;
        }
    }

    protected void onConnectionBroken() {
    }

    protected void onConnectedByWifi() {
    }

    protected void onConnectedByMobil() {
    }

    // -------------------------------------------------------------------------------
    // onCreate
    // -------------------------------------------------------------------------------

    protected void requestWindowFeature() {
    }

    protected void setContentView() {
        // setContentView(R.layout.activity_layout);
    }

    protected void attachActivity(String pageKey) {
        UIService.getInstance().attachActivity(formKey, this, pageKey);
    }

    protected void initOnCreate() {
    }

    protected void restoreState(Bundle savedState) {
    }

    // -------------------------------------------------------------------------------
    // onKeyDown
    // -------------------------------------------------------------------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        IPage page = UIService.getInstance().getFormManager(formKey).getCurrentPage();
        if (page != null && page.onKeyDown(keyCode, event)) {
            return true;
        } else {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    onKeyDown_Menu();
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    onKeyDown_Back();
                    return true;
                default:
                    return super.onKeyDown(keyCode, event);
            }
        }
    }


    protected void onKeyDown_Menu() {
        FormManager fm = UIService.getInstance().getFormManager(formKey);
        if (fm != null) {
            boolean isHome = fm.isHome();
            if (isHome) {
                fm.toggleMenu();
            }
        }

    }

    protected void onKeyDown_Back() {

        FormManager fm = UIService.getInstance().getTop();
        if (fm == null) {
            exit();
        } else {
            if (fm.isHome()) {
                exit();
            } else {
                UIService.getInstance().popBack();
            }
        }

    }

    protected void exit() {
        if (!isExit) {
            isExit = true;

            showTipWhenExit();

            TaskService.getInstance().postUiTask(new Runnable() {

                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
            app.exit();
        }
    }

    protected void showTipWhenExit() {
        // ToastUtils.showShort(R.string.app_exit);
    }
}
