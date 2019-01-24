package com.telegram.hook.config;

public class ConstantAction {
    private static int totalEvents = 999;

  /**
   * 初始化
   * 开始登录或者开始筛选账号
   */
    public static final int init =totalEvents++;
    /**
     * 输入登录手机号码
     * */
    public static final int loginNum=totalEvents++;
    /**
     * 输入登录验证码
     * */
    public static final int loginCode=totalEvents++;
    /**
     * 返回输入手机号码的界面
     * */
    public static final int returnPhonView =totalEvents++;
    /**
     * 退出到登录界面
     * */
    public static final int autoLoginOut=totalEvents++;
    /**
     * 开始筛选号码
     */
    public static final int searchNums=totalEvents++;
    /**
     * 筛选号码的权限
     */
    public static final int checkPermission=totalEvents++;
  /**
   * 添加需要发送消息用户
   */
    public static final int addMsgUser=totalEvents++;






}
