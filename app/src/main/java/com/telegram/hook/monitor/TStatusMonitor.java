package com.telegram.hook.monitor;

import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.DataCleanManager;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.MyLog;
import com.telegram.hook.utils.ReflectUtil;


import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class TStatusMonitor {
    private static final TStatusMonitor ourInstance = new TStatusMonitor();

    public static TStatusMonitor getInstance() {
        return ourInstance;
    }

    private TStatusMonitor() {
    }


    public static Object ConnectionsManager = null;
    public static Object MessagesController = null;
    public static Object SendMessagesHelper = null;
    public static Object UserConfig = null;

    public void init() {
        initObj();

        monitorIsLoginStatus();
        monitorLoginOutType();
        monitorResumeNetworkMaybe();

    }


    public void initObj() {

        Class cl = XposedHelpers.findClass("org.telegram.tgnet.ConnectionsManager", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "getInstance", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                int type = (int) param.args[0];
                if (type == 0 && ConnectionsManager == null) {
                    ConnectionsManager = param.getResult();
                    LogUtil.d("TContactControll set ConnectionsManager" + ConnectionsManager);
                }
            }
        });

        Class cl2 = XposedHelpers.findClass("org.telegram.messenger.MessagesController", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl2, "getInstance", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                int type = (int) param.args[0];
                if (type == 0 && MessagesController == null) {
                    MessagesController = param.getResult();
                    LogUtil.d("TContactControll set MessagesController" + MessagesController);
                }
            }
        });
        Class cl3 = XposedHelpers.findClass("org.telegram.messenger.SendMessagesHelper", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl3, "getInstance", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                int type = (int) param.args[0];
                if (type == 0 && SendMessagesHelper == null) {
                    SendMessagesHelper = param.getResult();
                    LogUtil.d("TContactControll set SendMessagesHelper" + SendMessagesHelper);
                }
            }
        });
        Class cl4 = XposedHelpers.findClass("org.telegram.messenger.UserConfig", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl4, "getInstance", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                int type = (int) param.args[0];
                if (type == 0 && UserConfig == null) {
                    UserConfig = param.getResult();
                    LogUtil.d("TContactControll set UserConfig" + UserConfig);
                }
            }
        });
    }

    /**
     * 监听是否是登录状态
     */
    public static boolean isLoginStatus = false;

    public void monitorIsLoginStatus() {
        Class cl = XposedHelpers.findClass("org.telegram.messenger.UserConfig", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "isClientActivated", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (!isLoginStatus) {
                    isLoginStatus = (Boolean) param.getResult();
                    if (isLoginStatus) {
                        sendLoginStatus();
                    }
                }

            }
        });
    }

    public void sendLoginStatus() {
        LogUtil.d("isLoginStatus=" + isLoginStatus);
        if (TStatusMonitor.UserConfig == null) {
            Class cl4 = XposedHelpers.findClass("org.telegram.messenger.UserConfig", HookController.mClassLoader);
            XposedHelpers.callStaticMethod(cl4, "getInstance", 0);
            HookController.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    sendLoginStatus();
                }
            }, 5000);
            return;
        }
        Object u = XposedHelpers.callMethod(UserConfig, "getCurrentUser");
        Class parent = XposedHelpers.findClass("org.telegram.tgnet.TLRPC.User", HookController.mClassLoader);
        String phoneNum = (String) ReflectUtil.getDeclaredField(parent, u, "phone");
        LogUtil.d("TStatusMonitor phoneNum=" + phoneNum);
        HookController.getInstance().sendStatus(ConstantStatus.loginStatus, phoneNum);
    }

    /**
     * 监听退出的状态
     */
    public void monitorLoginOutType() {
        Class cl2 = XposedHelpers.findClass("org.telegram.messenger.MessagesController", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl2, "performLogout", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                isLoginStatus = false;
                final int loginoutType = (int) param.args[0];
                LogUtil.d("loginoutType=" + loginoutType);
                MyLog.writeException("telegram loginoutType=" + loginoutType);
                DataCleanManager.deleData();

                HookController.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loginoutType == 0) {
                            LogUtil.d("TStatusMonitor android.os.Process.numBanned ");
                            HookController.getInstance().sendStatus(ConstantStatus.numBanned);
                        } else {
                            LogUtil.d("TStatusMonitor test3Times ");
                            HookController.getInstance().sendStatus(ConstantStatus.test3Times);
                        }
                    }
                }, 500);
                HookController.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d("TStatusMonitor android.os.Process.myPid ");
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }, 1000);

            }
        });
    }

    public void monitorResumeNetworkMaybe() {
        LogUtil.d("monitorResumeNetworkMaybe  contactNext");
        Class cl = XposedHelpers.findClass("org.telegram.tgnet.ConnectionsManager", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "resumeNetworkMaybe", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                LogUtil.d("monitorResumeNetworkMaybe  afterHookedMethod");
            }
        });
    }

}
