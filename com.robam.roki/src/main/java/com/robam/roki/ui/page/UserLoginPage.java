package com.robam.roki.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.legent.plat.Plat;
import com.legent.plat.pojos.User;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.utils.StringUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.roki.R;
import com.robam.roki.ui.Helper;
import com.robam.roki.ui.PageKey;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/8.
 */
public class UserLoginPage extends BasePage {

    @InjectView(R.id.edtAccount)
    EditText edtAccount;
    @InjectView(R.id.edtPwd)
    EditText edtPwd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_user_login, container, false);
        ButterKnife.inject(this, view);

        String account = Plat.accountService.getLastAccount();
        edtAccount.setText(account);
        if (Strings.isNullOrEmpty(account)) {
            edtAccount.requestFocus();
        } else {
            edtPwd.requestFocus();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @OnClick(R.id.txtLogin)
    public void onClickLogin() {
        try {
            login();
        } catch (Exception e) {
            ToastUtils.showException(e);
        }
    }

    @OnClick(R.id.txtExpressLogin)
    public void onClickExpressLogin() {
        UIService.getInstance().postPage(PageKey.UserExpressLogin);
    }

    @OnClick(R.id.txtRegist)
    public void onClickRegist() {
        UIService.getInstance().postPage(PageKey.UserRegist);
    }

    @OnClick(R.id.txtRetrievePwd)
    public void onClickRetrievePwd() {
        UIService.getInstance().postPage(PageKey.UserRecoverPwd);
    }

    private void login() {
        String account = edtAccount.getText().toString();
        String pwd = edtPwd.getText().toString();

        Preconditions.checkState(
                StringUtils.isMobile(account), "不支持的手机格式");

        Preconditions.checkState(!Strings.isNullOrEmpty(pwd), "未填写密码");

        final String pwdMd5 = User.encryptPassword(pwd);
        Helper.login(activity,account,pwdMd5);
    }

}
