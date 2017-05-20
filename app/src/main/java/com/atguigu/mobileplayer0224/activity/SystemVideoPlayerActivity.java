package com.atguigu.mobileplayer0224.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.bean.MediaItem;
import com.atguigu.mobileplayer0224.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//一般有上下左右的用相对布局或者帧布局
public class SystemVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
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

    //视频进度更新
    private static final int PROGRESS = 0;
    private VideoView vv;
    private Uri uri;
    private ArrayList<MediaItem> mediaItems;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystetime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwichePlayer;
    private LinearLayout llBottom;
    private TextView tvCurrenttime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwichScreen;
    private Utils utils;
    private MyBroadCastReceiver receiver;
    /**
     * 视频列表的位置
     */
    private int position;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-20 11:02:04 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        vv = (VideoView) findViewById(R.id.vv);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystetime = (TextView) findViewById(R.id.tv_systetime);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwichePlayer = (Button) findViewById(R.id.btn_swiche_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrenttime = (TextView) findViewById(R.id.tv_currenttime);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwichScreen = (Button) findViewById(R.id.btn_swich_screen);

        btnVoice.setOnClickListener(this);
        btnSwichePlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwichScreen.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-20 11:02:04 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {

        } else if (v == btnSwichePlayer) {

        } else if (v == btnExit) {
            finish();
        } else if (v == btnPre) {
            setPreVideo();
        } else if (v == btnStartPause) {
            if (vv.isPlaying()) {
                //暂停
                vv.pause();
                //按钮状态-播放
                btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);

            } else {
                //播放
                vv.start();
                //按钮状态-播放
                btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
            }
        } else if (v == btnNext) {
            setNextVideo();
        } else if (v == btnSwichScreen) {

        }
    }

    private void setPreVideo() {
        position--;
        if(position > 0) {
            //还是在列表范围内容
            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());

            //设置按钮状态
            setButtonStatus();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    //得到当前进度
                    int currentPosition = vv.getCurrentPosition();
                    //让SeekBar进度更新
                    seekbarVideo.setProgress(currentPosition);

                    //设置当前文本的播放速度
                    tvCurrenttime.setText(utils.stringForTime(currentPosition));

                    //得到系统的时间紧
                    tvSystetime.setText(getSystemTime());

                    //循环发送消息
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }

        }
    };

    /**
     * 得到系统的时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        findViews();
        getData();

        setListener();
        setData();


        //设置控制面板
//        vv.setMediaController(new MediaController(this));
    }

    private void setData() {

        if (mediaItems != null && mediaItems.size() > 0) {

            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            vv.setVideoPath(mediaItem.getData());
        } else if (uri != null) {
            //设置播放的地址
            vv.setVideoURI(uri);
        }
        setButtonStatus();
    }

    private void setButtonStatus() {
        if(mediaItems != null && mediaItems.size() >0){
            //有视频播放
            setEnable(true);

            if(position ==0){
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }

            if(position ==mediaItems.size()-1){
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }

        }else if(uri != null){
            //上一个和下一个不可用点击
            setEnable(false);
        }
    }
    /**
     * 设置按钮是否可以点击
     * @param b
     */
    private void setEnable(boolean b) {
        if( b){
            //上一个和下一个都可以点击
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        }else {
            //上一个和下一个灰色，并且不可用点击
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPre.setEnabled(b);
        btnNext.setEnabled(b);
    }


    private void getData() {
        //得到播放的地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);

    }

    private void initData() {
        utils = new Utils();

        //注册监听电量变化的广播
        receiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //监听电量变化的状态
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);

    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //在主线程中
            int level = intent.getIntExtra("level", 0);
            Log.e("TAG", "level==" + level);
            setBatteryView(level);
        }
    }

    private void setBatteryView(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //设置播放器的三个监听:播放准备好的监听,播放完成的监听,播放出错的监听
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //底层准备播放完成的时候回调
            @Override
            public void onPrepared(MediaPlayer mp) {
                //得到视频的总时长
                int duration = vv.getDuration();
                seekbarVideo.setMax(duration);
                //设置文本总时间
                tvDuration.setText(utils.stringForTime(duration));
                vv.start();//开始播放

                //发消息开始更新播放进度
                handler.sendEmptyMessage(PROGRESS);
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
//                Toast.makeText(SystemVideoPlayerActivity.this, "视频播放完成", Toast.LENGTH_SHORT).show();
//                finish();
                setNextVideo();
            }
        });

        //设置SeekBar状态改变的监听
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             *
             * @param seekBar
             * @param progress
             * @param fromUser true:用户改变 false: 系统更新的
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    vv.seekTo(progress);
                }
            }

            //设置控制面板
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setNextVideo() {
        position++;
        if (position < mediaItems.size()) {
            //还是在列表的范围的内容
            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());

            //设置按钮的状态
            setButtonStatus();
        } else {
            Toast.makeText(this, "退出播放器", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        //先释放子类的,在释放父类的,如果释放父类的话,如果有需要父类的,容易出空指针异常
        if (handler != null) {
            //把所有的消息移除
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        //取消注册
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }
}
