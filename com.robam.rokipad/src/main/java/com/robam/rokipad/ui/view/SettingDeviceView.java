package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.plat.pojos.device.DeviceGuid;
import com.legent.plat.pojos.device.IDevice;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.device.fan.Fan9700;
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
        AbsFan fan = Utils.getDefaultFan();
        Stove stove = Utils.getDefaultStove();
        List<IDevice> list = Lists.newArrayList();
        if (fan == null) {
            ToastUtils.showShort(R.string.dev_invalid_error);
        } else {
            list.add(fan);
            if (stove != null) {
                list.add(stove);
            }
        }

        adapter.loadData(list);
    }

    class Adapter extends BaseAdapter {

        List<IDevice> list = Lists.newArrayList();

        public void loadData(List<IDevice> devices) {
            list.clear();
            if (devices != null) {
                list.addAll(devices);
            }
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

        class ViewHolder {

            @InjectView(R.id.imgDevice)
            ImageView imgDevice;

            @InjectView(R.id.txtModel)
            TextView txtModel;

            @InjectView(R.id.txtNumber)
            TextView txtNumber;

            @InjectView(R.id.txtVersion)
            TextView txtVersion;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }

            public void showDevice(IDevice device) {
                DeviceGuid guid = device.getGuid();
                String bid = device.getBid();
                txtModel.setText(guid.getDeviceTypeId());
                //txtNumber.setText(guid.getDeviceNumber());        //显示GUID byzhaiyuanyi
                txtNumber.setText(bid);                             //显示业务编码 bid
                txtVersion.setText(String.valueOf(device.getVersion()));
                boolean isFan = (device instanceof Fan9700);
                int imgResid = isFan ? R.mipmap.img_fan
                        : R.mipmap.img_stove;

                imgDevice.setImageResource(imgResid);
            }
        }
    }

}
