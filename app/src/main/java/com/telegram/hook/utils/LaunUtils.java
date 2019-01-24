package com.telegram.hook.utils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.telegram.hook.App;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.service.ServiceLogicControl;

import java.util.Random;


public class LaunUtils {
    public static void startPeiFeng() {
       LogUtil.d("LaunUtils startPeiFeng");
        PackageManager packageManager = App.getContext().getPackageManager();
        if (checkPackInfo("org.telegram.messenger")) {
            Intent intent = packageManager.getLaunchIntentForPackage("org.telegram.messenger");
            App.getContext().startActivity(intent);
        }
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private static boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = App.getContext().getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }


}
