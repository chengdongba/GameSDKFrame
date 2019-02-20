package com.creative.gamesdk.common.utils_base.cache;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2019/2/18.
 *
 */

public class ApplicationCache {

    private Application mAppContext;
    public Context getApplication(){return mAppContext;}

    /**
     * 缓存全局的applicationContext
     * @return
     */
    public Context getApplicationContext(){
        return mAppContext.getApplicationContext();
    }

    /********************************同步锁双重检测机制实现单例模式(懒加载)**************************************/

    private volatile static ApplicationCache sCache;

    public ApplicationCache(Application appContext) {
        this.mAppContext = appContext;
    }

    public static ApplicationCache getInstance(){
        if (sCache==null){
            throw new RuntimeException("get(context) never called");
        }
        return sCache;
    }

    public static ApplicationCache init(Application ctx){
        if (sCache==null){
            synchronized (ApplicationCache.class){
                if (sCache==null){
                    sCache = new ApplicationCache(ctx);
                }
            }
        }
        return sCache;
    }
}
