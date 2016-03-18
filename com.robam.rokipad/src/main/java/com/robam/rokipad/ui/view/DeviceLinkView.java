package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.io.device.IDeviceFinder;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.plat.pojos.dictionary.DeviceType;
import com.legent.plat.services.DeviceTypeManager;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.EventUtils;
import com.legent.utils.api.PreferenceUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.api.WifiUtils;
import com.robam.common.PrefsKey;
import com.robam.common.events.DeviceEasylinkCompletedEvent;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.dialog.DeviceLinkFailDialog;
import com.robam.rokipad.ui.dialog.DeviceLinkSuccessDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/11.
 */
public class DeviceLinkView extends FrameLayout {

    @InjectView(R.id.txt1)
    TextView txt1;
    @InjectView(R.id.txtPlePush)
    TextView txtPlePush;
    @InjectView(R.id.txtPlePush2)
    TextView txtPlePush2;
    @InjectView(R.id.relIcon)
    RelativeLayout relIcon;
    @InjectView(R.id.txt2)
    TextView txt2;
    @InjectView(R.id.txtRssi)
    TextView txtRssi;
    @InjectView(R.id.edtPwd)
    EditText edtPwd;
    @InjectView(R.id.txtConfirm)
    TextView txtConfirm;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    easylink();
                    break;
            }
        }
    };

    public DeviceLinkView(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceLinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceLinkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventUtils.regist(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventUtils.unregist(this);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_device_link, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        String ssid = WifiUtils.getCurrentSsid(Plat.app);
        txtRssi.setText(ssid);
        if (ssid != null) {
            String pwd = PreferenceUtils.getString(ssid, null);
            edtPwd.setText(pwd);
            if (Strings.isNullOrEmpty(pwd)) {
                edtPwd.requestFocus();
            }
        }
    }

    @OnClick(R.id.txtConfirm)
    public void onClickConfirm() {
        try {
//            easylink();
            handler.sendEmptyMessage(0);
        } catch (Exception ex) {
            ToastUtils.showShort(ex.getMessage());
        }
    }

    private void easylink() {

        final String ssid = txtRssi.getText().toString().trim();
        final String pwd = edtPwd.getText().toString().trim();

        Preconditions.checkState(!Strings.isNullOrEmpty(ssid),
                getContext().getString(R.string.roki_error_no_wifi));
        Preconditions.checkState(!Strings.isNullOrEmpty(pwd),
                getContext().getString(R.string.roki_wifi_pwd_hint));

        PreferenceUtils.setString(PrefsKey.Ssid, ssid);
        PreferenceUtils.setString(ssid, pwd);

//        IDeviceFinder finder =  Plat.commander.getDeviceFinder();
        IDeviceFinder finder = Plat.dcMqtt.getDeviceFinder();

        ProgressDialogHelper.setRunning(getContext(), true);
        finder.start(ssid, pwd, 1000 * 40, new Callback<DeviceInfo>() {

            @Override
            public void onSuccess(DeviceInfo result) {
//                DeviceLinkSuccessDialog.show(getContext());
                ProgressDialogHelper.setRunning(getContext(), false);
                PreferenceUtils.setString(ssid, pwd);
                addKettle(result);
            }

            @Override
            public void onFailure(Throwable t) {
                ProgressDialogHelper.setRunning(getContext(), false);
                txtConfirm.setText("再试一次");
                ToastUtils.showThrowable(t);
            }
        });
    }

    void addKettle(final DeviceInfo devInfo) {

        devInfo.ownerId = Plat.accountService.getCurrentUserId();
        if (Strings.isNullOrEmpty(devInfo.name)) {
            DeviceType dt = DeviceTypeManager.getInstance().getDeviceType(
                    devInfo.guid);
            devInfo.name = dt.getName();
        }

        Plat.deviceService.addWithBind(devInfo.guid, devInfo.name,
                true, new VoidCallback() {

                    @Override
                    public void onSuccess() {
//                        ToastUtils.showShort("添加完成");
                        DeviceLinkSuccessDialog.show(getContext());
                        EventUtils.postEvent(new DeviceEasylinkCompletedEvent(devInfo));
                        UIService.getInstance().returnHome();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ToastUtils.showThrowable(t);
                    }
                });

    }

}