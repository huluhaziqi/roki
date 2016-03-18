package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.legent.Callback2;
import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.api.PreferenceUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class WifiAuthDialog extends AbsDialog {

    @InjectView(R.id.edtPwd)
    EditText edtPwd;
    @InjectView(R.id.img)
    ImageView img;
    @InjectView(R.id.txt)
    TextView txt;
    @InjectView(R.id.relPwd)
    LinearLayout relPwd;
    @InjectView(R.id.txtConfirm)
    TextView txtConfirm;
    @InjectView(R.id.imgPwd)
    ImageView imgPwd;

    private Callback2<String> callback;
    private boolean checked = true;

    static public void show(Context cx, String ssid,
                            final Callback2<String> callabck) {
        WifiAuthDialog dlg = new WifiAuthDialog(cx);
        dlg.setCallback(callabck);
        dlg.setEdtPwd(ssid);
        dlg.show();
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_input_wifi_pwd;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }

    public WifiAuthDialog(Context context) {
        super(context, R.style.Theme_Dialog_FullScreen);
    }

    public WifiAuthDialog(Context context, int theme) {
        super(context, theme);
    }

    private void setCallback(Callback2<String> callback) {
        this.callback = callback;
    }

    private void setEdtPwd(String ssid) {
        String savedPwd = PreferenceUtils.getString(ssid, null);
        edtPwd.setText(savedPwd);
    }

    @OnClick(R.id.txtConfirm)
    public void onClickConfirm() {
        String pwd = edtPwd.getText().toString();
        dismiss();
        callback.onCompleted(pwd);
    }

    @OnClick(R.id.img)
    public void onClickImg() {
        dismiss();
    }

    @OnClick(R.id.imgPwd)
    public void onClickPwd() {
        if (checked) {
            edtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            edtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        checked = !checked;
    }

}
