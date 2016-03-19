package com.robam.roki.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.ui.ext.views.WheelView;
import com.robam.roki.R;
import com.robam.roki.model.NormalModeItemMsg;

import java.util.List;

/**
 * Created by linxiaobin on 2015/12/22.
 */
public class DeviceOvenNormalSettingWheelView extends FrameLayout {

    private List<Integer> list = Lists.newArrayList();
    private List<Integer> list1 = Lists.newArrayList();
    private DeviceNormalSettingTemWheel wv1;
    private DeviceNormalSettingTimeWheel wv2;
    private ImageView imgKind;
    private TextView txtKind;
    private String type;

    public DeviceOvenNormalSettingWheelView(Context cx, String index) {
        super(cx);
        init(cx, null, index);
        wv2.setOnSelectListener(selectListener);
    }

    public DeviceOvenNormalSettingWheelView(Context context, AttributeSet attrs, String index) {
        super(context, attrs);
        init(context, attrs, index);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadData();
    }

    private void loadData() {
        wv1.setData(list);
        wv2.setData(list1);
    }

    private void init(Context cx, AttributeSet attrs, String s) {

        LayoutInflater.from(cx).inflate(R.layout.device_oven_normal_setting_two_wheel, this,
                true);

        wv1 = (DeviceNormalSettingTemWheel) findViewById(R.id.wv1);
        wv2 = (DeviceNormalSettingTimeWheel) findViewById(R.id.wv2);
        imgKind = (ImageView) findViewById(R.id.imgKind);
        txtKind = (TextView) findViewById(R.id.txtKind);
        type = s;
        if (type.equals("鸡翅")) {
            list.add(180);
            list1.clear();
            for (int i = 14; i <= 23; i++) {
                list1.add(i);
            }
            wv1.setData(list);
            wv2.setData(list1);
            wv2.setDefault(5);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_chicken_wing_unworking));
            txtKind.setText("烤" + s);
        } else if (type.equals("蛋糕")) {
            list.add(160);
            wv1.setData(list);
            list1.clear();
            for (int i = 23; i <= 28; i++) {
                list1.add(i);
            }
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_cake_unworking));
            txtKind.setText("烤" + s);
        } else if (type.equals("面包")) {
            list.add(165);
            wv1.setData(list);
            list1.clear();
            for (int i = 15; i <= 22; i++) {
                list1.add(i);
            }
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_bread_unworking));
            txtKind.setText("烤" + s);
        } else if (type.equals("五花肉")) {
            list.add(215);
            list1.clear();
            for (int i = 45; i <= 50; i++) {
                list1.add(i);
            }
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_streaky_pork_unworking));
            txtKind.setText("烤" + s);
        } else if (type.equals("牛排")) {
            list.add(180);
            wv1.setData(list);
            list1.clear();
            for (int i = 13; i <= 20; i++) {
                list1.add(i);
            }
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_steak_unworking));
            txtKind.setText("烤" + s);
        } else if (type.equals("披萨")) {
            list.add(200);
            wv1.setData(list);
            list1.clear();
            for (int i = 16; i <= 25; i++) {
                list1.add(i);
            }
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_pisa_unworking));
            txtKind.setText("烤" + s);
        } else if (type.equals("海鲜")) {
            list.add(200);
            wv1.setData(list);
            list1.clear();
            for (int i = 20; i <= 25; i++) {
                list1.add(i);
            }
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_seafood_unworking));
            txtKind.setText("烤" + s);
        } else if (type.equals("饼干")) {
            list.add(170);
            wv1.setData(list);
            list1.clear();
            for (int i = 12; i <= 20; i++) {
                list1.add(i);
            }
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_cookie_unworking));
            txtKind.setText("烤" + s);
        } else if (type.equals("蔬菜")) {
            list.add(200);
            wv1.setData(list);
            list1.clear();
            for (int i = 15; i <= 30; i++) {
                list1.add(i);
            }
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_vegetable_unworking));
            txtKind.setText("烤" + s);
        }


//        wv1.setDefault(1);

    }

    public NormalModeItemMsg getSelected() {
        NormalModeItemMsg msg = new NormalModeItemMsg();
        msg.setTemperature(wv1.getSelectedText());
        msg.setTime(wv2.getSelectedText());
        msg.setType(type);
        return msg;
    }

    DeviceNormalSettingTimeWheel.OnSelectListener selectListener = new DeviceNormalSettingTimeWheel.OnSelectListener() {
        @Override
        public void endSelect(int index, Object item) {
            wv1.setData(list);
            wv2.setData(list1);
            int num = 5;
            if (type.equals("鸡翅"))
                wv2.setDefault(num);
            if (type.equals("蛋糕"))
                wv2.setDefault(3);
            if (type.equals("饼干"))
                wv2.setDefault(5);
            if (type.equals("面包"))
                wv2.setDefault(4);
            if (type.equals("牛排"))
                wv2.setDefault(4);
            if (type.equals("五花肉"))
                wv2.setDefault(1);
            if (type.equals("披萨"))
                wv2.setDefault(5);
            if (type.equals("蔬菜"))
                wv2.setDefault(1);
            if (type.equals("海鲜"))
                wv2.setDefault(4);
        }
        @Override
        public void selecting(int index, Object item) {

        }
    };

}

