package com.creative.gamesdk.plugin.wechat.login;

/**
 * Created by Administrator on 2019/2/26.
 *
 * 封装微信登陆
 *
 */

public class WechatLogin {

    public static String TAG = "WechatLogin";

    private volatile static WechatLogin INSTANCE;

    private WechatLogin() {
    }

    public static WechatLogin getInstance() {
        if (INSTANCE == null) {
            synchronized (WechatLogin.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WechatLogin();
                }
            }
        }
        return INSTANCE;
    }

}
