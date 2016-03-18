package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.location.GpsStatus;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.DeviceDeleteEvent;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rosicky on 16/1/6.
 */
public class DeleteControlDialog extends AbsDialog {

    @InjectView(R.id.img)
    ImageView img;
    @InjectView(R.id.txt)
    TextView txt;
    @InjectView(R.id.txtCancel)
    TextView txtCancel;
    @InjectView(R.id.txtConfirm)
    TextView txtConfirm;

    String id;
    OnConfirmListener listener;

    public interface OnConfirmListener {
        public void onConfirm();
    }

    static public DeleteControlDialog show(Context cx, String sn, String id, OnConfirmListener listener) {
        DeleteControlDialog dlg = new DeleteControlDialog(cx);
        dlg.initUI(sn, id, listener);
        dlg.show();
        return dlg;
    }

    public DeleteControlDialog(Context context) {
        super(context, R.style.dialog);
    }

    public void initUI(String s, String id, OnConfirmListener listener) {
        this.id = id;
        this.listener = listener;
        if (s.startsWith("消毒")) {
            img.setImageResource(R.mipmap.img_control_delete_sterilizer);
        } else if (s.startsWith("蒸汽炉")) {
            img.setImageResource(R.mipmap.img_control_delete_steam);
        } else if (s.startsWith("烤箱")) {
            img.setImageResource(R.mipmap.img_control_delete_oven);
        } else if (s.startsWith("微波炉")) {
            img.setImageResource(R.mipmap.img_control_delete_microwave);
        }
        txt.setText("真的要把" + s + "删除吗？");
    }

    @OnClick(R.id.txtCancel)
    public void onClickCancel() {
        dismiss();
    }

    @OnClick(R.id.txtConfirm)
    public void onClickConfirm() {
        listener.onConfirm();
        dismiss();
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_delete_control;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }
}
