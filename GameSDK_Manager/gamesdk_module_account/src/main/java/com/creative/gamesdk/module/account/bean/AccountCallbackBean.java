package com.creative.gamesdk.module.account.bean;

/**
 * Created by Administrator on 2019/2/26.
 */

public class AccountCallbackBean {

    private int event;
    private AccountBean accountBean; //账号信息
    private int errorCode; //错误码
    private String msg; // 错误信息

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public AccountBean getAccountBean() {
        return accountBean;
    }

    public void setAccountBean(AccountBean accountBean) {
        this.accountBean = accountBean;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
