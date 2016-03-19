package com.robam.roki.ui.page;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.adapters.ExtPageAdapter;
import com.legent.utils.api.PreferenceUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.view.WizardView;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WizardPage extends BasePage {

    @InjectView(R.id.pager)
    ViewPager pager;

    @InjectView(R.id.indicator)
    CirclePageIndicator indicator;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        PreferenceUtils.setBool(PageArgumentKey.IsFirstUse, false);

        View view = inflater.inflate(R.layout.page_wizard, container, false);
        ButterKnife.inject(this, view);

        ExtPageAdapter adapter = new ExtPageAdapter();
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        List<View> views = buildViews();
        adapter.loadViews(views);

        return view;
    }

    List<View> buildViews() {
        List<View> views = new ArrayList<View>();
        ImageView view1 = new ImageView(cx);
        ImageView view2 = new ImageView(cx);
        WizardView view3 = new WizardView(cx);

        view1.setScaleType(ScaleType.CENTER_CROP);
        view2.setScaleType(ScaleType.CENTER_CROP);

        ImageUtils.displayImage(
                ImageUtils.fromDrawable(R.mipmap.img_wizard_1), view1);
        ImageUtils.displayImage(
                ImageUtils.fromDrawable(R.mipmap.img_wizard_2), view2);

        views.add(view1);
        views.add(view2);
        views.add(view3);

        return views;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
