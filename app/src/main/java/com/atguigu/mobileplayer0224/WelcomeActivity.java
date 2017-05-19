package com.atguigu.mobileplayer0224;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public class WelcomeActivity extends AppCompatActivity {

    //有handler不一定要写handleMessage
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后执行
                startMainActivity();
            }
        }, 2000);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //关闭当前页面
        finish();
    }

    /**
     * 只要触摸屏幕就立刻进入
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }

    /**
     * 移除消息,销毁当前页面
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //把延迟进入主页面的消息移除
        handler.removeCallbacksAndMessages(null);
    }
}
