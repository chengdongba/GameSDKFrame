package com.creative.gamesdk.common.utils_base.net.impl;

import android.text.TextUtils;

import com.creative.gamesdk.common.utils_base.cache.ApplicationCache;
import com.creative.gamesdk.common.utils_base.frame.google.volley.AuthFailureError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.NetworkResponse;
import com.creative.gamesdk.common.utils_base.frame.google.volley.Response;
import com.creative.gamesdk.common.utils_base.frame.google.volley.VolleyError;
import com.creative.gamesdk.common.utils_base.frame.google.volley.toolbox.HttpHeaderParser;
import com.creative.gamesdk.common.utils_base.net.base.VolleyRequestWrapper;
import com.creative.gamesdk.common.utils_base.net.base.VolleyResponseListener;
import com.creative.gamesdk.common.utils_base.utils.CodeUtils;
import com.creative.gamesdk.common.utils_base.utils.JsonUtils;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2019/2/19.
 * Volley网络请求基类,所有网络请求都继承这个类
 */

public class BaseRequest extends VolleyRequestWrapper {

    public static final String TAG = "BaseRequest";

    private int mMethod;
    private String mUrl;
    private Map<String, Object> mParamMap = new HashMap<>();

    private String authorization;
    private String userAgent;
    private Map<String, String> headers;

    public BaseRequest(int method, String url, VolleyResponseListener listener) {
        super(ApplicationCache.getInstance().getApplicationContext(), method, url, listener);
    }

    /**
     * GET和POST 需要额外处理下url和bodyMap
     *
     * @param method
     * @param url
     * @param bodyMap
     * @param listener
     */
    public BaseRequest(int method, String url, Map<String, Object> bodyMap, VolleyResponseListener listener) {
        this(method, method == Method.GET ? buildParamsUrl(url, bodyMap) : url, listener);
        this.mMethod = method;
        this.mUrl = url;
        this.mParamMap = bodyMap == null ? mParamMap : bodyMap;
    }

    /**
     * 拼接get请求链接
     *
     * @param url
     * @param bodyMap
     * @return
     */
    private static String buildParamsUrl(String url, Map<String, Object> bodyMap) {

        url += "?";

        if (bodyMap == null) {
            bodyMap = new HashMap<>();
        }

        return CodeUtils.appendParams(new StringBuilder(url), bodyMap);
    }

    /**
     * 封装请求头
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String header = getAuthorization(mMethod == Method.POST ? "POST" : "GET", mUrl, mParamMap);
        String userAget = getUserAgent();
        headers = new HashMap<>();
        headers.put("Authorization", header);
        headers.put("User-Agent", userAget);
        //默认使用gzip压缩
        headers.put("Accept-Encoding", "gzip");
        return headers;
    }

    /**
     * 对外提供设置authorization的接口
     *
     * @param authorization
     */
    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    private String getAuthorization(String method, String url, Map<String, Object> requestParams) {
        if (!TextUtils.isEmpty(authorization)) {
            return authorization;
        }

        return BaseRequestUtils.getOAuthHeader(method, url, requestParams);
    }

    /**
     * 对外提供接口设置userAgent
     *
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    private String getUserAgent() {
        if (userAgent != null) {
            return userAgent;
        }

        return BaseRequestUtils.getUserAgent();
    }

    /**
     * 封装请求body内容
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] content = null;
        String strBody = CodeUtils.appendParams(new StringBuilder(), mParamMap);
        try {
            content = strBody.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 根据网络返回数据,进行解压解密等操作
     *
     * @param response
     * @return
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        if (response == null || response.data == null) {
            return Response.error(new VolleyError());
        }

        byte[] data = null;
        if (response.headers.containsKey("Content-Encoding") && response.headers.get("Content-Encoding").equals("gzip")) {
            data = decompressZipToByte(response.data);//解压byte数组为字符串
            if (data == null || data.length == 0) {
                return Response.error(new VolleyError());
            }
        } else {
            data = response.data;
        }

        //解析网络返回的数据
        Response<String> rsp = parseResponse(response, data);
        if (rsp != null) {
            return rsp;
        } else {
            return Response.error(new VolleyError());
        }

    }

    /**
     * 解析请求成功返回数据
     *
     * @param response
     * @param data
     * @return
     */
    private Response<String> parseResponse(NetworkResponse response, byte[] data) {
        String parse = null;
        try {
            parse = new String(data, "UTF-8");
            printNetInfo(true, response, parse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Response.success(parse, HttpHeaderParser.parseCacheHeaders(response));
    }

    /**
     * 解析请求失败返回的数据
     *
     * @param volleyError the error retrieved from the network
     * @return
     */
    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (volleyError.networkResponse != null) {
            printNetInfo(false, volleyError.networkResponse, new String(volleyError.networkResponse.data));
        } else {
            printNetInfo(false, null, volleyError.toString());
        }
        return super.parseNetworkError(volleyError);
    }

    /**
     * 输出网络请求详情日志
     *
     * @param success
     * @param response
     * @param parsed
     */
    private void printNetInfo(boolean success, NetworkResponse response, String parsed) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("network request info");
            sb.append("\nmethod:" + (this.getMethod() == Method.GET ? "get" : "post"));
            sb.append("\nurl:" + URLDecoder.decode(this.getUrl(), "UTF-8"));
            sb.append("\nheaders:" + this.headers);
            sb.append("\nAuthorization:" + this.headers.get("Authorization"));
            sb.append("\nbody:" + URLDecoder.decode(new String(this.getBody()), "UTF-8"));
            sb.append("\nstatusCode:" + (response != null ? response.statusCode : "-1"));
            sb.append("\nnetworkTimeMs:" + (response != null ? response.networkTimeMs : "15000"));
            if (!TextUtils.isEmpty(parsed))
                sb.append("\nresponse:\n" + CodeUtils.Unicode2GBK(JsonUtils.isJson(parsed) ? JsonUtils.format(parsed) : parsed));

            if (!success)
                LogUtils.debug_i(TAG, sb.toString());
            else
                LogUtils.debug_i(TAG, sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param body
     * @return
     */
    private byte[] decompressZipToByte(byte[] body) {

        if (body == null || body.length == 0) {
            return null;
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(body);

            //判断是否是GZIP格式
            int ss = (body[0] & 0xff) | ((body[1] & 0xff) << 8);
            if (ss == GZIPInputStream.GZIP_MAGIC) {

                GZIPInputStream gunIn = new GZIPInputStream(in);

                byte[] buffer = new byte[256];
                int n;

                while ((n = gunIn.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }
                return out.toByteArray();

            } else {
                return body;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送网络请求
     */
    @Override
    public void sendRequest() {
        super.sendRequest();
        try {
            LogUtils.debug_i(TAG,"url"+URLEncoder.encode(this.getUrl(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消网络请求
     */
    @Override
    public void cancel() {
        super.cancel();
        try {
            LogUtils.debug_i(TAG, "cancel url:" + URLDecoder.decode(this.getUrl(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
