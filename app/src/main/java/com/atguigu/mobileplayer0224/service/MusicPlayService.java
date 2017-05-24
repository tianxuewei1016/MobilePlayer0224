package com.atguigu.mobileplayer0224.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MusicPlayService extends Service {

    public MusicPlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 根据位置播放一个音频
     * @param position
     */
    public void openAudio(int position){

    }

    /**
     * 播放音频
     */
    public void start(){

    }
    /**
     * 暂停音频
     */
    public void pause(){

    }
    /**
     * 得到演唱者
     * @return
     */
    private String getArtistName(){
        return "";
    }
    /**
     * 得到歌曲路径
     * @return
     */
    private String getAudioPath(){
        return "";
    }
    /**
     * 得到总时长
     * @return
     */
    private int getDuration(){
        return 0;
    }
    /**
     * 得到当前播放速度
     * @return
     */
    private int getCurrentPosition(){
        return 0;
    }
    /**
     * 音频拖动
     * @return
     */
    private void seekTo(){

    }
}
