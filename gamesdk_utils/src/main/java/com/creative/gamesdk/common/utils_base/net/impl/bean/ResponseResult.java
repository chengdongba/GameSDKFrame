package com.creative.gamesdk.common.utils_base.net.impl.bean;

import com.creative.gamesdk.common.utils_base.proguard.ProguardObject;

/**
 * Created by Administrator on 2019/2/19.
 * 服务端数据格式实体
 */

public class ResponseResult<T> extends ProguardObject {
    private String ret;
    private String msg;
    private T data;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
