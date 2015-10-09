package com.zhd.hi_test.module;

import com.zhd.hi_test.util.Coordinate;

/**
 * Created by 2015032501 on 2015/9/23.
 *
 * 需要显示到屏幕上的数据，其中包括有位置信息和时间信息
 * 最好是全部的数据都存满，不要留下空隙
 */
public class MyLocation {
    private String mB;
    private String mL;
    private String mH;
    private String mTime;
    private String mDireB;
    private String mDireL;

    /**
     *
     * @param mB
     * @param mL
     * @param mH
     * @param mTime
     * @param mDireB
     * @param mDireL
     */
    public MyLocation(String mB, String mL, String mH, String mTime, String mDireB, String mDireL) {
        this.mB = mB;
        this.mL = mL;
        this.mH = mH;
        this.mTime = mTime;
        this.mDireB = mDireB;
        this.mDireL = mDireL;
    }

    public MyLocation(String mB, String mL, String mH, String mTime) {
        this.mB = mB;
        this.mL = mL;
        this.mH = mH;
        this.mTime = mTime;
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
