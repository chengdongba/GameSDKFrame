package com.creative.gamesdk.common.utils_ui;

import android.content.Context;

import com.creative.gamesdk.common.utils_base.cache.ApplicationCache;

/**
 * Created by Administrator on 2019/2/25.
 * 加载系统目录下资源工具类
 */

public class ResourseIdUtils {

    public int getId(String name) {
        return getResourceIdByName("id", name);
    }

    public static int getStyleByName(String name) {
        return getResourceIdByName("style", name);
    }

    public static int getStringId(String name) {
        return getResourceIdByName("string", name);
    }

    public static int getColorId(String name) {
        return getResourceIdByName("color", name);
    }

    public static int getStringArrayId(String name) {
        return getResourceIdByName("array", name);
    }

    public static int getDimenId(String dimenName) {
        return getResourceIdByName("dimen", dimenName);
    }

    public static int getDrawableId(String name) {
        return getResourceIdByName("drawable", name);
    }

    public static int getLayoutId(String name) {
        return getResourceIdByName("layout", name);
    }

    public static int getAnimId(String name) {
        return getResourceIdByName("anim", name);
    }

    private static int getResourceIdByName(String className, String name){
        int id = 0;
        try {
            Context context = ApplicationCache.getInstance().getApplicationContext();
            id = context.getResources().getIdentifier(name,className,context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

}
