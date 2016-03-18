package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.views.UnslideViewPager;
import com.robam.common.events.RecipeBriefOkEvent;
import com.robam.common.events.RecipeDoneEvent;
import com.robam.common.events.RecipeStartEvent;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.view.RecipeAutoSettingView;
import com.robam.rokipad.ui.view.RecipeBriefView;
import com.robam.rokipad.ui.view.RecipeDoneView;
import com.robam.rokipad.ui.view.RecipeNormalView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WZTCM on 2016/1/15.
 */
public class RecipeContainerPage extends BasePage {

    View contentView;

    List<View> listViews;
    ViewPagerAdapter adapter;

    @InjectView(R.id.pager)
    UnslideViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.page_recipe_brief,
                container, false);
        ButterKnife.inject(this, contentView);
        initListViews();
        adapter = new ViewPagerAdapter(listViews);
        pager.setAdapter(adapter);
        return contentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void initListViews() {
        if (listViews == null) {
            listViews = Lists.newArrayList();
        }
        RecipeBriefView briefView = new RecipeBriefView(getContext());
        RecipeNormalView normalView1 = new RecipeNormalView(getContext());
        RecipeNormalView normalView2 = new RecipeNormalView(getContext());
        RecipeAutoSettingView settingView = new RecipeAutoSettingView(getContext());
        RecipeDoneView doneView = new RecipeDoneView(getContext());
        listViews.add(briefView);
        listViews.add(normalView1);
        listViews.add(normalView2);
        listViews.add(settingView);
        listViews.add(doneView);
    }

    @Subscribe
    public void onEvent(RecipeStartEvent event) {
        pager.setScrollable(false);
    }

    @Subscribe
    public void onEvent(RecipeDoneEvent event) {
        pager.setScrollable(true);
    }

    @Subscribe
    public void onEvent(RecipeBriefOkEvent event) {
        pager.setCurrentItem(1);
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private List<View> listViews;

        public ViewPagerAdapter(List<View> listViews) {
            this.listViews = listViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(listViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(listViews.get(position));
            return listViews.get(position);
        }

        @Override
        public int getCount() {
            return listViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
