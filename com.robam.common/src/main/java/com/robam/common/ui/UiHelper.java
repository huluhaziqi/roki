package com.robam.common.ui;

import android.content.Context;
import android.content.DialogInterface;

import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.utils.api.NetworkUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.AbsRecipe;
import com.robam.common.services.CookbookManager;

public class UiHelper {

    public static String second2String(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;

        String strTime = String.format("%s:%s", String.format("%02d", min),
                String.format("%02d", sec));

        return strTime;
    }

    public static boolean isNetworkConnected(Context cx) {
        return NetworkUtils.isConnect(cx);
    }

    public static boolean checkAuth(String loginPageKey) {
        boolean isAuth = Plat.accountService.isLogon();
        if (isAuth) {
            return true;
        } else {
            UIService.getInstance().postPage(loginPageKey);
            return false;
        }
    }

    public static boolean checkAuthWithDialog(Context cx, final String loginPageKey) {
        boolean isAuth = Plat.accountService.isLogon();
        if (isAuth) {
            return true;
        } else {
            DialogHelper.newDialog_OkCancel(cx, "您还未登录，快去登录吧~", null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        UIService.getInstance().postPage(loginPageKey);
                    }
                }
            }).show();
            return false;
        }
    }

    // ----------------------------------------Recipe-------------------------------------------

    public static void onToday(final AbsRecipe book) {

        if (book == null) return;

        CookbookManager cm = CookbookManager.getInstance();

        if (book.isToday) {
            cm.deleteTodayCookbook(book.id, new VoidCallback() {

                @Override
                public void onSuccess() {
                    book.isToday = false;
                    ToastUtils.showShort("已经从购物车中移除!");
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showShort(t.getMessage());
                }
            });
        } else {
            cm.addTodayCookbook(book.id, new VoidCallback() {

                @Override
                public void onSuccess() {
                    book.isToday = true;
                    ToastUtils.showShort("已经加入到购物车!");
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showShort(t.getMessage());
                }
            });

        }
    }

    public static void onFavority(final AbsRecipe book) {
        if (book == null) return;

        CookbookManager cm = CookbookManager.getInstance();

        if (book.isFavority) {
            cm.deleteFavorityCookbooks(book.id, new VoidCallback() {

                @Override
                public void onSuccess() {
                    book.isFavority = false;
                    ToastUtils.showShort("取消收藏成功!");
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showShort(t.getMessage());
                }
            });
        } else {
            cm.addFavorityCookbooks(book.id, new VoidCallback() {

                @Override
                public void onSuccess() {
                    book.isFavority = true;
                    ToastUtils.showShort("收藏成功!");
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showShort(t.getMessage());
                }
            });

        }
    }


}
