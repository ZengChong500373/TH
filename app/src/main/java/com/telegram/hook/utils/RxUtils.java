package com.telegram.hook.utils;


import android.text.TextUtils;
import android.util.LongSparseArray;


import com.google.gson.Gson;
import com.telegram.hook.bean.MsgPhonesBean;
import com.telegram.hook.bean.PickPhoneBean;

import com.telegram.hook.bean.TelegramMsgBean;
import com.telegram.hook.http.SdHttpMethods;
import com.telegram.hook.http.YmHttpMethods;
import com.telegram.hook.service.ServiceLogicControl;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {
    //上报禁号信息
    public static Observable<Boolean> Banned(final String num, final int type) {
        return Observable.interval(10, 30, TimeUnit.SECONDS).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) throws Exception {
                Boolean isOk = SdHttpMethods.getInstance().deviceBanned(num, type);
                return isOk;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }

    public static Observable<Boolean> ignore(final String num) {
        return Observable.interval(10, 30, TimeUnit.SECONDS).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) throws Exception {
                String ignoreNum = "";
                if (num.startsWith("86")) {
                    ignoreNum = num.substring(2);
                } else {
                    ignoreNum = num;
                }
                LogUtil.d("RxUtils ignore userPhoneNum=" + num);
                Boolean isOk = YmHttpMethods.getInstance().syncIgnore(ignoreNum);
                return isOk;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }

    public static Observable<String> getCode(final String num) {
        return Observable.interval(0, 5, TimeUnit.SECONDS).takeUntil(Observable.timer(240, TimeUnit.SECONDS)).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {
                String code = YmHttpMethods.getInstance().getCode(num);
                LogUtil.d("RxUtils getCode =" + code);
                if (code.contains("2007")) {
                    YmHttpMethods.getInstance().asyncReleaseNum(num);
                }
                return code;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }

    public static Observable<String> getPhone() {
        return Observable.interval(20, 30, TimeUnit.SECONDS).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {
                String code = YmHttpMethods.getInstance().syncGetPhoneNum();
                LogUtil.d("RxUtils getPhone =" + code);
                String phoneNum = "";
                if (!TextUtils.isEmpty(code)) {
                    phoneNum = YmUtils.getInstance().getEffectiveValue(code);
                    LogUtil.d("RxUtils phoneNum =" + phoneNum);
                }
                return phoneNum;
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());//回调在主线程;
    }

    public static Observable<PickPhoneBean> pickArea() {
        return Observable.interval(20, 30, TimeUnit.SECONDS).map(new Function<Long, PickPhoneBean>() {
            @Override
            public PickPhoneBean apply(Long aLong) throws Exception {
                String phoneArea = SdHttpMethods.getInstance().pickPhoneArea();
                if (TextUtils.isEmpty(phoneArea)) {
                    LogUtil.d("RxUtils pickArea =null");
                    return new PickPhoneBean();
                }
                LogUtil.d("RxUtils pickArea =" + phoneArea);
                SpUtils.setPhoneArea(phoneArea);
                PickPhoneBean pickPhoneBean = new Gson().fromJson(phoneArea, PickPhoneBean.class);
                return pickPhoneBean;
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());//回调在主线程;
    }

    public static Observable<String> getExam() {
        return Observable.interval(20, 45, TimeUnit.SECONDS).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {
                return SdHttpMethods.getInstance().getExamPhone(ServiceLogicControl.userPhoneNum);
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());//回调在主线程;
    }

    public static Observable<Boolean> phoneUseless(final String userPhone, final String testPhone) {
        return Observable.interval(10, 30, TimeUnit.SECONDS).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) throws Exception {
                //testPhone="false,13668232308";
                String str = "";
                if (testPhone.contains("false")) {
                    str = testPhone.split(",")[1];
                } else {
                    str = testPhone;
                }
                LogUtil.d("RxUtils newPhoneUseless str" + str, "userPhone=" + userPhone);
                Boolean isOk = SdHttpMethods.getInstance().NewPhoneUseless(userPhone, str);
                return isOk;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }

    public static Observable<Boolean> registered(final String phones, final String examingPhone, final PickPhoneBean pickPhoneBean) {
        return Observable.interval(10, 30, TimeUnit.SECONDS).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) throws Exception {
                Boolean isOk = SdHttpMethods.getInstance().registeredPhones(phones, pickPhoneBean, examingPhone);
                return isOk;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }


    public static Observable<String> currentTime() {
        return Observable.interval(0, 1, TimeUnit.SECONDS).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return df.format(System.currentTimeMillis());
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }

    public static Observable<MsgPhonesBean> msgUserPhoneList() {
        return Observable.interval(10, 20, TimeUnit.SECONDS).map(new Function<Long, MsgPhonesBean>() {
            @Override
            public MsgPhonesBean apply(Long aLong) throws Exception {
                String httpstr = SdHttpMethods.getInstance().getMsgUserPhoneList();
//                 String httpstr="{\"list\":[\"8615387906462\",\"8615510012672\",\"8615510012671\",\"8615510012601\",\"8615361547797\",\"8615526450082\",\"8615362998052\",\"8615363168105\",\"8615510012302\",\"8615389906004\"],\"msg\":\"你好\"}";
                LogUtil.d("httpstr =" + httpstr);
                if (TextUtils.isEmpty(httpstr)) {
                    MyLog.write2File("RxMessageUtils getphoneList is null");
                    return new MsgPhonesBean();
                }
                SpUtils.setPhoneArea("");
                return new Gson().fromJson(httpstr, MsgPhonesBean.class);
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }
    public static  int position=0;
    public static Observable<Boolean> reportMsgStatus(final LongSparseArray<TelegramMsgBean> array) {
        position=0;
        return Observable.interval(10, 30, TimeUnit.SECONDS).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) throws Exception {
                Boolean isSuccess =false;
                if (position<array.size()){
                     isSuccess=  SdHttpMethods.getInstance().reportMsgStatus(array.get(position));
                }
                LogUtil.d("SdHttpMethods reportMsgStatus isSuccess="+isSuccess,"position="+position );
                if (!isSuccess){
                    return false;
                }
                position=position+1;
                if (position<array.size()){
                    LogUtil.d("reportMsgStatus position< array Size  return false" );
                    return false;
                }
                position=0;
                return true;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }

    public static Observable<Boolean> lanch() {
        return Observable.interval(10, 55, TimeUnit.SECONDS).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) throws Exception {
                long time = SpUtils.getRandomLaunTime() - System.currentTimeMillis();
                if (time > 0) {
                    return true;
                }
                return false;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }
}
