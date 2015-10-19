package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/10/9.
 * 用来存放坐标点的N,E和名称的数据，主要用于绘图
 */
public class Point {
    private String name;
    private double mN;
    private double mE;

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Point(String name, double mN, double mE) {
        this.name = name;
        this.mN = mN;
        this.mE = mE;

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

}
