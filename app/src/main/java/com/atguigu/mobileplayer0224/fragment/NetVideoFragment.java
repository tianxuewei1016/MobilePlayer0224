package com.atguigu.mobileplayer0224.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.activity.SystemVideoPlayerActivity;
import com.atguigu.mobileplayer0224.adapter.NetVideoAdapter;
import com.atguigu.mobileplayer0224.base.BaseFragment;
import com.atguigu.mobileplayer0224.bean.MediaItem;
import com.atguigu.mobileplayer0224.utils.CacheUtils;
import com.atguigu.mobileplayer0224.utils.Constant;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 作者：田学伟 on 2017/5/19 11:47
 * QQ：93226539
 * 作用：网络视频
 */

public class NetVideoFragment extends BaseFragment {

    private ArrayList<MediaItem> mediaItems;
    private NetVideoAdapter adapter;

    private ListView lv;
    private TextView tv_nodata;
    private MaterialRefreshLayout refresh;
    /**
     * 是否加载更多
     */
    private boolean isLoadMore = false;

    //重写视图
    @Override
    public View initView() {
        Log.e("TAG", "NetVideoPager-initView");
        View view = View.inflate(mContext, R.layout.fragment_net_video_pager, null);
        x.view().inject(NetVideoFragment.this, view);

        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //传递列表数据
                Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);

                Bundle bundle = new Bundle();
                //列表数据
                bundle.putSerializable("videolist", mediaItems);
                intent.putExtras(bundle);
                //传递点击的位置
                intent.putExtra("position", position);
                startActivity(intent);

            }
        });
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                isLoadMore = false;
                getDataFromNet();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                isLoadMore = true;
                getDataFromNet();
            }
        });

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "NetVideoPager-initData");
        String json = CacheUtils.getString(mContext, Constant.NET_URL);
        if (!TextUtils.isEmpty(json)) {
            processData(json);
        }
        getDataFromNet();
    }

    private void getDataFromNet() {

        //配置联网请求地址
        //RequestParams request = new RequestParams(Constant.NET_URL);
        RequestParams request = new RequestParams(Constant.NET_WORK_VIDEO);

        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Log.e("TAG", "xUtils联网成功==" + result);
                CacheUtils.putString(mContext,Constant.NET_WORK_VIDEO, result);
                processData(result);

                if (!isLoadMore) {
                    //完成刷新
                    refresh.finishRefresh();
                } else {
                    //把上拉隐藏
                    refresh.finishRefreshLoadMore();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils联网失败==" + ex.getMessage());
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
     *
     * @param json
     */
    private void processData(String json) {
//        第一种方式使用Gson解析,但是网络视频不能实现上一个下一个,需要用手动的解析
        if (!isLoadMore) {
            mediaItems = parsedJson(json);
            if (mediaItems != null && mediaItems.size() > 0) {
                tv_nodata.setVisibility(View.GONE);
                //有数据-适配器
                adapter = new NetVideoAdapter(mContext, mediaItems);
                lv.setAdapter(adapter);
            } else {
                tv_nodata.setVisibility(View.VISIBLE);
            }
        } else {
            //加载更多
            ArrayList<MediaItem> mediaItem = parsedJson(json);
            mediaItems.addAll(mediaItem);
            //刷新适配器
            adapter.notifyDataSetChanged();//getCount-->getView
        }

    }

    /**
     * 使用系统的接口解析数据
     *
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("trailers");


            for (int i = 0; i < jsonArray.length(); i++) {

                MediaItem mediaItem = new MediaItem();
                mediaItems.add(mediaItem);//添加到集合中
                JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                String name = jsonObjectItem.optString("movieName");
                mediaItem.setName(name);
                String desc = jsonObjectItem.optString("videoTitle");
                mediaItem.setDesc(desc);
                String url = jsonObjectItem.optString("url");
                mediaItem.setData(url);
                String hightUrl = jsonObjectItem.optString("hightUrl");
                mediaItem.setHeightUrl(hightUrl);
                String coverImg = jsonObjectItem.optString("coverImg");
                mediaItem.setImageUrl(coverImg);
                int videoLength = jsonObjectItem.optInt("videoLength");
                mediaItem.setDuration(videoLength);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }


}

