package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.google.common.collect.Lists;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;

import java.util.List;

/**
 * Created by WZTCM on 2015/12/19.
 */
public class SteamSettingThreeWheelView extends FrameLayout {

    protected ImageWheelView wv1;
    protected TemlWheelView wv2;
    protected TimeWheelView wv3;

    public SteamSettingThreeWheelView(Context context) {
        super(context);
        init(context, null);
    }

    public SteamSettingThreeWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SteamSettingThreeWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    void init(Context cx, AttributeSet attrs) {

        LayoutInflater.from(cx).inflate(R.layout.view_image_three_wheel, this,
                true);

        wv1 = (ImageWheelView) findViewById(R.id.wv1);
        wv2 = (TemlWheelView) findViewById(R.id.wv2);
        wv3 = (TimeWheelView) findViewById(R.id.wv3);

        wv1.setOnSelectListener(wv1_Listener);
        wv1.setDefault(0);
    }

    public <T> T getSelectedItem1() {
        return (T) wv1.getSelectedTag();
    }

    public <T> T getSelectedItem2() {
        return (T) wv2.getSelectedTag();
    }

    public <T> T getSelectedItem3() {
        return (T) wv3.getSelectedTag();
    }


    ImageWheelView.OnSelectListener wv1_Listener = new ImageWheelView.OnSelectListener() {
        @Override
        public void endSelect(int index, Object item) {
            List<?> list1 = getList2(item);
            List<?> list2 = getList3(item);
            wv2.setData(list1);
            wv3.setData(list2);
            int def1 = 0, def2 = 0;
            if (index == 0) {
                def1 = 3;
                def2 = 35;
            } else if (index == 1) {
                def1 = 10;
                def2 = 20;
            } else if (index == 2) {
                def1 = 15;
                def2 = 40;
            } else if (index == 3 || index == 4) {
                def1 = 10;
                def2 = 25;
            } else if (index == 5) {
                def1 = 5;
                def2 = 25;
            }
            else if (index == 6) {
                def1 = 10;
                def2 = 55;
            } else if (index == 7) {
                def1 = 0;
                def2 = 35;
            }
            wv2.setDefault(def1);
            wv3.setDefault(def2);
        }

        @Override
        public void selecting(int index, Object item) {
        }
    };

    public DeviceWorkMsg getSelected() {
        DeviceWorkMsg msg = new DeviceWorkMsg();
        msg.setTemperature(wv2.getSelectedText());
        msg.setTime(wv3.getSelectedText());
        msg.setType(wv1.getSelectedText());
        return msg;
    }

    protected List<?> getList2(Object item) {
        List<Integer> list = Lists.newArrayList();
        String s = (String) item;
        if (s.equals("蔬菜")) {
            for (int i = 95; i <= 100; i++) {
                list.add(i);
            }
        } else if (s.equals("水蒸蛋")) {
            for (int i = 85; i <= 95; i++) {
                list.add(i);
            }
        } else if (s.equals("肉类")) {
            for (int i = 85; i <= 100; i++) {
                list.add(i);
            }
        } else if (s.equals("海鲜")) {
            for (int i = 75; i <= 95; i++) {
                list.add(i);
            }
        } else if (s.equals("糕点")) {
            for (int i = 85; i <= 95; i++) {
                list.add(i);
            }
        } else if (s.equals("面条")) {
            for (int i = 85; i <= 95; i++) {
                list.add(i);
            }
        } else if (s.equals("蹄筋")) {
            for (int i = 90; i <= 100; i++) {
                list.add(i);
            }
        } else if (s.equals("解冻")) {
            for (int i = 55; i <= 65; i++) {
                list.add(i);
            }
        }
        return list;
    }

    protected List<?> getList3(Object item) {
        List<Integer> list = Lists.newArrayList();
        String s = (String) item;
        if (s.equals("蔬菜")) {
            for (int i = 5; i <= 45; i++) {
                list.add(i);
            }
        } else if (s.equals("水蒸蛋")) {
            for (int i = 5; i <= 45; i++) {
                list.add(i);
            }
        } else if (s.equals("肉类")) {
            for (int i = 5; i <= 60; i++) {
                list.add(i);
            }
        } else if (s.equals("海鲜")) {
            for (int i = 5; i <= 45; i++) {
                list.add(i);
            }
        } else if (s.equals("糕点")) {
            for (int i = 5; i <= 45; i++) {
                list.add(i);
            }
        } else if (s.equals("面条")) {
            for (int i = 5; i <= 45; i++) {
                list.add(i);
            }
        } else if (s.equals("蹄筋")) {
            for (int i = 5; i <= 60; i++) {
                list.add(i);
            }
        } else if (s.equals("解冻")) {
            for (int i = 5; i <= 60; i++) {
                list.add(i);
            }
        }
        return list;
    }

}
