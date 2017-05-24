package com.atguigu.mobileplayer0224.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
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
import com.atguigu.mobileplayer0224.service.MusicPlayService;

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
    //这个就是IMusicPlayService.stub的实例
    private IMusicPlayService service;
    private int position;

    private ServiceConnection conon = new ServiceConnection() {
        /**
         * 当绑定服务成功后的服务
         * @param name
         * @param iBinder 就是IMusicPlayService.Stub的实例
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //这个就是stub，stub包含很多方法，这些方法调用服务的方法
            service =  IMusicPlayService.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    service.openAudio(position);//打开播放的资源
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

        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();
        getData();
        startAndBindService();
    }

    private void getData() {
        position = getIntent().getIntExtra("position",0);
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
                break;
            case R.id.btn_pre:
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
                break;
            case R.id.btn_lyric:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(conon != null) {
            unbindService(conon);
            conon = null;
        }
        super.onDestroy();
    }
}
