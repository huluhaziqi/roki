package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.legent.plat.Plat;
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.DeviceAddEvent;
import com.robam.rokipad.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rosicky on 16/1/6.
 */
public class AddDeviceDialog extends AbsDialog {

    @InjectView(R.id.txtTitle)
    TextView txtTitle;
    @InjectView(R.id.gridview)
    GridView gridview;

    GridViewAdapter adapter;

    DeleteControlDialog.OnConfirmListener listener;

    public AddDeviceDialog(Context context) {
        super(context, R.style.dialog);
    }

    static public void show(Context cx) {
        AddDeviceDialog dlg = new AddDeviceDialog(cx);
        dlg.show();
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_device_add;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
        gridview.setAdapter(new GridViewAdapter(getContext()));
    }

    class GridViewAdapter extends BaseAdapter {
        Context context;
        List<AbsDevice> list;

        List<String> nameList = Lists.newArrayList();

        public GridViewAdapter(Context context) {
            list = Plat.deviceService.queryDevices();
            nameList.add("电烤箱");
            nameList.add("蒸汽炉");
            nameList.add("消毒柜");
            nameList.add("微波炉");
            for (AbsDevice abs : list) {
                if (abs.getName().startsWith("电烤箱")) {
                    nameList.remove("电烤箱");
                } else if (abs.getName().startsWith("蒸汽炉")) {
                    nameList.remove("蒸汽炉");
                } else if (abs.getName().startsWith("消毒柜")) {
                    nameList.remove("消毒柜");
                } else if (abs.getName().startsWith("微波炉")) {
                    nameList.remove("微波炉");
                }
            }
            this.context = context;
        }

        @Override
        public int getCount() {
            return nameList.size();
        }

        @Override
        public Object getItem(int i) {
//            return list.get(i);
            return nameList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_device_add_item, viewGroup, false);
//            ((TextView) view).setText(getItem(i).getName());
            ((TextView) view).setText((String) getItem(i));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    UIService.getInstance().returnHome();
                    EventUtils.postEvent(new DeviceAddEvent((String) getItem(i)));

                }
            });
            return view;
        }
    }
}
