package com.example.achuan.teamsystem.presenter.contract;


import com.example.achuan.teamsystem.base.BasePresenter;
import com.example.achuan.teamsystem.base.BaseView;
import com.example.achuan.teamsystem.model.bean.Course;

import java.util.List;

/**
 * Created by achuan on 16-11-3.
 * 功能：展示课程界面的view和model层的方法
 */
public interface SigninCourseContract {
    //view层接口方法
    interface View extends BaseView {
        //显示和隐藏进度条
        void showLoading(String message);
        void hideLoading();
        //显示列表内容的方法
        void showContent(List<Course> mList);
    }
    //presenter层接口方法
    interface  Presenter extends BasePresenter<View> {
        //获取网络端全部的课程数据
        void getCourseData();
        //根据关键字获取数据集合
        void getSearchCourseData(String keyword);
    }
}
