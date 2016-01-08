package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceCollectionChangedEvent;
import com.legent.plat.pojos.device.IDevice;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.adapters.ExtBaseAdapter;
import com.robam.common.Utils;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.view.EmojiEmptyView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by sylar on 15/6/14.
 */
public class DeviceManagerPage extends HeadPage {

    @InjectView(R.id.emptyView)
    EmojiEmptyView emptyView;
    @InjectView(R.id.mainView)
    LinearLayout mainView;
    @InjectView(R.id.listview)
    ListView listview;
    @InjectView(R.id.addDeviceView)
    LinearLayout addDeviceView;

    Adapter adapter;

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.page_device_manager, viewGroup, false);
        ButterKnife.inject(this, view);
        adapter = new Adapter();
        listview.setAdapter(adapter);
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Subscribe
    public void onEvent(DeviceCollectionChangedEvent event) {
        initData();
    }

    @OnClick(R.id.addDeviceView)
    public void onClickAdd() {
        UIService.getInstance().postPage(PageKey.DeviceAddByEasylink);
    }


    @OnItemClick(R.id.listview)
    public void onItemClickDevice(AdapterView<?> parent, View view, int position, long id) {
        IDevice dev = adapter.getEntity(position);
        Bundle bd = new Bundle();
        bd.putString(PageArgumentKey.Guid, dev.getID());
        UIService.getInstance().postPage(PageKey.DeviceDetail, bd);
    }

    void initData() {
        boolean isEmpty = Plat.deviceService.isEmpty();
        switchView(isEmpty);

        if (isEmpty) return;

        List<IDevice> list = Plat.deviceService.queryAll();
        adapter.loadData(list);
    }


    void switchView(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mainView.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
        addDeviceView.setEnabled(isEmpty);
    }


    class Adapter extends ExtBaseAdapter<IDevice> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(cx).inflate(R.layout.view_device_manager_item, parent, false);
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
            @InjectView(R.id.edtName)
            TextView txtName;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }

            void showData(IDevice dev) {
                String DeviceName;
                if (dev instanceof AbsFan){
                    DeviceName = "油烟机／灶具";
                }else {
                    DeviceName = dev.getDeviceType().getName();
                }
                txtName.setText(DeviceName);
            }
        }
    }
}
