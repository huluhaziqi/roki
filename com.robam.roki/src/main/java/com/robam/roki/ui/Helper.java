package com.robam.roki.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.PopupWindow;

import com.legent.Callback;
import com.legent.Callback2;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.ui.UIService;
import com.legent.ui.ext.popoups.BasePickerPopupWindow;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.collection.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.robam.common.Utils;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.Stove.StoveStatus;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.roki.R;
import com.robam.roki.model.CrmArea;
import com.robam.roki.service.CookTaskService;
import com.robam.roki.ui.dialog.ChooseStoveByManualDialog;
import com.robam.roki.ui.dialog.ChooseStoveByWaitDialog;
import com.robam.roki.ui.form.MainActivity;
import com.robam.roki.ui.view.CrmAreaWheelView;
import com.robam.roki.ui.view.OrderAreaWheelView;

/**
 * Created by sylar on 15/6/11.
 */
public class Helper {

    public static DisplayImageOptions DisplayImageOptions_UserFace = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.ic_user_default_figure)
            .showImageForEmptyUri(R.mipmap.ic_user_default_figure)
            .showImageOnFail(R.mipmap.ic_user_default_figure).cacheInMemory(true)
            .cacheOnDisk(true).displayer(new CircleBitmapDisplayer()).build();

    public static void login(final Activity atv, String account, String pwdMd5) {
        Plat.accountService.login(account, pwdMd5, new Callback<User>() {

            @Override
            public void onSuccess(User user) {
                Helper.onLoginCompleted(atv, user);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    public static void login(String account, String pwdMd5) {
        Plat.accountService.login(account, pwdMd5, new Callback<User>() {

            @Override
            public void onSuccess(User user) {
                Helper.onLoginCompleted(user);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    public static void onLoginCompleted(Activity atv, final User user) {

        ToastUtils.showShort("登录成功");
        if (atv instanceof MainActivity) {
            UIService.getInstance().returnHome();
        } else {
            MainActivity.start(atv);
        }

    }

    public static void onLoginCompleted(final User user) {
        UIService.getInstance().returnHome();
    }

    public static void startCook(Context cx, Recipe recipe) {
        if (CookTaskService.getInstance().isRunning()) {
            ToastUtils.showShort("正在烧菜中,不可同时烧菜!");
            return;
        }

        Stove stove = Utils.getDefaultStove();
        AbsFan fan = Utils.getDefaultFan();

        if (stove == null) {
            if (fan == null) {
                Bundle bd = new Bundle();
                bd.putLong(PageArgumentKey.BookId, recipe.id);
                UIService.getInstance().postPage(PageKey.CookWithoutDevice, bd);
            } else {
                CookTaskService.getInstance().start(null, recipe);
            }
        } else {

            boolean isOffLeft = stove.leftHead != null && stove.leftHead.status == StoveStatus.Off;
            boolean isOffRight = stove.rightHead != null && stove.rightHead.status == StoveStatus.Off;

            if (isOffLeft != isOffRight) {
                //有一个是开机状态
                startCook(!isOffLeft ? stove.leftHead : stove.rightHead, recipe);
            } else {
                //左右灶状态一样
                if (isOffLeft) {
                    //都是关机状态
                    ChooseStoveByWaitDialog.show(cx, recipe);
                } else {
                    //都是开机状态

                    boolean isStandByLeft = stove.leftHead != null && stove.leftHead.status == StoveStatus.StandyBy;
                    boolean isStandByRight = stove.rightHead != null && stove.rightHead.status == StoveStatus.StandyBy;
                    if (isStandByLeft != isStandByRight) {
                        //其中有一个是待机状态
                        startCook(isStandByLeft ? stove.leftHead : stove.rightHead, recipe);
                    } else {
                        //都是待机或都是工作中时，需要选择
                        ChooseStoveByManualDialog.show(cx, recipe);
                    }

                }
            }
        }
    }

    public static void startCook(Stove.StoveHead head, Recipe recipe) {
        CookTaskService.getInstance().start(head, recipe);
    }

    public static PopupWindow newCrmAreaPicker(Context cx, final Callback2<CrmArea> callback) {
        final CrmAreaWheelView view = new CrmAreaWheelView(cx);
        BasePickerPopupWindow.PickListener listener = new BasePickerPopupWindow.PickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                if (callback != null) {
                    callback.onCompleted(view.getSelected());
                }
            }
        };

        BasePickerPopupWindow pop = new BasePickerPopupWindow(cx, view);
        pop.setPickListener(listener);
        return pop;
    }


    public static PopupWindow newOrderAreaPicker(Context cx, final Callback2<String> callback) {
        final OrderAreaWheelView view = new OrderAreaWheelView(cx);
        BasePickerPopupWindow.PickListener listener = new BasePickerPopupWindow.PickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                if (callback != null) {
                    callback.onCompleted(view.getSelected());
                }
            }
        };

        BasePickerPopupWindow pop = new BasePickerPopupWindow(cx, view);
        pop.setPickListener(listener);
        return pop;
    }

}
