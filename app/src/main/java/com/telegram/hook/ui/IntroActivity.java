package com.telegram.hook.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * telegram 的splash 界面
 * */
public class IntroActivity {
    public static void go2LoginView() {
        if (HookController.mClassLoader == null) {
            LogUtil.d("IntroActivity", "【ClassLoader==null】");
            return;
        }
        LogUtil.d("IntroActivity", "【closeSplashView】");
        Class clazz = XposedHelpers.findClass("org.telegram.ui.IntroActivity", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.thisObject == null) {
                    LogUtil.d("IntroActivity", "【thisObject == null 】");
//                    HookController.getInstance().sendStatus(ConstantStatus.noSplashView);
                    return;
                }
                Boolean startPressed = (Boolean) ReflectUtil.getDeclaredField(param.thisObject, "startPressed");
                startPressed=true;
                Boolean destroyed = (Boolean) ReflectUtil.getDeclaredField(param.thisObject, "destroyed");
                destroyed=true;
                Activity mActivity = (Activity) param.thisObject;
                Intent intent=new  Intent();
                intent.setClassName(mActivity.getApplicationContext(),"org.telegram.ui.LaunchActivity");
                intent.putExtra("fromIntro", true);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
    }
}
