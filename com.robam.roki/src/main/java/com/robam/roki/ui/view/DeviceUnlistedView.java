package com.robam.roki.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.legent.plat.services.DeviceTypeManager;
import com.robam.common.pojos.device.IRokiFamily;
import com.robam.roki.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DeviceUnlistedView extends FrameLayout {

    @InjectView(R.id.item1)
    DeviceUnlistedItemView item1;
    @InjectView(R.id.item2)
    DeviceUnlistedItemView item2;
    @InjectView(R.id.item3)
    DeviceUnlistedItemView item3;
    @InjectView(R.id.item4)
    DeviceUnlistedItemView item4;

    public DeviceUnlistedView(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceUnlistedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceUnlistedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_device_unlisted_before,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            DeviceTypeManager dm = DeviceTypeManager.getInstance();
            item1.loadData(dm.queryById(IRokiFamily.RR829));
            item2.loadData(dm.queryById(IRokiFamily.RR039));
            item3.loadData(dm.queryById(IRokiFamily.RM509));
            item4.loadData(dm.queryById(IRokiFamily.RS209));
        }
    }
}
