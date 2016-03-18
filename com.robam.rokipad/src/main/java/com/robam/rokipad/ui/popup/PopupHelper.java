package com.robam.rokipad.ui.popup;

import android.content.Context;
import android.widget.PopupWindow;

import com.robam.rokipad.ui.view.RecipeTypePopupWindow;

/**
 * Created by WZTCM on 2016/1/16.
 */
public class PopupHelper {

    public static PopupWindow newRecipeTypePopupWindow(Context context, RecipeTypePopupWindow.PickListener listener) {
        RecipeTypePopupWindow pop = new RecipeTypePopupWindow(context);
        pop.setListener(listener);
        return pop;
    }
}
