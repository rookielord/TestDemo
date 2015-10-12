package com.zhd.hi_test.module;

import com.zhd.hi_test.util.Coordinate;

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

    /**
     * @param mB
     * @param mL
     * @param mH
     * @param mTime
     * @param mDireB
     * @param mDireL
     */
    public MyLocation(String mB, String mL, String mH, String mTime, String mDireB, String mDireL) {
        this.mB = String.valueOf(Coordinate.saveAfterPoint(Double.valueOf(mB), 4));
        this.mL = String.valueOf(Coordinate.saveAfterPoint(Double.valueOf(mL), 4));
        this.mProgressB = String.valueOf(Coordinate.getLatitudeDegree(this.getmB()));
        this.mProgressL = String.valueOf(Coordinate.getLongtitudeDegree(this.getmL()));
        this.mH = mH;
        this.mTime = mTime;
        this.mDireB = mDireB;
        this.mDireL = mDireL;
    }

    public MyLocation(String mB, String mL, String mH, String mTime) {
        this.mProgressB = mB;
        this.mProgressL = mL;
        //1.保留小数位数，经度和纬度都保留6位,原始数据
        //2.转化为字符串，然后转化成IRTK格式
        this.mB = Coordinate.getLatitudeIRTK(mB);
        this.mL = Coordinate.getLongtitudeIRTK(mL);

        this.mH = mH;
        this.mTime = mTime;
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
