package com.zhd.hi_test.module;

import com.zhd.hi_test.Data;
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
    private String mH;
    private String mTime;
    private String mDireB;
    private String mDireL;
    private String mProgressB;
    private String mProgressL;

    public String getmDireB() {
        return mDireB;
    }

    public String getmDireL() {
        return mDireL;
    }

    public String getmProgressB() {
        return mProgressB;
    }

    public void setmProgressB(String mProgressB) {
        this.mProgressB = mProgressB;
    }

    public String getmProgressL() {
        return mProgressL;
    }

    public void setmProgressL(String mProgressL) {
        this.mProgressL = mProgressL;
    }

    //RTK传过来的数据
    public MyLocation(String B, String L, String H, String DireB, String DireL) {
        this.mB = String.valueOf(Coordinate.getDmsString(Coordinate.getLatitudeDegree(B)));
        this.mL = String.valueOf(Coordinate.getDmsString(Coordinate.getLongtitudeDegree(L)));
        this.mProgressB = String.valueOf(Coordinate.getLatitudeDegree(B));
        this.mProgressL = String.valueOf(Coordinate.getLongtitudeDegree(L));
        this.mH = H;
        this.mDireB = DireB;
        this.mDireL = DireL;
    }



    //内置GPS传过来的数据
    public MyLocation(String mB, String mL, String mH) {
        this.mProgressB = mB;
        this.mProgressL = mL;
        this.mB = Coordinate.getGPSdegree(mB);
        this.mL = Coordinate.getGPSdegree(mL);
        this.mH = mH;
        //根据正负来判断当前位于哪个半球，必须转化为double类型，转化成int类型显示为空指针
        double B = Double.valueOf(mB);
        double L = Double.valueOf(mL);
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

    public void setmB(String mB) {
        this.mB = mB;
    }

    public String getmL() {
        return mL;
    }

    public void setmL(String mL) {
        this.mL = mL;
    }

    public String getmH() {
        return mH;
    }

    public void setmH(String mH) {
        this.mH = mH;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

}
