package com.atguigu.mobileplayer0224.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.activity.SystemAudioPlayerActivity;
import com.atguigu.mobileplayer0224.adapter.LocalVideoAdapter;
import com.atguigu.mobileplayer0224.base.BaseFragment;
import com.atguigu.mobileplayer0224.bean.MediaItem;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者：田学伟 on 2017/5/19 11:47
 * QQ：93226539
 * 作用：本地音乐
 */

public class LocalAudioFragment extends BaseFragment {

    @InjectView(R.id.listview)
    ListView listview;
    @InjectView(R.id.tv_no_media)
    TextView tvNoMedia;

    private LocalVideoAdapter adapter;

    private ArrayList<MediaItem>mediaItems;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaItems != null && mediaItems.size()>0) {
                //有数据文本隐藏
                tvNoMedia.setVisibility(View.GONE);
                adapter = new LocalVideoAdapter(mContext,mediaItems,false);
                listview.setAdapter(adapter);
            }else{
                //没有数据,文本显示
                tvNoMedia.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public View initView() {
        Log.e("TAG", "本地音乐ui初始化了。。");
        View view = View.inflate(mContext, R.layout.fragment_local_video, null);
        ButterKnife.inject(this, view);

        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //3.传递列表数据
            Intent intent = new Intent(mContext, SystemAudioPlayerActivity.class);

            //传递点击的位置
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "本地音乐数据初始化了。。");
        //在子线程中加载音乐
        getDataFromLocal();
    }

    /**
     * 在子线程中得到音乐
     */
    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                //初始化集合
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = mContext.getContentResolver();
                //sdcard 的视频路径
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//在sdcard显示的视频名称
                        MediaStore.Audio.Media.DURATION,//视频的时长,毫秒
                        MediaStore.Audio.Media.SIZE,//文件大小-byte
                        MediaStore.Audio.Media.DATA,//在sdcard的路径-播放地址
                        MediaStore.Audio.Media.ARTIST//艺术家
                };
                Cursor cusor = resolver.query(uri, objs, null, null, null);
                if (cusor != null) {

                    while (cusor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();

                        //添加到集合中
                        mediaItems.add(mediaItem);//可以

                        String name = cusor.getString(0);
                        mediaItem.setName(name);
                        long duration = cusor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cusor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cusor.getString(3);//播放地址
                        mediaItem.setData(data);
                    }
                    cusor.close();
                }
                //发消息-切换到主线程
                handler.sendEmptyMessage(2);
            }
        }.start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
