package com.robam.rokipad.ui.page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legent.ui.ext.BasePage;
import com.legent.utils.api.PreferenceUtils;
import com.robam.common.PrefsKey;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.form.GuideActivity;
import com.robam.rokipad.ui.form.MainActivity;

public class WelcomePage extends BasePage {

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = new View(cx);
        view.setBackgroundResource(R.mipmap.img_welcome_bg);
        return view;
    }

//    @Override
//    protected void setRootBg() {
//        setRootBgRes(R.mipmap.img_welcome_bg);
//    }

    @Override
    public void onResume() {
        super.onResume();
        delayShow();
    }

    private void delayShow() {

        rootView.postDelayed(new Runnable() {

            @Override
            public void run() {
                startNextForm();
            }
        }, 1500);

    }

    private void startNextForm() {

        boolean guided = PreferenceUtils.getBool(PrefsKey.Guided, false);

        if (!guided) {
            // 未激活帐号
            startGuideForm();
        } else {
            startMainForm();
        }
    }

    private void startGuideForm() {
        startActivity(new Intent(cx, GuideActivity.class));
        activity.finish();
    }

    private void startMainForm() {
        MainActivity.start(activity);
    }

}
