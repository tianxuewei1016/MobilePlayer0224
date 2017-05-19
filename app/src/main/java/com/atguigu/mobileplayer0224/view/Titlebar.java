package com.atguigu.mobileplayer0224.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileplayer0224.R;

/**
 * 作者：田学伟 on 2017/5/19 10:55
 * QQ：93226539
 * 作用：自定义标题栏
 */

public class Titlebar extends LinearLayout implements View.OnClickListener {

    private final Context context;
    private TextView tv_search;
    private RelativeLayout rl_game;
    private ImageView iv_record;

    public Titlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 当布局加载完成后回调该方法
     * 得到孩子的实例
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_search = (TextView) getChildAt(1);
        rl_game = (RelativeLayout) getChildAt(2);
        iv_record = (ImageView) getChildAt(3);
        //设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record:
                Toast.makeText(context, "记录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
