package com.robam.common.pojos.device.Sterilizer;

import com.legent.VoidCallback;
import com.robam.common.pojos.device.IPauseable;

/**
 * Created by zhaiyuanyi on 15/11/19.
 */
public interface ISterilizer extends IPauseable {
    public String getSterilizerModel();
    /**
     * 读取消毒柜工作状态
     *
     * @param callback
     */
    void getSteriStatus(VoidCallback callback);

    /**
     * 设置消毒柜工作状态
     *
     * @param status   0-关机，1-开机
     * @param callback
     */
    void setSteriStatus(short status, VoidCallback callback);
}
