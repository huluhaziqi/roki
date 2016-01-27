package com.robam.roki.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.robam.roki.R;
import com.legent.Callback;
import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.api.ViewUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.pojos.Advert;
import com.robam.common.services.StoreService;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhaiyuanyi on 15/11/12.
 */
public class LostRecipeDialog extends AbsDialog {
    @InjectView(R.id.layout_lost_recipe)
    ImageView recipe;

    static public void show(Context cx) {
        LostRecipeDialog dlg = new LostRecipeDialog(cx);
        dlg.show();
    }

    public LostRecipeDialog(Context context) {
        super(context, R.style.maintain_home_dialog_style);
        ViewUtils.setFullScreen(context, this);
    }

    @OnClick(R.id.layout_lost_recipe)
    public void onClick() {
        dialog_dismiss();
    }

    @OnClick(R.id.btn_jmp2web)
    public void onClickbtn() {
        // 点击，跳转到公众号，再修改；
        jmp2WechatSubscription(getContext());
        //dialog_dismiss();
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_lost_recipe;
    }

    @Override
    protected void initView(View view) {
        initAdvertData();
        ButterKnife.inject(this, view);
    }

    private void initAdvertData() {
        StoreService.getInstance().getHomeAdvertsForMob(new Callback<List<Advert.MobAdvert>>() {
            @Override
            public void onSuccess(List<Advert.MobAdvert> mobAdverts) {
                ImageUtils.displayImage(mobAdverts.get(0).getImgUrl(), recipe, ImageUtils.defaultOptions);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void dialog_dismiss() {
        dismiss();
    }

    public void jmp2WechatSubscription(Context cx) {
        Uri uri = Uri.parse("http://mp.weixin.qq.com/s?__biz=MzA5ODAwNTA4Mg==&mid=400595111&idx=1&sn=b8e8933677714f74351df70e3da3e011#rd");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        cx.startActivity(it);
    }
}
