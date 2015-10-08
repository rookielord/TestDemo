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
    //0为未连接;1为蓝牙连接;2为内置GPS
    private int mConnectType=0;
    private boolean IsConnected=false;

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
