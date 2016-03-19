package com.robam.roki.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robam.roki.R;
import com.robam.roki.ui.UIListeners;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Gu on 2016/3/1.
 */
public class SteriStovingDialog extends Dialog {

    UIListeners.SteriStoveCallback callback;
    @InjectView(R.id.rl_six)
    RelativeLayout rlSix;
    @InjectView(R.id.rl_eight)
    RelativeLayout rlEight;
    @InjectView(R.id.tv_eight)
    TextView txtEight;
    @InjectView(R.id.tv_six)
    TextView txtSix;
    @InjectView(R.id.img_check_eight)
    ImageView imgEight;
    @InjectView(R.id.img_check_six)
    ImageView imgSix;
    @InjectView(R.id.commit)
    TextView commit;

    public static void show(Context context, UIListeners.SteriStoveCallback callback) {
        SteriStovingDialog dialog = new SteriStovingDialog(context, callback);
        dialog.show();
    }

    public SteriStovingDialog(Context context, UIListeners.SteriStoveCallback callback) {
        super(context);
        this.callback = callback;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadView(context);
    }

    private void loadView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_steri_stoving, null);
        initView(view, context);
    }

    private void initView(View view, Context context) {
        setContentView(view);
        ButterKnife.inject(this, view);
        initData(context);
    }

    private void initData(Context context) {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        //将对话框的大小按屏幕大小的百分比设置
        WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.4
        p.width = d.getWidth(); // 宽度设置为屏幕的宽度
        dialogWindow.setAttributes(p);
    }

    @OnClick(R.id.rl_six)
    public void onClickSix(View v) {
        setEight(true);
        boolean selected = rlSix.isSelected();
        setSix(selected);

    }

    @OnClick(R.id.rl_eight)
    public void onClickEight(View v) {
        setSix(true);
        boolean selected = rlEight.isSelected();
        setEight(selected);
    }

    @OnClick(R.id.commit)
    public void onClickCommit() {
        if (!rlSix.isSelected() && !rlEight.isSelected())
            return;
        if (rlSix.isSelected()) {
            callback.callBack(60);
        }
        if (rlEight.isSelected()) {
            callback.callBack(80);
        }
        this.dismiss();
    }

    private void setSix(boolean selected) {
        if (selected) {
            rlSix.setSelected(false);
            imgSix.setVisibility(View.GONE);
            txtSix.setTextColor(Color.parseColor("#969696"));
        } else {
            rlSix.setSelected(true);
            imgSix.setVisibility(View.VISIBLE);
            txtSix.setTextColor(Color.parseColor("#222222"));
        }
    }

    private void setEight(boolean selected) {
        if (selected) {
            rlEight.setSelected(false);
            imgEight.setVisibility(View.GONE);
            txtEight.setTextColor(Color.parseColor("#969696"));
        } else {
            rlEight.setSelected(true);
            imgEight.setVisibility(View.VISIBLE);
            txtEight.setTextColor(Color.parseColor("#222222"));
        }
    }
}
