package com.zhd.hi_test.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.R;
import com.zhd.hi_test.ui.CustomDialog;

import java.util.Set;

/**
 * Created by 2015032501 on 2015/9/16.
 * 这里显示所有的匹配和未匹配的蓝牙设备，如果点击该设备则会把所有的MAC码返回到上一个Activity
 * 这里的蓝牙已经在上一个页面被打开了，所以不再考虑蓝牙没有打开的情况
 * 用一个scrolview来包含两个listview
 */
public class DeviceListActivity extends Activity {
    //device上面的控件
    Button btn_search;
    ListView lv_pairedlist,lv_newlist;
    //存放数据的两个Adapter
    ArrayAdapter<String> mPairedAdapter, mNewsAdapter;
    //蓝牙适配器
    BluetoothAdapter mAdapter;
    //意图筛选器，设置监听的Action
    IntentFilter filter = new IntentFilter();
    //在上个Activity中获取设备地址的名称
    public static final String ADRESS = "ADRESS";
    private static final String TAG = "DEVICE";
    //设置蓝牙开启返回的对应code
    private static final int REQUES_CODE = 0x1;

    //要获取适配器中的内容已经匹配的内容，必须一开始就打开蓝牙
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        //找到控件
        btn_search = (Button) findViewById(R.id.btn_search);
        lv_pairedlist = (ListView) findViewById(R.id.lv_pairedlist);
        lv_newlist = (ListView) findViewById(R.id.lv_newlist);
        //获得蓝牙适配器
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //默认设置结果为cancel，防止意外返回没有数据
        setResult(RESULT_CANCELED);
        //实例化两个Adapter
        mPairedAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewsAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        //获得已配对的蓝牙进行显示
        getPairedAdaper();
        //注册广播接受者，监听设备添加
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        //设置对每个item的监听并填充内容
        lv_newlist.setAdapter(mNewsAdapter);
        lv_pairedlist.setAdapter(mPairedAdapter);
        lv_newlist.setOnItemClickListener(mItemlistener);
        lv_pairedlist.setOnItemClickListener(mItemlistener);
        //这里需要设置蓝牙可用被设置可以被搜索

        //开始搜索蓝牙设备
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
                //搜索完毕后按钮消失
                v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUES_CODE){
            if (resultCode==RESULT_OK){
                Toast.makeText(this,"打开成功",Toast.LENGTH_SHORT).show();
                btn_search.setEnabled(true);
            }else {
                Toast.makeText(this,"请打开蓝牙",Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getPairedAdaper() {
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        //这里需要保证devices大于0
        if (devices.size() > 0) {
            findViewById(R.id.tv_pairedlist_title).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : devices) {
                mPairedAdapter.add(device.getName() + ";" + device.getAddress());
            }
            //在接下来添加没有适配过的仪器
            mPairedAdapter.add("位配对过的仪器");
        } else {
            mPairedAdapter.add("当前没有配对的数据");
        }
    }

    /**
     * 进行蓝牙设备搜索，搜索前需要进行判断是否正在搜索
     */
    private void startDiscovery() {
        //显示搜索条
        setProgressBarIndeterminateVisibility(true);
        setTitle("正在搜索中……");
        //将新蓝牙设备的listview找到并设为可见
        findViewById(R.id.tv_newlist_title).setVisibility(View.VISIBLE);
        if (mAdapter.isDiscovering())
            mAdapter.cancelDiscovery();
        //开始搜索
        mAdapter.startDiscovery();
    }

    /**
     * 关闭时，停止搜索并且注销广播
     */
    @Override
    protected void onDestroy() {
        if (mAdapter != null)
            mAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    //listview的item监听器
    /**
     * 点击Item后关闭当前Activity,然后返回蓝牙的地址给前一个Activity
     */
    OnItemClickListener mItemlistener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //点击时停止查找
            mAdapter.cancelDiscovery();
            String info = ((TextView) view).getText().toString();
            String adress = info.substring(info.length() - 17);//固定的长度
            Log.d(TAG, adress);
            Intent intent = new Intent();//创建一个空intent来存放数据
            intent.putExtra(ADRESS, adress);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    /**
     * 长点击后可以删除该适配
     */
    OnItemLongClickListener mitemLongClickListener=new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            CustomDialog customDialog=new CustomDialog();
            customDialog.show(getFragmentManager(),"BluetoothDialog");
            return false;
        }
    };

    //广播接收者接收广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//监听两个Action
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (mNewsAdapter.getCount() == 0) {
                        mNewsAdapter.add("当前没有设备");
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    //找到一个新的蓝牙设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//如果已经绑定就不用添加进列表了
                        mNewsAdapter.add(device.getName() + ";" + device.getAddress());
                    }
                    break;
            }
        }
    };
}
