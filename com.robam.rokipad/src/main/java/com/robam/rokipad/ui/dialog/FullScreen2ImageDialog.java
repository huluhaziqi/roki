package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/11.
 */
public class FullScreen2ImageDialog extends AbsDialog {

    @InjectView(R.id.imgDelete)
    ImageView imgDelete;
    @InjectView(R.id.imgQR1)
    ImageView imgQR1;
    @InjectView(R.id.imgQR2)
    ImageView imgQR2;

    public FullScreen2ImageDialog(Context context) {
        super(context, R.style.Dialog_FullScreen);
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_full_screen_2img;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }

    private void setTitle(String s) {
        // TODO 二维码图案
    }

    static public FullScreen2ImageDialog show(Context cx, String name) {
        FullScreen2ImageDialog dlg = new FullScreen2ImageDialog(cx);
        dlg.setTitle(name);
        dlg.show();
        return dlg;
    }

    @OnClick(R.id.imgDelete)
    public void onClickDelete() {
        dismiss();
    }

}
