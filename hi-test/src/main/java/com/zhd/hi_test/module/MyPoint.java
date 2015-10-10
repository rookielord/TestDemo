package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/10/9.
 * 用来存放坐标点的N,E,Z数据
 */
public class MyPoint {
    private String name;
    private double mN;
    private double mE;
    private double mZ;

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public MyPoint(String name, double mN, double mE, double mZ) {
        this.name = name;
        this.mN = mN;
        this.mE = mE;
        this.mZ = mZ;
    }

    public double getmN() {
        return mN;
    }

    public void setmN(double mN) {
        this.mN = mN;
    }

    public double getmE() {
        return mE;
    }

    public void setmE(double mE) {
        this.mE = mE;
    }

    public double getmZ() {
        return mZ;
    }

    public void setmZ(double mZ) {
        this.mZ = mZ;
    }
}
