package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import java.util.logging.Handler;

/**
 * Created by WZTCM on 2016/1/15.
 */
public class RecipeAutoSetSucDialog extends AbsDialog {

    public RecipeAutoSetSucDialog(Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_recipe_set_suc;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    static public void show(Context context) {
        RecipeAutoSetSucDialog dlg = new RecipeAutoSetSucDialog(context);
        dlg.show();
    }
}
