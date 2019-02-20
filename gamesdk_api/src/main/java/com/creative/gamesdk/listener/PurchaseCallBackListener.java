package com.creative.gamesdk.listener;

/**
 * Created by Administrator on 2019/2/16.
 * 支付回调
 */

public interface PurchaseCallBackListener {

    /**
     * 返回订单成功信息,比支付结果早
     * @param orderId
     */
    void onOrderId(String orderId);

    /**
     * 支付成功
     */
    void onSuccess();

    /**
     * 支付失败
     * @param errCode
     * @param errMsg
     */
    void onFailure(int errCode,String errMsg);

    /**
     * 支付取消
     */
    void onCancel();

    /**
     * 支付完成,当渠道没有正确回调时,会返回该结果
     */
    void onComplete();
}
