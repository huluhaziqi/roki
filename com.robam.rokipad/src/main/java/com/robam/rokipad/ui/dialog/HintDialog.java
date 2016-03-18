package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.api.ViewUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HintDialog extends AbsDialog {

	public static void show(Context cx, String msg) {
		HintDialog dlg = new HintDialog(cx, msg);
		dlg.show();
	}

	@InjectView(R.id.txtMsg)
	TextView txtMsg;

	private HintDialog(Context cx, String msg) {
		super(cx, R.style.Theme_Dialog_FullScreen);
		ViewUtils.setFullScreen(cx, this);
		txtMsg.setText(msg);
	}

	@Override
	protected int getViewResId() {
		return R.layout.dialog_hint;
	}

	@Override
	protected void initView(View view) {

		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
		}
	}

	@OnClick(R.id.layout)
	public void onClick() {
		dismiss();
	}
}
