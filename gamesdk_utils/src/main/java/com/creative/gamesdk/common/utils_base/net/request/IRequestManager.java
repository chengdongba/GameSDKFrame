package com.creative.gamesdk.common.utils_base.net.request;

import java.util.Map;

/**
 * Created by Administrator on 2019/2/20.
 * 将网络请求方法抽象抽离出来
 */

public interface IRequestManager {
    void get(String url, Map<String,Object> params,RequestCallback callback);
    void post(String url,Map<String,Object> params,RequestCallback callback);
    void cancel();
    void setUserAgent(String userAgent);
    void setHeader(String header);
}
