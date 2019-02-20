package com.creative.gamesdk.common.utils_base.interfaces;

/**
 * Created by Administrator on 2019/2/13.
 * 项目回调基类
 */

public interface CallBackListener<T> {

    /**
     * 成功回调
     * @param t 详细信息
     */
    void onSuccess(T t);

    /**
     * 失败回调
     * @param errCode 错误码
     * @param errMsg 错误信息
     */
    void onFailure(int errCode,String errMsg);
}
