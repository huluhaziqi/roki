//package com.robam.rokipad.ui.dialog;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.OnClick;
//
//import com.legent.ui.ext.dialogs.AbsDialog;
//import com.legent.ui.ext.dialogs.DialogHelper;
//import com.legent.utils.api.ViewUtils;
//import com.robam.rokipad.R;
//
//import android.content.Context;
//import android.view.View;
//import android.widget.ImageView;
//
//public class PauseDialog extends AbsDialog {
//
//	public static interface StartClickCallback {
//		void onStartClick();
//	}
//
//	public static void show(Context cx, StartClickCallback callback) {
//		PauseDialog dlg = new PauseDialog(cx, callback);
//		dlg.show();
//	}
//
//	@InjectView(R.id.img_pause)
//	ImageView imgPause;
//
//	StartClickCallback callback;
//
//	public PauseDialog(Context cx, StartClickCallback callback) {
//		super(cx, R.style.PauseDialog);
//		ViewUtils.setFullScreen(cx, this);
//		this.callback = callback;
//	}
//
//	@Override
//	protected int getViewResId() {
//		return R.layout.dialog_pause;
//	}
//
//	@Override
//	protected void initView(View view) {
//		if (!view.isInEditMode()) {
//			ButterKnife.inject(this, view);
//		}
//	}
//
//	@OnClick(R.id.img_pause)
//	public void onClick() {
//		dismiss();
//		if (callback != null) {
//			callback.onStartClick();
//		}
//	}
//
//}
