package com.zhd.hi_test.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 坐标转化类
 */
public class Coordinate {

    /**
     * 将WGS84转化为高斯投影的内容
     * a和e^2
     */
    private static final double A = 6378137.00;
    private static final double EE = Math.sqrt(0.00669437999013);
    private static final double PI = Math.PI;

    /**
     * 这个是iRTK的数据类型
     * 把NMEA格式ddmm.mmm表示的角度值转成以度为
     * 弧度值
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
     * 将一个double型的数保留n位小数
     * 即将小数点后面的位数结局
     *
     * @param value 要保留的double型数
     * @param n     要保留的小数位数
     * @return 保留数值过后的double
     */
    public static double saveAfterPoint(double value, int n) {
        String str = String.valueOf(value);
        String[] info = str.split("\\.");
        String front;
        String later;
        //判断n的合理范围
        if (n < 0) {
            return 0;
        }
        //如果小数位不足的话，补足0
        if (n > info[1].length()) {
            while (n > info[1].length()) {
                info[1] += "0";
            }
        }
        //分别获取小数点前后的值
        front = info[0];
        later = info[1].substring(0, n);
        //拼接整个数字,并转化为double型
        double num = Double.valueOf(front + "." + later);
        return num;
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

    public static HashMap<String, Double> getCoordinateXY(double latitude, double longtitude) {
        HashMap<String, Double> info = new HashMap<String, Double>();
        //经过转化后x和y的坐标
        double n;
        double e;
        double b = Math.sqrt(A * A * (1 - EE * EE));
        double c = A * A / b;
        double epp = Math.sqrt((A * A - b * b) / b / b);
        //1.获得弧度
        double Radianlatitude = Method.degreeToRadian(latitude);
        double Radianlongtitude = Method.degreeToRadian(longtitude);
        //2.将弧度重新转化为度
        int deglon = Integer.valueOf((int) (Radianlongtitude * 180 / PI));
        //3.默认为3度带num为带号，midlong为中央经度
        int num;
        double midlong;
        num = (deglon / 3);
        midlong = num * 3 * PI / 180;

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

        //获得转化后的X和Y的值
        n = Nscnb * lp * lp / 2.0 + Nscnb * cosb * cosb * Math.pow(lp, 4) * (5 - t * t + 9 * ita * ita +
                4 * Math.pow(ita, 4)) / 24.0 + Nscnb * Math.pow(cosb, 4) * Math.pow(lp, 6) * (61 - 58 * t * t +
                Math.pow(t, 4)) / 720.0 + X;

        e = Ncosb * Math.pow(lp, 1) + Ncosb * cosb * cosb * (1 - t * t + ita * ita) / 6.0 * Math.pow(lp, 3) +
                Ncosb * Math.pow(lp, 5) * Math.pow(cosb, 4) * (5 - 18 * t * t + Math.pow(t, 4) + 14 * ita * ita -
                        58 * ita * ita * t * t) / 120.0;
        if (e < 0)
            e += 500000;
        //将其值进行处理，不是四舍五入而是单纯的保存
        n=saveAfterPoint(n,5);
        e=saveAfterPoint(e,5);
        info.put("n", n);
        info.put("e", e);
        return info;
    }

}
