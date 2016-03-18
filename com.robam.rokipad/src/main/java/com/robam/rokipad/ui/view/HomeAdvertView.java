package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rosicky on 16/1/20.
 */
public class HomeAdvertView extends FrameLayout {

    @InjectView(R.id.titleView)
    HomeTitleView titleView;

    public HomeAdvertView(Context context) {
        super(context);
        init(context, null);
    }

    public HomeAdvertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeAdvertView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_home_ad, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        titleView.setPnlLeft(false);
    }

    @OnClick(R.id.img1)
    public void onClickImg1() {

    }

    @OnClick(R.id.img2)
    public void onClickImg2() {

    }
}
