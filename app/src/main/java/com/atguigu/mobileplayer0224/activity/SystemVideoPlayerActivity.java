package com.atguigu.mobileplayer0224.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.mobileplayer0224.R;

//一般有上下左右的用相对布局或者帧布局
public class SystemVideoPlayerActivity extends AppCompatActivity {
    /**
     * 视频的本质是连续的画面,在加上声音,形成电影
     * VideoView简介:
     * VideoView继承SurfaceView,内部封装MediaPlayer,
     * 可以用来显示一秒切换很多图片的行为--视频的播放
     * 封装了MediaPlayer就可以播放视频和音频
     * <p>
     * SurfaceView:使用的双缓冲技术,它在子线程中绘制图片,这样不会阻塞主线程,
     * 适合游戏开发和视频的播放的显示
     * <p>
     * MediaPlayer负责调用底层的C代码解码视频的声音和画面.
     * MediaPlayer有自己的生命周期,通过它可以播放网络和本地的视频音频.
     * 视频: .mp4,.3gp,.ts(系统自带的支持的)
     * 音频: .mp3,.ogg
     * <p>
     * 为什么调用C代码?
     * java代码的效率比较低,C代码是底层代码效率比较高,视频播放不卡顿
     */

    private VideoView vv;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        vv = (VideoView) findViewById(R.id.vv);

        //得到播放的地址
        uri = getIntent().getData();

        //设置播放器的三个监听:播放准备好的监听,播放完成的监听,播放出错的监听
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //底层准备播放完成的时候回调
            @Override
            public void onPrepared(MediaPlayer mp) {
                vv.start();
            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemVideoPlayerActivity.this, "播放出错了...", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            //如果是列表就播放下一个,如果是最后一个就退出当前页面
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(SystemVideoPlayerActivity.this, "视频播放完成", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //设置播放的地址
        vv.setVideoURI(uri);

        //设置控制面板
        vv.setMediaController(new MediaController(this));
    }
}
