package com.creative.gamesdk.common.utils_base.net.impl;

import android.content.Context;
import android.os.Build;

import com.creative.gamesdk.common.utils_base.cache.ApplicationCache;
import com.creative.gamesdk.common.utils_base.utils.ContextUtils;
import com.creative.gamesdk.common.utils_base.utils.Md5Utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2019/2/19.
 *
 * 用于获取oathHeader和userAgent
 *
 */

public class BaseRequestUtils {
    public static final String SIGNATURE_METHOD = "MD5";
    public static final String VERSION_1_0 = "1.0";

    public static String getUserAgent(){
        Context context = ApplicationCache.getInstance().getApplicationContext();
        StringBuilder builder = new StringBuilder(256);
        builder.append("SYSDK/1.0")
                .append("(android:").append(Build.VERSION.RELEASE)
                .append(";app_version:").append(ContextUtils.getVersionName(context))
                .append(";package:").append(context.getPackageName())
                .append(";network_type:").append(ContextUtils.getNetworkType(context))
                .append(";imei:").append(ContextUtils.getDeviceId(context))
                .append(";device_brand:").append(Build.BRAND)
                .append(";device_model:").append(Build.MODEL)
                .append(";resolution:").append(ContextUtils.getResolutionAsString(context))
                .append(";cpu_freq:").append(ContextUtils.getCpuFre())
                .append(";game_name:").append(URLEncoder.encode(ContextUtils.getLabel(context)))
                .append(";sim_type:").append(ContextUtils.getSimCardType(context))
                .append(";platform:").append(ContextUtils.getDeviceName())
                .append(")");
        return builder.toString();
    }

    public static String getOAuthHeader(String method, String url, Map<String, ?> requestParams) {

        String OAuthMethod = SIGNATURE_METHOD;
        String version = VERSION_1_0;
        String timeStamp = generateTimestamp();
        String nonce = generateNonce();


        HashMap<String,Object> map = new HashMap<>();
        map.put("method",method);
        map.put("url",url);
        map.put("requestParams",requestParams.toString());

        String md5 = Md5Utils.getMd5(map.toString());


        StringBuilder sb = new StringBuilder(256);
        sb.append("SYSDK/1.0")
                .append("(OAuthMethod:").append(OAuthMethod)
                .append(";version:").append(version)
                .append(";time:").append(timeStamp)
                .append(";nonce:").append(nonce)
                .append(";md5:").append(md5)
                .append(")");

        String header = sb.toString();

        return header;
    }

    private static String generateNonce() {
        return Long.toString(new Random().nextLong());
    }

    private static String generateTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000L);
    }
}
