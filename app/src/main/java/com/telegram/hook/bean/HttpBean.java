package com.telegram.hook.bean;

public class HttpBean {
    int ation;
    String result="";
    /**
     *  true  client  control telegram
     *  flase client  go to next step
     * */
    boolean isAction=true;
    long delay=0;
    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
    public int getAtion() {
        return ation;
    }

    public void setAtion(int ation) {
        this.ation = ation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isAction() {
        return isAction;
    }

    public void setAction(boolean action) {
        isAction = action;
    }
    public HttpBean(int ation) {
        this.ation = ation;
    }
    public HttpBean(int ation, String result) {
        this.ation = ation;
        this.result = result;
    }



    public HttpBean(int ation, String result, boolean isAction) {
        this.ation = ation;
        this.result = result;
        this.isAction = isAction;
    }
    public HttpBean(int ation, String result, boolean isAction,long delay) {
        this.ation = ation;
        this.result = result;
        this.isAction = isAction;
        this.delay = delay;
    }



}
