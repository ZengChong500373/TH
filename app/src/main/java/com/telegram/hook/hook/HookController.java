package com.telegram.hook.hook;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;


import com.telegram.hook.PeifengController;
import com.telegram.hook.TelegramListener;
import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.config.ConstantStatus;

import com.telegram.hook.monitor.TContactControll;
import com.telegram.hook.monitor.TStatusMonitor;
import com.telegram.hook.ui.LoginActivity;
import com.telegram.hook.utils.CrashHandler;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookController {
    private static final String TAG = "HookController";
    public static HookController instance;
    public static ClassLoader mClassLoader;
    public static Context mContext;
    private TelegramListener mListener;
    public static Handler handler = new Handler(Looper.getMainLooper());
    public static HookController getInstance() {
        if (instance == null)
            synchronized (HookController.class) {
                if (instance == null)
                    instance = new HookController();
            }
        return instance;
    }

    public void init(ClassLoader classLoader) {
        boolean isHook = this.mClassLoader == null && classLoader != null;
        this.mClassLoader = classLoader;
        LogUtil.d(TAG, "isHook = " + isHook);
        if (isHook) {
            hookApp();
        }
    }
    /**
     *hook application 的context 启动自己的services
     * 然后通过aidl 进行 自己应用和hook 的目标应用进行通信
     * */
    private void hookApp() {
        Class clazz = XposedHelpers.findClass("org.telegram.messenger.ApplicationLoader", mClassLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                mContext = (Context) ReflectUtil.getDeclaredField(param.thisObject, "applicationContext");
                connService();
            }
        });

    }

    private void connService() {
        LogUtil.d(TAG, "connService ");
        Intent intent = new Intent();
        intent.setClassName("com.telegram.hook", "com.telegram.hook.service.BridgeService");
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TelegramListener listener = TelegramListener.Stub.asInterface(service);
            if (listener != null) {
                try {
                    mListener = listener;
                    mListener.registerController(peifengController);
                    sendStatus(ConstantStatus.serviceConnection);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    LogUtil.d("onServiceConnected RemoteException registerController");
                }
            }
        }

        PeifengController peifengController = new PeifengController.Stub() {
            @Override
            public void currentAction(final int action,final String info) throws RemoteException {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                         responseAction(action,info);
                    }
                },0);
            }
        };

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    /**
     *  自己应用通过aidl 操作telegram
     * */
    public void responseAction(int action,String info) {
        if (action == ConstantAction.init) {
            LogUtil.d("HookController responseAction startLogin");
            LoginActivity.init();
            TStatusMonitor.getInstance().init();
            return;
        }
        if (action == ConstantAction.loginNum) {
            LogUtil.d("HookController responseAction loginNum="+info,"Thread="+Thread.currentThread().getName());
            LoginActivity.setNum(info);
            return;
        }
        if (action == ConstantAction.loginCode) {
            LogUtil.d("HookController responseAction loginCode="+info);
            LoginActivity.setCode(info);
            return;
        }
        if (action==ConstantAction.returnPhonView){
            LogUtil.d("HookController responseAction colseSmsView");
            LoginActivity.returnPhonView(5000);
        }
        if (action==ConstantAction.checkPermission||action==ConstantAction.searchNums||action==ConstantAction.addMsgUser){
            LogUtil.d("HookController responseAction add User ");
            TContactControll.getInstance().startSearch(info,action);
        }
        if (action==ConstantAction.autoLoginOut){
            TContactControll.getInstance().autoLoginOut();
        }
    }
    /**
     *通过aidl 把当前telegram 进程状态上报给自己应用
     * */
    public void sendStatus(int status,String info) {
        if (mListener == null) {
            LogUtil.d("sendStatus currentStatus ==null ");
            return;
        }
        try {
            mListener.currentStatus(status,info);
        } catch (RemoteException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "RemoteException ");
        }
    }
    public void sendStatus(int status) {
        if (mListener == null) {
            LogUtil.d("sendStatus currentStatus ==null ");
            return;
        }
        try {
            mListener.currentStatus(status,"");
        } catch (RemoteException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "RemoteException ");
        }
    }


    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            handler.post(runnable);
        } else {
            handler.postDelayed(runnable, delay);
        }
    }
}
