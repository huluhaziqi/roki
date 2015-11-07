package com.robam.roki.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.legent.ui.ext.dialogs.AbsDialog;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ViewUtils;
import com.robam.common.events.StoveStatusChangedEvent;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.device.Stove;
import com.robam.common.pojos.device.StoveStatus;
import com.robam.roki.R;
import com.robam.roki.ui.Helper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/17.
 */
public class ChooseStoveByWaitDialog extends AbsDialog {

    static public void show(Context cx, Recipe book) {
        ChooseStoveByWaitDialog dlg = new ChooseStoveByWaitDialog(cx, book);
        dlg.show();
    }


    @InjectView(R.id.txtCookNoStove)
    TextView txtCookNoStove;

    Recipe book;

    public ChooseStoveByWaitDialog(Context cx, Recipe book) {
        super(cx, R.style.Theme_Dialog_FullScreen);
        ViewUtils.setFullScreen(cx, this);
        this.book = book;

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                EventUtils.regist(ChooseStoveByWaitDialog.this);
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventUtils.unregist(ChooseStoveByWaitDialog.this);
            }
        });
    }


    @Subscribe
    public void onEvent(StoveStatusChangedEvent event) {
        Stove stove = event.pojo;
        if (stove.leftHead != null && stove.leftHead.status == StoveStatus.StandyBy) {
            startCook(stove.leftHead);
        } else if (stove.rightHead != null && stove.rightHead.status == StoveStatus.StandyBy) {
            startCook(stove.rightHead);
        }
    }


    @OnClick(R.id.layout)
    public void onClick() {
        this.dismiss();
    }

    @Override
    protected int getViewResId() {
        return R.layout.dialog_cook_choose_stove_by_wait;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.inject(this, view);
        txtCookNoStove.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtCookNoStove.getPaint().setAntiAlias(true);
    }

    @OnClick(R.id.txtCookNoStove)
    public void onClickNoStove() {
        startCook(null);
    }


    void startCook(Stove.StoveHead head) {
        Helper.startCook(head, book);
        this.dismiss();
    }
}
