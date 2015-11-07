package com.robam.common.services;

import android.content.Context;
import android.content.DialogInterface;

import com.legent.VoidCallback;
import com.legent.services.AbsService;
import com.legent.services.TaskService;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.utils.EventUtils;
import com.robam.common.events.CookCountdownEvent;
import com.robam.common.pojos.CookStep;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.device.Stove;
import com.robam.common.pojos.device.Stove.StoveHead;
import com.robam.common.pojos.device.StoveStatus;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.ui.dialog.PauseDialog;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

abstract public class AbsCookTaskService extends AbsService {

    protected AbsFan fan;
    protected Stove stove;
    protected StoveHead stoveHead;
    protected Recipe book;
    protected List<CookStep> steps;
    protected long startTime, endTime;

    protected boolean isRunning, isPause, isMinimized;
    protected int stepIndex, remainTime;
    protected ScheduledFuture<?> future;

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPause() {
        return isPause;
    }

    public boolean isMinimized() {
        return isMinimized;
    }

    public int getStepCount() {
        return steps != null ? steps.size() : 0;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public int getRemainTime() {
        return remainTime;
    }

    /**
     * 启动烧菜
     *
     * @param book
     */
    public void start(StoveHead stoveHead, Recipe book) {
        if (isRunning)
            return;

        this.book = book;
        this.stoveHead = stoveHead;
        this.startTime = Calendar.getInstance().getTimeInMillis();

        if (stoveHead != null) {
            stove = stoveHead.parent;
            fan = (AbsFan)stove.getParent();
        }

        steps = book.getCookSteps();
        stepIndex = -1;
        isRunning = true;
        isMinimized = false;

        next();
        onShowCookingView();
    }

    /**
     * 烧菜完成
     */
    public void stop() {

        this.endTime = Calendar.getInstance().getTimeInMillis();
        setFanLevel(0);
        setStoveStatus(StoveStatus.Off);

        stopCountdown();
        onStoped();

        this.stove = null;
        this.book = null;
        this.stepIndex = -1;
        isRunning = false;
    }

    /**
     * 执行下一步工序
     *
     * @return
     */
    public void next() {

        //是否最后一个步骤
        boolean isLastStep = stepIndex + 1 == steps.size();
        if (isLastStep) {
            //是否仍在倒计时
            boolean isCountdown = remainTime > 0;
            if (isCountdown) {
                // 仍在倒计时
                onAskAtEnd();
            } else {
                // 倒计时完成
                stopCountdown();
                stepIndex++;

                stop();
            }

        } else {
            // 中间步骤
            stopCountdown();
            stepIndex++;

            CookStep step = steps.get(stepIndex);
            setCommand(step);
        }

    }

    /**
     * 执行上一步工序
     *
     * @return
     */
    public void back() {
        if (stepIndex == 0)
            return;

        stepIndex--;

        CookStep step = steps.get(stepIndex);
        setCommand(step);
    }

    /**
     * 暂停
     */
    public void pause() {
        if (isPause)
            return;
        isPause = true;

        if (isRunning) {
            stopCountdown();
        }

        if (fan != null) {
            fan.pause();
        }
        if (stoveHead != null) {
            stoveHead.pause();
        }
        onPause();
    }

    /**
     * 从暂停恢复
     */
    public void restore() {

        if (!isPause)
            return;

        if (isRunning) {
            startCountdown(remainTime);
        }

        if (fan != null) {
            fan.restore();
        }
        if (stoveHead != null) {
            stoveHead.restore();
        }

        onRestore();
        isPause = false;
    }

    /**
     * 烧菜界面最小化，回到桌面
     */
    public void minimize() {
        isMinimized = true;
        onMinimize();

    }

    /**
     * 烧菜界面最大化
     */
    public void maximize() {
        isMinimized = false;
        onMaximize();
    }

    // -----------------------------------------------------------------------------------------------------
    // dynamic
    // -----------------------------------------------------------------------------------------------------

    protected void onShowCookingView() {
        // TODO
    }

    protected void onStoped() {

        if (fan != null && book != null) {
            boolean isLastStep = stepIndex + 1 >= steps.size();
            boolean isNotCountdown = remainTime <= 0;
            boolean isBroken = !(isLastStep && isNotCountdown);

            CookbookManager.getInstance().addCookingLog(fan.getID(), book, startTime, endTime, isBroken, new VoidCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    protected void onPause() {
        Context cx = UIService.getInstance().getTop().getActivity();
        PauseDialog.show(cx, new PauseDialog.DialogCallback() {

            @Override
            public void onRestore() {
                restore();
            }
        });

    }

    protected void onRestore() {
        // TODO
    }

    protected void onMinimize() {
        // TODO
    }

    protected void onMaximize() {
        // TODO
    }

    protected void onAskAtEnd() {
        Context cx = UIService.getInstance().getTop().getActivity();
        String message = "已到最后一步,是否退出？";
        DialogHelper.newDialog_OkCancel(cx, null, message,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dlg, int witch) {
                        if (witch == DialogInterface.BUTTON_POSITIVE) {
                            stop();
                        }
                    }
                }).show();
    }

    // -----------------------------------------------------------------------------------------------------
    // private
    // -----------------------------------------------------------------------------------------------------

    protected void setCommand(CookStep step) {
        setStepParams(step);
        startCountdown(step.needTime);
    }

    protected void setStepParams(final CookStep step) {

        TaskService.getInstance().postUiTask(new Runnable() {

            @Override
            public void run() {
                setFanLevel(step.fanLevel);
                setStoveLevel(step.stoveLevel);
            }
        }, 1000 * 2);
    }

    protected void startCountdown(final int needTime) {

        if (!isRunning)
            return;

        stopCountdown();
        remainTime = needTime;

        future = TaskService.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                remainTime--;
                onCount(remainTime);

                if (remainTime <= 0) {
                    stopCountdown();
                }
            }
        }, 1000 * 2, 1000, TimeUnit.MILLISECONDS);

    }

    protected void stopCountdown() {

        if (!isRunning)
            return;

        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    protected void onCount(final int count) {

        EventUtils.postEvent(new CookCountdownEvent(stepIndex, count));

        if (count <= 0) {
            if (stove == null || stoveHead == null)
                return;
            if (stoveHead.status == StoveStatus.StandyBy
                    || stoveHead.status == StoveStatus.Off
                    || stoveHead.level == Stove.PowerLevel_0)
                return;


            setStoveLevel(Stove.PowerLevel_1);
        }
    }

    protected void setFanLevel(int level) {
        if (fan != null) {
            fan.setFanLevel((short) level, null);
        }
    }

    protected void setStoveStatus(int status) {
        if (stove != null) {
            stove.setStoveStatus(true, stoveHead.ihId, (short) status, null);
        }
    }

    protected void setStoveLevel(int level) {
        if (stove != null) {

            if (stoveHead.status == StoveStatus.StandyBy
                    || stoveHead.status == StoveStatus.Off
                    || stoveHead.level == Stove.PowerLevel_0) {
                setStoveStatus(StoveStatus.StandyBy);
            }

            stove.setStoveLevel(true, stoveHead.ihId, (short) level, null);
        }
    }

}
