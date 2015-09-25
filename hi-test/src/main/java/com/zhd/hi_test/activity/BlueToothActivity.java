package com.zhd.hi_test.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Constant;
import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.util.ConnectType;
import com.zhd.hi_test.util.Infomation;
import com.zhd.hi_test.util.TrimbleOrder;


import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * Created by 2015032501 on 2015/9/16.
 * 这里进行设备的连接和数据的读取，因为是测试用，所以就使用固定的命令
 */
public class BlueToothActivity extends Activity {
    //控件
    Button btn_connect, btn_clear, btn_request;
    TextView tv_content;
    Spinner sp_device, sp_way;
    //设置蓝牙的适配器
    private BluetoothAdapter mAdapter;
    //设置返回是否允许启动蓝牙
    private static final int REQUEST_CODE = 1;
    //启动返回得到地址
    private static final int DEVICE_MESSAGE = 2;
    //判断是否可以被其它设备搜索
    private static final int DISCOVERED = 3;
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
    //判断是否开启连接
    private static boolean mIsConnect = false;
    //通过绑定handler确定信息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                Toast.makeText(BlueToothActivity.this, "连接成功", Toast.LENGTH_SHORT).show();

            } else if (msg.arg1 == -1) {
                Toast.makeText(BlueToothActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    };
    private ProgressDialog dialog;

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
        //根据所选择仪器类型，对连接方式进行设置,注意是onItemSelected
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
                    startActivityForResult(intent, DISCOVERED);
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
            case DISCOVERED:
                StartDeviceList();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 这里获取到输入输出流，然后输入流开启线程不断地读
     * 需要在这里开始解析数据并处理丢包问题
     * 这里也需要开启一个线程用来确认用户蓝牙连接成功和失败
     *
     * @param mDevice
     */
    private void connect(final BluetoothDevice mDevice) {
        //显示进度条，开始连接蓝牙
        dialog = new ProgressDialog(this);
        dialog.setTitle("蓝牙连接中……");
        dialog.show();
        //在这里进行连接，连接发送消息
        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(mUUid);
            mSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                //用来查看读取了多少的数据
                int num = 0;
                //缓冲区,用来读取数据
                byte[] buffer = new byte[1024 * 4];
                //  1、用来接收每次读取到的数据
                //  2、把每次读到的数据装入buffer缓冲区
                //  3、去装好的缓冲区Buffer搜索解析
                //$符号之前的数据，用来和上一次的不完整数据进行拼接
                byte[] completeInfo;
                //$符号之后不完整的数据，用来和下一次的数据进行拼接
                byte[] uncompleteInfo = null;
                //拼接后得到的完整数据
                byte[] useInfo;
                try {
                    in = mSocket.getInputStream();
                    while (true) {
                        while ((num = in.read(buffer)) != -1) {
                            //获取$最后的位置
                            int loc = getLastLocation(buffer, num);
                            //获取最后$之前的数据的所有数据
                            completeInfo = getcomplete(buffer, loc);
                            //拼接之前的不完整的数据，得到的完整的数据
                            useInfo = MergeInfo(completeInfo, uncompleteInfo);
                            //获取不完整的数据
                            uncompleteInfo = getuncomplete(buffer, loc, num);
                            //首先要发指令，让其发送位置和卫星信息
                            if (useInfo != null) {
                                String msg1 = new String(useInfo);
                                Log.d(Constant.TAG, msg1);
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
     * 获得不完整的数据，如果没有找到$符号的话，则返回整个读到的byte[] buffer，其长度为num
     *
     * @param buffer
     * @param loc
     * @param num
     * @return
     */
    private byte[] getuncomplete(byte[] buffer, int loc, int num) {
        byte[] temp;
        if (loc == -2) {
            temp = new byte[num];
            for (int i = 0; i < num; i++) {
                temp[i] = buffer[i];
            }
        } else {
            int length = num - loc;//正确
            temp = new byte[length];//创建不完整数据的长度
            for (int i = 0; i < length; i++) {//赋值
                temp[i] = buffer[i + loc];
            }
        }
        return temp;
    }

    /**
     * 通过$的位置来创建完整的byte[] complete
     * 需要注意不包含$符号的情况，需要分情况讨论
     * 如果不包含$符号，则整条数据都是不完整的，当前返回为null
     *
     * @param buffer
     * @param loc
     * @return
     */
    private byte[] getcomplete(byte[] buffer, int loc) {
        if (loc == -2) {
            return null;
        }
        byte[] temp = new byte[loc];
        for (int i = 0; i < loc; i++) {
            temp[i] = buffer[i];
        }
        return temp;
    }

    /**
     * 这里进行 本次查询到完整的数据，以及上次查词到的不完整的数据的拼接
     * 注意：
     * 1.第一次进行合并时，uncompleteInfo为null,需要返回完整数据
     * 2.有时会有没有包含$符的情况，则整个buffer都是不完整的数据，让其和下一次进行拼接，其返回null
     * 3.首先判断是否有completeinfo[]然后再判断uncompleteinfo
     *
     * @param completeInfo
     * @param uncompleteInfo
     * @return
     */
    private byte[] MergeInfo(byte[] completeInfo, byte[] uncompleteInfo) {
        if (completeInfo == null)
            return null;
        if (uncompleteInfo == null)
            return completeInfo;
        byte[] useinfo = new byte[completeInfo.length + uncompleteInfo.length];
        System.arraycopy(uncompleteInfo, 0, useinfo, 0, uncompleteInfo.length);
        System.arraycopy(completeInfo, 0, useinfo, uncompleteInfo.length, completeInfo.length);
        return useinfo;
    }

    /**
     * 需要考虑当前数据没有$符号的状态，如果没有则会返回null
     *
     * @param buffer
     * @param num
     * @return
     */
    private int getLastLocation(byte[] buffer, int num) {
        byte Fdelimiter = 0x24;//$符号的byte
        int location = -1;//默认没有找到$符的位置
        for (int i = 0; i < num; i++) {
            if (buffer[i] == Fdelimiter && i > location) {//判断条件1.当前位置大于位置2.确定是$符
                location = i;
            }
        }
        //因为获取到的是$之前的数据，而找到的是$符号的位置，所以要-1
        return location - 1;
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
     * 是否第一条必须获得流后就必须发送？
     * 是否只能一次性发送两次数据？
     */
    private void sendMessage() {
        try {
            out = mSocket.getOutputStream();
            out.write(TrimbleOrder.GPGSV);
            Thread.sleep(200);
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
