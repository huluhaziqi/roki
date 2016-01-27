package com.robam.roki.ui.page;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.plat.pojos.device.AbsDeviceHub;
import com.legent.plat.pojos.device.DeviceGuid;
import com.legent.plat.pojos.device.IDevice;
import com.legent.plat.pojos.dictionary.DeviceType;
import com.legent.plat.services.DeviceTypeManager;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.adapters.ExtBaseAdapter;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.ui.ext.views.NestedListView;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sylar on 15/6/14.
 */
public class DeviceDetailPage extends HeadPage {

    @InjectView(R.id.deviceListView)
    NestedListView deviceListView;
    @InjectView(R.id.userListView)
    NestedListView userListView;

    DeviceAdapter deviceAdapter;
    UserAdapter userAdapter;
    AbsDeviceHub deviceHub;

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        String guid = getArguments().getString(PageArgumentKey.Guid);
        deviceHub = Plat.deviceService.lookupChild(guid);
        titleBar.setTitle(deviceHub.getName());


        View view = layoutInflater.inflate(R.layout.page_device_detail, viewGroup, false);
        ButterKnife.inject(this, view);

        regsitRightView();
        deviceAdapter = new DeviceAdapter();
        userAdapter = new UserAdapter();

        deviceListView.setAdapter(deviceAdapter);
        userListView.setAdapter(userAdapter);

        initData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    void initData() {

        List<IDevice> devices = Lists.newArrayList();
        devices.add(deviceHub);
        Stove stove = deviceHub.getChild();                     //getChildByDeviceType(IRokiFamily.R9W70);
        if (stove != null) {
            devices.add(stove);
        }

        deviceAdapter.loadData(devices);

        long ownerId = Plat.accountService.getCurrentUserId();
        Plat.deviceService.getDeviceUsers(ownerId, deviceHub.getID(), new com.legent.Callback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                userAdapter.loadData(users);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void onUnbind() {
        DialogHelper.newDialog_OkCancel(cx, "确定删除此设备？", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ProgressDialogHelper.setRunning(cx, true);

                    Plat.deviceService.deleteWithUnbind(deviceHub.getID(), new com.legent.VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ProgressDialogHelper.setRunning(cx, false);
                            UIService.getInstance().popBack();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ProgressDialogHelper.setRunning(cx, false);
                            ToastUtils.showThrowable(t);
                        }
                    });
                }
            }
        }).show();
    }

    void regsitRightView() {

        TextView txtView = TitleBar.newTitleTextView(cx, "删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnbind();
            }
        });


        titleBar.replaceRight(txtView);
    }

    class DeviceAdapter extends ExtBaseAdapter<IDevice> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(cx).inflate(R.layout.view_device_detail, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            IDevice dev = list.get(position);
            vh.showData(dev);
            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.imgDevice)
            ImageView imgDevice;
            @InjectView(R.id.txtDevice)
            TextView txtDevice;
            @InjectView(R.id.txtDeviceType)
            TextView txtDeviceType;
            @InjectView(R.id.txtBid)
            TextView txtBid;
            @InjectView(R.id.txtOtaVer)
            TextView txtOtaVer;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }

            void showData(IDevice device) {
                DeviceGuid dg = device.getGuid();
                DeviceType dt = DeviceTypeManager.getInstance().getDeviceType(dg.getGuid());

                txtDeviceType.setText(Utils.getDeviceModel(dt));
                txtDevice.setText(dt.getName());
                txtBid.setText(dg.getDeviceNumber());//(device.getBid());
                txtOtaVer.setText(String.valueOf(device.getVersion()));

                if (Utils.isFan(device.getID())) {
                    imgDevice.setImageResource(R.mipmap.ic_device_detail_fan);
                } else if (Utils.isStove(device.getID())) {
                    imgDevice.setImageResource(R.mipmap.ic_device_detail_stove);
                }
            }
        }
    }

    class UserAdapter extends ExtBaseAdapter<User> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(cx).inflate(R.layout.view_device_user_item, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            User user = list.get(position);
            vh.showData(user);
            return convertView;
        }


        class ViewHolder {
            @InjectView(R.id.txtUserName)
            TextView txtUserName;
            @InjectView(R.id.txtDesc)
            TextView txtDesc;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }

            void showData(User user) {
                txtUserName.setText(user.name);
                txtDesc.setText(user.phone);
            }
        }
    }

}
