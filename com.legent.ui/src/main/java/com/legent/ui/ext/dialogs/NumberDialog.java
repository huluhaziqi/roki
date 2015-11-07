package com.legent.ui.ext.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.legent.utils.api.ViewUtils;

public class NumberDialog {

	public static void show(Context cx, String title, final int min,
			final int max, int value, final NumberSeletedCallback callback) {

		final NumberPicker numberPicker = new NumberPicker(cx);
		numberPicker.setFocusable(false);
		numberPicker.setMinValue(min);
		numberPicker.setMaxValue(max);
		numberPicker.setValue(value);
		numberPicker
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

		AlertDialog dialog = DialogHelper.newDialog_OkCancel(cx, title, null,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// numberPicker.clearFocus();
						ViewUtils.setDialogShowField(dialog, true);
						if (which == DialogInterface.BUTTON_POSITIVE) {
							int v = numberPicker.getValue();
							if (v < min || v > max) {
								ViewUtils.setDialogShowField(dialog, false);
							} else {
								if (callback != null) {
									callback.onNumberSeleted(v);
								}
							}

						}
					}
				});

		dialog.setView(numberPicker, 150, 50, 150, 50);
		dialog.show();
	}

	public interface NumberSeletedCallback {
		void onNumberSeleted(int value);
	}
}
