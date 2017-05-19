package com.example.achuan.teamsystem.presenter;


import com.example.achuan.teamsystem.base.RxPresenter;
import com.example.achuan.teamsystem.model.bean.MyUser;
import com.example.achuan.teamsystem.model.http.BmobHelper;
import com.example.achuan.teamsystem.presenter.contract.MySelfContract;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by achuan on 16-11-13.
 * 功能：
 */
public class MySelfPresenter extends RxPresenter<MySelfContract
        .View> implements MySelfContract.Presenter{


    //获取当前用户的实例操作对象
    @Override
    public void getUserObject(String userName) {
        mView.showLoading("正在加载用户信息,请稍候");
        BmobHelper.getInstance().userQuery(userName).findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                mView.hideLoading();
                if(e==null){
                    if(list.size()>0){
                        mView.showNetUserContent(list.get(0));
                    }else {
                        mView.showError("当前用户不存在ヽ(≧Д≦)ノ");
                    }
                }else {
                    //网络加载失败时,显示本地用户的缓存信息
                    mView.showLocalUserContent();
                    //mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                    mView.showError(e.getMessage());
                }
            }
        });
    }
    //更新用户的基本消息
    @Override
    public void updateUserInfoByKey(String id,String key, Object value) {
        /*开始更新,显示进度对话框*/
        mView.showLoading("正在修改,请稍后");
        //执行更新操作
        BmobHelper.getInstance().userUpdate(key,value).update(id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                //关闭进度对话框
                mView.hideLoading();
                if(e==null){
                }else {
                    //mView.showError("数据更新失败ヽ(≧Д≦)ノ");
                    mView.showError(e.getMessage());
                    //LogUtil.e("lyc-changeworld",e.getMessage());
                    //出现错误：User cannot be altered without sessionToken Error的原因
                    //用户在别的地方登录导致sessionToken发生了改变
                    /*可以根据sessionToken来进行异地登录的监控和强行退出账号的功能*/
                }
            }
        });
    }
    /*//上传头像到后台服务器端
    @Override
    public void uploadFile(final BmobFile bmobFile) {
        mView.showLoading("正在上传图片,请稍候");
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                mView.hideLoading();
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    //LogUtil.d("lyc-changeworld",bmobFile.getFileUrl());
                    //mView.(把后台图片的地址的链接传递给vie层,在让presenter进行更新处理)
                    mView.showUploadFileSuccess(bmobFile.getFileUrl());
                }else{
                    mView.showError(e.getMessage());
                }
            }
        });
    }
    //删除历史存储的头像
    @Override
    public void deleteFile(String headUrl) {
        BmobHelper.getInstance().fileBmobDelete(headUrl).delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                }else{
                    mView.showError(e.getMessage());
                }
            }
        });
    }*/

}
