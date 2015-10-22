package com.zhd.hi_test.module;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.IConnect;
import com.zhd.hi_test.util.Infomation;
import com.zhd.hi_test.util.ProgressInfo;
import com.zhd.hi_test.util.TrimbleOrder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.UUID;

/**
 * Created by 2015032501 on 2015/10/17.
 * 除了连接其它的发送、关闭、读取操作都必须建立在打开了连接之后
 * 调用这个类的情况就是返回了蓝牙地址的情况
 */
public class BluetoothConnect implements IConnect {

    private static UUID mUUid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream out;
    private InputStream in;
    private BluetoothAdapter mAdapter;
    private String mAddress;
    private Activity mActivity;
    private static final String TAG = "BlueTooth_TEST";

    //设置一个量用于维护读取操作
    private boolean isRead = false;
    //消息类
    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(mActivity,"仪器断开连接",Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    /**
     * 为了在Connect上面显示toast需要传入Activity
     * 为了创建蓝牙连接对象，需要使用BluetoothAdapter对象，但是为了方便，会把在主Activity中的mAdapter传过来进行判断
     *
     * @param address
     * @param adapter
     * @param activity
     */
    public BluetoothConnect(String address, BluetoothAdapter adapter, Activity activity) {
        this.mAddress = address;
        this.mAdapter = adapter;
        this.mActivity = activity;
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
            Const.setmInfo(mAddress);
            Const.setIsConnected(true);
            Const.setmConnectType(Const.BlueToothConncet);
            ((Button) mActivity.findViewById(R.id.btn_connect)).setText("断开");
            ((TextView) mActivity.findViewById(R.id.tv_device_info)).setText(mDevice.getName());
            //获取连接对象的名称
            Const.setmInfo(mDevice.getName());
            Toast.makeText(mActivity, "连接成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Const.setmInfo("设备未连接");
            e.printStackTrace();
            Toast.makeText(mActivity, "连接失败", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void sendMessage(List<byte[]> orders) {
//        if (!Const.isConnected())
//            return;
//        int i = 0;
//        for (byte[] order : orders) {
//            try {
//                out.write(order);
//                Thread.sleep(200);
//                i++;
//                if (i == orders.size() - 1)
//                    out.flush();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 发送读取的数据
     */
    @Override
    public void sendMessage() {
        if (!Const.isConnected())
            return;
        try {
            out.write(TrimbleOrder.CLOSE_COM1);
            Thread.sleep(300);
            out.write(TrimbleOrder.GPGSA);
            Thread.sleep(300);
            out.write(TrimbleOrder.GPGSV);
            Thread.sleep(300);
            out.write(TrimbleOrder.GPGGA);
            Thread.sleep(300);
            out.write(TrimbleOrder.GPZDA);
            Thread.sleep(300);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取其中的数据
     */
    @Override
    public void readMessage() {
        if (!Const.isConnected())
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
//                                Log.d(TAG, msg1);
                                Infomation.setmInputMsg(msg1);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Const.setIsConnected(false);
                    Const.setmConnectType(0);
                    mHandler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    /**
     * 终止读取，关闭流
     */
    @Override
    public void breakConnect() {
        if (!Const.isConnected())
            return;
        isRead = false;
        if (in != null)
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mSocket != null) {
            try {
                if (mSocket.isConnected()) {
                    mSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
