package com.atguigu.mobileplayer0224.bean;

/**
 * 作者：田学伟 on 2017/5/26 11:49
 * QQ：93226539
 * 作用：代表一句歌词
 */

public class Lyric {
    /**
     * 这一句时间的内容
     */
    private String content;
    /**
     * 时间戳
     */
    private long timePoint;
    /**
     * 高亮持续时间
     */
    private long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
