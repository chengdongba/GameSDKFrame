package com.creative.gamesdk.common.utils_business.cache;

import android.content.Context;
import android.text.TextUtils;

import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2019/2/21.
 * <p>
 * 初始化配置文件信息
 */

public class SDKInfoCache {

    private static final String SDK_JSON = "SDKInfo.json";
    private static final String TAG = "SDKInfoCache";
    private Context mAppContext;
    private ConcurrentHashMap<String, String> mFileStrings = new ConcurrentHashMap<>();

    /***********************  同步锁双重检测机制实现单例模式(懒加载) ******************************/
    public static volatile SDKInfoCache sLoader;

    public SDKInfoCache(Context context) {
        mAppContext = context;
        setConfig(SDK_JSON, parseConfig(SDK_JSON));
    }

    public static SDKInfoCache getDefault(Context context) {
        if (sLoader == null) {
            synchronized (SDKInfoCache.class) {
                if (sLoader == null) {
                    sLoader = new SDKInfoCache(context);
                }
            }
        }
        return sLoader;
    }

    /***********************  同步锁双重检测机制实现单例模式(懒加载) ******************************/

    private static HashMap<String, String> mConfigs = new HashMap<>();

    public String get(String key) {
        return mConfigs.get(key);
    }

    private void setConfig(String filePath, HashMap<String, String> configs) {
        LogUtils.debug_d(TAG, "filePath: " + configs.toString());
        if (!configs.isEmpty()) {
            mConfigs.putAll(configs);
            Set<String> keys = mConfigs.keySet();
            for (String key : keys) {
                BaseCache.getInstance().put(key, mConfigs.get(key));
            }
        }
    }


    private HashMap<String, String> parseConfig(String filePath) {

        HashMap<String, String> configs = new HashMap<>();
        String sConfigs = readFile(filePath);
        if (!TextUtils.isEmpty(sConfigs)) {
            try {
                JSONObject jsonObject = new JSONObject(sConfigs);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    configs.put(key, jsonObject.getString(key));
                }
            } catch (JSONException e) {
                LogUtils.d(e.toString());
            }
        }

        return configs;
    }

    /**
     * 需要同步锁,共有数据缓存,mFileStrings
     * @param fileName
     * @return
     */
    private synchronized String readFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }

        String content = mFileStrings.get(fileName);
        if (content!=null){
            return content;
        }

        InputStream in = null;
        ByteArrayOutputStream baos = null;

        try {
            in = mAppContext.getAssets().open(fileName);
            baos = new ByteArrayOutputStream(1024);

            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer))>0 ){
                baos.write(buffer,0,length);
            }

            String s = baos.toString();
            mFileStrings.put(fileName,s);
            return s;

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (baos!=null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        mFileStrings.put(fileName,"");
        return "";
    }
}
