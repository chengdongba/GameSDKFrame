package com.creative.gamesdk.plugin.alipay;

import android.content.Context;

import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.parse.plugin.Plugin;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/26.
 *
 * 支付宝功能插件,方便后续添加登陆接口,支付(H5支付)接口,统计接口
 *
 */

public class AliPayPlugin extends Plugin {

    private final String TAG = "AlipayPlugin";

    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init: " + getClass().getSimpleName());
    }

    /**
     * 调用支付宝支付app接口
     * @param context
     * @param payMap
     * @param callBackListener
     */
    public void alipay(Context context, HashMap<String,Object> payMap, CallBackListener callBackListener){
//        AlipayPay.getInstance().pay(context,payMap,callBackListener);
    }
}
