package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.legent.ui.UIService;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageKey;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HomeRightView extends FrameLayout {

	public HomeRightView(Context context) {
		super(context);
		init(context, null);
	}

	public HomeRightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HomeRightView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@InjectView(R.id.pnlFavority)
	View pnlFavority;

	@InjectView(R.id.pnlRecommend)
	View pnlRecommend;

	@InjectView(R.id.pnlAll)
	View pnlAll;

	void init(Context cx, AttributeSet attrs) {

		View view = LayoutInflater.from(cx).inflate(R.layout.view_home_right,
				this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
		}
	}

	@OnClick(R.id.pnlFavority)
	public void onFavority() {
		UIService.getInstance().postPage(PageKey.RecipeFavority);
	}

	@OnClick(R.id.pnlRecommend)
	public void onRecommend() {
		UIService.getInstance().postPage(PageKey.RecipeRecommend);
	}

	@OnClick(R.id.pnlAll)
	public void onAll() {
		UIService.getInstance().postPage(PageKey.RecipeCategory);
	}
}
