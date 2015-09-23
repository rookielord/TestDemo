package com.zhd.hi_test.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebHistoryItem;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.util.ConnectType;
import com.zhd.hi_test.util.Infomation;
import com.zhd.hi_test.util.TrimbleOrder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private TextView tv_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        tv_info= (TextView) findViewById(R.id.tv_info);
        Data d = (Data) getApplication();
        //根据data中获取连接方式，判断是内置GPS还是RTK连接
        // 按指定模式在字符串查找
        String line = "This order was placed for QT3000! OK?";
        String pattern = "(.*)(\\d+)(.*)";


        String msg="$GPGGA,064113.00,2259.01106495,N,11322.05956001,E,1,21,0.6,40.343,M,-6.251,M,,*4A\n" +
                "    $GPGSV,8,1,25,13,5,188,39,2,43,325,50,12,32,294,47,17,47,118,47*7F\n" +
                "    $GPGSV,8,2,25,5,48,238,49,9,29,068,44,6,46,021,47*71" +
                "$GPGGA,064bvbv13.00,2259.01106495,N,11322.05956001,E,1,21,0.6,40.343,M,-6.251,M,,*4A\n" +
                "    $GPGSV,8,1,25,13,5,188,39,2,43,325,50,12,32,294,47,17,47,118,47*7F\n" +
                "    $GPGSV,8,2,25,5,48,238,49,9,29,068,44,6,46,021,47*71";
        // 创建 Pattern 对象
        Infomation.setmInputMsg(msg);
        ArrayList<Satellite>list=Infomation.getmSatellites();

        // 现在创建 matcher 对象
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
