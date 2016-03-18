package com.robam.rokipad.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.common.base.Preconditions;
import com.legent.VoidCallback;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HomeStoveView extends FrameLayout {

	@InjectView(R.id.leftHead)
	StoveHeadView leftHead;

	@InjectView(R.id.rightHead)
	StoveHeadView rightHead;

	@InjectView(R.id.imgLock)
	ImageView imgLock;

	public HomeStoveView(Context context) {
		super(context);
		init(context, null);
	}

	public HomeStoveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HomeStoveView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	Stove stove;

	void init(Context cx, AttributeSet attrs) {

		View view = LayoutInflater.from(cx).inflate(R.layout.view_home_stove,
				this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
		}
		stove = Utils.getDefaultStove();
		if (stove != null) {
			leftHead.setData(stove != null ? stove.leftHead : null);
			rightHead.setData(stove != null ? stove.rightHead : null);
		}
//		Log.e("test",stove.getStoveStatus());
	}

	@OnClick(R.id.imgLock)
	public void onClickLock() {
  		Log.e("test", stove.toString());
		if (!checkStove())
			return;

		onSetLock(!stove.isLock);
	}

	 void setData(Stove stove) {
		this.stove = stove;
		leftHead.setData(stove != null ? stove.leftHead : null);
		rightHead.setData(stove != null ? stove.rightHead : null);

		refresh();
	}

	void refresh() {

		imgLock.setSelected(stove == null ? false : stove.isLock);
		leftHead.refresh();
		rightHead.refresh();
	}

	boolean checkStove() {
		try {
			Resources r = getResources();
			Preconditions.checkNotNull(stove,
					r.getString(R.string.dev_invalid_error));
			Preconditions.checkState(stove.isConnected(),
					r.getString(R.string.stove_invalid_error));
			return true;
		} catch (Exception e) {
			ToastUtils.showException(e);
			return false;
		}
	}

	void onSetLock(boolean isLock) {

		stove.setStoveLock(isLock, new VoidCallback() {

			@Override
			public void onSuccess() {
				refresh();
			}

			@Override
			public void onFailure(Throwable t) {
				ToastUtils.showThrowable(t);
			}
		});
	}

}
