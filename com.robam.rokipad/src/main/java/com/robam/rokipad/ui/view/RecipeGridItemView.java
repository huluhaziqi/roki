package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.events.FavorityBookRefreshEvent;
import com.robam.common.events.TodayBookCleanEvent;
import com.robam.common.events.TodayBookRefreshEvent;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.adapter.RecipeAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class RecipeGridItemView extends FrameLayout {

	public RecipeGridItemView(Context context) {
		super(context);
		init(context, null);
	}

	public RecipeGridItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RecipeGridItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@InjectView(R.id.txtName)
	TextView txtName;

	@InjectView(R.id.imgDish)
	ImageView imgDish;

	@InjectView(R.id.imgToday)
	ImageView imgToday;

	@InjectView(R.id.imgFavority)
	ImageView imgFavority;

	@InjectView(R.id.imgDelete)
	ImageView imgDelete;

	Recipe book;
	int modelType = RecipeAdapter.Model_Normal;
	boolean isSmallSize = false;
	boolean isInDelete = false;

	int smallHeight, mediumHeight;

	CookbookManager cm = CookbookManager.getInstance();

	private void init(Context cx, AttributeSet attrs) {

		smallHeight = (int) cx.getResources().getDimension(
				R.dimen.recipe_grid_item_small_img_height);
		mediumHeight = (int) cx.getResources().getDimension(
				R.dimen.recipe_grid_item_medium_img_height);

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_recipe_grid_item, this, true);

		if (!isInEditMode()) {
			ButterKnife.inject(this, view);
		}

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		EventUtils.regist(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		EventUtils.unregist(this);
	}

	@Subscribe
	public void onEvent(TodayBookCleanEvent event) {
		book.isToday = false;
		refresh();
	}

	@Subscribe
	public void onEvent(TodayBookRefreshEvent event) {
		if (event.bookId != book.id)
			return;

		book.isToday = event.flag;
		refresh();
	}

	@Subscribe
	public void onEvent(FavorityBookRefreshEvent event) {
		if (event.bookId != book.id)
			return;

		book.isFavority = event.flag;
		refresh();
	}

	@OnLongClick(R.id.layout)
	public boolean onLongClick(View view) {

		switch (modelType) {
		case RecipeAdapter.Model_Today:
			isInDelete = true;
			imgDelete.setVisibility(isInDelete ? View.VISIBLE : View.GONE);

			return true;
		case RecipeAdapter.Model_Favority:
			isInDelete = true;

			imgToday.setVisibility(!isInDelete ? View.VISIBLE : View.GONE);
			imgDelete.setVisibility(isInDelete ? View.VISIBLE : View.GONE);
			return true;
		default:
			return true;
		}

	}

	@OnClick({ R.id.layout, R.id.imgToday, R.id.imgFavority })
	public void onClick(View view) {

		if (book == null)
			return;

		switch (view.getId()) {
		case R.id.layout:
			if (isInDelete) {
				onDelete();
			} else {
				onToDetail();
			}
			break;
		case R.id.imgToday:
			if (UiHelper.checkAuth(PageKey.UserLogin)) {
				onToday();
			}
			break;
		case R.id.imgFavority:
			if (UiHelper.checkAuth(PageKey.UserLogin)) {
				onFavority();
			}
			break;

		default:
			break;
		}
	}

	public void setBook(Recipe book, int modelType, boolean isSmallSize) {
		this.book = book;
		this.modelType = modelType;
		this.isSmallSize = isSmallSize;

		this.isInDelete = false;

		setItemSize();
		refresh();
	}

	private void setItemSize() {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgDish
				.getLayoutParams();

		params.height = isSmallSize ? smallHeight : mediumHeight;
		imgDish.setLayoutParams(params);
	}

	private void refresh() {
		if (book == null)
			return;

		txtName.setText(book.name);
		String imgUri = isSmallSize ? book.imgSmall : book.imgMedium;

		imgDish.setImageBitmap(null);
		ImageUtils.displayImage(imgUri, imgDish);

		imgFavority.setSelected(book.isFavority);
		imgToday.setSelected(book.isToday);

		imgFavority.setVisibility(View.GONE);
		imgToday.setVisibility(View.GONE);
		imgDelete.setVisibility(View.GONE);

		switch (modelType) {
		case RecipeAdapter.Model_Today:
			imgFavority.setVisibility(View.VISIBLE);
			imgToday.setVisibility(View.GONE);
			//
			imgDelete.setVisibility(isInDelete ? View.VISIBLE : View.GONE);
			break;
		case RecipeAdapter.Model_Favority:
			imgFavority.setVisibility(View.GONE);
			//
			imgToday.setVisibility(!isInDelete ? View.VISIBLE : View.GONE);
			imgDelete.setVisibility(isInDelete ? View.VISIBLE : View.GONE);
			break;
		default:
			imgFavority.setVisibility(View.VISIBLE);
			imgToday.setVisibility(View.VISIBLE);
			break;
		}

	}

	private void onToDetail() {

		final Context cx = getContext();
		ProgressDialogHelper.setRunning(cx, true);
		CookbookManager.getInstance().getCookbookById(book.id,
				new Callback<Recipe>() {

					@Override
					public void onSuccess(Recipe result) {
						// 云端或本地缓存中有菜谱详情，才跳转
						Bundle bd = new Bundle();
						bd.putLong(PageArgumentKey.BookId, book.id);
						UIService.getInstance().postPage(
								PageKey.RecipeIntroduce, bd);

						ProgressDialogHelper.setRunning(cx, false);
					}

					@Override
					public void onFailure(Throwable t) {
						ProgressDialogHelper.setRunning(cx, false);
						ToastUtils.showShort("获取菜谱详情失败");
					}
				});

	}

	private void onToday() {
		if (book.isToday) {
			onDeleteToday();
		} else {
			cm.addTodayCookbook(book.id, new VoidCallback() {

				@Override
				public void onSuccess() {
					ToastUtils.showShort("添加今日菜谱成功!");
				}

				@Override
				public void onFailure(Throwable t) {
					ToastUtils.showShort(t.getMessage());
				}
			});

		}
	}

	private void onFavority() {
		if (book.isFavority) {
			onDeleteFavority();
		} else {
			cm.addFavorityCookbooks(book.id, new VoidCallback() {

				@Override
				public void onSuccess() {
					ToastUtils.showShort("收藏成功!");
				}

				@Override
				public void onFailure(Throwable t) {
					ToastUtils.showShort(t.getMessage());
				}
			});

		}
	}

	private void onDelete() {

		if (!UiHelper.checkAuth(PageKey.UserLogin))
			return;

		switch (modelType) {
		case RecipeAdapter.Model_Today:
			onDeleteToday();
			break;
		case RecipeAdapter.Model_Favority:
			onDeleteFavority();
			break;
		default:
			break;
		}
	}

	void onDeleteToday() {
		cm.deleteTodayCookbook(book.id, new VoidCallback() {

			@Override
			public void onSuccess() {
				ToastUtils.showShort("取消今日菜谱成功!");
			}

			@Override
			public void onFailure(Throwable t) {
				ToastUtils.showShort(t.getMessage());
			}
		});
	}

	void onDeleteFavority() {
		cm.deleteFavorityCookbooks(book.id, new VoidCallback() {

			@Override
			public void onSuccess() {
				ToastUtils.showShort("取消收藏成功!");
			}

			@Override
			public void onFailure(Throwable t) {
				ToastUtils.showShort(t.getMessage());
			}
		});
	}

}
