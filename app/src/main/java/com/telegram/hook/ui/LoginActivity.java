package com.telegram.hook.ui;


import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.config.ConstantTelegramView;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.TelegramLoginStatusUtils;


import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

/**
 * telegram 登录界面
 * 本身是一个自定义fragment
 * 用户所看到的登录都在其里面完成
 * 含有 9 个子界面
 * 字界面继承 自定义 SlideView
 */
public class LoginActivity {
    private static Object currentView;
    private static Object loginObject;

    public static void init() {
        LogUtil.d("LoginActivity init");
        hookLoginActvity();
        IntroActivity.go2LoginView();//如果有欢迎界面 则关闭
        ListenOnShow();
        ListenAlertDialog();
    }

    private static void ListenAlertDialog() {
        Class cl = XposedHelpers.findClass("org.telegram.ui.LoginActivity", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "needShowAlert", String.class, String.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                for (int i = 0; i < param.args.length; i++) {
                    TelegramLoginStatusUtils.isTooManyAttempts(param.args[i] + "");
                }
                return null;
            }
        });

        XposedHelpers.findAndHookMethod(cl, "needShowInvalidAlert", String.class, boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                String str= (String) param.args[0];
                boolean type = (boolean) param.args[1];
                TelegramLoginStatusUtils.isBannedOrinvalid(type,str);
                return null;
            }
        });
    }

    public static void hookLoginActvity() {
        LogUtil.d("hookNextPage");
        Class cl = XposedHelpers.findClass("org.telegram.ui.LoginActivity", HookController.mClassLoader);
        XposedHelpers.findAndHookConstructor(cl, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                loginObject = param.thisObject;
                LogUtil.d("hookNextPage loginObject=" + loginObject);
            }
        });

    }

    /**
     * 监听登录界面的子fragment
     * 当 子界面显示的时候，做出相应逻辑
     */
    public static void ListenOnShow() {
        LogUtil.d("LoginActivity ListenOnShow");
        Class cl = XposedHelpers.findClass("org.telegram.ui.Components.SlideView", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "onShow", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                currentView = param.thisObject;
                LogUtil.d("LoginActivity ListenOnShow class=" + currentView.getClass().getName());
                if (currentView.getClass().getName().equals(ConstantTelegramView.phoneView)) {
                    HookController.getInstance().sendStatus(ConstantStatus.enterPhoneView);
                    return;
                }
                if (currentView.getClass().getName().equals(ConstantTelegramView.smsView)) {
                    if (SmsView.isClose(currentView)) {
                        returnPhonView(10000);
                    }
                    return;
                }
                if (currentView.getClass().getName().equals(ConstantTelegramView.twoStepCodeView)) {
                    HookController.getInstance().sendStatus(ConstantStatus.twoStepCode,currentNum);
                    returnPhonView(10000);
                    return;
                }
                if (currentView.getClass().getName().equals(ConstantTelegramView.registerView)) {
                    RegisterView.setName(currentView);
                    return;
                }
            }
        });
    }

    /**
     * 输入手机号码并登录
     */
    private  static String currentNum="";
    public static void setNum(final String num) {
        if (checkCurrentView()){
            currentNum=num;
            PhoneView.setNumAndNext(currentView, num);
        }

    }

    /**
     * 输入手机号码的验证码
     */
    public static void setCode(final String code) {
        if (checkCurrentView()){
            SmsView.setCode(currentView, code);
        }
    }

    public static void setFristName(){
        if (checkCurrentView()){
            RegisterView.setName(currentView);
        }
    }
    /**
     * 关闭摄入验证码界面
     */
    public static void returnPhonView(int time) {
        if (!checkCurrentView()){
            return;
        }
        HookController.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("LoginActivity returnPhonView");
                XposedHelpers.callMethod(currentView, "onNextPressed");
                XposedHelpers.callMethod(loginObject, "setPage", 0, true, null, true);
            }
        }, time);
    }
    public static boolean checkCurrentView(){
         if (currentView==null){
             killSelf();
             return false;
         }
         return true;
    }
    public static void killSelf(){
        LogUtil.d("LoginActivity go 2 killSelf");
        HookController.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                HookController.getInstance().sendStatus(ConstantStatus.killSelf);
            }
        },500);
        HookController.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        },1000);
    }
}
