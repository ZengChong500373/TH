package com.telegram.hook.http;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.telegram.hook.bean.HttpBean;
import com.telegram.hook.bean.MsgPhonesBean;
import com.telegram.hook.bean.PickPhoneBean;
import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.listener.HttpCallBack;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.MsgUtils;
import com.telegram.hook.utils.RxUtils;
import com.telegram.hook.utils.SpUtils;
import com.telegram.hook.utils.ToastUtil;




import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


public class HttpSdk {
    private static Disposable currentDisposable;
    private static Disposable banOrTestDisposable;
    private static HttpSdkException httpSdkException;
    private static HttpCallBack<HttpBean> httpCallBack;

    public static void init(final HttpCallBack<HttpBean> callBack) {
        if (httpSdkException == null) {
            httpSdkException = new HttpSdkException();
        }
        httpCallBack = callBack;
    }

    public static void start() {
        if (SpUtils.getMode() == 0) {
            ToastUtil.getInstance().showLong("begin search user");
            contactNext();
        } else {
            ToastUtil.getInstance().showLong("begin send  msg");
            getMsgUserPhoneList();
        }
    }
  private  static Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        getPhoneNum();
    }
};
    public static void getPhoneNum() {
        closeDisposable();
        if (SpUtils.getRandomLaunTime() != 0 && System.currentTimeMillis() - SpUtils.getRandomLaunTime() < 0) {
            long time = System.currentTimeMillis() - SpUtils.getRandomLaunTime();
            LogUtil.d("HttpSdk getPhoneNum currentTime not right   time=" + time);
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1,3000);
            return;
        }
        LogUtil.d("HttpSdk getPhoneNum ");
        SpUtils.reSetLaunTime();
        currentDisposable = RxUtils.getPhone().subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (!TextUtils.isEmpty(s)) {
                    LogUtil.d("HttpSdk getPhoneNum userPhoneNum=" + s, "ConstantAction.loginNum");
                    httpCallBack.onSuccess(new HttpBean(ConstantAction.loginNum, s));
                    closeDisposable();
                }
            }
        }, httpSdkException);
    }

    public static void getCode(final String num) {
        LogUtil.d("HttpSdk getCode userPhoneNum=" + num);
        closeDisposable();
        currentDisposable = RxUtils.getCode(num).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtil.d("HttpSdk getCode userPhoneNum=" + num + "sms code is " + s);
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                if (s.contains("2007")) {
                    LogUtil.d("HttpSdk getcode is 2007 in 64 Line ", "ConstantAction.returnPhonView");
                    httpCallBack.onSuccess(new HttpBean(ConstantAction.returnPhonView));
                    closeDisposable();
                    return;
                }
                if (s.contains("is")) {
                    String str = s.split("is")[1];
                    LogUtil.d("HttpSdk getCode contain is ", "ConstantAction.loginCode");
                    httpCallBack.onSuccess(new HttpBean(ConstantAction.loginCode, str.trim()));
                    closeDisposable();
                    return;
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtil.d("HttpSdk getCode exception return 2007", "ConstantAction.returnPhonView");
                httpCallBack.onSuccess(new HttpBean(ConstantAction.returnPhonView));
                closeDisposable();
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                YmHttpMethods.getInstance().releaseNum(num);
                LogUtil.d("HttpSdk getCode finish retrun 2007", "ConstantAction.returnPhonView");
                httpCallBack.onSuccess(new HttpBean(ConstantAction.returnPhonView));
                closeDisposable();
            }
        });
    }

    public static void ignoreYmNum(final String num) {
        LogUtil.d("HttpSdk ignoreYmNum userPhoneNum=" + num);
        closeDisposable();
        currentDisposable = RxUtils.ignore(num).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    LogUtil.d("HttpSdk ignoreYmNum success", "ConstantStatus.needGetPhoneNum");
                    httpCallBack.onSuccess(new HttpBean(ConstantStatus.needGetPhoneNum, "", false, 20000));
                    closeDisposable();
                }

            }
        }, httpSdkException);
    }

    public static void reportBanOrTest3Time(final String num, int status) {
        int reporetType = -1;
        if (status == ConstantStatus.numBanned) {
            reporetType = 10;
        } else {
            reporetType = 9;
        }
        if (banOrTestDisposable!=null){
            banOrTestDisposable.dispose();
            banOrTestDisposable=null;
        }
        LogUtil.d("HttpSdk reportBanOrTest3Time num=" + num + " reporetType=" + reporetType);
        banOrTestDisposable = RxUtils.Banned(num, reporetType).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    LogUtil.d("HttpSdk reportBanOrTest3Time  success");
                    if (banOrTestDisposable!=null){
                        banOrTestDisposable.dispose();
                        banOrTestDisposable=null;
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (banOrTestDisposable!=null){
                    banOrTestDisposable.dispose();
                    banOrTestDisposable=null;
                }
            }
        });
    }

    public static void contactNext() {
        int position = SpUtils.getPosition();
        LogUtil.d("HttpSdk contactNext position=" + position);
        if (position % 12 == 0 && position != 0) {
            getExamPhone();
        } else {
            pickPhoneArea();
        }

    }

    public static void pickPhoneArea() {
        LogUtil.d("HttpSdk pickPhoneArea ");
        closeDisposable();
        currentDisposable = RxUtils.pickArea().subscribe(new Consumer<PickPhoneBean>() {
            @Override
            public void accept(PickPhoneBean bean) throws Exception {
                if (bean.getStart() != -1) {
                    LogUtil.d("HttpSdk pickPhoneArea  success", "ConstantAction.searchNums", "bean.getStart=" + bean.getStart(), "bean.getEnd=" + bean.getEnd());
                    httpCallBack.onSuccess(new HttpBean(ConstantAction.searchNums, bean.getStart() + "," + bean.getEnd()));
                    closeDisposable();
                }
            }
        }, httpSdkException);
    }

    public static void getExamPhone() {
        LogUtil.d("HttpSdk getExamPhone ");
        closeDisposable();
        currentDisposable = RxUtils.getExam().subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (!TextUtils.isEmpty(s)) {
                    LogUtil.d("HttpSdk getExamPhone userPhone=" + s, "ConstantAction.getExamPhone");
                    httpCallBack.onSuccess(new HttpBean(ConstantAction.checkPermission, s));
                    closeDisposable();
                }
            }
        }, httpSdkException);
    }

    public static void newPhoneUseless(String userPhone, String testPhone, final HttpCallBack<HttpBean> callBack) {
        //testPhone="false,13668232308";
        LogUtil.d("HttpSdk newPhoneUseless userPhone=" + userPhone + " testPhone=" + testPhone);
        closeDisposable();
        currentDisposable = RxUtils.phoneUseless(userPhone, testPhone).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    LogUtil.d("HttpSdk newPhoneUseless success");
                    callBack.onSuccess(new HttpBean(ConstantStatus.checkPermission, "", false, 60000));
                    closeDisposable();
                }
            }
        }, httpSdkException);
    }

    public static void registeredPhones(final String phones, final String userPhone, final HttpCallBack<HttpBean> callBack) {
        LogUtil.d("HttpSdk registered ");
        closeDisposable();
        LogUtil.d("HttpSdk registered userPhone=" + userPhone);
        LogUtil.d("HttpSdk registered phones=" + phones);
        LogUtil.d("HttpSdk registered PhoneArea=" + SpUtils.getPhoneArea());
        PickPhoneBean pickPhoneBean = new Gson().fromJson(SpUtils.getPhoneArea(), PickPhoneBean.class);
        currentDisposable = RxUtils.registered(phones, userPhone, pickPhoneBean).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    LogUtil.d("HttpSdk registered true", "ConstantStatus.addContactNext");
                    SpUtils.positionIncrease();
                    LogUtil.d("HttpSdk registered positionIncrease ");
                    callBack.onSuccess(new HttpBean(ConstantStatus.addContactNext, "", false, 120000));
                    closeDisposable();
                }
            }
        }, httpSdkException);
    }

    public static void getMsgUserPhoneList() {
        LogUtil.d("HttpSdk getMsgUserPhoneList ");
        closeDisposable();
        currentDisposable = RxUtils.msgUserPhoneList().subscribe(new Consumer<MsgPhonesBean>() {
            @Override
            public void accept(MsgPhonesBean msgPhonesBean) throws Exception {
                if (msgPhonesBean.getList() != null) {
                    MsgUtils.bean2SparseArray(msgPhonesBean);
                    String info = MsgUtils.bean2String(msgPhonesBean);
                    LogUtil.d("HttpSdk getMsgUserPhoneList info=" + info);
                    httpCallBack.onSuccess(new HttpBean(ConstantAction.addMsgUser, info));
                    closeDisposable();
                }
            }
        }, httpSdkException);
    }

    public static void reportMsgStatus() {
        LogUtil.d("HttpSdk reportMsgStatus ");
        closeDisposable();
        currentDisposable = RxUtils.reportMsgStatus(MsgUtils.sparseArray).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    LogUtil.d("HttpSdk reportMsgStatus finish");
                    closeDisposable();
                }
            }
        }, httpSdkException);
    }

    public static class HttpSdkException implements Consumer<Throwable> {
        @Override
        public void accept(Throwable throwable) throws Exception {
            LogUtil.d("HttpSdk HttpSdkException =" + throwable.toString());
            httpCallBack.onFail("fail");
            closeDisposable();
        }
    }

    public static void closeDisposable() {
        if (currentDisposable != null) {
            LogUtil.d("HttpSdk currentDisposable disposable not not null");
            currentDisposable.dispose();
            currentDisposable = null;
        }
    }
}
