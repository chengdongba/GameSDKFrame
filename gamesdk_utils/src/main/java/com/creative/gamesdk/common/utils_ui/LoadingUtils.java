package com.creative.gamesdk.common.utils_ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2019/2/25.
 */

public class LoadingUtils {

    private final ProgressDialog mProgress;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private LoadingUtils(Context context) {
        mProgress = new ProgressDialog(context);
        mProgress.setMessage("Loading...");
    }

    public void setMessage(String msg){
        mProgress.setMessage(msg);
    }

    public void show(){

        if (mProgress!=null){
            try {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dismiss(){
        if (mProgress!=null&&mProgress.isShowing()){
            try {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
