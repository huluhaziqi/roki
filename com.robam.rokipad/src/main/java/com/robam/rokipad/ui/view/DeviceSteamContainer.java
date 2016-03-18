package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.utils.EventUtils;
import com.legent.utils.ObjectUtils;
import com.robam.common.Utils;
import com.robam.common.events.DeviceEasylinkCompletedEvent;
import com.robam.common.events.DeviceLinkEvent;
import com.robam.common.events.DeviceSteamSwitchViewEvent;
import com.robam.common.events.SteamOvenStatusChangedEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WZTCM on 2016/1/11.
 */
public class DeviceSteamContainer extends FrameLayout {

    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;
    @InjectView(R.id.relMain)
    RelativeLayout relMain;
    @InjectView(R.id.fraIcon)
    FrameLayout fraIcon;

    AbsSteamoven steam;
    boolean isAdded = false;
    @InjectView(R.id.titleView)
    HomeTitleView titleView;

    boolean onSteamView = false;
    boolean onWorkView = false;

    public DeviceSteamContainer(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceSteamContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceSteamContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_steam_container, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        titleView.setPnlLeft(false);
        initData();
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

    private void initData() {
        steam = Utils.getDefaultSteam();
        if (steam == null) {
            onSteamView = false;
            onWorkView = false;
            DeviceAddView view = new DeviceAddView(getContext());
            ((TextView) view.findViewById(R.id.txtMsg)).setText("添加智能蒸箱");
            ((TextView) view.findViewById(R.id.txtMsg2)).setText("蒸出美好生活~");
            relMain.removeAllViews();
            relMain.addView(view);
            isAdded = false;
        } else {
            isAdded = true;
            if (steam.status == SteamStatus.On) {
                DeviceSteamView view = new DeviceSteamView(getContext());
                view.setSwitch(true);
                relMain.removeAllViews();
                relMain.addView(view);
                onSteamView = true;
                onWorkView = false;
            } else if (steam.status == SteamStatus.Off) {
                DeviceSteamView view = new DeviceSteamView(getContext());
                view.setSwitch(false);
                relMain.removeAllViews();
                relMain.addView(view);
                onSteamView = true;
                onWorkView = false;
            } else {
                DeviceSteamWorkView view = new DeviceSteamWorkView(getContext());
                relMain.removeAllViews();
                relMain.addView(view);
                onSteamView = false;
                onWorkView = true;
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    DeviceWorkMsg deviceWorkMsg = (DeviceWorkMsg) msg.obj;
                    switchV(new DeviceSteamWorkView(getContext(), deviceWorkMsg));
                    onWorkView = true;
                    onSteamView = false;
                    break;
                case 2:
                    switchV(new DeviceSteamWorkView(getContext()));
                    onWorkView = true;
                    onSteamView = false;
                    break;
                case 3:
                    DeviceSteamView view = new DeviceSteamView(getContext());
                    view.setSwitch(steam.status == SteamStatus.Off ? false : true);
                    switchV(view);
                    onWorkView = false;
                    onSteamView = true;
                default:
                    break;
            }
        }
    };

    public void switchV(final View v) {
        relMain.removeAllViews();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        v.setLayoutParams(lp);
        relMain.addView(v);
    }

    @Subscribe
    public void onEvent(DeviceSteamSwitchViewEvent event) {
        switch (event.type) {
            case 0:
                switchV(new DeviceSteamView(getContext()));
                break;
            case 1:
                DeviceWorkMsg msg = (DeviceWorkMsg) event.bd.getSerializable("msg");
                Log.e("steam", "msg = " + msg);
                Message message = Message.obtain();
                message.what = 1;
                message.obj = msg;
                handler.sendMessage(message);
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onEvent(DeviceLinkEvent event) {
        if (isAdded) {

        } else {
            relMain.removeAllViews();
            DeviceLinkView view = new DeviceLinkView(getContext());
            relMain.addView(view);
        }
    }

    @Subscribe
    public void onEvent(DeviceEasylinkCompletedEvent event) {
        if (event != null && event.pojo != null) {
            steam = Utils.getDefaultSteam();
            if (!isAdded && ObjectUtils.isEquals(event.pojo.getID(), steam.getID())) {
                handler.sendEmptyMessage(3);
            }
        }
    }

    @Subscribe
    public void onEvent(SteamOvenStatusChangedEvent event) {
        if (event.pojo == null) {
            return;
        }
        if (event.pojo instanceof AbsSteamoven) {
            if (event.pojo.status == SteamStatus.Off || event.pojo.status == SteamStatus.On) {
                if (onWorkView) {
                    handler.sendEmptyMessage(3);
                }
            } else if (event.pojo.status == SteamStatus.Working) {
                if (onSteamView) {
                    handler.sendEmptyMessage(2);
                }
            }
        } else  {
            return;
        }
    }

}
