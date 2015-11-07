package com.robam.roki.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.robam.common.Utils;
import com.robam.common.pojos.device.Stove;
import com.robam.roki.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecipeCookingStoveStatusView extends FrameLayout {

    @InjectView(R.id.txtValue)
    TextView txtValue;

    public RecipeCookingStoveStatusView(Context context) {
        super(context);
        init(context, null);
    }

    public RecipeCookingStoveStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecipeCookingStoveStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_recipe_cooking_device_status_stove,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
    }

    public void setLevel(short level) {

        boolean isRoki = Utils.getDefaultFan() != null;
        String str = String.format("P%s", level);
        if (!isRoki) {
            if (level == Stove.PowerLevel_0)
                str = "关";
            else if (level <= Stove.PowerLevel_3)
                str = "小火";
            else if (level <= Stove.PowerLevel_6)
                str = "中火";
            else str = "大火";
        }

        txtValue.setText(str);
    }

}
