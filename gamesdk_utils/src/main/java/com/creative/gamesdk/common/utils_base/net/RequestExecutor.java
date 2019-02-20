package com.creative.gamesdk.common.utils_base.net;

import com.creative.gamesdk.common.utils_base.net.request.IRequestManager;
import com.creative.gamesdk.common.utils_base.net.request.RequestCallback;
import com.creative.gamesdk.common.utils_base.net.request.VolleyRequestManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/19.
 * 网络请求接口类
 * 网络请求框架要替换成别的的时候,只实现具体封装,并修改具体实现
 * 比如修改成OKHttp写法:return new OkHttpRequestManager();
 */

public class RequestExecutor {

    public static final String POST = "POST";
    public static final String GET = "GET";

    private String method;
    private String url;
    private String header;
    private String userAgent;
    private Map<String,Object> params;
    private RequestCallback requestCallback;

    private IRequestManager iRequestManager;

    public RequestExecutor(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        this.header = builder.header;
        this.userAgent = builder.userAgent;
        this.params = builder.params;
        this.requestCallback = builder.callback;
    }

    public void startRequest(){
        iRequestManager = new VolleyRequestManager();
        iRequestManager.setUserAgent(userAgent);
        iRequestManager.setHeader(header);
        if (POST.equals(method)){
            iRequestManager.post(url,params,requestCallback);
        }else if (GET.equals(method)){
            iRequestManager.get(url,params,requestCallback);
        }
    }

    /**
     * 取消当前网络请求
     */
    public void cancel(){
        iRequestManager.cancel();
    }

    public static class Builder{
        private String url;
        private String method;
        private Map<String,Object> params;
        private RequestCallback callback;
        private String header;
        private String userAgent;

        public RequestExecutor build(){
            return new RequestExecutor(this);
        }

        public RequestExecutor.Builder setMethod(String method){
            this.method = method;
            return this;
        }

        public RequestExecutor.Builder setUrl(String url){
            this.url = url;
            return this;
        }

        public RequestExecutor.Builder setHeader(String header){
            this.header = header;
            return this;
        }

        public RequestExecutor.Builder setUserAgent(String userAgent){
            this.userAgent = userAgent;
            return this;
        }

        public RequestExecutor.Builder setParams(HashMap<String,Object> params){
            this.params = params;
            return this;
        }

        public RequestExecutor.Builder setRequestCallback(RequestCallback requestCallback){
            this.callback = requestCallback;
            return this;
        }
    }

}
