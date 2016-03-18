package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ChooseStoveDialog extends AbsDialog {

	public static interface StoveSelectedCallback {
		void onStoveSelected(int stoveHeadId);
	}

	public static void show(Context cx, StoveSelectedCallback callback) {
		ChooseStoveDialog dlg = new ChooseStoveDialog(cx, callback);
		dlg.show();
	}

	@InjectView(R.id.img_left)
	ImageView imgLeft;

	@InjectView(R.id.img_right)
	ImageView imgRight;

	StoveSelectedCallback callback;

	private ChooseStoveDialog(Context context, StoveSelectedCallback callback) {
		super(context, R.style.Theme_Dialog_FullScreen);
		this.callback = callback;
	}

	@Override
	protected int getViewResId() {
		return R.layout.dialog_choose_stove;
	}

	@Override
	protected void initView(View view) {
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
		}
	}

	@OnClick(R.id.img_left)
	public void onClickLeft() {
		dismiss();

		if (callback != null) {
			callback.onStoveSelected(Stove.StoveHead.LEFT_ID);
		}
	}

	@OnClick(R.id.img_right)
	public void onClickRight() {
		dismiss();

		if (callback != null) {
			callback.onStoveSelected(Stove.StoveHead.RIGHT_ID);
		}
	}

}
