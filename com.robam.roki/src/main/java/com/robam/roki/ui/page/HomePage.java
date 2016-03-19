package com.robam.roki.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.legent.events.ConnectionModeChangedEvent;
import com.legent.plat.events.RecipeOpenEvent;
import com.legent.plat.events.RecipeShowEvent;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.adapters.ExtPageAdapter;
import com.legent.ui.ext.views.ExtViewPager;
import com.legent.utils.EventUtils;
import com.robam.common.events.DeviceEasylinkCompletedEvent;
import com.robam.roki.R;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.view.HomeDeviceView;
import com.robam.roki.ui.view.HomePersonalView;
import com.robam.roki.ui.view.HomeRecipeView;
import com.robam.roki.ui.view.HomeTabView;
import com.robam.roki.ui.view.HomeTrolleyView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sylar on 15/6/4.
 */
public class HomePage extends BasePage {

    static public final int TAB_PERSONAL = 0;
    static public final int TAB_RECIPE = 1;
    static public final int TAB_DEVICE = 2;
    static public final int TAB_TROLLEY = 3;


    @InjectView(R.id.pager)
    ExtViewPager pager;
    @InjectView(R.id.tabView)
    HomeTabView tabView;

    Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_home, container, false);
        ButterKnife.inject(this, view);

        adapter = new Adapter();
        adapter.loadViews(buildViews());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount());
        tabView.setOnTabSelectedCallback(tabCallback);
        tabView.selectTab(TAB_RECIPE);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Subscribe
    public void onEvent(RecipeShowEvent event) {            //处理跳转到菜谱主页的消息
        tabView.selectTab(TAB_RECIPE);
    }

    @Subscribe
    public void onEvent(UserLoginEvent event) {
        adapter.refresh();
    }

    @Subscribe
    public void onEvent(UserLogoutEvent event) {
        adapter.refresh();
    }

    @Subscribe
    public void onEvent(DeviceEasylinkCompletedEvent event) {
        tabView.selectTab(TAB_DEVICE);
    }

    List<View> buildViews() {
        List<View> views = Lists.newArrayList();
        views.add(new HomePersonalView(cx));
        views.add(new HomeRecipeView(cx));
        views.add(new HomeDeviceView(cx));
        views.add(new HomeTrolleyView(cx));
        return views;
    }

    HomeTabView.OnTabSelectedCallback tabCallback = new HomeTabView.OnTabSelectedCallback() {
        @Override
        public void onTabSelected(final int tabIndex) {

            tabView.setBackgroundDrawable(null);
            pager.setCurrentItem(tabIndex, true);
            switch (tabIndex) {
                case TAB_PERSONAL:
                    tabView.setBackgroundResource(R.color.White_90);
                    setRootBgRes(R.mipmap.img_user_info_bg);
                    break;
                case TAB_RECIPE:
                    setRootBgRes(R.drawable.shape_home_recipe_bg);
                    break;
                case TAB_DEVICE:
                    setRootBgRes(R.color.main_background);
                    break;
                case TAB_TROLLEY:
                    setRootBgRes(R.color.main_background);
                    postEvent(new RecipeOpenEvent());
                    break;
            }

            EventUtils.postEvent(new HomeTabSelectedEvent(tabIndex));

        }
    };

    class Adapter extends ExtPageAdapter {
        void refresh() {
            for (int i = 0; i < list.size(); i++) {
                refresh(i);
            }
        }

        void refresh(int tabIndex) {
            View view = list.get(tabIndex);
            if (view instanceof UIListeners.IRefresh) {
                UIListeners.IRefresh refView = (UIListeners.IRefresh) view;
                refView.onRefresh();
            }
        }

    }

    public class HomeTabSelectedEvent {
        public int tabIndex;

        public HomeTabSelectedEvent(int tabIndex) {
            this.tabIndex = tabIndex;
        }
    }

}
