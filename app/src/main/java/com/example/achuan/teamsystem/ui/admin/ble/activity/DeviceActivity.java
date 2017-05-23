package com.example.achuan.teamsystem.ui.admin.ble.activity;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.Tools;
import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.base.MvpActivity;
import com.example.achuan.teamsystem.model.bean.MyBean;
import com.example.achuan.teamsystem.presenter.CardSigninPresenter;
import com.example.achuan.teamsystem.presenter.contract.CardSigninContract;
import com.example.achuan.teamsystem.service.BleService;
import com.example.achuan.teamsystem.ui.admin.ble.adapter.DeviceLinkAdapter;
import com.example.achuan.teamsystem.util.DialogUtil;
import com.example.achuan.teamsystem.util.SnackbarUtil;
import com.example.achuan.teamsystem.widget.RyItemDivider;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by achuan on 17-4-10.
 * 功能：设备连接界面
 */

public class DeviceActivity extends MvpActivity<CardSigninPresenter> implements CardSigninContract.View {

    public static final String TAG = "DeviceActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    //设置广播监听
    private BluetoothReceiver bluetoothReceiver = null;

    /*一些连接时的状态标志*/
    private boolean read_name_flag = false;//读取名称标志
    //	private boolean servicesdiscovered_flag = false;
    private boolean connect_flag = false;//连接标志
    private boolean bind_flag = false;//设备标志
    private boolean exit_activity = false;//是否退出当前活动的标志

    private BluetoothDevice mBluetoothDevice;//当前需要连接的设备实例
    private BluetoothGattCharacteristic mGattCharacteristic;
    private int proper = 0; // 通道权限

    //视图布局
    private List<MyBean> mMyBeanList;
    DeviceLinkAdapter mDeviceLinkAdapter;
    LinearLayoutManager mLinearlayoutManager;//列表布局管理者

    //标题栏显示的内容\课程编号\学号
    private String title, Cno, Sno=null;
    Context mContext;

    String mCnum= "1001";
    byte[] mCbyte= mCnum.getBytes();

    @Override
    protected int getLayout() {
        return R.layout.activity_device;
    }

    @Override
    protected CardSigninPresenter createPresenter() {
        return new CardSigninPresenter();
    }

    @Override
    protected void initEventAndData() {
        mContext = this;
        /*获取上个活动传递过来的意图对象*/
        Intent intent = getIntent();
        //拿到上个活动传递过来的"设备对象"
        mBluetoothDevice = intent.getParcelableExtra(Constant.DEVICE);
        title = mBluetoothDevice.getName();
        Cno = intent.getExtras().getString(Constant.CNO);//课程号
        //设置标题内容
        setToolBar(mToolbar, title, true);
        //获取到目标特征值对象，并进行权限设置
        getDefaultName();
        //添加广播监听器
        setBroadcastReceiver();
        //数据测试的界面初始化
        initView();
    }

