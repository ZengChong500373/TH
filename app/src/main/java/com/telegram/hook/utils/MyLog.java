package com.telegram.hook.utils;


import android.text.TextUtils;
import android.util.Log;

import com.telegram.hook.App;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/2.
 */

public class MyLog {
    public static String path = "/storage/emulated/0/PeiFeng";
    public static String name = path + File.separator + "peifeng.txt";

    public static String filePath="";
    private static void writeBase(String str,String path) {
        StackTraceElement ste[] = Thread.currentThread().getStackTrace();
        StackTraceElement heihei=ste[3];
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("["+heihei.getClassName()+"]").append("["+heihei.getMethodName()+" "+ heihei.getLineNumber()+"]");
        Log.e("writeLocal",str);
        BufferedWriter out = null;
        try {
            init();
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path, true)));
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);
            out.write(dateString + " " + str + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static void write2File(String str) {
     writeBase(str,getFilePath());
    }

    public static void writeException(String str) {
     writeBase(str,path + File.separator + "exception.text");
    }
    public static void writeSwitchAccount(String str) {
      writeBase(str,path + File.separator + "account.text");
    }
    public  static String getFilePath(){
        if (!TextUtils.isEmpty(filePath)){
            return filePath;
        }
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        filePath=path + File.separator+dateString+"peifeng.txt";
        return filePath;
    }


    private static void init() {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


}
