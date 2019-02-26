package com.creative.gamesdk.project;

import android.content.Context;

import com.creative.gamesdk.channel.application.ChannelApplication;
import com.creative.gamesdk.module.init.InitManager;

/**
 * Created by Administrator on 2019/2/13.
 *
 * 项目SDK的Application
 *
 * projectApplication extends ChannelApplication
 *
 */

public class ProjectApplication extends ChannelApplication {

    @Override
    protected void attachBaseContext(Context context) {
        InitManager.getInstance().initApplication(this,context,true);
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
