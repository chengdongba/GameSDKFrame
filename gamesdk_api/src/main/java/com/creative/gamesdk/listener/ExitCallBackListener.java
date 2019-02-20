package com.creative.gamesdk.listener;

/**
 * Created by Administrator on 2019/2/16.
 * 对外的SDK Api 退出监听接口
 */

public interface ExitCallBackListener {

    /**
     * 退出框退出成功回调
     */
    void onExitDialogSuccess();

    /**
     * 退出框退出取消回调
     */
    void onExitDialogCancel();

    /**
     * 不存在退出框,需要游戏自己实现退出框
     */
    void onNotExitDialog();
}
