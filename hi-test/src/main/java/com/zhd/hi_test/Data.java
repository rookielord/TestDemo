package com.zhd.hi_test;

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
public class Data{
    private static Project mProject;
    private static String mPath;
    //0为未连接;1为蓝牙连接;2为内置GPS
    private static int mConnectType = 0;
    private static boolean IsConnected = false;
    private static double mheight = 0;
    //内置GPS的一些监听
    private static LocationManager mManager;
    private static LocationListener mLocListener;
    private static GpsStatus.Listener mListener;
    //连接的scocket对象
    private static Closeable mSocket;

    public static Closeable getmSocket() {
        return mSocket;
    }

    public static void setmSocket(Closeable mSocket) {
        Data.mSocket = mSocket;
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

    public static LocationManager getmManager() {
        return mManager;
    }

    public static void setmManager(LocationManager mManager) {
        Data.mManager = mManager;
    }

    public static LocationListener getmLocListener() {
        return mLocListener;
    }

    public static void setmLocListener(LocationListener mLocListener) {
        Data.mLocListener = mLocListener;
    }

    public static GpsStatus.Listener getmListener() {
        return mListener;
    }

    public static void setmListener(GpsStatus.Listener mListener) {
        Data.mListener = mListener;
    }
}
