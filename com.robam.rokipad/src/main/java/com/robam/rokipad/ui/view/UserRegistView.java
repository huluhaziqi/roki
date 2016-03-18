package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.legent.Callback;
import com.legent.Helper;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.utils.StringUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.rokipad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserRegistView extends FrameLayout {

    @InjectView(R.id.edtPhone)
    EditText edtPhone;

    @InjectView(R.id.edtNickname)
    EditText edtNickname;

    @InjectView(R.id.edtPwd)
    EditText edtPwd;

    @InjectView(R.id.edtPwd2)
    EditText edtPwd2;

    @InjectView(R.id.edtCode)
    EditText edtCode;

    @InjectView(R.id.btnGetCode)
    Button btnGetCode;

    Context cx;
    String verifyCode;

    public UserRegistView(Context context) {
        super(context);
        init(context, null);
    }

    public UserRegistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserRegistView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @OnClick(R.id.btnGetCode)
    public void onGetCode() {
        getCode();
    }

    public void registAndLogin(VoidCallback callback) {
        try {
            regist(callback);
        } catch (Exception e) {
            Helper.onFailure(callback, e);
        }
    }

    void init(Context cx, AttributeSet attrs) {

        this.cx = cx;
        View view = LayoutInflater.from(cx).inflate(R.layout.view_user_regist,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
    }

    private void getCode() {
        String phone = edtPhone.getText().toString();
        if (Strings.isNullOrEmpty(phone)) {
            ToastUtils.showShort("未填写手机号码");
            return;
        }

        Plat.accountService.getVerifyCode(phone, new Callback<String>() {

            @Override
            public void onSuccess(String result) {
                verifyCode = result;
                ToastUtils.showShort("请关注查收短信验证码");

                btnGetCode.setEnabled(false);
                btnGetCode.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        btnGetCode.setEnabled(true);
                    }
                }, 1000 * 30);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    private void regist(final VoidCallback callback) {
        final String phone = edtPhone.getText().toString();
        final String nickname = edtNickname.getText().toString();
        String pwd = edtPwd.getText().toString();
        String pwd2 = edtPwd2.getText().toString();
        String code = edtCode.getText().toString();


        Preconditions.checkState(!Strings.isNullOrEmpty(phone), "手机不能为空");
        Preconditions.checkState(
                StringUtils.isMobile(phone), "不支持的手机格式");
        Preconditions.checkState(!Strings.isNullOrEmpty(nickname), "昵称不可为空");
        Preconditions.checkState(!Strings.isNullOrEmpty(pwd), "未填写密码");
        Preconditions.checkState(pwd.length() >= 6, "密码不得小于6位");
        Preconditions.checkState(Objects.equal(pwd, pwd2), "两次输入的密码不一致");
        Preconditions.checkState(Objects.equal(code, verifyCode), "校验码错误");

        final String password = User.encryptPassword(pwd);

        String figure = null;
        boolean gender = true;
        Plat.accountService.registByPhone(phone, nickname, password, figure,
                gender, verifyCode, new VoidCallback() {

                    @Override
                    public void onSuccess() {
                        ToastUtils.showShort("注册成功");
                        login(phone, password, callback);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Helper.onFailure(callback, t);
                    }
                });

    }

    private void login(String account, String pwd, final VoidCallback callback) {

        Plat.accountService.login(account, pwd, new Callback<User>() {

            @Override
            public void onSuccess(User user) {
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }
}
