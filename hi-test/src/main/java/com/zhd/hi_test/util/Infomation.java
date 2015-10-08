package com.zhd.hi_test.util;


import android.os.Handler;
import android.os.Message;

import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.Satellite;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 2015032501 on 2015/9/23.
 * 对所获得的字符串进行进行解析并将值传给接口
 */
public class Infomation {

    private static Handler mHandler;

    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    private static Pattern GGA_pattern = Pattern.compile("(?<=\\$GPGGA\\,).*?(?=\\*)");
    private static Pattern Satellite_pattern = Pattern.compile("(\\$GPGSV|\\$GLGSV|\\$BDGSV).*?(?=\\*)");
    //存放对应的数据
    private static ArrayList<Satellite> mSatellites = new ArrayList<Satellite>();
    private static MyLocation location;
    //用来存放临时的数据然后发送过去
    private static Object mTemps;
    //用来获取对应的字段
    private static Matcher mMacher;

    public static void setmInputMsg(String mInputMsg) {

        if (mHandler == null)
            return;
        //获得卫星信息
        mMacher = Satellite_pattern.matcher(mInputMsg);
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
        //1.获得位置信息和时间
        String[] info = group.split(",");
        String time = info[0];
        String B = info[1];
        String BSide = info[2];
        String L = info[3];
        String LSide = info[4];
        String HDPO = info[7];
        String H = info[8];
        //形成对象
        location = new MyLocation(B, L, H, null, MyLocation.COOR_POINT);
        Message m = Message.obtain();
        m.obj = location;
        m.what = 1;
        mHandler.sendMessage(m);
    }

    /**
     * 根据获取的总条数和当前条数来拼凭借同一时间的卫星数据信息，遇到了一次如果只有头的数据，
     * 是解析每一条数据例如：$GPGSV,11,4,36,50,46,122,42,40,21,257,42
     *
     * @param group
     */
    private static void getSatelliteInfo(String group) {
        //1.获得类型
        String[] info = group.split(",");
        //1.1获得该时间的总共条数（总的GSV语句电文数）
        int allnum = Integer.parseInt(info[1]);
        //1.2获得GSV是该条目的第几条
        int curerntnum = Integer.parseInt(info[2]);
        //1.3解析该时间段的数据
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
        //4.获取对应数据创建卫星对象,注意空值。需要对空值进行判断
        Satellite s = null;
        for (int i = 0; i < num; i++) {
            int loc = 4 * (i + 1);
            //1.4创建卫星数据传入list集合中
            //最后一个信噪比可能为空值，如果为空值则赋值0,表示没有收到信号
            s = new Satellite(info[loc], info[loc + 1], info[loc + 2], info[loc + 3], type);
            mSatellites.add(s);
        }

        //1.5传输出去
        //1.5.1这里进行判断，如果currentnum<allnum的话，就不会发送而继续添加，只有当currentnum==allnum才发送
        //赋值集合，如果使用的是同一个集合的话会出现同步错误，因为线程一边在加，然后一边在取,就会造成这个错误
        if (curerntnum==allnum){
            mTemps = mSatellites.clone();
            Message m = Message.obtain();
            m.obj = mTemps;
            m.what = 2;
            mHandler.sendMessage(m);
            mSatellites.clear();
            //然后清空其中的东西
        }
    }

}
