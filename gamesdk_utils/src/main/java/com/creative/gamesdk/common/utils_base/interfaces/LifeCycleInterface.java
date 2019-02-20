package com.creative.gamesdk.common.utils_base.interfaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2019/2/13.
 * 生命周期接口
 */

public interface LifeCycleInterface {

    void onCreate(Context context, Bundle savedInstanceState);

    void onStart(Context context);

    void onResume(Context context);

    void onPause(Context context);

    void onStop(Context context);

    void onRestart(Context context);

    void onDestroy(Context context);

    void onNewIntent(Context context, Intent intent);

    void onActivityResult(Context context, int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults);
}
