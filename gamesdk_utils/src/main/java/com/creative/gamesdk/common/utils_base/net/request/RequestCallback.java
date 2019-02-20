package com.creative.gamesdk.common.utils_base.net.request;

/**
 * Created by Administrator on 2019/2/20.
 */

public interface RequestCallback {

    /**
     * 网络请求成功回调
     * @param object
     */
    void onSuccess(Object object);

    /**
     * 网路请求失败回调
     * @param errCode
     * @param msg
     */
    void onFailure(int errCode,String msg);
}
