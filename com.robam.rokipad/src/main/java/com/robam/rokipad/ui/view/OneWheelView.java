package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.robam.rokipad.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WZTCM on 2015/12/21.
 */
public class OneWheelView extends FrameLayout {

    protected TimeWheelView wheelView;

    public OneWheelView(Context cx) {
        super(cx);
        init(cx, null);
    }

    public OneWheelView(Context cx, AttributeSet attrs) {
        super(cx, attrs);
        init(cx, attrs);
    }

    void init(Context cx, AttributeSet attrs) {
        LayoutInflater.from(cx).inflate(R.layout.one_wheel_view, this,
                true);

        wheelView = (TimeWheelView) findViewById(R.id.wheelview);
        List<String> list = new ArrayList<String>();
        list.add("01");
        list.add("02");
        list.add("03");
        list.add("04");
        list.add("05");
        list.add("06");
        list.add("07");
        wheelView.setData(list);
        wheelView.setDefault(3);
    }

    public String getSelectedText() {
        return wheelView.getSelectedText();
    }
}
