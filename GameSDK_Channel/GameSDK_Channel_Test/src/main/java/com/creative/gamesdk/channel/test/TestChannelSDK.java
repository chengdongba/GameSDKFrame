package com.creative.gamesdk.channel.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.creative.gamesdk.common.utils_base.config.TypeConfig;
import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.parse.channel.Channel;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/13.
 * 测试渠道SDK
 */

public class TestChannelSDK extends Channel {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void initChannel() {
        LogUtils.d(TAG, getClass().getSimpleName() + "is init");
    }

    @Override
    public String getChannelID() {
        return "1";
    }

    @Override
    public boolean isSupport(int FuncType) {

        switch (FuncType) {
            case TypeConfig.FUNC_SWITCHACCOUNT:
                return true;

            case TypeConfig.FUNC_LOGOUT:
                return true;

            case TypeConfig.FUNC_SHOW_FLOATWINDOW:
                return true;

            case TypeConfig.FUNC_DISMISS_FLOATWINDOW:
                return true;

            default:
                return false;
        }

    }

    @Override
    public void init(Context context, HashMap<String, Object> initMap, CallBackListener initCallBackListener) {
        LogUtils.d(TAG,getClass().getSimpleName()+" init");
        initOnSuccess(initCallBackListener);
    }

    @Override
    public void login(Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        LogUtils.d(TAG,getClass().getSimpleName()+" login");
        showLoginView(context,loginCallBackListener);
    }

    private void showLoginView(Context context, final CallBackListener loginCallBackListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否登陆?")
                .setMessage("登陆界面")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("sid","testID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loginOnSuccess(json.toString(),loginCallBackListener);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginOnFail("channel login fail",loginCallBackListener);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void switchAccount(final Context context, final CallBackListener changeAccountCallBackLister) {
        LogUtils.d(TAG,getClass().getSimpleName()+" switchAccount");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否切换账号")
                .setMessage("切换账号")
                .setPositiveButton("切换账号", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoginView(context,changeAccountCallBackLister);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchAccountOnCancel("channel switchAccount chancel",changeAccountCallBackLister);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void logout(Context context, final CallBackListener logoutCallBackLister) {
        LogUtils.d(TAG,getClass().getSimpleName()+" logout");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否注销登陆?")
                .setMessage("注销登陆")
                .setPositiveButton("注销", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutOnSuccess(logoutCallBackLister);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginOnCancel("channel logout cancel",logoutCallBackLister);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void pay(Context context, HashMap<String, Object> payMap, final CallBackListener payCallBackListener) {
        LogUtils.d(TAG,getClass().getSimpleName() + " pay");

        String orderID = (String) payMap.get("orderId");
        String productName = (String) payMap.get("productName");
        String productDesc = (String) payMap.get("productDesc");
        String money = String.valueOf(payMap.get("money"));
        String productID = String.valueOf(payMap.get("productID"));
        LogUtils.d(TAG,productID);

        final HashMap<String,Object> paymap = new HashMap<>();
        paymap.put("orderID",orderID);
        paymap.put("productName",productName);
        paymap.put("money",money);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String message = "充值金额：" + money
                + "\n商品名称：" + productName
                + "\n商品数量：" + "1"
                + "\n资费说明：" + productDesc;
        builder.setMessage(message);
        builder.setTitle("请确认充值信息");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int index) {
                        payOnSuccess(payCallBackListener);
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        OnCancel(payCallBackListener);
                    }
                });
        builder.create().show();
    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
        LogUtils.d(TAG,getClass().getSimpleName() + " exit");
        channelNotExitDialog(exitCallBackLister);
    }
}
