package com.example.achuan.teamsystem.presenter.contract;


import com.example.achuan.teamsystem.base.BasePresenter;
import com.example.achuan.teamsystem.base.BaseView;

/**
 * Created by achuan on 16-11-11.
 * 功能：具体签到界面的功能方法
 */
public interface CardSigninContract {
    //view层接口方法
    interface View extends BaseView {
        //显示和隐藏进度条
        void showLoading(String message);
        void hideLoading();
        //查询学号成功
        void querySnoSuccess(String Sno);
        //显示签到记录查询结果
        void showCheckResult();
        //显示签到成功
        void showSigninSuccess();
    }
    //presenter层接口方法
    interface  Presenter extends BasePresenter<View> {
        //通过卡号查询获得学号
        void getSno(String Cnum);
        //对签到记录进行查询(是否重复)
        void checkSignInRecord(String Sno, String Cno);
        //签到处理(学号，课程号),存储到后台服务器
        void signinDeal(String Sno, String Cno);
    }
}
