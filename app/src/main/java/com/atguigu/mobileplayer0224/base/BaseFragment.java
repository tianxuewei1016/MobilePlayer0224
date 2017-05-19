package com.atguigu.mobileplayer0224.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：田学伟 on 2017/5/19 11:37
 * QQ：93226539
 * 作用：
 */

public abstract class BaseFragment extends Fragment {

    public Context mContext;

    /**
     * 当Fragment创建的时候回调
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MainActivity
        mContext = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();//得到视图
    }

    /**
     * 由子类实现,写不同的布局,显示不同的效果
     *
     * @return
     */
    protected abstract View initView();

    /**
     * 当依付Activity被创建的时候被回调
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();//在得到视图的基础上,绑定数据
    }

    /**
     * 当子类需要：
     * 1.联网请求网络，的时候重写该方法
     * 2.绑定数据
     */
    public void initData() {

    }
}
