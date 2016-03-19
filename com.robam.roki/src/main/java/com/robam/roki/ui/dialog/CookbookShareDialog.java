package com.robam.roki.ui.dialog;

import android.content.Context;
import android.view.View;

import com.google.common.base.Objects;
import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.api.ViewUtils;
import com.legent.utils.sharesdk.ShareHelper;
import com.robam.common.pojos.Recipe;
import com.robam.roki.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by sylar on 15/6/15.
 */
public class CookbookShareDialog extends AbsDialog {

    static public void show(Context cx, Recipe book) {
        CookbookShareDialog dlg = new CookbookShareDialog(cx, book);
        dlg.show();
    }

    Recipe book;

    public CookbookShareDialog(Context cx, Recipe book) {
        super(cx, R.style.Theme_Dialog_FullScreen);
        ViewUtils.setFullScreen(cx, this);
        this.book = book;
    }


    @Override
    protected int getViewResId() {
        return R.layout.dialog_cookbook_share;
    }


    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
    }


    @OnClick(R.id.layout)
    public void onClick() {
        this.dismiss();
    }

    @OnClick(R.id.imgMoment)
    public void onClickMoment() {
        share(WechatMoments.NAME);
    }

    @OnClick(R.id.imgFriend)
    public void onClickWechat() {
        share(Wechat.NAME);
    }

    @OnClick(R.id.imgQQ)
    public void onClickQQ() {
        share(QQ.NAME);
    }

    void share(final String platKey) {
        this.dismiss();

        final Context cx = getContext();
        final String title = book.name;
        final String text = book.desc;
        final String webUrl = book.getViewUrl();
        String imgUrl = book.imgSmall;

        if (Objects.equal(platKey, WechatMoments.NAME)) {
            ShareHelper.shareWebByWechatMoments(cx, title, text, webUrl, imgUrl);
        } else if (Objects.equal(platKey, Wechat.NAME)) {
            ShareHelper.shareWebByWechat(cx, title, text, webUrl, imgUrl);
        } else if (Objects.equal(platKey, QQ.NAME)) {
            ShareHelper.shareWebByQQ(cx, title,text, webUrl,imgUrl);
        }

    }


}
