package com.zhd.hi_test.util;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.module.Project;

import java.util.HashMap;

/**
 * 坐标转化相关的类。需要进行BLH到NEZ的转化。主要是涉及到
 * 再进行坐标转化的时候更具对应的坐标系统转化根据全局变量project来获得
 */
public class Coordinate {

    /**
     * 将WGS84转化为高斯投影的内容
     * a和e^2
     * a:长半轴
     * e^2:第一偏心率平方
     * num:带号数
     * midlong:中央经度
     */

    private static double A;
    private static double EE;
    private static double PI = Math.PI;
    private static int num;
    private static double midlong;

    /**
     * 这个是iRTK的数据类型
     * 把NMEA格式ddmm.mmm表示的角度值转成以度为
     *
     * @param ddmm 传过来的数据
     * @return
     */
    public static double getLatitudeDegree(String ddmm) {
        if (ddmm.equals("")) {
            return 0.0;
        } else {
            double value = Double.valueOf(ddmm);
            int degree = getIntegerPart(value / 100);
            double m = (value - degree * 100) / 60;
            return degree + m;
        }
    }

    /**
     * 获得经度的度数
     * 转化为弧度数，
     *
     * @param dddmm
     * @return
     */
    public static double getLongtitudeDegree(String dddmm) {
        if (dddmm.equals(""))
            return 0;
        else {
            double value = Double.valueOf(dddmm);
            int degree = getIntegerPart(value / 100);
            double m = (value - degree * 100) / 60;
            return degree + m;
        }
    }

    /**
     * 将内置GPS的经度，转化为IRTK的样式的纬度
     * 将度=ddd.dddddd=》dddmm.mmmm
     * 123.556666=>12355.6666
     *
     * @param longtitude
     * @return
     */
    public static String getLongtitudeIRTK(String longtitude) {
        if (longtitude.equals("0"))
            return "0";
        else {
            double value = Double.valueOf(longtitude);
            //将小数点前的转化为整数
            int degree = getIntegerPart(value);
            //将小数点后转化为mm
            double mm = saveAfterPoint((value - degree) * 60, 4);
            //两者进行字符串拼接
            return String.valueOf(degree) + String.valueOf(mm);
        }
    }

    /**
     * 将内置GPS的纬度，转化为IRTK样式的经度，用于存储
     * dd.dddd……转化为ddmm.mmmm的格式
     *
     * @param latitude
     * @return
     */
    public static String getLatitudeIRTK(String latitude) {
        if (latitude.equals("0")) {
            return "0";
        } else {
            //转化为double类型
            double value = Double.valueOf(latitude);
            //将小数点前的转化为整数
            int degree = getIntegerPart(value);
            //将小数点后转化为mm
            double mm = saveAfterPoint((value - degree) * 60, 4);
            //两者进行字符串拼接
            return String.valueOf(degree) + String.valueOf(mm);
        }
    }

    /**
     * 将一个double型的数保留n位小数
     * 即将小数点后面的位数结局,使用四舍五入来计算
     *
     * @param value 要保留的double型数
     * @param n     要保留的小数位数
     * @return 保留数值过后的double
     */
    public static double saveAfterPoint(double value, int n) {
        //使用math.round()只会保留整数位
        //保留位数需要乘以10^n移动小数点
        //最后再把小数位移动过来
        double num = Math.pow(10, n);
        return (Math.round(value * num) / num);
    }

    /**
     * 如果一个double型数是一位数，则在前面加一个"0"凑成两个，如果是两位数，则不变
     *
     * @param num
     * @return
     */
    public static String getZeroString(double num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return String.valueOf(num);
        }
    }

    /**
     * 一个int型数，参见{@link #getZeroString(double)},
     *
     * @param num
     * @return
     */
    public static String getZeroString(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return String.valueOf(num);
        }
    }

    /**
     * 将一个以度为单位的角度值转成ddd:mm:ss.ss的格式的字符串
     * 这个是经度转化？
     *
     * @param degree
     * @return
     */
