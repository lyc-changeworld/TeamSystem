package com.example.achuan.teamsystem.ui.user.signin.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.app.Constant;
import com.example.achuan.teamsystem.base.MvpFragment;
import com.example.achuan.teamsystem.model.bean.Course;
import com.example.achuan.teamsystem.presenter.SigninCoursePresenter;
import com.example.achuan.teamsystem.presenter.contract.SigninCourseContract;
import com.example.achuan.teamsystem.ui.admin.ble.activity.DeviceScanActivity;
import com.example.achuan.teamsystem.ui.admin.main.activity.AdminMainActivity;
import com.example.achuan.teamsystem.ui.user.main.activity.UserMainActivity;
import com.example.achuan.teamsystem.ui.user.signin.activity.SigninDetailActivity;
import com.example.achuan.teamsystem.ui.user.signin.adapter.SigninCourseAdapter;
import com.example.achuan.teamsystem.util.DialogUtil;
import com.example.achuan.teamsystem.util.SnackbarUtil;
import com.example.achuan.teamsystem.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 16-11-3.
 * 功能：课程界面（碎片）
 */
public class SigninFragment extends MvpFragment<SigninCoursePresenter> implements SigninCourseContract.View {

    private final static String TAG="SigninFragment";

    @BindView(R.id.id_recyclerView)
    RecyclerView mIdRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.sideBar)
    SideBar mSideBar;
    @BindView(R.id.tv_hint)
    TextView mTvHint;

    Context mContext;//上下文变量
    private SigninCourseAdapter mCourseAdapter;//列表适配器
    private List<Course> mCourseBeanList;//数据集合引用变量
    private LinearLayoutManager linearManager;


    @Override
    protected SigninCoursePresenter createPresenter() {
        return new SigninCoursePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_signin_course;
    }

    @Override
    protected void initEventAndData() {
        mContext=getActivity();
        //创建集合实例对象
        mCourseBeanList = new ArrayList<>();
        //初始化适配器数据绑定
        mCourseAdapter = new SigninCourseAdapter(getActivity(), mCourseBeanList);
        //设置相关布局管理
        linearManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);//设置布局方式为线性居中布局
        mIdRecyclerView.setLayoutManager(linearManager);
        //为列表控件配置适配器
        mIdRecyclerView.setAdapter(mCourseAdapter);

        //初始化获取网络数据
        mPresenter.getCourseData();

        //设置颜色渐变的刷新条
        mSwipeRefresh.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        //设置下拉刷新处理
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getCourseData();//重新获取列表显示数据
            }
        });

        /***设置滑动监听事件,后续再具体实现相关功能***/
        mIdRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE)//滑动停止的状态,加载数据
                {
                    //将指定范围中的图片加载显示出来
                    //mImageLoader.loadImages(mStart,mEnd);
                } else {//其他状态停止加载数据
                    //mImageLoader.cancelAllTasks();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // dy :正  列表向上划动
                // dy :负  列表向下划动 上下滑动时dx一直为正（水平方向）
                //当前列表界面最上面的item的序号（小号） 序号从0开始
                /*mStart=((LinearLayoutManager) recyclerView
                .getLayoutManager()).findFirstVisibleItemPosition();
                //当前列表界面最下面的item的序号（大号）　
                mEnd = ((LinearLayoutManager) recyclerView
                        .getLayoutManager()).findLastVisibleItemPosition();
                //int mCurrent=recyclerView.getLayoutManager().
                /*//***第一次显示列表时先加载第一屏的图片***//**//**//**//*
                if(mFirstIn){
                    mFirstIn=false;//已经不是第一次加载列表
                    mImageLoader.loadImages(mStart,mEnd);
                }*/
            }
        });

        /***设置索引栏点击监听事件,实现点击字母实现列表栏定位移动***/
        mSideBar.setTextView(mTvHint);//添加中间部分显示控件
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnChooseLetterChangedListener() {
            @Override
            public void onChooseLetter(String s) {
                int i = mCourseAdapter.getFirstPositionByChar(s.charAt(0));
                //i==-1代表头字母为s的数据不存在,不执行任何操作
                if (i == -1) {
                    return;
                }
                //列表滚动跳转到指定的位置
                linearManager.scrollToPositionWithOffset(i, 0);
            }
            @Override
            public void onNoChooseLetter() {

            }
        });

        /***设置item的点击监听事件***/
        mCourseAdapter.setOnItemClickListener(new SigninCourseAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Course course) {
                Intent intent = null;
                Activity activity = (Activity)mContext;
                //根据不同的用户权限进行跳转
                if(activity instanceof AdminMainActivity){
                    //跳转到蓝牙设备列表界面
                    intent=new Intent(mContext, DeviceScanActivity.class);
                }else if(activity instanceof UserMainActivity){
                    //跳转到签到界面
                    intent=new Intent(mContext, SigninDetailActivity.class);
                }
                intent.putExtra(Constant.TITLE,course.getCname());
                intent.putExtra(Constant.CNO,course.getCno());//课程号
                mContext.startActivity(intent);
            }
        });
    }

    //将后台加载好的数据进行适配显示
    @Override
    public void showContent(List<Course> mList) {
        //如果在进行下拉刷新,则停止
        if (mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(false);
        }
        mCourseBeanList.clear();//清空数据
        mCourseBeanList.addAll(mList);//更新集合数据
        /*让数组中的数据按照compareTo方法中的规则返回的结果进行排序*/
        Collections.sort(mCourseBeanList);
        mCourseAdapter.notifyDataSetChanged();//刷新列表显示内容

        //关闭加载窗口
        if (!getActivity().isFinishing() && DialogUtil.isProgressDialogShowing()) {
            DialogUtil.closeProgressDialog();
        }

        /*if(EMClient.getInstance().groupManager().getAllGroups().size()==0){
            for (int i = 0; i <mList.size() ; i++) {
                Course course=mList.get(i);
                final String groupName=course.getCname();
                final String desc="开始神奇的“"+groupName+"”课程学习吧！";
                final EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                option.maxUsers=200;//设置群组最大用户数(默认200)
                option.style=EMGroupStylePublicOpenJoin;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EaseMobHelper.getInstance().createGroup(groupName, desc,new String[10], new String(""), option, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //回到主线程进行UI更新
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(DialogUtil.isProgressDialogShowing()){
                                            DialogUtil.closeProgressDialog();
                                        }
                                    }
                                });
                            }
                            @Override
                            public void onError(int code, final String error) {
                                //回到主线程进行UI更新
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(DialogUtil.isProgressDialogShowing()){
                                            DialogUtil.closeProgressDialog();
                                        }
                                        //对异常进行提示
                                        Toast.makeText(mContext,
                                                getString(R.string.Failed_to_create_groups)+
                                                        error,
                                                Toast.LENGTH_LONG).show();
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
        }*/
    }

    /*//根据关键字进行数据查询
    public void doSearch(String query) {
        mPresenter.getSearchCourseData(query);
    }*/

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
        //如果在进行下拉刷新,则停止
        if (mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(false);
        }
        SnackbarUtil.showShort(mIdRecyclerView,msg);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
