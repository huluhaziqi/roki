package com.robam.rokipad.ui.form;

import android.app.Activity;
import android.content.Intent;

import com.legent.events.AppVisibleEvent;
import com.legent.ui.ext.BaseActivity;
import com.legent.utils.EventUtils;
import com.robam.rokipad.ui.FormKey;

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


}
