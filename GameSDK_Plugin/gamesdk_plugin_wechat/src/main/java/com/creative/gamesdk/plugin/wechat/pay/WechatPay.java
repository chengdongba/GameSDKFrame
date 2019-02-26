package com.creative.gamesdk.plugin.wechat.pay;

import android.content.Context;

import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/26.
 *
 * 封装微信支付
 *
 */

public class WechatPay {

    public static String TAG = "WechatPay";

    private static volatile WechatPay INSTANCE;

    public WechatPay() {
    }

    public static WechatPay getInstance(){
        if (INSTANCE==null){
            synchronized (WechatPay.class){
                if (INSTANCE==null){
                    INSTANCE = new WechatPay();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 微信app支付
     * @param context
     * @param payMap
     * @param callBackListener
     */
    public void pay(Context context, HashMap<String,Object> payMap, CallBackListener callBackListener){

    }

    /**
     * 处理微信没有回调的问题
     * @param context
     */
    public void onResume(Context context){
        LogUtils.debug_d(TAG,"onResume");
    }
}
