package com.example.achuan.teamsystem.presenter.contract;


import com.example.achuan.teamsystem.base.BasePresenter;
import com.example.achuan.teamsystem.base.BaseView;
import com.example.achuan.teamsystem.model.bean.MyUser;

/**
 * Created by achuan on 16-11-13.
 */
public interface MySelfContract {
    //view层接口方法
    interface View extends BaseView {
        //显示当前用户的信息
        void showNetUserContent(MyUser myUser);//有网时显示
        void showLocalUserContent();//无网时显示
        //显示和隐藏进度条
        void showLoading(String message);
        void hideLoading();
        //上传成功,传入后台的图片链接地址,并保存到用户信息中
        //void showUploadFileSuccess(String headUri);

    }
    //presenter层接口方法
    interface  Presenter extends BasePresenter<View> {
        //根据用户名获取用户对象
        void getUserObject(String userName);
        //根据键值更新用户信息
        void updateUserInfoByKey(String id, String key, Object value);
        //(上传|删除|下载)图片
        //void uploadFile(BmobFile bmobFile);
        //void deleteFile(String headUrl);
    }

}
