package com.creative.gamesdk.module.purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.creative.gamesdk.common.utils_base.config.ErrCode;
import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/26.
 *
 * 购买管理类,管理SDK的各个购买接口:创建订单,三方支付,运营商支付,渠道支付,补单逻辑,包月,订阅等.
 *
 * 注意,可能还会有各个复杂的支付逻辑,可能会先短代支付,然后渠道支付,三方支付,后台切换支付开关等.
 *
 * 后续项目待定
 *
 */

public class PurchaseManager {

    private final String TAG = "PurchaseManager";

    private static volatile PurchaseManager INSTANCE;

    public PurchaseManager() {
    }

    public static PurchaseManager getInstance(){

        if (INSTANCE==null){
            synchronized (PurchaseManager.class){
                if (INSTANCE==null){
                    INSTANCE = new PurchaseManager();
                }
            }
        }

        return INSTANCE;
    }

    /**
     * 创建订单,具体项目具体实现
     * @param activity
     * @param payParams
     * @param callBackListener
     */
    public void creatOrderId(Activity activity, HashMap<String,Object> payParams, CallBackListener callBackListener){
        LogUtils.debug_d(TAG,"payParams = " + payParams.toString());
        String orderID = "DD1441";
        callBackListener.onSuccess(orderID);
    }

    /**
     * 显示支付界面
     * @param activity
     * @param payParams
     * @param callBackListener
     */
    public void showPayView(Activity activity, HashMap<String ,Object> payParams, final CallBackListener callBackListener){
        LogUtils.debug_d(TAG,"payParams: " + payParams.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String message = "充值金额：" + "2"
                + "\n商品名称：" + "大饼"
                + "\n商品数量：" + "1"
                + "\n资费说明：" + "2元";
        builder.setMessage(message);
        builder.setTitle("请确认充值信息");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int index) {
                        //支付结果回调到这里来
                        PurchaseResult purchaseResult = new PurchaseResult(PurchaseResult.PurchaseState,null);
                        callBackListener.onSuccess(purchaseResult);
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        callBackListener.onFailure(ErrCode.FAILURE,"pay fail");
                    }
                });
        builder.create().show();
    }

}
