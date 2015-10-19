package com.zhd.hi_test;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;

import com.zhd.hi_test.module.Project;

import java.io.Closeable;
import java.net.Socket;

/**
 * Created by 2015032501 on 2015/9/18.
 * 全局变量
 * 包含项目对象Project
 * 包含ZHD_TEST路径
 */
public class Data {
    private static Project mProject;
    private static String mPath;
    //0为未连接;1为蓝牙连接;2为内置GPS
    private static int mConnectType = 0;
    private static boolean IsConnected = false;
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
    public static String mInfo = "设备尚未连接";

    public static String getmInfo() {
        return mInfo;
    }

    public static void setmInfo(String mInfo) {
        Data.mInfo = mInfo;
    }

    public static Project getmProject() {
        return mProject;
    }

    public static void setmProject(Project mProject) {
        Data.mProject = mProject;
    }

    public static String getmPath() {
        return mPath;
    }

    public static void setmPath(String mPath) {
        Data.mPath = mPath;
    }

    public static int getmConnectType() {
        return mConnectType;
    }

    public static void setmConnectType(int mConnectType) {
        Data.mConnectType = mConnectType;
    }

    public static boolean isConnected() {
        return IsConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        IsConnected = isConnected;
    }

    public static double getMheight() {
        return mheight;
    }

    public static void setMheight(double mheight) {
        Data.mheight = mheight;
    }

}
