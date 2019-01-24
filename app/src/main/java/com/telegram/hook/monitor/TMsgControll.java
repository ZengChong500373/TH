package com.telegram.hook.monitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LongSparseArray;

import com.telegram.hook.bean.TelegramMsgBean;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.config.PeiFengContant;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;


import java.util.List;

import de.robv.android.xposed.XposedHelpers;

public class TMsgControll {
    private static final TMsgControll ourInstance = new TMsgControll();

    public static TMsgControll getInstance() {
        return ourInstance;
    }

    private TMsgControll() {

    }

    public void sendMsg(final CharSequence text, final long userid) {
        LogUtil.d("TMsgControll sendMsg text=" + text.toString(), "userid=" + userid);
        if (TStatusMonitor.SendMessagesHelper == null || TStatusMonitor.MessagesController == null) {
            if (TStatusMonitor.SendMessagesHelper == null) {
                Class cl4 = XposedHelpers.findClass("org.telegram.messenger.SendMessagesHelper", HookController.mClassLoader);
                XposedHelpers.callStaticMethod(cl4, "getInstance", 0);
            }
            LogUtil.d("TMsgControll SendMessagesHelper ==null 5s later check");
            HookController.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    sendMsg(text, userid);
                }
            }, 5000);
            return;
        }
        Object o = XposedHelpers.callMethod(TStatusMonitor.MessagesController, "getUser", (int) userid);
        if (o != null) {
            Class parent = XposedHelpers.findClass("org.telegram.tgnet.TLRPC.User", HookController.mClassLoader);
            String name = (String) ReflectUtil.getDeclaredField(parent, o, "first_name");
            LogUtil.d("TMsgControll sendMsg name=" + name);
            XposedHelpers.callMethod(TStatusMonitor.SendMessagesHelper, "sendMessage", text, userid, null, null, true, null, null, null);
        }
    }

    int position = 0;
    List<TelegramMsgBean> currentList;

    public void startSendMsgList(List<TelegramMsgBean> list) {
        position = 0;
        if (list == null || list.size() == 0) {
            return;
        }
        currentList = list;
        creatAndSendHandlerMsg(0);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Boolean b=Thread.currentThread() == Looper.getMainLooper().getThread();
            LogUtil.d("Thread.currentThread() ?= Looper.getMainLooper().getThread()"+b);
            Bundle bundle = msg.getData();
            int type = bundle.getInt("type");
            int position = bundle.getInt("position");
            TelegramMsgBean bean = currentList.get(position);
            LogUtil.d("Handler handleMessage ","type="+type,"position="+position);
            if (type == 0) {
                sendMsg(bean.getMsg(), bean.getId());
                // 5s 后检查 发送是否成功
                creatAndSendHandlerMsg(1);
            }
            if (type == 1) {
                checkMsgStatus(bean.getId());
            }
        }
    };

    @SuppressLint("NewApi")
    public int sendType(int id) {
        LongSparseArray longSparseArray = (LongSparseArray) ReflectUtil.getDeclaredField(TStatusMonitor.MessagesController, "dialogMessage");
        LogUtil.d("longSparseArray =" + longSparseArray.size());
        Object message = longSparseArray.get(id);
        Boolean isSendError = (Boolean) XposedHelpers.callMethod(message, "isSendError");
        if (isSendError) {
            return PeiFengContant.SEND_ERROR;
        }
        Boolean isSending = (Boolean) XposedHelpers.callMethod(message, "isSending");
        if (isSending) {
            return PeiFengContant.SEND_SENDING;
        }
        Boolean isSent = (Boolean) XposedHelpers.callMethod(message, "isSent");
        if (isSent) {
            return PeiFengContant.SEND_SUCCESS;
        }

        return PeiFengContant.SEND_DEFAULT;
    }


    public void checkMsgStatus(final int id) {
        LogUtil.d("TMsgControll checkMsgStatus="+id,"position="+position);
        int sendStatus=sendType(id);
        if (sendStatus==PeiFengContant.SEND_SUCCESS){
            HookController.getInstance().sendStatus(ConstantStatus.sendMsgStatus,"sendSuccess,"+currentList.get(position).getPhone());
            if (position<currentList.size()-1){
                position=position+1;
                LogUtil.d("TMsgControll checkMsgStatus position+1","position="+position);
                creatAndSendHandlerMsg(0);
            }else {
                position=0;
                currentList=null;
                LogUtil.d("TMsgControll checkMsgStatus finish","position="+position);
                HookController.getInstance().sendStatus(ConstantStatus.sendMsgListFinish);
            }
            return;
        }
        if (sendStatus==PeiFengContant.SEND_SENDING||sendStatus==PeiFengContant.SEND_DEFAULT){
            creatAndSendHandlerMsg(1);
            return;
        }
        if (sendStatus==PeiFengContant.SEND_ERROR){
            HookController.getInstance().sendStatus(ConstantStatus.sendMsgStatus,"sendError,"+currentList.get(position).getPhone());
            creatAndSendHandlerMsg(0);
            return;
        }
    }

    public void creatAndSendHandlerMsg(int type) {
        LogUtil.d("TMsgControll creatAndSendHandlerMsg="+type);
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        LogUtil.d("creatAndSendHandlerMsg type="+type,"position="+position);
        bundle.putInt("type", type);
        message.setData(bundle);
        handler.removeMessages(0);
        handler.removeMessages(1);
        if (type == 0) {
            handler.sendMessageDelayed(message,15000);
        } else {
            handler.sendMessageDelayed(message, 20000);
        }
    }
}
