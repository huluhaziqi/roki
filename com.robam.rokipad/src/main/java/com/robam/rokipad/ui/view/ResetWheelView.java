package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.google.common.collect.Lists;
import com.legent.ui.ext.views.AbsTwoWheelView;
import com.robam.rokipad.model.DeviceWorkMsg;

import java.util.List;

/**
 * Created by WZTCM on 2015/12/21.
 */
public class ResetWheelView extends AbsTwoWheelView {

    public ResetWheelView(Context cx) {
        super(cx);
    }

    public ResetWheelView(Context cx, AttributeSet attrs) {
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

    public DeviceWorkMsg getSelected() {
        DeviceWorkMsg msg = new DeviceWorkMsg();
        msg.setTemperature(wv1.getSelectedText());
        msg.setTime(wv2.getSelectedText());
        return msg;
    }
}
