package com.creative.gamesdk.module.init;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.creative.gamesdk.common.utils_base.cache.ApplicationCache;
import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.parse.channel.ChannelManager;
import com.creative.gamesdk.common.utils_base.parse.plugin.PluginManager;
import com.creative.gamesdk.common.utils_base.parse.project.ProjectManager;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;
import com.creative.gamesdk.common.utils_business.cache.BaseCache;
import com.creative.gamesdk.common.utils_business.cache.SDKInfoCache;
import com.creative.gamesdk.common.utils_business.cache.SharedPreferencesCache;
import com.creative.gamesdk.common.utils_business.config.KeyConfig;
import com.creative.gamesdk.common.utils_business.config.UrlConfig;

/**
 * Created by Administrator on 2019/2/26.
 * <p>
 * SDK初始化逻辑
 * 后继可能还会有复杂的初始化逻辑要做
 * 如:初始化获取后台开关,切换域名,获取权限等
 */

public class InitManager {

    private final String TAG = getClass().getSimpleName();
    private static volatile InitManager INSTANCE;

    public InitManager() {
    }

    public static InitManager getInstance() {
        if (INSTANCE == null) {
            synchronized (InitManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new InitManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 加载SDK项目配置入口插件(这是最开始加载的)
     * @param cxt
     * @param context 上下文
     * @param isDebug 日志调试开关
     */
    public void initApplication(Application cxt, Context context,boolean isDebug){
        ApplicationCache.init(cxt);
        LogUtils.setDebugLogModel(isDebug);
        ProjectManager.init(context).loadAllProjects();
        //聚合SDK加载渠道插件
        ChannelManager.init(context).loadChannel();
    }

    private static Handler sApiHandler;
    private static boolean initState = false;

    /**
     * SDK初始化逻辑
     * @param activity
     * @param gameid
     * @param gamekey
     * @param callBackListener
     */
    public void init (final Activity activity, final String gameid, final String gamekey, final CallBackListener callBackListener){
        if (sApiHandler==null){
            HandlerThread ht = new HandlerThread("project_sdk_thread", Process.THREAD_PRIORITY_BACKGROUND);
            ht.start();
            sApiHandler = new Handler(ht.getLooper());
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                //1.初始化全局缓存变量
                BaseCache.init(activity.getApplication());
                BaseCache.getInstance().put(KeyConfig.GAME_ID,gameid);
                BaseCache.getInstance().put(KeyConfig.GAME_KEY,gamekey);

                //2.初始化SDK参数
                SDKInfoCache.getDefault(activity.getApplication());

                //3.初始化持久化数据
                SharedPreferencesCache spCache = new SharedPreferencesCache(activity);
                spCache.init();

                //4.加载功能插件
                PluginManager.init(activity).loadAllPlugins();

                //5.初始化域名配置
                UrlConfig.initUrl();

                //6.开始初始化逻辑
                startInitLogic(activity,callBackListener);
            }
        };

        sApiHandler.post(r);
    }

    /**
     * 初始化逻辑
     * @param activity
     * @param callBackListener
     */
    private void startInitLogic(Activity activity, CallBackListener callBackListener) {
        //----------------已初始化完成------------------
        setInitState(true);
        callBackListener.onSuccess(null);
    }

    public void setInitState(boolean state) {
        initState = state;
        BaseCache.getInstance().put(KeyConfig.IS_INIT,initState);
    }

    public boolean getInitState(){
        return initState;
    }

    /**
     * 初始化功能插件
     * @param activity
     */
    private void initFunctionPlugin(Activity activity){

        //腾讯bugly日志收集

    }
}
