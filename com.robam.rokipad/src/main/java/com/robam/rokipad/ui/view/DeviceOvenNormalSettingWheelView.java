package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.ui.ext.views.WheelView;
import com.robam.rokipad.R;
import com.robam.rokipad.model.NormalModeItemMsg;

import java.util.List;

/**
 * Created by linxiaobin on 2015/12/22.
 */
public class DeviceOvenNormalSettingWheelView extends FrameLayout {

    private List<Integer> list = Lists.newArrayList();
    private List<Integer> list1 = Lists.newArrayList();
    private WheelView wv2;
    private DeviceNormalSettingTemWheel wv1;

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

    private void init(Context cx, AttributeSet attrs, String s) {

        LayoutInflater.from(cx).inflate(R.layout.device_oven_normal_setting_two_wheel, this,
                true);

        wv1 = (DeviceNormalSettingTemWheel) findViewById(R.id.wv1);
        wv2 = (WheelView) findViewById(R.id.wv2);

        if (s.equals("鸡翅")) {
            for (int i = 180; i <= 180; i++) {
                list.add(i);
            }
            for (int i = 14; i <= 23; i++) {
                list1.add(i);
            }
            wv2.setDefault(3);
        } else if (s.equals("蛋糕")) {
            for (int i = 160; i <= 160; i++) {
                list.add(i);
            }
            for (int i = 23; i <= 28; i++) {
                list1.add(i);
            }
            wv2.setDefault(3);
        } else if (s.equals("面包")) {
            for (int i = 165; i <= 165; i++) {
                list.add(i);
            }
            for (int i = 15; i <= 22; i++) {
                list1.add(i);
            }
            wv2.setDefault(4);
        } else if (s.equals("五花肉")) {
            for (int i = 215; i <= 215; i++) {
                list.add(i);
            }
            for (int i = 45; i <= 50; i++) {
                list1.add(i);
            }
            wv2.setDefault(1);
        } else if (s.equals("牛排")) {
            for (int i = 180; i <= 180; i++) {
                list.add(i);
            }
            for (int i = 13; i <= 20; i++) {
                list1.add(i);
            }
            wv2.setDefault(3);
        } else if (s.equals("披萨")) {
            for (int i = 200; i <= 200; i++) {
                list.add(i);
            }
            for (int i = 16; i <= 25; i++) {
                list1.add(i);
            }
            wv2.setDefault(5);
        } else if (s.equals("海鲜")) {
            for (int i = 200; i <= 200; i++) {
                list.add(i);
            }
            for (int i = 20; i <= 25; i++) {
                list1.add(i);
            }
            wv2.setDefault(4);
        } else if (s.equals("饼干")) {
            for (int i = 170; i <= 170; i++) {
                list.add(i);
            }
            for (int i = 12; i <= 20; i++) {
                list1.add(i);
            }
            wv2.setDefault(5);
        } else if (s.equals("蔬菜")) {
            for (int i = 200; i <= 200; i++) {
                list.add(i);
            }
            for (int i = 15; i <= 30; i++) {
                list1.add(i);
            }
            wv2.setDefault(1);
        }
        wv1.setDefault(1);

    }

    public NormalModeItemMsg getSelected() {
        NormalModeItemMsg msg = new NormalModeItemMsg();
        msg.setTemperature(wv1.getSelectedText());
        msg.setTime(wv2.getSelectedText());
        return msg;
    }

}

