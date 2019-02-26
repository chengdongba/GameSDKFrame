package com.creative.gamesdk.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.creative.gamesdk.common.utils_base.config.ErrCode;
import com.creative.gamesdk.common.utils_base.config.TypeConfig;
import com.creative.gamesdk.common.utils_base.frame.google.gson.JsonObject;
import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.parse.channel.Channel;
import com.creative.gamesdk.common.utils_base.parse.channel.ChannelManager;
import com.creative.gamesdk.common.utils_base.parse.project.Project;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;
import com.creative.gamesdk.common.utils_business.cache.BaseCache;
import com.creative.gamesdk.common.utils_ui.ToastUtils;
import com.creative.gamesdk.module.account.AccountManager;
import com.creative.gamesdk.module.account.bean.AccountCallbackBean;
import com.creative.gamesdk.module.init.InitManager;
import com.creative.gamesdk.module.purchase.PurchaseManager;
import com.creative.gamesdk.module.purchase.PurchaseResult;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/13.
 *
 * (业务逻辑判断)
 *
 * JuHeProject 聚合SDK项目的入口类
 *
 * (注意生命周期方法必接,且必须初始化后再调用渠道,功能插件的生命周期方法)
 *
 */

public class JuHeProject extends Project{

    private final String TAG = getClass().getSimpleName();

    //渠道对象
    private Channel channel;
    private Activity mActivity;

    /**
     * 表明项目功能入口已实例化,可以正常加载走后续SDK功能逻辑
     */
    @Override
    protected synchronized void initProject() {
        LogUtils.d(TAG,getClass().getSimpleName()+" has init");
        super.initProject();
    }

    /**************************  初始化  *********************************/

    @Override
    public void init(final Activity activity, String gameid, String gamekey, final CallBackListener callBackListener) {
        super.init(activity, gameid, gamekey, callBackListener);
        LogUtils.d(TAG," init ");

        if (activity==null||callBackListener==null){
            callBackListener.onFailure(ErrCode.PARAMS_ERROR," activity or callBackListener is empty");
            return;
        }

        AccountManager.getInstance().setLoginCallbackListener(projectAccountCallBackListener);

        InitManager.getInstance().init(activity, gameid, gamekey, new CallBackListener() {
            @Override
            public void onSuccess(Object object) {
                channel = ChannelManager.getInstance().getChannel();//注意渠道配置文件有没有加载
                channel.init(activity, null, new CallBackListener() {
                    @Override
                    public void onSuccess(Object object) {
                        //项目SDK初始化成功后,调用渠道SDK初始化
                        InitManager.getInstance().setInitState(true);
                        callBackListener.onSuccess(object);
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        InitManager.getInstance().setInitState(false);
                        callBackListener.onFailure(errCode,errMsg);
                    }
                });
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                callBackListener.onFailure(errCode,errMsg);
            }
        });
    }

    /*******************************  账号  ****************************************/

    /*** SDKApi层设置回调监听*/
    private CallBackListener ApiAccountCallback;

    @Override
    public void setAccountCallBackLister(CallBackListener callBackLister) {
        ApiAccountCallback = callBackLister;
    }

    /**
     * 监听AccountManager登陆,切换账号,绑定,注销账号的回调信息
     */
    private CallBackListener projectAccountCallBackListener = new CallBackListener<AccountCallbackBean>() {

        @Override
        public void onSuccess(AccountCallbackBean accountCallbackBean) {
            ApiAccountCallback.onSuccess(accountCallbackBean);
        }

        @Override
        public void onFailure(int errCode, String errMsg) {
            //不会走到这里
        }
    };

    private void AccountOnFailCallBack(int event,int code,String msg){
        AccountCallbackBean accountCallbackBean = new AccountCallbackBean();
        accountCallbackBean.setEvent(event);
        accountCallbackBean.setErrorCode(code);
        accountCallbackBean.setMsg(msg);
        ApiAccountCallback.onSuccess(accountCallbackBean);
    }

