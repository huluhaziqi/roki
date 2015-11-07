package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.ui.ext.BasePage;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.UIEvents;
import com.robam.rokipad.ui.view.HomeLeftView;
import com.robam.rokipad.ui.view.HomeRightView;
import com.robam.rokipad.ui.view.HomeTitleView;
import com.viewpagerindicator.PageIndicator;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomePage extends BasePage {

	@InjectView(R.id.titleView)
	HomeTitleView titleView;

	@InjectView(R.id.pager)
	ViewPager pager;

	@InjectView(R.id.indicator)
	PageIndicator indicator;

	Adapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.frame_home, container, false);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);

			adapter = new Adapter();
			pager.setAdapter(adapter);
			pager.setOffscreenPageLimit(adapter.getCount());
			indicator.setViewPager(pager);
			pager.setCurrentItem(0);
		}

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@Subscribe
	public void onEvent(UIEvents.HomeDeviceSelectedEvent event) {
		pager.setCurrentItem(0,true);
	}

	class Adapter extends PagerAdapter {

		List<View> views = Lists.newArrayList();

		public Adapter() {

			HomeLeftView left = new HomeLeftView(cx);
			HomeRightView right = new HomeRightView(cx);
			views.add(left);
			views.add(right);
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = views.get(position);
			container.addView(view);

			return view;
		}

	}

}
