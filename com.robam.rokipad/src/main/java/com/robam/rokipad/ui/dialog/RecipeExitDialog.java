package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/15.
 */
public class RecipeExitDialog extends AbsDialog {

    @InjectView(R.id.txtCancel)
    TextView txtCancel;
    @InjectView(R.id.txtConfirm)
    TextView txtConfirm;

    DeleteControlDialog.OnConfirmListener listener;

    public RecipeExitDialog(Context context) {
        super(context, R.style.dialog);
    }

    static public void show(Context cx, DeleteControlDialog.OnConfirmListener listener) {
        RecipeExitDialog dlg = new RecipeExitDialog(cx);
        dlg.setListener(listener);
        dlg.show();
    }

    public void setListener(DeleteControlDialog.OnConfirmListener listener) {
        this.listener = listener;
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_recipe_exit;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
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
