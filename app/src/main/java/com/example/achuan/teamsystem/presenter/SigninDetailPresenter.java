package com.example.achuan.teamsystem.presenter;


import com.example.achuan.teamsystem.base.RxPresenter;
import com.example.achuan.teamsystem.model.bean.CheckInRecord;
import com.example.achuan.teamsystem.model.http.BmobHelper;
import com.example.achuan.teamsystem.presenter.contract.SigninDetailContract;
import com.example.achuan.teamsystem.util.DateUtil;

import java.text.ParseException;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by achuan on 16-11-12.
 * 功能：签到界面的主持者方法逻辑具体实现
 */
public class SigninDetailPresenter extends RxPresenter<SigninDetailContract.View> implements SigninDetailContract.Presenter {

    @Override
    public void checkSignInRecord(String Sno, String Cno) {
        mView.showLoading("正在查询记录,请稍候...");
        //参数：学号，课程号
        BmobHelper.getInstance().checkSignInRecord(Sno,Cno).findObjects(new FindListener<CheckInRecord>() {
            @Override
            public void done(List<CheckInRecord> list, BmobException e) {
                mView.hideLoading();
                if(e==null){
                    for (int i = 0; i < list.size(); i++) {
                        CheckInRecord check=list.get(i);
                        try {
                            if(DateUtil.IsToday(check.getCreatedAt())){
                                mView.showCheckResult();
                                mView.showError("今天已经签过到了(≖ ‿ ≖)✧");
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }else{
                    mView.showError("签到记录查询失败ヽ(≧Д≦)ノ");
                }
            }
        });
    }

    //签到处理
    @Override
    public void signinDeal(String Sno, String Cno) {
        mView.showLoading("正在签到,请稍候...");
        //参数：学号，课程号
        BmobHelper.getInstance().signinDetailSave(Sno,Cno).
                save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        mView.hideLoading();
                        if(e==null){
                            //显示签到成功
                            mView.showSigninSuccess();
                            mView.showError("签到成功(∩_∩)");
                        }else{
                            mView.showError("签到失败ヽ(≧Д≦)ノ");
                        }
                    }
                });
    }
}
