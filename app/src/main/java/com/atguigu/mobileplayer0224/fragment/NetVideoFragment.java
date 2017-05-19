package com.atguigu.mobileplayer0224.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.base.BaseFragment;

/**
 * 作者：田学伟 on 2017/5/19 11:47
 * QQ：93226539
 * 作用：网络视频
 */

public class NetVideoFragment extends BaseFragment{
    private TextView textView;
    @Override
    public View initView() {
        Log.e("TAG","网络视频ui初始化了。。");
        textView = new TextView(mContext);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG","网络视频数据初始化了。。");
        textView.setText("网络视频");
    }
}

