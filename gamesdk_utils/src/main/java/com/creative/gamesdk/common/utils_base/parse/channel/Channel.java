package com.creative.gamesdk.common.utils_base.parse.channel;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.interfaces.LifeCycleInterface;
import com.creative.gamesdk.common.utils_base.proguard.ProguardInterface;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/13.
 * 用于描述渠道SDK的顶层接口
 */

public abstract class Channel extends ChannelListenerImpl implements LifeCycleInterface, ProguardInterface {
    public static final String PARAMS_OAUTH_TYPE = "PARAMS_OAUTH_TYPE";
    public static final String PARAMS_OAUTH_URL = "PARAMS_OAUTH_URL";

    /***************************** Channel加载必须接口 *************************/

    /**
     * 示例渠道插件对象,必须实现
     */
    protected abstract void initChannel();

    public ChannelBeanList.ChannelBean channelBean;

    @Override
    public String toString() {
        return "Channel{" + "channelBean = " + channelBean.toString() + "}";
    }

    /***************************** 必须业务逻辑接口 *************************/

    /**
     * 返回渠道ID,用于识别渠道
     * @return
     */
    public abstract String getChannelID();

    /**
     * 由于部分渠道只简单实现登陆,支付接口
     * 对外提供给cp判断接口是否已实现
     * @param FuncType
     * @return
     */
    public abstract boolean isSupport(int FuncType);

    /**
     * 渠道SDK初始化
     */
    public abstract void init(Context context, HashMap<String,Object> initMap, CallBackListener initCallBackListener);

    /**
     * 渠道SDK登录
     */
    public abstract void login(Context context, HashMap<String,Object> loginMap, CallBackListener loginCallBackListener);

    /**
     * 渠道切换账号
     */
    public abstract void switchAccount(Context context, CallBackListener changeAccountCallBackLister);

    /**
     * 渠道SDK注销账号
     */
    public abstract void logout(Context context, CallBackListener logoutCallBackLister);

    /**
     * 渠道SDK支付
     */
    public abstract void pay(Context context, HashMap<String,Object> payMap, CallBackListener payCallBackListener);

    /**
     * 渠道SDK退出
     */
    public abstract void exit(Context context, CallBackListener exitCallBackLister);


    /***************************** 非必须业务逻辑接口 *************************/

    /**
     * 返回渠道版本号
     */
    public String getChannelVersion(){
        return null;
    }

    /**
     * 渠道SDK个人中心
     */
    public void enterPlatform(Context context, CallBackListener enterPlatformCallBackLister){}

    /**
     * 显示渠道SDK悬浮窗
     */
    public void showFloatView(Context context){}

    /**
     * 关闭渠道SDK悬浮窗
     */
    public void dismissFloatView(Context context){}

    /**
     * 渠道SDK上报数据
     */
    public void reportData(Context context, HashMap<String,Object> dataMap){}

    /**
     * 横竖屏
     * @return true为横屏，false为竖屏
     */
    public boolean getOrientation(Context context){
        boolean isLandscape = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        return isLandscape;
    }

    /****************************** 生命周期接口 ***********************************/

    @Override
    public void onCreate(Context context, Bundle savedInstanceState) {

    }

    @Override
    public void onStart(Context context) {

    }

    @Override
    public void onResume(Context context) {

    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onStop(Context context) {

    }

    @Override
    public void onRestart(Context context) {

    }

    @Override
    public void onDestroy(Context context) {

    }

    @Override
    public void onNewIntent(Context context, Intent intent) {

    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {

    }
}