    private CallBackListener authCallBackListener = new CallBackListener<HashMap<String,Object>>() {

        @Override
        public void onSuccess(HashMap<String, Object> loginAuthData) {
            LogUtils.debug_d(TAG,channel.getClass().getSimpleName()+" = "+ loginAuthData.toString());

            int type = (int) loginAuthData.get(Channel.PARAMS_OAUTH_TYPE);

            //类型为登陆,切换账号,就走服务器登陆逻辑
            if (type== TypeConfig.LOGIN||type==TypeConfig.SWITCHACCOUNT){
                AccountManager.getInstance().authLogin(mActivity,loginAuthData);
            }

            //类型为登出
            if (type==TypeConfig.LOGOUT){
                AccountManager.getInstance().logout(mActivity);
            }
        }

        @Override
        public void onFailure(int errCode, String errMsg) {
            LogUtils.debug_d(TAG,channel.getClass().getSimpleName()+errMsg);

            //做失败处理
            switch (errCode){
                case ErrCode.CHANNEL_LOGIN_FAIL: //登录失败
                    AccountOnFailCallBack(TypeConfig.LOGIN, ErrCode.FAILURE,errMsg);
                    break;

                case ErrCode.CHANNEL_LOGIN_CANCEL: //登录取消
                    AccountOnFailCallBack(TypeConfig.LOGIN, ErrCode.CANCEL,errMsg);
                    break;

                case ErrCode.CHANNEL_SWITCH_ACCOUNT_FAIL: //切换账号失败
                    AccountOnFailCallBack(TypeConfig.SWITCHACCOUNT, ErrCode.FAILURE,errMsg);
                    break;

                case ErrCode.CHANNEL_SWITCH_ACCOUNT_CANCEL: //切换账号取消
                    AccountOnFailCallBack(TypeConfig.SWITCHACCOUNT, ErrCode.CANCEL,errMsg);
                    break;

                case ErrCode.CHANNEL_LOGOUT_FAIL:// 注销失败
                    AccountOnFailCallBack(TypeConfig.LOGOUT, ErrCode.FAILURE,errMsg);
                    break;

                case ErrCode.CHANNEL_LOGOUT_CANCEL: //注销取消
                    AccountOnFailCallBack(TypeConfig.LOGOUT, ErrCode.CANCEL,errMsg);
                    break;
            }

        }
    };

    @Override
    public void login(Activity activity, HashMap<String, Object> loginParams) {
        LogUtils.d(TAG,"login");

        if (activity==null){
            AccountOnFailCallBack(TypeConfig.LOGIN,ErrCode.PARAMS_ERROR,"activity is empty");
            return;
        }

        if (!InitManager.getInstance().getInitState()){
            ToastUtils.showToast(activity,"请先初始化");
            return;
        }

        mActivity = activity;
        channel.login(activity,loginParams,authCallBackListener);
    }

    @Override
    public void switchAccount(Activity activity) {
        LogUtils.d(TAG,"switchAccount");
        if (activity==null){
            AccountOnFailCallBack(TypeConfig.SWITCHACCOUNT,ErrCode.PARAMS_ERROR,"activity is null");
            return;
        }

        if (!InitManager.getInstance().getInitState()){
            ToastUtils.showToast(activity,"please init first");
            return;
        }

        if (!AccountManager.getInstance().getLoginState()){
            ToastUtils.showToast(activity,"please login first");
            return;
        }

        mActivity = activity;
        if (isSupport(TypeConfig.FUNC_SWITCHACCOUNT)){
            channel.switchAccount(activity,authCallBackListener);
        }else {
            ToastUtils.showToast(activity,"does not implement this method");
        }
    }

    @Override
    public void logout(Activity activity) {
        LogUtils.d(TAG,"logout");

        if (activity==null){
            AccountOnFailCallBack(TypeConfig.LOGOUT,ErrCode.PARAMS_ERROR,"activity is null");
            return;
        }

        if (!InitManager.getInstance().getInitState()){
            ToastUtils.showToast(activity,"please init first");
            return;
        }

        if (!AccountManager.getInstance().getLoginState()){
            ToastUtils.showToast(activity,"please login first");
            return;
        }

        mActivity = activity;
        if (isSupport(TypeConfig.LOGOUT)){
            channel.logout(activity,authCallBackListener);
        }else {
            ToastUtils.showToast(activity,"dose not implement this method");
        }
    }

    /**************************** 购买 ***************************/

