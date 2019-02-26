package com.creative.gamesdk.module.purchase;

/**
 * Created by Administrator on 2019/2/26.
 *
 * 用于描述支付回调的信息
 */

public class PurchaseResult {

    public static final int OrderState = 1;//创建订单成功
    public static final int PurchaseState = 2;//支付结果

    public int status;
    public Object message;

    public PurchaseResult(int status, Object message) {
        this.status = status;
        this.message = message;
    }
}

