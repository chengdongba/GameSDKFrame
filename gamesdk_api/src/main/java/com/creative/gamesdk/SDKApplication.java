package com.creative.gamesdk;


import android.content.Context;

import com.creative.gamesdk.project.ProjectApplication;

/**
 * Created by Administrator on 2019/2/13.
 * 项目SDK的application
 */

public class SDKApplication extends ProjectApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}