package com.zhd.hi_test.module;

import android.app.PendingIntent;

import com.zhd.hi_test.Const;

/**
 * Created by 2015032501 on 2015/11/5.
 * <p>
 * 包含当前连接状态的信息
 * 1.连接状态 是否连接
 * 2.连接方式 蓝牙还是GPS
 * 3.连接名称 蓝牙对应仪器号；GPS对应内置GPS
 * 4.将其保持为单例状态
 */
public class ConnectInfo {

    private ConnectInfo() {

    }

    private static ConnectInfo mConnect;

    public static ConnectInfo getInstance() {
        if (mConnect == null) {
            mConnect = new ConnectInfo();
        }
        return mConnect;
    }

    /**
     * 连接的类型
     */
    private int mConnectType = Const.NoneConnect;
    /**
     * 连接的状态
     */
    private boolean mIsConnected = false;
    /**
     * 连接的信息
     */
    private String mInfo = "设备尚未连接";


    public int getType() {
        return mConnectType;
    }

    public String getInfo() {
        return mInfo;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public void SetInfo(int ConnectType, boolean isConnected, String Info) {
        this.mConnectType = ConnectType;
        mIsConnected = isConnected;
        this.mInfo = Info;
    }
}
