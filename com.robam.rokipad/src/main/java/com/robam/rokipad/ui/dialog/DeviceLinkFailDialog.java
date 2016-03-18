package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import butterknife.ButterKnife;

/**
 * Created by WZTCM on 2016/1/11.
 */
public class DeviceLinkFailDialog extends AbsDialog {

    public DeviceLinkFailDialog(Context context) {
        super(context, R.style.dialog);
    }
    static public DeviceLinkFailDialog show(Context cx) {
        DeviceLinkFailDialog dlg = new DeviceLinkFailDialog(cx);
        dlg.show();
        return dlg;
    }


    @Override
    protected int getViewResId() {
        return R.layout.dialog_link_fail;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }
}
