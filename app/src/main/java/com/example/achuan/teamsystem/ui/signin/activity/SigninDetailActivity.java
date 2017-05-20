package com.example.achuan.teamsystem.ui.signin.activity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.Tools;
import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.base.MvpActivity;
import com.example.achuan.teamsystem.model.bean.BleDevice;
import com.example.achuan.teamsystem.model.bean.MyUser;
import com.example.achuan.teamsystem.presenter.SigninDetailPresenter;
import com.example.achuan.teamsystem.presenter.contract.SigninDetailContract;
import com.example.achuan.teamsystem.service.BleService;
import com.example.achuan.teamsystem.util.DialogUtil;
import com.example.achuan.teamsystem.util.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * Created by achuan on 16-11-7.
 * 功能：点击课程item后打开的活动界面
 * 　　　实现签到功能的具体逻辑处理
 */
public class SigninDetailActivity extends MvpActivity<SigninDetailPresenter> implements SigninDetailContract.View {

    public static final String TAG="SigninDetailActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bt_signin)
    Button mBtSignin;

    Context mContext;
    //标题栏显示的内容\课程编号\学号
    private String title,Cno,Sno;

    private boolean mScanning;
    private Handler mHandler;
    // Stops scanning after 5 seconds.
    private static final long SCAN_PERIOD = 5000;
    private final static int REQUEST_ENABLE_BT = 301;//打开蓝牙的请求码
    //预处理和最后使用的设备数据集合
    private List<BleDevice> mScanDevices_before;

    private ServiceConnection mConnection;
    Intent mBleIntent;//打开蓝牙服务的意图引用对象

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
        mContext=this;
        mHandler=new Handler();//主线程控制(定时)
        mScanDevices_before=new ArrayList<>();

        /*获取上个活动传递过来的意图对象*/
        Intent intent = getIntent();
        //获取标题和课程号
        title = intent.getExtras().getString(Constant.TITLE);
        Cno=intent.getExtras().getString(Constant.CNO);
        MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        Sno=myUser.getSno();
        //设置标题栏内容
        setToolBar(mToolbar, title,true);

