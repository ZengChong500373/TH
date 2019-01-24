package com.telegram.hook.service;
import android.app.Service;
import android.content.Intent;


import android.os.IBinder;

import com.telegram.hook.utils.LaunUtils;
import com.telegram.hook.utils.MyLog;
import com.telegram.hook.utils.RxUtils;
import com.telegram.hook.utils.VPNNotificationManager;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PermanentService extends Service {

    VPNNotificationManager manager;
    Disposable disposable;
    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.write2File("PermanentService oncreat");
        if (manager==null){
            manager=new VPNNotificationManager(this);
            manager.showNotification();
            disposable=   RxUtils.currentTime().subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    manager.updateNotification(s);
                }
            });
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.write2File("PermanentService onDestroy");
        Intent localIntent = new Intent();
        localIntent.setClass(this, Service.class); // 销毁时重新启动Service
        this.startService(localIntent);
        MyLog.write2File("PermanentService onreStart");
        disposable.dispose();
        disposable=null;
    }


}