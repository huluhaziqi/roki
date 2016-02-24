package com.robam.roki.ui.page;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.legent.ui.ext.BaseActivity;
import com.legent.ui.ext.HeadPage;
import com.robam.common.pojos.device.Sterilizer.AbsSterilizer;
import com.robam.common.pojos.device.Sterilizer.ISterilizer;
import com.robam.roki.R;
import com.robam.roki.ui.SterilizerAnimationUtil;
import com.robam.roki.ui.UIListeners;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Gu on 2016/1/6.
 */
public class SteriSwitchPage extends Activity implements UIListeners.ISteriCtrView{
    AbsSterilizer sterilizer;
    @InjectView(R.id.tv_steri_running)
    TextView steriRun;
    @InjectView(R.id.rl_one)
    RelativeLayout rlTem;
    @InjectView(R.id.rl_two)
    RelativeLayout rlGerm;
    @InjectView(R.id.rl_three)
    RelativeLayout rlHum;
    @InjectView(R.id.rl_four)
    RelativeLayout rlOzone;
    SterilizerAnimationUtil animationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_sterilizer_run);
        ButterKnife.inject(this);
        animationUtil = new SterilizerAnimationUtil(this, rlTem, rlHum, rlGerm, rlOzone);
        animationUtil.setAnimation();
        steriRun.setText(Html.fromHtml("<h1><big><big>02:30:00</big></big></h1>\n<h2><big>消毒中</big></h2>"));
    }

    @Override
    public void attachSteri(ISterilizer steri) {

    }

    @Override
    public void onRefresh() {

    }
}
