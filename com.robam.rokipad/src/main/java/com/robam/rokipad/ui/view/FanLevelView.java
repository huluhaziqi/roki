package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FanLevelView extends FrameLayout {

    @InjectView(R.id.imgCircle)
    ImageView imgCircle;

    @InjectView(R.id.imgLevel)
    ImageView imgLevel;

    @InjectView(R.id.txtTitle)
    TextView txtTitle;

    Animation anim;
    short level;


    public FanLevelView(Context cx) {
        super(cx);
        init(cx, null);
    }

    public FanLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FanLevelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        imgLevel.setSelected(selected);
        imgCircle.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
//        if (selected)
//            imgCircle.startAnimation(anim);   //档位动画 by zhaiyuanyi
//        else
//            imgCircle.clearAnimation();
    }

    void init(Context cx, AttributeSet attrs) {
        View view = LayoutInflater.from(cx).inflate(R.layout.view_fan_level,
                this, true);

        if (!isInEditMode()) {
            ButterKnife.inject(this, view);
            anim = AnimationUtils.loadAnimation(cx, R.anim.anim_wind_rotate);

            setSelected(false);
        }

    }


    public void setLevel(int level) {
        if (this.level == level) return;

        this.level = (short) level;
        txtTitle.setVisibility(View.INVISIBLE);

        switch (level) {
            case 1:
                imgLevel.setImageResource(R.drawable.ic_gear1_selector);
                break;
            case 2:
                imgLevel.setImageResource(R.drawable.ic_gear2_selector);
                break;
            case 3:
                imgLevel.setImageResource(R.drawable.ic_gear3_selector);
                break;
            case 6:
                imgLevel.setImageResource(R.drawable.ic_gear6_selector);
                txtTitle.setVisibility(View.VISIBLE);
                break;

            default:
                imgLevel.setBackgroundResource(0);
                break;
        }
    }

    public short getLevel() {
        return level;
    }

}
