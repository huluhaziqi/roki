package com.robam.rokipad.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SettingInactiveHintView extends FrameLayout {

	public static interface OnClickCallback {
		void onClick();
	}

	@InjectView(R.id.txtHint)
	TextView txtHint;

	@InjectView(R.id.btnHint)
	Button btnHint;

	OnClickCallback callback;

	public SettingInactiveHintView(Context cx, String hintText,
			String buttonText, OnClickCallback callback) {
		super(cx);

		this.callback = callback;

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_setting_inactive_hint, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);

			txtHint.setText(hintText);
			btnHint.setText(buttonText);
		}
	}

	@OnClick(R.id.btnHint)
	public void onClick() {
		if (callback != null) {
			callback.onClick();
		}
	}

}
