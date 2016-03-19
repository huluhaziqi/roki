package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.legent.ui.ext.HeadPage;
import com.robam.common.Utils;
import com.robam.common.pojos.device.Sterilizer.AbsSterilizer;
import com.robam.roki.R;

import butterknife.ButterKnife;

/**
 * Created by Gu on 2016/2/29.
 */
public class SteriSmartParamsPage extends HeadPage{
    AbsSterilizer sterilizer;

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_steri_smart_params,null);
        ButterKnife.inject(this, view);
        sterilizer = Utils.getDefaultSterilizer();
        initData();
        return view;
    }

    private void initData() {
        boolean isEmpty = sterilizer == null;
    }
}
