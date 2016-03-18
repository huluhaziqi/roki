package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/15.
 */
public class RecipeInWorkingDialog extends AbsDialog {

    public RecipeInWorkingDialog(Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_recipe_in_working;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }

    @OnClick(R.id.txtIKnow)
    public void onClickIKnow() {
        dismiss();
    }

    static public void show(Context context) {
        RecipeInWorkingDialog dlg = new RecipeInWorkingDialog(context);
        dlg.show();
    }
}
