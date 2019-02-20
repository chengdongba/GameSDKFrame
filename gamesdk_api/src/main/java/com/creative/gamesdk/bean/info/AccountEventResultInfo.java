package com.creative.gamesdk.bean.info;

/**
 * Created by Administrator on 2019/2/16.
 * 用于转换成json对象的实体类,不对外提供get方法
 */

public class AccountEventResultInfo {
    private int eventType;//事件类型
    private int statusCode;//事件状态码
    private String message;//描述信息
    private PlayerInfo playerInfo;//用户信息

    public int getEventType() {
        return eventType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
