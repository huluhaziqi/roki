package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.legent.VoidCallback;
import com.legent.ui.ext.HeadPage;
import com.legent.utils.api.ToastUtils;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.form.GuideActivity;
import com.robam.rokipad.ui.view.UserRegistView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GuideRegistPage extends HeadPage {

	@InjectView(R.id.registView)
	UserRegistView resistView;

	@InjectView(R.id.btnRegist)
	Button btnRegist;

	@Override
	protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle bundle)  {

		View view = inflater.inflate(R.layout.frame_guide_regist, container,
				false);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
		}

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.btnRegist)
	public void onRegist() {

		resistView.registAndLogin(new VoidCallback() {

			@Override
			public void onSuccess() {
				GuideActivity.onGuideOver(activity, true);
			}

			@Override
			public void onFailure(Throwable t) {
				ToastUtils.showThrowable(t);
			}
		});

	}

}
