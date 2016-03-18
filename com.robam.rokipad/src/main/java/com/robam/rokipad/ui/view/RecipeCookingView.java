package com.robam.rokipad.ui.view;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.utils.EventUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.events.CookCountdownEvent;
import com.robam.common.pojos.CookStep;
import com.robam.common.pojos.Recipe;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.service.CookTaskService;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecipeCookingView extends FrameLayout {

	static public interface CookingNextCallback {
		void onClickNext();
	}

	@InjectView(R.id.imgRecipe)
	ImageView imgRecipe;

	@InjectView(R.id.txtRecipe)
	TextView txtRecipe;

	@InjectView(R.id.txtStepIndex)
	TextView txtStepIndex;

	@InjectView(R.id.txtStepCount)
	TextView txtStepCount;

	@InjectView(R.id.txtStepDesc)
	TextView txtStepDesc;

	@InjectView(R.id.txtFan)
	TextView txtFan;

	@InjectView(R.id.txtStove)
	TextView txtStove;

	@InjectView(R.id.pnlNext)
	View pnlNext;

	@InjectView(R.id.txtNext)
	TextView txtNext;

	@InjectView(R.id.imgNext)
	ImageView imgNext;

	@InjectView(R.id.pnlCountdown)
	View pnlCountdown;

	@InjectView(R.id.txtCountdown)
	TextView txtCountdown;

	@InjectView(R.id.imgCountdownNext)
	ImageView imgCountdownNext;

	Recipe book;
	CookStep step;
	int stepIndex;
	List<CookStep> steps;
	CookingNextCallback callback;

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		EventUtils.regist(this);

		if (step != null) {
			
			int remainTime = CookTaskService.getInstance().getRemainTime();
			setCountdownTime(remainTime);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		EventUtils.unregist(this);
	}

	public RecipeCookingView(Context context, Recipe book, int stepIndex,
			CookingNextCallback callback) {
		super(context);
		this.book = book;
		this.stepIndex = stepIndex;
		this.callback = callback;

		this.steps = book.getCookSteps();
		this.step = steps.get(stepIndex);

		init(context, null);
	}

	@OnClick({ R.id.pnlNext, R.id.pnlCountdown })
	public void onClickNext() {
		onNext();
	}

	@Subscribe
	public void onEvent(CookCountdownEvent event) {
		if (stepIndex != event.stepIdnex)
			return;

		setCountdownTime(event.remainTime);
	}

	void init(Context cx, AttributeSet attrs) {

		View view = LayoutInflater.from(cx).inflate(
				R.layout.view_recipe_cooking, this, true);
		if (!view.isInEditMode()) {
			ButterKnife.inject(this, view);
			txtStepDesc.setMovementMethod(ScrollingMovementMethod.getInstance());

			loadData();
		}

	}

	void loadData() {
		ImageUtils.displayImage(step.imageUrl, imgRecipe);

		txtRecipe.setText(book.name);
		txtStepDesc.setText(step.desc);
		txtStepIndex.setText(String.format("%s", stepIndex + 1));
		txtStepCount.setText(String.format("/%s.", book.getCookSteps().size()));

		//
		txtFan.setText(String.format("%s", step.fanLevel));
		txtStove.setText(String.format("%s", step.stoveLevel));

		//
		boolean isNotLastStep = stepIndex < steps.size() - 1;
		txtNext.setText(isNotLastStep ? "下一步" : "退出");
		imgNext.setImageResource(isNotLastStep ? R.mipmap.ic_recipe_cooking_next
				: R.mipmap.ic_recipe_cooking_exit);
	}

	void onNext() {
		if (stepIndex == steps.size() - 1) {
			CookTaskService.getInstance().next();
		} else {
			if (callback != null) {
				callback.onClickNext();
			}
		}
	}

	void setCountdownTime(int time) {

		String strTime = UiHelper.second2String(time);
		final boolean isCountdown = time > 0;

		txtCountdown.setText(strTime);
		pnlCountdown.setVisibility(isCountdown ? VISIBLE : GONE);
		pnlNext.setVisibility(!isCountdown ? VISIBLE : GONE);
	}

}
