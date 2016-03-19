package com.legent.ui.ext.dialogs;

import android.content.Context;

public class ProgressDialogHelper {

	static ProgressDialog progress;

	public static void setRunning(Context cx, boolean isRunning) {
		if (isRunning) {
			show(cx);
		} else {
			hide();
		}
	}

	public static ProgressDialog show(Context cx) {
		return show(cx, "正在加载,请稍候...");
	}

	public static ProgressDialog show(Context cx, String msg) {
		return show(cx, msg, false);
	}

	public static ProgressDialog show(Context cx, String msg, boolean cancelable) {
		return show(cx, msg, cancelable, false);
	}

	public static ProgressDialog show(Context cx, String msg,
			boolean cancelable, boolean canceledOnTouchOutside) {
		if (progress == null)
			progress = new ProgressDialog(cx);

		progress.setMsg(msg);
		progress.setCancelable(cancelable);
		progress.setCanceledOnTouchOutside(canceledOnTouchOutside);
		progress.show();

		return progress;
	}

	public static void hide() {
		if (progress != null) {
			progress.dismiss();
			progress = null;
		}
	}
}
