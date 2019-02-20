package com.creative.gamesdk.common.utils_base.net.base;

import android.content.Context;
import android.text.TextUtils;

import com.creative.gamesdk.common.utils_base.frame.google.volley.Request;
import com.creative.gamesdk.common.utils_base.frame.google.volley.Response;
import com.creative.gamesdk.common.utils_base.frame.google.volley.VolleyError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.toolbox.StringRequest;

/**
 * Created by Administrator on 2019/2/19.
 *
 * 封装Volley StringRequest:网络请求基础基类
 *
 */

public abstract class VolleyRequestWrapper extends StringRequest{

    private VolleySingleton baseVolleySingleton;

    public VolleyRequestWrapper(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public VolleyRequestWrapper(Context context, int method, String url, final VolleyResponseListener responseListener){
        this(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseListener.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseFailure(error);
            }
        });

        baseVolleySingleton = VolleySingleton.getInstance(context.getApplicationContext());

    }

    /**
     * 添加请求到请求队列
     */
    public void sendRequest(){
        addToRequestQueue(this);
    }

    public <T> void addToRequestQueue(Request<T> request){
        if (request==null) return;
        baseVolleySingleton.addToRequestQueue(request);
    }

    /**
     * 停止当前请求
     */
    @Override
    public void cancel() {
        super.cancel();
    }

    /**
     * 取消特定的请求
     * @param tag
     */
    public void cancelByTag(String tag){
        if (TextUtils.isEmpty(tag)) return;
        baseVolleySingleton.getRequestQueue().cancelAll(tag);
    }
}
