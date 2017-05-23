package com.example.achuan.teamsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.base.SimpleActivity;
import com.example.achuan.teamsystem.model.bean.Admin;
import com.example.achuan.teamsystem.model.db.ContactUser;
import com.example.achuan.teamsystem.model.db.LitePalDBHelper;
import com.example.achuan.teamsystem.model.http.BmobHelper;
import com.example.achuan.teamsystem.model.http.EaseMobHelper;
import com.example.achuan.teamsystem.model.http.MobHelper;
import com.example.achuan.teamsystem.ui.admin.main.activity.AdminMainActivity;
import com.example.achuan.teamsystem.ui.user.main.activity.UserMainActivity;
import com.example.achuan.teamsystem.util.DialogUtil;
import com.example.achuan.teamsystem.util.LogUtil;
import com.example.achuan.teamsystem.util.SharedPreferenceUtil;
import com.example.achuan.teamsystem.util.SnackbarUtil;
import com.example.achuan.teamsystem.util.StringUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by achuan on 17-2-27.
 * 功能：登录界面的逻辑功能
 */

public class LoginActivity extends SimpleActivity {

    public static final String TAG="LoginActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_forgetPassword)
    TextView mTvForgetPassword;
    @BindView(R.id.tv_newUser)
    TextView mTvNewUser;
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.txtInput_name)
    TextInputLayout mTxtInputName;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.txtInput_password)
    TextInputLayout mTxtInputPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    /*对应EditText控件中输入字符的引用变量*/
    String userName, password;

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initEventAndData() {
        //设置Toolbar
        setToolBar(mToolbar, getString(R.string.login), true);

        //默认用户名输入框显示历史用户名
        if(SharedPreferenceUtil.getCurrentUserName()!=null){
            String userName=SharedPreferenceUtil.getCurrentUserName();
            mEtUsername.setText(userName);
            mEtUsername.setSelection(userName.length());
        }

        /***为Edit输入框添加输入监听类,实现合理的效果***/
        //对只有在用户名和密码的输入都不为空的情况下，button按钮才显示有效，
        // 可以自己构造一个TextChange的类，实现一个TextWatcher接口，
        // 里面有三个函数可以实现对所有text的监听。
        TextChange textChange = new TextChange();
        mEtUsername.addTextChangedListener(textChange);
        mEtPassword.addTextChangedListener(textChange);
    }

    //创建一个多editext的输入监听类
    class TextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mEtUsername.length() > 0 &&
                    mEtPassword.length() > 0) {
                mBtnLogin.setBackgroundResource(R.drawable.btn_login_enable_shape);
                mBtnLogin.setEnabled(true);//设置按钮可以点击使用
            } else {
                mBtnLogin.setBackgroundResource(R.drawable.btn_login_disable_shape);
                mBtnLogin.setEnabled(false);
            }
        }
    }


    /*点击监听事件入口*/
    @OnClick({R.id.tv_forgetPassword, R.id.tv_newUser, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login://登录
                loginDeal();//登录处理
                break;
            case R.id.tv_forgetPassword://忘记密码
                SnackbarUtil.showShort(view,"该功能还未实现...");
                break;
            case R.id.tv_newUser://新用户注册
                /*Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);*/
                MobHelper.getInstance().registerBySms(LoginActivity.this);
                break;
            default:break;
        }
    }

    /**1-登录的处理方法*/
    private void loginDeal() {
        userName = mEtUsername.getText().toString().trim();//用户名
        password = mEtPassword.getText().toString().trim();//密码

        //创建加载进度框
        DialogUtil.createProgressDialog(this,null,
                getString(R.string.Is_landing),
                false,false);//对话框无法被取消

        //开启子线程进行登录操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //1-先登录环信聊天服务器
                EaseMobHelper.getInstance().login(userName, password, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        //2-接着登录Bmob后台服务器
                        BmobHelper.getInstance().userLogin(userName,password).
                                login(new SaveListener<BmobUser>() {
                                    @Override
                                    public void done(BmobUser bmobUser, BmobException e) {
                                        if (e == null) {
                                            /**进行普通用户和管理员的身份识别，然后跳转到不同功能界面*/
                                            BmobHelper.getInstance().adminQuery(userName).findObjects(new FindListener<Admin>() {
                                                @Override
                                                public void done(final List<Admin> list, BmobException e) {
                                                    if(e==null){
                                                        //注意:这里要回到主线程中进行UI更新
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                //关闭加载窗口
                                                                if (!LoginActivity.this.isFinishing() && DialogUtil.isProgressDialogShowing()) {
                                                                    DialogUtil.closeProgressDialog();
                                                                }
                                                                /*-登录成功后更新当前用户信息-*/
                                                                SharedPreferenceUtil.setCurrentUserName(userName);
                                                                /*环信初始化加载*/
                                                                //第一次登录或者之前logout后再登录,加载所有本地群和会话
                                                                EMClient.getInstance().groupManager().loadAllGroups();
                                                                EMClient.getInstance().chatManager().loadAllConversations();
                                                                //加载好友信息并存储到本地
                                                                //getFriends();
                                                                /***---切换数据库文件到当前对应的用户---***/
                                                                /*创建一个名为xxx的数据库,而它的所有配置都会直接使用litepal.xml文件中配置的内容*/
                                                                LitePalDB litePalDB= LitePalDB.fromDefault(userName+"_TEAM");
                                                                LitePal.use(litePalDB);
                                                                //切换回litepal.xml中指定的默认数据库
                                                                //LitePal.useDefault();
                                                                if(list.size()>0){
                                                                    //说明该账户为管理员
                                                                    SharedPreferenceUtil.setAdmin(true);
                                                                    startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                                                                    //提示登录成功
                                                                    Toast.makeText(getApplicationContext(),
                                                                            "管理员账号"+getString(R.string.Login_successfully),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }else {
                                                                    SharedPreferenceUtil.setAdmin(false);
                                                                    startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                                                                    //提示登录成功
                                                                    Toast.makeText(getApplicationContext(),
                                                                            getString(R.string.Login_successfully),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                                finish();
                                                            }
                                                        });
                                                    }else {
                                                        LogUtil.d(TAG,e.getMessage());
                                                        Toast.makeText(getApplicationContext(),
                                                                "身份验证失败,请检查网络是否通畅...",
                                                                Toast.LENGTH_SHORT).show();
                                                        //退出Bmob登录
                                                        BmobHelper.getInstance().userLogOut();
                                                        //退出环信登录
                                                        EaseMobHelper.getInstance().logout(false, new EMCallBack() {
                                                            @Override
                                                            public void onSuccess() {
                                                            }
                                                            @Override
                                                            public void onError(int code, String error) {

                                                            }
                                                            @Override
                                                            public void onProgress(int progress, String status) {

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }else {
                                            //提示失败
                                            Toast.makeText(LoginActivity.this,
                                                    getString(R.string.Login_failed) + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            LogUtil.d(TAG,e.getMessage());
                                            //退出环信登录
                                            EaseMobHelper.getInstance().logout(false, new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                }
                                                @Override
                                                public void onError(int code, String error) {

                                                }
                                                @Override
                                                public void onProgress(int progress, String status) {

                                                }
                                            });
                                        }
                                    }
                                });
                    }
                    @Override
                    public void onError(final int code, final String error) {
                        //发生错误时,在主线程中进行警告提示
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (DialogUtil.isProgressDialogShowing()) {
                                    DialogUtil.closeProgressDialog();
                                }
                                //根据不同的错误码进行不同的提醒
                                if(code== EMError.USER_NOT_FOUND){
                                    //不存在此用户 错误码:204
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.User_not_found),
                                            Toast.LENGTH_SHORT).show();
                                }else if(code==EMError.USER_AUTHENTICATION_FAILED){
                                    //用户id或密码错误 错误码:202
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.UserName_or_password_is_wrong),
                                            Toast.LENGTH_SHORT).show();
                                }else if(code==EMError.SERVER_TIMEOUT){
                                    //等待服务器响应超时 错误码:301
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.Wait_for_server_response_timeout),
                                            Toast.LENGTH_SHORT).show();
                                }else if(code==EMError.USER_LOGIN_ANOTHER_DEVICE) {
                                    //账户在另外一台设备登录 错误码:206
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.User_login_on_another_device),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.Login_failed) + error,
                                            Toast.LENGTH_SHORT).show();
                                }
                                //用来测试获取错误码,错误码对应错误信息见以下地址:
                                //http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                        /*Toast.makeText(getApplicationContext(), ""+code,
                                Toast.LENGTH_SHORT).show();*/
                            }
                        });
                    }
                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }).start();
    }

    /**2-获取好友列表,并存储到本地数据库中*/
    private  void  getFriends(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取网络端的所有好友列表
                    List<String> usernames = EMClient.getInstance().contactManager().
                            getAllContactsFromServer();

                    Map<String ,ContactUser> users=new HashMap<String ,ContactUser>();
                    for(String username:usernames){
                        //测试打印当前用户的联系人的名称
                        //LogUtil.d(TAG,username);
                        ContactUser user=new ContactUser();
                        user.setUserName(username);
                        //设置首字母
                        user.setInitialLetter(StringUtil.getHeadChar(username));
                        users.put(username, user);
                    }
                    //保存联系人到本地数据库
                    LitePalDBHelper.getInstance().saveContactList(new ArrayList<ContactUser>(users.values()));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
