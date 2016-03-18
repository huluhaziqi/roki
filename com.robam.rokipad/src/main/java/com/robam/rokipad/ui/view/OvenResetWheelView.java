package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.google.common.collect.Lists;
import com.legent.ui.ext.views.AbsTwoWheelView;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.model.NormalModeItemMsg;

import java.util.List;

/**
 * Created by linxiaobin on 2016/1/18.
 */
public class OvenResetWheelView extends AbsTwoWheelView {

    public OvenResetWheelView(Context cx) {
        super(cx);
    }

    public OvenResetWheelView(Context cx, AttributeSet attrs) {
        super(cx, attrs);
    }

    @Override
    protected void loadData() {
        super.loadData();
    }

    @Override
    protected List<?> getList2(Object item) {
        List<Integer> list = Lists.newArrayList();
        for (int i = 5; i <= 60; i++) {
            list.add(i);
        }
        return list;
    }

    @Override
    protected List<?> getList1() {
        List<Integer> list = Lists.newArrayList();
        for (int i = 50; i <= 100; i++) {
            list.add(i);
        }
        return list;
    }

    public NormalModeItemMsg getSelected() {
        NormalModeItemMsg msg = new NormalModeItemMsg();
        msg.setTemperature(wv1.getSelectedText());
        msg.setTime(wv2.getSelectedText());
        return msg;
    }
}
