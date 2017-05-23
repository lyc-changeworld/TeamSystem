package com.example.achuan.teamsystem.presenter;


import com.example.achuan.teamsystem.base.RxPresenter;
import com.example.achuan.teamsystem.model.bean.Card;
import com.example.achuan.teamsystem.model.bean.CheckInRecord;
import com.example.achuan.teamsystem.model.http.BmobHelper;
import com.example.achuan.teamsystem.presenter.contract.CardSigninContract;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by achuan on 16-11-12.
 * 功能：签到界面的主持者方法逻辑具体实现
 */
public class CardSigninPresenter extends RxPresenter<CardSigninContract.View> implements CardSigninContract.Presenter {


    @Override
    public void getSno(String Cnum) {
        mView.showLoading("正在查询学号,请稍候...");
        BmobHelper.getInstance().cardQuery(Cnum).findObjects(new FindListener<Card>() {
            @Override
            public void done(List<Card> list, BmobException e) {
                mView.hideLoading();
                if(e==null){
                    if(list.size()>0){
                        mView.querySnoSuccess(list.get(0).getSno());
                    }else {
                        mView.showError("查询为空ヽ(≧Д≦)ノ");
                    }
                }else {
                    mView.showError("查询学号失败ヽ(≧Д≦)ノ");
                }
            }
        });
    }

    @Override
    public void checkSignInRecord(String Sno, String Cno) {
        mView.showLoading("正在查询签到记录,请稍候...");
        //参数：学号，课程号
        BmobHelper.getInstance().checkSignInRecord(Sno,Cno).findObjects(new FindListener<CheckInRecord>() {
            @Override
            public void done(List<CheckInRecord> list, BmobException e) {
                mView.hideLoading();
                if(e==null){
                    if(list.size()>0){
                        mView.showError("今天已经签过到了(≖ ‿ ≖)✧");
                    }else {
                        mView.showCheckResult();
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
                            mView.showError("刷卡签到成功(∩_∩)");
                            mView.showSigninSuccess();
                        }else{
                            mView.showError("刷卡签到失败ヽ(≧Д≦)ノ");
                        }
                    }
                });
    }
}
