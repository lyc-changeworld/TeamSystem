package com.example.achuan.teamsystem.presenter;


import com.example.achuan.teamsystem.base.RxPresenter;
import com.example.achuan.teamsystem.model.http.BmobHelper;
import com.example.achuan.teamsystem.presenter.contract.SigninDetailContract;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by achuan on 16-11-12.
 * 功能：签到界面的主持者方法逻辑具体实现
 */
public class SigninDetailPresenter extends RxPresenter<SigninDetailContract.View> implements SigninDetailContract.Presenter {


    //获取该课程对应的老师的数据
    /*@Override
    public void getTeacherData() {
        BmobHelper.getInstance().teacherBmobQueryAll().findObjects(new FindListener<TeacherBean>() {
            @Override
            public void done(List<TeacherBean> list, BmobException e) {
                if(e==null){
                    mView.showContent(list);
                }else{
                    mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                    //LogUtil.d("lyc-bmob",e.getMessage());
                }
            }
        });
    }*/
    //签到处理
    @Override
    public void signinDeal(String Sno, String Cno) {
        //参数：学号，课程号
        BmobHelper.getInstance().signinDetailSave(Sno,Cno).
                save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            //显示签到成功
                            mView.showSigninSuccess("签到成功(∩_∩)");
                        }else{
                            mView.showError("签到失败ヽ(≧Д≦)ノ");
                        }
                    }
                });
    }
}
