package com.legent.ui.ext.popoups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.legent.ui.R;

/**
 * Created by linxiaobin on 2016/1/15.
 */
public class BasePickerPopupWindowTwoSelectorPad extends PopupWindow implements View.OnClickListener {

    public interface PickListener {

        void onCancel();

        void onConfirm();

    }

    protected TextView txtConfirm;
    protected FrameLayout divMain;
    protected TextView txtCancel;

    protected View customView;
    protected PickListener listener;
    protected TextView txtKind;

    public BasePickerPopupWindowTwoSelectorPad(Context cx, View customView, String index) {
        super(cx);

        View view = LayoutInflater.from(cx).inflate(R.layout.view_oven_normal_two_selector_pad, null);
        divMain = (FrameLayout) view.findViewById(R.id.divMain);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        txtCancel = (TextView) view.findViewById(R.id.txtCancel);
        txtKind = (TextView) view.findViewById(R.id.txtKind);
        txtKind.setText(index);
        txtConfirm.setOnClickListener(this);
        txtCancel.setOnClickListener(this);

        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(R.style.bottom_window_style);
        this.customView = customView;
        divMain.addView(customView);
    }

    @Override
    public void onClick(View v) {
        if (v == txtConfirm) {
            dismiss();
            if(listener!=null)
            listener.onConfirm();
        }
        if (v == txtCancel) {
            dismiss();
            if(listener!=null)
            listener.onCancel();
        }
    }


    public void setPickListener(PickListener listener) {
        this.listener = listener;
    }

    public View getCustomView() {
        return customView;
    }

    protected void onCancel() {
        dismiss();
        if (listener != null) {
            listener.onCancel();
        }
    }

    protected void onConfirm() {
        dismiss();
        if (listener != null) {
            listener.onConfirm();
        }
    }


}

