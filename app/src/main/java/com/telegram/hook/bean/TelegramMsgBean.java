package com.telegram.hook.bean;

public class TelegramMsgBean {
    public int id;
    public String phone;
    public String msg;
    public String status="BANNED";
    public String getStatus() {
        return status;
    }
    public TelegramMsgBean() {

    }
    public void setStatus(String status) {
        this.status = status;
    }
    public TelegramMsgBean(String phone) {
        this.phone = phone;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
