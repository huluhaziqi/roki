package com.robam.rokipad.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.legent.utils.graphic.collection.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.robam.common.Utils;
import com.robam.common.events.DeviceDeleteEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.dialog.AddDeviceDialog;
import com.robam.rokipad.ui.dialog.AddUserDialog;
import com.robam.rokipad.ui.dialog.DeleteControlDialog;
import com.robam.rokipad.ui.dialog.FullScreenImageDialog;
import com.robam.rokipad.ui.dialog.LogoutDialog;

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

	@InjectView(R.id.fraMain)
	LinearLayout fraMain;

	@InjectView(R.id.linAddDevice)
	LinearLayout linAddDevice;

	Context cx;
	User owner;
	AbsFan fan;
	AbsSteamoven steam;

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
		steam = Utils.getDefaultSteam();

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_setting_account, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);

			addUserView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UIService.getInstance().postPage(PageKey.GuideLogin);
				}
			});
			linAddDevice.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					AddDeviceDialog.show(getContext());
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
			User user = users.get(0);
			view = inflater.inflate(R.layout.view_setting_account_item,
					this, false);
			item = new UserViewItem(view, user);
			view.setTag(item);

			layout.addView(view);
		} else {
			layout.addView(addUserView);
		}

		UserDeviceItem device;
		List<AbsDevice> devices = Plat.deviceService.queryDevices();
		if (devices == null && devices.size() == 0) {
			return;
		}

		fraMain.removeAllViews();
		for (final AbsDevice fan : devices) {
			if (fan.getName().startsWith("油烟机")) {
				view = inflater.inflate(R.layout.view_setting_account_device_item, this, false);
				view.findViewById(R.id.txtShare).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
//					FullScreenImageDialog.show(getContext(), "烤箱");
						addAccount(fan, fan.getName());
					}
				});
				device = new UserDeviceItem(view, fan, 0);
				view.setTag(device);
				fraMain.addView(view);
				view = inflater.inflate(R.layout.view_yellow_line, this, false);
				fraMain.addView(view);
			}
		}
		for (int i = 0; i < devices.size(); i++) {
			final AbsDevice absDevice = devices.get(i);
			if (absDevice.getName().startsWith("油烟机")) {
				continue;
			}
			view = inflater.inflate(R.layout.view_setting_account_device_item, this, false);
			view.findViewById(R.id.delete).
					setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							DeleteControlDialog.show(getContext(), "烤箱", "asdfasdf", new DeleteControlDialog.OnConfirmListener() {
								@Override
								public void onConfirm() {
									ToastUtils.show("不能删除油烟机", Toast.LENGTH_SHORT);
								}
							});
						}
					});
			view.findViewById(R.id.txtShare).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
//					FullScreenImageDialog.show(getContext(), "烤箱");
					addAccount(absDevice, absDevice.getName());
				}
			});
//			((SwipeLayout) view).setSwipeEnabled(false);
			device = new UserDeviceItem(view, absDevice, i + 1);
			view.setTag(device);
			fraMain.addView(view);
			view = inflater.inflate(R.layout.view_yellow_line, this, false);
			fraMain.addView(view);
		}

	}

	private void addAccount(AbsDevice device, final String name) {
		if (!Plat.accountService.isLogon())
			return;

		if (device == null) {
			ToastUtils.showShort(getContext().getResources().getString(
					R.string.unconnect_toast));
			return;
		}

		Plat.deviceService.getSnForDevice(owner.id, device.getID(),
				new Callback<String>() {

					@Override
					public void onSuccess(String sn) {
						if (Strings.isNullOrEmpty(sn)) {
							ToastUtils.showShort("获取设备串码失败");
							return;
						}

						FullScreenImageDialog dlg = FullScreenImageDialog.show(getContext(), sn, name);
//						AddUserDialog dlg = AddUserDialog
//								.show(getContext(), sn);
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

	class UserDeviceItem {

		@InjectView(R.id.txtAlreadyHas)
		TextView txtAlreadyHas;
		@InjectView(R.id.position)
		ImageView icon;
		@InjectView(R.id.txtDevice)
		TextView txtDevice;
		@InjectView(R.id.delete)
		TextView delete;

		View view;
		AbsDevice device;
		int position;

		public UserDeviceItem(View view, AbsDevice device, int position) {
			this.view = view;
			this.device = device;
			this.position = position;
			ButterKnife.inject(this, view);
			init();
		}

		void init() {
			if (position == 0) {
				txtAlreadyHas.setVisibility(View.VISIBLE);
			}
			String name = device.getName();
			if (name.startsWith("油烟")) {
				icon.setImageResource(R.mipmap.img_device_manager_fan);
				txtDevice.setText("油烟机9700+电磁灶9W70");
			} else if (name.startsWith("消毒")) {
				icon.setImageResource(R.mipmap.img_device_manager_sterilizer);
				txtDevice.setText("消毒柜829");
			} else if (name.startsWith("电烤箱")) {
				icon.setImageResource(R.mipmap.img_device_manager_oven);
				txtDevice.setText("电烤箱R039");
			} else if (name.startsWith("蒸汽")) {
				icon.setImageResource(R.mipmap.img_device_manager_steam);
				txtDevice.setText("蒸汽炉S209");
			}
			if (position != 0) {
				view.setOnLongClickListener(listener);
			} else {
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						ToastUtils.show("不能删除油烟机", Toast.LENGTH_SHORT);
					}
				});
			}
		}

		OnLongClickListener listener = new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				DeleteControlDialog.show(getContext(), device.getName(), device.getID(), new DeleteControlDialog.OnConfirmListener() {
					@Override
					public void onConfirm() {
						deleteDevice(device.getID());
					}
				});
				return true;
			}
		};

		void deleteDevice(String id) {
			Plat.deviceService.deleteWithUnbind(id, new VoidCallback() {
				@Override
				public void onSuccess() {
					fraMain.removeView(findViewWithTag(UserDeviceItem.this));
				}

				@Override
				public void onFailure(Throwable t) {

				}
			});
		}
	}

	class UserViewItem {

		@InjectView(R.id.img_account)
		ImageView imgFigure;

		@InjectView(R.id.tv_name)
		TextView txtName;

		@InjectView(R.id.tv_phone)
		TextView txtPhone;

		@InjectView(R.id.txtLogout)
		TextView txtLogout;

		View view;
		User user;
		boolean isOwner;

		public UserViewItem(View view, User user) {
			this.view = view;
			this.user = user;
			ButterKnife.inject(this, view);
			view.setClickable(false);
			isOwner = user.id == Plat.accountService.getCurrentUserId();

			txtName.setText(user.name);
			txtPhone.setText(user.phone);

			ImageUtils.displayImage(user.figureUrl, imgFigure, opt);

			txtLogout.setOnClickListener(onClickListener);
		}

		View.OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				LogoutDialog.show(getContext(), new DeleteControlDialog.OnConfirmListener() {
					@Override
					public void onConfirm() {
						onClickUser(true);
					}
				});
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
