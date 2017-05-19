package com.example.achuan.teamsystem.presenter.contract;


import com.example.achuan.teamsystem.base.BasePresenter;
import com.example.achuan.teamsystem.base.BaseView;

/**
 * Created by achuan on 16-11-11.
 * 功能：具体签到界面的功能方法
 */
public interface SigninDetailContract {
    //view层接口方法
    interface View extends BaseView {
        //显示列表内容的方法
        //void showContent(List<TeacherBean> mList);
        //显示签到成功
        void showSigninSuccess(String message);
    }
    //presenter层接口方法
    interface  Presenter extends BasePresenter<View> {
        //获取课程对应的任课老师的数据
        //void getTeacherData();
        //签到处理(学号，课程号)
        void signinDeal(String Sno, String Cno);
    }
}
