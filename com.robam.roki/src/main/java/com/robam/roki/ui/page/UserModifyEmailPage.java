package com.robam.roki.ui.page;

import android.os.Bundle;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.ui.UIService;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.StringUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.roki.ui.PageArgumentKey;

/**
 * Created by sylar on 15/6/16.
 */
public class UserModifyEmailPage extends AbsModifyTextPage {

    User user;

    @Override
    void initData(Bundle bd) {
        user = bd.getParcelable(PageArgumentKey.User);
        editText.setHint("邮箱");
        editText.setText(user.email);
    }

    @Override
    void onConfirm() {
        String email = editText.getText().toString();
        Preconditions.checkNotNull(email, "邮箱不可为空");
        Preconditions.checkNotNull(StringUtils.isEmail(email), "邮箱格式错误");

        if (Objects.equal(user.email, email)) {
            UIService.getInstance().popBack();
        } else {
            checkEmail(email);
        }
    }

    void checkEmail(final String email) {
        ProgressDialogHelper.setRunning(cx, true);
        Plat.accountService.isExisted(email,
                new Callback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        ProgressDialogHelper.setRunning(cx, false);
                        if (result) {
                            ToastUtils.showShort("邮箱已经注册，请使用其它邮箱");
                        } else {
                            bindEmail(email);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ProgressDialogHelper.setRunning(cx, false);
                        ToastUtils.showThrowable(t);
                    }
                });
    }


    void bindEmail(final String email) {
        ProgressDialogHelper.setRunning(cx, true);
        Plat.accountService.updateUser(user.id, user.name, user.phone, email, user.gender, new VoidCallback() {
            @Override
            public void onSuccess() {
                ProgressDialogHelper.setRunning(cx, false);
                UIService.getInstance().popBack();
            }

            @Override
            public void onFailure(Throwable t) {
                ProgressDialogHelper.setRunning(cx, false);
                ToastUtils.showThrowable(t);
            }
        });
    }
}
