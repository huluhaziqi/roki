package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.legent.VoidCallback;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.events.DeviceSteamSwitchViewEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.ui.PageKey;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2015/12/19.
 */
public class SteamCleanAndSterDialog extends AbsDialog {
    @InjectView(R.id.txtOne)
    TextView txtOne;
    @InjectView(R.id.txtTwo)
    TextView txtTwo;
    @InjectView(R.id.start)
    TextView start;
    @InjectView(R.id.imgIcon)
    ImageView imgIcon;

    public interface ClickListener {
        public void onClick();
    }

    ClickListener listener;
    DeviceWorkMsg deviceWorkMsg = null;
    AbsSteamoven steam = null;

    public SteamCleanAndSterDialog(Context context) {
        super(context, R.style.Theme_Dialog_HorziFullScreen);
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_clean_and_ster;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }

    public void setText(String one, String two) {
        if (one != null) txtOne.setText(one);
        if (two != null) txtTwo.setText(two);
    }

    public void setMsg(DeviceWorkMsg msg) {
        deviceWorkMsg = msg;
    }

    public void setIcon(int resId) {
        imgIcon.setImageResource(resId);
    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    static public void show(Context cx, DeviceWorkMsg msg, ClickListener listener) {
        SteamCleanAndSterDialog dlg = new SteamCleanAndSterDialog(cx);
        Window win = dlg.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.6f;
        win.setAttributes(lp);
        String one = "", two = null;
        if (msg.getType().equals("自洁")) {
            one = "请在凹状加热盘内滴些食醋\n工作45分钟后即可轻松除去水垢";
            dlg.setIcon(R.mipmap.img_steam_ster_tag);
        } else if (msg.getType().equals("杀菌")) {
            dlg.setIcon(R.mipmap.img_steam_clean_tag);
            one = "即将开启60分钟的\n高温蒸汽杀菌模式";
        }
        dlg.setMsg(msg);
        dlg.setText(one, two);
        dlg.setListener(listener);
        dlg.show();
    }

    @OnClick(R.id.start)
    public void onClickStart() {
        dismiss();
        listener.onClick();
//        if (UiHelper.checkAuthWithDialog(getContext(), PageKey.UserLogin)) {
//            final Bundle bundle = new Bundle();
//            if (deviceWorkMsg != null) {
//                short time = Short.valueOf(deviceWorkMsg.getTime());
//                short temp = Short.valueOf(deviceWorkMsg.getTemperature());
//                bundle.putSerializable("msg", deviceWorkMsg);
//                steam.setSteamWorkMode((short) 0, temp, time, (short) 0, new VoidCallback() {
//                    @Override
//                    public void onSuccess() {
//                        EventUtils.postEvent(new DeviceSteamSwitchViewEvent(1, bundle));
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        ToastUtils.showThrowable(t);
//                    }
//                });
//            }
//        }
    }
}