        //判断该课程今天是否已经签到过了
        mPresenter.checkSignInRecord(Sno,Cno);

    }

    /*扫描设备的方法*/
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Tools.mBleService.stopscanBle(mLeScanCallback);

                    if(DialogUtil.isProgressDialogShowing()){
                        DialogUtil.closeProgressDialog();
                    }

                    //单次扫描结束,进行结果检测
                    for (int i = 0; i < mScanDevices_before.size(); i++) {
                        BleDevice device=mScanDevices_before.get(i);
                        //LogUtil.d(TAG,device.GetDevice().getAddress()+":"+device.getDistance());
                        //如果发现可以签到的设备
                        if(device.GetDevice().getAddress().equals(Constant.TARGET_BLE_MAC)){
                            //如果在允许范围内
                            if(device.getDistance()<Constant.DISTANCE){
                                //将签到记录存储到后台服务器
                                mPresenter.signinDeal(Sno,Cno);
                            }
                            else {
                                SnackbarUtil.showShort(mBtSignin,"发现可用设备,但未在允许范围内");
                            }
                        }else {
                            SnackbarUtil.showShort(mBtSignin,"未发现可用设备ヽ(≧Д≦)ノ");
                        }
                    }
                }
            }, SCAN_PERIOD);
            mScanning = true;//标记正在扫描设备
            //启动扫描后,清空数据
            mScanDevices_before.clear();
            Tools.mBleService.scanBle(mLeScanCallback);
            DialogUtil.createProgressDialog(mContext,"",
                    "正在检查远程设备信息,请稍候...",
                    true,false);
        } else {
            mScanning = false;
            Tools.mBleService.stopscanBle(mLeScanCallback);
        }
    }

    /*蓝牙设备扫描时的回调方法,在此处进行列表刷新显示*/
    /**
     * @param device 被手机蓝牙扫描到的BLE外设实体对象
     * @param rssi 大概就是表示BLE外设的信号强度，如果为0，则表示BLE外设不可连接。
     * @param scanRecord 被扫描到的BLE外围设备提供的扫描记录，一般没什么用
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    //回到主线程进行UI更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 检查是否是搜索过的设备，并且更新
                            for (int i = 0; i < mScanDevices_before.size(); i++) {
                                //compareTo()方法的返回结果为0时:代表相等
                                if (0 == device.getAddress().compareTo(
                                        mScanDevices_before.get(i).GetDevice().getAddress())) {
                                    mScanDevices_before.get(i).ReflashInf(device, rssi, scanRecord); // 更新信息
                                    return;//当前设备已经搜索过了,更新一下信息就行,下面的操作不再执行
                                }
                            }
                            BleDevice mDevice=new BleDevice(device, rssi, scanRecord);
                            mDevice.CalculateDistance(19);//开始计算距离(19代表信号存储的次数)
                            // 增加新设备
                            mScanDevices_before.add(mDevice);
                        }
                    });
                }
            };

    //点击签到按钮时进行的警告操作
    private void siginWarning() {
        DialogUtil.createOrdinaryDialog(this,
                getString(R.string.warn), "确定要使用学号"+"“"+Sno+"”"+"进行签到吗",
                getString(R.string.confirm), getString(R.string.cancel),true,
                new DialogUtil.OnAlertDialogButtonClickListener() {
                    @Override
                    public void onRightButtonClick() {
                        /**后台启动蓝牙服务*/
                        if(mBleIntent==null){
                            //第一次启动蓝牙服务时进行后台任务绑定
                            mBleIntent=new Intent(mContext, BleService.class);
                            /*活动和服务绑定的实例化方法*/
                            mConnection=new ServiceConnection() {
                                @Override
                                public void onServiceConnected(ComponentName name, IBinder service) {
                                    BleService.LocalBinder binder= (BleService.LocalBinder) service;
                                    Tools.mBleService=binder.getService();//获取到对应的的服务对象
                                    //对硬件设备进行初始化判断
                                    if(Tools.mBleService.initBle()){
                                        if (!Tools.mBleService.mBluetoothAdapter.isEnabled()) {
                                            final Intent enableBtIntent = new Intent(
                                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                        } else {
                                            //蓝牙已经打开
                                            scanLeDevice(true); // 开始扫描设备
                                        }
                                    }else {
                                        //不支持蓝牙,直接退出
                                        finish();
                                    }
                                }
                                @Override
                                public void onServiceDisconnected(ComponentName name) {

                                }
                            };
                            //startService(intent);
                            //将活动和服务绑定在一起
                            bindService(mBleIntent,//创建一个意图,指向服务
                                    mConnection,//绑定的实例化对象
                                    Context.BIND_AUTO_CREATE);//活动和服务绑定后自动创建服务
                        }else {
                            //第一次绑定之后，每次触发扫描操作
                            scanLeDevice(true);
                        }
                    }
                    @Override
                    public void onLeftButtonClick() {

                    }});
    }

    /**
     * 从打开蓝牙的窗口处理完后的回调方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //进行设备查询
                scanLeDevice(true);
            } else {
                finish();
            }
        }
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

    @Override
    public void showCheckResult() {
        screenSignBtn();
    }

    //屏蔽签到按钮
    private void screenSignBtn(){
        //今天已经签到,屏蔽签到按钮
        mBtSignin.setEnabled(false);
        mBtSignin.setText("已签到");
        mBtSignin.setBackgroundResource(R.drawable.bt_bg_white);
    }

    @Override
    public void showSigninSuccess() {
        screenSignBtn();
    }

    @Override
    public void showLoading(String message) {
        DialogUtil.createProgressDialog(mContext,"", message,true,false);
    }

    @Override
    public void hideLoading() {
        if(DialogUtil.isProgressDialogShowing()){
            DialogUtil.closeProgressDialog();
        }
    }

    @Override
    public void showError(String msg) {
        SnackbarUtil.showShort(mBtSignin,msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除活动和服务之间的绑定
        if(mConnection!=null){
            unbindService(mConnection);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
