package com.atguigu.mobileplayer0224.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.bean.Lyric;

import java.util.ArrayList;

/**
 * 作者：田学伟 on 2017/5/26 11:39
 * QQ：93226539
 * 作用：自定义显示歌词的控件
 */

public class LyricShowView extends TextView {
    private Paint paint;
    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;
    private Paint paintWhile;
    /**
     * 表示的是歌词列表中的哪一句
     */
    private int index;
    private float textHeight = 20;

    public LyricShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextSize(16);
        //设置居中
        paint.setTextAlign(Paint.Align.CENTER);

        paintWhile = new Paint();
        paintWhile.setColor(Color.GREEN);
        paintWhile.setAntiAlias(true);
        paintWhile.setTextSize(16);
        //设置居中
        paintWhile.setTextAlign(Paint.Align.CENTER);

        //准备歌词
        lyrics = new ArrayList<>();
        Lyric lyric = new Lyric();
        for (int i = 0; i < 1000; i++) {
            //不同歌词
            lyric.setContent("aaaaaaaaaaaaaaa_" + i);
            lyric.setSleepTime(2000);
            lyric.setTimePoint(2000 * i);
            //添加到集合
            lyrics.add(lyric);
            //重新创建新对象
            lyric = new Lyric();
        }
    }


    /**
     * 绘制歌词
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {
            //才有歌词
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paint);

            //得到中间距离的坐标
            float tempY = height / 2;
            //绘制前面部分
            for (int i = index - 1; i >= 0; i--) {
                //得到前一部分所有的歌词内容
                String preContent = lyrics.get(i).getContent();

                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                //绘制内容
                canvas.drawText(preContent, width / 2, tempY, paintWhile);
            }
            tempY = height / 2;
            //绘制后面部分
            for (int i = index + 1; i < lyrics.size(); i++) {
                //得到后一部分的内容
                String nextContent = lyrics.get(i).getContent();

                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                //绘制内容
                canvas.drawText(nextContent, width / 2, tempY, paintWhile);
            }
        } else {
            canvas.drawText("没有找到歌词..", width / 2, height / 2, paint);
        }
    }
}
