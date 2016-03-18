package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.legent.ui.UIService;
import com.legent.utils.EventUtils;
import com.robam.common.events.RecipeBriefOkEvent;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.dialog.DeleteControlDialog;
import com.robam.rokipad.ui.dialog.RecipeExitDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/15.
 */
public class RecipeBriefView extends FrameLayout {

    @InjectView(R.id.imgRecipeBrief)
    ImageView imgRecipeBrief;
    @InjectView(R.id.txtBrief)
    TextView txtBrief;
    @InjectView(R.id.txtDiff)
    TextView txtDiff;
    @InjectView(R.id.recipeDifficulity)
    RecipeDifficultyView recipeDifficulity;
    @InjectView(R.id.txtStart)
    TextView txtStart;
    @InjectView(R.id.imgReturn)
    ImageView imgReturn;
    @InjectView(R.id.txtRecipeName)
    TextView txtRecipeName;
    @InjectView(R.id.txtStepCount)
    TextView txtStepCount;
    @InjectView(R.id.txtTimeCount)
    TextView txtTimeCount;
    @InjectView(R.id.txtCollectCount)
    TextView txtCollectCount;

    public RecipeBriefView(Context context) {
        super(context);
        init(context, null);
    }

    public RecipeBriefView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecipeBriefView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_recipe_brief, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        initData();
    }

    private void initData() {
        recipeDifficulity.load(2);
    }

    @OnClick(R.id.txtStart)
    public void onClickStart() {
        EventUtils.postEvent(new RecipeBriefOkEvent());
    }

    @OnClick(R.id.imgReturn)
    public void onClickBack() {
        RecipeExitDialog.show(getContext(), new DeleteControlDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                UIService.getInstance().popBack();
            }
        });
    }
}
