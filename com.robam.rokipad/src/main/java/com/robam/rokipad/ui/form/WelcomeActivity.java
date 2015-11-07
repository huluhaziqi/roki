package com.robam.rokipad.ui.form;

import com.legent.ui.ext.BaseActivity;
import com.robam.rokipad.ui.FormKey;

public class WelcomeActivity extends BaseActivity {

	@Override
	protected String createFormKey() {
		return FormKey.WelcomeForm;
	}

	@Override
	protected void onKeyDown_Back() {
	}

}
