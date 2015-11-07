package com.robam.rokipad.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.ui.UIService;
import com.legent.ui.ext.utils.OnTouchListenerWithAntiShake;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.pojos.Advert;
import com.robam.common.pojos.Advert.PadAdvert;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.PageKey;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeAdvertItemView extends FrameLayout {

	static final public String TAG_Today_Menu = "todayMenu";

	public HomeAdvertItemView(Context context) {
		super(context);
		init(context, null);
	}

	public HomeAdvertItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HomeAdvertItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@InjectView(R.id.imgAdvert)
	ImageView imgAdvert;

	@InjectView(R.id.txtTitle)
	TextView txtTitle;

	List<Advert> list = Lists.newArrayList();

	void init(Context cx, AttributeSet attrs) {

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_home_left_advert_item, this, true);
        view.setOnTouchListener(new OnTouchListenerWithAntiShake(view, new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickView();
            }
        }));
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);

			if (attrs != null) {
				TypedArray ta = cx.obtainStyledAttributes(attrs,
						R.styleable.AdvertItemView);

				String title = ta.getString(R.styleable.AdvertItemView_title);
				int imgResid = ta.getResourceId(
						R.styleable.AdvertItemView_imgBackgroud,
						R.mipmap.img_home_love_meal_bg);
				ta.recycle();

				txtTitle.setText(title);
				ImageUtils.displayImage(ImageUtils.fromDrawable(imgResid),
						imgAdvert);
			}

		}
	}

	public void setAdverts(List<PadAdvert> adverts) {
		if (adverts == null || adverts.size() == 0)
			return;

		Advert ad = adverts.get(0);
		setAdvert(ad.desc, ad);
	}

	public void setAdvert(String title, Advert advert) {
		txtTitle.setText(title);
		if (advert == null) {
		} else {
			this.setTag(advert);
			ImageUtils.displayImage(advert.imgUrl, imgAdvert);
		}
	}

    void onClickView() {
        Object tag = getTag();
        if (tag == null)
            return;

		if (TAG_Today_Menu.equals(tag.toString())) {
			onToday();
		} else if (tag instanceof Advert) {
			onAdvert((Advert) tag);
		} else {
		}
	}

	void onAdvert(Advert ad) {

		Bundle bd = new Bundle();

		if (ad.type == Advert.Type_Link) {
			bd.putString(PageArgumentKey.Url, ad.content);
			UIService.getInstance().postPage(PageKey.WebClient, bd);
		} else if (ad.type == Advert.Type_Cookbook) {
			long bookId = Long.parseLong(ad.content);
			bd.putLong(PageArgumentKey.BookId, bookId);
			UIService.getInstance().postPage(PageKey.RecipeIntroduce, bd);
		}
	}

	void onToday() {
		UIService.getInstance().postPage(PageKey.RecipeToday);
	}

}
