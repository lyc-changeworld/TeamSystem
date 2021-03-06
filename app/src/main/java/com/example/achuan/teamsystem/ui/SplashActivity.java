package com.example.achuan.teamsystem.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.base.SimpleActivity;
import com.example.achuan.teamsystem.ui.admin.main.activity.AdminMainActivity;
import com.example.achuan.teamsystem.ui.user.main.activity.UserMainActivity;
import com.example.achuan.teamsystem.util.SharedPreferenceUtil;
import com.hyphenate.chat.EMClient;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 17-2-27.
 * 功能：开屏界面
 */

public class SplashActivity extends SimpleActivity {

    private static final int sleepTime = 2000;
    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;//申请权限的请求码

    @BindView(R.id.splash_root)
    RelativeLayout mSplashRoot;
    @BindView(R.id.iv_splash)
    ImageView mIvSplash;

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initEventAndData() {
        /*运行时申请权限*/
        requestPermission();
        //通过Glide来加载图片,避免图片过大造成的异常加载
        Glide.with(this).//传入上下文(Context|Activity|Fragment)
                load(R.drawable.em_splash).//加载图片,传入(URL地址｜资源id｜本地路径)
                into(mIvSplash);//将图片设置到具体某一个IV中
        //开屏设置动画
        setAnim();
        //跳转到登录界面或主界面
        jumpTo();
    }

    /*开屏动画效果*/
    private void setAnim() {
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        mSplashRoot.startAnimation(animation);
    }

    /*启动一个子线程进行延时加载,然后进行跳转*/
    private void jumpTo() {
        new Thread(new Runnable() {
            public void run() {
                //如果环信账号已经登录,直接跳转到主界面
                if (EMClient.getInstance().isLoggedInBefore()) {
                    // ** 免登陆情况 加载所有本地群和会话
                    long start = System.currentTimeMillis();//记录当前时间

                    /*-----自动登录状态下,通过存储的用户名来加载对应的数据库文件-----*/
                    String userName= SharedPreferenceUtil.getCurrentUserName();
                    /***---切换数据库文件到当前对应的用户---***/
                    /*创建一个名为xxx的数据库,而它的所有配置都会直接使用litepal.xml文件中配置的内容*/
                    LitePalDB litePalDB=LitePalDB.fromDefault(userName+"_TEAM");
                    LitePal.use(litePalDB);
                    //切换回litepal.xml中指定的默认数据库
                    //LitePal.useDefault();
                    //删除数据库文件
                    //LitePal.deleteDatabase("xxx");
                    /*环信初始化加载*/
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;//计算加载耗费的时间
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);//接着再睡眠一段时间
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进行身份识别
                    if(SharedPreferenceUtil.getAdmin()){
                        //说明该账户为管理员
                        startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
                    }else {
                        //说明该账户为普通用户
                        startActivity(new Intent(SplashActivity.this, UserMainActivity.class));
                    }
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    //跳转到登录界面
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }

    /*用户对申请权限进行操作后的回调方法*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION:
                //授权结果通过
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //授予了该权限
                } else {
                    //拒绝授予该权限
                }
            default:break;
        }
    }

    /**
     * 1-运行时申请权限
     */
    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, //Context
                Manifest.permission.ACCESS_FINE_LOCATION)//具体的权限名
                != PackageManager.PERMISSION_GRANTED) {//用来比较权限
            // No explanation needed　申请权限.
            ActivityCompat.requestPermissions(this,//Activity实例
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},//数组,存放权限名
                    PERMISSIONS_REQUEST_FINE_LOCATION);//请求码
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
