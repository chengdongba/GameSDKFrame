package com.creative.gamesdk.module.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.creative.gamesdk.common.utils_base.config.ErrCode;
import com.creative.gamesdk.common.utils_base.config.TypeConfig;
import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;
import com.creative.gamesdk.common.utils_business.cache.BaseCache;
import com.creative.gamesdk.common.utils_business.config.KeyConfig;
import com.creative.gamesdk.module.account.bean.AccountBean;
import com.creative.gamesdk.module.account.bean.AccountCallbackBean;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/25.
 * 账号管理类,管理SDK的各个功能逻辑,登陆,注册,注销登陆,账号绑定,切换账号
 * 注意:可能还会有复杂的登陆,绑定等逻辑
 * 后继项目待定
 */

public class AccountManager {

    private final String TAG = getClass().getSimpleName();

    private static volatile AccountManager INSTANCE;

    private AccountManager() {

    }

    public static AccountManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AccountManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AccountManager();
                }
            }
        }
        return INSTANCE;
    }

    private Activity mActivity;
    private AccountBean mLoginInfo;//当前登录的登陆信息
    private boolean isSwitchAccount;//通过标记位判断是否是切换账号回调

    /********************************  获取project账号监听 *****************************************/

    private CallBackListener projectLoginCallBackListener;

    public void setLoginCallbackListener(CallBackListener callbackListener) {
        projectLoginCallBackListener = callbackListener;
    }

    private void callbackToProject(int event, int code, AccountBean accountBean, String msg) {
        //设置回调信息
        AccountCallbackBean accountCallbackBean = new AccountCallbackBean();
        accountCallbackBean.setEvent(event);
        accountCallbackBean.setErrorCode(code);
        accountCallbackBean.setAccountBean(accountBean);
        accountCallbackBean.setMsg(msg);
        if (projectLoginCallBackListener != null) {
            projectLoginCallBackListener.onSuccess(accountCallbackBean);
        }
    }

    /**
     * 登陆结果监听
     */
    private CallBackListener<AccountBean> loginCallBackListener = new CallBackListener<AccountBean>() {
        @Override
        public void onSuccess(AccountBean loginInfo) {
            LogUtils.d(TAG, "loginInfo " + loginInfo.toString());
            mLoginInfo = loginInfo;
            //登陆成功，设置登录信息
            setLoginSuccess(loginInfo);
            if (isSwitchAccount) {
                callbackToProject(TypeConfig.SWITCHACCOUNT, ErrCode.SUCCESS, loginInfo, "user switchAccount success");
                isSwitchAccount = false;
            } else {
                callbackToProject(TypeConfig.LOGIN, ErrCode.SUCCESS, loginInfo, "user login success");
            }
        }

        @Override
        public void onFailure(int errCode, String errMsg) {
            mLoginInfo = null;
            if (isSwitchAccount) {
                if (errCode == ErrCode.CANCEL) {
                    callbackToProject(TypeConfig.LOGOUT, ErrCode.SUCCESS, null, "user logout success");
                } else {
                    callbackToProject(TypeConfig.SWITCHACCOUNT, errCode, null, errMsg);
                }
            } else {
                callbackToProject(TypeConfig.LOGIN, errCode, null, errMsg);
            }
        }
    };

    /**
     * 设置登陆成功行为
     *
     * @param loginInfo
     */
    private void setLoginSuccess(AccountBean loginInfo) {
        if (loginInfo != null) {
            BaseCache.getInstance().put(KeyConfig.PLAYER_ID, loginInfo.getUserID());
            BaseCache.getInstance().put(KeyConfig.PLAYER_NAME, loginInfo.getUserName());
            BaseCache.getInstance().put(KeyConfig.PLAYER_TOKEN, loginInfo.getUserToken());

            //将当前状态保存到全局变量,供其他插件模块使用
            BaseCache.getInstance().put(KeyConfig.IS_LOGIN, getLoginState());
        }
    }

    /**
     * 获取当前登陆状态
     * 默认false
     *
     * @return
     */
    public boolean getLoginState() {
        if (mLoginInfo != null) {
            return mLoginInfo.getLoginState();
        }
        return false;
    }

    /*****************************  登陆 *******************************/

    /**
     * 显示登陆界面
     *
     * @param activity
     * @param loginMap
     */
    public void showLoginView(Activity activity, HashMap<String, Object> loginMap) {
        mActivity = activity;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("登陆界面")
                .setMessage("是否登陆")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccountBean loginInfo = new AccountBean();
                        loginInfo.setLoginState(true); //将登录成功状态返回
                        loginInfo.setUserToken("dasfkaf-SAFA-kfad");
                        loginInfo.setUserID("userID-123");
                        loginInfo.setUserName("测试用户"); //聚合将用名设置为UserID
                        loginCallBackListener.onSuccess(loginInfo);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginCallBackListener.onFailure(ErrCode.FAILURE, "login fail");
                    }
                })
                .create()
                .show();
    }

    /**
     * 授权登陆,具体项目具体实现逻辑
     *
     * @param activity
     * @param loginMap
     */
    public void authLogin(Activity activity, HashMap<String, Object> loginMap) {

        mActivity = activity;

        AccountBean accountBean = new AccountBean();
        accountBean.setLoginState(true);
        accountBean.setUserName("测试账号");
        accountBean.setUserID("userID-123");
        accountBean.setUserToken("dasfkaf-SAFA-kfad");

        loginCallBackListener.onSuccess(accountBean);
    }

    /************************* 切换账号 *************************************/

    public void switchAccount(Activity activity) {
        mActivity = activity;
        mLoginInfo = null;
        isSwitchAccount = true;
        clearLoginInfo(activity);
    }

    /**
     * 清空登陆信息
     *
     * @param activity
     */
    private void clearLoginInfo(Activity activity) {
        mLoginInfo = null;

        //清空内存的用户信息
        BaseCache.getInstance().put(KeyConfig.PLAYER_ID, "");
        BaseCache.getInstance().put(KeyConfig.PLAYER_NAME, "");
        BaseCache.getInstance().put(KeyConfig.PLAYER_TOKEN, "");

        //将当前状态存储到全局变量供其他模块插件使用
        BaseCache.getInstance().put(KeyConfig.IS_LOGIN, getLoginState());
    }

    /*********************************  账号登出  *************************************/

    public void logout(Activity activity) {
        mActivity = activity;
        mActivity = activity;

        mLoginInfo = null; //登录信息清空
        isSwitchAccount = false;
        clearLoginInfo(activity);

        callbackToProject(TypeConfig.LOGOUT, ErrCode.SUCCESS, null, "user logout success");
    }
}
