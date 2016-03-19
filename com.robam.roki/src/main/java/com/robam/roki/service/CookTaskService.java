package com.robam.roki.service;

import android.os.Bundle;

import com.legent.plat.Plat;
import com.legent.services.TaskService;
import com.legent.ui.UIService;
import com.legent.utils.speech.SpeechManager;
import com.robam.common.pojos.CookStep;
import com.robam.common.pojos.CookStepTip;
import com.robam.common.services.AbsCookTaskService;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;

import java.util.List;

public class CookTaskService extends AbsCookTaskService {

    static private CookTaskService instance = new CookTaskService();

    synchronized public static CookTaskService getInstance() {
        return instance;
    }

    private CookTaskService() {
        SpeechManager.getInstance().init(Plat.app);
    }

    @Override
    public void dispose() {
        super.dispose();
        SpeechManager.getInstance().dispose();
    }

    @Override
    protected void onShowCookingView() {
        Bundle bd = new Bundle();
        bd.putLong(PageArgumentKey.BookId, book.id);
        bd.putInt(PageArgumentKey.RecipeStepIndex, stepIndex);
        UIService.getInstance().postPage(PageKey.RecipeCooking, bd);
    }

    @Override
    protected void onStoped() {
        super.onStoped();
        UIService.getInstance().returnHome();
    }

    @Override
    protected void onCount(int count) {
        super.onCount(count);
        CookStep step = steps.get(stepIndex);
        List<CookStepTip> tips = step.getCookStepTips();
        if (tips != null && tips.size() > 0) {
            int lostTime = step.needTime - count;
            for (CookStepTip tip : tips) {
                if (tip.time == lostTime) {
                    speak(tip.prompt);
                }
            }
        }

    }

    private void speak(final String text) {

        TaskService.getInstance().postUiTask(new Runnable() {

            @Override
            public void run() {
                SpeechManager.getInstance().startSpeaking(text);
            }
        }, 500);
    }
}
