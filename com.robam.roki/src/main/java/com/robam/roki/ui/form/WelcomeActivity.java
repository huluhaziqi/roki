package com.robam.roki.ui.form;

import com.legent.ui.ext.BaseActivity;
import com.robam.roki.ui.FormKey;

public class WelcomeActivity extends BaseActivity {

	@Override
	protected String createFormKey() {
		return FormKey.WelcomeForm;
	}
	

	@Override
	protected void onKeyDown_Back() {
	}

}
