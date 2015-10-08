package com.zhd.hi_test.util;

/**
 * 工具类，处理各种杂项
 */
public class Coordinate {
    /**
     * 把NMEA格式ddmm.mmm表示的角度值转成以度为单位的角度值
     * @param ddmm
     * @return
     */
    public static double getDegree(String ddmm){
        if(ddmm.equals("")){
            return 0.0;
        }else {
            double value = Double.valueOf(ddmm);
            int degree = getIntegerPart(value / 100);
            double m = (value - degree * 100) / 60;
            return degree + m;
        }
    }

    /**
     * 将一个double型的数保留n位小数
     * @param num 要保留的double型数
     * @param n 要保留的小数位数
     * @return 保留相应小数位数的数值字符串
     */
    public static String reserveNDecimal(double num, int n){

        String str=String.valueOf(num);

        int dotPosition=str.indexOf(".");

        char[] digitChars=str.substring(dotPosition+1).toCharArray();

        if(n<digitChars.length){
            int last=Integer.valueOf(String.valueOf(digitChars[n-1]));
            String former=str.substring(0,dotPosition+n);
            int next=Integer.valueOf(String.valueOf(digitChars[n]));
            if(next<5){}else{
                last++;
            }
            return former+String.valueOf(last);
        }else if(n==digitChars.length){
            return String.valueOf(num);
        }else{
            String later="";
            for(int i=0; i<n-digitChars.length;i++){
                later+="0";
            }
            return String.valueOf(num)+later;
        }

    }

    /**
     * 如果一个double型数是一位数，则在前面加一个"0"凑成两个，如果是两位数，则不变
     * @param num
     * @return
     */
    public static String getZeroString(double num){
        if(num<10){
            return "0"+num;
        }else{
            return String.valueOf(num);
        }
    }

    /**
     * 一个int型数，参见{@link #getZeroString(double)},
     * @param num
     * @return
     */
    public static String getZeroString(int num){
        if(num<10){
            return "0"+num;
        }else{
            return String.valueOf(num);
        }
    }

    /**
     * 将一个以度为单位的角度值转成ddd:mm:ss.ss的格式的字符串
     * @param degree
     * @return
     */
    public static String getDmsString(double degree){
        int d=getIntegerPart(degree);
        int m=getIntegerPart(getFractionalPart(degree) * 60);
        double s=getFractionalPart(getFractionalPart(degree)*60)*60;
        return d+":"+getZeroString(m)+":"+getZeroString(Double.valueOf(reserveNDecimal(s, 2)));

    }

    /**
     * 获取一个double型数的整数部分
     * @param num
     * @return
     */
    public static int getIntegerPart(double num){
        String str=String.valueOf(num);
        String[] strArray=str.split("[.]");
        if(strArray.length==2){
            return Integer.valueOf(strArray[0]);
        }else{
            return 0;
        }
    }

    /**
     * 获取一个double型数的小数部分
     * @param num
     * @return
     */
    public static double getFractionalPart(double num){
        return num-getIntegerPart(num);
    }
}