    //接收字节流数据的方法
    private void dis_recive_msg(byte[] tmp_byte) {
        String tmp = "";
        if (0 == tmp_byte.length) {
            return;
        }
        try {
            tmp = new String(tmp_byte, "GB2312");
            if(tmp!=""){
                mPresenter.getSno(tmp);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 3-初始化布局
     */
    private void initView() {
        mMyBeanList = new ArrayList<>();
        mDeviceLinkAdapter = new DeviceLinkAdapter(mContext, mMyBeanList);
        mLinearlayoutManager = new LinearLayoutManager(mContext);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(mLinearlayoutManager);//为列表添加布局
        mRv.setAdapter(mDeviceLinkAdapter);//为列表添加适配器
        //添加自定义的分割线
        mRv.addItemDecoration(new RyItemDivider(mContext, R.drawable.di_item));
    }

    /**
     * 2-动态注册广播接收器
     */
    private void setBroadcastReceiver() {
        // 创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND
        IntentFilter intentFilter = new IntentFilter(
                BleService.ACTION_READ_Descriptor_OVER);//设备描述相关读取完毕
        intentFilter.addAction(BleService.ACTION_ServicesDiscovered_OVER);//设备发现完毕
        intentFilter.addAction(BleService.ACTION_STATE_CONNECTED);//已连接上设备
        intentFilter.addAction(BleService.ACTION_STATE_DISCONNECTED);//连接断开
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//设备绑定状态改变
        //数据相关
        intentFilter.addAction(BleService.ACTION_READ_OVER);//读取数据
        //intentFilter.addAction(BleService.ACTION_RSSI_READ);
        intentFilter.addAction(BleService.ACTION_WRITE_OVER);//写入数据
        intentFilter.addAction(BleService.ACTION_DATA_CHANGE);//数据发生改变

        bluetoothReceiver = new BluetoothReceiver();
        // 注册广播接收器
        registerReceiver(bluetoothReceiver, intentFilter);
    }

    /*按钮测试数据*/
    @OnClick({R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.fab:
                if (mGattCharacteristic != null) {
                    //把数据交给“搬运工”
                    mGattCharacteristic.setValue(mCbyte);
                    Tools.mBleService.mBluetoothGatt
                            .writeCharacteristic(mGattCharacteristic);
                }
                break;
            default:break;
        }
    }

    /*自定义的蓝牙广播监听器类*/
    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BleService.ACTION_READ_Descriptor_OVER:
                    //设备描述读取完毕
                    if (BluetoothGatt.GATT_SUCCESS == intent.getIntExtra("value", -1)) {
                        read_name_flag = true;
                    }
                    break;
                case BleService.ACTION_ServicesDiscovered_OVER:
                    //connect_flag = true;
                    break;
                case BleService.ACTION_STATE_CONNECTED:
                    //设备已连接
                    connect_flag = true;
                    Toast.makeText(context, "已连接", Toast.LENGTH_SHORT).show();
                    break;
                case BleService.ACTION_STATE_DISCONNECTED:
                    //设备断开连接
                    connect_flag = false;
                    Toast.makeText(context, "已断开连接", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //绑定状态发生改变
                    if (BluetoothDevice.BOND_BONDED == mBluetoothDevice.getBondState()) {
                        //该设备已经被绑定了
                        Tools.mBleService.disConectBle();
                        readNameFail.sendEmptyMessageDelayed(0, 200);
                    } else if (BluetoothDevice.BOND_BONDING == mBluetoothDevice.getBondState()) {
                        //正在配对
                        bind_flag = true;
                    }
                    break;
                case BleService.ACTION_DATA_CHANGE:
                    //数据改变通知
                    dis_recive_msg(intent.getByteArrayExtra("value"));
                    break;
                case BleService.ACTION_READ_OVER:
                    //读取数据
                    dis_recive_msg(intent.getByteArrayExtra("value"));
                    break;
                case BleService.ACTION_WRITE_OVER:
                    //写入数据
                    break;
                default:break;
            }
        }
    }

    /**
     * 1-查询并获取到目标特征值对象
     */
    private void getDefaultName() {
        // 开启一个缓冲对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("正在加载...");
        pd.setMessage("正在连接");
        pd.show();
        new readNameThread().start();
    }

    //开启异步线程来进行特征值对象的获取和设置
    private class readNameThread extends Thread {
        @Override
        public void run() {
            super.run();

            Message msg = reflashDialogMessage.obtainMessage();
            Bundle b = new Bundle();
            msg.setData(b);

            try {
                while (true) {
                    connect_flag = false;
                    if (exit_activity)
                        return;  // 如果已经退出程序，则结束线程
                    //开始连接设备
                    Tools.mBleService.conectBle(mBluetoothDevice);

                    for (int j = 0; j < 50; j++) {
                        if (connect_flag) {
                            //如果在5秒内连接上了,就跳出循环
                            break;
                        }
                        sleep(100);
                    }
                    if (connect_flag) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            read_name_flag = false; // 读取设备名

            //获取到该Gatt中存在的service服务集合
            List<BluetoothGattService> services = Tools.mBleService.mBluetoothGatt
                    .getServices();

            if (services.size() == 0) {
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    //未发现任何的服务service,发出消息
                    readNameFail.sendEmptyMessage(0);
                }
                return;
            }
            String uuid;
            //走到这一步,开始读取通道信息
            b.putString("msg", "读取通道信息");
            reflashDialogMessage.sendMessage(msg);

            //遍历服务集合,服务名作为一级条目,该服务中存在的特征值作为二级条目
            for (BluetoothGattService service : services) {
                //获取服务的uuid表示
                uuid = service.getUuid().toString().trim();
                //仅显示我们需要的service
                if (!uuid.equalsIgnoreCase(Constant.TARGET_SERVICE_UUID)) {
                    continue;
                }
                //获取到该服务中存在的特征值集合
                List<BluetoothGattCharacteristic> gattCharacteristics = service
                        .getCharacteristics();
                if (gattCharacteristics.size() == 0) {
                    //服务中没有特征值也跳过下面的操作
                    continue;
                }

                //遍历该服务中的特征值对象
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    //获取"特征值"对应的uuid号
                    uuid = gattCharacteristic.getUuid().toString();

                    //只获取我期望使用的"特征值"的对象
                    if (!uuid.equalsIgnoreCase(Constant.TARGET_CHARACTERISTIC_UUID)) {
                        continue;
                    }
                    //Log.d(TAG,"我期望的Characteristic的uuid号为："+uuid);
                    //这样就获取到了我想要的"特征值"对象
                    mGattCharacteristic = gattCharacteristic;

                    // 查看是有什么权限
                    proper = mGattCharacteristic.getProperties();

                    //下面的操作是用来设置"通知权限"的
                    if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
                            || (0 != (proper & BluetoothGattCharacteristic.PROPERTY_INDICATE))) { // 通知
                        //Receiving GATT Notifications
                        //让应用可以收到GATT的通知
                        Tools.mBleService.mBluetoothGatt.setCharacteristicNotification(
                                mGattCharacteristic, true);
                        //获取该"特征值"的描述可操作对象
                        BluetoothGattDescriptor descriptor = mGattCharacteristic
                                .getDescriptor(UUID
                                        .fromString("00002902-0000-1000-8000-00805f9b34fb"));
                        descriptor
                                .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        Tools.mBleService.mBluetoothGatt.writeDescriptor(descriptor);

                        //Once notifications are enabled for a characteristic,
                        //an onCharacteristicChanged() callback is triggered if
                        // the characteristic changes on the remote device

                    }

                    /*BluetoothGattDescriptor descriptor = gattCharacteristic
                            .getDescriptor(UUID
                                    .fromString("00002901-0000-1000-8000-00805f9b34fb"));*/
                    //根据descriptor对象来设置"特征名"
                    /*if (null != descriptor) {
                        read_name_flag = false;
                        Tools.mBleService.mBluetoothGatt.readDescriptor(descriptor);
                        while (!read_name_flag) {// 等待读取完成
                            if (exit_activity || bind_flag){
                                bind_flag = false;
                                return; // 读取超时，结束线程
                            }
                        }
                        try {
                            child_data.put("name",
                                    new String(descriptor.getValue(), "GB2312"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        child_data.put("name", SampleGattAttributes.lookup(uuid, "unknow"));
                    }*/
                    //获取并设置操作权限名
                    /*String pro = "";
                    if (0 != (gattCharacteristic.getProperties() &
                            BluetoothGattCharacteristic.PROPERTY_READ)) { // 可读
                        pro += "可读,";
                    }
                    if ((0 != (gattCharacteristic.getProperties() &
                            BluetoothGattCharacteristic.PROPERTY_WRITE)) ||
                            (0 != (gattCharacteristic.getProperties()
                                    & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE))) { // 可写
                        pro += "可写,";
                    }
                    if ((0 != (gattCharacteristic.getProperties() &
                            BluetoothGattCharacteristic.PROPERTY_NOTIFY)) ||
                            (0 != (gattCharacteristic.getProperties() &
                                    BluetoothGattCharacteristic.PROPERTY_INDICATE))	) { // 通知
                        pro += "可通知";
                    }
                   */
                }
            }
            //服务查询完毕后的操作
            dis_services_handl.sendEmptyMessage(0);
        }
    }

