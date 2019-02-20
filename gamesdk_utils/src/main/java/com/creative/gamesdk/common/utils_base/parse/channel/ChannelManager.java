package com.creative.gamesdk.common.utils_base.parse.channel;

import android.content.Context;
import android.text.TextUtils;

import com.creative.gamesdk.common.utils_base.frame.google.gson.Gson;
import com.creative.gamesdk.common.utils_base.frame.google.gson.JsonSyntaxException;
import com.creative.gamesdk.common.utils_base.utils.FileUtils;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Administrator on 2019/2/20.
 * 渠道管理类
 */

public class ChannelManager {

    private static final String TAG = "ChannelManager";
    private static String CHANNEL_CONFIG = "Channel_config.txt";

    private Channel channel;
    private HashMap<String, ChannelBeanList.ChannelBean> channelBeans = new HashMap<>();


    /************************ 同步锁双重检测机制实现单例模式(懒加载) **********************************/

    public static volatile ChannelManager channelManager;
    private ChannelBeanList channelBeanList;

    public static ChannelManager init(Context context) {
        if (channelManager == null) {
            synchronized (ChannelManager.class) {
                if (channelManager == null) {
                    channelManager = new ChannelManager(context);
                }
            }
        }
        return channelManager;
    }

    public static ChannelManager getInstance() {
        return channelManager;
    }

    private ChannelManager(Context context) {
        parse(context, CHANNEL_CONFIG);
    }

    /**
     * 从配置文件中读取插件配置
     *
     * @param context
     * @param channelConfig
     */
    private void parse(Context context, String channelConfig) {
        StringBuilder channelContent = FileUtils.readAssetsFile(context, channelConfig);
        String strChannelContent = String.valueOf(channelContent);

        if (!TextUtils.isEmpty(strChannelContent)) {
            Gson gson = new Gson();
            try {
                ChannelBeanList channelBeanList = gson.fromJson(strChannelContent, ChannelBeanList.class);
                if (channelBeanList.getChannel() != null) {
                    for (ChannelBeanList.ChannelBean channelBean : channelBeanList.getChannel()) {
                        channelBeans.put(channelBean.getChannel_name(), channelBean);
                    }
                    LogUtils.debug_i(TAG, CHANNEL_CONFIG + " parse \n" + channelBeans.toString());
                } else {
                    LogUtils.e(TAG, CHANNEL_CONFIG + " parse error ");
                }

            } catch (JsonSyntaxException e) {
                LogUtils.e(TAG, CHANNEL_CONFIG + " parse\n" + " parse exception ");
            }
        } else {
            LogUtils.e(TAG, CHANNEL_CONFIG + " parse is blank ");
        }
    }


    private boolean hasLoaded = false;
    private HashMap<String, Channel> channelList = new HashMap<>();

    /**
     * 加载所有渠道,可能存在多个渠道
     */
    public synchronized void loadAllChannels() {
        if (hasLoaded) {
            return;
        }
        Set<String> keys = channelBeans.keySet();
        for (String key : keys) {
            loadChannel(key);
        }
        LogUtils.i(TAG, "loadAllChannels: " + channelList.toString());
        hasLoaded = true;
    }

    /**
     * 加载一个渠道,返回渠道可能为空
     *
     * @param channelName
     */
    private Channel loadChannel(String channelName) throws RuntimeException {
        //1.查看从配置文件中读取的插件列表,是否存在此插件
        HashMap<String, ChannelBeanList.ChannelBean> entries = channelBeans;
        ChannelBeanList.ChannelBean channelBean = entries.get(channelName);
        if (channelBean == null) {
            LogUtils.debug_w(TAG, "loadChannel: " + "the channel:" + channelName + "dose not exit in" + CHANNEL_CONFIG);
            return null;
        }
        //2.调用其单例模式方法
        Channel channel = channelBean.invokeGetInstance();

        if (channel != null) {
            //3.反射插件初始化方法
            channel.initChannel();
            //4.将插件添加到channelList中
            channelList.put(channelName, channel);
        }
        return channel;
    }

    /**
     * 获取特定渠道,可能为空
     *
     * @param channelName
     * @return
     */
    public Channel getChannel(String channelName) {
        if (!hasLoaded) {
            LogUtils.debug_i(TAG, "getChannel " + channelName + " Channel is not load yet");
            return null;
        }
        Channel channel = null;
        HashMap<String, Channel> entries = channelList;
        channel = entries.get(channelName);
        return channel;
    }

    /**
     * 当前的加载渠道SDK(只有一个渠道,且渠道名为channel)
     */
    public void loadChannel() {
        ChannelBeanList.ChannelBean channelBean = channelBeans.get("channel");
        if (channelBean == null) {
            LogUtils.debug_d(TAG, "The channel dose not exit in " + CHANNEL_CONFIG);
            return;
        }
        Channel channel = null;
        channel = channelBean.invokeGetInstance();
        if (channel != null) {
            channel.initChannel();
            this.channel = channel;
        }
    }

    /**
     * 返回当前渠道名为channel的渠道对象
     *
     * @return
     */
    public Channel getChannel() {
        return this.channel;
    }

}
