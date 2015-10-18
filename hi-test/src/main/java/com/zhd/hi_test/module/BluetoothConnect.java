package com.zhd.hi_test.module;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zhd.hi_test.Constant;
import com.zhd.hi_test.Data;
import com.zhd.hi_test.activity.BluetoothDeviceActivity;
import com.zhd.hi_test.interfaces.IConnect;
import com.zhd.hi_test.util.Infomation;
import com.zhd.hi_test.util.ProgressInfo;
import com.zhd.hi_test.util.TrimbleOrder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by 2015032501 on 2015/10/17.
 * 除了连接其它的发送、关闭、读取操作都必须建立在打开了连接之后
 */
public class BluetoothConnect implements IConnect {

    private static UUID mUUid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream out;
    private InputStream in;
    private BluetoothAdapter mAdapter;
    private String mAddress;

    //设置一个量用于维护读取操作
    private boolean isRead = false;

    /**
     * 蓝牙连接只需要传入一个地址则可以了
     *
     * @param address
     */
    public BluetoothConnect(String address, BluetoothAdapter adapter) {
        this.mAddress = address;
        this.mAdapter = adapter;
        isRead = true;
    }

    /**
     * 通过传入的蓝牙地址来进行蓝牙匹配
     */
    @Override
    public void startConnect() {
        mDevice = mAdapter.getRemoteDevice(mAddress);
        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(mUUid);
            mSocket.connect();
            in = mSocket.getInputStream();
            out = mSocket.getOutputStream();
            Data.setIsConnected(true);
            Data.setmConnectType(Constant.BlueToothConncet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送读取的数据
     */
    @Override
    public void sendMessage() {
        if (!Data.isConnected())
            return;
        try {
            out.write(TrimbleOrder.CLOSE_COM1);
            Thread.sleep(200);
            out.write(TrimbleOrder.GPGSV);
            Thread.sleep(200);
            out.write(TrimbleOrder.GGA);
            Thread.sleep(200);
            out.write(TrimbleOrder.GPZDA);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void readMessage() {
        if (!Data.isConnected())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int num;
                byte[] buffer = new byte[1024 * 4];
                byte[] completeInfo;
                byte[] incompleteInfo = null;
                byte[] useInfo;
                try {
                    while (isRead) {
                        while ((num = in.read(buffer)) != -1) {
                            //获取$最后的位置
                            int loc = ProgressInfo.getLastLocation(buffer, num);
                            //获取最后$之前的数据的所有数据
                            completeInfo = ProgressInfo.getComplete(buffer, loc);
                            //拼接之前的不完整的数据，得到的完整的数据
                            useInfo = ProgressInfo.MergeInfo(completeInfo, incompleteInfo);
                            //获取不完整的数据
                            incompleteInfo = ProgressInfo.getUncomplete(buffer, loc, num);
                            //首先要发指令，让其发送位置和卫星信息
                            if (useInfo != null) {
                                String msg1 = new String(useInfo);
                                //注意，显示数据是不完善的，经过调试后发现是完整拼接
//                                Log.d(Constant.TAG, msg1);
                                Infomation.setmInputMsg(msg1);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 终止读取，关闭流
     */
    @Override
    public void breakConnect() {
        if (!Data.isConnected())
            return;
        isRead=false;
        if (in!=null)
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (out!=null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mSocket != null) {
            try {
                if (mSocket.isConnected()){
                    mSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
