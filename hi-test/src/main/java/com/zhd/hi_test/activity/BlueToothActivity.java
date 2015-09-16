package com.zhd.hi_test.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;

/**
 * Created by 2015032501 on 2015/9/16.
 */
public class BlueToothActivity extends Activity implements OnClickListener {
    //按钮
    Button btn_open, btn_scan;
    //设置蓝牙的适配器
    private BluetoothAdapter mAdapter;
    //设置返回是否启动的request_code
    private static final int REQUEST_CODE = 0x1;
    //数组适配器,包括已经配对和没有配对的
    private static final String TAG = "BLUETOOTHTEST";
    //启动返回得到地址
    private static final int DEVICE_MESSAGE=1;
    //连接数据对象和连接数据device
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        mAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mAdapter.isEnabled()){
            Toast.makeText(this,"请打开蓝牙",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"蓝牙已经打开",Toast.LENGTH_SHORT).show();
        }
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_open.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:
                if (mAdapter.isEnabled()) {
                    Toast.makeText(this, "蓝牙成功开启", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
            case R.id.btn_scan:
                Intent intent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(intent,DEVICE_MESSAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case DEVICE_MESSAGE:
                if (resultCode==RESULT_OK){
                    String adress=data.getExtras().getString(DeviceListActivity.ADRESS);
                    Log.d(TAG,adress);
                    mDevice=mAdapter.getRemoteDevice(adress);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
