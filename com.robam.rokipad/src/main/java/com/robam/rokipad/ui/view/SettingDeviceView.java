package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.plat.pojos.device.DeviceGuid;
import com.legent.plat.pojos.device.IDevice;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.device.fan.Fan9700;
import com.robam.common.pojos.device.fan.IFan;
import com.robam.rokipad.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingDeviceView extends FrameLayout {

    @InjectView(R.id.deviceList)
    ListView deviceList;

    Adapter adapter;

    public SettingDeviceView(Context context) {
        super(context);
        init(context, null);
    }

    public SettingDeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingDeviceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(
                R.layout.view_setting_device, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            adapter = new Adapter();
            deviceList.setAdapter(adapter);
            refresh();
        }
    }

    void refresh() {
//        AbsFan fan = Utils.getDefaultFan();
//        Stove stove = Utils.getDefaultStove();
        List<IDevice> list = Lists.newArrayList();
//        if (fan == null) {
//            ToastUtils.showShort(R.string.dev_invalid_error);
//        } else {
//            list.add(fan);
//            if (stove != null) {
//                list.add(stove);
//            }
//        }
        list = Plat.deviceService.queryDevices();
        adapter.loadData(list);
    }

    class Adapter extends BaseAdapter {

        List<IDevice> list = Lists.newArrayList();

        public void loadData(List<IDevice> devices) {
            list.clear();
//            if (devices != null) {
//                list.addAll(devices);
//            }
            Log.e("steam", devices.size() + "");
            for (int i = 0; i < devices.size(); i++) {
                IDevice d = devices.get(i);
                if (d instanceof IFan) {
                    devices.remove(i);
                    list.add(d);
                    break;
                }
            }
            list.addAll(devices);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (position == 0) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_setting_device_item0, parent, false);
                final AbsFan fan = Utils.getDefaultFan();
                Stove stove = fan.getChild();
                Log.e("steam", fan.getName());
//                Log.e("steam", stove.toString());
                RelativeLayout fanView = (RelativeLayout) view.findViewById(R.id.fanView);
                RelativeLayout stoveView = (RelativeLayout) view.findViewById(R.id.stoveView);
                stoveView.setVisibility(View.GONE);
                ((TextView) fanView.findViewById(R.id.txtNum)).setText("编码 " + fan.getBid());
                ((TextView) fanView.findViewById(R.id.txtType)).setText("型号 " + fan.getGuid());
                ((TextView) fanView.findViewById(R.id.txtVersion)).setText("固件版本 " + fan.getVersion());
//                ((TextView) stoveView.findViewById(R.id.txtNum)).setText("编码 " + stove.getBid());
//                ((TextView) stoveView.findViewById(R.id.txtType)).setText("型号 " + stove.getGuid());
//                ((TextView) stoveView.findViewById(R.id.txtVersion)).setText("固件版本 " + stove.getVersion());
                final LinearLayout linUser = (LinearLayout) view.findViewById(R.id.linUser);
                final List<User> users = Lists.newArrayList();
                long userId = Plat.accountService.getCurrentUserId();
                Plat.deviceService.getDeviceUsers(userId, fan.getID(),
                        new Callback<List<User>>() {

                            @Override
                            public void onSuccess(List<User> result) {
                                if (result != null) {
//                                    if (result.contains(owner)) {
//                                        result.remove(owner);
//                                    }
                                    users.addAll(result);
                                }
                                    for (final User user : users) {
                                        final List<Long> userIds = Lists.newArrayList();
                                        userIds.add(user.id);
                                        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_setting_device_item_user, SettingDeviceView.this, false);
                                        view.setTag(user.id);
                                        ((TextView) view.findViewById(R.id.tv_name)).setText(user.getName());
                                        ((TextView) view.findViewById(R.id.tv_phone)).setText(user.getAccount());
                                        view.findViewById(R.id.txtLogout).setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Plat.deviceService.deleteDeviceUsers(user.id, fan.getID(), userIds, new VoidCallback() {

                                                    @Override
                                                    public void onSuccess() {
                                                        linUser.removeView(findViewWithTag(user.id));
                                                        ProgressDialogHelper.setRunning(getContext(), false);
                                                    }

                                                    @Override
                                                    public void onFailure(Throwable t) {
                                                        ProgressDialogHelper.setRunning(getContext(), false);
                                                        ToastUtils.showThrowable(t);
                                                    }
                                                });
                                            }
                                        });
                                        linUser.addView(view);
                                    }
                                ProgressDialogHelper.setRunning(getContext(), false);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ProgressDialogHelper.setRunning(getContext(), false);
                                ToastUtils.showThrowable(t);
                            }
                        });
                return view;
            } else {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.view_setting_device_item, parent, false);
                    vh = new ViewHolder(convertView);
                    convertView.setTag(vh);
                } else {
                    vh = (ViewHolder) convertView.getTag();
                }

                IDevice device = list.get(position);
                vh.showDevice(device);
                return convertView;
            }
        }

        class ViewHolder {
            @InjectView(R.id.linDevice)
            LinearLayout linDevice;
            @InjectView(R.id.linUser)
            LinearLayout linUser;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }

            public void showDevice(final IDevice device) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_setting_device_item_device, SettingDeviceView.this, false);
                String bid = device.getBid();
                DeviceGuid guid = device.getGuid();
                int version = device.getVersion();
                ((TextView) view.findViewById(R.id.txtNum)).setText("编码 " + bid);
                ((TextView) view.findViewById(R.id.txtType)).setText("型号 " + guid);
                ((TextView) view.findViewById(R.id.txtVersion)).setText("固件版本 " + version);
                ((TextView) view.findViewById(R.id.txtName)).setText(device.getName());
                ((ImageView) view.findViewById(R.id.img)).setImageResource(getImageRes(device.getName()));
                linDevice.removeAllViews();
                linDevice.addView(view);

                final List<User> users = Lists.newArrayList();
                long userId = Plat.accountService.getCurrentUserId();
                Plat.deviceService.getDeviceUsers(userId, device.getID(),
                        new Callback<List<User>>() {

                            @Override
                            public void onSuccess(List<User> result) {
                                if (result != null) {
                                    users.addAll(result);
                                }
                                buildViews(users, device);
                                ProgressDialogHelper.setRunning(getContext(), false);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ProgressDialogHelper.setRunning(getContext(), false);
                                ToastUtils.showThrowable(t);
                            }
                        });
            }

            void buildViews(List<User> users, final IDevice device) {
                for (final User user : users) {
                    final List<Long> userIds = Lists.newArrayList();
                    userIds.add(user.id);
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.view_setting_device_item_user, SettingDeviceView.this, false);
                    ((TextView) view.findViewById(R.id.tv_name)).setText(user.getName());
                    ((TextView) view.findViewById(R.id.tv_phone)).setText(user.getAccount());
                    view.setTag(user.id);
                    view.findViewById(R.id.txtLogout).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Plat.deviceService.deleteDeviceUsers(user.id, device.getID(), userIds, new VoidCallback() {

                                @Override
                                public void onSuccess() {
                                    linUser.removeView(findViewWithTag(user.id));
                                    ProgressDialogHelper.setRunning(getContext(), false);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    ProgressDialogHelper.setRunning(getContext(), false);
                                    ToastUtils.showThrowable(t);
                                }
                            });
                        }
                    });
                    linUser.addView(view);
                }
            }

            private int getImageRes(String name) {
                int res = -1;
                if (name.startsWith("油烟机")) {
                    res = R.mipmap.img_device_detail_fan;
                } else if (name.startsWith("蒸汽炉")) {
                    res = R.mipmap.img_device_detail_steam;
                } else if (name.startsWith("电烤箱")) {
                    res = R.mipmap.img_device_detail_oven;
                }
                return res;
            }
        }
    }

}
