package com.robam.roki.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceCollectionChangedEvent;
import com.legent.utils.EventUtils;
import com.robam.common.Utils;
import com.robam.common.events.OrderRefreshEvent;
import com.robam.common.services.StoreService;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.dialog.OrderHintDialog;
import com.robam.roki.ui.page.HomePage;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TrolleyFreeView extends FrameLayout {

    interface OnDeliverCallback {
        void onDeliver();
    }

    final static String HINT_IS_ROKI = "*免费配送:\n每周一次；每次最多三道菜";
    final static String HINT_NOT_ROKI = "*免费配送:一人一次最多三道菜\n(每日限量50人次)";

    @InjectView(R.id.txtDeliver)
    TextView txtDeliver;
    @InjectView(R.id.txtHint)
    TextView txtHint;

    int rc;
    OnDeliverCallback callback;

    public TrolleyFreeView(Context context) {
        super(context);
        init(context, null);
    }

    public TrolleyFreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TrolleyFreeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_trolley_free,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            onRefresh();
        }

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


    @Subscribe
    public void onEvent(DeviceCollectionChangedEvent event) {
        refreshHint();
    }

    @Subscribe
    public void onEvent(OrderRefreshEvent event) {
        onRefresh();
    }

    @Subscribe
    public void onEvent(HomePage.HomeTabSelectedEvent event) {
        if (event.tabIndex == HomePage.TAB_TROLLEY) {
            onRefresh();
        }
    }

    @OnClick(R.id.txtDeliver)
    public void onClick() {
        if (rc > 0) {
            OrderHintDialog.show(getContext(), rc);
        } else if (callback != null && UiHelper.checkAuthWithDialog(getContext(), PageKey.UserLogin)) {
            callback.onDeliver();
        }
    }

    public void setOnDeliverCallback(OnDeliverCallback callback) {
        this.callback = callback;
    }


    void onRefresh() {

        refreshHint();

        if (!Plat.accountService.isLogon()) {
            setDeliverable(false);
            return;
        }

        StoreService.getInstance().deiverIfAllow(new Callback<Integer>() {

            @Override
            public void onSuccess(Integer i) {
                rc = i;
                setDeliverable(i == 0);
            }

            @Override
            public void onFailure(Throwable throwable) {
                setDeliverable(false);
            }
        });
    }

    void refreshHint() {
        txtHint.setText(Utils.hasRokiDevice() ? HINT_IS_ROKI : HINT_NOT_ROKI);
    }


    void setDeliverable(boolean isDeliver) {
        txtDeliver.setSelected(!isDeliver);
    }
}
