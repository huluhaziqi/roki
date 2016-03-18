package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.robam.rokipad.R;

public class FanLightView extends ImageView {

    public FanLightView(Context cx) {
        super(cx);
        init(cx, null);
    }

    public FanLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FanLightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    boolean power;

    void init(Context cx, AttributeSet attrs) {
        this.setImageResource(R.drawable.ic_light_selector);
    }

    public void setPower(boolean power) {
        if (power == this.power) return;

        this.power = power;
        this.setSelected(power);
    }

    public boolean power() {
        return power;
    }

}
