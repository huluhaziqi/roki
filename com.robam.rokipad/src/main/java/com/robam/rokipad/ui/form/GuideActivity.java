package com.robam.rokipad.ui.form;

import android.app.Activity;

import com.legent.ui.ext.BaseActivity;
import com.legent.utils.api.PreferenceUtils;
import com.robam.common.PrefsKey;
import com.robam.rokipad.ui.FormKey;

public class GuideActivity extends BaseActivity {

	static public void onGuideOver(Activity atv, boolean isGuided) {
		PreferenceUtils.setBool(PrefsKey.Guided, isGuided);
		MainActivity.start(atv);
	}

	@Override
	protected String createFormKey() {
		return FormKey.GuideForm;
	}

}
