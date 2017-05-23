package com.example.achuan.teamsystem.model.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by achuan on 17-5-22.
 * 功能：管理员实体类
 */

public class Admin extends BmobObject {

    private String id;//管理员ID号

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
