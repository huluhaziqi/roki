package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.utils.api.PackageUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.pojos.device.fan.Fan9700;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageKey;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GuideHomePage extends BasePage {

    @InjectView(R.id.pnlDeviceInfo)
    View pnlDeviceInfo;

    @InjectView(R.id.txtModel)
    TextView txtModel;

    @InjectView(R.id.txtVersion)
    TextView txtVersion;

    @InjectView(R.id.btnStart)
    Button btnStart;

    AbsFan fan = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fan = Utils.getDefaultFan();

//        int width = cx.getResources().getDisplayMetrics().widthPixels;
//        int height = cx.getResources().getDisplayMetrics().heightPixels;
//        Log.d("pad", String.format("size : %s * %s",width, height));

        View view = inflater.inflate(R.layout.frame_guide_home, container,
                false);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            initData();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btnStart)
    public void onClick() {
        UIService.getInstance().postPage(PageKey.GuideWifi);
    }

    void initData() {
        fan = Utils.getDefaultFan();
        pnlDeviceInfo.setVisibility(fan != null ? View.VISIBLE : View.GONE);
        if (fan == null)
            return;

        txtModel.setText(txtModel.getText() + fan.getGuid().getDeviceTypeId());
        txtVersion.setText(txtVersion.getText() +
                PackageUtils.getVersionName(cx));
    }

}
