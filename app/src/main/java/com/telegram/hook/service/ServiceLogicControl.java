package com.telegram.hook.service;



import android.text.TextUtils;

import com.telegram.hook.App;
import com.telegram.hook.bean.HttpBean;
import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.http.HttpSdk;
import com.telegram.hook.http.SdHttpMethods;
import com.telegram.hook.http.YmHttpMethods;
import com.telegram.hook.listener.HttpCallBack;
import com.telegram.hook.listener.TListener;
import com.telegram.hook.utils.LaunUtils;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.MsgUtils;
import com.telegram.hook.utils.MyLog;
import com.telegram.hook.utils.SpUtils;
import com.telegram.hook.utils.ToastUtil;

/**
 * 在services接收telegram 应用发过来的状态
 * 通过判断telegram 状态 让其进行下一步操作
 */
public class ServiceLogicControl implements HttpCallBack<HttpBean> {

    private TListener listener;
    private static final ServiceLogicControl ourInstance = new ServiceLogicControl();
    public static String userPhoneNum;
    private static int fail_time = 0;

    private ServiceLogicControl() {
        HttpSdk.init(this);
    }

    static ServiceLogicControl getInstance() {
        return ourInstance;
    }

    public void setListener(TListener listener) {
        this.listener = listener;
    }

