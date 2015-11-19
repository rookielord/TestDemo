package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/10/9.
 * 用来存放坐标点的N,E和名称的数据，主要用于绘图
 */
public class MyPoint {
    private String name;
    private double mN;
    private double mE;

    public MyPoint(String name, double N, double E) {
        this.name = name;
        this.mN = N;
        this.mE = E;

    }

    public String getName() {
        return name;
    }

    public double getN() {
        return mN;
    }

    public double getE() {
        return mE;
    }

}
