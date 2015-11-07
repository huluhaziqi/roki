package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.legent.Callback;
import com.legent.plat.pojos.User;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.utils.api.ToastUtils;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.view.UserLoginView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserLoginPage extends BasePage {

	@InjectView(R.id.loginView)
	UserLoginView loginView;

	@InjectView(R.id.btnLogin)
	Button btnLogin;

	@InjectView(R.id.imgBack)
	ImageView imgBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.frame_user_login, container,
				false);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			loginView.setOnRegistCallback(resitCallback);
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
		UIService.getInstance().popBack();
	}

	@OnClick(R.id.btnLogin)
	public void onLogin() {

		loginView.login(new Callback<User>() {

			@Override
			public void onSuccess(User user) {
				onLoginCompleted(user);
			}

			@Override
			public void onFailure(Throwable t) {
				ToastUtils.showThrowable(t);
			}
		});

	}

	UserLoginView.OnRegistCallback resitCallback = new UserLoginView.OnRegistCallback() {

		@Override
		public void onRegist() {
			UIService.getInstance().postPage(PageKey.UserRegist);
		}
	};

	private void onLoginCompleted(final User user) {
		ToastUtils.showShort("登录成功!");
		onTitleBack();
	}

}
