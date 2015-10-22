package com.zhd.hi_test.util;


import com.zhd.hi_test.module.MyProject;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * 坐标转化相关的类。需要进行BLH到NEZ的转化。主要是涉及到
 * 再进行坐标转化的时候更具对应的坐标系统转化根据全局变量project来获得
 */
public class Coordinate {

    /**
     * 将WGS84转化为高斯投影的内容
     * A:长半轴
     * B:短半径
     */
    static final double R2D = 180 / Math.PI;
    static final double D2R = Math.PI / 180;
    private static double A;
    private static double B;

    /**
     * 这个是iRTK的数据类型
     * 把NMEA格式ddmm.mmm表示的角度值转成以度为单位的大小
     *
     * @param ddmm 传过来的数据
     * @return
     */
    public static double getDegreeFromRTK(String ddmm) {
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
     * 将dd:mm:ss.ssss的形式转化为dd.dddd的形式
     *
     * @param ddmmss
     * @return
     */
    public static double getDegreeFromSQL(String ddmmss,String Dire) {
        String[] value = ddmmss.split(":");
        double res=Integer.valueOf(value[0]) + Double.valueOf(value[1]) / 60 + Double.valueOf(value[2]) / 3600;
        if (Dire.equals("N")|| Dire.equals("E"))
            return res;
        else
            return -res;
    }

    /**
     * 将一个double型的数保留n位小数
     * 即将小数点后面的位数结局,使用四舍五入来计算
     *
     * @param value 要保留的double型数
     * @param n     要保留的小数位数
     * @return 保留数值过后的double
     */
    public static String saveAfterPoint(double value, int n) {
        String pattern = "00.";
        for (int i = 0; i < n; i++) {
            pattern = pattern + "0";
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    /**
     * 将dd.dddd=>dd:mm:ss.sssss的格式
     * 将一个以度为单位的角度值转成ddd:mm:ss.ss的格式的字符串
     *
     * @param degree
     * @return
     */
    public static String getDmsString(double degree) {
        //获取度数的整数部分==度的大小
        int d = getIntegerPart(degree);
        //将度数的小数位*60之后获取整数位==分位的大小
        int m = getIntegerPart(getFractionalPart(degree) * 60);
        //获取分为的小数位*60==秒位的大小
        double s = getFractionalPart(getFractionalPart(degree) * 60) * 60;
        return d + ":" + getZeroString(m) + ":" + saveAfterPoint(s, 5);
    }

    /**
     * 获得度分秒
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
    public static HashMap<String, String> getCoordinateXY(double latitude, double longtitude, MyProject project) {

        double radB = degreeToRadian(latitude);
        double radL = degreeToRadian(longtitude);

        //根据传入的coordinate来判断所用的变量
        getConvertValue(project);
        double x;
        double y;
        double FstE, SndE, FstESquare, SndESquare;
        FstE = Math.sqrt(A * A - B * B) / A;
        SndE = Math.sqrt(A * A - B * B) / B;
        FstESquare = FstE * FstE;
        SndESquare = SndE * SndE;
        double m0, m2, m4, m6, m8;
        m0 = A * (1 - FstESquare);
        m2 = 3 * FstESquare * m0 / 2;
        m4 = 5 * FstESquare * m2 / 4;
        m6 = 7 * FstESquare * m4 / 6;
        m8 = 9 * FstESquare * m6 / 8;
        double a0, a2, a4, a6, a8;
        a0 = m0 + m2 / 2 + 3 * m4 / 8 + 5 * m6 / 16 + 35 * m8 / 128;
        a2 = m2 / 2 + m4 / 2 + 15 * m6 / 32 + 7 * m8 / 16;
        a4 = m4 / 8 + 3 * m6 / 16 + 7 * m8 / 32;
        a6 = m6 / 32 + m8 / 16;
        a8 = m8 / 128;
        double X;
        X = a0 * radB - a2 * Math.sin(2 * radB) / 2 + a4 * Math.sin(4 * radB) / 4 - a6 * Math.sin(6 * radB) / 6 + a8 * Math.sin(8 * radB) / 8;
        double W;
        W = Math.sqrt(1 - FstESquare * Math.sin(radB) * Math.sin(radB));
        double N;
        N = A / W;
        double t;
        t = Math.tan(radB);
        double EtaSqure;
        EtaSqure = SndESquare * Math.cos(radB) * Math.cos(radB);
        double DeltaL;
        DeltaL = (radL * R2D - 114) * D2R;
        x = X + N * Math.sin(radB) * Math.cos(radB) * DeltaL * DeltaL / 2 +
                N * Math.sin(radB) * Math.pow(Math.cos(radB), 3) * (5 - t * t + 9 * EtaSqure + 4 * EtaSqure * EtaSqure) * Math.pow(DeltaL, 4) / 24 +
                N * Math.sin(radB) * Math.pow(Math.cos(radB), 5) * (61 - 58 * t * t + Math.pow(t, 4)) * Math.pow(DeltaL, 6) / 720;
        y = N * Math.cos(radB) * DeltaL + N * Math.pow(Math.cos(radB), 3) * (1 - t * t + EtaSqure) * Math.pow(DeltaL, 3) / 6 +
                N * Math.pow(Math.cos(radB), 5) * (5 - 18 * t * t + Math.pow(t, 4) +
                        14 * EtaSqure - 58 * EtaSqure * t * t) * Math.pow(DeltaL, 5) / 120;

        if (y < 0)
            y += 500000;

        HashMap<String, String> info = new HashMap<>();
        info.put("n", saveAfterPoint(x, 5));
        info.put("e", saveAfterPoint(y, 5));
        return info;
    }

    private static void getConvertValue(MyProject myProject) {
        switch (myProject.getmCoordinate()) {
            case "国家2000坐标系":
                A = 6378137.0;
                B = 6356752.31414;
                break;
            case "WGS84坐标系":
                A = 6378137.00;
                B = 6356752.3142;
                break;
            case "北京54坐标系":
                A = 6378245.0;
                B = 6356863.0188;
                break;
            case "西安80坐标系":
                A = 6378140;
                B = 6356755;
                break;
        }
    }

    public static double degreeToRadian(double degree) {
        return (degree * Math.PI) / 180.0d;
    }

}
