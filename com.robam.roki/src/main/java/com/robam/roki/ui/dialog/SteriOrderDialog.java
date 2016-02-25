package com.robam.roki.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robam.roki.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Gu on 2016/2/24.
 */
public class SteriOrderDialog extends Dialog {
    @InjectView(R.id.tv_three)
    TextView threeHour;
    @InjectView(R.id.tv_time_three)
    TextView timeThree;
    @InjectView(R.id.tv_four)
    TextView fourHour;
    @InjectView(R.id.tv_time_four)
    TextView timeFour;
    @InjectView(R.id.tv_five)
    TextView fiveHour;
    @InjectView(R.id.tv_time_five)
    TextView timeFive;
    @InjectView(R.id.tv_six)
    TextView sixHour;
    @InjectView(R.id.tv_time_six)
    TextView timeSix;
    @InjectView(R.id.tv_seven)
    TextView sevenHour;
    @InjectView(R.id.tv_time_seven)
    TextView timeSeven;
    @InjectView(R.id.rl_order_three)
    RelativeLayout rlThree;
    @InjectView(R.id.rl_order_four)
    RelativeLayout rlFour;
    @InjectView(R.id.rl_order_five)
    RelativeLayout rlFive;
    @InjectView(R.id.rl_order_six)
    RelativeLayout rlSix;
    @InjectView(R.id.rl_order_seven)
    RelativeLayout rlSeven;

    public static void show(Context context) {
        SteriOrderDialog dialog = new SteriOrderDialog(context);
        dialog.show();
    }

    public SteriOrderDialog(Context context) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadView(context);
    }

    private void loadView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sterilizer_order_dialog, null, false);
        initView(view, context);
    }

    @OnClick({R.id.tv_three, R.id.tv_four, R.id.tv_five, R.id.tv_six, R.id.tv_seven})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_three:
                setOrderViewStatus(threeHour, timeThree, rlThree);
                break;
            case R.id.tv_four:
                setOrderViewStatus(fourHour, timeFour, rlFour);
                break;
            case R.id.tv_five:
                setOrderViewStatus(fiveHour, timeFive, rlFive);
                break;
            case R.id.tv_six:
                setOrderViewStatus(sixHour, timeSix, rlSix);
                break;
            case R.id.tv_seven:
                setOrderViewStatus(sevenHour, timeSeven, rlSeven);
                break;
        }

    }

    private void setOrderViewStatus(TextView hour, TextView time, RelativeLayout layout) {
        boolean selected = hour.isSelected() ? false : true;
        hour.setSelected(selected);
        layout.setSelected(selected);
        int visible = selected ? View.VISIBLE : View.GONE;
        time.setVisibility(visible);
    }

    private void initView(View view, Context context) {
        setContentView(view);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        //将对话框的大小按屏幕大小的百分比设置
        WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.6
        p.width = d.getWidth(); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        ButterKnife.inject(this, view);
    }
}