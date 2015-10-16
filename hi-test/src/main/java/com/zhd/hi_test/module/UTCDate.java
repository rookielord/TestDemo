package com.zhd.hi_test.module;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 2015032501 on 2015/10/15.
 */
public class UTCDate {

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
     * 转化后的当前年月日
     */
    private String mCurrentDate="尚未确定";

    public UTCDate(String Day, String Month, String Year) {
        this.mDay = Day;
        this.mMonth = Month;
        this.mYear = Year;
        //拼接当前的时间
        this.mCurrentDate = mYear + " " + mMonth + " " + mDay + " ";
    }



    public String getmCurrentDate() {
        return mCurrentDate;
    }

    /**
     * 内置GPS传入的时间，进行解析
     *
     * @param CurrentTime 内置GPS传入的long类型的时间
     */
    public UTCDate(long CurrentTime) {
        this.mCurrentDate = getDate(CurrentTime);
    }

    /**
     * 将内置GPS的时间信息转化为年月日
     * @param mTime long类型时间
     * @return
     */
    private String getDate(long mTime) {
        Date date = new Date(mTime);//GPSneizhi
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return (format.format(date));
    }
}
