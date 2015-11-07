package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.qrcode.QrUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddUserDialog extends AbsDialog {

	static public AddUserDialog show(Context cx, String sn) {
		AddUserDialog dlg = new AddUserDialog(cx, sn);
		dlg.show();
		return dlg;
	}

	private AddUserDialog(Context context, String sn) {
		super(context, R.style.Theme_Dialog_FullScreen);
		Bitmap bmp = QrUtils.create2DCode(sn);
		imgQrCode.setImageBitmap(bmp);
	}

	@Override
	protected int getViewResId() {
		return R.layout.dialog_add_user;
	}

	@InjectView(R.id.img_qrcode)
	ImageView imgQrCode;

	@Override
	protected void initView(View view) {
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
		}
	}

	@OnClick(R.id.layout)
	public void onClick() {
		dismiss();
	}

}
