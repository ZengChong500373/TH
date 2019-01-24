package com.telegram.hook.bean;

import java.util.List;

public class MsgPhonesBean {
    /**
     * list : ["8615536450010","8615362999485","8615520158166","8615351259855","8615378727137","8615362999008","8615526450821","8615510013156","8615510013120","8615526450508"]
     * msg : 你好,初次见面
     */

    private String msg;
    private List<String> list;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }


}
