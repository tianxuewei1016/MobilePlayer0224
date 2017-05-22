package com.atguigu.mobileplayer0224.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.bean.MediaItem;
import com.atguigu.mobileplayer0224.utils.Utils;
import com.atguigu.mobileplayer0224.view.VideoView;

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

    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLLER = 1;
    /**
     * 显示网速
     */
    private static final int SHOW_NET_SPEED = 2;
    /**
     * 默认视频的画面
     */
    private static final int DEFUALT_SCREEN = 0;
    /**
     * 全屏视屏的画面
     */
    private static final int Full_SCREEN = 1;

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
    private LinearLayout ll_buffering;
    private TextView tv_net_speed;
    private LinearLayout ll_loading;
    private TextView tv_loading_net_speed;

    private Utils utils;
    private MyBroadCastReceiver receiver;
    /**
     * 视频列表的位置
     */
    private int position;
    //手势识别器
    private GestureDetector detector;
    /**
     * 默认不是全屏
     */
    private boolean isFullScreen = false;
    /**
     * 设置屏幕的宽和高
     */
    private int screenHeight;
    private int screenWidth;

    //当前的原声的宽和高
    private int videoWidth;
    private int videoHeight;

    //当前的音量0到15
    private int currentVoice;
    private AudioManager am;
    //最大音量
    private int maxVoice;
    //是否静音
    private boolean isMute = false;
    /**
     * 震动
     */
    private Vibrator vibrator;
    /**
     * 是否是网络的资源
     */
    private boolean isNetUrl = true;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-20 11:02:04 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
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
        vv = (VideoView) findViewById(R.id.vv);
        ll_buffering = (LinearLayout) findViewById(R.id.ll_buffering);
        tv_net_speed = (TextView) findViewById(R.id.tv_net_speed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_loading_net_speed = (TextView) findViewById(R.id.tv_loading_net_speed);

        btnVoice.setOnClickListener(this);
        btnSwichePlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwichScreen.setOnClickListener(this);

        //关联最大的声音
        seekbarVoice.setMax(maxVoice);
        //设置当前的进度
        seekbarVoice.setProgress(currentVoice);

        //发消息开始显示网速
        handler.sendEmptyMessage(SHOW_NET_SPEED);
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
            isMute = !isMute;

            updateVoice(isMute);

        } else if (v == btnSwichePlayer) {

            switchPlayer();
        } else if (v == btnExit) {
            finish();
        } else if (v == btnPre) {
            setPreVideo();
        } else if (v == btnStartPause) {
            setStartorPause();
        } else if (v == btnNext) {
            setNextVideo();
        } else if (v == btnSwichScreen) {
            if (isFullScreen) {
                //默认
                setVideoType(DEFUALT_SCREEN);
            } else {
                //全屏
                setVideoType(Full_SCREEN);
            }
        }
        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
    }

    private void switchPlayer() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前使用系统播放器播放，当播放有声音没有画面，请切换到万能播放器播放")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startVitamioPlayer();
                    }
                }).setNegativeButton("取消",null)
                .show();
    }

    /**
     * 是否静音的方法
     *
     * @param isMute
     */
    private void updateVoice(boolean isMute) {
        if (isMute) {
            //静音
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            //非静音
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
            seekbarVoice.setProgress(currentVoice);
        }
    }

    /**
     * 设置视频的全屏和默认
     *
     * @param videoType
     */
    private void setVideoType(int videoType) {
        switch (videoType) {
            case Full_SCREEN:
                isFullScreen = true;
                //按钮状态--默认
                btnSwichScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                //设置视频画面为全屏显示
                vv.setVideoSize(screenWidth, screenHeight);

                break;
            case DEFUALT_SCREEN:
                isFullScreen = false;
                btnSwichScreen.setBackgroundResource(R.drawable.btn_screen_full_selector);

                //视频原生的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //计算好的要显示的视频的宽和高
                int width = screenWidth;
                int height = screenHeight;
                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                vv.setVideoSize(width, height);
                break;
        }
    }

    /**
     * 播放和暂停功能
     */
    private void setStartorPause() {
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
    }

    private int preCurrentPosition;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NET_SPEED:
                    if (isNetUrl) {
                        String netSpeed = utils.getNetSpeed(SystemVideoPlayerActivity.this);
                        tv_loading_net_speed.setText("正在加载中...." + netSpeed);
                        tv_net_speed.setText("正在缓冲...." + netSpeed);
                        sendEmptyMessageDelayed(SHOW_NET_SPEED, 4000);
                    }
                    break;
                case PROGRESS:
                    //得到当前进度
                    int currentPosition = vv.getCurrentPosition();
                    //让SeekBar进度更新
                    seekbarVideo.setProgress(currentPosition);

                    //设置当前文本的播放速度
                    tvCurrenttime.setText(utils.stringForTime(currentPosition));

                    //得到系统的时间紧
                    tvSystetime.setText(getSystemTime());

                    //设置视频缓存效果
                    if (isNetUrl) {
                        int bufferPercentage = vv.getBufferPercentage();
                        int totalBuffer = bufferPercentage * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    if (isNetUrl && vv.isPlaying()) {

                        int duration = currentPosition - preCurrentPosition;
                        if (duration < 500) {
                            //卡
                            ll_buffering.setVisibility(View.VISIBLE);
                        } else {
                            //不卡
                            ll_buffering.setVisibility(View.GONE);
                        }
                        preCurrentPosition = currentPosition;
                    }

                    //循环发送消息
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;

                case HIDE_MEDIACONTROLLER://隐藏控制面
                    hideMediaController();
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
            isNetUrl = utils.isNetUrl(mediaItem.getData());
        } else if (uri != null) {
            //设置播放的地址
            vv.setVideoURI(uri);
            tvName.setText(uri.toString());
            isNetUrl = utils.isNetUrl(uri.toString());
        }
        setButtonStatus();
    }

    private void getData() {
        //得到播放的地址
        uri = getIntent().getData();//获取从外界传入的播放地址
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

        //实例化手势识别器
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            //长按的
            @Override
            public void onLongPress(MotionEvent e) {
                //Toast.makeText(SystemVideoPlayerActivity.this, "长按了", Toast.LENGTH_SHORT).show();
                setStartorPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //Toast.makeText(SystemVideoPlayerActivity.this, "双击了", Toast.LENGTH_SHORT).show();
                if (isFullScreen) {
                    //默认
                    setVideoType(DEFUALT_SCREEN);
                } else {
                    //全屏
                    setVideoType(Full_SCREEN);
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //Toast.makeText(SystemVideoPlayerActivity.this, "单机了", Toast.LENGTH_SHORT).show();
                if (isShowMediaController) {
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    showMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕的高度
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        //初始化声音相关
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    /**
     * 滑动的最大的区域
     */
    private float touchRang = 0;

    /**
     * 当按下的初始的音量
     */
    private int mVol;
    private float startY;

    /**
     * 按下屏幕改变右边声音和左边亮度
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件交给手势是比起解析
        detector.onTouchEvent(event);
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //1.按下
            //按下的时候记录起始坐标，最大的滑动区域（屏幕的高），当前的音量
            startY = event.getY();
            touchRang = Math.min(screenHeight, screenWidth);//返回的是screeHeight
            //记录当前的声音
            mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            //把消息移除
            handler.removeMessages(HIDE_MEDIACONTROLLER);

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float endY = event.getY();
            //滑动的距离
            float distanceY = startY - endY;
//            if (startX > screenWidth / 2) {
            //屏幕滑动的距离
            //滑动屏幕的距离 ： 总距离  = 改变的声音 ： 总声音
            //改变的声音 = （滑动屏幕的距离 / 总距离)*总声音
            float delta = (distanceY / touchRang) * maxVoice;
            //判断
            if (delta != 0) {
                // 最终的声音  = 原来记录的 + 改变的声音
                int mVoice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
                updateVoiceProgress(mVoice);
            }
//            } else {
//                //左边屏幕--改变亮度
//                final double FLING_MIN_DISTANCE = 0.5;
//                final double FLING_MIN_VELOCITY = 0.5;
//
//                if (startY - endY > FLING_MIN_DISTANCE
//                        && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                    Log.e("TAG", "up");
//                    setBrightness(20);
//                }
//                if (startY - endY < FLING_MIN_DISTANCE
//                        && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                    Log.e("TAG", "down");
//                    setBrightness(-20);
//                }
//
//            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
        }
        return true;
    }

    /*
    *
    * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
    */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // if (lp.screenBrightness <= 0.1) {
        // return;
        // }
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {10, 200}; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {10, 200}; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        }
        getWindow().setAttributes(lp);
    }

    /**
     * 是否显示控制面板,默认是false
     */
    private boolean isShowMediaController = false;

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        isShowMediaController = false;
        //这个是隐藏不占位置
        llBottom.setVisibility(View.INVISIBLE);
        //这个隐藏占位置
        llTop.setVisibility(View.GONE);

    }

    /**
     * 显示控制面板
     */
    public void showMediaController() {
        isShowMediaController = true;
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
    }


    /**
     * 内容提供者
     */
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

                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();

                //得到视频的总时长
                int duration = vv.getDuration();
                seekbarVideo.setMax(duration);
                //设置文本总时间
                tvDuration.setText(utils.stringForTime(duration));
                vv.start();//开始播放

                //发消息开始更新播放进度
                handler.sendEmptyMessage(PROGRESS);

                //隐藏加载效果的画面
                ll_loading.setVisibility(View.GONE);

                //默认隐藏
                hideMediaController();

                //设置默认屏幕
                setVideoType(DEFUALT_SCREEN);

//                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                    @Override
//                    public void onSeekComplete(MediaPlayer mp) {
//                        Toast.makeText(SystemVideoPlayerActivity.this, "拖动完成", Toast.LENGTH_SHORT).show();
//                    }
//                });
                if(vv.isPlaying()) {
                    //设置暂停
                    btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
                }else {
                    btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
                }
            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //Toast.makeText(SystemVideoPlayerActivity.this, "播放出错了...", Toast.LENGTH_SHORT).show();
                //1.视频格式不支持,一进来播放就会报错--切换到万能播放器
                startVitamioPlayer();

                //2.播放过程中网络中断,导致网络异常--重新播放--三次重试

                //3.文件中间部分损毁或者文件不完整--把下载做好
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
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            }
        });

        //设置拖动的声音
        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVoiceProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        //设置监听卡
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                vv.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//                    @Override
//                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                        switch (what){
//                            //播放卡，拖拽卡
//                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                                  ll_buffering.setVisibility(View.VISIBLE);
//                                break;
//                            //播放不卡了，拖拽不卡了
//                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                                  ll_buffer.setVisibility(View.GONE);
//                                break;
//                        }
//                        return false;
//                    }
//                });
//        }
    }

    private void startVitamioPlayer() {
        if(vv != null){
            vv.stopPlayback();
        }
        Intent intent = new Intent(this, VitamioVideoPlayerActivity.class);
        if(mediaItems != null && mediaItems.size() >0){
            Bundle bunlder = new Bundle();
            bunlder.putSerializable("videolist",mediaItems);
            intent.putExtra("position",position);
            //放入Bundler
            intent.putExtras(bunlder);
        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);
        finish();//关闭系统播放器
    }

    /**
     * 设置屏幕滑动改变声音
     *
     * @param progress
     */
    private void updateVoiceProgress(int progress) {
        currentVoice = progress;
        //真正的声音
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
        //改变进度条
        seekbarVoice.setProgress(currentVoice);
        if (currentVoice <= 0) {
            isMute = true;
        } else {
            isMute = false;
        }
    }

    private void setPreVideo() {
        position--;
        if (position > 0) {
            //还是在列表范围内容
            MediaItem mediaItem = mediaItems.get(position);
            isNetUrl = utils.isNetUrl(mediaItem.getData());
            ll_loading.setVisibility(View.VISIBLE);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());

            //设置按钮状态
            setButtonStatus();
        }
    }

    private void setNextVideo() {
        position++;
        if (position < mediaItems.size()) {
            //还是在列表的范围的内容
            MediaItem mediaItem = mediaItems.get(position);
            isNetUrl = utils.isNetUrl(mediaItem.getData());

            ll_loading.setVisibility(View.VISIBLE);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            //设置按钮的状态
            setButtonStatus();
        } else {
            Toast.makeText(this, "退出播放器", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setButtonStatus() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //有视频播放
            setEnable(true);

            if (position == 0) {
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }

            if (position == mediaItems.size() - 1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }

        } else if (uri != null) {
            //上一个和下一个不可用点击
            setEnable(false);
        }
    }


    /**
     * 设置按钮是否可以点击
     *
     * @param b
     */
    private void setEnable(boolean b) {
        if (b) {
            //上一个和下一个都可以点击
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            //上一个和下一个灰色，并且不可用点击
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPre.setEnabled(b);
        btnNext.setEnabled(b);
    }

    /**
     * 按手机上的按钮实现声音变大变小
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //改变音量值
            currentVoice--;
            updateVoiceProgress(currentVoice);
            //移除消息
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            //发消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updateVoiceProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            //必须返回true,要不然就响应系统的了,设置false系统的也会出来
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
