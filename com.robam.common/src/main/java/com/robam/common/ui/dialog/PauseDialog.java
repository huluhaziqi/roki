package com.robam.common.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.api.ViewUtils;
import com.robam.common.R;

public class PauseDialog extends AbsDialog {
	Context cx;

	public static interface DialogCallback {
		void onRestore();
	}

	static public void show(Context cx, DialogCallback callback) {
		PauseDialog dlg = new PauseDialog(cx, callback);
		dlg.show();
	}

	DialogCallback callback;

	private PauseDialog(Context cx, DialogCallback callback) {
		super(cx, R.style.Theme_Dialog_FullScreen);

		ViewUtils.setFullScreen(cx, this);
		this.callback = callback;
	}

	@Override
	protected int getViewResId() {
		return R.layout.dialog_pause;
	}

	@Override
	protected void initView(View view) {

		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		this.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (callback != null) {
					callback.onRestore();
				}
			}
		});
	}

}
