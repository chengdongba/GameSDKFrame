package com.creative.gamesdk.common.utils_base.parse.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.creative.gamesdk.common.utils_base.frame.google.gson.Gson;
import com.creative.gamesdk.common.utils_base.frame.google.gson.JsonSyntaxException;
import com.creative.gamesdk.common.utils_base.utils.FileUtils;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Administrator on 2019/2/21.
 * 插件管理类
 */

public class PluginManager extends PluginReflectApi {

    private static final String TAG = "PluginManager";

    public static String PLUGIN_CONFIG = "plugin_config.txt";

    public static HashMap<String, PluginBeanList.PluginBean> pluginBeans = new HashMap<>();

    /**********************  同步锁双重检测机制实现单例模式(懒加载)  **********************************/

    private volatile static PluginManager pluginManager;

    public static PluginManager init(Context context) {
        if (pluginManager == null) {
            synchronized (PluginManager.class) {
                if (pluginManager == null) {
                    pluginManager = new PluginManager(context);
                }
            }
        }
        return pluginManager;
    }

    public static PluginManager getInstance() {
        return pluginManager;
    }

    /**********************  同步锁双重检测机制实现单例模式(懒加载)  **********************************/


    public PluginManager(Context context) {

        parse(context, PLUGIN_CONFIG);
    }

    /**
     * 读取配置文件
     *
     * @param context
     * @param pluginFilePath
     */
    private void parse(Context context, String pluginFilePath) {
        StringBuilder builder = FileUtils.readAssetsFile(context, pluginFilePath);
        String strContent = String.valueOf(builder);
        if (!TextUtils.isEmpty(strContent)) {
            Gson gson = new Gson();
            try {
                PluginBeanList pluginBeanList = gson.fromJson(strContent, PluginBeanList.class);
                if (pluginBeanList.getPlugin().isEmpty()) {
                    LogUtils.e(TAG, PLUGIN_CONFIG + " parse error");
                } else {
                    for (PluginBeanList.PluginBean pluginBean : pluginBeanList.getPlugin()) {
                        pluginBeans.put(pluginBean.getClass_name(), pluginBean);
                    }
                    LogUtils.debug_i(TAG, PLUGIN_CONFIG + " parse \n" + pluginBeans.toString());
                }
            } catch (JsonSyntaxException e) {
                LogUtils.e(TAG, PLUGIN_CONFIG + " parse exception");
            }
        } else {
            LogUtils.e(TAG, PLUGIN_CONFIG + " parse is empty");
        }
    }

    private boolean hasLoaded;
    private HashMap<String, Plugin> pluginLists = new HashMap<>();

    /**
     * 加载所有插件
     *
     * @return
     */
    private synchronized void loadAllPlugins() {
        if (hasLoaded) {
            return;
        }
        HashMap<String, PluginBeanList.PluginBean> entries = pluginBeans;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            loadPlugin(key);
        }
        LogUtils.debug_i(TAG, "loadAllPlugins: " + pluginLists.toString());
        hasLoaded = true;
    }

    /**
     * 加载一个插件,返回的插件可能为空
     *
     * @param pluginName
     * @return
     * @throws RuntimeException
     */
    private Plugin loadPlugin(String pluginName) throws RuntimeException {
        //1.在配置文件中查找是否存在该插件
        HashMap<String, PluginBeanList.PluginBean> entries = pluginBeans;
        PluginBeanList.PluginBean pluginBean = entries.get(pluginName);
        if (pluginBean == null) {
            LogUtils.debug_i(TAG, "The plugin [ " + pluginName + " ] dose not exit in" + PLUGIN_CONFIG);
            return null;
        }
        Plugin plugin = null;
        plugin = pluginBean.invokeGetInstance();
        if (plugin != null) {
            plugin.initPlugin();
            pluginLists.put(pluginName, plugin);
        }
        return plugin;
    }

    /**
     * 获取指定的插件,返回插件可能为空
     *
     * @param pluginName
     * @return
     */
    public Plugin getPlugin(String pluginName) {
        if (!hasLoaded) {
            LogUtils.debug_i(TAG, "getPlugin: " + pluginName + "plugin never load yet");
            return null;
        }
        HashMap<String, Plugin> entries = pluginLists;
        Plugin plugin = null;
        plugin = entries.get(pluginName);
        return plugin;
    }

    /*****************************  生命周期接口  ************************************/

    public void onCreate(Context context, Bundle savedInstanceState) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onCreate(context, savedInstanceState);
        }
    }

    public void onStart(Context context) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onStart(context);
        }
    }

    public void onResume(Context context) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onResume(context);
        }
    }

    public void onPause(Context context) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onPause(context);
        }
    }

    public void onStop(Context context) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onStop(context);
        }
    }

    public void onRestart(Context context) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onRestart(context);
        }
    }

    public void onDestroy(Context context) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onDestroy(context);
        }
    }

    public void onNewIntent(Context context, Intent intent) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onNewIntent(context, intent);
        }
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onActivityResult(context, requestCode, resultCode, data);
        }
    }

    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        HashMap<String, Plugin> entries = pluginLists;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            Plugin plugin = entries.get(key);
            plugin.onRequestPermissionsResult(context, requestCode, permissions, grantResults);
        }
    }
}
