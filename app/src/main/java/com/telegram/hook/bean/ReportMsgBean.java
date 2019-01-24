package com.telegram.hook.bean;

public class ReportMsgBean {

    /**
     * data : ok
     * status : OK
     * systemTime : 1544676548746
     */

    private String data;
    private String status;
    private long systemTime;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }
}
