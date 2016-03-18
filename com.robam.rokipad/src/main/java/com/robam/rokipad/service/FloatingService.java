package com.robam.rokipad.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.events.CookCountdownEvent;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageArgumentKey;

/**
 * @author:Jack Tony
 * 
 *              重要：注意要申请权限！！！！ <!-- 悬浮窗的权限 --> <uses-permission
 *              android:name="android.permission.SYSTEM_ALERT_WINDOW" />
 * 
 * @tips :思路： 1.获得一个windowManager类 2.通过wmParams设置好windows的各种参数
 *       3.获得一个视图的容器，找到悬浮窗视图的父控件，比如linearLayout 4.将父控件添加到WindowManager中去
 *       5.通过这个父控件找到要显示的悬浮窗图标，并进行拖动或点击事件的设置
 * @date :2014-9-25
 */
public class FloatingService extends Service {

	static FloatingService service;

	static public void start(Context cx, long bookId) {
		Bundle bd = new Bundle();
		bd.putLong(PageArgumentKey.BookId, bookId);

		Intent intent = new Intent(cx, FloatingService.class);
		intent.putExtras(bd);
		cx.startService(intent);
	}

	static public void showIfExisted() {
		if (service == null)
			return;

		service.show();
	}

	static public void pauseIfExisted() {
		if (service == null)
			return;

		service.pause();
	}

	/**
	 * 定义浮动窗口布局
	 */
	LinearLayout mlayout;
	/**
	 * 悬浮窗控件
	 */
	TextView tvFloatTime;
	TextView tvFloatName;
	LinearLayout lyFloat;
	/**
	 * 悬浮窗的布局
	 */
	WindowManager.LayoutParams wmParams;
	LayoutInflater inflater;
	/**
	 * 创建浮动窗口设置布局参数的对象
	 */
	WindowManager mWindowManager;

	// 触摸监听器
	GestureDetector mGestureDetector;

	Bundle bundle;
	Recipe book;
	int currentStepNum, remainTime;
	boolean isStarted = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bd = intent.getExtras();
		long bookId = bd.getLong(PageArgumentKey.BookId);

		CookbookManager.getInstance().getCookbookById(bookId,
				new Callback<Recipe>() {

					@Override
					public void onSuccess(Recipe result) {
						book = result;

						initFloating(book);// 设置悬浮窗图标
					}

					@Override
					public void onFailure(Throwable t) {
						ToastUtils.showThrowable(t);
					}
				});

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		EventUtils.regist(this);
		initWindow();// 设置窗口的参数
		service = this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventUtils.unregist(this);
		if (mlayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mlayout);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Subscribe
	public void onEvent(final CookCountdownEvent event) {
		if (mlayout == null)
			return;

		mlayout.post(new Runnable() {

			@Override
			public void run() {
				setTextTime(event.remainTime);
			}
		});

	}

	public void pause() {
		if (mlayout != null) {
			mlayout.setVisibility(View.GONE);
		}
	}

	public void show() {
		if (mlayout != null) {
			mlayout.setVisibility(View.VISIBLE);
		}
	}

	// /////////////////////////////////////////////////////////////////////

	/**
	 * 初始化windowManager
	 */
	@SuppressLint("InflateParams")
	private void initWindow() {
		mWindowManager = (WindowManager) getApplication().getSystemService(
				Context.WINDOW_SERVICE);
		wmParams = getParams(wmParams);// 设置好悬浮窗的参数
		// 悬浮窗默认显示以左上角为起始坐标
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
		wmParams.x = 1070;
		wmParams.y = 657;
		// 得到容器，通过这个inflater来获得悬浮窗控件
		inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mlayout = (LinearLayout) inflater.inflate(R.layout.view_float, null);
		// 添加悬浮窗的视图
		mWindowManager.addView(mlayout, wmParams);
	}

	/**
	 * 对windowManager进行设置
	 * 
	 * @param wmParams
	 * @return
	 */
	public WindowManager.LayoutParams getParams(
			WindowManager.LayoutParams wmParams) {
		wmParams = new WindowManager.LayoutParams();
		// 设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
		// wmParams.type = LayoutParams.TYPE_PHONE;
		// wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		// wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		// 设置可以显示在状态栏上
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		return wmParams;
	}

	/**
	 * 找到悬浮窗的图标，并且设置事件 设置悬浮窗的点击、滑动事件
	 */
	private void initFloating(Recipe book) {
		tvFloatTime = (TextView) mlayout.findViewById(R.id.tv_time);
		tvFloatName = (TextView) mlayout.findViewById(R.id.tv_name);
		lyFloat = (LinearLayout) mlayout.findViewById(R.id.float_layout);
		tvFloatName.setText(book.name);
		setTextTime(0);

		mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
		// 设置监听器
		lyFloat.setOnTouchListener(new FloatingListener());
	}

	private void setTextTime(int time) {
		String strTime = UiHelper.second2String(time);
		tvFloatTime.setText(strTime);
	}

	// 开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
	private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
	// 开始时的坐标和结束时的坐标（相对于自身控件的坐标）
	private int mStartX, mStartY, mStopX, mStopY;
	private boolean isMove;// 判断悬浮窗是否移动

	/**
	 * @author:金凯
	 * @tips :自己写的悬浮窗监听器
	 * @date :2014-3-28
	 */
	private class FloatingListener implements OnTouchListener {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View arg0, MotionEvent event) {

			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				isMove = false;
				mTouchStartX = (int) event.getRawX();
				mTouchStartY = (int) event.getRawY();
				mStartX = (int) event.getX();
				mStartY = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				mTouchCurrentX = (int) event.getRawX();
				mTouchCurrentY = (int) event.getRawY();
				wmParams.x += mTouchCurrentX - mTouchStartX;
				wmParams.y += mTouchCurrentY - mTouchStartY;
				mWindowManager.updateViewLayout(mlayout, wmParams);

				mTouchStartX = mTouchCurrentX;
				mTouchStartY = mTouchCurrentY;
				break;
			case MotionEvent.ACTION_UP:
				mStopX = (int) event.getX();
				mStopY = (int) event.getY();
				// System.out.println("|X| = "+ Math.abs(mStartX - mStopX));
				// System.out.println("|Y| = "+ Math.abs(mStartY - mStopY));
				if (Math.abs(mStartX - mStopX) >= 1
						|| Math.abs(mStartY - mStopY) >= 1) {
					isMove = true;
				}
				break;
			}
			return mGestureDetector.onTouchEvent(event); // 此处必须返回false，否则OnClickListener获取不到监听
		}
	}


	class MyOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (!isMove) {
				CookTaskService.getInstance().maximize();
				service.stopSelf();
				service = null;
			}
			return super.onSingleTapConfirmed(e);
		}
	}

}
