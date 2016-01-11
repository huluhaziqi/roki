package com.robam.roki.ui.dialog;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.legent.ui.ext.BaseActivity;
import com.robam.roki.ui.FormKey;


public class CountDownDialog extends BaseActivity {

    static public void start(Activity atv) {
        atv.startActivity(new Intent(atv, CountDownDialog.class));
    }

    @Override
    protected String createFormKey() {
        return FormKey.CountDownForm;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedState);
    }
}
