package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.legent.VoidCallback;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.io.cloud.RokiRestHelper;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.BuildConfig;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.dialog.HintDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SettingSaleServiceView extends FrameLayout {

	@InjectView(R.id.imgApply)
	ImageView imgApply;

	public SettingSaleServiceView(Context context) {
		super(context);
		init(context, null);
	}

	public SettingSaleServiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SettingSaleServiceView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	void init(Context cx, AttributeSet attrs) {

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_setting_sale_service, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
		}
	}

	@OnClick(R.id.imgApply)
	public void onClickApply() {

		if (BuildConfig.DEBUG) {
			HintDialog.show(getContext(),
					getResources().getString(R.string.forsale_dialog_hint));

			return;
		}

		if (!UiHelper.checkAuth(PageKey.UserLogin))
			return;

		AbsFan fan = Utils.getDefaultFan();
		if (fan == null) {
			ToastUtils.showShort(R.string.dev_invalid_error);
			return;
		}

		RokiRestHelper.applyAfterSale(fan.getID(), new VoidCallback() {

			@Override
			public void onSuccess() {
				HintDialog.show(getContext(),
						getResources().getString(R.string.forsale_dialog_hint));
			}

			@Override
			public void onFailure(Throwable t) {
				ToastUtils.showThrowable(t);
			}
		});
	}
}
