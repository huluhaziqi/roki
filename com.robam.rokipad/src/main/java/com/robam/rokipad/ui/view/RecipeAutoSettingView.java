package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.legent.ui.UIService;
import com.legent.utils.EventUtils;
import com.robam.common.events.RecipeDoneEvent;
import com.robam.common.events.RecipeStartEvent;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.dialog.DeleteControlDialog;
import com.robam.rokipad.ui.dialog.RecipeExitDialog;
import com.robam.rokipad.ui.dialog.RecipeInWorkingDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by WZTCM on 2016/1/15.
 */
public class RecipeAutoSettingView extends FrameLayout {

    @InjectView(R.id.txtStep1)
    TextView txtStep1;
    @InjectView(R.id.txtStep2)
    TextView txtStep2;
    @InjectView(R.id.lin)
    LinearLayout lin;
    @InjectView(R.id.txtBrief)
    TextView txtBrief;
    @InjectView(R.id.recipeList)
    ListView recipeList;
    @InjectView(R.id.linSet)
    LinearLayout linSet;
    @InjectView(R.id.txtTips)
    TextView txtTips;
    @InjectView(R.id.rel)
    RelativeLayout rel;
    @InjectView(R.id.txtAutoSet)
    TextView txtAutoSet;
    @InjectView(R.id.img)
    ImageView img;
    @InjectView(R.id.imgLight)
    ImageView imgLight;
    @InjectView(R.id.txtStart)
    TextView txtStart;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.txtProgress)
    TextView txtProgress;
    @InjectView(R.id.relProgress)
    RelativeLayout relProgress;
    @InjectView(R.id.imgClose)
    ImageView imgClose;
    @InjectView(R.id.imgReturn)
    ImageView imgReturn;

    private Adapter adapter;
    private int time;
    private int progressCount;
    private int sumCount;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (progressCount <= sumCount) {
                        progress.setProgress(progressCount++);
                        sendEmptyMessageDelayed(1, 1000);
                    } else {
                        EventUtils.postEvent(new RecipeDoneEvent());
                    }
                    break;
            }
        }
    };

    public RecipeAutoSettingView(Context context) {
        super(context);
        init(context, null);
    }

    public RecipeAutoSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecipeAutoSettingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventUtils.regist(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventUtils.unregist(this);
    }


    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_recipe_auto_setting, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }

        adapter = new Adapter(context);
        recipeList.setAdapter(adapter);
    }

    @OnClick(R.id.txtAutoSet)
    public void onClickSet() {
        // TODO AutoSet
//        RecipeAutoSetSucDialog.show(getContext());
        RecipeInWorkingDialog.show(getContext());
        txtAutoSet.setVisibility(View.INVISIBLE);
        txtAutoSet.setClickable(false);
        txtStart.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.txtStart)
    public void onClickStart() {
        EventUtils.postEvent(new RecipeStartEvent());
        txtStart.setVisibility(View.INVISIBLE);
        initProgressBar();
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

    private void initProgressBar() {
        relProgress.setVisibility(View.VISIBLE);
        progress.setProgress(0);
        time = 10;
        sumCount = 10;
        progress.setMax(10);
        progressCount = 0;
        handler.sendEmptyMessage(1);
    }

    private class Adapter extends BaseAdapter {
        Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.view_recipe_auto_setting_item, null, false);
            return view;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
