package com.creative.gamesdk.manager.impl;

import android.content.Context;

import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.parse.plugin.Plugin;
import com.creative.gamesdk.common.utils_base.parse.plugin.PluginManager;
import com.creative.gamesdk.common.utils_base.parse.plugin.PluginReflectApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/26.
 *
 * 对接支付宝功能插件的api,反射调用
 *
 */

public class AlipayPluginApi extends PluginReflectApi {

    private final String TAG = "AliPayPluginApi";

    private static volatile AlipayPluginApi INSTANCE;

    private Plugin alipayPlugin;

    public AlipayPluginApi() {
        alipayPlugin = PluginManager.getInstance().getPlugin("plugin_alipay");
    }

    public static AlipayPluginApi getInstance(){

        if (INSTANCE==null){
            synchronized (AlipayPluginApi.class){
                if (INSTANCE==null){
                    INSTANCE = new AlipayPluginApi();
                }
            }
        }

        return INSTANCE;
    }

    public void pay(Context context, HashMap<String,Object> payMap, CallBackListener callBackListener){
        if (alipayPlugin!=null){
            invoke("alipay",new Class[]{Context.class, Map.class,CallBackListener.class},new Object[]{context,payMap,callBackListener});
        }
    }

}
