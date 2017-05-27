package com.example.achuan.teamsystem.ui.user.myself.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.base.MvpFragment;
import com.example.achuan.teamsystem.model.bean.MyUser;
import com.example.achuan.teamsystem.model.http.BmobHelper;
import com.example.achuan.teamsystem.model.http.EaseMobHelper;
import com.example.achuan.teamsystem.presenter.MySelfPresenter;
import com.example.achuan.teamsystem.presenter.contract.MySelfContract;
import com.example.achuan.teamsystem.ui.LoginActivity;
import com.example.achuan.teamsystem.util.DialogUtil;
import com.example.achuan.teamsystem.util.SnackbarUtil;
import com.hyphenate.EMCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;

/**
 * Created by achuan on 17-5-19.
 * 功能：个人信息设置界面
 */

public class MySelfFragment extends MvpFragment<MySelfContract.Presenter> implements MySelfContract.View {


    @BindView(R.id.rt_headIcon)
    RelativeLayout mRtHeadIcon;
    @BindView(R.id.tv_nickName)
    TextView mTvNickName;
    @BindView(R.id.rt_nickName)
    RelativeLayout mRtNickName;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.rt_sex)
    RelativeLayout mRtSex;
    @BindView(R.id.tv_age)
    TextView mTvAge;
    @BindView(R.id.rt_age)
    RelativeLayout mRtAge;
    @BindView(R.id.tv_sno)
    TextView mTvSno;
    @BindView(R.id.rt_sno)
    RelativeLayout mRtSno;
    @BindView(R.id.tv_email)
    TextView mTvEmail;
    @BindView(R.id.rt_email)
    RelativeLayout mRtEmail;
    @BindView(R.id.tv_info)
    TextView mTvInfo;
    @BindView(R.id.tv_signature)
    TextView mTvSignature;
    @BindView(R.id.rt_signature)
    RelativeLayout mRtSignature;
    Unbinder unbinder;
    @BindView(R.id.btn_logout)
    Button mBtnLogout;


    private Context mContext;
    //引用变量,指向当前登录的缓存用户
    private MyUser mMyUser;

    @Override
    protected MySelfContract.Presenter createPresenter() {
        return new MySelfPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_myself;
    }

    @Override
    protected void initEventAndData() {
        mContext = getActivity();
        //1-获取本地缓存用户
        mMyUser = BmobUser.getCurrentUser(MyUser.class);
        /*2-初始化显示用户信息(先去网络端加载,如果不成功再去本地加载缓存的用户信息)*/
        mPresenter.getUserObject(mMyUser.getUsername());
    }


    @Override
    public void showNetUserContent(MyUser myUser) {
        //让本地端缓存的用户信息和网络端同步
        mMyUser.setObjectId(myUser.getObjectId());
        mMyUser.setHeadUri(myUser.getHeadUri());
        mMyUser.setNickName(myUser.getNickName());
        mMyUser.setSex(myUser.getSex());
        mMyUser.setAge(myUser.getAge());
        mMyUser.setSno(myUser.getSno());
        mMyUser.setEmail(myUser.getEmail());
        mMyUser.setSignature(myUser.getSignature());
        //显示当前用户的信息
        mTvNickName.setText(myUser.getNickName());
        mTvSex.setText(myUser.getSex());
        mTvAge.setText(myUser.getAge().toString());
        mTvSno.setText(myUser.getSno());
        mTvEmail.setText(myUser.getEmail());
        mTvSignature.setText(myUser.getSignature());
        //showHeadIcon();
    }

    @Override
    public void showLocalUserContent() {
        mTvNickName.setText(mMyUser.getNickName());
        mTvSex.setText(mMyUser.getSex());
        mTvAge.setText(mMyUser.getAge().toString());
        mTvSno.setText(mMyUser.getSno());
        mTvEmail.setText(mMyUser.getEmail());
        mTvSignature.setText(mMyUser.getSignature());
        //showHeadIcon();
    }

    @OnClick({R.id.rt_headIcon, R.id.rt_nickName, R.id.rt_sex, R.id.rt_age,
            R.id.rt_sno, R.id.rt_email, R.id.rt_signature,R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                logoutDeal();
            case R.id.rt_headIcon:
                break;
            case R.id.rt_nickName:
                final String nickName = mTvNickName.getText().toString();
                DialogUtil.createInputDialog(mContext, nickName,
                        "修改昵称", "确定", "取消", new DialogUtil.OnInputDialogButtonClickListener() {
                            @Override
                            public void onRightButtonClick(String input) {
                                if (!input.equals(nickName)) {
                                    mTvNickName.setText(input);
                                    mPresenter.updateUserInfoByKey(
                                            mMyUser.getObjectId(), "nickName", input);
                                    mMyUser.setNickName(input);
                                }
                            }

                            @Override
                            public void onLeftButtonClick() {
                            }
                        });
                break;
            case R.id.rt_sex:
                final Dialog dialog2 = DialogUtil.createMyselfDialog(mContext,
                        R.layout.dlg_three, Gravity.BOTTOM);
                //初始化布局控件
                TextView chooseMan = (TextView) dialog2.findViewById(R.id.tv_one);
                TextView chooseWoman = (TextView) dialog2.findViewById(R.id.tv_two);
                TextView chooseCancel = (TextView) dialog2.findViewById(R.id.tv_choose_cancel);
                chooseMan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mTvSex.getText().equals("男")) {
                            mTvSex.setText("男");
                            mPresenter.updateUserInfoByKey(mMyUser.getObjectId(),
                                    "sex", "男");
                            mMyUser.setSex("男");
                        }
                        dialog2.dismiss();
                    }
                });
                chooseWoman.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mTvSex.getText().equals("女")) {
                            mTvSex.setText("女");
                            mPresenter.updateUserInfoByKey(mMyUser.getObjectId(),
                                    "sex", "女");
                            mMyUser.setSex("女");
                        }
                        dialog2.dismiss();
                    }
                });
                chooseCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });
                break;
            case R.id.rt_age:
                DialogUtil.createDatePickerDialog(mContext, "请选择出生日期", "确定", "取消",
                        new DialogUtil.OnDatePickerDialogButtonClickListener() {
                            @Override
                            public void onRightButtonClick(Boolean isBorn, int age, String StarSeat) {
                                //根据是否出生的标志进行判断
                                if (isBorn) {
                                    if (!mTvAge.getText().equals(String.valueOf(age))) {
                                        mTvAge.setText(String.valueOf(age));
                                        SnackbarUtil.showShort(mRtAge, "你今年" + age + "岁,星座:" + StarSeat);
                                        mPresenter.updateUserInfoByKey(
                                                mMyUser.getObjectId(), "age", age);
                                        mMyUser.setAge(age);
                                    }
                                } else {
                                    SnackbarUtil.showShort(mRtSignature, "你还未出生,请重新选择-=͟͟͞͞( °∀° )☛");
                                }
                            }

                            @Override
                            public void onLeftButtonClick() {
                            }
                        });
                break;
            case R.id.rt_sno:
                final String sno = mTvSno.getText().toString();
                DialogUtil.createInputDialog(mContext, sno,
                        "修改学号", "确定", "取消", new DialogUtil.OnInputDialogButtonClickListener() {
                            @Override
                            public void onRightButtonClick(String input) {
                                //equals()比较的是对象的内容（区分字母的大小写格式），但是
                                // 如果使用“==”比较两个对象时，比较的是两个对象的内存地址，所以不相等
                                if (!input.equals(sno)) {
                                    mTvSno.setText(input);
                                    mPresenter.updateUserInfoByKey(
                                            mMyUser.getObjectId(), Constant.SNO, input);
                                    mMyUser.setSno(input);
                                }
                            }

                            @Override
                            public void onLeftButtonClick() {
                            }
                        });
                break;
            case R.id.rt_email:
                final String email = mTvEmail.getText().toString();
                DialogUtil.createInputDialog(mContext, email,
                        "修改邮箱", "确定", "取消", new DialogUtil.OnInputDialogButtonClickListener() {
                            @Override
                            public void onRightButtonClick(String input) {
                                //equals()比较的是对象的内容（区分字母的大小写格式），但是
                                // 如果使用“==”比较两个对象时，比较的是两个对象的内存地址，所以不相等
                                if (!input.equals(email)) {
                                    mTvEmail.setText(input);
                                    mPresenter.updateUserInfoByKey(
                                            mMyUser.getObjectId(), "email", input);
                                    mMyUser.setEmail(input);
                                }
                            }

                            @Override
                            public void onLeftButtonClick() {
                            }
                        });
                break;
            case R.id.rt_signature:
                final String signature = mTvSignature.getText().toString();
                DialogUtil.createInputDialog(mContext, signature,
                        "修改个性签名", "确定", "取消", new DialogUtil.OnInputDialogButtonClickListener() {
                            @Override
                            public void onRightButtonClick(String input) {
                                //equals()比较的是对象的内容（区分字母的大小写格式），但是
                                // 如果使用“==”比较两个对象时，比较的是两个对象的内存地址，所以不相等
                                if (!input.equals(signature)) {
                                    mTvSignature.setText(input);
                                    mPresenter.updateUserInfoByKey(
                                            mMyUser.getObjectId(), "signature", input);
                                    mMyUser.setSignature(input);
                                }
                            }
                            @Override
                            public void onLeftButtonClick() {
                            }
                        });
                break;
            default:
                break;
        }
    }
    //退出登录处理
    private void logoutDeal() {
        //弹出对话框确认是否退出
        DialogUtil.createOrdinaryDialog(mContext,
                getString(R.string.Whether_or_not_to_logout),//标题
                getString(R.string.Logout_alert),//内容
                getString(R.string.logout), //右边按钮内容
                getString(R.string.cancel), //左边按钮内容
                true, new DialogUtil.OnAlertDialogButtonClickListener() {
                    //点击退出按钮
                    @Override
                    public void onRightButtonClick() {
                        //创建进度加载对话框
                        DialogUtil.createProgressDialog(mContext,"",
                                getString(R.string.Are_logged_out),
                                false,false);//对话框无法被取消
                        /*-1-首先退出环信登录--*/
                        EaseMobHelper.getInstance().logout(false,new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //2-接着退出Bmob登录
                                BmobHelper.getInstance().userLogOut();//清除缓存用户对象
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(!getActivity().isFinishing()&&DialogUtil.isProgressDialogShowing()){
                                            DialogUtil.closeProgressDialog();
                                        }
                                        //后期可以设置是否退出后清空当前用户的缓存数据,即删除数据库文件
                                        //结束主界面并跳转到登录页面
                                        startActivity(new Intent(mContext, LoginActivity.class));
                                        getActivity().finish();
                                    }
                                });
                            }
                            @Override
                            public void onProgress(int progress, String status) {
                            }
                            @Override
                            public void onError(int code, String message) {
                                //需要在主线程中进行UI更新
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        if(DialogUtil.isProgressDialogShowing()){
                                            DialogUtil.closeProgressDialog();
                                        }
                                        Toast.makeText(mContext,
                                                "退出登录失败...",
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }
                        });
                    }
                    @Override
                    public void onLeftButtonClick() {
                    }
                });
    }

    @Override
    public void showLoading(String message) {
        DialogUtil.createProgressDialog(mContext, "", message, true, false);
    }

    @Override
    public void hideLoading() {
        if (DialogUtil.isProgressDialogShowing()) {
            DialogUtil.closeProgressDialog();
        }
    }

    @Override
    public void showError(String msg) {
        SnackbarUtil.showShort(mRtSignature, msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
