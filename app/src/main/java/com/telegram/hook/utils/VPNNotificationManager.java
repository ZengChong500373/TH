package com.telegram.hook.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.telegram.hook.App;
import com.telegram.hook.MainActivity;
import com.telegram.hook.R;


public class VPNNotificationManager {

    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_PENDING_INTENT_CONTENT = 0;
    private static final int NOTIFICATION_PENDING_INTENT_STOP_V2RAY = 1;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Service mService;

    public VPNNotificationManager(Service service) {
        this.mService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification() {
        Context context = App.getContext();
        Intent startMainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_PENDING_INTENT_CONTENT, startMainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        /*Intent stopV2RayIntent = new Intent(AppConfig.BROADCAST_ACTION_SERVICE);
        stopV2RayIntent.setPackage(AndroidUtils.getPackageName(BaseApp.getInstance()));
        stopV2RayIntent.putExtra("key", AppConfig.MSG_STATE_STOP);
        PendingIntent stopV2RayPendingIntent = PendingIntent.getBroadcast(context,
                NOTIFICATION_PENDING_INTENT_STOP_V2RAY, stopV2RayIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);*/
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
        }

        mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("培风Server")
                .setContentText("speeding")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(contentPendingIntent)
                //取消声音和震动 参考：https://blog.csdn.net/yp021/article/details/52087303
                .setDefaults(Notification.DEFAULT_SOUND)
//                .setVibrate(null);
                .setVibrate(new long[]{0L});
//        mBuilder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);  //取消震动,铃声其他都不好使
        mService.startForeground(NOTIFICATION_ID, mBuilder.build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        String channelId = "M_CH_ID";
        String channelName = "Background Service";
        //中等级别（没有通知声音，但是通知栏有通知）
        NotificationChannel chan = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_LOW);
//        NotificationChannel chan = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.DKGRAY);
        chan.setSound(null,null); //<---- ignore sound
        //必须卸载app才能生效（参考：http://www.jsc0.com/post/153.html）
        chan.enableLights(false);//关闭指示灯，如果设备有的话。
        chan.enableVibration(false);//关闭震动
//        chan.enableLights(false);
//        chan.enableVibration(false);
        chan.setImportance(NotificationManager.IMPORTANCE_LOW);
        // 设置是否应在锁定屏幕上显示此频道的通知
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getNotificationManager().createNotificationChannel(chan);
        return channelId;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {

            mNotificationManager = (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    public void updateNotification(String contentText) {
        if (mBuilder != null) {
            mBuilder.setContentText(contentText);
            getNotificationManager().notify(NOTIFICATION_ID, mBuilder.build());
        }
    }



}
