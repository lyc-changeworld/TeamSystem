package com.example.achuan.teamsystem.ui.admin.ble.activity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.Tools;
import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.base.SimpleActivity;
import com.example.achuan.teamsystem.model.bean.BleDevice;
import com.example.achuan.teamsystem.service.BleService;
import com.example.achuan.teamsystem.ui.admin.ble.adapter.DeviceScanAdapter;
import com.example.achuan.teamsystem.widget.RyItemDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceScanActivity extends SimpleActivity {

    private final static String TAG = "DeviceScanActivity";

    private final static int REQUEST_ENABLE_BT = 2001;//打开蓝牙的请求码

    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //标题栏显示的内容\课程编号\学号
    private String title, Cno;
    private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 5 seconds.
    private static final long SCAN_PERIOD = 5000;

    Context mContext;
    DeviceScanAdapter mDeviceAdapter;
    LinearLayoutManager mLinearlayoutManager;//列表布局管理者

    //预处理和最后使用的设备数据集合
    private List<BleDevice> mScanDevices_before;
    //private List<MTBeacon> mScanDevices_last;

    @Override
    protected int getLayout() {
        return R.layout.activity_ble_scan;
    }

    @Override
    protected void initEventAndData() {
        mContext = this;
        //初始化
        initViewAndData();


        Intent intent = new Intent(mContext, BleService.class);
        //startService(intent);
        //将活动和服务绑定在一起
        mContext.bindService(intent,//创建一个意图,指向服务
                mConnection,//绑定的实例化对象
                Context.BIND_AUTO_CREATE);//活动和服务绑定后自动创建服务
    }

    /*初始化view和数据*/
    private void initViewAndData() {
        /*获取上个活动传递过来的意图对象*/
        Intent intent = getIntent();
        //获取标题和课程号
        title = intent.getExtras().getString(Constant.TITLE);
        Cno = intent.getExtras().getString(Constant.CNO);
        //设置标题栏内容
        setToolBar(mToolbar, title, true);

        mHandler = new Handler();
        mScanDevices_before = new ArrayList<>();
        //mScanDevices_last=new ArrayList<>();
        //创建集合实例对象
        mDeviceAdapter = new DeviceScanAdapter(mContext, mScanDevices_before);
        mLinearlayoutManager = new LinearLayoutManager(mContext);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(mLinearlayoutManager);//为列表添加布局
        mRv.setAdapter(mDeviceAdapter);//为列表添加适配器
        //添加自定义的分割线
        mRv.addItemDecoration(new RyItemDivider(mContext, R.drawable.di_item));


        mDeviceAdapter.setOnClickListener(new DeviceScanAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int postion) {
                Intent intent = new Intent(mContext, DeviceActivity.class);
                intent.putExtra(Constant.CNO,Cno);//课程号
                intent.putExtra(Constant.DEVICE, mScanDevices_before.get(postion).GetDevice());
                startActivity(intent);
            }
        });
    }


    /*活动和服务绑定的实例化方法*/
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleService.LocalBinder binder = (BleService.LocalBinder) service;
            Tools.mBleService = binder.getService();//获取到对应的的服务对象

            //对硬件设备进行初始化判断
            if (Tools.mBleService.initBle()) {
                if (!Tools.mBleService.mBluetoothAdapter.isEnabled()) {
                    final Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    //蓝牙已经打开
                    scanLeDevice(true); // 开始扫描设备
                }
            } else {
                //不支持蓝牙,直接退出
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    /**
     * 恢复交互时,重新进行设备扫描操作
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mScanning == false && Tools.mBleService != null) {
            scanLeDevice(true);
        }
    }

    /**
     * 退出交互时,停止设备扫描操作,清空数据
     */
    @Override
    public void onPause() {
        super.onPause();
        if (Tools.mBleService != null) {
            scanLeDevice(false);
        }
    }

    /*活动销毁时记得解绑服务连接*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unbindService(mConnection);
    }

    /**
     * 2-从打开蓝牙的窗口处理完后的回调方法
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    /*扫描设备的方法*/
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Tools.mBleService.stopscanBle(mLeScanCallback);
                    //最后更新距离
                    mDeviceAdapter.notifyDataSetChanged();
                    /*for (int i = 0; i < mScanDevices_before.size();) { // 防抖
                        if (mScanDevices_before.get(i).CheckSearchcount() > 2) {
                            mScanDevices_before.remove(i);
                        } else {
                            i++;
                        }
                    }*/
                    /*mScanDevices_last.clear(); // 显示出来
                    for (MTBeacon device : mScanDevices_before) {
                        mScanDevices_last.add(device);
                    }
                    mDeviceAdapter.notifyDataSetChanged();*/
                }
            }, SCAN_PERIOD);
            mScanning = true;//标记正在扫描设备
            //启动扫描后,清空数据
            mScanDevices_before.clear();
            Tools.mBleService.scanBle(mLeScanCallback);
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
                                    //mDeviceAdapter.notifyDataSetChanged();
                                    return;//当前设备已经搜索过了,更新一下信息就行,下面的操作不再执行
                                }
                            }
                            BleDevice bleDevice = new BleDevice(device, rssi, scanRecord);
                            bleDevice.CalculateDistance(19);//开始计算距离(19代表信号存储的次数)
                            // 增加新设备
                            mScanDevices_before.add(bleDevice);
                            mDeviceAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
