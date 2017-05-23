package com.example.achuan.teamsystem.model.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by achuan on 17-5-23.
 * 功能：签到卡数据实体类
 */

public class Card extends BmobObject {

    private String Cnum;//卡号

    private String Sno;//学生号

    public String getCnum() {
        return Cnum;
    }

    public void setCnum(String cnum) {
        Cnum = cnum;
    }

    public String getSno() {
        return Sno;
    }

    public void setSno(String sno) {
        Sno = sno;
    }
}
