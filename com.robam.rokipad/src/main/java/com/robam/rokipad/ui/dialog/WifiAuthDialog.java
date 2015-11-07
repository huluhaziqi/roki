package com.robam.rokipad.ui.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.legent.Callback2;
import com.legent.utils.api.PreferenceUtils;
import com.robam.rokipad.R;

public class WifiAuthDialog {

	static public void show(Context cx, String ssid,
			final Callback2<String> callabck) {

		View view = LayoutInflater.from(cx).inflate(
				R.layout.dialog_input_wifi_pwd, null, false);

		final EditText edtPwd = (EditText) view.findViewById(R.id.edtPwd);
		String savedPwd = PreferenceUtils.getString(ssid, null);
		edtPwd.setText(savedPwd);

		CheckBox chkPwd = (CheckBox) view.findViewById(R.id.chkShowPwd);
		chkPwd.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton chk, boolean checked) {
				if (!checked)
					edtPwd.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				else
					edtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			}
		});
		chkPwd.setChecked(true);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dlg, int witch) {
				if (witch == DialogInterface.BUTTON_POSITIVE) {
					String pwd = edtPwd.getText().toString();
					dlg.dismiss();
					if (callabck != null) {
						callabck.onCompleted(pwd);
					}
				}
			}
		};

		AlertDialog.Builder builder = new Builder(cx);
		builder.setPositiveButton("连接", listener);
		builder.setNegativeButton("取消", null);
		builder.setView(view);
		builder.setTitle(ssid);
		builder.create().show();
	}

}
