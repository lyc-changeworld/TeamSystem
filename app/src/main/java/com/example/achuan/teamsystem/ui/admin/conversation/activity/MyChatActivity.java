package com.example.achuan.teamsystem.ui.admin.conversation.activity;


import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.base.SimpleActivity;
import com.example.achuan.teamsystem.ui.admin.conversation.fragment.MyChatFragment;

/**
 * Created by achuan on 17-3-17.
 * 功能:在官方例程的基础上扩展实现自己的聊天界面
 */

public class MyChatActivity extends SimpleActivity {

    private MyChatFragment mMyChatFragment;
    String toChatUsername;

    @Override
    protected int getLayout() {
        return R.layout.my_activity_chat;
    }

    @Override
    protected void initEventAndData() {

        //get user id or group id
        toChatUsername = getIntent().getExtras().getString("userId");
        //use EaseChatFratFragment
        mMyChatFragment=new MyChatFragment();
        //pass parameters to chat fragment
        mMyChatFragment.setArguments(getIntent().getExtras());

        //添加碎片到内容区域
        addFragment(R.id.container,mMyChatFragment);

    }
}
