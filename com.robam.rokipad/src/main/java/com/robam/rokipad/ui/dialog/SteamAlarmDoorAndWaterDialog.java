package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WZTCM on 2015/12/20.
 */
public class SteamAlarmDoorAndWaterDialog extends AbsDialog{

    public static final int RESET_DIALOG = 6;
    public static final int STEAM_OFF = 7;
    public static final int ALARM_DOOR = 8;
    public static final int ALARM_WATER = 9;
    public static final int ALARM_SENSOR = 10;

    @InjectView(R.id.img)
    ImageView img;
    @InjectView(R.id.txt)
    TextView txt;
    @InjectView(R.id.start)
    TextView start;

    public SteamAlarmDoorAndWaterDialog(Context context) {
        super(context, R.style.Theme_Dialog_HorziFullScreen);
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_steam_alarm_water_door;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }

    public void setUI(int type) {
        switch (type) {
            case STEAM_OFF:
                img.setImageResource(R.mipmap.img_steam_alarm_glove);
                txt.setText("结束蒸汽炉工作");
                break;
            case ALARM_DOOR:
                img.setImageResource(R.mipmap.img_steam_alarm_water);
                txt.setText("打开蒸汽炉，拿出右侧的水箱加满水。");
                break;
            case ALARM_WATER:
                img.setImageResource(R.mipmap.img_steam_alarm_door);
                txt.setText("蒸汽炉门未关");
                break;
        }
    }

    static public void show(Context context, int type) {
        SteamAlarmDoorAndWaterDialog dlg = new SteamAlarmDoorAndWaterDialog(context);
        Window win = dlg.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.6f;
        win.setAttributes(lp);
        dlg.setUI(type);
        dlg.show();
    }
}