    /**
     * 下面的几个方法是用来对某些重要信号进行处理的操作
     */
    //发现服务后的处理方法
    private Handler dis_services_handl = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pd.dismiss();
        }
    };

    //读取信息失败后的处理方法
    private Handler readNameFail = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //关闭连接
            Tools.mBleService.disConectBle();
            //重新读取名称信息
            new readNameThread().start();
        }
    };

    //连接失败后的处理方法
    private Handler connect_fail_handl = new Handler() {
        public void handleMessage(Message msg) {
            Tools.mBleService.disConectBle();
            Toast.makeText(getApplicationContext(), "连接失败",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    };

    //下面的方法是对话框实时显示进度情况的方法
    private ProgressDialog pd;
    private Handler reflashDialogMessage = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            pd.setMessage(b.getString("msg"));
        }
    };

    @Override
    public void showSigninSuccess() {
        //进行ui更新
        MyBean myBean = new MyBean(Sno, Cno);
        mMyBeanList.add(myBean);
        mDeviceLinkAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCheckResult() {
        //今天还未签到,接着执行签到操作
        mPresenter.signinDeal(Sno,Cno);
    }

    @Override
    public void querySnoSuccess(String sno) {
        Sno=sno;//获取到学号
        //开始查询今天是否已经签到过
        mPresenter.checkSignInRecord(Sno,Cno);
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
        SnackbarUtil.showShort(mRv,msg);
    }

    /**
     * 下面的方法时复写activity的一些生命周期方法
     **/
    /*活动销毁后,记得关闭连接和取消蓝牙监听*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.mBleService.disConectBle();
        exit_activity = true;
        unregisterReceiver(bluetoothReceiver);
        if (!Tools.mBleService.isConnected()) {
            return;
        }
        //如果当前还连接着,去掉"可通知"的权限
        if (0 != (proper & 0x10)) {
            Tools.mBleService.mBluetoothGatt.setCharacteristicNotification(
                    mGattCharacteristic, false);
        }
    }

    /*恢复交互时进行连接状态提示*/
    @Override
    protected void onResume() {
        super.onResume();
        if (Tools.mBleService.isConnected()) {
            Toast.makeText(this, "已连接", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "已断开", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
