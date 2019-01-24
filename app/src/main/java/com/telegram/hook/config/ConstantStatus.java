package com.telegram.hook.config;

public class ConstantStatus {
    private static int totalEvents = 1;
    /**
     * 开启service
     * */
    public static final int serviceConnection=totalEvents++;
    /**
     * 监听到telegram 进入到输入手机号码界面
     * */
    public static final int enterPhoneView=totalEvents++;
    public static final int needGetPhoneNum=totalEvents++;
    /**监听到telegram 进入输入手机验证码界面*/
    public static final int enterSmsView=totalEvents++;
    /**
     * 监听到telegram是登录状态
     * */
    public static final int loginStatus=totalEvents++;







//    -------------------login status---------------------------
    /**
     * 监听到telegram 在登录状态时候 弹窗
     * 显示 tooManyAttempts
     * */
    public static final int tooManyAttempts=totalEvents++;
    public static final int killSelf =totalEvents++;
    /**
     * 监听到telegram 在登录状态时候 弹窗
     * 显示 号码被禁了
     * */
    public static final int loginNumBanned =totalEvents++;
    /**
     * 监听到telegram 在登录状态时候 弹窗
     * 显示 手机号码不对
     * */
    public static final int phoneNumberInvalid=totalEvents++;

    public static final int twoStepCode=totalEvents++;
    //--------------------------add Contacts status----------------------------
    /**
     * 每隔9次添加批量添加联系人
     * 就检查下 当前账号是否权限正常，
     * 能否在telegram 有查找用户的权限
     * */
    public static final int test3Times=totalEvents++;
    /**
     * 监听到telegram 把当前账号封了
     * */
    public static final int numBanned=totalEvents++;
    /**
     * 批量筛选号码完成
     * */
    public static final int checkFinsh=totalEvents++;
    /**
     * 通知我们自己服务前，当前账号是否状态正常
     * */
    public static final int statusIsNormal=totalEvents++;
    /**
     * 进行下一步筛选联系人操作
     * */
    public static final int addContactNext=totalEvents++;


    public static final int checkPermission=totalEvents++;
    /**
     * 添加筛选联系人的时候telegram 报错
     * */
    public static final int addContactEorreorr=totalEvents++;


/*-------------------------------------------------------------------------------*/
    /**
     * 添加需要发送消息用户
     */
    public static final int sendMsgStatus=totalEvents++;
    /**
     * 发送消息列表已经执行完了
     */
    public static final int sendMsgListFinish=totalEvents++;

}
