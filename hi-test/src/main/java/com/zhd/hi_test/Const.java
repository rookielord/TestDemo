package com.zhd.hi_test;



import com.zhd.hi_test.interfaces.IConnect;
import com.zhd.hi_test.module.ConnectInfo;
import com.zhd.hi_test.module.MyProject;
import com.zhd.hi_test.module.Satellite;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 2015032501 on 2015/9/18.
 * 全局变量
 * 包含项目对象Project
 * 包含ZHD_TEST路径
 */
public class Const {
    public static final int BlueToothConncet = 1;
    public static final int InnerGPSConnect = 2;
    public static final int NoneConnect = 0;
    //当前打开的项目对象
    private static MyProject mMyProject;
    //HI_TEST的路径
    private static String mPath;
    //0为未连接;1为蓝牙连接;2为内置GPS
    private static int mConnectType = 0;
    public static boolean IsConnected = false;
    private static double mheight = 0;
    //设置返回是否允许启动蓝牙
    public static final int REQUEST_CODE = 1;
    //启动返回得到地址
    public static final int DEVICE_MESSAGE = 2;
    //判断是否可以被其它设备搜索
    public static final int DISCOVERED = 3;
    //判断GPS是否开启
    public static final int GPS_REQUEST = 4;
    //当前的连接信息，为了在第二次打开的时候用
    private static String mInfo = "设备尚未连接";
    //判断当前是否解析了日期信息，如果没有则在刷新位置信息的时候，获取手机本身的日期如果有，则不会去获取手机本身
    public static boolean HasDataInfo=false;
    public static final String PFNAME = "config";//SharePreference文件名
    public static final String ISUPDATA = "updata";//是否检查版本更新
    //判断当前是否有PDOP数据
    public static boolean HasPDOP=false;

    public static ConnectInfo Info=new ConnectInfo();
    //当前的卫星数据
    public static List<Satellite> satellites=new ArrayList<>();

    public static IConnect mConnect;

    public static IConnect getmConnect() {
        return mConnect;
    }

    public static void setmConnect(IConnect mConnect) {
        Const.mConnect = mConnect;
    }

    public static String getmInfo() {
        return mInfo;
    }

    public static void setmInfo(String mInfo) {
        Const.mInfo = mInfo;
    }

    public static MyProject getmProject() {
        return mMyProject;
    }

    public static void setProject(MyProject mMyProject) {
        Const.mMyProject = mMyProject;
    }

    public static String getPath() {
        return mPath;
    }

    public static void setmPath(String mPath) {
        Const.mPath = mPath;
    }

    public static int getmConnectType() {
        return mConnectType;
    }

    public static void setmConnectType(int mConnectType) {
        Const.mConnectType = mConnectType;
    }

    public static double getheight() {
        return mheight;
    }

    public static void setheight(double mheight) {
        Const.mheight = mheight;
    }



}