    public void DealData(final int status,final String info) {
        LogUtil.d("ServiceLogicControl DealData status="+status,"info="+info);
        if (listener == null) {
            LogUtil.d("ServiceLogicControl listener =null ");
            return;
        }
        if (status == ConstantStatus.serviceConnection) {
            LogUtil.d("ServiceLogicControl  serviceConnection=" + ConstantStatus.serviceConnection);
            listener.onDataCome(ConstantAction.init, "");
            return;
        }
        if (status == ConstantStatus.enterPhoneView || status == ConstantStatus.phoneNumberInvalid || status == ConstantStatus.needGetPhoneNum) {
            LogUtil.d("ServiceLogicControl currentStatus enterPhoneView or banned or numberInvalid status=" + status);
            if (status == ConstantStatus.enterPhoneView){
                fail_time=0;
                ToastUtil.getInstance().showLong("telegram  enterPhoneView");
            }
            HttpSdk.getPhoneNum();
            return;
        }
        if (status == ConstantStatus.loginNumBanned) {
            HttpSdk.ignoreYmNum(info);
            return;
        }
        if (status == ConstantStatus.enterSmsView) {
            LogUtil.d("ServiceLogicControl currentStatus enterSmsView");
            HttpSdk.getCode(userPhoneNum);
            return;
        }
        if ( status == ConstantStatus.loginStatus) {
            LogUtil.d("ServiceLogicControl currentStatus loginSuccess||loginStatus");
            if (status == ConstantStatus.loginStatus){
                ToastUtil.getInstance().showLong("telegram loginStatus start search Users");
            }
            SpUtils.setUserPhone(info);
            userPhoneNum= info;
            LogUtil.d("ServiceLogicControl loginStatus userPhoneNum = "+userPhoneNum);
            HttpSdk.start();
            return;
        }
        if (status==ConstantStatus.addContactEorreorr){
            LogUtil.d("ServiceLogicControl currentStatus addContactEorreorr");
            return;
        }
        if (status==ConstantStatus.checkFinsh){
            HttpSdk.registeredPhones(info, userPhoneNum,this);
            return;
        }
        if (status == ConstantStatus.tooManyAttempts||status == ConstantStatus.killSelf) {
            LogUtil.d("BridgeService currentStatus tooManyAttempts");
            App.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    fail_time=0;
                    LaunUtils.startPeiFeng();
                }
            }, 15000);
            return;
        }
        if (status == ConstantStatus.numBanned ||status ==ConstantStatus.test3Times) {
            HttpSdk.reportBanOrTest3Time(userPhoneNum, status);
            if (status == ConstantStatus.numBanned){
                LogUtil.d("禁号了");
                SpUtils.setRandomLaunTime();
            }
            App.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    LaunUtils.startPeiFeng();
                }
            }, 5000);

            userPhoneNum=null;
        }
        if (status == ConstantStatus.addContactNext){
            LogUtil.d("ServiceLogicControl  ConstantStatus.addContactNext fail time="+fail_time);
            if (fail_time==3){
                listener.onDataCome(ConstantAction.autoLoginOut, "");
                fail_time=0;
            }else {
                HttpSdk.contactNext();
            }
        }
        if (status == ConstantStatus.statusIsNormal){
            LogUtil.d("ServiceLogicControl  ConstantStatus.statusIsNormal isNormal info="+info);
            if (info.contains("true")){
                LogUtil.d("ServiceLogicControl  statusIsNormal contains(\"true\")");
                fail_time=0;
                SpUtils.positionIncrease();
                App.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        DealData(ConstantStatus.addContactNext,"");
                    }
                }, 120000);
                return;
            }
            if (info.contains("false")){
                LogUtil.d("ServiceLogicControl  statusIsNormal contains(\"false\")");
                fail_time = fail_time + 1;
                MyLog.writeSwitchAccount("telegram testPhone="+info+" fail_time="+fail_time);
                HttpSdk.newPhoneUseless(userPhoneNum,info ,this);
                return;
            }
            LogUtil.d("ServiceLogicControl  statusIsNormal 我什么都没走到， 是不是少判断了");
        }
        if (status == ConstantStatus.checkPermission){
            LogUtil.d("ServiceLogicControl  .checkPermission fail_time="+fail_time);
            if (fail_time==3){
                LogUtil.d("ServiceLogicControl  .checkPermission fail time=3 killSelf");
                MyLog.writeSwitchAccount("logout current phone="+SpUtils.getUserPhone()+" fail_time="+fail_time);
                listener.onDataCome(ConstantAction.autoLoginOut, "");
                return;
            }
            HttpSdk.getExamPhone();
        }
        if (status == ConstantStatus.twoStepCode){
            LogUtil.d("ServiceLogicControl  twoStepCode info="+info);
            YmHttpMethods.getInstance().ignore(info);
            SdHttpMethods.getInstance().syncDeviceBanned(info,13);
            return;
        }

        if (status == ConstantStatus.sendMsgStatus){
            MsgUtils.setMsgStatus(info);
            return;
        }
        if (status == ConstantStatus.sendMsgListFinish){
            LogUtil.d("ServiceLogicControl  sendMsgListFinish info="+info);
            HttpSdk.reportMsgStatus();
            return;
        }
    }

    public void removeListener() {
        listener = null;
    }

    @Override
    public void onSuccess(final HttpBean result) {
        LogUtil.d("------------------------ServiceLogicControl onSuccess action="+result.getAtion(),"result="+result.getResult(),"delay="+result.getDelay(),"isAction="+result.isAction()+"---------------");
        if (result.isAction()) {
            if (result.getAtion() == ConstantAction.loginNum) {
                SpUtils.setUserPhone(result.getResult());
                LogUtil.d("ServiceLogicControl onSuccess set userPhoneNum="+result.getResult());
                userPhoneNum = result.getResult();
            }
            if (result.getAtion()==ConstantAction.addMsgUser||result.getAtion()==ConstantAction.checkPermission||result.getAtion()==ConstantAction.searchNums){
                if (userPhoneNum==null||TextUtils.isEmpty(userPhoneNum)){
                    LogUtil.d("ServiceLogicControl login status =null cant search");
                    return;
                }
            }
            listener.onDataCome(result.getAtion(), result.getResult());
        } else {
            App.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    DealData(result.getAtion(), result.getResult());
                }
            }, result.getDelay());
        }
    }

    @Override
    public void onFail(String string) {

    }

}
