package com.telegram.hook.peifeng;

import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.monitor.TContactControll;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.MyLog;
import com.telegram.hook.utils.ReflectUtil;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class PeifengSearchControl {
    public  static void dealData(XC_MethodHook.MethodHookParam param,int currentAction){
        List list = (List) ReflectUtil.getDeclaredField(param.args[0], "users");
        StringBuffer buffer = new StringBuffer();
        if (list==null||list.size()==0){
            if (currentAction==ConstantAction.checkPermission){
                LogUtil.d("TContactControll  dealDataConstantStatus.statusIsNormal  list ==null");
                HookController.getInstance().sendStatus(ConstantStatus.statusIsNormal,"false,"+TContactControll.currentInfo);
            }else {
                LogUtil.d("TContactControll dealData ConstantStatus.checkFinsh  list ==null");
                HookController.getInstance().sendStatus(ConstantStatus.checkFinsh,"");
            }
            return;
        }
        LogUtil.d("TContactControll dealData  list size="+list.size());
        for (int i = 0; i < list.size(); i++) {
            Class parent = XposedHelpers.findClass("org.telegram.tgnet.TLRPC.User", HookController.mClassLoader);
            String phoneNum = (String) ReflectUtil.getDeclaredField(parent, list.get(i), "phone");
            LogUtil.d("TContactControll dealData  user phone position=" + i+" phoneNum="+phoneNum);
            if (i == list.size() - 1) {
                buffer.append(phoneNum);
                if (currentAction==ConstantAction.checkPermission){
                    LogUtil.d("TContactControll dealData  ConstantStatus.statusIsNormal true phoneNum="+phoneNum);
                  HookController.getInstance().sendStatus(ConstantStatus.statusIsNormal,"true"+","+buffer.toString());
                }else {
                    if (i==0){
                        MyLog.writeSwitchAccount("TContactControll dealData checkFinsh have data list size="+list.size());
                    }
                    LogUtil.d("TContactControll dealData  ConstantStatus.checkFinsh  phoneNums="+buffer.toString());
                    MyLog.writeSwitchAccount("ConstantStatus.checkFinsh search users="+buffer.toString());
                    HookController.getInstance().sendStatus(ConstantStatus.checkFinsh,buffer.toString());
                }
            } else {
                buffer.append(phoneNum + ",");
            }
        }
    }
}
