package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.legent.Callback;
import com.legent.Helper;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.StringUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.security.MD5Utils;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.form.GuideActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rosicky on 16/3/9.
 */
public class GuideFindPage extends HeadPage {

    @InjectView(R.id.edtPhone)
    EditText edtPhone;
    @InjectView(R.id.edtCode)
    EditText edtCode;
    @InjectView(R.id.btnGetCode)
    Button btnGetCode;
    @InjectView(R.id.linCode)
    LinearLayout linCode;
    @InjectView(R.id.edtNewPwd)
    EditText edtNewPwd;

    String verifyCode;

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {

        View view = inflater.inflate(R.layout.frame_guide_find, container,
                false);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        TextView v = TitleBar.newTitleTextView(cx, "忘记密码");
        getTitleBar().replaceMiddle(v);
        return view;
    }

    @OnClick(R.id.btnGetCode)
    public void onClickCode() {
        getCode();
    }

    @OnClick(R.id.btnFind)
    public void onClickFind() {
        setPwdByPhone(new VoidCallback() {

            @Override
            public void onSuccess() {
                GuideActivity.onGuideOver(activity, true);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
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

//                btnGetCode.setEnabled(false);
                btnGetCode.setBackgroundColor(getResources().getColor(R.color.c19));
                btnGetCode.postDelayed(new Runnable() {

                    @Override
                    public void run() {
//                        btnGetCode.setBackgroundColor(getResources().getColor(R.color.home_bg));
                    }
                }, 1000 * 30);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    public void setPwdByPhone(final VoidCallback callback) {
        final String phone = edtPhone.getText().toString().trim();
        final String code = edtCode.getText().toString();
        final String password = edtNewPwd.getText().toString().trim();

        Preconditions.checkState(!Strings.isNullOrEmpty(phone), "手机不能为空");
        Preconditions.checkState(
                StringUtils.isMobile(phone), "不支持的手机格式");
        Preconditions.checkState(!Strings.isNullOrEmpty(password), "未填写密码");
        Preconditions.checkState(password.length() >= 6, "密码不得小于6位");
        Preconditions.checkState(Objects.equal(code, verifyCode), "校验码错误");
        final String md5Pwd = User.encryptPassword(password);
        Plat.accountService.resetPasswordByPhone(phone, md5Pwd, verifyCode, new VoidCallback() {

                    @Override
                    public void onSuccess() {
                        ToastUtils.showShort("设置成功");
                        login(phone, md5Pwd, callback);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
