package com.telegram.hook.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.telegram.hook.App;

import java.util.Iterator;
import java.util.List;

public class ProcessUtils {
    public static void logProcess(String tag) {
        int pid = android.os.Process.myPid();
        LogUtil.d(tag,"pid="+pid);

    }

    /**
     * 获取当前进程的名字，一般就是当前app的包名

     * @return 返回进程的名字
     */
    public static String getAppName() {
        int pid = android.os.Process.myPid(); // Returns the identifier of this process
        ActivityManager activityManager = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    // 根据进程的信息获取当前进程的名字
                    return info.processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有匹配的项，返回为null
        return null;
    }
}
