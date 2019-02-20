package com.creative.gamesdk.bean.info;

/**
 * Created by Administrator on 2019/2/16.
 * 返回外界的用户信息实体类
 */

public class PlayerInfo {
    private String playerId;
    private String token;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
