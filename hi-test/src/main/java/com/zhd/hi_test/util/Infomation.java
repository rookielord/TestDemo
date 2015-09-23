package com.zhd.hi_test.util;


import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.MyPoint;
import com.zhd.hi_test.module.Satellite;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 2015032501 on 2015/9/23.
 */
public class Infomation {

    private static Pattern GGA_pattern = Pattern.compile("\\$GPGGA(.*)\\*");
    private static Pattern GPS_pattern = Pattern.compile("\\$GPGSV(.*)\\*");
    private static Pattern GLONASS_pattern = Pattern.compile("\\$GLGSV(.*)\\*");
    private static Pattern BD_pattern = Pattern.compile("\\$BDGSV(.*)\\*");

    //存放对应的数据
    private static ArrayList<Satellite> mSatellites = new ArrayList<Satellite>();
    private static ArrayList<MyLocation> mPoints = new ArrayList<MyLocation>();
    //用来获取对应的字段
    private static Matcher mMacher;

    public static void setmInputMsg(String mInputMsg) {
        //获取GPS卫星的信息
        mMacher = GPS_pattern.matcher(mInputMsg);
        while (mMacher.find()) {
            getSatelliteInfo(mMacher.group());
        }
        //获取GLONASS卫星信息
        mMacher = GLONASS_pattern.matcher(mInputMsg);
        while (mMacher.find()) {
            getSatelliteInfo(mMacher.group());
        }
        //获取BD卫星信息
        mMacher = BD_pattern.matcher(mInputMsg);
        while (mMacher.find()) {
            getSatelliteInfo(mMacher.group());
        }
        //获取位置的信息
        mMacher = GGA_pattern.matcher(mInputMsg);
        while (mMacher.find()) {
            getLoactionInfo(mMacher.group());
        }
    }

    private static void getLoactionInfo(String group) {

    }

    private static void getSatelliteInfo(String group) {
        //1.获得类型
        String[] info = group.split(",");
        //2.获得该数据中有多长，即有多少个卫星
        int num = (info.length - 4) / 4;
        //3.根据卫星数量来进行循环
        String str_type = info[0];
        int type = -1;
        if (str_type.equals("$GPGSV"))
            type = Satellite.GPS;
        else if (str_type.equals("$GLGSV"))
            type = Satellite.GLONASS;
        else if (str_type.equals("$BDGSV"))
            type = Satellite.BD;
        //4.获取对应数据创建卫星对象
        Satellite s = null;
        for (int i = 0; i < num; i++) {
            int loc = 4 * (i + 1);
            if (i == num - 1)
                info[loc+3]=info[loc+3].substring(0,1);
            s = new Satellite(info[loc], info[loc + 1], info[loc + 2], info[loc + 3], type);
            mSatellites.add(s);
        }
    }

    public static ArrayList<Satellite> getmSatellites() {
        return mSatellites;
    }
}
