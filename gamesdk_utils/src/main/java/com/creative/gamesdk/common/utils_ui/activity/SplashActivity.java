package com.creative.gamesdk.common.utils_ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2019/2/25.
 * 闪屏
 */

public class SplashActivity extends Activity {

    private ImageView mView;
    private AssetManager mAssets;
    private String[] mImages;
    private int mImageIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mView = new ImageView(this);
        mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mAssets = getAssets();
        try {
            mImages = mAssets.list("splash");
        } catch (IOException e) {
            e.printStackTrace();
        }

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        setContentView(linearLayout, layoutParams);
        linearLayout.addView(mView);

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mImageIndex < mImages.length) {
                            String newFileName = "splash/" + mImages[mImageIndex++];
                            InputStream newSplashFile;
                            try {
                                newSplashFile = mAssets.open(newFileName);
                                mView.setImageBitmap(BitmapFactory.decodeStream(newSplashFile));
                                mView.setScaleType(ImageView.ScaleType.FIT_XY);
                                newSplashFile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            timer.cancel();
                            Intent intent = new Intent("DQCHEN.MAIN");
                            intent.setPackage(SplashActivity.this.getPackageName());
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1500);
        hideVirtualButtons();
    }

    private void hideVirtualButtons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}
