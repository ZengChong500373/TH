package com.telegram.hook.ui;

import android.widget.EditText;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;


/**
 * 设置验证码界面
 */
public class SmsView {
    public static void setCode(Object thisObject, String code) {
        LogUtil.d("SmsView  setCode code="+code );
        EditText[] codeField = (EditText[]) ReflectUtil.getDeclaredField(thisObject, "codeField");
        for (int i=0;i<codeField.length;i++){
          String pointName = String.valueOf(code.charAt(i));
            LogUtil.d("SmsView  pointName i="+i,pointName );
            codeField[i].setText(pointName);
        }
    }

    /**
     * 判断是否该关闭输入验证码界面
     * 如果不关闭 则上报telegram 状态给自己应用
     * 自己应用获取验证码 在传回来
     *
     * 否则关闭输入验证码界面
     */
    public static boolean isClose(final Object thisObject) {
        int currentType = (int) ReflectUtil.getDeclaredField(thisObject, "currentType");
        LogUtil.d("currentType=" + currentType);
        if (currentType!=1){
            HookController.getInstance().sendStatus(ConstantStatus.enterSmsView);
            return false;
        }else {
            return true;
        }
    }


}
