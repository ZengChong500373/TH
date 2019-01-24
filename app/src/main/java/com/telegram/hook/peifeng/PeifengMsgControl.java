package com.telegram.hook.peifeng;

import com.telegram.hook.bean.TelegramMsgBean;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.monitor.TMsgControll;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class PeifengMsgControl {
    public static void dealData(XC_MethodHook.MethodHookParam param,String info){
        List list = (List) ReflectUtil.getDeclaredField(param.args[0], "users");
        LogUtil.d("PeifengMsgControl dealData list.size="+list.size());
        if (list==null||list.size()==0){
            HookController.getInstance().sendStatus(ConstantStatus.sendMsgListFinish);
            return;
        }
        String msg=info.split("\\|")[0];
        List<TelegramMsgBean> msgUsers=new ArrayList<>();
        TelegramMsgBean bean=null;
        for (int i = 0; i < list.size(); i++){
            bean=new TelegramMsgBean();
            Class parent = XposedHelpers.findClass("org.telegram.tgnet.TLRPC.User", HookController.mClassLoader);
            String phoneNum = (String) ReflectUtil.getDeclaredField(parent, list.get(i), "phone");
            int id = (int) ReflectUtil.getDeclaredField(parent, list.get(i), "id");
            bean.setId(id);
            bean.setPhone(phoneNum);
            bean.setMsg(msg);
            if (i == list.size() - 1){
                msgUsers.add(bean);
                TMsgControll.getInstance().startSendMsgList(msgUsers);
            }else {
                msgUsers.add(bean);
            }
        }
    }
}
