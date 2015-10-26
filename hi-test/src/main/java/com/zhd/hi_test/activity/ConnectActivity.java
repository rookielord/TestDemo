package com.zhd.hi_test.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.IConnect;
import com.zhd.hi_test.module.BluetoothConnect;
import com.zhd.hi_test.module.InnerGPSConnect;


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
    Button btn_connect;
    TextView tv_content;
    Spinner sp_device, sp_way;
    ImageView image_title;
    //设置蓝牙的适配器
    private BluetoothAdapter mAdapter;
    //设置打开关闭的判断
    private String[] way_items;
    private ArrayAdapter<String> wayAdapter;
    //获取连接的方式
    private int mConnectWay;
    //当前的连接对象
    private static IConnect mConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        init();
        btn_connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!
                        Const.IsConnected) {
                    startConnect();
                } else {
                    mConnect.breakConnect();
                    btn_connect.setText("连接");
                    Const.IsConnected=false;
                    Const.setmConnectType(0);
                    Const.setmInfo("设备未连接");
                    tv_content.setText("设备未连接");
                    sp_way.setEnabled(true);
                    sp_device.setEnabled(true);
                }
            }
        });
    }

    private void init() {
        sp_device = (Spinner) findViewById(R.id.sp_device);
        sp_way = (Spinner) findViewById(R.id.sp_way);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        tv_content = (TextView) findViewById(R.id.tv_device_info);
        image_title = (ImageView) findViewById(R.id.image_title);
        //获取设备的内容
        String[] device_items = getResources().getStringArray(R.array.devices);
        //创建对应的内容适配器
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, device_items);
        //设置下拉菜单的样式
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //进行数据填充,只对厂商Device进行填充，由它来决定接下来的方式中的内容
        sp_device.setAdapter(deviceAdapter);
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
                    mConnectWay = Const.BlueToothConncet;
                else if (way.equals("内置GPS"))
                    mConnectWay = Const.InnerGPSConnect;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //当前显示的默认信息
        getDefaultInfo();
    }

    private void getDefaultInfo() {
        int ConnectType = Const.getmConnectType();
        if (Const.IsConnected) {
            btn_connect.setText("断开");
            sp_way.setEnabled(false);
            sp_device.setEnabled(false);
        } else {
            btn_connect.setText("连接");
            sp_way.setEnabled(true);
            sp_device.setEnabled(true);
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
        tv_content.setText(Const.getmInfo());
    }
    /**
     * 1.首先判断连接方式
     * 2.打开蓝牙连接
     * 3.打开连接后跳转
     */
    private void startConnect() {
        if (mConnectWay == Const.BlueToothConncet) {
            //只有选择蓝牙，才获得蓝牙适配器进行操作
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mAdapter.isEnabled()) {//判断蓝牙是否开启
                StartDeviceList();
            } else {//打开蓝牙
                OpenBluetooth();
            }
        } else if (mConnectWay == Const.InnerGPSConnect) {
            mConnect = new InnerGPSConnect(this);
            Const.setmConnect(mConnect);
            startConnecting();
            getDefaultInfo();
        }
    }

    private void startConnecting() {
        mConnect.startConnect();
        mConnect.sendMessage();
        mConnect.readMessage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.REQUEST_CODE://打开蓝牙选项
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(intent, Const.DISCOVERED);
                }
                break;
            case Const.DEVICE_MESSAGE://建立蓝牙连接的选项
                if (resultCode == RESULT_OK) {
                    String address = data.getExtras().getString(BluetoothDeviceActivity.ADDRESS);
                    mConnect = new BluetoothConnect(address, mAdapter, this);
                    Const.setmConnect(mConnect);
                    startConnecting();
                    getDefaultInfo();
//                    mConnect.startConnect();
//                    List<byte[]> orders = new ArrayList<byte[]>() {{
//                        add(TrimbleOrder.CLOSE_COM1);
//                        add(TrimbleOrder.GGA);
//                        add(TrimbleOrder.GPGSV);
//                        add(TrimbleOrder.GPZDA);
//                    }};
//                    mConnect.sendMessage(orders);
                }
                break;
            case Const.DISCOVERED://打开蓝牙设备连接窗口的
                StartDeviceList();
                break;
            case Const.GPS_REQUEST:
                //通过选择GPS打开界面来确定是否打开GPS
                if (resultCode == RESULT_OK) {
                    mConnect = new InnerGPSConnect(this);
                    mConnect.startConnect();
                    Const.setmConnect(mConnect);
                    sp_way.setEnabled(false);
                    sp_device.setEnabled(false);
                } else {
                    Toast.makeText(this, "请打开GPS服务", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 打开蓝牙设备的选择画面
     */
    private void StartDeviceList() {
        Intent intent = new Intent(this, BluetoothDeviceActivity.class);
        startActivityForResult(intent, Const.DEVICE_MESSAGE);
    }

    /**
     * 打开系统的蓝牙
     */
    private void OpenBluetooth() {
        if (!mAdapter.isEnabled()) {
            //设置开启请求
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, Const.REQUEST_CODE);
        } else {
            Toast.makeText(this, "开启蓝牙", Toast.LENGTH_SHORT).show();
        }
    }
}
