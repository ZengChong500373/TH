package com.telegram.hook.monitor;


import android.text.TextUtils;

import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.peifeng.PeifengMsgControl;
import com.telegram.hook.peifeng.PeifengSearchControl;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.MyLog;
import com.telegram.hook.utils.ReflectUtil;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class TContactControll {
    private static final TContactControll ourInstance = new TContactControll();
    public static TContactControll getInstance() {
        return ourInstance;
    }
    private TContactControll() {
        initDelegate();
    }

    private  Object delegate= null;
    private static int currentAction=-1;
    public static String currentInfo;
    public void startSearch(final String info,final int action) {
        LogUtil.d("TContactControll startSearch ", info);
        if (TStatusMonitor.ConnectionsManager != null) {
            LogUtil.d("TContactControll startSearch ConnectionsManager!=null");
            XposedHelpers.callMethod(TStatusMonitor.ConnectionsManager, "resumeNetworkMaybe");
        } else {
            LogUtil.d("TContactControll startSearch ConnectionsManager==null 5s later check");
            HookController.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    startSearch(info,action);
                }
            }, 5000);
            return;
        }
        try {
            currentAction=action;
            this.currentInfo=info;
            Class importContactsClass = XposedHelpers.findClass("org.telegram.tgnet.TLRPC.TL_contacts_importContacts", HookController.mClassLoader);
            Object req = importContactsClass.newInstance();
            List list =  creatData(info,action);
            ReflectUtil.setField(req, "contacts", list);
            LogUtil.d("TContactControll startSearch  data ok");
            XposedHelpers.callMethod(TStatusMonitor.ConnectionsManager, "sendRequest", req, delegate, 6);
            LogUtil.d("TContactControll startSearch  sendRequest ok");
        } catch (Exception e) {
            LogUtil.d(e.toString());
        }
    }

    public void initDelegate() {
        try {
            Class delegateClass = XposedHelpers.findClass("org.telegram.messenger.MessagesController$$Lambda$43", HookController.mClassLoader);
            Constructor constructor = delegateClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            delegate = constructor.newInstance();
            LogUtil.d("TContactControll  delegateClass set ok");
            Class cl2 = XposedHelpers.findClass("org.telegram.tgnet.TLObject", HookController.mClassLoader);
            Class cl3 = XposedHelpers.findClass("org.telegram.tgnet.TLRPC.TL_error", HookController.mClassLoader);
            XposedHelpers.findAndHookMethod(delegateClass, "run", cl2, cl3, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object error = param.args[1];
                    if (error == null) {
                        dealData(param);
                    }else {
                        String text= (String) ReflectUtil.getDeclaredField(error,"text");
                        MyLog.writeException("TContactControll addContacts error="+text);
                        LogUtil.d("TContactControll  ConstantStatus.addContactEorreorr");
                        if (!TextUtils.isEmpty(text)||text.contains("FLOOD_WAIT")){
                            String[] strs=text.split("");
                            if (strs.length==3){
                                long time=(Long.parseLong(strs[2])+10)*1000;
                                MyLog.writeException("TContactControll addContacts wait time="+time);
                                HookController.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LogUtil.d("TContactControll  because error  so call startSearch 20s later");
                                        startSearch(currentInfo,currentAction);
                                    }
                                },time);
                            }
                        }
                        HookController.getInstance().sendStatus(ConstantStatus.addContactEorreorr);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(e.toString());
        }
    }

    public void dealData(XC_MethodHook.MethodHookParam param){
        LogUtil.d("TContactControll dealData");
        if (param.args[0]==null){
            LogUtil.e("TContactControll dealData  param.args[0]==null");
            return;
        }

       if (currentAction==ConstantAction.addMsgUser){
           LogUtil.d("TContactControll currentAction== ConstantAction.addMsgUser");
           PeifengMsgControl.dealData(param,currentInfo);
       }else {
           LogUtil.d("TContactControll currentAction!= ConstantAction.addMsgUser");
           PeifengSearchControl.dealData(param,currentAction);
       }
    }
    public List creatData(String info,int action){
        LogUtil.d("TContactControll creatData");
        if (action==ConstantAction.checkPermission){
            LogUtil.d("TContactControll creatInputContact");
            return creatInputContact(info);
        }else if (action==ConstantAction.searchNums){
            LogUtil.d("TContactControll creatInputContacts ");
            return creatInputContacts(info);
        }else {
            LogUtil.d("TContactControll creatData last if");
            LogUtil.d("info="+info);
            String contactInfo=null;
            if (info.contains("|")){
                contactInfo=  info.split("\\|")[1];
            }else {
                contactInfo=info;
            }
            LogUtil.d("TContactControll creatData contactInfo="+contactInfo);
            return creatMsgContacts(contactInfo);
        }
    }
    public List creatInputContact(String info){
       try {
           if (TextUtils.isEmpty(info)) {
               return null;
           }
           ArrayList list = new ArrayList();
           int count = 1;
           Class inputPhoneContactClass = XposedHelpers.findClass(" org.telegram.tgnet.TLRPC.TL_inputPhoneContact", HookController.mClassLoader);
               Object intput = inputPhoneContactClass.newInstance();
               ReflectUtil.setField(intput, "first_name", info);
               ReflectUtil.setField(intput, "last_name", "");
               ReflectUtil.setField(intput, "phone", info);
               ReflectUtil.setField(intput, "client_id", count);
               list.add(intput);
               count++;
           return list;
       }catch (Exception e){
           LogUtil.e(e.toString());
       }
       return null;
    }
    public List creatInputContacts(String info){
        LogUtil.d("TContactControll creatInputContacts info="+info);
        try {
            if (TextUtils.isEmpty(info)) {
                return null;
            }
            ArrayList list = new ArrayList();
            int count = 1;
            Long startNum=Long.parseLong(info.split(",")[0]);
            Long endNum=Long.parseLong(info.split(",")[1]);
            Class inputPhoneContactClass = XposedHelpers.findClass(" org.telegram.tgnet.TLRPC.TL_inputPhoneContact", HookController.mClassLoader);
            for (Long i = startNum; i < endNum; i++){
                Object intput = inputPhoneContactClass.newInstance();
                ReflectUtil.setField(intput, "first_name", "86"+i + "");
                ReflectUtil.setField(intput, "last_name", "");
                ReflectUtil.setField(intput, "phone", "86"+i + "");
                ReflectUtil.setField(intput, "client_id", count);
                list.add(intput);
                count++;
            }
            return list;
        }catch (Exception e){
            LogUtil.e(e.toString());
        }
        return null;
    }
    public List creatMsgContacts(String info){
        LogUtil.d("TContactControll creatMsgContacts info="+info);
        try {
            if (TextUtils.isEmpty(info)) {
                return null;
            }
            ArrayList list = new ArrayList();
            int count = 1;
            String [] nums=info.split(",");
            Class inputPhoneContactClass = XposedHelpers.findClass(" org.telegram.tgnet.TLRPC.TL_inputPhoneContact", HookController.mClassLoader);
            for (int i = 0; i < nums.length; i++){
                Object intput = inputPhoneContactClass.newInstance();
                ReflectUtil.setField(intput, "first_name", "86"+nums[i] + "");
                ReflectUtil.setField(intput, "last_name", "");
                ReflectUtil.setField(intput, "phone", "86"+nums[i] + "");
                ReflectUtil.setField(intput, "client_id", count);
                list.add(intput);
                count++;
            }
            return list;
        }catch (Exception e){
            LogUtil.e(e.toString());
        }
        return null;
    }
    public void autoLoginOut(){
        LogUtil.d("TContactControll autoLoginOut ");
        if (TStatusMonitor.ConnectionsManager != null) {
            LogUtil.d("TContactControll autoLoginOut ConnectionsManager!=null");
            XposedHelpers.callMethod(TStatusMonitor.ConnectionsManager, "resumeNetworkMaybe");
        } else {
            LogUtil.d("TContactControll autoLoginOut ConnectionsManager==null 5s later check");
            HookController.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    autoLoginOut();
                }
            }, 5000);
            return;
        }
        XposedHelpers.callMethod(TStatusMonitor.MessagesController, "performLogout", 1);
    }

}
