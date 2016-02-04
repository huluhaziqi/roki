package com.robam.roki.ui.page;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.common.services.StoreService;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.dialog.ApplyServiceHintDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/14.
 */
public class SaleServicePage extends HeadPage {

    @InjectView(R.id.imgCall)
    ImageView imgCall;
    @InjectView(R.id.txtTel)
    TextView txtTel;
    @InjectView(R.id.applyService)
    LinearLayout applyService;

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.page_sale_service, viewGroup, false);
        ButterKnife.inject(this, view);
        regsitRightView();
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.imgCall)
    public void onClickCall() {
        Uri uri = Uri.parse(String.format("tel:%s", txtTel.getText()
                .toString()));
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(it);
    }

//    @OnClick(R.id.applyService)
//    public void onClickApply() {
//        if (UiHelper.checkAuthWithDialog(cx, PageKey.UserLogin)) {
//            onApplyService();
//        }
//    }

    void onApplyService() {
        String phone = Plat.accountService.getCurrentUser().phone;
        if (Strings.isNullOrEmpty(phone)) {
            String msg = "还没有手机注册信息，请在用户资料里完善联系方式以方便我们主动回拨。";
            ToastUtils.showLong(msg);
            return;
        }

        AbsFan dev = Utils.getDefaultFan();
        if (dev == null) {
            ToastUtils.showShort(R.string.dev_invalid_error);
            return;
        }

        StoreService.getInstance().applyAfterSale(dev.getID(),
                new VoidCallback() {

                    @Override
                    public void onSuccess() {
                        ApplyServiceHintDialog.show(cx);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ToastUtils.showThrowable(t);
                    }
                });

    }

    void regsitRightView() {

        TextView txtView = TitleBar.newTitleTextView(cx, "在线咨询", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UiHelper.checkAuthWithDialog(cx, PageKey.UserLogin)) {
                    UIService.getInstance().postPage(PageKey.Chat);
                }
            }
        });


        titleBar.replaceRight(txtView);
    }
}
