package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import butterknife.ButterKnife;

/**
 * Created by WZTCM on 2016/1/11.
 */
public class DeviceLinkSuccessDialog extends AbsDialog {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    AbsDialog dialog = (AbsDialog) msg.obj;
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public void sendMsg(DeviceLinkSuccessDialog dlg) {
        Message msg = Message.obtain();
        msg.what = 0;
        msg.obj = dlg;
        handler.sendMessageDelayed(msg, 1000);
    }

    public DeviceLinkSuccessDialog(Context context) {
        super(context, R.style.dialog);
    }

    static public DeviceLinkSuccessDialog show(Context cx) {
        DeviceLinkSuccessDialog dlg = new DeviceLinkSuccessDialog(cx);
        dlg.show();
        dlg.sendMsg(dlg);
        return dlg;
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_link_success;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }
}
