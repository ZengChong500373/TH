package com.telegram.hook.ui;



import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;

import de.robv.android.xposed.XposedHelpers;

/**
 * 两次验证码界面
 * */
public class TwoStepCodeView {

    public static void close(Object thisObject){
        LogUtil.d("TwoStepCodeView close");
        Object actionBar= ReflectUtil.getDeclaredField(thisObject, "actionBar");
        XposedHelpers.callMethod(actionBar,"onItemClick",6);
    }
}
