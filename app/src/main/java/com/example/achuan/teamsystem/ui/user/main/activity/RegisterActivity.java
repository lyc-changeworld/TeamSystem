package com.example.achuan.teamsystem.ui.user.main.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.base.SimpleActivity;
import com.example.achuan.teamsystem.model.bean.MyUser;
import com.example.achuan.teamsystem.model.http.BmobHelper;
import com.example.achuan.teamsystem.model.http.EaseMobHelper;
import com.example.achuan.teamsystem.util.DialogUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by achuan on 17-2-27.
 * 功能：注册环信账号界面
 */

public class RegisterActivity extends SimpleActivity {

    public static final String TAG="RegisterActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_confirmPassword)
    EditText mEtConfirmPassword;
    @BindView(R.id.btn_register)
    Button mBtnRegister;

    /*对应EditText控件中输入字符的引用变量*/
    String userName, firstPassword, confirmPassword;



    @Override
    protected int getLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initEventAndData() {
        //设置Toolbar
        setToolBar(mToolbar, getString(R.string.register), true);

        /**对手机号验证界面传递过来的手机号进行判断处理*/
        //获取到手机号码验证界面传递过来的“手机号”
        String phoneNumber=getIntent().getStringExtra(Constant.PHONE);

        if(phoneNumber!=null){
            //将用户名输入框设置好，然后禁止进行编辑修改
            mEtUsername.setText(phoneNumber);
            mEtUsername.setKeyListener(null);
            mEtUsername.setFocusable(false);
            mEtUsername.setFocusableInTouchMode(false);
            /*接着对手机号进行是否已经存在的判断*/
            BmobHelper.getInstance().userQuery(phoneNumber).findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> list, BmobException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            //存在,提示该用户已经存在
                            Toast.makeText(
                                    RegisterActivity.this,//在该activity显示
                                    getString(R.string.Phone_already_exists),//显示的内容
                                    Toast.LENGTH_LONG).show();//显示的格式
                            //屏蔽编辑框
                            mEtPassword.setKeyListener(null);
                            mEtPassword.setFocusable(false);
                            mEtPassword.setFocusableInTouchMode(false);
                            mEtConfirmPassword.setKeyListener(null);
                            mEtConfirmPassword.setFocusable(false);
                            mEtConfirmPassword.setFocusableInTouchMode(false);
                            //
                            mBtnRegister.setText(getString(R.string.Phone_already_exists));
                        }
                    }else {
                        Toast.makeText(
                                RegisterActivity.this,//在该activity显示
                                "查询账号信息失败:" + e.getMessage(),//显示的内容
                                Toast.LENGTH_LONG).show();//显示的格式
                    }
                }
            });
        }else {
            Toast.makeText(
                    RegisterActivity.this,//在该activity显示
                    "手机号为空...",//显示的内容
                    Toast.LENGTH_LONG).show();//显示的格式
        }



        /***为Edit输入框添加输入监听类,实现合理的效果***/
        //对只有在用户名和密码的输入都不为空的情况下，button按钮才显示有效，
        // 可以自己构造一个TextChange的类，实现一个TextWatcher接口，
        // 里面有三个函数可以实现对所有text的监听。
        TextChange textChange = new TextChange();
        mEtUsername.addTextChangedListener(textChange);
        mEtPassword.addTextChangedListener(textChange);
        mEtConfirmPassword.addTextChangedListener(textChange);
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
                    mEtPassword.length() > 0 &&
                    mEtConfirmPassword.length() > 0) {
                mBtnRegister.setBackgroundResource(R.drawable.btn_login_enable_shape);
                mBtnRegister.setEnabled(true);//设置按钮可以点击使用
            } else {
                mBtnRegister.setBackgroundResource(R.drawable.btn_login_disable_shape);
                mBtnRegister.setEnabled(false);
            }
        }
    }

    @OnClick({R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                registerDeal();
                break;
            default:
                break;
        }
    }

    //注册处理的方法实现
    private void registerDeal() {
        userName = mEtUsername.getText().toString().trim();//用户名
        firstPassword = mEtPassword.getText().toString().trim();//密码
        confirmPassword = mEtConfirmPassword.getText().toString().trim();//确认密码
        //判断两次输入的密码是否相同
        if (firstPassword.equals(confirmPassword)) {
            //创建加载进度框
            DialogUtil.createProgressDialog(this, null,
                    getString(R.string.Is_the_registered),
                    false,false);//对话框无法被取消
            
            //开启子线程进行注册操作
            new Thread(new Runnable() {
                public void run() {
                    //1-调用环信的sdk注册方法
                    EaseMobHelper.getInstance().register(userName, firstPassword, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            //2-接着注册Bmob用户
                            BmobHelper.getInstance().userSignUp(userName,firstPassword).signUp(new SaveListener<BmobUser>() {
                                @Override
                                public void done(BmobUser bmobUser, final BmobException e) {
                                    //如果注册成功
                                    if (e == null) {
                                        //跳转到主线程进行UI更新
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                if (!RegisterActivity.this.isFinishing()&&
                                                        DialogUtil.isProgressDialogShowing()){
                                                    //关闭进度窗口
                                                    DialogUtil.closeProgressDialog();
                                                }
                                                Toast.makeText(getApplicationContext(),
                                                        getResources().getString(R.string.Registered_successfully),
                                                        Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }else {
                                        //跳转到主线程进行UI更新
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                if (!RegisterActivity.this.isFinishing()&&
                                                        DialogUtil.isProgressDialogShowing()){
                                                    //关闭进度窗口
                                                    DialogUtil.closeProgressDialog();
                                                }
                                                Toast.makeText(getApplicationContext(),
                                                        getResources().getString(R.string.Registration_failed) + e,
                                                        Toast.LENGTH_SHORT).show();
                                                //LogUtil.d(TAG,"错误提示"+e);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        @Override
                        public void onError(final int code, final String error) {
                            //出现异常,返回主线程进行UI提示
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (DialogUtil.isProgressDialogShowing()) {
                                        DialogUtil.closeProgressDialog();
                                    }
                                    if (code == EMError.NETWORK_ERROR) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                    } else if (code == EMError.USER_ALREADY_EXIST) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                    } else if (code == EMError.USER_AUTHENTICATION_FAILED) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                    } else if (code == EMError.USER_ILLEGAL_ARGUMENT) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        @Override
                        public void onProgress(int progress, String status) {

                        }
                    });
                }
            }).start();
        } else {
            Toast.makeText(
                    RegisterActivity.this,//在该activity显示
                    getResources().getString(R.string.Two_input_password),//显示的内容
                    Toast.LENGTH_SHORT).show();//显示的格式
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
