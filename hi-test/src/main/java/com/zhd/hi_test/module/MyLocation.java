package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/9/23.
 * 获得位置信息，这个是直接从iRTK中开始获得数据
 * 需要显示到屏幕上的数据，其中包括有位置信息和时间信息
 */
public class MyLocation {
    private String B;
    private String L;
    private String H;
    private String Time;

    public MyLocation(String b, String l, String h, String time) {
        B = b;
        L = l;
        H = h;
        Time = time;
    }

    public String getB() {
        return B;
    }

    public String getL() {
        return L;
    }

    public String getTime() {
        return Time;
    }

    public String getH() {
        return H;
    }

    public void setB(String b) {
        B = b;
    }

    public void setL(String l) {
        L = l;
    }

    public void setH(String h) {
        H = h;
    }

    public void setTime(String time) {
        Time = time;
    }
}
