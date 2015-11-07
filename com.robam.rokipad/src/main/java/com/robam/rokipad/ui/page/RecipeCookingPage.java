package com.robam.rokipad.ui.page;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.legent.Callback;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.api.ViewUtils;
import com.robam.common.pojos.CookStep;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.rokipad.R;
import com.robam.rokipad.service.CookTaskService;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.view.RecipeCookingView;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class RecipeCookingPage extends BasePage {

	@InjectView(R.id.imgHome)
	ImageView imgHome;

	@InjectView(R.id.imgPause)
	ImageView imgPause;

	@InjectView(R.id.imgExit)
	ImageView imgExit;

	@InjectView(R.id.pager)
	ViewPager pager;

	Recipe book;
	int stepIndex;
	Adapter adapter;

	CookTaskService cts = CookTaskService.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		Bundle bd = getArguments();
		long bookId = bd.getLong(PageArgumentKey.BookId);
		stepIndex = bd.getInt(PageArgumentKey.RecipeStepIndex, 0);
		if (stepIndex < 0) {
			stepIndex = 0;
		}

		View view = inflater.inflate(R.layout.frame_recipe_cooking, container,
				false);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			adapter = new Adapter();
			pager.setAdapter(adapter);
			ViewUtils.setScroolDuration(pager, 300);

			initData(bookId);
		}
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.imgHome)
	public void onTitleBack() {
		CookTaskService.getInstance().minimize();
	}

	@OnClick(R.id.imgPause)
	public void onTitlePause() {
		CookTaskService.getInstance().pause();
	}

	@OnClick(R.id.imgExit)
	public void onExit() {
		String message = "是否确定退出？";
		DialogHelper.newDialog_OkCancel(cx, null, message,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dlg, int witch) {
						if (witch == DialogInterface.BUTTON_POSITIVE) {
							CookTaskService.getInstance().stop();
						}
					}
				}).show();
	}

	@OnPageChange(R.id.pager)
	public void onPagesChanged(int index) {
		if (index > stepIndex) {
			cts.next();
		} else if (index < stepIndex) {
			cts.back();
		}
		stepIndex = index;
	}

	protected void initData(long bookId) {
		ProgressDialogHelper.setRunning(cx, true);
		CookbookManager.getInstance().getCookbookById(bookId,
				new Callback<Recipe>() {

					@Override
					public void onSuccess(Recipe book) {
						loadData(book);
						ProgressDialogHelper.setRunning(cx, false);
					}

					@Override
					public void onFailure(Throwable t) {
						ProgressDialogHelper.setRunning(cx, false);
						ToastUtils.showThrowable(t);
					}
				});
	}

	protected void loadData(Recipe book) {
		this.book = book;
		adapter.setData(book);
		pager.setCurrentItem(stepIndex, true);
	}

	// ---------------------------------------------------------------------------------------

	class Adapter extends PagerAdapter {

		List<CookStep> list = Lists.newArrayList();

		Map<Integer, View> mapViews = Maps.newHashMap();

		public void setData(Recipe book) {

			list.clear();
			mapViews.clear();

			List<CookStep> steps = book.getCookSteps();
			if (list != null) {
				list.addAll(steps);
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mapViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = null;
			if (mapViews.containsKey(position)) {
				view = mapViews.get(position);
			} else {
				view = new RecipeCookingView(cx, book, position, callback);
				view.setTag(position);
				mapViews.put(position, view);
			}

			container.addView(view);
			return view;
		}

		RecipeCookingView.CookingNextCallback callback = new RecipeCookingView.CookingNextCallback() {

			@Override
			public void onClickNext() {

				if (stepIndex < getCount() - 1) {
					pager.setCurrentItem(stepIndex + 1, true);
				}
			}
		};

	}

}
