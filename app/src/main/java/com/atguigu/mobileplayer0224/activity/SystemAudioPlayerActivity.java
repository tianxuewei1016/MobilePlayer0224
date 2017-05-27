package com.atguigu.mobileplayer0224.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.IMusicPlayService;
import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.bean.Lyric;
import com.atguigu.mobileplayer0224.bean.MediaItem;
import com.atguigu.mobileplayer0224.service.MusicPlayService;
import com.atguigu.mobileplayer0224.utils.LyricUtils;
import com.atguigu.mobileplayer0224.utils.Utils;
import com.atguigu.mobileplayer0224.view.BaseVisualizerView;
import com.atguigu.mobileplayer0224.view.LyricShowView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SystemAudioPlayerActivity extends AppCompatActivity {

    @InjectView(R.id.iv_icon)
    ImageView ivIcon;
    @InjectView(R.id.tv_artist)
    TextView tvArtist;
    @InjectView(R.id.tv_audioname)
    TextView tvAudioname;
    @InjectView(R.id.rl_top)
    RelativeLayout rlTop;
    @InjectView(R.id.tv_time)
    TextView tvTime;
    @InjectView(R.id.seekbar_audio)
    SeekBar seekbarAudio;
    @InjectView(R.id.btn_playmode)
    Button btnPlaymode;
    @InjectView(R.id.btn_pre)
    Button btnPre;
    @InjectView(R.id.btn_start_pause)
    Button btnStartPause;
    @InjectView(R.id.btn_next)
    Button btnNext;
    @InjectView(R.id.btn_lyric)
    Button btnLyric;
    @InjectView(R.id.ll_bottom)
    LinearLayout llBottom;
    @InjectView(R.id.lyric_show_view)
    LyricShowView lyricShowView;
    @InjectView(R.id.visualizerview)
    BaseVisualizerView visualizerview;

    //这个就是IMusicPlayService.stub的实例
    private IMusicPlayService service;
    private int position;
    private Utils utils;
    private MyReceiver receiver;

    private boolean notification;

    private Visualizer mVisualizer;

    private final static int PROGRESS = 0;
    /**
     * 显示歌词
     */
    private static final int SHOW_LYRIC = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC:
                    try {
                        int currentPosition = service.getCurrentPosition();

                        //调用歌词显示空间的方法
                        lyricShowView.LyricShowView(currentPosition);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessage(SHOW_LYRIC);
                    break;
                case PROGRESS:
                    try {
                        //获取当前的进度
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);

                        //设置更新的时间
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    //每秒钟刷新一次
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };

    private ServiceConnection conon = new ServiceConnection() {
        /**
         * 当绑定服务成功后的服务
         * @param name
         * @param iBinder 就是IMusicPlayService.Stub的实例
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //这个就是stub，stub包含很多方法，这些方法调用服务的方法
            service = IMusicPlayService.Stub.asInterface(iBinder);
            if (service != null) {
                try {
//                    service.openAudio(position);//打开播放的资源
                    if (notification) {
                        //什么不用做
                        setViewData(null);
                    } else {
                        service.openAudio(position);//打开播放第0个音频

                        tvArtist.setText(service.getArtistName());
                        tvAudioname.setText(service.getAudioName());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开连接的时候回调
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_audio_player);
        ButterKnife.inject(this);

        initData();
        setListener();

        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();
        getData();
        startAndBindService();
    }

    /**
     * 设置监听的方法
     */
    private void setListener() {
        //设置监听拖动视频
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void initData() {
        //注册广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayService.OPEN_COMPLETE);
        registerReceiver(receiver, intentFilter);

        utils = new Utils();

        //1.注册EventBus
        EventBus.getDefault().register(this);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //主线程中
            setViewData(null);
        }
    }

    public boolean isLyric() {
        return isLyric;
    }

    private boolean isLyric = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setViewData(MediaItem mediaItem) {
        try {
            setButtonImage();

            //只有准备好的时候,得到的才不是-1
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);

            //解析歌词
            //1.歌词所在的路径
            String audioPath = service.getAudioPath();
            //2.传入解析歌词的工具类
            String lyricPath = audioPath.substring(0, audioPath.lastIndexOf("."));
            File file = new File(lyricPath + ".lrc");
            if (!file.exists()) {
                file = new File(lyricPath + ".txt");
            }

            LyricUtils lyricUtils = new LyricUtils();
            //把文件读进去
            lyricUtils.readFile(file);
            //3.如果有歌词,就歌词同步

            ArrayList<Lyric> lyrics = lyricUtils.getLyrics();
            //设置到歌词显示控件上
            lyricShowView.setLyrics(lyrics);
            //3.如果有歌词，就歌词同步
            if (lyricUtils.isLyric()) {
                handler.sendEmptyMessage(SHOW_LYRIC);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //发送更新的进度
        handler.sendEmptyMessage(PROGRESS);
        //显示音乐频谱
        setupVisualizerFxAndUi();
    }

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi() {

        int audioSessionid = 0;
        try {
            audioSessionid = service.getAudioSessionId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("audioSessionid==" + audioSessionid);
        mVisualizer = new Visualizer(audioSessionid);
        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 设置允许波形表示，并且捕获它
        visualizerview.setVisualizer(mVisualizer);
        mVisualizer.setEnabled(true);
    }

    private void getData() {
//        position = getIntent().getIntExtra("position", 0);
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()) {
            mVisualizer.release();
        }
    }

    //启动服务
    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayService.class);
        //绑定服务--得到服务的操作对象---IMusicPlayService service
        bindService(intent, conon, Context.BIND_AUTO_CREATE);
        //防止多次实例化Service
        startService(intent);
    }

    @OnClick({R.id.btn_playmode, R.id.btn_pre, R.id.btn_start_pause, R.id.btn_next, R.id.btn_lyric})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_playmode:
                setPlayMode();
                break;
            case R.id.btn_pre:
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_start_pause:
                try {
                    if (service.isPlaying()) {
                        //暂停
                        service.pause();
                        //按钮的状态--播放
                        btnStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } else {
                        //播放
                        service.start();
                        //按钮的状态--暂停
                        btnStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_next:
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_lyric:
                break;
        }
    }

    private void setPlayMode() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                playmode = MusicPlayService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                playmode = MusicPlayService.REPEAT_ALL;
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                playmode = MusicPlayService.REPEAT_NORMAL;
            }
            //保存到服务里面
            service.setPlaymode(playmode);

            setButtonImage();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setButtonImage() {

        //从服务器得到播放模式
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (conon != null) {
            unbindService(conon);
            conon = null;
        }
        //广播取消注册
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        //2.取消注册
        EventBus.getDefault().unregister(this);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
