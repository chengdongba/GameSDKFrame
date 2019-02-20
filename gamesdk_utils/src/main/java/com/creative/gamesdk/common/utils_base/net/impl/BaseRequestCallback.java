package com.creative.gamesdk.common.utils_base.net.impl;

import com.creative.gamesdk.common.utils_base.config.ErrCode;
import com.creative.gamesdk.common.utils_base.frame.google.volley.AuthFailureError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.NetworkError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.NoConnectionError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.ParseError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.RedirectError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.ServerError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.TimeoutError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.VolleyError;
import com.creative.gamesdk.common.utils_base.net.base.VolleyResponseListener;

/**
 * Created by Administrator on 2019/2/20.
 * 处理请求返回数据分类,通过泛型将结果数据转换为对应的实体回传
 */

public abstract class BaseRequestCallback<T> implements VolleyResponseListener {

    @Override
    public void onResponseSuccess(String response) {
        if (response==null){
            //请求成功但返回数据为空
            onFailure(ErrCode.NET_DATA_NULL,"net data null");
        }else {
            onSuccess((T)response);
        }
    }

    @Override
    public void onResponseFailure(VolleyError error) {

        Class errorClass = error.getClass();
        if (errorClass.equals(AuthFailureError.class)){//网络认证失败
            onFailure(ErrCode.NET_ERROR, "Network AuthFailureError");

        }else if (errorClass.equals(NetworkError.class)){ //网络错误
            onFailure(ErrCode.NET_ERROR, "NetworkError");

        }else if (errorClass.equals(NoConnectionError.class)){ //无网络连接
            onFailure(ErrCode.NET_ERROR, "Network NoConnectionError");

        }else if (errorClass.equals(ParseError.class)){ //服务器响应无法解析
            onFailure(ErrCode.NET_ERROR, "Network ParseError");

        }else if (errorClass.equals(RedirectError.class)){ //已存在重定向
            onFailure(ErrCode.NET_ERROR, "Network RedirectError");

        }else if (errorClass.equals(ServerError.class)){ //服务器错误响应
            onFailure(ErrCode.NET_ERROR, "Network ServerError");

        }else if (errorClass.equals(TimeoutError.class)){ //连接超时
            onFailure(ErrCode.NET_ERROR, "Network TimeoutError");
        }

    }

    /**
     * 失败后的回调
     * @param code
     * @param msg
     */
    protected abstract void onFailure(int code, String msg);

    /**
     * 成功后的回调
     * @param response
     */
    protected abstract void onSuccess(T response);

}
