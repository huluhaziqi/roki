package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.legent.ui.UIService;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.dialog.DeleteControlDialog;
import com.robam.rokipad.ui.dialog.RecipeExitDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/15.
 */
public class RecipeNormalView extends FrameLayout {

    @InjectView(R.id.imgReturn)
    ImageView imgReturn;
    @InjectView(R.id.imgClose)
    ImageView imgClose;
    @InjectView(R.id.txtStep1)
    TextView txtStep1;
    @InjectView(R.id.txtStep2)
    TextView txtStep2;
    @InjectView(R.id.lin)
    LinearLayout lin;
    @InjectView(R.id.txtBrief)
    TextView txtBrief;
    @InjectView(R.id.txtTips)
    TextView txtTips;
    @InjectView(R.id.rel)
    RelativeLayout rel;
    @InjectView(R.id.imgRecipeBrief)
    ImageView imgRecipeBrief;
    @InjectView(R.id.imgLight)
    ImageView imgLight;

    public RecipeNormalView(Context context) {
        super(context);
        init(context, null);
    }

    public RecipeNormalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecipeNormalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_recipe_normal, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
    }

    @OnClick(R.id.imgClose)
    public void onClickClose() {
        RecipeExitDialog.show(getContext(), new DeleteControlDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                UIService.getInstance().popBack();
            }
        });
    }
}
