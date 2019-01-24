package com.telegram.hook;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.telegram.hook.service.PermanentService;
import com.telegram.hook.utils.CrashHandler;

import okhttp3.OkHttpClient;

public class App extends Application {
    private  static Context mContex;
    private final static OkHttpClient client = new OkHttpClient();
    public static volatile Handler appHandler;
    @Override
    public void onCreate() {
        super.onCreate();
        mContex=getApplicationContext();
        appHandler=new Handler(mContex.getMainLooper());
        startService();
        CrashHandler.getInstance().init(mContex);
    }
    public static Context getContext(){
        return mContex;
    }

    public static OkHttpClient getClient(){
        return client;
    }
    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            appHandler.post(runnable);
        } else {
            appHandler.postDelayed(runnable, delay);
        }
    }
    /**
     * 判断是否在当前主线程
     * @return
     */
    public static boolean isOnMainThread(){
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public void startService() {
        Intent intent = new Intent(this, PermanentService.class);
        startService(intent);
    }
}
