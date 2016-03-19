package com.robam.roki.ui.page;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.StringUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.io.cloud.IRokiRestService;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/9.
 */
abstract public class AbsVerifyCodePage extends HeadPage {

    @InjectView(R.id.edtPhone)
    EditText edtPhone;
    @InjectView(R.id.edtCode)
    EditText edtCode;
    @InjectView(R.id.txtUserProfile)
    TextView txtUserProfile;
    @InjectView(R.id.txtConfirm)
    TextView txtConfirm;
    @InjectView(R.id.txtGetCode)
    TextView txtGetCode;
    @InjectView(R.id.divUserProfile)
    LinearLayout divUserProfile;

    String phone, code;
    CountDownTimer timer;

    abstract void onConfirm(String phone, String verifyCode);

    protected void init(Bundle bd) {
    }

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.abs_page_verify_code, viewGroup, false);
        ButterKnife.inject(this, view);
        txtUserProfile.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtUserProfile.getPaint().setAntiAlias(true);
        edtPhone.requestFocus();

        txtGetCode.setText("获取" + getCodeDesc());
        edtCode.setHint("请输入" + getCodeDesc());

        init(getArguments());
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopCountdown();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.txtUserProfile)
    public void onClickUserProfile() {
        String url = String.format("%s/%s",
                Plat.serverOpt.getRestfulBaseUrl(),
                IRokiRestService.Url_UserProfile);

        Bundle bd = new Bundle();
        bd.putString(PageArgumentKey.Url, url);
        UIService.getInstance().postPage(PageKey.WebClient, bd);
    }


    @OnCheckedChanged(R.id.chkUserProfile)
    public void onCheckChanged(boolean checked) {
        txtConfirm.setSelected(!checked);
        txtConfirm.setEnabled(checked);
    }

    @OnClick(R.id.txtGetCode)
    public void onClickGetCode() {

        try {
            String phone = edtPhone.getText().toString();
            Preconditions.checkState(!Strings.isNullOrEmpty(phone), "手机不能为空");
            Preconditions.checkState(StringUtils.isMobile(phone), "无效的手机号码");

            getCode(phone);
        } catch (Exception e) {
            ToastUtils.showException(e);
        }
    }

    @OnClick(R.id.txtConfirm)
    public void onClickConfirm() {

        try {
            String code = edtCode.getText().toString();
            Preconditions.checkState(Objects.equal(this.code, code), getCodeDesc() + "不匹配");
            onConfirm(phone, code);
        } catch (Exception e) {
            ToastUtils.showException(e);
        }
    }


    void getCode(final String phone) {
        this.phone = phone;

        ProgressDialogHelper.setRunning(cx, true);
        getVerifyCode(phone, new Callback<String>() {
            @Override
            public void onSuccess(String s) {
                code = s;
                ProgressDialogHelper.setRunning(cx, false);
                ToastUtils.showShort(getCodeDesc() + "已发送，请及时查收");

                startCountdown();
            }

            @Override
            public void onFailure(Throwable t) {
                ProgressDialogHelper.setRunning(cx, false);
                ToastUtils.showThrowable(t);
            }
        });

    }


    void startCountdown() {
        stopCountdown();

        txtGetCode.setEnabled(false);
        edtCode.requestFocus();

        timer = new CountDownTimer(1000 * 60, 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                if (AbsVerifyCodePage.this.isDetached()) return;

                txtGetCode.post(new Runnable() {
                    @Override
                    public void run() {
                        if (AbsVerifyCodePage.this.isAdded() && !AbsVerifyCodePage.this.isDetached()) {
                            txtGetCode.setText(String.format(getCodeDesc() + "(%s)", millisUntilFinished / 1000));
                        }
                    }
                });
            }

            @Override
            public void onFinish() {
                txtGetCode.setEnabled(true);
                txtGetCode.setText("重获" + getCodeDesc());
            }
        };

        timer.start();
    }

    void stopCountdown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    void getVerifyCode(String phone, Callback<String> callback) {
        Plat.accountService.getVerifyCode(phone, callback);
    }

    String getCodeDesc() {
        return "验证码";
    }


}
