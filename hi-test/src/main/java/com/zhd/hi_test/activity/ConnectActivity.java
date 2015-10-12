package com.zhd.hi_test.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.util.Infomation;
import com.zhd.hi_test.util.TrimbleOrder;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


/**
 * Created by 2015032501 on 2015/9/16.
 * 这里连接后，就开始发送数据。
 * 主要有两种连接方式：1.内置GPS,2.中海达蓝牙连接
 * 以上两种方式都是通过Handler来进行传输：其中蓝牙，是传给Information类来进行解析；内置GPS则是传输给GPSActivity和SurveyActivity来解析
 * 主要是保证：1.一种连接方式连接2.断开连接后可以重连
 * 如果单论这个页面的话取消监听是没有问题的，但是涉及到其他页面
 */
public class ConnectActivity extends Activity {
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
    //判断GPS是否开启
    private static final int GPS_REQUEST = 4;
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
    private int mConnectWay;
    //获得全局变量的Data
    private Data d;
    //使用内置GPS进行连接
    private LocationManager mManager;
    private List<String> mProviders;
    private String mProvider;
    private int minTime = 1000;
    private int minDistance = 0;
    private static Handler mHandler;
    //判断是否连接上，用来断开连接。清空发送的数据用
    public static void setmHandler(Handler mHandler) {
        ConnectActivity.mHandler = mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        sp_device = (Spinner) findViewById(R.id.sp_device);
        sp_way = (Spinner) findViewById(R.id.sp_way);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_request = (Button) findViewById(R.id.btn_request);
        tv_content = (TextView) findViewById(R.id.tv_device_info);
        //因为重新打开后，mManager都为空值，所以要取消的话，必须得到同一个mManger,同一个listener。
        //获取连接状态和连接内容。然后对其内容进行赋值，如何保证单个连接
        getSingleInfo();
        btn_connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean IsConnected = d.isConnected();
                if (!IsConnected) {
                    startConnect();
                } else {
                    btn_connect.setText("连接");
                    disconnect();
                    d.setIsConnected(false);
                }

            }
        });

        btn_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMessage();
            }
        });

        btn_request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        //获取设备的内容
        String[] device_items = getResources().getStringArray(R.array.devices);
        //创建对应的内容适配器
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, device_items);
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
                        wayAdapter = new ArrayAdapter<>(ConnectActivity.this, android.R.layout.simple_list_item_1, way_items);
                        wayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_way.setAdapter(wayAdapter);
                        break;
                    case 1://安卓设备
                        way_items = getResources().getStringArray(R.array.phone_way);
                        wayAdapter = new ArrayAdapter<>(ConnectActivity.this, android.R.layout.simple_list_item_1, way_items);
                        wayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_way.setAdapter(wayAdapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_way.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String way = parent.getItemAtPosition(position).toString();
                if (way.equals("蓝牙"))
                    mConnectWay = Constant.BlueToothConncet;
                else if (way.equals("内置GPS"))
                    mConnectWay = Constant.InnerGPSConnect;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setDefaultInfo();
    }

    /**
     * 获得监听器
     */
    private void getSingleInfo() {
        d = (Data) getApplication();
        LocationManager manager = d.getmManager();
        LocationListener locListener = d.getmLocListener();
        GpsStatus.Listener listener = d.getmListener();
        if (manager == null) {
            mManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            d.setmManager(mManager);
        } else {
            mManager = manager;
        }
        if (locListener == null)
            d.setmLocListener(mLocListener);
        else
            mLocListener = locListener;
        if (listener == null)
            d.setmListener(mListener);
        else
            mListener = listener;
    }

    private void setDefaultInfo() {
        int ConnectType = d.getmConnectType();
        boolean IsConnected = d.isConnected();
        if (IsConnected) {
            btn_connect.setText("断开");
        } else {
            btn_connect.setText("连接");
        }
        switch (ConnectType) {
            //蓝牙
            case 1:
                sp_device.setSelection(0);//中海达
                sp_way.setSelection(0);//蓝牙
                break;
            //内置GPS
            case 2:
                sp_device.setSelection(1);//安卓设备
                sp_way.setSelection(0);//内置GPS
                break;
        }
    }

    /**
     * 取消时，定位操作仍然在继续
     */
    private void disconnect() {
        //取消内置GPS定位
        if (mManager != null) {
            mManager.removeUpdates(mLocListener);
            mManager.removeGpsStatusListener(mListener);
            d.setmConnectType(0);
        }

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } //取消蓝牙的数据接收
        if (mAdapter != null) {
            mAdapter.disable();
        }
    }

    /**
     * 1.首先判断连接方式
     * 2.打开蓝牙连接
     * 3.打开连接后跳转
     * 4.设置全局变量的打开类型
     */
    private void startConnect() {

        if (mConnectWay == Constant.BlueToothConncet) {
            if (mAdapter.isEnabled()) {//判断蓝牙是否开启
                StartDeviceList();
            } else {//打开蓝牙
                OpenBluetooth();
            }
        } else if (mConnectWay == Constant.InnerGPSConnect) {
            GPSinit();
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
                    //这里的device只有一个地址
                    //开始连接前关闭蓝牙搜索
                    connect(mDevice);
                }
                break;
            case DISCOVERED:
                StartDeviceList();
                break;
            case GPS_REQUEST:
                //判断是否打开，没有打开则提示打开，
                // 判断的话只能用当前是否包含GPS服务,只有在打开为true的情况下才行
                if (resultCode == RESULT_OK) {
                    mProviders = mManager.getProviders(true);
                    if (mProviders.contains(LocationManager.GPS_PROVIDER)) {
                        mManager.addGpsStatusListener(mListener);
                        mManager.requestLocationUpdates(mProvider, minTime, minDistance, mLocListener);
                    } else {
                        Toast.makeText(this, "请打开GPS服务", Toast.LENGTH_SHORT).show();
                    }
                    btn_connect.setText("断开");
                    d.setIsConnected(true);
                    d.setmConnectType(Constant.InnerGPSConnect);
                }
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
    private void connect(BluetoothDevice mDevice) {

        try {
            //这里打开socket连接，并将其
            mSocket = mDevice.createRfcommSocketToServiceRecord(mUUid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //这里进行配对,主要是这一步
            if (mSocket != null) {
                mSocket.connect();
            }
            //这里执行的太快
            //在这里进行适配，如果连接成功，正确执行
            Toast.makeText(ConnectActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            //连接成功设置
            sendMessage();
            btn_connect.setText("断开");
            d.setIsConnected(true);
            d.setmConnectType(Constant.BlueToothConncet);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ConnectActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                //用来查看读取了多少的数据
                int num = 0;
                //缓冲区,用来读取数据
                byte[] buffer = new byte[1024 * 4];
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
     * 获得不完整的数据，如果没有找到$符号的话，则返回整个读到的byte[] buffer，其长度为num
     * 注意$符号位置的情况：
     * 1.$不存在，2.$位于第一位，整条数据都是不完整的，返回<读到的>整条数据
     * 3.$正常位置，返回$位置之后<读到的>数据4.$位于最后一位，不会返回数据
     *
     * @param buffer
     * @param loc
     * @param num
     * @return
     */
    private byte[] getuncomplete(byte[] buffer, int loc, int num) {
        byte[] temp;
        if (loc == -2 || loc == -1) {//不存在$的情况和$位置是第一位的情况
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
//        String msg=new String(temp);
        return temp;
    }

    /**
     * 通过$的位置来创建完整的byte[] complete
     * 需要注意不包含$符号的情况，需要分情况讨论
     * 1.不包含$符号和2.$符号为第一位==当条数据都不完整
     * 3.最后一位和4.通常位置==获得$之前的数据
     *
     * @param buffer
     * @param loc
     * @return
     */
    private byte[] getcomplete(byte[] buffer, int loc) {
        if (loc == -2 || loc == -1) {
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
     * 最后一个$位置的4种状态：
     * 1.通常状态，介于(0,length-1)之间；
     * 2.不存在，返回值为-2
     * 3.位于首位，返回值为-1
     * 4.位于末尾，返回值为length-1
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

    /**
     * 在打开GPS的情况下可以直接的进行位置和GPS的监听
     */
    private void GPSinit() {
        mProviders = mManager.getProviders(true);
        if (mProviders.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS服务已经打开", Toast.LENGTH_SHORT).show();
            mProvider = LocationManager.GPS_PROVIDER;
            mManager.requestLocationUpdates(mProvider, minTime, minDistance, mLocListener);
            mManager.addGpsStatusListener(mListener);
            //这里才能算上GPS连上了
            d.setmConnectType(Constant.InnerGPSConnect);
            btn_connect.setText("断开");
            d.setIsConnected(true);
        } else {
            Toast.makeText(getApplicationContext(), "请打开GPS服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, GPS_REQUEST);
        }
    }

    /**
     * 位置和卫星状态的监听字段也必须保证是同一个对象
     */
    private GpsStatus.Listener mListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            //获取卫星对象
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(Constant.GPS_TAG, "卫星第一次锁定");
                    break;
                //卫星状态改变,当定位信息启动一次，那么它就会调用一次
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d(Constant.GPS_TAG, "卫星的状态");
                    //获取当前接收到的卫星情况
                    GpsStatus status = mManager.getGpsStatus(null);
                    //迭代接口，只有实现了这个接口才能实现其迭代器对象,可以对数据进行修改
                    Iterable<GpsSatellite> satellites = status.getSatellites();
                    //获得迭代对象，然后进行遍历
                    Iterator<GpsSatellite> it = satellites.iterator();
                    //获得最大的卫星数量，对接收的数量进行限制
                    int maxSatellite = status.getMaxSatellites();
                    int SatelliteNum = 0;
                    //这里创建需要进行传递的对象
                    ArrayList<GpsSatellite> satelliteList = new ArrayList<GpsSatellite>();
                    while (it.hasNext() && SatelliteNum <= maxSatellite) {//判断条件1.有卫星数据2.小于最大卫星接收数
                        GpsSatellite s = it.next();
                        satelliteList.add(s);
                        SatelliteNum++;
                    }
                    //只有在有handler的情况下才进行数据传输
                    if (mHandler != null) {
                        Object satellitesInfo = satelliteList.clone();
                        Message message = Message.obtain();
                        message.what = 2;
                        message.obj = satellitesInfo;
                        mHandler.sendMessage(message);
                    }
                    break;
                //GPS定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(Constant.GPS_TAG, "定位启动");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(Constant.GPS_TAG, "定位结束");
                    break;
            }
        }
    };

    private LocationListener mLocListener = new LocationListener() {
        //这里可以先获得最后的位置信息，再获得当前的位置信息。定位了就不会调用
        @Override
        public void onLocationChanged(Location location) {
            String longitude = String.valueOf(location.getLongitude());
            String altitude = String.valueOf(location.getAltitude());
            String latitude = String.valueOf(location.getLatitude());
            String time = String.valueOf(location.getTime());
            //在有handler的情况下才进行数据传输
            if (mHandler != null) {
                MyLocation loc = new MyLocation(latitude, longitude, altitude, time);
                Message message = Message.obtain();
                message.what = 1;
                message.obj = loc;
                mHandler.sendMessage(message);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}
