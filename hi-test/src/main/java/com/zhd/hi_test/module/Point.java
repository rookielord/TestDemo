package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/9/21.
 * 每个碎步点的类里面包含多个字段
 *
 *  "name varchar(10) ," +
 "B varchar(32) ," +
 "L varchar(32) ," +
 "H varchar(32) ," +
 "N varchar(32) ," +
 "E varchar(32) ," +
 "Z varchar(32) ," +
 "time varchar(32),"+
 "NRMS varchar(32),"+
 "ERMS varchar(32),"+
 "ZRMS varchar(32),"+
 "DES text)
 */
public class Point {
    private String name;
    private String B;
    private String L;
    private String H;
    private String N;
    private String E;
    private String Z;
    private String time;
    private String NRMS;
    private String ERMS;
    private String ZRMS;
    private String DES;

    public Point(String name, String n, String e, String z) {
        this.name = name;
        N = n;
        E = e;
        Z = z;
    }

    public Point(String name, String b, String l, String h, String n, String e, String z, String time, String NRMS, String ERMS, String ZRMS, String DES) {
        this.name = name;
        B = b;
        L = l;
        H = h;
        N = n;
        E = e;
        Z = z;
        this.time = time;
        this.NRMS = NRMS;
        this.ERMS = ERMS;
        this.ZRMS = ZRMS;
        this.DES = DES;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getL() {
        return L;
    }

    public void setL(String l) {
        L = l;
    }

    public String getH() {
        return H;
    }

    public void setH(String h) {
        H = h;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public String getE() {
        return E;
    }

    public void setE(String e) {
        E = e;
    }

    public String getZ() {
        return Z;
    }

    public void setZ(String z) {
        Z = z;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNRMS() {
        return NRMS;
    }

    public void setNRMS(String NRMS) {
        this.NRMS = NRMS;
    }

    public String getERMS() {
        return ERMS;
    }

    public void setERMS(String ERMS) {
        this.ERMS = ERMS;
    }

    public String getZRMS() {
        return ZRMS;
    }

    public void setZRMS(String ZRMS) {
        this.ZRMS = ZRMS;
    }

    public String getDES() {
        return DES;
    }

    public void setDES(String DES) {
        this.DES = DES;
    }
}
