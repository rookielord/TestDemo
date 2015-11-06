package com.zhd.hi_test.module;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.Connectable;
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
 * 调用这个类的情况就是返回了蓝牙地址的情况
 */
public class BluetoothConnect2 implements Connectable {

    private static UUID mUUid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream out;
    private InputStream in;
    private BluetoothAdapter mAdapter;
    private String mAddress;
    private Activity mActivity;


    /**
     * 为了在Connect上面显示toast需要传入Activity
     * 为了创建蓝牙连接对象，需要使用BluetoothAdapter对象，但是为了方便，会把在主Activity中的mAdapter传过来进行判断
     *
     * @param address
     * @param adapter
     * @param activity
     */
    public BluetoothConnect2(String address, BluetoothAdapter adapter, Activity activity) {
        this.mAddress = address;
        this.mAdapter = adapter;
        this.mActivity = activity;
//        mDevice = mAdapter.getRemoteDevice(mAddress);
//        try {
//            mSocket = mDevice.createRfcommSocketToServiceRecord(mUUid);
//            mSocket.connect();
//            in = mSocket.getInputStream();
//            out = mSocket.getOutputStream();
//            Const.Info.SetInfo(Const.BlueToothConncet, true, mDevice.getName());
//            ((Button) mActivity.findViewById(R.id.btn_connect)).setText(R.string.disconnect);
//            ((TextView) mActivity.findViewById(R.id.tv_device_info)).setText(Const.Info.getInfo());
//            //获取连接对象的名称
//            Toast.makeText(mActivity, R.string.connect_success, Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            Const.Info.SetInfo(Const.NoneConnect, false, mActivity.getString(R.string.unconnect));
//            //清空之前得到的mDevice
//            mDevice = null;
//            e.printStackTrace();
//            Toast.makeText(mActivity, R.string.connect_failure, Toast.LENGTH_SHORT).show();
//        }
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
            Const.Info.SetInfo(Const.BlueToothConncet, true, mDevice.getName());
            ((Button) mActivity.findViewById(R.id.btn_connect)).setText(R.string.disconnect);
            ((TextView) mActivity.findViewById(R.id.tv_device_info)).setText(Const.Info.getInfo());
            //获取连接对象的名称
            Toast.makeText(mActivity, R.string.connect_success, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Const.Info.SetInfo(Const.NoneConnect, false, mActivity.getString(R.string.unconnect));
            mDevice = null;
            Toast.makeText(mActivity, R.string.connect_failure, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送命令
     * @param order
     * @throws IOException
     */
    @Override
    public void sendMessage(byte[] order) throws IOException {
        if (!Const.Info.isConnected())
            return;
        out.write(order);
    }

    /**
     * 写入命令
     * @throws IOException
     */
    @Override
    public void flushMessage() throws IOException {
        if (!Const.Info.isConnected())
            return;
        out.flush();
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
        return in.read(buffer);
    }

    /**
     * 断开连接
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        if (!Const.Info.isConnected())
            return;
        in.close();
        out.close();
        mSocket.close();
        mDevice = null;
        Const.Info.SetInfo(Const.NoneConnect, false, mActivity.getString(R.string.unconnect));
        Const.HasDataInfo = false;
        Const.HasPDOP = false;
        Const.satellites.clear();
    }


}
