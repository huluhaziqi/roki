package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.legent.VoidCallback;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.utils.api.ToastUtils;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.view.UserRegistView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserRegistPage extends BasePage {

	@InjectView(R.id.registView)
	UserRegistView resistView;

	@InjectView(R.id.btnRegist)
	Button btnRegist;

	@InjectView(R.id.imgBack)
	ImageView imgBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.frame_user_regist, container,
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

	@OnClick(R.id.imgBack)
	public void onTitleBack() {
		UIService.getInstance().returnHome();
	}

	@OnClick(R.id.btnRegist)
	public void onRegist() {

		resistView.registAndLogin(new VoidCallback() {

			@Override
			public void onSuccess() {
				onTitleBack();
			}

			@Override
			public void onFailure(Throwable t) {
				ToastUtils.showThrowable(t);
			}
		});
	}
}
