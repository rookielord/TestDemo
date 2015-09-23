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

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.util.ConnectType;
import com.zhd.hi_test.util.TrimbleOrder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;


/**
 * Created by 2015032501 on 2015/9/16.
 * 这里进行设备的连接和数据的读取，因为是测试用，所以就使用固定的命令
 */
public class BlueToothActivity extends Activity {
    //按钮
    Button btn_connect, btn_clear, btn_request;
    TextView tv_content;
    Spinner sp_device, sp_way;
    //设置蓝牙的适配器
    private BluetoothAdapter mAdapter;
    //设置返回是否允许启动蓝牙
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
    private String[] way_items;
    private ArrayAdapter<String> wayAdapter;
    //获取连接的方式
    private String mConnectWay;
    //获得全局变量的Data
    private Data d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        //获取内容
        d = (Data) getApplication();
        sp_device = (Spinner) findViewById(R.id.sp_device);
        sp_way = (Spinner) findViewById(R.id.sp_way);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnect();
            }
        });
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMessage();
            }
        });
        btn_request = (Button) findViewById(R.id.btn_request);
        btn_request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        tv_content = (TextView) findViewById(R.id.tv_device_info);
        //获取设备的内容
        String[] device_items = getResources().getStringArray(R.array.devices);
        //创建对应的内容适配器
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, device_items);
        //设置下拉菜单的样式
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //进行数据填充,只对厂商Device进行填充，由它来决定接下来的方式中的内容
        sp_device.setAdapter(deviceAdapter);
        //获得蓝牙的适配器
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //根据所选择仪器类型，对连接方式进行设置
        sp_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://中海达
                        //创建Adapter来填充内容
                        way_items = getResources().getStringArray(R.array.zhd_way);
                        wayAdapter = new ArrayAdapter<String>(BlueToothActivity.this, android.R.layout.simple_list_item_1, way_items);
                        sp_way.setAdapter(wayAdapter);
                        break;
                    case 1://安卓设备
                        way_items = getResources().getStringArray(R.array.phone_way);
                        wayAdapter = new ArrayAdapter<String>(BlueToothActivity.this, android.R.layout.simple_list_item_1, way_items);
                        sp_way.setAdapter(wayAdapter);
                        break;
                }
                mConnectWay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_way.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mConnectWay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 1.首先判断连接方式
     * 2.打开蓝牙连接
     * 3.打开连接后跳转
     * 4.设置全局变量的打开类型
     */
    private void startConnect() {
        mConnectWay = sp_way.getSelectedItem().toString();
        if (mConnectWay.equals("蓝牙")) {
            if (mAdapter.isEnabled()) {//判断蓝牙是否开启
                StartDeviceList();
                d.setConnectType(ConnectType.BlueToothConncet);
            } else {
                OpenBluetooth();
            }
        } else if (mConnectWay.equals("内置GPS")) {
            d.setConnectType(ConnectType.InnerGPSConnect);
        }
    }

    /**
     * 要求销毁资源数据
     * 在注销mSocket之前要判断是否有蓝牙连接对象
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE://获取蓝牙打开，选择可以被搜索
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(intent);
                    StartDeviceList();
                }
                break;
            case DEVICE_MESSAGE:
                if (resultCode == RESULT_OK) {
                    String adress = data.getExtras().getString(DeviceListActivity.ADRESS);
                    mDevice = mAdapter.getRemoteDevice(adress);
                    //根据地址创建连接
                    connect(mDevice);
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
            //设为全局变量
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
                    byte[] buffer = new byte[1024 * 1024];
                    try {
                        in = mSocket.getInputStream();
                        StringBuilder sb = new StringBuilder();
                        while (true) {
                            while ((num = in.read(buffer)) != -1) {
                                byte[] message = buffer;
                                String msg1 = new String(buffer, 0, num);
                                //sb.append(msg);
                                Log.d(TAG, msg1);
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
     * 这里发送命令，获取卫星数据和位置信息
     * 即发送两条指令，包括($GPGGA和$GPGSV)
     * 关闭流就是断开连接，然后中间间隔时间才能发送两条命令
     */
    private void sendMessage() {
        try {
            out = mSocket.getOutputStream();

            out.write(TrimbleOrder.GPGSV);
            Thread.sleep(100);
            //out.flush();
            out.write(TrimbleOrder.GGA);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void StartDeviceList() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, DEVICE_MESSAGE);
    }

    private void OpenBluetooth() {
        if (!mAdapter.isEnabled()) {
            //设置开启请求
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Toast.makeText(this, "开启蓝牙", Toast.LENGTH_SHORT).show();
        }
    }
}
