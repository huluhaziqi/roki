package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by linxiaobin on 2015/12/27.
 */
public class OvenBrokenDialog extends AbsDialog {

    @InjectView(R.id.txt)
    TextView txt;
    @InjectView(R.id.img)
    ImageView img;

    public OvenBrokenDialog(Context context) {
        super(context, R.style.Theme_Dialog_HorziFullScreen);
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_oven_broken;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }
    public void setText(String s) {
        if (s != null) txt.setText(s);
    }

    static public void show(Context cx, String one, short type) {
        OvenBrokenDialog dlg = new OvenBrokenDialog(cx);
        Window win = dlg.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.6f;
        win.setAttributes(lp);
        dlg.show();
    }

    @OnClick(R.id.afterBuy)
    public void onClickAfterBuy() {
        dismiss();
//        UIService.getInstance().postPage()
    }

}

