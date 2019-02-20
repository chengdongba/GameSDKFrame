package com.creative.gamesdk.listener;

/**
 * Created by Administrator on 2019/2/13.
 */

public interface InitCallBackListener {
    /**
     * 初始化成功
     */
    void onSuccess();

    /**
     * 初始化失败
     * @param errCode
     * @param msg
     */
    void onFailure(int errCode,String msg);
}
