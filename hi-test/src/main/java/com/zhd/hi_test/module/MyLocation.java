package com.zhd.hi_test.module;


import com.zhd.hi_test.util.Coordinate;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 2015032501 on 2015/9/23.
 * <p>
 * 需要显示到屏幕上的数据，其中包括有位置信息和时间信息
 * 最好是全部的数据都存满，不要留下空隙
 * 注意内置GPS和IRTK的数据格式是不一样的
 */
public class MyLocation {

    private String mB;
    private String mL;
    private String mH;
    private String mTime;
    private String mDireB;
    private String mDireL;
    private String mProgressB;
    private String mProgressL;
    private String mQuality;
    private String mAge = "尚无差分龄期";
    private String mUseSate = "0";

    public String getmUseSate() {
        return mUseSate;
    }

    public String getmQuality() {
        return mQuality;
    }

    public String getmAge() {
        return mAge;
    }

    public String getmDireB() {
        return mDireB;
    }

    public String getmDireL() {
        return mDireL;
    }

    public String getmProgressB() {
        return mProgressB;
    }

    public String getmProgressL() {
        return mProgressL;
    }


    //RTK传过来的数据
    public MyLocation(String B, String L, String H, String DireB, String DireL, String time, int Quality, String Age, String useSate) {
        this.mQuality = getQuality(Quality);
        this.mAge = Age;
        this.mB = Coordinate.getDmsString(Coordinate.getDegreeFromRTK(B));
        this.mL = Coordinate.getDmsString(Coordinate.getDegreeFromRTK(L));
        this.mProgressB = String.valueOf(Coordinate.getDegreeFromRTK(B));
        this.mProgressL = String.valueOf(Coordinate.getDegreeFromRTK(L));
        this.mH = H;
        this.mDireB = DireB;
        this.mDireL = DireL;
        this.mTime = getLocalTime(time);
        this.mUseSate = useSate;
    }

    private String getQuality(int quality) {
        //默认无解
        String msg = "无解";
        switch (quality) {
            case 0:
                msg = "无解";
                break;
            case 1:
                msg = "GPS固定解";
                break;
            case 2:
                msg = "不同GPS固定解";
                break;
            case 4:
                msg = "实时差分固定解";
                break;
            case 5:
                msg = "实时差分浮动解";
                break;
        }
        return msg;
    }


    //内置GPS传过来的数据
    public MyLocation(String mB, String mL, String mH, long time,String UseSate) {
        this.mProgressB = mB;
        this.mProgressL = mL;
        this.mB = Coordinate.getDmsString(Double.valueOf(mB));
        this.mL = Coordinate.getDmsString(Double.valueOf(mL));
        this.mH = mH;
        this.mTime = getTime(time);
        //根据正负来判断当前位于哪个半球，必须转化为double类型，转化成int类型显示为空指针
        double B = Double.valueOf(mB);
        double L = Double.valueOf(mL);
        this.mUseSate=UseSate;
        this.mQuality = "GPS单点定位";
        if (B > 0)
            this.mDireB = "N";
        else
            this.mDireB = "S";
        if (L > 0)
            this.mDireL = "E";
        else
            this.mDireL = "W";
    }

    public String getmB() {
        return mB;
    }

    public String getmL() {
        return mL;
    }

    public String getmH() {
        return mH;
    }

    public String getmTime() {
        return mTime;
    }

    private String getTime(long mTime) {
        Date date = new Date(mTime);//GPSneizhi
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return (format.format(date));
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

}
