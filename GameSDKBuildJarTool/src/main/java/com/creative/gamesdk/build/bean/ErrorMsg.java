package com.creative.gamesdk.build.bean;

/**
 * Created by Administrator on 2019/2/28.
 *
 * 描述打包结果
 *
 */

public class ErrorMsg {

    private int code;
    private String msg;
    private Exception exc;

    public ErrorMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ErrorMsg(int code, String msg, Exception exc) {
        this.code = code;
        this.msg = msg;
        this.exc = exc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
