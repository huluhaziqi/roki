package com.legent.ui.ext.popoups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.legent.ui.R;

/**
 * Created by linxiaobin on 2016/1/18.
 */
public class BasePickerPopupWindowOvenThreePickerPad extends PopupWindow implements View.OnClickListener {

    public interface PickListener {

        void onCancel();

        void onConfirm();
    }

    FrameLayout divMain;
    ImageView warning;
    TextView txtConfirm, txtCancel;
    View customView;
    protected PickListener listener;

    public BasePickerPopupWindowOvenThreePickerPad(Context cx, View customView) {
        super(cx);

        View view = LayoutInflater.from(cx).inflate(R.layout.abs_view_oven_picker_popoup_pad, null);
        divMain = (FrameLayout) view.findViewById(R.id.divMain);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        txtCancel = (TextView) view.findViewById(R.id.txtCancel);
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
    public void onClick(View view) {
        if (view == txtConfirm) {
            dismiss();
            if (listener != null) {
                listener.onConfirm();
            }
        } else if (view == txtCancel) {
            dismiss();
            if (listener != null) {
                listener.onCancel();
            }
        }
    }

    public void setPickListener(PickListener listener) {
        this.listener = listener;
    }
}

