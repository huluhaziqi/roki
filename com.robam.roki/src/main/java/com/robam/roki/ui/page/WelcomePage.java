package com.robam.roki.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legent.ui.ext.BasePage;
import com.legent.utils.api.PreferenceUtils;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.form.MainActivity;
import com.robam.roki.ui.form.WizardActivity;

/**
 * Created by sylar on 15/6/4.
 */
public class WelcomePage extends BasePage {

    final long DELAY = 1500;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_welcome, container, false);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                startNext();
            }
        }, DELAY);
        return view;
    }

    void startNext() {
        boolean isFirstUse = PreferenceUtils.getBool(
                PageArgumentKey.IsFirstUse, true);
        if (isFirstUse) {
            WizardActivity.start(activity);
        } else {
            MainActivity.start(activity);
        }
    }

}
