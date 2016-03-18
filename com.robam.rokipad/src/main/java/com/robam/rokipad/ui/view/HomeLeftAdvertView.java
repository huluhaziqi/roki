package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.legent.Callback;
import com.legent.utils.api.ToastUtils;
import com.robam.common.io.cloud.Reponses.HomeAdvertsForPadResponse;
import com.robam.common.services.AdvertManager;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeLeftAdvertView extends FrameLayout {

	public HomeLeftAdvertView(Context context) {
		super(context);
		init(context, null);
	}

	public HomeLeftAdvertView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HomeLeftAdvertView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@InjectView(R.id.adLeft)
	HomeAdvertItemView adLeft;

	@InjectView(R.id.adMiddle)
	HomeAdvertItemView adMiddle;

	@InjectView(R.id.adRight)
	HomeAdvertItemView adRight;

	void init(Context cx, AttributeSet attrs) {

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_home_left_advert, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);

			adRight.setTag(HomeAdvertItemView.TAG_Today_Menu);
			initData();
		}
	}

	void initData() {
		AdvertManager.getInstance().getPadAdverts(
				new Callback<HomeAdvertsForPadResponse>() {

					@Override
					public void onSuccess(HomeAdvertsForPadResponse result) {
						if (result != null) {
							adLeft.setAdverts(result.left);
							adMiddle.setAdverts(result.middle);
						}

					}

					@Override
					public void onFailure(Throwable t) {
						ToastUtils.showThrowable(t);
					}
				});
	}
}
