package com.creative.gamesdk.build.utils;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by Administrator on 2019/2/28.
 */

public class Utils {
    public static final int OK = 0;
    public static final int ERROR = 10001;
    //小写系统名称
    private static final String os = System.getProperty("os.name").toLowerCase();

    public static String OS_SEMICOLONE = isWindows()?";":":";

    private static boolean isWindows(){
        return os.indexOf("win")>=0;
    }

    /**
     * Get jar file path list String,use the delimiter splice
     * @param libsPath
     * @param delimiter
     * @return
     */
    public static String getJarPathSet(String libsPath,String delimiter){
        File file = new File(libsPath);
        String jarPathSet = "";
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File libFlie = files[i];
            if (libFlie.getName().endsWith(".jar")){
                if (!TextUtils.isEmpty(jarPathSet)){
                    jarPathSet+=jarPathSet+delimiter;
                }
                jarPathSet+=libFlie.getAbsolutePath();
            }
        }
        return jarPathSet;
    }

}
