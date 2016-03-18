package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rosicky on 16/1/6.
 */
public class LogoutDialog extends AbsDialog {

    @InjectView(R.id.txtCancel)
    TextView txtCancel;
    @InjectView(R.id.txtConfirm)
    TextView txtConfirm;

    DeleteControlDialog.OnConfirmListener listener;

    public LogoutDialog(Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_logout;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }

    static public void show(Context cx, DeleteControlDialog.OnConfirmListener listener) {
        LogoutDialog dlg = new LogoutDialog(cx);
        dlg.setListener(listener);
        dlg.show();
    }

    public void setListener(DeleteControlDialog.OnConfirmListener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.txtCancel)
    public void onClickCancel() {
        dismiss();
    }

    @OnClick(R.id.txtConfirm)
    public void onClickConfirm() {
        dismiss();
        listener.onConfirm();
    }
}
