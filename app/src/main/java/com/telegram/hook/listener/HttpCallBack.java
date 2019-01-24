package com.telegram.hook.listener;

public interface HttpCallBack<T> {
    void onSuccess(T result);
    void onFail(String string);
}
