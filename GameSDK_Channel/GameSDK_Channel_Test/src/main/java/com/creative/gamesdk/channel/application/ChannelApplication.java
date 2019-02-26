package com.creative.gamesdk.channel.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2019/2/13.
 *
 * 预留用于继承渠道Application
 */

public class ChannelApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
