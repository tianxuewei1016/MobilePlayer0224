package com.atguigu.mobileplayer0224.fragment;


import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.activity.SystemVideoPlayerActivity;
import com.atguigu.mobileplayer0224.adapter.NetVideoAdapter;
import com.atguigu.mobileplayer0224.base.BaseFragment;
import com.atguigu.mobileplayer0224.bean.MoveInfo;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * 作者：田学伟 on 2017/5/19 11:47
 * QQ：93226539
 * 作用：网络视频
 */

public class NetVideoFragment extends BaseFragment {

    private NetVideoAdapter adapter;

    private ListView lv;
    private TextView tv_nodata;
    //重写视图
    @Override
    public View initView() {
        Log.e("TAG","NetVideoPager-initView");
        View view = View.inflate(mContext, R.layout.fragment_net_video_pager,null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoveInfo.TrailersBean item = adapter.getItem(position);

                Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
                intent.setDataAndType(Uri.parse(item.getUrl()),"video/*");
                startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG","NetVideoPager-initData");
        getDataFromNet();
    }

    private void getDataFromNet() {

        //配置联网请求地址
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Log.e("TAG","xUtils联网成功=="+result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG","xUtils联网失败=="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 解析json数据和显示数据
     * @param json
     */
    private void processData(String json) {
        MoveInfo moveInfo = new Gson().fromJson(json, MoveInfo.class);
        List<MoveInfo.TrailersBean> datas = moveInfo.getTrailers();
        if(datas != null && datas.size() >0){
            tv_nodata.setVisibility(View.GONE);
            //有数据-适配器
            adapter = new NetVideoAdapter(mContext,datas);
            lv.setAdapter(adapter);
        }else{
            tv_nodata.setVisibility(View.VISIBLE);
        }

    }
}
