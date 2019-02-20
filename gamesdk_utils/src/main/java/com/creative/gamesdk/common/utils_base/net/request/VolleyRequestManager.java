package com.creative.gamesdk.common.utils_base.net.request;

import android.text.TextUtils;

import com.creative.gamesdk.common.utils_base.net.base.VolleyResponseListener;
import com.creative.gamesdk.common.utils_base.net.impl.BaseRequest;
import com.creative.gamesdk.common.utils_base.net.impl.BaseRequestCallback;

import java.util.Map;

/**
 * Created by Administrator on 2019/2/20.
 */

public class VolleyRequestManager implements IRequestManager {

    private String userAgent;
    private String header;
    private BaseRequest request;

    @Override
    public void get(String url, Map<String, Object> params, final RequestCallback callback) {
        this.request = GetRequest.create(url, params, new BaseRequestCallback<String>() {
            @Override
            protected void onFailure(int code, String msg) {
                callback.onFailure(code, msg);
            }

            @Override
            protected void onSuccess(String response) {
                callback.onSuccess(response);
            }
        });

        if (!TextUtils.isEmpty(header)) {
            request.setAuthorization(header);
        }

        if (!TextUtils.isEmpty(userAgent)) {
            request.setUserAgent(userAgent);
        }

        request.sendRequest();
    }

    @Override
    public void post(String url, Map<String, Object> params, final RequestCallback callback) {
        this.request = PostRequest.create(url, params, new BaseRequestCallback<String>() {
            @Override
            protected void onFailure(int code, String msg) {
                callback.onFailure(code, msg);
            }

            @Override
            protected void onSuccess(String response) {
                callback.onSuccess(response);
            }
        });

        if (!TextUtils.isEmpty(header)) {
            this.request.setAuthorization(header);
        }

        if (!TextUtils.isEmpty(userAgent)) {
            this.request.setUserAgent(userAgent);
        }

        this.request.sendRequest();

    }

    @Override
    public void cancel() {
        this.request.cancel();
    }

    @Override
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public void setHeader(String header) {
        this.header = header;
    }

    /*************************************网络请求***********************************************/
    private static class GetRequest extends BaseRequest {

        public GetRequest(int method, String url, Map<String, Object> bodyMap, VolleyResponseListener listener) {
            super(method, url, bodyMap, listener);
        }

        public static GetRequest create(String url, Map<String, Object> bodyMap, BaseRequestCallback callback) {
            return new GetRequest(Method.GET, url, bodyMap, callback);
        }

    }

    private static class PostRequest extends BaseRequest {

        public PostRequest(int method, String url, Map<String, Object> bodyMap, VolleyResponseListener listener) {
            super(method, url, bodyMap, listener);
        }

        public static PostRequest create(String url, Map<String, Object> bodyMap, BaseRequestCallback callback) {
            return new PostRequest(Method.POST, url, bodyMap, callback);
        }
    }
}
