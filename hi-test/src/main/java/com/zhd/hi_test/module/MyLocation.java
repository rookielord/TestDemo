package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/9/23.
 * 获得位置信息，这个是直接从iRTK中开始获得数据
 * 需要显示到屏幕上的数据，其中包括有位置信息和时间信息
 */
public class MyLocation {
    private String mB;
    private String mL;
    private String mH;
    private String mTime;
    private String mDES;
    private int mType;

    //点的类型
    public static final int COOR_POINT = 1;
    public static final int LAYOUT_POINT = 2;
    public static final int CONTROL_POINT = 3;

    public MyLocation(String mB, String mL, String mH, String mDES, int mType) {
        this.mB = mB;
        this.mL = mL;
        this.mH = mH;
        this.mDES = mDES;
        this.mType = mType;
    }

    public MyLocation(String mB, String mL, String mH) {
        this.mB = mB;
        this.mL = mL;
        this.mH = mH;
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

    public String getmDES() {
        return mDES;
    }

    public void setmDES(String mDES) {
        this.mDES = mDES;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }
}
