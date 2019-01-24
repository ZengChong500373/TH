package com.telegram.hook.ui;


import android.content.SharedPreferences;
import android.widget.EditText;

import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;
import com.telegram.hook.utils.SpUtils;

import de.robv.android.xposed.XposedHelpers;
/**登录界面设置手机号码*/
public class PhoneView {
    public  static void setNumAndNext(Object thisObject,String num){
        long time=System.currentTimeMillis()-getLastLoginTime();
        if (time<60*1000*2){
            LogUtil.d("PhoneView setNumAndNext 2 min login many times");
LoginActivity.killSelf();
            return;
        }
        LogUtil.d("PhoneView setNumAndNext","thisOject="+thisObject,"userPhoneNum="+num);
        EditText codeField= (EditText) ReflectUtil.getDeclaredField(thisObject, "codeField");
        codeField.setText("86");
        EditText phoneField= (EditText) ReflectUtil.getDeclaredField(thisObject, "phoneField");
        phoneField.setText(num);
        XposedHelpers.callMethod(thisObject,"onNextPressed");
        setLoginTime();
    }
public  static void setLoginTime(){
    SharedPreferences sp= HookController.mContext.getSharedPreferences("config",0);
    sp.edit().putLong("loginTime",System.currentTimeMillis());
}
public  static long getLastLoginTime(){
    SharedPreferences sp= HookController.mContext.getSharedPreferences("config",0);
    return sp.getLong("loginTime",0);
}
}
