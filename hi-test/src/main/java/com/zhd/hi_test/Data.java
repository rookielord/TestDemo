package com.zhd.hi_test;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

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
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    //1为蓝牙连接;2为内置GPS
    private int ConnectType = 0;

    public int getConnectType() {
        return this.ConnectType;
    }

    public void setConnectType(int connectType) {
        this.ConnectType = connectType;
    }

    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }

    public BluetoothSocket getmSocket() {
        return mSocket;
    }

    public void setmSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
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
