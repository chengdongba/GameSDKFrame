package com.creative.gamesdk.common.utils_base.net.base;

import com.creative.gamesdk.common.utils_base.frame.google.volley.VolleyError;

/**
 * Created by Administrator on 2019/2/19.
 * 网络请求回调,封装Volley网络请求回调
 */

public interface VolleyResponseListener {

    /**
     * 网络请求成功
     * @param response
     */
    void onResponseSuccess(String response);

    /**
     * 网络请求失败
     * @param error
     */
    void onResponseFailure(VolleyError error);
}
