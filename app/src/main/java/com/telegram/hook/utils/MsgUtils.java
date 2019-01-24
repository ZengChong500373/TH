package com.telegram.hook.utils;

import android.util.LongSparseArray;


import com.telegram.hook.bean.MsgPhonesBean;
import com.telegram.hook.bean.TelegramMsgBean;

import java.util.List;

public class MsgUtils {
    public static String bean2String(MsgPhonesBean msgPhonesBean){
        StringBuffer buffer = new StringBuffer();
        buffer.append(msgPhonesBean.getMsg()+"|");
        List<String> list=msgPhonesBean.getList();
        for (int i=0;i<list.size();i++){
            if (i == list.size() - 1){
                buffer.append(list.get(i));
            }else {
                buffer.append(list.get(i) + ",");
            }
        }
        return buffer.toString();
    }
    public  static LongSparseArray<TelegramMsgBean> sparseArray=new LongSparseArray<>();
    public static void bean2SparseArray(MsgPhonesBean bean){
        List<String> list=bean.getList();
        for (int i=0;i<list.size();i++){
            Long key=Long.parseLong(list.get(i));
            sparseArray.put(key,new TelegramMsgBean(list.get(i)));
        }
    }
    public static void setMsgStatus(String info){
        String strKey=info.split(",")[1];
        Long key=Long.parseLong(strKey);
        TelegramMsgBean bean=sparseArray.get(key);
        LogUtil.d("MsgUtils setMsgStatus phone="+bean.getPhone());
        if (bean==null){
            LogUtil.d("MsgUtils setMsgStatus bean ==null");
            return;
        }
        if (info.contains("sendSuccess")){
            bean.setStatus("FINISH");
            LogUtil.d("MsgUtils setMsgStatus sendSuccess FINISH");
            return;
        }
        if (info.contains("sendError")){
            LogUtil.d("MsgUtils setMsgStatus sendError FAIL");
            bean.setStatus("FAIL");
            return;
        }
        sparseArray.put(key,bean);
    }
}
