package com.robam.rokipad.ui.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.utils.api.PackageUtils;
import com.legent.utils.api.PreferenceUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.PrefsKey;
import com.robam.common.Utils;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.services.AppUpdateService;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SettingAboutView extends FrameLayout {

    @InjectView(R.id.imgLogo)
    ImageView imgLogo;

    @InjectView(R.id.txtVersion)
    TextView txtVersion;

    @InjectView(R.id.txtUpgrade)
    TextView txtUpgrade;

    boolean isExit;
    int logoClickCount;

    public SettingAboutView(Context context) {
        super(context);
        init(context, null);
    }

    public SettingAboutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingAboutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(
                R.layout.view_setting_about, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            txtVersion.append("V" + PackageUtils.getVersionName(getContext()));
        }
    }

    @OnClick(R.id.imgLogo)
    public void onClickLogo() {
        if (!isExit) {
            isExit = true;
            this.postDelayed(new Runnable() {

                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            logoClickCount++;
            if (logoClickCount >= 5) {
                onToDestop();
            }
        }
    }

    @OnClick(R.id.txtUpgrade)
    public void onClickUpgrade() {
        AppUpdateService.getInstance().start(getContext());
    }

    @OnClick(R.id.txtRestore)
    public void onClickRestore() {
        onRestore();
    }

    void onToDestop() {
        Intent mIntent = new Intent();
        ComponentName comp = new ComponentName("com.android.launcher3",
                "com.android.launcher3.Launcher");
        mIntent.setComponent(comp);
        mIntent.setAction("android.intent.action.VIEW");
        if (mIntent != null) {
            getContext().startActivity(mIntent);
        }
    }


    void onRestore() {                      //增加恢复出厂设置
        if (Plat.accountService.isLogon()) {
            AbsFan dev = Utils.getDefaultFan();
            if (dev != null) {
                Plat.deviceService.deleteWithUnbind(dev.getID(), new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        Log.e("zhaiyuanyi", "onSuccess ");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            Plat.accountService.logout();
        }

        PreferenceUtils.setBool(PrefsKey.Guided, false);
        PreferenceUtils.setString(PrefsKey.Ssid, null);
        PreferenceUtils.setString(PrefsKey.HotKeys, null);
        PreferenceUtils.setString(PrefsKey.HistoryKeys, null);
        ToastUtils.show("出厂设置完成，请重启", Toast.LENGTH_SHORT);
    }

}
