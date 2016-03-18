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
//Handler handler = new Handler(){
//    @Override
//    public void handleMessage(Message msg) {
//        switch (msg.what){
//            case 0x123:
//                wv1.setDefault(1);
//                break;
//            default:break;
//        }
//    }
//};
    private void init(Context cx, AttributeSet attrs, String s) {

        LayoutInflater.from(cx).inflate(R.layout.device_oven_normal_setting_two_wheel, this,
                true);

        wv1 = (DeviceNormalSettingTemWheel) findViewById(R.id.wv1);
        wv2 = (DeviceNormalSettingTimeWheel) findViewById(R.id.wv2);
        imgKind = (ImageView) findViewById(R.id.imgKind);
        txtKind = (TextView) findViewById(R.id.txtKind);
        type = s;

        if (s.equals("鸡翅")) {
            list.add(180);
            for (int i = 14; i <= 23; i++) {
                list1.add(i);
            }
            wv1.setData(list);
//            handler.sendEmptyMessage(0x123);

            wv2.setDefault(3);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_chicken_wing_unworking));
            txtKind.setText("烤" + s);
        } else if (s.equals("蛋糕")) {
            for (int i = 160; i <= 160; i++) {
                list.add(i);
            }
            for (int i = 23; i <= 28; i++) {
                list1.add(i);
            }
            wv2.setDefault(3);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_cake_unworking));
            txtKind.setText("烤" + s);
        } else if (s.equals("面包")) {
            for (int i = 165; i <= 165; i++) {
                list.add(i);
            }
            for (int i = 15; i <= 22; i++) {
                list1.add(i);
            }
            wv2.setDefault(4);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_bread_unworking));
            txtKind.setText("烤" + s);
        } else if (s.equals("五花肉")) {
            for (int i = 215; i <= 215; i++) {
                list.add(i);
            }
            for (int i = 45; i <= 50; i++) {
                list1.add(i);
            }
            wv2.setDefault(1);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_streaky_pork_unworking));
            txtKind.setText("烤" + s);
        } else if (s.equals("牛排")) {
            for (int i = 180; i <= 180; i++) {
                list.add(i);
            }
            for (int i = 13; i <= 20; i++) {
                list1.add(i);
            }
            wv2.setDefault(3);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_steak_unworking));
            txtKind.setText("烤" + s);
        } else if (s.equals("披萨")) {
            for (int i = 200; i <= 200; i++) {
                list.add(i);
            }
            for (int i = 16; i <= 25; i++) {
                list1.add(i);
            }
            wv2.setDefault(5);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_pisa_unworking));
            txtKind.setText("烤" + s);
        } else if (s.equals("海鲜")) {
            for (int i = 200; i <= 200; i++) {
                list.add(i);
            }
            for (int i = 20; i <= 25; i++) {
                list1.add(i);
            }
            wv2.setDefault(4);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_seafood_unworking));
            txtKind.setText("烤" + s);
        } else if (s.equals("饼干")) {
            for (int i = 170; i <= 170; i++) {
                list.add(i);
            }
            for (int i = 12; i <= 20; i++) {
                list1.add(i);
            }
            wv2.setDefault(5);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_cookie_unworking));
            txtKind.setText("烤" + s);
        } else if (s.equals("蔬菜")) {
            for (int i = 200; i <= 200; i++) {
                list.add(i);
            }
            for (int i = 15; i <= 30; i++) {
                list1.add(i);
            }
            wv2.setDefault(1);
            imgKind.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_vegetable_unworking));
            txtKind.setText("烤" + s);
        }
        wv1.setDefault(1);

    }

    public NormalModeItemMsg getSelected() {
        NormalModeItemMsg msg = new NormalModeItemMsg();
        msg.setTemperature(wv1.getSelectedText());
        msg.setTime(wv2.getSelectedText());
        msg.setType(type);
        return msg;
    }

}

