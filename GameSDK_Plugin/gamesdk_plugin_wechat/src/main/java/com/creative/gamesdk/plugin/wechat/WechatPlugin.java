package com.creative.gamesdk.plugin.wechat;

import android.content.Context;

import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.parse.plugin.Plugin;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;
import com.creative.gamesdk.plugin.wechat.pay.WechatPay;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/26.
 *
 * 微信功能插件,方便后续添加支付(H5支付)接口,登陆接口,统计接口
 *
 */

public class WechatPlugin extends Plugin{

    private final String TAG = "WechatPlugin";

    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init: " + getClass().getSimpleName());
    }

    /**
     * 调用微信支付接口
     * @param context
     * @param payMap
     * @param callBackListener
     */
    public void wechatPay(Context context, HashMap<String,Object> payMap, CallBackListener callBackListener){
        WechatPay.getInstance().pay(context,payMap,callBackListener);
    }

    /**
     * 调用微信登陆接口
     * @param context
     * @param loginMap
     * @param callBackListener
     */
    public void wechatLogin(Context context,HashMap<String,Object> loginMap,CallBackListener callBackListener){

    }

    /**
     * 调用微信分享接口
     * @param context
     * @param shareMap
     * @param callBackListener
     */
    public void wechatShare(Context context,HashMap<String,Object> shareMap,CallBackListener callBackListener){

    }

    /**
     *
     * @param context
     */
    @Override
    public void onResume(Context context) {
        WechatPay.getInstance().onResume(context);
    }
}
