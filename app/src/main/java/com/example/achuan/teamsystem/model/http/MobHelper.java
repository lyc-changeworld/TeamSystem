package com.example.achuan.teamsystem.model.http;

import android.content.Context;
import android.content.Intent;

import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.ui.main.activity.RegisterActivity;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * Created by achuan on 17-3-22.
 * 功能："Mob"相关操作方法的封装类
 * 注明：采用创建实例对象的形式来调用方法
 */

public class MobHelper {

    private static MobHelper instance = null;

    /***在服务器端获取的Mob应用key和secret*/
    private static String MobAppkey="1c3919a8f1f78";
    private static String MobAppsecret="7ac5de47980f78446f5ea7be9556b770";

    /*单例模式构造实例*/
    public synchronized static MobHelper getInstance(){
        if(instance == null){
            instance=new MobHelper();
        }
        return instance;
    }

    /*----------环信SDK初始化配置----------*/
    public void init(Context context) {
        SMSSDK.initSDK(context,MobAppkey,MobAppsecret);
    }

    /*1-执行注册流程的方法*/
    //创建一个注册界面实例对象
    public static RegisterPage registerPage = new RegisterPage();
    public void registerBySms(final Context context){
        //打开注册页面
        registerPage.setRegisterCallback(new EventHandler() {
            @Override
            public void afterEvent(int i, int i1, Object o) {
                super.afterEvent(i, i1, o);
                // 解析注册结果
                if (i1 == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) o;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get(Constant.PHONE);
                    /*跳转到密码设置界面*/
                    Intent intent=new Intent(context, RegisterActivity.class);
                    //携带电话号码过去,用电话号码进行注册
                    intent.putExtra("phone",phone);
                    context.startActivity(intent);
                    //注册成功后才结束之前的activity
                    // 提交用户信息（此方法可以不调用,）
                    // 该方法体由自己实现,将验证成功的号码信息发送给服务器
                    //registerUser(country, phone);
                }
            }
        });
        registerPage.show(context);
    }





}
