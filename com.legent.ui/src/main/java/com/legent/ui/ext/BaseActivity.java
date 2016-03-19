package com.legent.ui.ext;

import com.legent.ui.AbsActivity;
import com.legent.ui.R;

public abstract class BaseActivity extends AbsActivity {

	@Override
	protected void setContentView() {
		setContentView(R.layout.abs_activity);
	}
}
