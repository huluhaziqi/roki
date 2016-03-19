package com.robam.roki.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.robam.common.pojos.Recipe;
import com.robam.roki.R;
import com.robam.roki.ui.form.MainActivity;
import com.robam.roki.ui.form.UserActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WizardView extends FrameLayout {


    public WizardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public WizardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WizardView(Context context) {
        super(context);
        init(context, null);
    }

    public WizardView(Context context, Recipe Cookbook) {
        super(context);
        init(context, null);
    }

    @OnClick(R.id.txtLogin)
    public void onClickLogin() {
        UserActivity.start((Activity) getContext());
    }

    @OnClick(R.id.txtStroll)
    public void onClickStroll() {
        MainActivity.start((Activity) getContext());
    }

    private void init(Context cx, AttributeSet attrs) {
        View view = LayoutInflater.from(cx).inflate(R.layout.view_wizard,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }

    }


}
