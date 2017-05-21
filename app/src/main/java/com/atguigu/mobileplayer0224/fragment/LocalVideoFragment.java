package com.atguigu.mobileplayer0224.fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.activity.SystemVideoPlayerActivity;
import com.atguigu.mobileplayer0224.adapter.LocalVideoAdapter;
import com.atguigu.mobileplayer0224.base.BaseFragment;
import com.atguigu.mobileplayer0224.bean.MediaItem;

import java.util.ArrayList;

/**
 * 作者：田学伟 on 2017/5/19 11:47
 * QQ：93226539
 * 作用：本地视频
 */

public class LocalVideoFragment extends BaseFragment {

    private ListView listview;
    private TextView tv_no_media;
    private ArrayList<MediaItem> mediaItems;
    private LocalVideoAdapter adapter;

    //判断有没有数据
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据,文本隐藏
                tv_no_media.setVisibility(View.GONE);
                adapter = new LocalVideoAdapter(mContext, mediaItems, true);
                //设置适配器
                listview.setAdapter(adapter);
            } else {
                //没有数据,文本显示
                tv_no_media.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public View initView() {
        Log.e("TAG", "本地视频ui初始化了。。");
        View view = View.inflate(mContext, R.layout.fragment_local_video, null);
        //初始化
        listview = (ListView) view.findViewById(R.id.listview);
        tv_no_media = (TextView) view.findViewById(R.id.tv_no_media);

        //设置listview的item的监听
        listview.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            MediaItem mediaItem = mediaItems.get(position);
            //MediaItem item = adapter.getItem(position);
            // Toast.makeText(mContext, "" + item.toString(), Toast.LENGTH_SHORT).show();
            /**
             * 调用系统的播放器播放视频
             */
//            Intent intent = new Intent(mContext,SystemVideoPlayerActivity.class);
//            intent.setDataAndType(Uri.parse(item.getData()), "video/*");
//            startActivity(intent);

            //传递视频列表过去
            Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);

            Bundle bunlder = new Bundle();
            bunlder.putSerializable("videolist", mediaItems);
            intent.putExtra("position", position);
            //放入Bundler
            intent.putExtras(bunlder);
            startActivity(intent);

        }
    }

    /**
     * 当子类需要：
     * 1.联网请求网络，的时候重写该方法
     * 2.绑定数据
     */
    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "本地视频数据初始化了。。");
        //加载本地所有的视频
        getData();
    }

    /**
     * 得到数据
     */
    /**
     * 得到数据
     */
    private void getData() {
        new Thread() {
            public void run() {
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频在sdcard上的名称
                        MediaStore.Video.Media.DURATION,//视频时长
                        MediaStore.Video.Media.SIZE,//视频文件的大小
                        MediaStore.Video.Media.DATA//视频播放地址
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //里面也可以是0
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        //里面也可以是1
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        //里面也可以是2
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        //里面也可以是3
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        Log.e("TAG", "name==" + name + ",duration==" + duration + ",data===" + data);

                        mediaItems.add(new MediaItem(name, duration, size, data));


                    }

                    cursor.close();
                }

                //使用handler
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
}
