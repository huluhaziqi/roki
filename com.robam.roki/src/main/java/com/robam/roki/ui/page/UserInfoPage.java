package com.robam.roki.ui.page;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.events.UserUpdatedEvent;
import com.legent.plat.pojos.User;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.dialogs.DialogHelper;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.legent.utils.graphic.PickImageHelper;
import com.robam.roki.R;
import com.robam.roki.ui.Helper;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.view.UserGenderView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/14.
 */
public class UserInfoPage extends HeadPage {
    @InjectView(R.id.genderBoyView)
    UserGenderView genderBoyView;
    @InjectView(R.id.genderGirlView)
    UserGenderView genderGirlView;
    @InjectView(R.id.imgFigure)
    ImageView imgFigure;
    @InjectView(R.id.edtName)
    TextView txtName;
    @InjectView(R.id.txtPhone)
    TextView txtPhone;
    @InjectView(R.id.txtEmail)
    TextView txtEmail;
    @InjectView(R.id.txtPwd)
    TextView txtPwd;

    User user;
    PickImageHelper pickHelper;

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_user_info, container, false);
        ButterKnife.inject(this, view);

        user = Plat.accountService.getCurrentUser();
        showUser(user);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Subscribe
    public void onEvent(UserUpdatedEvent event) {
        showUser(user);
    }

    @OnClick(R.id.imgFigure)
    public void onClickFigure() {
        if (pickHelper == null) {
            pickHelper = new PickImageHelper(activity, pickCallback);
        }

        pickHelper.showPickDialog("设置头像");
    }


    @OnClick({R.id.edtName, R.id.txtPhone, R.id.txtEmail, R.id.txtPwd})
    public void onClick(View view) {
        Bundle bd = new Bundle();
        bd.putParcelable(PageArgumentKey.User, user);

        switch (view.getId()) {
            case R.id.edtName:
                UIService.getInstance().postPage(PageKey.UserModifyName, bd);
                break;
            case R.id.txtPwd:
                UIService.getInstance().postPage(PageKey.UserModifyPwd, bd);
                break;
            case R.id.txtPhone:
                UIService.getInstance().postPage(PageKey.UserModifyPhone, bd);
                break;
            case R.id.txtEmail:
                UIService.getInstance().postPage(PageKey.UserModifyEmail, bd);
                break;
        }
    }

    @OnClick(R.id.txtLogout)
    public void onClickLogout() {

        DialogHelper.newDialog_OkCancel(cx, "确定退出当前帐户？", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Plat.accountService.logout();
                    UIService.getInstance().popBack();
                }
            }
        }).show();
    }

    @OnClick(R.id.genderBoyView)
    public void onClickGenderBoy(UserGenderView view) {

        final boolean gender = view.isChecked();
        setGender(gender);
    }

    @OnClick(R.id.genderGirlView)
    public void onClickGenderGirl(UserGenderView view) {

        final boolean gender = !view.isChecked();
        setGender(gender);
    }

    void showUser(User user) {

        txtName.setText(Strings.isNullOrEmpty(user.name) ? user.phone : user.name);
        txtEmail.setText(Strings.isNullOrEmpty(user.email) ? "请设置邮箱" : user.email);
        txtPhone.setText(Strings.isNullOrEmpty(user.phone) ? "请设置手机" : user.phone);
        txtPwd.setText(user.hasPassword() ? "修改密码" : "请设置密码");

        showGender(user.gender);
        showFigure(user.figureUrl);
    }

    void showGender(boolean gender) {
        genderGirlView.setSelected(gender);
        genderBoyView.setSelected(!gender);
    }

    void showFigure(String figure) {
        if (Strings.isNullOrEmpty(figure))
            imgFigure.setImageResource(R.mipmap.ic_user_default_figure);
        else
            ImageUtils.displayImage(figure, imgFigure, Helper.DisplayImageOptions_UserFace);
    }

    void setGender(final boolean gender) {
        ProgressDialogHelper.setRunning(cx, true);
        Plat.accountService.updateUser(user.id, user.name, user.phone, user.email, gender, new VoidCallback() {
            @Override
            public void onSuccess() {
                ProgressDialogHelper.setRunning(cx, false);
                user.gender = gender;
                showGender(gender);
            }

            @Override
            public void onFailure(Throwable t) {
                ProgressDialogHelper.setRunning(cx, false);
                ToastUtils.showThrowable(t);
            }
        });
    }


    PickImageHelper.PickCallback pickCallback = new PickImageHelper.PickCallback() {

        @Override
        public void onPickComplete(Bitmap bmp) {
            if (bmp == null)
                return;

            String strFace = User.figure2String(bmp);

            ProgressDialogHelper.setRunning(cx, true);
            Plat.accountService.updateFigure(user.id, strFace,
                    new Callback<String>() {

                        @Override
                        public void onSuccess(String result) {
                            ProgressDialogHelper.setRunning(cx, false);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ProgressDialogHelper.setRunning(cx, false);
                            ToastUtils.showThrowable(t);
                        }
                    });
        }
    };


}
