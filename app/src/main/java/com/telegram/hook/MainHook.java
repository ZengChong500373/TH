package com.telegram.hook;


import com.telegram.hook.hook.HookController;



import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainHook implements IXposedHookLoadPackage {

    private static final String TAG = "MainHook";
    private static final String packageName = "org.telegram.messenger";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        LogUtil.d(TAG, "handleLoadPackage: packageName = " + lpparam.packageName + "  processName = " + lpparam.processName);
        if (!packageName.equals(lpparam.packageName) || !packageName.equals(lpparam.processName)) {
            return;
        }
        HookController.getInstance().init(lpparam.classLoader);
    }

}
