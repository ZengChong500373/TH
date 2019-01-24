package com.telegram.hook.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import com.telegram.hook.PeifengController;
import com.telegram.hook.TelegramListener;
import com.telegram.hook.listener.TListener;
import com.telegram.hook.utils.CrashHandler;
import com.telegram.hook.utils.LogUtil;



public class BridgeService extends Service implements TListener {
    public static RemoteCallbackList<PeifengController> mRemotelist = null;

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceLogicControl.getInstance().setListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IBinder stub = new TelegramListener.Stub() {
        @Override
        public void currentStatus(int status,String info) throws RemoteException {
            LogUtil.d("----------------------------client start---------------------------" );
            LogUtil.d("BridgeService currentStatus=" + status);
           ServiceLogicControl.getInstance().DealData(status,info);

        }

        @Override
        public void registerController(PeifengController controller) throws RemoteException {
            LogUtil.d("BridgeService registerController");
            mRemotelist = new RemoteCallbackList<PeifengController>();
            mRemotelist.register(controller);
            mRemotelist.beginBroadcast();
            mRemotelist.finishBroadcast();

        }
    };

    public static void controllerTelegram(int action,String info) {
        if (mRemotelist == null) {
            LogUtil.e("controllerTelegram mRemotelist ==null");
            return;
        }
        try {
            int count = mRemotelist.beginBroadcast();
            LogUtil.e("controllerTelegram Handler count=" + count+" info="+info);
            for (int i = 0; i < count; ++i) {
                PeifengController controller = mRemotelist
                        .getBroadcastItem(i);
                controller.currentAction(action,info);
            }
            mRemotelist.finishBroadcast();
            LogUtil.d("----------------------------client end---------------------------" );
        } catch (Exception e) {
            LogUtil.e("controllerTelegram Exception=" + e.toString());
            CrashHandler.getInstance().handleException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("BridgeService onDestroy");
        ServiceLogicControl.getInstance().removeListener();
    }

    @Override
    public void onDataCome(final int action,final String info) {
       controllerTelegram(action,info);
    }

}