//    public static String getDmsString(double degree) {
//        int d = getIntegerPart(degree);
//        int m = getIntegerPart(getFractionalPart(degree) * 60);
//        double s = getFractionalPart(getFractionalPart(degree) * 60) * 60;
//        return d + ":" + getZeroString(m) + ":" + getZeroString(Double.valueOf(reserveNDecimal(s, 2)));
//
//    }

    /**
     * 获取一个double型数的整数部分
     *
     * @param num
     * @return
     */
    public static int getIntegerPart(double num) {
        String str = String.valueOf(num);
        String[] strArray = str.split("\\.");
        if (strArray.length == 2) {
            return Integer.valueOf(strArray[0]);
        } else {
            return 0;
        }
    }

    /**
     * 获取一个double型数的小数部分
     *
     * @param num
     * @return
     */
    public static double getFractionalPart(double num) {
        return num - getIntegerPart(num);
    }

    /**
     * 应该对应相对的坐标系统来进行转化坐标，所采用的坐标系统从
     *
     * @param latitude   纬度
     * @param longtitude 经度
     * @return 返回HASH表，转化后的B,L的度数
     */
    public static HashMap<String, Double> getCoordinateXY(double latitude, double longtitude, Project project) {

        //根据传入的coordinate来判断所用的变量
        getConvertValue(project);
        HashMap<String, Double> info = new HashMap<>();
        //经过转化后x和y的坐标
        double n;
        double e;
        double b = Math.sqrt(A * A * (1 - EE * EE));
        double c = A * A / b;
        double epp = Math.sqrt((A * A - b * b) / b / b);
        //1.获得弧度
        double Radianlatitude = Method.degreeToRadian(latitude);
        double Radianlongtitude = Method.degreeToRadian(longtitude);
        //2.将当前经度的弧度转化为经度数
        int deglon = (int) (Radianlongtitude * 180 / PI);
        //3.默认为3度带num为带号，midlong为中央经度
        if (project.getmGuass().equals("3度带")) {
            num = (deglon / 3);
            midlong = num * 3 * PI / 180;
        } else {
            num = (deglon / 6 + 1);
            midlong = (6 * num - 3) / 180.0 * PI;
        }
        double lp = Radianlongtitude - midlong;
        double N = c / Math.sqrt(1 + epp * epp * Math.cos(Radianlatitude) * Math.cos(Radianlatitude));
        double M = c / Math.pow(1 + epp * epp * Math.cos(Radianlatitude) * Math.cos(Radianlatitude), 1.5);
        double ita = epp * Math.cos(Radianlatitude);
        double t = Math.tan(Radianlatitude);
        double Nscnb = N * Math.sin(Radianlatitude) * Math.cos(Radianlatitude);
        double Ncosb = N * Math.cos(Radianlatitude);
        double cosb = Math.cos(Radianlatitude);

        double X;
        double m0, m2, m4, m6, m8;
        double a0, a2, a4, a6, a8;
        m0 = A * (1 - EE * EE);
        m2 = 3.0 / 2.0 * m0 * EE * EE;
        m4 = 5.0 / 4.0 * EE * EE * m2;
        m6 = 7.0 / 6.0 * EE * EE * m4;
        m8 = 9.0 / 8.0 * EE * EE * m6;

        a0 = m0 + m2 / 2.0 + 3.0 / 8.0 * m4 + 5.0 / 16.0 * m6 + 35.0 / 128.0 * m8;
        a2 = m2 / 2 + m4 / 2 + 15.0 / 32.0 * m6 + 7.0 / 16.0 * m8;
        a4 = m4 / 8.0 + 3.0 / 16.0 * m6 + 7.0 / 32.0 * m8;
        a6 = m6 / 32.0 + m8 / 16.0;
        a8 = m8 / 128.0;

        double B = Radianlatitude;
        double sb = Math.sin(B);
        double cb = Math.cos(B);
        double s2b = sb * cb * 2;
        double s4b = s2b * (1 - 2 * sb * sb) * 2;
        double s6b = s2b * Math.sqrt(1 - s4b * s4b) + s4b * Math.sqrt(1 - s2b * s2b);

        X = a0 * B - a2 / 2.0 * s2b + a4 * s4b / 4.0 - a6 / 6.0 * s6b;                 //X为子午线弧长

        //获得转化后的N和E的值
        n = Nscnb * lp * lp / 2.0 + Nscnb * cosb * cosb * Math.pow(lp, 4) * (5 - t * t + 9 * ita * ita +
                4 * Math.pow(ita, 4)) / 24.0 + Nscnb * Math.pow(cosb, 4) * Math.pow(lp, 6) * (61 - 58 * t * t +
                Math.pow(t, 4)) / 720.0 + X;

        e = Ncosb * Math.pow(lp, 1) + Ncosb * cosb * cosb * (1 - t * t + ita * ita) / 6.0 * Math.pow(lp, 3) +
                Ncosb * Math.pow(lp, 5) * Math.pow(cosb, 4) * (5 - 18 * t * t + Math.pow(t, 4) + 14 * ita * ita -
                        58 * ita * ita * t * t) / 120.0;
        if (e < 0)
            e += 500000;
        //将其值进行处理，不是四舍五入而是单纯的保存
        n = saveAfterPoint(n, 5);
        e = saveAfterPoint(e, 5);
        info.put("n", n);
        info.put("e", e);
        return info;
    }

    private static void getConvertValue(Project project) {
        switch (project.getmCoordinate()) {
            case "国家2000坐标系":
                A = 6378137.0;
                EE = Math.sqrt(0.0066943802290);
                break;
            case "WGS84坐标系":
                A = 6378137.00;
                EE = Math.sqrt(0.00669437999013);
                break;
            case "北京54坐标系":
                A = 6378245.0;
                EE = Math.sqrt(0.006693421622966);
                break;
            case "西安80坐标系":
                A = 6378140;
                EE = Math.sqrt(0.00669438499959);
                break;
        }
    }

}