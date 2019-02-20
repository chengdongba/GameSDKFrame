package com.creative.gamesdk;

/**
 * Created by Administrator on 2019/2/13.
 *
 * 用于对外给CP获取对应的gameid和gamekey
 */

public class GameInfoSetting {

    public String gameid;
    public String gamekey;

    public GameInfoSetting(String gameid, String gamekey) {
        this.gameid = gameid;
        this.gamekey = gamekey;
    }
}
