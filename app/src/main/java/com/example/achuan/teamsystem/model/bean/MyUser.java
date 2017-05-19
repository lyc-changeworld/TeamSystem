package com.example.achuan.teamsystem.model.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by achuan on 16-10-23.
 * 功能：扩展用户类
 *     对BmobUser类进行扩展，添加一些新的属性
 *     同时也是环信的账号入口
 */
public class MyUser extends BmobUser {
    //扩展的信息
    private String Sno;
    private String headUri;//头像在后台服务器端对应的链接地址
    private String nickName;//昵称
    private String sex;//性别
    private Integer age;//年龄
    private String signature;//个性签名

    public String getSno() {
        return Sno;
    }

    public void setSno(String sno) {
        Sno = sno;
    }

    public String getHeadUri() {
        return headUri;
    }

    public void setHeadUri(String headUri) {
        this.headUri = headUri;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}

