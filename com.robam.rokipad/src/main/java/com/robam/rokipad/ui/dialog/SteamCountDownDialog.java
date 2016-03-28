package com.robam.rokipad.ui.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.legent.ui.ext.dialogs.AbsDialog;
import com.robam.rokipad.R;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rosicky on 15/12/19.
 */
public class SteamCountDownDialog extends AbsDialog {

    private int count;
    private Timer timer;
    private MyTimerTask timerTask;

    @InjectView(R.id.txt)
    TextView txt;

    public SteamCountDownDialog(Context context) {
        super(context, R.style.Theme_Dialog_HorziFullScreen);
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_count_down_work;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }

    static public void show(Context cx, int time) {
        SteamCountDownDialog dlg = new SteamCountDownDialog(cx);
        Window win = dlg.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.6f;
        win.setAttributes(lp);
        dlg.start(time);
        dlg.show();
    }

    private void start(int time) {
        count = time;
        timer = new Timer();
        timerTask = new MyTimerTask();
        handler.sendEmptyMessage(0);
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    txt.setText(String.valueOf(count));
                    break;
                case 1:
                    dismiss();
                    break;
            }
        }
    };

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            count -= 1;
            if(count == 0){
                cancel();
                handler.sendEmptyMessage(1);
            }
            handler.sendEmptyMessage(0);
        }
    }

}