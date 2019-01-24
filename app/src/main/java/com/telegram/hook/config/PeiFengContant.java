package com.telegram.hook.config;

import android.annotation.SuppressLint;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.telegram.hook.App;


public class PeiFengContant {

    /**
     * 公司http请求的 base url
     */
    public static String SD_HTTP_BASE_URL = "http://106.14.106.240:8088/";
    /**
     * 获取手机号码
     */
    public static String SD_HTTP_PICKPHONEAREA = SD_HTTP_BASE_URL + "pickPhoneArea?deviceId=";

    public static String SD_HTTP_REGISTEREDPHONES = SD_HTTP_BASE_URL + "registeredPhones?phones=";



    public static String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) App.getContext().getSystemService(App.getContext().TELEPHONY_SERVICE);

        @SuppressLint("MissingPermission")
        String imei = telephonyManager.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            imei = "";
        }
        return imei;
    }
//    -----------------msg---------------------------------------
    public static final int SEND_ERROR=0;
    public static final int SEND_SENDING=1;
    public static final int SEND_SUCCESS=2;
    public static final int SEND_DEFAULT=3;
    public static final int NUM_NOT_EXIST=4;

    public static String SD_MSG_USER_PHONELIST=SD_HTTP_BASE_URL + "getPhoneList?deviceId=" + getImei();
    public static String SD_MSG_STATUS=SD_HTTP_BASE_URL+"message/"+getImei()+"/";

}
