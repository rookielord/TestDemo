package com.zhd.hi_test.module;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 2015032501 on 2015/10/15.
 */
public class UTCTime {

    /**
     * 当前的UTC时间
     */
    private String mTime;
    /**
     * 当前的日期
     */
    private String mDay;
    /**
     * 当前的月份
     */
    private String mMonth;
    /**
     * 当前的年份
     */
    private String mYear;
    /**
     * 转化后的当前时间
     */
    private String mCurrentTime;

    public UTCTime(String Time, String Day, String Month, String Year) {
        this.mTime = getLocalTime(Time);
        this.mDay = Day;
        this.mMonth = Month;
        this.mYear = Year;
        //拼接当前的时间
        this.mCurrentTime=mYear+" "+mMonth+" "+mDay+" "+mTime;
    }

    private String getLocalTime(String mTime) {
        //1.获取时间
        String hour = mTime.substring(0, 2);
        //2.获取分秒
        String munites = mTime.substring(2, 4);
        //3.获取秒
        String seconds = mTime.substring(4, 6);
        //4.将小时数量+8
        int currenthour = Integer.valueOf(hour) + 8;
        if (currenthour < 9)
            hour = "0" + currenthour;
        else
            hour = String.valueOf(currenthour);
        return hour + ":" + munites + ":" + seconds;
    }

    public String getmCurrentTime() {
        return mCurrentTime;
    }


    public UTCTime(long CurrentTime) {
        this.mCurrentTime = getTime(CurrentTime);
    }

    /**
     * 将内置GPS的时间信息转化为年月日 时分秒的数据
     * @param mTime
     * @return
     */
    private String getTime(long mTime) {
        Date date = new Date(mTime);//GPSneizhi
        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return (format.format(date));
    }
}
