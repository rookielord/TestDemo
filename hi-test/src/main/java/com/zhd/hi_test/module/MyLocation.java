package com.zhd.hi_test.module;


import com.zhd.hi_test.util.Coordinate;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 2015032501 on 2015/9/23.
 * <p/>
 * 需要显示到屏幕上的数据，其中包括有位置信息和时间信息
 * 最好是全部的数据都存满，不要留下空隙
 * 注意内置GPS和IRTK的数据格式是不一样的
 */
public class MyLocation {

    private String mB;
    private String mL;
    private double mH;
    private String mTime;
    private String mDireB;
    private String mDireL;
    private double mProgressB;
    private double mProgressL;
    private double mN;
    private double mE;
    private double mZ;
    private String mQuality;
    private float mAge = 0.0f;
    private int mUseSate = 0;

    public double getUseSate() {
        return mUseSate;
    }

    public String getQuality() {
        return mQuality;
    }

    public float getAge() {
        return mAge;
    }

    public String getDireB() {
        return mDireB;
    }

    public String getDireL() {
        return mDireL;
    }

    public double getProgressB() {
        return mProgressB;
    }

    public double getProgressL() {
        return mProgressL;
    }


    //RTK传过来的数据
    public MyLocation(String B, String L, String H, String DireB, String DireL, String time, int Quality, String Age, String useSate) {
        this.mQuality = showQuality(Quality);
        this.mAge = Float.valueOf(Age);
        this.mB = Coordinate.getDmsString(Coordinate.getDegreeFromRTK(B));
        this.mL = Coordinate.getDmsString(Coordinate.getDegreeFromRTK(L));
        this.mProgressB = Coordinate.getDegreeFromRTK(B);
        this.mProgressL = Coordinate.getDegreeFromRTK(L);
        this.mH = Double.valueOf(H);
        this.mDireB = DireB;
        this.mDireL = DireL;
        this.mTime = getLocalTime(time);
        this.mUseSate = Integer.valueOf(useSate);
        this.mN = 1;
        this.mE = 1;
        this.mZ = Double.valueOf(H);
    }

    private String showQuality(int quality) {
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
    public MyLocation(double B, double L, double H, long time, int UseSate) {
        this.mProgressB = B;
        this.mProgressL = L;
        this.mB = Coordinate.getDmsString(B);
        this.mL = Coordinate.getDmsString(L);
        this.mH = H;
        this.mZ = H;
        this.mTime = getCurTime(time);
        //根据正负来判断当前位于哪个半球，必须转化为double类型，转化成int类型显示为空指针
        this.mUseSate = UseSate;
        this.mQuality = "内置GPS定位";
        if (B > 0)
            this.mDireB = "N";
        else
            this.mDireB = "S";
        if (L > 0)
            this.mDireL = "E";
        else
            this.mDireL = "W";
    }

    public String getB() {
        return mB;
    }

    public String getL() {
        return mL;
    }

    public double getH() {
        return mH;
    }

    public String getTime() {
        return mTime;
    }

    public double getZ() {
        return mZ;
    }

    private String getCurTime(long mTime) {
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
