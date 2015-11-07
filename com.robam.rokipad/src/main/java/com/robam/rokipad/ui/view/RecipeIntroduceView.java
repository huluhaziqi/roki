package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.ui.UIService;
import com.robam.common.pojos.CookStep;
import com.robam.common.pojos.Recipe;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.PageKey;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecipeIntroduceView extends FrameLayout {

	@InjectView(R.id.stepList)
	ListView stepList;

	@InjectView(R.id.imgNext)
	ImageView imgNext;

	Recipe book;

	public RecipeIntroduceView(Context context, Recipe book) {
		super(context);
		this.book = book;

		init(context, null);
	}

	@OnClick(R.id.imgNext)
	public void onClickNext() {
		if (book == null)
			return;
		
		Bundle bd = new Bundle();
		bd.putLong(PageArgumentKey.BookId, book.id);
		UIService.getInstance().postPage(PageKey.RecipePrepare, bd);
	}

	void init(Context cx, AttributeSet attrs) {

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_recipe_introduce, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			Adapter adapter = new Adapter(book.getCookSteps());
			stepList.setAdapter(adapter);
		}
	}

	class Adapter extends BaseAdapter {

		List<CookStep> steps = Lists.newArrayList();

		public Adapter(List<CookStep> list) {
			if (list != null) {
				steps.addAll(list);
			}
		}

		@Override
		public int getCount() {
			return steps.size();
		}

		@Override
		public Object getItem(int position) {
			return steps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater layoutInflator = LayoutInflater
						.from(getContext());
				convertView = layoutInflator.inflate(
						R.layout.view_recipe_introduce_step_item, parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_order.setText(String.format("%d.", position + 1));
			holder.tv_content.setText(steps.get(position).desc);
			return convertView;
		}

		class ViewHolder {

			@InjectView(R.id.tv_order)
			TextView tv_order;

			@InjectView(R.id.tv_content)
			TextView tv_content;

			public ViewHolder(View view) {
				ButterKnife.inject(this, view);
			}

		}
	}

}
