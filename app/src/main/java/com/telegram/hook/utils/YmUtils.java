package com.telegram.hook.utils;





public class YmUtils {
    private  static String token="0092532946fe22ab865e9315215b4de84c0f4d154001";
    /**
     * 易码平台 请求的base url'
     */
    private static String base_url = "http://api.fxhyd.cn/UserInterface.aspx?";
    private static YmUtils utils = new YmUtils();
    private YmUtils() {
    }

    public static YmUtils getInstance() {
        return utils;
    }

    public String getEffectiveValue(String str) {
        if (str.contains("|")) {
            return str.split("\\|")[1];
        }
        return "";
    }


    String url = "";
    public String getPhoneNumUrl() {
        url = base_url + "action=getmobile&token=" + token + "&itemid=3988&excludeno=";
        MyLog.write2File("url=" + url);
        return url;
    }

    public String getMsgCodeUrl(String num) {
        url = base_url + "action=getsms&token=" +token + "&itemid=3988&mobile=" + num + "&release=1";
        MyLog.write2File("url=" + url);
        return url;
    }

    public String getReleaseNumUrl(String num) {
        url = base_url + "action=release&token=" +token + "&itemid=3988&mobile=" + num;
        MyLog.write2File("url=" + url);
        return url;
    }

    public String getIgnoreUrl(String num) {
        url = base_url + "action=addignore&token=" + token + "&itemid=3988&mobile=" + num;
        MyLog.write2File("url=" + url);
        return url;
    }
}
