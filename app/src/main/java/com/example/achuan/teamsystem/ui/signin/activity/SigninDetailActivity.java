package com.example.achuan.teamsystem.ui.signin.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.base.MvpActivity;
import com.example.achuan.teamsystem.model.bean.BleDevice;
import com.example.achuan.teamsystem.presenter.SigninDetailPresenter;
import com.example.achuan.teamsystem.presenter.contract.SigninDetailContract;
import com.example.achuan.teamsystem.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by achuan on 16-11-7.
 * 功能：点击课程item后打开的活动界面
 * 　　　实现签到功能的具体逻辑处理
 */
public class SigninDetailActivity extends MvpActivity<SigninDetailPresenter> implements SigninDetailContract.View {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bt_signin)
    Button mBtSignin;

    //标题栏显示的内容\课程编号\学号
    private String title,Cno,Sno;
    // Stops scanning after 5 seconds.
    private static final long SCAN_PERIOD = 5000;
    //预处理和最后使用的设备数据集合
    private List<BleDevice> mScanDevices_before;

    @Override
    protected SigninDetailPresenter createPresenter() {
        return new SigninDetailPresenter();
    }
    @Override
    protected int getLayout() {
        return R.layout.activity_signin_detail;
    }
    @Override
    protected void initEventAndData() {
        mScanDevices_before=new ArrayList<>();

        /*获取上个活动传递过来的意图对象*/
        Intent intent = getIntent();
        //获取标题和课程号
        title = intent.getExtras().getString(Constant.TITLE);
        Cno=intent.getExtras().getString(Constant.CNO);
        //设置标题栏内容
        setToolBar(mToolbar, title,true);
        /***初始化***/
        //初始化获取网络数据
        //mPresenter.getTeacherData();

        /**准备添加：开始签到后，启动后台服务进行BLE蓝牙设备扫描*/







    }


    @OnClick({R.id.bt_signin})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_signin:
                siginWarning();
                break;
            default:break;
        }
    }

    //点击签到按钮时进行的警告操作
    private void siginWarning() {
        DialogUtil.createOrdinaryDialog(this,
                getString(R.string.warn), "确定要签到吗",
                getString(R.string.confirm), getString(R.string.cancel),true,
                new DialogUtil.OnAlertDialogButtonClickListener() {
                    @Override
                    public void onRightButtonClick() {
                        //执行签到处理,这里暂时还未对学号进行添加,后续完善用户资料设置后将添加
                        mPresenter.signinDeal("201321111136",Cno);
                    }
                    @Override
                    public void onLeftButtonClick() {

                    }});
    }


    @Override
    public void showSigninSuccess(String message) {
        //签到成功后屏蔽签到按钮
        mBtSignin.setEnabled(false);
        mBtSignin.setText("已签到");
        mBtSignin.setBackgroundResource(R.drawable.bt_bg_white);
        //SnackbarUtil.showShort(mIdRecyclerView,message);
    }
    @Override
    public void showError(String msg) {
        //SnackbarUtil.showShort(mIdRecyclerView,msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
