package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;

import com.legent.ui.ext.dialogs.DialogHelper;
import com.robam.common.pojos.device.fan.Fan9700;


public class FanClearConfirmDialog {

	static boolean isRunning;

	public static void show(Context cx, final Fan9700 fan) {


		if (!fan.smartParams.IsNoticClean)
			return;

		if (isRunning || !fan.clean)
			return;
		isRunning = true;

		String title = "烟机清洗";
		String message = "您的烟机需要清洗了！";
		DialogHelper.newOKDialog(cx, title, message,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						isRunning = false;
						fan.restFanCleanTime(null);
					}
				}).show();
	}

}
