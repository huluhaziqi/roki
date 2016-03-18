package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.robam.rokipad.R;

public class FanWindView extends ImageView {

    int level;
    Animation anim;

    public FanWindView(Context cx) {
        super(cx);
        init(cx, null);
    }

    public FanWindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FanWindView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {
        this.setImageResource(R.mipmap.img_blower);
        anim = AnimationUtils.loadAnimation(getContext(),
                R.anim.anim_wind_rotate);
    }

    public void setLevel(int level) {
        if (level == this.level) return;
        this.level = level;

//        switch (level) {                       //delete wind animation    by zhaiyuanyi 15/10/17
//            case Fan.PowerLevel_1:
//                anim.setDuration(1500/2);
//                this.startAnimation(anim);
//                break;
//
//            case Fan.PowerLevel_2:
//                anim.setDuration(1200/2);
//                this.startAnimation(anim);
//                break;
//
//            case Fan.PowerLevel_3:
//                anim.setDuration(900/2);
//                this.startAnimation(anim);
//                break;
//
//            case Fan.PowerLevel_6:
//                anim.setDuration(600/2);
//                this.startAnimation(anim);
//                break;
//
//            default:
//                this.clearAnimation();
//                break;

//        }

    }

}