    @Override
    public void pay(final Activity activity, final HashMap<String, Object> payParams, final CallBackListener callBackListener) {
        LogUtils.d(TAG,"pay");
        if (activity==null||callBackListener==null){
            callBackListener.onFailure(ErrCode.PARAMS_ERROR,"activity or callbackListener is null");
            return;
        }

        if (!InitManager.getInstance().getInitState()){
            ToastUtils.showToast(activity,"please init first");
            return;
        }

        if (!AccountManager.getInstance().getLoginState()){
            ToastUtils.showToast(activity,"please login first");
            return;
        }

        PurchaseManager.getInstance().creatOrderId(activity, payParams, new CallBackListener() {
            @Override
            public void onSuccess(Object object) {
                String orderId = (String) object;

                //创建订单成功,回调订单信息和cp透传信息
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderId",orderId);
                PurchaseResult purchaseResult = new PurchaseResult(PurchaseResult.OrderState,jsonObject);
                callBackListener.onSuccess(purchaseResult);

                payParams.put("orderId",orderId);

                channel.pay(activity, payParams, new CallBackListener() {
                    @Override
                    public void onSuccess(Object object) {
                        //支付结果回调到这里
                        PurchaseResult result = new PurchaseResult(PurchaseResult.OrderState,null);
                        callBackListener.onSuccess(result);
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        callBackListener.onFailure(errCode,errMsg);
                    }
                });
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                callBackListener.onFailure(errCode,"create order fail");
            }
        });
    }

    @Override
    public void showFloatView(Activity activity) {
        LogUtils.d(TAG,"showFloatView");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(activity,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        mActivity = activity;

        if (isSupport(TypeConfig.FUNC_SHOW_FLOATWINDOW)){
            channel.showFloatView(activity);

        }else {
            Toast.makeText(activity,"没有实现该方法",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void dismissFloatView(Activity activity) {
        LogUtils.d(TAG,"dismissFloatView");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(activity,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        mActivity = activity;

        if (isSupport(TypeConfig.FUNC_DISMISS_FLOATWINDOW)){
            channel.dismissFloatView(activity);

        }else {
            Toast.makeText(activity,"没有实现该方法",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void exit(Activity activity, CallBackListener callBackListener) {
        LogUtils.d(TAG,"exit");

        if (activity == null || callBackListener == null) {
            callBackListener.onFailure(ErrCode.PARAMS_ERROR,"activity or callBackListener is null");
            return;
        }

        mActivity = activity;

        channel.exit(activity,callBackListener);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        LogUtils.d(TAG,"reportData");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(context,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        channel.reportData(context,dataMap);
    }


    @Override
    public void extendFunction(Activity activity, int functionType, Object object, CallBackListener callBackListener) {
        super.extendFunction(activity, functionType, object, callBackListener);
    }

    @Override
    public String getChannelID() {
        return BaseCache.getInstance().getChannelId();
    }

    /**
     * 是否支持该接口,由于部分渠道只简单实现登陆,支付接口
     * @param funcType
     * @return
     */
    public boolean isSupport(int funcType){
        return channel.isSupport(funcType);
    }

    /******************************  生命周期接口  ******************************/

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        LogUtils.d(TAG,"onCreate");

        if (InitManager.getInstance().getInitState()){
            super.onCreate(activity, savedInstanceState);
            channel.onCreate(activity,savedInstanceState);
        }
    }

    @Override
    public void onStart(Activity activity) {
        LogUtils.d(TAG,"onStart");
        if (InitManager.getInstance().getInitState()){
            super.onStart(activity);
            channel.onStart(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        LogUtils.d(TAG,"onResume");

        if (InitManager.getInstance().getInitState()){
            super.onResume(activity);
            channel.onResume(activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        LogUtils.d(TAG,"onPause");
        if (InitManager.getInstance().getInitState()){
            super.onPause(activity);
            channel.onPause(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        LogUtils.d(TAG,"onStop");
        if (InitManager.getInstance().getInitState()){
            super.onStop(activity);
            channel.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        LogUtils.d(TAG,"onDestroy");
        if (InitManager.getInstance().getInitState()){
            super.onDestroy(activity);
            channel.onDestroy(activity);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        LogUtils.d(TAG,"onActivityResult");
        if (InitManager.getInstance().getInitState()){
            super.onActivityResult(activity, requestCode, resultCode, data);
            channel.onActivityResult(activity,requestCode,resultCode,data);
        }
    }

    @Override
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,int[] grantResults) {
        LogUtils.d(TAG,"onRequestPermissionsResult");
        if (InitManager.getInstance().getInitState()){
            super.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
            channel.onRequestPermissionsResult(activity,requestCode,permissions,grantResults);
        }
    }

}
