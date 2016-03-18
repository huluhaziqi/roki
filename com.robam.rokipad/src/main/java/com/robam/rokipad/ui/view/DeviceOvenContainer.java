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
import com.legent.plat.Plat;
import com.legent.utils.EventUtils;
import com.legent.utils.ObjectUtils;
import com.robam.common.Utils;
import com.robam.common.events.DeviceDeleteEvent;
import com.robam.common.events.DeviceEasylinkCompletedEvent;
import com.robam.common.events.DeviceLinkEvent;
import com.robam.common.events.DeviceOvenSwitchViewEvent;
import com.robam.common.events.DeviceSteamSwitchViewEvent;
import com.robam.common.events.OvenStatusChangedEvent;
import com.robam.common.pojos.device.Oven.AbsOven;
import com.robam.common.pojos.device.Oven.OvenStatus;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;
import com.robam.common.pojos.device.Steamoven.SteamStatus;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.model.NormalModeItemMsg;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linxiaobin on 2016/1/11.
 */
public class DeviceOvenContainer extends FrameLayout {

    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;
    @InjectView(R.id.relMain)
    RelativeLayout relMain;
    @InjectView(R.id.fraIcon)
    FrameLayout fraIcon;
    @InjectView(R.id.titleView)
    HomeTitleView titleView;

    boolean isAdded = false;
    AbsOven oven;

    boolean onOvenView = false;
    boolean onWorkView = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    switchV(new DeviceOvenView(getContext()));
                    onOvenView = true;
                    onWorkView = false;
                    break;
                case 3:
                    NormalModeItemMsg obj = (NormalModeItemMsg) msg.obj;
                    switchV(new DeviceOvenWorkView(getContext(), obj));
                    onOvenView = false;
                    onWorkView = true;
                    break;
                case 4:
                    DeviceOvenView view = new DeviceOvenView(getContext());
                    view.setSwitch(oven.status == OvenStatus.Off ? false : true);
                    switchV(view);
                    onWorkView = false;
                    onOvenView = true;
                    break;
                case 5:
                    switchV(new DeviceOvenWorkView(getContext()));
                    onOvenView = false;
                    onWorkView = true;
            }
        }
    };

    public DeviceOvenContainer(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceOvenContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceOvenContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        oven = Utils.getDefaultOven();
        View view = LayoutInflater.from(context).inflate(R.layout.device_oven_container, this, true);
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
//        oven = Utils.getDefaultOven();
        if (oven == null) {
            DeviceAddView view = new DeviceAddView(getContext());
            relMain.removeAllViews();
            relMain.addView(view);
            onOvenView = false;
            onWorkView = false;
            isAdded = false;
        } else {
            Message msg2 = Message.obtain();
            msg2.what = 2;
            handler.sendMessage(msg2);
            isAdded = true;
            if (oven.status == OvenStatus.On) {
                DeviceOvenView view = new DeviceOvenView(getContext());
                view.setSwitch(false);
                relMain.removeAllViews();
                relMain.addView(view);
                onOvenView = true;
                onWorkView = false;
            } else if (oven.status == OvenStatus.Off) {
                DeviceOvenView view = new DeviceOvenView(getContext());
                view.setSwitch(false);
                relMain.removeAllViews();
                relMain.addView(view);
                onOvenView = true;
                onWorkView = false;
            } else {
                DeviceOvenWorkView view = new DeviceOvenWorkView(getContext());
                relMain.removeAllViews();
                relMain.addView(view);
                onOvenView = false;
                onWorkView = true;
            }
        }
    }
    public void switchV(final View v) {
        relMain.removeAllViews();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        v.setLayoutParams(lp);
        relMain.addView(v);
    }

    @Subscribe
    public void onEvent(DeviceOvenSwitchViewEvent event) {
        switch (event.type) {
            case 2:
                Message msg2 = Message.obtain();
                msg2.what = 2;
                handler.sendMessage(msg2);
                break;
            case 3:
                Message msg3 = Message.obtain();
                msg3.what = 3;
                msg3.obj = (NormalModeItemMsg) event.bd.getSerializable("msg");
                handler.sendMessage(msg3);
                break;
        }
    }

    @Subscribe
    public void onEvent(DeviceLinkEvent event) {
        if (isAdded) {

        } else {
            relMain.removeAllViews();//未连接时候处理
            DeviceLinkView view = new DeviceLinkView(getContext());
            relMain.addView(view);
        }
    }

    @Subscribe
    public void onEvent(DeviceDeleteEvent event) {
        if (event.name.startsWith("电烤箱")) {
            DeviceAddView view = new DeviceAddView(getContext());
            relMain.removeAllViews();
            relMain.addView(view);
            onOvenView = false;
            onWorkView = false;
            isAdded = false;
        }
    }

    @Subscribe
    public void onEvent(DeviceEasylinkCompletedEvent event) {
        if (event != null && event.pojo != null) {
            oven = Utils.getDefaultOven();
            if(!isAdded && ObjectUtils.isEquals(event.pojo.getID(), oven.getID())) {
                Message msg2 = Message.obtain();
                msg2.what = 2;
                handler.sendMessage(msg2);
            }
        }
    }

    @Subscribe
    public void onEvent(OvenStatusChangedEvent event) {
        if (event.pojo == null) {
            return;
        }
        if (event.pojo instanceof AbsOven) {
            if (event.pojo.status == OvenStatus.Off || event.pojo.status == OvenStatus.On) {
                if (onWorkView) {
                    handler.sendEmptyMessage(4);
                }
            } else if (event.pojo.status == OvenStatus.Working) {
                if (onOvenView) {
                    handler.sendEmptyMessage(5);
                }
            }
        } else  {
            return;
        }
    }
}
