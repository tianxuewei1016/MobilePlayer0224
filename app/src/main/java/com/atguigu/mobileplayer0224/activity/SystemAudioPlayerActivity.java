package com.atguigu.mobileplayer0224.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.service.MusicPlayService;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SystemAudioPlayerActivity extends AppCompatActivity {


    @InjectView(R.id.iv_icon)
    ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_audio_player);
        ButterKnife.inject(this);

        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();

        //启动服务
        Intent intent = new Intent(this, MusicPlayService.class);
        startService(intent);
    }
}
