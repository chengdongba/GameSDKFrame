package com.creative.gamesdk.common.utils_ui;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2019/2/25.
 * toast 公共类
 */

public class ToastUtils {

    private static Toast toast;

    public static void showToast(Context context,String content){
        if (content==null){
            return;
        }
        if (toast==null){
            toast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        }else {
            toast.setText(content);
        }
        toast.show();
    }

}
