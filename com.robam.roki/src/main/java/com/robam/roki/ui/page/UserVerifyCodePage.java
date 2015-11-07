package com.robam.roki.ui.page;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.legent.Callback;
import com.legent.VoidCallback2;
import com.legent.plat.Plat;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.StringUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.io.cloud.IRokiRestService;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/8.
 */
abstract public class UserVerifyCodePage extends HeadPage {

    final String[] BUTTON_TEXT_ITEMS = new String[]{"获取验证码", "提交验证码", "确定"};

    @InjectViews({R.id.hint1, R.id.hint2, R.id.hint3})
    List<TextView> hintViews;

    @InjectViews({R.id.divStep1, R.id.divStep2, R.id.divStep3})
    List<View> stepViews;

    @InjectView(R.id.edtPhone)
    EditText edtPhone;
    @InjectView(R.id.edtCode)
    EditText edtCode;
    @InjectView(R.id.edtPwd)
    EditText edtPwd;
    @InjectView(R.id.edtPwd2)
    EditText edtPwd2;
    @InjectView(R.id.txtPhone)
    TextView txtPhone;
    @InjectView(R.id.txtUserProfile)
    TextView txtUserProfile;
    @InjectView(R.id.txtConfirm)
    TextView txtConfirm;
    @InjectView(R.id.chkUserProfile)
    CheckBox chkUserProfile;
    @InjectView(R.id.userProfileView)
    LinearLayout userProfileView;

    TextView titleTextView;
    String phone, code;
    CountDownTimer timer;
    int stepIndex;


    abstract protected void onFinalConfirm(String phone, String pwd, String verifyCode);

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        registTitleRightView();

        View view = layoutInflater.inflate(R.layout.page_user_verify_code, viewGroup, false);
        ButterKnife.inject(this, view);
        txtUserProfile.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtUserProfile.getPaint().setAntiAlias(true);
        chkUserProfile.setChecked(false);
        onCheckChanged(false);

        setStep(0);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    @OnClick(R.id.txtConfirm)
    public void onClickConfirm() {

        try {
            switch (stepIndex) {
                case 0:
                    getCode(edtPhone.getText().toString(), new VoidCallback2() {
                        @Override
                        public void onCompleted() {
                            setStep(1);
                        }
                    });
                    break;
                case 1:
                    checkCode(edtCode.getText().toString());
                    setStep(2);
                    break;
                case 2:
                    String pwd1 = edtPwd.getText().toString();
                    String pwd2 = edtPwd2.getText().toString();
                    Preconditions.checkState(!Strings.isNullOrEmpty(pwd1), "密码不可为空");
                    Preconditions.checkState(pwd1.length() >= 6, "密码至少6位");
                    Preconditions.checkState(Objects.equal(pwd1, pwd2), "两次输入的密码不一致");
                    onFinalConfirm(phone, pwd1, code);
                    break;
            }

        } catch (Exception e) {
            ToastUtils.showException(e);
        }
    }


    void setStep(int index) {

        if (index < 0 || index >= hintViews.size())
            return;

        stepIndex = index;
        titleTextView.setVisibility(stepIndex == 1 ? View.VISIBLE : View.GONE);
        txtConfirm.setText(BUTTON_TEXT_ITEMS[stepIndex]);

        for (View view : hintViews) {
            view.setSelected(false);
        }
        for (View view : stepViews) {
            view.setVisibility(View.GONE);
        }

        hintViews.get(index).setSelected(true);
        stepViews.get(index).setVisibility(View.VISIBLE);

        switch (index) {
            case 0:
                edtPhone.requestFocus();
                break;
            case 1:
                edtCode.requestFocus();
                startCountdown();
                break;
            case 2:
                edtPwd.requestFocus();
                break;
        }

    }

    void getCode(final String phone, final VoidCallback2 callback) {

        Preconditions.checkState(!Strings.isNullOrEmpty(phone), "手机不能为空");
        Preconditions.checkState(StringUtils.isMobile(phone), "无效的手机号码");

        this.phone = phone;

        ProgressDialogHelper.setRunning(cx, true);
        Plat.accountService.getVerifyCode(phone, new Callback<String>() {
            @Override
            public void onSuccess(String s) {
                code = s;
                String strPhone = phone.substring(0, 3) + "****" + phone.substring(7);
                txtPhone.setText(strPhone);
                ProgressDialogHelper.setRunning(cx, false);
                ToastUtils.showShort("短信验证码已发送，请及时查收");

                if (callback != null) {
                    callback.onCompleted();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ProgressDialogHelper.setRunning(cx, false);
                ToastUtils.showThrowable(t);
            }
        });

    }

    void checkCode(String code) {
        Preconditions.checkState(Objects.equal(this.code, code), "验证码不匹配");
    }


    void registTitleRightView() {
        titleTextView = TitleBar.newTitleTextView(cx, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode(edtPhone.getText().toString(), null);
            }
        });

        titleBar.replaceRight(titleTextView);
    }

    void startCountdown() {
        stopCountdown();
        titleTextView.setEnabled(false);

        timer = new CountDownTimer(1000 * 60, 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                titleTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (UserVerifyCodePage.this.isAdded() && !UserVerifyCodePage.this.isDetached()) {
                            titleTextView.setText(String.format("验证码(%s)", millisUntilFinished / 1000));
                        }
                    }
                });
            }

            @Override
            public void onFinish() {
                titleTextView.setEnabled(true);
                titleTextView.setText("重获验证码");
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


}
