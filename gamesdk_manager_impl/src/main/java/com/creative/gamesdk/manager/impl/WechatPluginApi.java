package com.creative.gamesdk.manager.impl;

import android.content.Context;

import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.parse.plugin.Plugin;
import com.creative.gamesdk.common.utils_base.parse.plugin.PluginManager;
import com.creative.gamesdk.common.utils_base.parse.plugin.PluginReflectApi;

import java.util.Map;

/**
 * Created by Administrator on 2019/2/26.
 * 对接微信功能插件的api,反射调用
 */

public class WechatPluginApi extends PluginReflectApi {

    private final String TAG = "WechatPluginApi";

    private Plugin wechatPlugin;

    private volatile static WechatPluginApi INSTANCE;

    private WechatPluginApi() {
        wechatPlugin = PluginManager.getInstance().getPlugin("plugin_wechat");
    }

    public static WechatPluginApi getInstance() {
        if (INSTANCE == null) {
            synchronized (WechatPluginApi.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WechatPluginApi();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 调用微信app支付
     */
    public void pay(Context context, Map<String, Object> map, CallBackListener callBackListener) {

        if (wechatPlugin != null) {
            invoke(wechatPlugin, "wechatPay", new Class<?>[]{Context.class, Map.class, CallBackListener.class},
                    new Object[]{context, map, callBackListener});
        }
    }

}
