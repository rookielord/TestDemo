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

    public static void setHandler(Handler handler){
        mHandler=handler;
    }

    private static Pattern GGA_pattern = Pattern.compile("(?<=\\$GPGGA\\,).*?(?=\\*)");
    private static Pattern Satellite_pattern=Pattern.compile("(\\$GPGSV|\\$GLGSV|\\$BDGSV).*?(?=\\*)");
    //存放对应的数据
    private static ArrayList<Satellite> mSatellites = new ArrayList<Satellite>();
    private static MyLocation location;
    //用来存放临时的数据然后发送过去
    private static Object mTemps;
    //用来获取对应的字段
    private static Matcher mMacher;

    public static void setmInputMsg(String mInputMsg) {

        if (mHandler==null)
            return;
        //获得卫星信息
        mMacher=Satellite_pattern.matcher(mInputMsg);
        while (mMacher.find()){
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
        String[] info=group.split(",");
        String time=info[0];
        String B=info[1];
        String BSide=info[2];
        String L=info[3];
        String LSide=info[4];
        String HDPO=info[7];
        String H=info[8];
        //形成对象
        location=new MyLocation(B,L,H,null,MyLocation.COOR_POINT);
        Message m=Message.obtain();
        m.obj=location;
        m.what=1;
        mHandler.sendMessage(m);
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
        //4.获取对应数据创建卫星对象,注意空值。需要对空值进行判断
        Satellite s = null;
        for (int i = 0; i < num; i++) {
            int loc = 4 * (i + 1);
            //最后一个信噪比可能为空值，如果为空值则赋值0,表示没有收到信号
            s = new Satellite(info[loc], info[loc + 1], info[loc + 2], info[loc + 3], type);
            mSatellites.add(s);
        }
        //赋值集合，如果使用的是同一个集合的话会出现同步错误，因为线程一边在加，然后一边在取,就会造成这个错误
        mTemps=mSatellites.clone();
        Message m=Message.obtain();
        m.obj=mTemps;
        m.what=2;
        mHandler.sendMessage(m);
        mSatellites.clear();
        //然后清空其中的东西
    }

}
