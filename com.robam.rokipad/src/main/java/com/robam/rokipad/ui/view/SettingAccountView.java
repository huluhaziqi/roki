package com.robam.rokipad.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.legent.utils.graphic.collection.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.robam.common.Utils;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.dialog.AddUserDialog;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingAccountView extends FrameLayout {

	final int DEF_IMG = R.mipmap.img_user_default;
	final DisplayImageOptions opt = ImageUtils.getDefaultBuilder()
			.showImageOnLoading(DEF_IMG)
			.showImageForEmptyUri(DEF_IMG)
			.showImageOnFail(DEF_IMG)
			.displayer(new CircleBitmapDisplayer()).build();

	@InjectView(R.id.layout)
	LinearLayout layout;

	@InjectView(R.id.addUserView)
	View addUserView;

	Context cx;
	User owner;
	AbsFan fan;

	public SettingAccountView(Context context) {
		super(context);
		init(context, null);
	}

	public SettingAccountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SettingAccountView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	void init(Context cx, AttributeSet attrs) {
		this.cx = cx;
		owner = Plat.accountService.getCurrentUser();
		fan = Utils.getDefaultFan();

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_setting_account, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);

			addUserView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (UiHelper.checkAuth(PageKey.UserLogin)) {
						addAccount();
					}
				}
			});
		}

		refresh();
	}

	private void refresh() {

		final List<User> users = Lists.newArrayList();
		if (owner != null) {
			users.add(owner);
		}

		if (fan == null) {
			buildViews(users);
		} else if (Plat.accountService.isLogon()) {
			ProgressDialogHelper.setRunning(cx, true);
			long userId = Plat.accountService.getCurrentUserId();
			Plat.deviceService.getDeviceUsers(userId, fan.getID(),
					new Callback<List<User>>() {

						@Override
						public void onSuccess(List<User> result) {
							if (result != null) {
								if (result.contains(owner)) {
									result.remove(owner);
								}
								users.addAll(result);
							}
							buildViews(users);
							ProgressDialogHelper.setRunning(cx, false);
						}

						@Override
						public void onFailure(Throwable t) {
							ProgressDialogHelper.setRunning(cx, false);
							ToastUtils.showThrowable(t);
						}
					});
		}
	}

	void buildViews(List<User> users) {
		if (users == null)
			return;

		layout.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(cx);
		UserViewItem item;
		View view;
		if (users.size() > 0) {
			for (User user : users) {
				view = inflater.inflate(R.layout.view_setting_account_item,
						this, false);
				item = new UserViewItem(view, user);
				view.setTag(item);

				layout.addView(view);
			}
		}

		layout.addView(addUserView);
	}

	private void addAccount() {
		if (!Plat.accountService.isLogon())
			return;

		if (fan == null) {
			ToastUtils.showShort(getContext().getResources().getString(
					R.string.unconnect_toast));
			return;
		}

		Plat.deviceService.getSnForDevice(owner.id, fan.getID(),
				new Callback<String>() {

					@Override
					public void onSuccess(String sn) {
						if (Strings.isNullOrEmpty(sn)) {
							ToastUtils.showShort("获取设备串码失败");
							return;
						}

						AddUserDialog dlg = AddUserDialog
								.show(getContext(), sn);
						dlg.setOnDismissListener(new OnDismissListener() {

							@Override
							public void onDismiss(DialogInterface arg0) {
								refresh();
							}
						});
					}

					@Override
					public void onFailure(Throwable t) {
						ToastUtils.showThrowable(t);
					}
				});
	}

	class UserViewItem {

		@InjectView(R.id.img_account)
		ImageView imgFigure;

		@InjectView(R.id.tv_name)
		TextView txtName;

		@InjectView(R.id.tv_main_account)
		TextView txtIsMain;

		@InjectView(R.id.tv_phone)
		TextView txtPhone;

		View view;
		User user;
		boolean isOwner;

		public UserViewItem(View view, User user) {
			this.view = view;
			this.user = user;
			ButterKnife.inject(this, view);

			isOwner = user.id == Plat.accountService.getCurrentUserId();

			txtName.setText(user.name);
			txtPhone.setText(user.phone);
			txtIsMain.setVisibility(isOwner ? View.VISIBLE : View.GONE);

			ImageUtils.displayImage(user.figureUrl, imgFigure, opt);

			view.setOnLongClickListener(onLongClick);
		}

		View.OnLongClickListener onLongClick = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				String message = isOwner ? "确定要退出主账户吗？" : "确定要删除该账户吗？";
				DialogHelper.newDialog_OkCancel(getContext(), null, message,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dlg, int witch) {
								if (witch == DialogInterface.BUTTON_POSITIVE) {
									onClickUser(isOwner);
								}
							}
						}).show();

				return true;
			}
		};

		void onClickUser(boolean isOwner) {
			if (isOwner) {
				Plat.accountService.logout();
			} else {
				List<Long> userIds = Lists.newArrayList();
				userIds.add(user.id);

				ProgressDialogHelper.setRunning(cx, true);
				Plat.deviceService.deleteDeviceUsers(owner.id, fan.getID(),
						userIds, new VoidCallback() {

							@Override
							public void onSuccess() {
								ProgressDialogHelper.setRunning(cx, false);
								layout.removeView(layout
										.findViewWithTag(UserViewItem.this));
							}

							@Override
							public void onFailure(Throwable t) {
								ProgressDialogHelper.setRunning(cx, false);
								ToastUtils.showThrowable(t);
							}
						});
			}
		}
	}
}
