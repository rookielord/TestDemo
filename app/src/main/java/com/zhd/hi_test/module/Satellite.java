package com.zhd.hi_test.module;

import android.location.GpsSatellite;

/**
 * 当前类主要是按照系统自带的GPSSatellite类进行封装，以便处理相同的数据
 * Created by 2015032501 on 2015/9/23.
 * <4> 卫星编号，01至32。
 * 　<5> 卫星仰角，00至90度。
 * 　<6> 卫星方位角，000至359度。实际值。
 * 　<7> 信噪比（C/No），00至99dB；无表未接收到讯号。
 * satellite.getElevation(); //卫星仰角
 * satellite.getAzimuth();   //卫星方位角
 * satellite.getSnr();       //信噪比
 * satellite.getPrn();       //伪随机数，可以认为他就是卫星的编号
 */
public class Satellite {
    private float mElevation;
    private float mAzimuth;
    private float mSnr;
    private int mPrn;
    private int mType;

    public float getElevation() {
        return mElevation;
    }

    public float getAzimuth() {
        return mAzimuth;
    }

    public float getSnr() {
        return mSnr;
    }

    public int getPrn() {
        return mPrn;
    }

    public int getType() {
        return mType;
    }


    //卫星类型分类
    public static final int GPS = 1;
    public static final int GLONASS = 2;
    public static final int BD = 3;
    public static final int SBAS = 4;

    /**
     * 卫星的数据
     * @param Prn 编号
     * @param Elevation 仰角
     * @param Azimuth 方位角
     * @param Snr 信噪比
     * @param Type 类型
     */
    public Satellite(String Prn, String Elevation, String Azimuth, String Snr, int Type) {
        this.mElevation = Float.valueOf(Elevation);
        this.mAzimuth = Float.valueOf(Azimuth);
        this.mSnr = Float.valueOf(Snr);
        this.mPrn = Integer.valueOf(Prn);
        this.mType = Type;
    }
}
