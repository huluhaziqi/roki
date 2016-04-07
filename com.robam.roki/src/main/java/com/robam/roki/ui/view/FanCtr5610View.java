package com.robam.roki.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.legent.VoidCallback;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.device.fan.Fan8229;
import com.robam.common.pojos.device.fan.Fan8700;
import com.robam.common.pojos.device.fan.FanStatus;
import com.robam.common.pojos.device.fan.IFan;
import com.robam.roki.R;
import com.robam.roki.ui.CircularProgressDrawable;
import com.robam.roki.ui.FramesSequenceAnimation;
import com.robam.roki.ui.UIListeners;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FanCtr5610View extends FrameLayout implements UIListeners.IFanCtrView {

    Fan8229 fan;

    @InjectView(R.id.gearLow)
    DeviceFanGearView gearLow;
    @InjectView(R.id.gearHigh)
    DeviceFanGearView gearHigh;
    @InjectView(R.id.imgLight)
    ImageView imgLight;
    @InjectView(R.id.img_logo)
    RelativeLayout img_logo;

    //工作动画
    private ImageView gearLowImg;
    private ImageView gearHighImg;
    private Animation operatingAnim;
    //油网动画
    @InjectView(R.id.you_wang_dh)
    ImageView ywImg;
    FramesSequenceAnimation animation;
    //开机动画
    @InjectView(R.id.kj_dh)
    FrameLayout kjDh;
    @InjectView(R.id.iv_drawable)
    ImageView ivDrawable;
    @InjectView(R.id.img)
    ImageView kjImg;
    CircularProgressDrawable drawable;
    Animator currentAnimation;
    Animation imgPushOut;
    Animation imgPushIn;

    public FanCtr5610View(Context context) {
        super(context);
        init(context, null);
    }

    public FanCtr5610View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_5610, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            //初始化动画
            initAnimotion(cx);
        }


    }

    //开启油网动画
    @OnClick(R.id.btn)
    public void onClickYW() {
        img_logo.setVisibility(GONE);
        kjDh.setVisibility(GONE);
        ywImg.setVisibility(VISIBLE);
        animation.start();
    }

    @OnClick(R.id.img_logo)
    public void onClickLogo() {
        setLight();
        short status = (fan.status == FanStatus.Off) ? FanStatus.On
                : FanStatus.Off;
        setStatus(status);
        //开机动画
        img_logo.setVisibility(GONE);
        ywImg.setVisibility(GONE);
        kjDh.setVisibility(VISIBLE);
        currentAnimation.start();
    }

    @OnClick(R.id.imgLight)
    public void onClickLight() {
        setLight();
    }

    @OnClick(R.id.gearLow)
    public void onClickLow() {
        if (fan.level == fan.PowerLevel_1)
            setLevel(fan.PowerLevel_0);
        else
            setLevel(fan.PowerLevel_1);
    }

    @OnClick(R.id.gearHigh)
    public void onClickHigh() {
        if (fan.level == fan.PowerLevel_6 || fan.level == fan.PowerLevel_3)
            setLevel(fan.PowerLevel_0);
        else
            setLevel(fan.PowerLevel_6);
    }


    @Override
    public void attachFan(IFan fan) {
        Preconditions.checkState(fan instanceof Fan8229, "attachFan error:not 8700");
        this.fan = (Fan8229) fan;
    }

    @Override
    public void onRefresh() {
        if (fan == null)
            return;
        if (fan.status==0){
            gearLow.setVisibility(GONE);
            gearHigh.setVisibility(GONE);
        }
        if (fan.level==1){
            kjDh.setVisibility(GONE);
            img_logo.setVisibility(VISIBLE);
        }
        imgLight.setSelected(fan.light);
        showLevel(fan.level);
    }

    void showLevel(int level) {
        gearLow.setSelected(false);
        gearHigh.setSelected(false);
        gearLow.setTitle("弱", "档");
        gearHigh.setTitle("爆", "炒");

        if (level == fan.PowerLevel_0) {
            //关闭工作动画
            gearLowImg.clearAnimation();
            gearHighImg.clearAnimation();
            return;
        }

        boolean isLow = level < fan.PowerLevel_3;
        boolean isLevel3 = level == fan.PowerLevel_3;
        gearLow.setSelected(isLow);
        gearHigh.setSelected(!isLow);
        //设置工作动画
        setAnimotion(isLow);

        if (!isLow && isLevel3) {
            gearHigh.setTitle("强", "档");
        }

    }

    void setLight() {
//        if (!checkConnection())
//            return;

        fan.setFanLight(!fan.light, new VoidCallback() {

            @Override
            public void onSuccess() {
                imgLight.setSelected(fan.light);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void setLevel(final int level) {
//        if (!checkConnection())
//            return;

        fan.setFanLevel((short) level, new VoidCallback() {

            @Override
            public void onSuccess() {
                showLevel(level);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });

    }

    public void setAnimotion(boolean isLow) {
        if (isLow) {
            //开启弱档动画
            if (operatingAnim != null)
                gearLowImg.startAnimation(operatingAnim);
            gearHighImg.clearAnimation();
        } else {
            //开启强档动画
            if (operatingAnim != null)
                gearHighImg.startAnimation(operatingAnim);
            gearLowImg.clearAnimation();
        }
    }

    private void initAnimotion(Context cx) {
        //工作动画初始化
        initWorkDh(cx);
        //油网动画初始化
        initYwDh(cx);
        //开机动画
        initKjDh(cx);

    }

    boolean checkConnection() {
        if (!fan.isConnected()) {
            ToastUtils.showShort(R.string.fan_invalid_error);
            return false;
        } else {
            return true;
        }
    }


    private void initWorkDh(Context cx) {
        operatingAnim = AnimationUtils.loadAnimation(cx, R.anim.fan_run_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        gearLowImg = gearLow.getImg();
        gearHighImg = gearHigh.getImg();
    }

    private void initYwDh(final Context cx) {
        animation = new FramesSequenceAnimation(cx, ywImg, R.array.annimortion, 100);
        animation.setFramesSequenceAnimationListener(new FramesSequenceAnimation.FramesSequenceAnimationListener() {
            @Override
            public void AnimationStopped() {
                //动画停止
                Toast.makeText(cx, "已结束动画", Toast.LENGTH_SHORT).show();
                ywImg.setVisibility(GONE);
                kjDh.setVisibility(GONE);
                img_logo.setVisibility(VISIBLE);
            }

            @Override
            public void AnimationStarted() {

            }

            @Override
            public void AnimationCompleted() {
                //动画播放完成
                animation.stop();
            }
        });
    }

    private void initKjDh(Context context) {
        imgPushIn = AnimationUtils.loadAnimation(context, R.anim.push_in);
        imgPushIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                kjImg.clearAnimation();
//                kjImg.startAnimation(imgPushOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        imgPushOut = AnimationUtils.loadAnimation(context, R.anim.push_out);
//        imgPushOut.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                kjImg.clearAnimation();
//                kjImg.startAnimation(imgPushIn);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

        drawable = new CircularProgressDrawable.Builder()
                .setRingWidth(2)
                .setOutlineColor(getResources().getColor(R.color.c01))
                .setRingColor(getResources().getColor(R.color.home_bg))
                .setCenterColor(getResources().getColor(R.color.Black_80))
                .create();
        ivDrawable.setImageDrawable(drawable);
        currentAnimation = prepareStyle3Animation();
        currentAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                kjImg.startAnimation(imgPushIn);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private Animator prepareStyle3Animation() {
        AnimatorSet animation = new AnimatorSet();

        ObjectAnimator invertedProgress = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.PROGRESS_PROPERTY, 0f, 1.0f);
        invertedProgress.setDuration(8000);
        invertedProgress.setInterpolator(new OvershootInterpolator());

        Animator invertedCircle = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.CIRCLE_SCALE_PROPERTY, 0.5f, 0.75f);
        invertedCircle.setDuration(8000);
        invertedCircle.setStartDelay(800);
        invertedCircle.setInterpolator(new OvershootInterpolator());

        animation.playTogether(invertedCircle, invertedProgress);
        return animation;
    }
    void setStatus(short status) {

        if (!checkConnection())
            return;

        fan.setFanStatus(status, new VoidCallback() {

            @Override
            public void onSuccess() {
                onRefresh();
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

}
