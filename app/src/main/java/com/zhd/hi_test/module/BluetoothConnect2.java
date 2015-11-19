package com.zhd.hi_test.module;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.Joinable;
import com.zhd.hi_test.interfaces.OnBreakListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by 2015032501 on 2015/10/17.
 * 除了连接其它的发送、关闭、读取操作都必须建立在打开了连接之后
 * 调用这个类的情况就是返回了蓝牙地址的情况
 */
public class BluetoothConnect2 implements Joinable {

    private static UUID mUUid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream mOut;
    private InputStream mIn;
    private BluetoothAdapter mAdapter;
    private String mAddress;
    private Activity mActivity;

    /**
     * 为了在Connect上面显示toast需要传入Activity
     * 为了创建蓝牙连接对象，需要使用BluetoothAdapter对象，但是为了方便，会把在主Activity中的mAdapter传过来进行判断
     *
     */
    public BluetoothConnect2(String address, BluetoothAdapter adapter, Activity activity) {
        this.mAddress = address;
        this.mAdapter = adapter;
        this.mActivity=activity;
        //这里的handler是新建的，所以不能获得
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
            mIn = mSocket.getInputStream();
            mOut = mSocket.getOutputStream();
            Const.Info.SetInfo(Const.BlueToothConncet, true, mDevice.getName());
        } catch (IOException e) {
            e.printStackTrace();
            Const.Info.SetInfo(Const.NoneConnect, false, mActivity.getString(R.string.unconnected));
            mDevice = null;
//            这里调用Activity中的一个方法发送消息，然后取消连接，提示连接失败
//            mHandler.sendEmptyMessage(Const.TYPE_UPDATE);
            Intent intent=new Intent("");
           mActivity.sendBroadcast(intent);
        }
    }

    /**
     * 发送命令
     * @throws IOException
     */
    @Override
    public void sendMessage(byte[] order) throws IOException {
        if (!Const.Info.isConnected())
            return;
        mOut.write(order);
    }

    /**
     * 写入命令
     * @throws IOException
     */
    @Override
    public void flushMessage() throws IOException {
        if (!Const.Info.isConnected())
            return;
        mOut.flush();
    }

    /**
     * @param buffer buffer为所读取到的内容
     * @return 读取到数据的长度
     * @throws IOException
     */
    @Override
    public int readMessage(byte[] buffer) throws IOException {
        if (!Const.Info.isConnected())
            return -1;
        return mIn.read(buffer);
    }

    /**
     * 断开连接
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        if (!Const.Info.isConnected())
            return;
        mIn.close();
        mOut.close();
        mSocket.close();
        mDevice = null;
//        mHandler.sendEmptyMessage(Const.TYPE_CLEAR);
        Const.Info.SetInfo(Const.NoneConnect, false, mActivity.getString(R.string.unconnected));
        Const.HasDataInfo = false;
        Const.HasPDOP = false;
    }

}
