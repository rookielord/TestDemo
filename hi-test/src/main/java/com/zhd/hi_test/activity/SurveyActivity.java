package com.zhd.hi_test.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.util.ConnectType;
import com.zhd.hi_test.util.TrimbleOrder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 2015032501 on 2015/9/22.
 * 通过根据连接方式，来获得对应的数据信息
 * 包括：1.与RTK的蓝牙连接 2.与内部GPS的连接方式：
 * 以上两种方式通过设备连接=》全局变量=》这里获取的方式来获得
 */
public class SurveyActivity extends Activity {

    //读取内容
    private OutputStream out;
    //获得对应的连接对象
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private static final String TAG = "Survey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Data d = (Data) getApplication();
        //根据data中获取连接方式，判断是内置GPS还是RTK连接
        if (d.getConnectType()==0)
        {
            Toast.makeText(getApplicationContext(),"请先连接",Toast.LENGTH_SHORT).show();
            return;
        }
        mAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    /**
     * 清空接收机发过来的数据
     */
    private void clearMessage() {
        try {
            out = mSocket.getOutputStream();
            //out.write(msg.getBytes());
            out.write(TrimbleOrder.CLOSE_COM1);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这是请求GGA_LOC数据流
     */
    private void sendMessage() {
        try {
            out = mSocket.getOutputStream();
            //out.write(msg.getBytes());
            out.write(TrimbleOrder.GGA_LOC);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当界面销毁的时候会关闭蓝牙和流
     */
    @Override
    protected void onDestroy() {
        //注销连接和流
        if (mSocket != null && mSocket.isConnected()) {
            try {
                mAdapter.disable();
                mSocket.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

}
