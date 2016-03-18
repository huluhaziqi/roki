package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.model.NormalModeItemMsg;

import java.util.List;

/**
 * Created by linxiaobin on 2016/1/18.
 */
public class OvenSettingThreeWheelView extends FrameLayout {

    protected DeviceOvenModeWheel wv1;
    protected DeviceOvenTemWheel wv2;
    protected DeviceOvenTimeWheel wv3;
    protected TextView proText;
    protected TextView proTextInfo;

    public OvenSettingThreeWheelView(Context context) {
        super(context);
        init(context, null);
    }

    public OvenSettingThreeWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OvenSettingThreeWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x03) {
                proText.setText(wv1.getSelectedText());
                if (wv1.getSelectedText().toString().equals("快热"))
                    proTextInfo.setText("快热模式适合烤蛋挞、玉米、葱油饼。");
                else if (wv1.getSelectedText().toString().equals("风焙烤"))
                    proTextInfo.setText("风焙烤模式适合烤一些成品食物，如：肉串、薯条。");
                else if (wv1.getSelectedText().toString().equals("焙烤"))
                    proTextInfo.setText("焙烤模式特别适合烘焙，主要烤蛋糕、饼干、奶黄包。");
                else if (wv1.getSelectedText().toString().equals("风扇烤"))
                    proTextInfo.setText("风扇烤适合烤鸭，烤鸡，也适合烤牛排、猪排、五花肉、培根、翅中、鸡腿。");
                else if (wv1.getSelectedText().toString().equals("烧烤"))
                    proTextInfo.setText("烧烤模式适合烤猪排、肉串、香肠、翅根。");
                else if (wv1.getSelectedText().toString().equals("强烧烤"))
                    proTextInfo.setText("强烧烤功能强大，适合烤：牛排、香肠、培根、鸡肉、翅中、翅根、鸡腿、肉串、烤鱼。");
                else if (wv1.getSelectedText().toString().equals("解冻"))
                    proTextInfo.setText("尤其适合禽类和肉类的解冻。");
                else if (wv1.getSelectedText().toString().equals("底加热"))
                    proTextInfo.setText("适合加热餐点、燉干汤汁、制作果酱，也适合烘烤浅色糕点。");
            }
        }
    };
                @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        loadData();
    }

    void init(Context cx, AttributeSet attrs) {

        LayoutInflater.from(cx).inflate(R.layout.view_oven_image_three_wheel, this,
                true);

        wv1 = (DeviceOvenModeWheel) findViewById(R.id.wv1);
        wv2 = (DeviceOvenTemWheel) findViewById(R.id.wv2);
        wv3 = (DeviceOvenTimeWheel) findViewById(R.id.wv3);
        proText = (TextView) findViewById(R.id.proText);
        proTextInfo = (TextView) findViewById(R.id.proTextInfo);

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


    DeviceOvenModeWheel.OnSelectListener wv1_Listener = new DeviceOvenModeWheel.OnSelectListener() {
        @Override
        public void endSelect(int index, Object item) {
            List<?> list1 = getList2(item);
            List<?> list2 = getList3(item);
            wv2.setData(list1);
            wv3.setData(list2);
            int def1 = 0, def2 = 0;
            if (index == 0) {
                def1 = 150;
                def2 = 45;
            } else if (index == 1) {
                def1 = 150;
                def2 = 55;
            } else if (index == 2) {
                def1 = 110;
                def2 = 55;
            } else if (index == 3) {
                def1 = 145;
                def2 = 45;
            } else if (index == 4) {
                def1 = 10;
                def2 = 15;
            } else if (index == 5) {
                def1 = 170;
                def2 = 55;
            } else if (index == 6) {
                def1 = 130;
                def2 = 45;
            } else if (index == 7) {
                def1 = 130;
                def2 = 35;
            }
            wv2.setDefault(def1);
            wv3.setDefault(def2);
            handler.sendEmptyMessage(0x03);
        }

        @Override
        public void selecting(int index, Object item) {
        }
    };

    public NormalModeItemMsg getSelected() {
        NormalModeItemMsg msg = new NormalModeItemMsg();
        msg.setTemperature(wv2.getSelectedText());
        msg.setTime(wv3.getSelectedText());
        msg.setType(wv1.getSelectedText());
        return msg;
    }

    protected List<?> getList2(Object item) {
        List<Integer> list = Lists.newArrayList();
        String s = (String) item;
        if (s.equals("焙烤")) {
            for (int i = 50; i <= 180; i++) {
                list.add(i);
            }
        } else if (s.equals("底加热")) {
            for (int i = 15; i <= 80; i++) {
                list.add(i);
            }
        } else if (s.equals("快热") || s.equals("风焙烤") || s.equals("解冻")
                || s.equals("风扇烤") || s.equals("烧烤") || s.equals("强烧烤")) {
            for (int i = 50; i <= 230; i++) {
                list.add(i);
            }
        }
        return list;
    }

    protected List<?> getList3(Object item) {
        List<Integer> list = Lists.newArrayList();
        String s = (String) item;
        if (s.equals("快热") || s.equals("风焙烤") || s.equals("解冻") || s.equals("风扇烤")
                || s.equals("烧烤") || s.equals("强烧烤") || s.equals("焙烤") || s.equals("底加热")) {
            for (int i = 5; i <= 90; i++) {
                list.add(i);
            }
        }
        return list;
    }

}
