package com.example.achuan.teamsystem.model.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by achuan on 16-11-12.
 * 功能：学生信息的数据模型类
 */
public class Student extends BmobObject {
    //Student课程表数据类型
    private String Sno;//学生号
    private String Sname;//学生名
    private String Sdept;//院系
    private String Ssex;//性别
    private Integer Sage;//年龄

    public String getSno() {
        return Sno;
    }

    public void setSno(String sno) {
        Sno = sno;
    }

    public String getSname() {
        return Sname;
    }

    public void setSname(String sname) {
        Sname = sname;
    }

    public String getSsex() {
        return Ssex;
    }

    public void setSsex(String ssex) {
        Ssex = ssex;
    }

    public Integer getSage() {
        return Sage;
    }

    public void setSage(Integer sage) {
        Sage = sage;
    }

    public String getSdept() {
        return Sdept;
    }

    public void setSdept(String sdept) {
        Sdept = sdept;
    }
}
