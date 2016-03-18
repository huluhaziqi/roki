package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.legent.Helper;
import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.pojos.User;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.EventUtils;
import com.legent.utils.StringUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserLoginView extends FrameLayout {

    public static interface OnRegistCallback {
        void onRegist();
    }

    public static interface OnFindPwdCallback {
        void onFindPwd();
    }

    @InjectView(R.id.edtPhone)
    EditText edtPhone;

    @InjectView(R.id.edtPwd)
    EditText edtPwd;

    @InjectView(R.id.txtRegist)
    TextView txtRegist;

    Context cx;
    OnRegistCallback callabck;
    OnFindPwdCallback callbackFind;

    public UserLoginView(Context context) {
        super(context);
        init(context, null);
    }

    public UserLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserLoginView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @OnClick(R.id.txtRegist)
    public void onClickRegist() {
        if (callabck != null) {
            callabck.onRegist();
        }
    }

    @OnClick(R.id.txtForgotPwd)
    public void onClickForgot() {
        if (callbackFind != null) {
            callbackFind.onFindPwd();
        }
    }

    void init(Context cx, AttributeSet attrs) {

        this.cx = cx;
        View view = LayoutInflater.from(cx).inflate(R.layout.view_user_login,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            String account = Plat.accountService.getLastAccount();
            edtPhone.setText(account);
        }
    }

    public void setOnRegistCallback(OnRegistCallback callback) {
        this.callabck = callback;
    }

    public void setOnFindPwdCallback(OnFindPwdCallback callbackFind) {
        this.callbackFind = callbackFind;
    }

    public void login(final Callback<User> callback) {

        try {
            String account = edtPhone.getText().toString();
            String pwd = edtPwd.getText().toString();

            Preconditions.checkState(
                    StringUtils.isMobile(account), "不支持的手机格式");
            Preconditions.checkState(!Strings.isNullOrEmpty(pwd), "未填写密码");

            ProgressDialogHelper.setRunning(cx, true);

            final String pwdMd5 = User.encryptPassword(pwd);
            Plat.accountService.login(account, pwdMd5, new Callback<User>() {

                @Override
                public void onSuccess(User user) {
                    ProgressDialogHelper.setRunning(cx, false);
                    Helper.onSuccess(callback, user);
                }

                @Override
                public void onFailure(Throwable t) {
                    ProgressDialogHelper.setRunning(cx, false);
                    Helper.onFailure(callback, t);
                }
            });
        } catch (Exception e) {
            Helper.onFailure(callback, e.getCause());
        }
    }

}
