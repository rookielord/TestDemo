package com.zhd.hi_test.module;

/**
 * 当前类主要是按照系统自带的GPSSatellite类进行封装，以便处理相同的数据
 * Created by 2015032501 on 2015/9/23.
 *  <4> 卫星编号，01至32。
 *　<5> 卫星仰角，00至90度。
 *　<6> 卫星方位角，000至359度。实际值。
 *　<7> 信噪比（C/No），00至99dB；无表未接收到讯号。
 * satellite.getElevation(); //卫星仰角
 * satellite.getAzimuth();   //卫星方位角
 * satellite.getSnr();       //信噪比
 * satellite.getPrn();       //伪随机数，可以认为他就是卫星的编号
 *
 */
public class Satellite {
    private String mElevation;
    private String mAzimuth;
    private String mSnr;
    private String mPrn;

    public String getmElevation() {
        return mElevation;
    }

    public String getmAzimuth() {
        return mAzimuth;
    }

    public String getmSnr() {
        return mSnr;
    }

    public String getmPrn() {
        return mPrn;
    }

    public int getmType() {
        return mType;
    }

    private int mType;

    //卫星类型分类
    public static final int GPS=1;
    public static final int GLONASS=2;
    public static final int BD=3;
    public static final int SBAS=4;

    public Satellite(String mPrn, String mElevation, String mAzimuth, String mSnr, int mType) {
        this.mElevation = mElevation;
        this.mAzimuth = mAzimuth;
        this.mSnr = mSnr;
        this.mPrn = mPrn;
        this.mType = mType;
    }
}
