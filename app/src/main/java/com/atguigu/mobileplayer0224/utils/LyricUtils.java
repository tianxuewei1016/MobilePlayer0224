package com.atguigu.mobileplayer0224.utils;

import com.atguigu.mobileplayer0224.bean.Lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 作者：田学伟 on 2017/5/26 15:16
 * QQ：93226539
 * 作用：解析歌词的工具类
 */

public class LyricUtils {
    private ArrayList<Lyric> lyrics;

    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }

    /**
     * 歌词是否存在
     * @return
     */
    public boolean isLyric(){
        return isLyric;
    }

    private boolean isLyric = false;

    /**
     * 解析歌词,把解析好的放入结合中
     *
     * @param file
     */
    public void readFile(File file) {
        if (file == null || !file.exists()) {
            //文件不存在
            lyrics = null;
            isLyric = false;
        } else {
            //歌词文件存在
            lyrics = new ArrayList<>();
            isLyric = true;
            //读入文件,并且一行一行的读取
            FileInputStream fis = null;
            try {
                //文件输入流
                fis = new FileInputStream(file);
                InputStreamReader streamReader = new InputStreamReader(fis, "GBK");
                BufferedReader reader = new BufferedReader(streamReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    //解析每一行歌词,并且把解析好的歌词加入到集合里面
                    analyzeLyric(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //把解析好的歌词排序
            Collections.sort(lyrics, new Comparator<Lyric>() {
                @Override
                public int compare(Lyric o1, Lyric o2) {
                    if (o1.getTimePoint() < o2.getTimePoint()) {
                        return -1;
                    } else if (o1.getTimePoint() > o2.getTimePoint()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            //计算每一句的高亮显示的时间
            for (int i = 0; i < lyrics.size(); i++) {
                Lyric onelyric = lyrics.get(i);

                if (i + 1 < lyrics.size()) {
                    Lyric twolyric = lyrics.get(i + 1);
                    //等于后一句的时间戳减去当前句的时间戳
                    onelyric.setSleepTime(twolyric.getTimePoint() - onelyric.getTimePoint());
                }
            }
        }
    }

    /**
     * 解析歌词 -- 是某一行
     *
     * @param line
     */
    private void analyzeLyric(String line) {
        int pos1 = line.indexOf("[");//0
        int pos2 = line.indexOf("]");//9,如果没有就返回-1

        if (pos1 == 0 && pos2 != -1) {//至少有一句歌词
            //装long类型的时间戳
            long[] timeLongs = new long[getCountTag(line)];
            String timeStr = line.substring(pos1 + 1, pos2);
            //解析第0句
            timeLongs[0] = stringToLong(timeStr);//转换成long的毫秒类型
            if (timeLongs[0] == -1) {
                return;
            }

            int i = 1;
            String content = line;
            while (pos1 == 0 && pos2 != -1) {
                content = content.substring(pos2 + 1);
                pos1 = content.indexOf("[");//0
                pos2 = content.indexOf("]");//9,如果没有就返回-1

                if (pos1 == 0 && pos2 != -1) {//至少还有一句
                    timeStr = content.substring(pos1 + 1, pos2);
                    //解析第1句
                    timeLongs[i] = stringToLong(timeStr);//转换成long的毫秒类型

                    if (timeLongs[i] == -1) {
                        return;
                    }
                    i++;//2
                }
            }

            //装long类型的时间戳
            for (int j = 0; j < timeLongs.length; j++) {
                if (timeLongs[j] != 0) {
                    Lyric lyric = new Lyric();
                    //设置内容
                    lyric.setTimePoint(timeLongs[j]);
                    //我在这里欢笑
                    lyric.setContent(content);
                    lyrics.add(lyric);
                }
            }
        }
    }

    /**
     * 转换成long的毫秒类型
     *
     * @param timeStr
     * @return
     */
    private long stringToLong(String timeStr) {
        long result = -1;
        try {
            //切换
            String[] s1 = timeStr.split(":");
            String[] s2 = s1[1].split("\\.");
            //把他们转换成毫秒
            //分
            long min = Long.valueOf(s1[0]);
            //秒
            long second = Long.valueOf(s2[0]);
            //毫秒
            long mil = Long.valueOf(s2[1]);

            result = min * 60 * 1000 + second * 1000 + mil * 10;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断有多少句歌词
     *
     * @param line
     * @return
     */
    private int getCountTag(String line) {
        int result = 1;
        String[] s1 = line.split("\\[");
        String[] s2 = line.split("\\]");
        if (s1.length == 0 && s2.length == 0) {
            result = 1;
        } else if (s1.length > s2.length) {
            result = s1.length;
        } else {
            result = s2.length;
        }
        return result;
    }
}
