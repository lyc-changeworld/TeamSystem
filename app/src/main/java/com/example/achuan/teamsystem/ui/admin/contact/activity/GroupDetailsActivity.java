package com.example.achuan.teamsystem.ui.admin.contact.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.teamsystem.R;
import com.example.achuan.teamsystem.base.SimpleActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by achuan on 17-5-26.
 * 功能：公开群的简单群信息介绍
 */

public class GroupDetailsActivity extends SimpleActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_group_name)
    TextView mTvGroupName;
    @BindView(R.id.tv_admin)
    TextView mTvAdmin;
    @BindView(R.id.tv_introduction)
    TextView mTvIntroduction;
    @BindView(R.id.btn_add_to_group)
    Button mBtnAddToGroup;


    private String groupId;
    private EMGroup group;
    Context mContext;

    @Override
    protected int getLayout() {
        return R.layout.em_activity_group_simple_details;
    }

    @Override
    protected void initEventAndData() {
        mContext = this;
        //设置toolbar
        setToolBar(mToolbar, "群聊资料", true);
        //获取上一个activity传递过来的群数据
        EMGroupInfo groupInfo = (EMGroupInfo) getIntent().getSerializableExtra("groupinfo");

        if (groupInfo != null) {
            //groupName = groupInfo.getGroupName();
            groupId = groupInfo.getGroupId();
            new Thread(new Runnable() {
                public void run() {
                    //get detail from server
                    try {
                        group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                showGroupDetail();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(mContext,
                                        getString(R.string.Failed_to_get_group_chat_information),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                }
            }).start();
        } else {
            finish();
            return;
        }
    }

    /*显示群资料的方法*/
    private void showGroupDetail() {
        mTvGroupName.setText(group.getGroupName());
        mTvAdmin.setText(group.getOwner());
        mTvIntroduction.setText(group.getDescription());

        if(!group.getMembers().contains(EMClient.getInstance().getCurrentUser())){
            //如果当前用户并不在该群中
            mBtnAddToGroup.setBackgroundResource(R.drawable.btn_login_enable_shape);
            mBtnAddToGroup.setEnabled(true);
        }else {
            mBtnAddToGroup.setText("你已经加入该群");
        }
    }

    @OnClick({R.id.btn_add_to_group})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_to_group:
                addToGroup();
                break;
            default:break;
        }
    }

    private void addToGroup() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    //if group is membersOnly，you need apply to join
                    if(group.isMemberOnly()){
                        //需要申请和验证才能加入的，即group.isMembersOnly()为true，调用下面方法
                        EMClient.getInstance().groupManager().applyJoinToGroup(groupId, getString(R.string.Request_to_join));
                    }else{
                        //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
                        EMClient.getInstance().groupManager().joinGroup(groupId);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(group.isMemberOnly()){
                                //需要验证
                                Toast.makeText(mContext, getString(R.string.send_the_request_is), Toast.LENGTH_SHORT).show();
                            }else {
                                //不用验证
                                Toast.makeText(mContext, getString(R.string.Join_the_group_chat), Toast.LENGTH_SHORT).show();
                                mBtnAddToGroup.setEnabled(false);
                            }
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mContext,
                                    getString(R.string.Failed_to_join_the_group_chat)+e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}
