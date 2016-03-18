package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.common.collect.Lists;
import com.robam.rokipad.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WZTCM on 2016/1/15.
 */
public class RecipeDifficultyView extends FrameLayout {

    @InjectView(R.id.img1)
    ImageView img1;
    @InjectView(R.id.img2)
    ImageView img2;
    @InjectView(R.id.img3)
    ImageView img3;
    @InjectView(R.id.img4)
    ImageView img4;
    @InjectView(R.id.img5)
    ImageView img5;

    List<ImageView> list = Lists.newArrayList();

    public RecipeDifficultyView(Context context) {
        super(context);
        init(context, null);
    }

    public RecipeDifficultyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecipeDifficultyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_recipe_difficulty, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        list.add(img1);
        list.add(img2);
        list.add(img3);
        list.add(img4);
        list.add(img5);
    }

    public void load(int index) {
        for (int i = 0; i < index; i++) {
            list.get(i).setImageResource(R.mipmap.img_recipe_difficulty_hard);
        }
    }
}
