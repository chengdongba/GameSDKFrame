package com.creative.gamesdk.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.creative.gamesdk.GameInfoSetting;
import com.creative.gamesdk.bean.info.AccountEventResultInfo;
import com.creative.gamesdk.bean.info.PlayerInfo;
import com.creative.gamesdk.bean.params.GameRoleParams;
import com.creative.gamesdk.bean.params.PayParams;
import com.creative.gamesdk.common.utils_base.interfaces.CallBackListener;
import com.creative.gamesdk.common.utils_base.parse.project.Project;
import com.creative.gamesdk.common.utils_base.parse.project.ProjectManager;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;
import com.creative.gamesdk.listener.AccountCallBackListener;
import com.creative.gamesdk.listener.ExitCallBackListener;
import com.creative.gamesdk.listener.InitCallBackListener;
import com.creative.gamesdk.listener.PurchaseCallBackListener;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/2/13.
 */

public class SDKAPI {

    private final String TAG = getClass().getSimpleName();

    private volatile static SDKAPI INSTANCE;

    private SDKAPI() {
    }

    public static SDKAPI getInstance() {
        if (INSTANCE == null) {
            synchronized (SDKAPI.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SDKAPI();
                }
            }
        }
        return INSTANCE;
    }

    //获取实际项目对象

    private Project project = ProjectManager.getInstance().getProject("project"); //获取实际的项目对象

    /*********************************************  生命周期接口   *************************************************************/
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        project.onCreate(activity, savedInstanceState);
    }

    public void onStart(Activity activity) {
        project.onStart(activity);
    }

    public void onResume(Activity activity) {
        project.onResume(activity);
    }

    public void onPause(Activity activity) {
        project.onPause(activity);
    }

    public void onStop(Activity activity) {
        project.onStop(activity);
    }

    public void onRestart(Activity activity) {
        project.onRestart(activity);
    }

    public void onDestroy(Activity activity) {
        project.onDestroy(activity);
    }

    public void onNewIntent(Activity activity, Intent intent) {
        project.onNewIntent(activity, intent);
    }

    public void onActivityResult(Activity activity, int requestCode, int responseCode, Intent data) {
        project.onActivityResult(activity, requestCode, responseCode, data);
    }

    public void onRequestPermissionResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        project.onRequestPermissionResult(activity, requestCode, permissions, grantResults);
    }

    /*********************************************  SDK接口  *************************************************/

    /**
     * 初始化接口
     *
     * @param activity                游戏activity
     * @param gameInfoSetting         游戏参数对象
     * @param accountCallBackListener 账号回调监听
     * @param initCallBackListener    初始化回调监听
     */
    public void init(Activity activity, GameInfoSetting gameInfoSetting, AccountCallBackListener accountCallBackListener, final InitCallBackListener initCallBackListener) {

        //检测是否已设置配置游戏必须参数
        if (TextUtils.isEmpty(gameInfoSetting.gameid) || TextUtils.isEmpty(gameInfoSetting.gamekey)) {
            Toast.makeText(activity, "param one of gameInfoSetting is null,please checks first", Toast.LENGTH_SHORT).show();
        }

        //检测是否设置登陆监听
        if (accountCallBackListener == null) {
            Toast.makeText(activity, "accountCallBackListener is null,please set accountCallBackListener first", Toast.LENGTH_SHORT).show();
        }

        //设置账号回调监听
        mAccountCallBackListener = accountCallBackListener;
        project.setAccontCallBackListener(SDKaccountCallBackListener);

        project.init(activity, gameInfoSetting.gameid, gameInfoSetting.gamekey, new CallBackListener() {

            @Override
            public void onSuccess(Object o) {
                initCallBackListener.onSuccess();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                initCallBackListener.onFailure(errCode, errMsg);
            }
        });
    }

    /***************************************************************************************
     *
     * 设置Account回调接口,避免有些渠道登陆是从悬浮窗或者个人中心等入口接口回调回来,没法正确回调给cp
     *
     *****************************************************************************************/

    private AccountCallBackListener mAccountCallBackListener;

    private void accountCallBack(int type, int statusCode, String msg, AccountBean loginInfo) {

        AccountEventResultInfo accountResult = new AccountEventResultInfo();
        accountResult.setEventType(type);
        accountResult.setStatusCode(statusCode);
        accountResult.setMsg(msg);
        if (loginInfo != null) {

            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.setPlayerId(loginInfo.getUserID());
            playerInfo.setToken(loginInfo.getUserToken());
            accountResult.setPlayerInfo(playerInfo);

        } else {
            accountResult.setPlayerInfo(new PlayerInfo());
        }

        Gson gson = new Gson();
        String jsonStr = gson.toJson(accountResult);
        if (mAccountCallCallBackLister != null) {
            mAccountCallCallBackLister.onAccountEventCallBack(jsonStr);
        }
    }


    /**
     * 将project层的结果回调到这里
     */
    private CallBackListener SDKAccountCallBackListener = new CallBackListener<AccountCallBackBean>() {
        /**
         * 事件结果都回调到这里,包括失败的,取消的
         * @param callBackBean
         */
        @Override
        public void onSuccess(AccountCallBackBean callBackBean) {
            int event = callBackBean.getEvent();
            int code = callBackBean.getErrorCode();
            String msg = callBackBean.getMsg();
            AccountBean loginInfo = callBackBean.getAccountBean();
            switch (event) {
                case TypeConfig.LOGIN:
                    loginEventCallBack(code, loginInfo, msg);
                    break;
                case TypeConfig.SWITCHACCOUNT:
                    switchAccountCallback(code, loginInfo, msg);
                    break;
                case TypeConfig.LOGOUT:
                    logoutcallback(code, msg);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onFailure(int errCode, String errMsg) {

        }
    };

    private void logoutcallback(int code, String msg) {

        if (code == ErrCode.SUCCESS) {
            accountCallBack(AccountCallBackLister.LOGOUT_SUCCESS, ErrCode.SUCCESS, msg, null);

        } else if (code == ErrCode.CANCEL) {
            accountCallBack(AccountCallBackLister.LOGOUT_CANCEL, code, msg, null);

        } else {
            accountCallBack(AccountCallBackLister.LOGOUT_FAILURE, code, msg, null);
        }
    }

    private void switchAccountCallback(int code, AccountBean loginInfo, String msg) {

        if (code == ErrCode.SUCCESS) {
            accountCallBack(AccountCallBackLister.SWITCH_ACCOUNT_SUCCESS, ErrCode.SUCCESS, msg, loginInfo);

        } else if (code == ErrCode.CANCEL) {
            accountCallBack(AccountCallBackLister.SWITCH_ACCOUNT_CANCEL, code, msg, null);

        } else {
            accountCallBack(AccountCallBackLister.SWITCH_ACCOUNT_FAILURE, code, msg, null);
        }
    }

    private void loginEventCallBack(int code, AccountBean loginInfo, String msg) {
        if (code == ErrCode.SUCCESS) {
            accountCallBack(AccountCallBackListener.LOGIN_SUCCESS, ErrCode.SUCCESS, msg, loginInfo);
        } else if (code == ErrCode.CANCEL) {
            accountCallBack(AccountCallBackListener.LOGIN_CANCEL, code, msg, loginInfo);
        } else if (code == ErrCode.cannel_login_close) {
            accountCallBack(AccountCallBackListener.LOGIN_FAILURE, code, msg, loginInfo);
        } else {
            accountCallBack(AccountCallBackListener.LOGIN_FAILURE, code, msg, loginInfo);
        }
    }

    /**
     * 账号登陆
     *
     * @param activity 游戏activity
     */
    public void login(Activity activity) {
        project.login(activity, null);
    }

    /**
     * 切换账号
     *
     * @param activity 游戏activity
     */
    public void switchAccount(Activity activity) {
        project.switchAccount(activity);
    }

    /**
     * 账号注销
     *
     * @param activity 游戏activity
     */
    public void logout(Activity activity) {
        project.logout(activity);
    }

    /**
     * 购买
     *
     * @param activity                 游戏activity
     * @param params                   购买参数
     * @param purchaseCallBackListener 购买回调
     */
    public void pay(Activity activity, PayParams params, final PurchaseCallBackListener purchaseCallBackListener) {
        HashMap<String, Object> payParams = ObjectUtils.objectToMap(params);
        project.pay(activity, payParams, new CallBackListener<PurchaseResult>() {
            @Override
            public void onSuccess(PurchaseResult purchaseResult) {

                int type = purchaseResult.status;
                if (type == PurchaseResult.OrderState) { //创建订单成功回调

                    JsonObject object = (JsonObject) purchaseResult.message;
                    String orderID = object.get("orderId").getAsString();
                    purchaseCallBackListener.onOrderId(orderID);
                }

                if (type == PurchaseResult.PurchaseState) {//支付成功回调
                    purchaseCallBackListener.onSuccess();
                }

            }

            @Override
            public void onFailure(int errCode, String errMsg) {

                if (purchaseCallBackListener != null) {
                    if (code == ErrCode.CANCEL) {
                        purchaseCallBackListener.onCancel();

                    } else if (code == ErrCode.NO_PAY_RESULT) {

                        purchaseCallBackListener.onComplete();

                    } else {

                        purchaseCallBackListener.onFailure(code, msg);
                    }
                }
            }
        });
    }

    /**
     * 退出接口
     *
     * @param activity             游戏activity
     * @param exitCallBackListener 退出回调
     */
    public void exit(Activity activity, final ExitCallBackListener exitCallBackListener) {
        project.exit(activity, new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                //存在渠道退出框,并且点击退出成功
                exitCallBackListener.onExitDailogSuccess();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ErrCode.CANCEL) {
                    exitCallBackListener.onExitDialogCancel();
                } else if (code == ErrCode.NO_EXIT_DIALOG) {
                    exitCallBackListener.onNotExitDialog();
                } else {
                    exitCallBackListener.onNotExitDialog();
                }
            }
        });
    }

    /**
     * 显示浮窗
     *
     * @param activity
     */
    public void showFloatWindow(Activity activity) {
        project.showFloatView(activity);
    }

    /**
     * 隐藏浮窗
     */
    public void dismissFloatView(Activity activity) {
        project.dismissFloatView(activity);
    }

    /**
     * 上报数据信息
     *
     * @param activity       游戏activity
     * @param gameRoleParams 游戏参数实体
     */
    public void submitRoleInfo(Activity activity, GameRoleParams gameRoleParams) {

        //将实体对象装换为Map集合，方便后续自动扩展
        HashMap<String, Object> reportData = ObjectUtils.objectToMap(gameRoleParams);
        LogUtils.d(TAG, reportData.toString());
        project.reportData(activity, reportData);
    }

    public String getChannelId() {
        return project.getChannelID();
    }
}
