package com.example.achuan.teamsystem.presenter;

import android.widget.Toast;

import com.example.achuan.teamsystem.app.App;
import com.example.achuan.teamsystem.base.RxPresenter;
import com.example.achuan.teamsystem.model.bean.Course;
import com.example.achuan.teamsystem.model.http.BmobHelper;
import com.example.achuan.teamsystem.presenter.contract.SigninCourseContract;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by achuan on 16-11-3.
 * 功能：Presenter操作者的具体方法逻辑实现
 */
public class SigninPresenter extends RxPresenter<SigninCourseContract.View>
        implements SigninCourseContract.Presenter {


    //接口实现网络端全部课程数据的加载
    @Override
    public void getCourseData() {
        BmobHelper.getInstance().courseQueryAll().findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if(e==null){
                    mView.showContent(list);
                }else{
                    mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                    //LogUtil.d("lyc-bmob",e.getMessage());
                }
            }
        });
    }
    //接口实现根据关键字进行模糊查询数据的方法
    @Override
    public void getSearchCourseData(String keyword) {
        BmobHelper.getInstance().courseQueryFromKeyword(keyword).findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if(e==null){
                    mView.showContent(list);
                }else{
                    //LogUtil.d("lyc-bmob",e.getMessage());
                    Toast.makeText(App.getInstance().getContext(),
                            "课程加载失败"+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
