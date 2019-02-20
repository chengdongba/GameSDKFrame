package com.creative.gamesdk.common.utils_base.parse.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.creative.gamesdk.common.utils_base.interfaces.LifeCycleInterface;
import com.creative.gamesdk.common.utils_base.proguard.ProguardInterface;

/**
 * Created by Administrator on 2019/2/20.
 * 基础功能插件基类
 */

public class Plugin implements LifeCycleInterface,ProguardInterface{




    /****************************  生命周期接口  **************************************/
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
