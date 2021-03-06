package com.example.achuan.teamsystem.app;

/**
 * Created by achuan on 17-1-25.
 * 功能：存放一些静态不变的量
 */

public class Constant {

    //=================MODULE TYPE CODE====================
    public static final int TYPE_SIGNIN=100;
    public static final int TYPE_NEWS=200;
    public static final int TYPE_CONTACTS=300;
    public static final int TYPE_MYSELF=400;
    /*MAIN TYPE CODE*/
    public static final int TYPE_SETTINGS=101;

    //活动间进行数据传递的"内容标示名称"
    public static final String PHONE="phone";//手机号
    public static final String TITLE="title";//标题
    public static final String CNO="cno";//课程号


    //对应于Bmob表中的列名称内容
    public static final String SNO="Sno";//学号


    //=================SHARED_PREFERENCE VALUE_NAME====================
    //创建的SharedPreferences文件的文件名
    public static final String PREFERENCES_NAME = "my_sp";
    //当前处于的模块
    public static final String CURRENT_ITEM = "current_item";
    //当前环信账号的用户名
    public static final String KEY_USERNAME = "username";
    //当前用户的身份权限
    public static final String ADMIN_RIGHT = "admin_right";


    //=================环信即时通讯相关====================
    //用户登录异常的情况
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String ACCOUNT_FORBIDDEN = "user_forbidden";
    //聊天消息的类型
    public static final String EXTRA_CHAT_TYPE="chatType";//当前会话的类型(单聊\群聊)
    public static final String EXTRA_USER_ID="userId";//单人聊天的名称
    public static final int CHATTYPE_SINGLE = 10;//单人
    //public static final String EXTRA_CHAT_TYPE="group_type";//群聊天类型
    public static final int CHATTYPE_GROUP = 20;//群
    public static final String EXTRA_GROUP_ID="groupId";//选中群组时传递过来的群ID号
    public static final int CHATTYPE_CHATROOM = 30;//聊天室
    //创建群组
    public static final String GROUP_NAME="groupName";//创建群组时的群名称
    public static final String NEW_MEMBERS="newmembers";//创建群组时邀请的成员集合
    //=================REQUEST CODE请求码====================
    public static final int GROUP_PICK_CONTACTS_REQUEST_CODE=0;
    public static final int NEW_GROUP_REQUEST_CODE=1;
    public static final int CHAT_REQUEST_CODE=2;


    //=================蓝牙相关====================
    public static final String DEVICE="device";
    public static final int DISTANCE=10;//允许可以进行签到的距离
    //目标蓝牙的MAC地址
    public static final String TARGET_BLE_MAC="20:91:48:31:D0:EB";
    //目标蓝牙service的uuid号
    public static final String TARGET_SERVICE_UUID="0000fff0-0000-1000-8000-00805f9b34fb";
    //目标特征值的uuid号
    public static final String TARGET_CHARACTERISTIC_UUID="0000fff6-0000-1000-8000-00805f9b34fb";


    //=================OTHER STRING====================


}
