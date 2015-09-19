package com.zhd.hi_test.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.util.TrimbleOrder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;


/**
 * Created by 2015032501 on 2015/9/16.
 */
public class BlueToothActivity extends Activity implements OnClickListener {
    //按钮
    Button btn_open, btn_scan, btn_send,btn_clear;
    TextView tv_content;
    //设置蓝牙的适配器
    private BluetoothAdapter mAdapter;
    //设置返回是否启动的request_code
    private static final int REQUEST_CODE = 1;
    //数组适配器,包括已经配对和没有配对的
    private static final String TAG = "LIJIAJI";
    //启动返回得到地址
    private static final int DEVICE_MESSAGE = 2;
    //连接数据对象和连接数据device
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    //uuid
    private static UUID mUUid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //读取内容
    private OutputStream out;
    //设置打开关闭的判断
    private boolean mIsOpenBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_send = (Button) findViewById(R.id.btn_getinfo);
        btn_clear= (Button) findViewById(R.id.btn_clear);
        tv_content = (TextView) findViewById(R.id.tv_content);
        btn_open.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        Data d= (Data) getApplication();
        mIsOpenBluetooth = mAdapter.isEnabled();
        if (mIsOpenBluetooth)
            btn_open.setText("关闭蓝牙");
        else
            btn_open.setText("打开蓝牙");
    }

    /**
     * 要求销毁资源数据
     * 在注销mSocket之前要判断是否有蓝牙连接对象
     */
    @Override
    protected void onDestroy() {
        //注销连接和流
        if (mSocket!=null&&mSocket.isConnected()) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId
                ()) {
            case R.id.btn_open:
                //打开蓝牙
                if (!mIsOpenBluetooth) {
                    OpenBluetooth();
                    btn_open.setText("关闭蓝牙");
                    mIsOpenBluetooth = true;
                } else {
                    CloseBluetooth();
                    btn_open.setText("打开蓝牙");
                    mIsOpenBluetooth = false;
                }
                break;
            case R.id.btn_scan:
                Intent intent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(intent, DEVICE_MESSAGE);
                break;
            case R.id.btn_getinfo:
                sendMessage();
                break;
            case R.id.btn_clear:
                clearMessage();
        }
    }

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

    private void CloseBluetooth() {
        mAdapter.disable();
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DEVICE_MESSAGE:

                if (resultCode == RESULT_OK) {
                    String adress = data.getExtras().getString(DeviceListActivity.ADRESS);
                    Log.d(TAG, adress);
                    mDevice = mAdapter.getRemoteDevice(adress);
                    //根据地址创建连接对象获得流
                    connect(mDevice);
                }
                break;
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    btn_scan.setEnabled(true);
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 这里获取到输入输出流，然后输入流开启线程不断地读
     *
     * @param mDevice
     */
    private void connect(BluetoothDevice mDevice) {
        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(mUUid);
            //建立连接，就可以不断的从里面获取输入流和输出流
            try {
                mSocket.connect();
                Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
            }

            //开启线程不断的读取其中的数据，因为不知道什么时候发过来，所以一直死循环的读取
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream in = null;
                    int num = 0;
                    byte[] buffer = new byte[1024*1024];
                    try {
                        in = mSocket.getInputStream();
                        StringBuilder sb=new StringBuilder();
                        while (true) {
                            while ((num = in.read(buffer)) != -1) {
                                byte[] message=buffer;
                                String msg=new String(buffer,0,num);
                                //sb.append(msg);
                                Log.d(TAG, msg);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void OpenBluetooth() {
        if (!mAdapter.isEnabled()) {
            //设置开启请求
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Toast.makeText(this, "开启蓝牙", Toast.LENGTH_SHORT).show();
            btn_scan.setEnabled(true);
        }
    }
}
