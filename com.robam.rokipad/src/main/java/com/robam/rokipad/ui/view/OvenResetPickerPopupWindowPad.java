package com.robam.rokipad.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.robam.rokipad.R;

/**
 * Created by linxiaobin on 2016/1/18.
 */
public class OvenResetPickerPopupWindowPad extends PopupWindow implements View.OnClickListener {

    public interface PickListener {

        void onCancel();

        void onConfirm();
    }

    FrameLayout divMain;
    TextView txtConfirm, txtCancel, txtResetMode;
    View customView;
    protected PickListener listener;

    public OvenResetPickerPopupWindowPad(Context cx, View customView, String index) {
        super(cx);

        View view = LayoutInflater.from(cx).inflate(R.layout.view_oven_reset_picker_popup_pad, null);
        divMain = (FrameLayout) view.findViewById(R.id.divMain);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        txtCancel = (TextView) view.findViewById(R.id.txtCancel);
        txtResetMode = (TextView) view.findViewById(R.id.txtResetMode);
        txtResetMode.setText(index + "（调整）");
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

