package com.robam.rokipad.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.PopupWindow;

import com.legent.Callback2;
import com.legent.ui.UIService;
import com.legent.ui.ext.popoups.BasePickerPopupWindowOvenThreePickerPad;
import com.legent.ui.ext.popoups.BasePickerPopupWindowPad;
import com.legent.ui.ext.popoups.BaseStoveSettingPopupWindowPad;
import com.legent.ui.ext.views.WheelView;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.model.NormalModeItemMsg;
import com.legent.ui.ext.popoups.BasePickerPopupWindowTwoSelectorPad;
import com.robam.rokipad.ui.view.DeviceOvenNormalSettingWheelView;
import com.robam.rokipad.ui.view.OneWheelView;
import com.robam.rokipad.ui.view.OvenResetPickerPopupWindowPad;
import com.robam.rokipad.ui.view.OvenResetWheelView;
import com.robam.rokipad.ui.view.OvenSettingThreeWheelView;
import com.robam.rokipad.ui.view.ResetPickerPopupWindowPad;
import com.robam.rokipad.ui.view.ResetWheelView;
import com.robam.rokipad.ui.view.SteamSettingThreeWheelView;
import com.robam.rokipad.ui.view.TimeWheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WZTCM on 2015/12/19.
 */
public class Helper {

    public static PopupWindow newSteamThreePickerPAD(Context cx, final Callback2<DeviceWorkMsg> callback) {
        final SteamSettingThreeWheelView view = new SteamSettingThreeWheelView(cx);
        BasePickerPopupWindowPad.PickListener listener = new BasePickerPopupWindowPad.PickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                if (callback != null) {
                    callback.onCompleted(view.getSelected());
                }
            }
        };
        BasePickerPopupWindowPad pop = new BasePickerPopupWindowPad(cx, view);
        pop.setPickListener(listener);
        return pop;
    }

    public static PopupWindow newOvenThreePickerPAD(Context cx, final Callback2<NormalModeItemMsg> callback) {
        final OvenSettingThreeWheelView view = new OvenSettingThreeWheelView(cx);
        BasePickerPopupWindowOvenThreePickerPad.PickListener listener = new BasePickerPopupWindowOvenThreePickerPad.PickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                if (callback != null) {
                    callback.onCompleted(view.getSelected());
                }
            }
        };
        BasePickerPopupWindowOvenThreePickerPad pop = new BasePickerPopupWindowOvenThreePickerPad(cx, view);
        pop.setPickListener(listener);
        return pop;
    }

    public static PopupWindow newResetTwoWheelPickerPAD(Context cx, final Callback2<DeviceWorkMsg> callback) {
        final ResetWheelView view = new ResetWheelView(cx);
        ResetPickerPopupWindowPad.PickListener listener = new ResetPickerPopupWindowPad.PickListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onConfirm() {
                if (callback != null) {
                    callback.onCompleted(view.getSelected());
                }
            }
        };
        ResetPickerPopupWindowPad pop = new ResetPickerPopupWindowPad(cx, view);
        pop.setPickListener(listener);
        return pop;
    }

    public static PopupWindow newOvenResetTwoSettingPickerPad(Context cx, final Callback2<NormalModeItemMsg> callback, NormalModeItemMsg msg) {
        final OvenResetWheelView view = new OvenResetWheelView(cx);
        OvenResetPickerPopupWindowPad.PickListener listener = new OvenResetPickerPopupWindowPad.PickListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onConfirm() {
                if (callback != null) {
                    callback.onCompleted(view.getSelected());
                }
            }
        };
        OvenResetPickerPopupWindowPad pop = new OvenResetPickerPopupWindowPad(cx, view, msg.getType());
        pop.setPickListener(listener);
        return pop;
    }

    public static PopupWindow newOvenTwoSettingPickerPad(Context cx, final Callback2<NormalModeItemMsg> callback, NormalModeItemMsg msg) {
        final DeviceOvenNormalSettingWheelView view = new DeviceOvenNormalSettingWheelView(cx, msg.getType());
        BasePickerPopupWindowTwoSelectorPad.PickListener listener = new BasePickerPopupWindowTwoSelectorPad.PickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                if (callback != null) {
                    callback.onCompleted(view.getSelected());
//                    Bundle bundle1 = new Bundle();
//                    bundle1.putString(PageArgumentKey.Guid, guid);
//                    bundle1.putSerializable("msg",view.getSelected());
//                    UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle1);
                }

            }
        };
        BasePickerPopupWindowTwoSelectorPad pop = new BasePickerPopupWindowTwoSelectorPad(cx, view, msg.getType());
        pop.setPickListener(listener);
        return pop;
    }

    public static PopupWindow newStoveSettingPickerPad(Context cx, final Callback2<String> callback) {
        final OneWheelView view = new OneWheelView(cx);
        BaseStoveSettingPopupWindowPad.PickListener listener = new BaseStoveSettingPopupWindowPad.PickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                callback.onCompleted(view.getSelectedText());
            }
        };
        BaseStoveSettingPopupWindowPad pop = new BaseStoveSettingPopupWindowPad(cx, view);
        pop.setPickListener(listener);
        return pop;
    }
}
