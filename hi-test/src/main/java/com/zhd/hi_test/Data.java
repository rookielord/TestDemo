package com.zhd.hi_test;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;

import com.zhd.hi_test.module.Project;

/**
 * Created by 2015032501 on 2015/9/18.
 * 全局变量
 * 包含项目对象Project
 * 包含ZHD_TEST路径
 */
public class Data extends Application {
    private Project mProject;
    private String mPath;
    //0为未连接;1为蓝牙连接;2为内置GPS
    private int mConnectType = 0;
    private boolean IsConnected = false;
    private LocationManager mManager;
    private LocationListener mLocListener;
    private GpsStatus.Listener mListener;
    private double mheight = 0;

    public double getMheight() {
        return mheight;
    }

    public void setMheight(double mheight) {
        this.mheight = mheight;
    }

    public LocationListener getmLocListener() {
        return mLocListener;
    }

    public void setmLocListener(LocationListener mLocListener) {
        this.mLocListener = mLocListener;
    }

    public GpsStatus.Listener getmListener() {
        return mListener;
    }

    public void setmListener(GpsStatus.Listener mListener) {
        this.mListener = mListener;
    }

    public LocationManager getmManager() {
        return mManager;
    }

    public void setmManager(LocationManager mManager) {
        this.mManager = mManager;
    }

    public boolean isConnected() {
        return IsConnected;
    }

    public void setIsConnected(boolean isConnected) {
        IsConnected = isConnected;
    }

    public int getmConnectType() {
        return mConnectType;
    }

    public void setmConnectType(int mConnectType) {
        this.mConnectType = mConnectType;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public Project getmProject() {
        return mProject;
    }

    public void setmProject(Project mProject) {
        this.mProject = mProject;
    }
}
