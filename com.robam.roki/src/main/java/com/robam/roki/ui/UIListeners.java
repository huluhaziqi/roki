package com.robam.roki.ui;

import com.robam.common.pojos.device.IStove;
import com.robam.common.pojos.device.fan.IFan;

/**
 * Created by sylar on 15/6/12.
 */
public interface UIListeners {

    interface IRefresh {
        void onRefresh();
    }

    interface IFanCtrView extends IRefresh {
        void attachFan(IFan fan);
    }
    interface IStoveCtrView extends IRefresh{
        void attachStove(IStove stove);
    }

    interface CookingNextCallback {
        void onClickNext();
    }
}