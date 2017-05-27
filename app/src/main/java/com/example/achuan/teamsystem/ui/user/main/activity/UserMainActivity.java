package com.example.achuan.teamsystem.ui.user.main.activity;


import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.base.SimpleActivity;
import com.example.achuan.teamsystem.ui.admin.contact.fragment.ContactsMainFragment;
import com.example.achuan.teamsystem.ui.admin.conversation.fragment.ConversationMainFragment;
import com.example.achuan.teamsystem.ui.user.myself.fragment.MySelfFragment;
import com.example.achuan.teamsystem.ui.user.signin.fragment.SigninFragment;
import com.example.achuan.teamsystem.util.SharedPreferenceUtil;
import com.example.achuan.teamsystem.util.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserMainActivity extends SimpleActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String TAG="UserMainActivity";
    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;//申请权限的请求码

    //需要装载到主活动中的Fragment的引用变量
    SigninFragment mSigninFragment;
    ConversationMainFragment mConversationMainFragment;
    ContactsMainFragment mContactsMainFragment;
    MySelfFragment mMySelfFragment;


    //定义变量记录需要隐藏和显示的fragment的编号
    private int hideFragment = Constant.TYPE_SIGNIN;
    private int showFragment = Constant.TYPE_SIGNIN;

    //记录左侧navigation的item点击
    MenuItem mLastMenuItem;//历史
    int contentViewId;//内容显示区域的控件的id号,后面用来添加碎片使用

    @BindView(R.id.btm_nav)
    BottomNavigationView mBtmNav;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    protected int getLayout() {
        return R.layout.activity_main_user;
    }

    @Override
    protected void initEventAndData() {
        /*运行时申请权限*/
        requestPermission();
        /********************检测并打开网络****************/
        //SystemUtil.checkAndShowNetSettingDialog(this);
        contentViewId = R.id.fl_main_content;//获取内容容器的ID号

        /***1-初始化底部导航栏设置***/
        //初始化第一次显示的item为设置界面
        mLastMenuItem = mBtmNav.getMenu().findItem(R.id.bottom_0);
        mLastMenuItem.setChecked(true);
        //添加点击监听事件
        mBtmNav.setOnNavigationItemSelectedListener(this);

        /***2-初始化创建模块的fragment实例对象,并装载到主布局中****/
        //初始化toolbar
        setToolBar(mToolbar, (String) mLastMenuItem.getTitle(),false);
        //mToolbar.setLogo(R.drawable.logo);//设置logo
        //先添加这两个碎片
        mSigninFragment=new SigninFragment();
        addFragment(contentViewId,mSigninFragment);

        //默认先显示会话界面
        showFragment(mSigninFragment, mSigninFragment);
        //并将第一界面碎片添加到布局容器中
        //replaceFragment(contentViewId, getTargetFragment(showFragment));
        SharedPreferenceUtil.setCurrentItem(showFragment);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //EMClient.getInstance().chatManager().removeMessageListener(mEMMessageListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
    }

    /**1.2-在活动销毁时移除联系人监听器*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //EMClient.getInstance().contactManager().removeContactListener(mEMContactListener);
        //EMClient.getInstance().removeConnectionListener(mEMConnectionListener);
    }

    //重写back按钮的点击事件
    @Override
    public void onBackPressed() {
        SystemUtil.showExitDialog(this);
    }

    /*-----为工具栏创建菜单选项按钮-----*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().//获得MenuInflater对象
                inflate(R.menu.menu_toolbar_main,//指定通过哪一个资源文件来创建菜单
                menu);
        return true;//返回true,表示允许创建的菜单显示出来
    }

    /*-----为菜单按钮添加点击监听事件-----*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                break;
            default:break;
        }
        return true;//返回true,表示允许item点击响应
    }

    /*底部导航栏的切换监听事件*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_0:
                showFragment = Constant.TYPE_SIGNIN;
                //第一次加载显示时,才创建碎片对象,并添加到内容容器中
                /*if (mNewsMainFragment == null) {
                    mNewsMainFragment = new ConversationMainFragment();
                    addFragment(contentViewId, mNewsMainFragment);
                }*/
                break;
            case R.id.bottom_1:
                showFragment = Constant.TYPE_NEWS;
                if (mConversationMainFragment == null) {
                    mConversationMainFragment = new ConversationMainFragment();
                    addFragment(contentViewId, mConversationMainFragment);
                }
                break;
            case R.id.bottom_2:
                showFragment = Constant.TYPE_CONTACTS;
                if (mContactsMainFragment == null) {
                    mContactsMainFragment = new ContactsMainFragment();
                    addFragment(contentViewId, mContactsMainFragment);
                }
                break;
            case R.id.bottom_3:
                showFragment = Constant.TYPE_MYSELF;
                if (mMySelfFragment == null) {
                    mMySelfFragment = new MySelfFragment();
                    addFragment(contentViewId, mMySelfFragment);
                }
                break;
            default:break;
        }
        /***点击item后进行显示切换处理,并记录在本地中***/
        if (mLastMenuItem != null && mLastMenuItem != item) {
            mToolbar.setTitle(item.getTitle());//改变标题栏的内容
            mLastMenuItem.setChecked(false);//取消历史选择
            item.setChecked(true);//设置当前item选择
            //记录当前显示的item
            SharedPreferenceUtil.setCurrentItem(showFragment);
            //实现fragment的切换显示
            showFragment(getTargetFragment(hideFragment), getTargetFragment(showFragment));
            //选择过的item变成了历史
            mLastMenuItem = item;
            //当前fragment显示完就成为历史了
            hideFragment = showFragment;
        }
        return true;
    }

    //根据item编号获取fragment对象的方法
    private Fragment getTargetFragment(int item) {
        switch (item) {
            case Constant.TYPE_SIGNIN:
                return mSigninFragment;
            case Constant.TYPE_NEWS:
                return mConversationMainFragment;
            case Constant.TYPE_CONTACTS:
                return mContactsMainFragment;
            case Constant.TYPE_MYSELF:
                return mMySelfFragment;
            default:break;
        }
        return mSigninFragment;
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
