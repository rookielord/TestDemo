package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/9/9.
 * 将Satellite对象的数据转化为画在平面上所需要的数据
 */
public class StarPoint {

    private int mNum;
    private double mX;
    private double mY;
    private int mLevel;
    private int mType;

    public int getmType() {
        return mType;
    }

    public StarPoint(double mX, double mY, int mNum, int mLevel, int mType) {
        this.mX = mX;
        this.mY = mY;
        this.mNum = mNum;
        this.mLevel = mLevel;
        this.mType = mType;
    }

    public int getmLevel() {
        return mLevel;
    }

    public double getmX() {
        return mX;
    }

    public double getmY() {
        return mY;
    }

    public int getmNum() {
        return mNum;
    }
}
