package com.robam.rokipad.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.legent.Callback;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.Group;
import com.robam.common.services.CookbookManager;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.dialog.RecipePopWindow;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecipeMiddleTitleView extends FrameLayout implements
		OnClickListener {
	public static interface TitleClickCallback {
		void onCategorySelected(Group group);
	}

	@InjectView(R.id.tv_title)
	TextView tvTitle;

	@InjectView(R.id.img_arrow)
	ImageView imgArrow;

	@InjectView(R.id.layoutTitle)
	RelativeLayout layoutTitle;

	int index;
	List<Group> groups;
	TitleClickCallback callback;

	public RecipeMiddleTitleView(Context cx, TitleClickCallback callback) {
		super(cx);
		this.callback = callback;

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_recipe_middle_title, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			initData();
		}
	}

	void initData() {
		CookbookManager.getInstance().getGroupsWithoutHome(
				new Callback<List<Group>>() {

					@Override
					public void onSuccess(List<Group> result) {
						groups = result;
						setSelectedIndex(0);
					}

					@Override
					public void onFailure(Throwable t) {
						ToastUtils.showThrowable(t);
					}
				});
	}

	@OnClick(R.id.layoutTitle)
	public void onClick(View view) {

		imgArrow.setImageResource(R.mipmap.ic_title_arrow_up);

		RecipePopWindow pop = RecipePopWindow.show(getContext(), view, index,
				new RecipePopWindow.CategorySelectedCallback() {

					@Override
					public void onCategorySelected(int position, Group group) {
						setSelectedIndex(position);
					}
				});

		pop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				imgArrow.setImageResource(R.mipmap.ic_title_arrow_down);
			}
		});
	}

	private void setSelectedIndex(int index) {
		this.index = index;
		if (groups != null && groups.size() > index) {
			Group group = groups.get(index);
			tvTitle.setText(group.name);
			if (callback != null) {
				callback.onCategorySelected(group);
			}
		}
	}

}
