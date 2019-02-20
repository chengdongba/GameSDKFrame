package com.creative.gamesdk.common.utils_base.parse.channel;

import android.text.TextUtils;

import com.creative.gamesdk.common.utils_base.proguard.ProguardObject;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2019/2/20.
 *
 * 渠道SDK实体类
 *
 *   只提供get方法，不提供set
 *
 */

public class ChannelBeanList extends ProguardObject{

    private List<ChannelBean> channel;//注意解析的名字要跟文件一致，不然会导致解析错误

    public List<ChannelBean> getChannel(){
        return channel;
    }

    public static class ChannelBean extends ProguardObject{

        private static final String TAG = "ChannelBean";

        /**
         * channel_name : 渠道SDK名称
         * class_name : 渠道SDK入口类
         * description : 项目描述
         * version : 版本信息
         */

        private String channel_name;
        private String class_name;
        private String description;
        private String version;

        public String getChannel_name() {
            return channel_name;
        }

        public String getClass_name() {
            return class_name;
        }

        public String getDescription() {
            return description;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "ChannelBean{" +
                    " channel_name='" + channel_name + '\'' +
                    ", class_name='" + class_name + '\'' +
                    ", description='" + description + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }

        /**
         * 反射插件的单例模式方法
         * 返回插件可能为空
         * @return
         */
        public Channel invokeGetInstance(){
            Channel channel = null;
            Class<?> glass = null;
            if (TextUtils.isEmpty(class_name)){
                LogUtils.debug_w(TAG,"invokeGetInstance: the class_name is blank");
                return channel;
            }

            try {
                glass = Class.forName(class_name);
            } catch (ClassNotFoundException e) {
                LogUtils.debug_w(TAG,"invokeGetInstance: "+"do not find " +class_name);
            }

            //尝试调用getInstance
            try {
                Method m = glass.getDeclaredMethod("getInstance",new Class<?>[]{});
                m.setAccessible(true);
                channel = (Channel) m.invoke(null,new Object[]{});
            } catch (NoSuchMethodException e) {
                //getInstance调用失败,尝试new其对象
                try {
                    channel = (Channel) glass.newInstance();
                } catch (InstantiationException e1) {
                    LogUtils.debug_w(TAG,"invokeGetInstance: "+"do not find " +class_name);
                } catch (IllegalAccessException e1) {
                    LogUtils.debug_w(TAG,"invokeGetInstance: "+"do not find " +class_name);
                }
            } catch (IllegalAccessException e) {
                LogUtils.debug_w(TAG,"invokeGetInstance: "+"do not find " +class_name);
            } catch (InvocationTargetException e) {
                LogUtils.debug_w(TAG,"invokeGetInstance: "+"do not find " +class_name);
            }

            if (channel==null){
                LogUtils.debug_w(TAG,class_name + "is empty");
            }else {
                channel.channelBean = this;
            }

            return channel;
        }
    }

}
