package com.zhd.hi_test.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
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

import static android.view.View.*;

/**
 * Created by 2015032501 on 2015/9/16.
 * 这里显示所有的匹配和未匹配的蓝牙设备，如果点击该设备则会把所有的MAC码返回到上一个Activity
 * 这里的蓝牙已经在上一个页面被打开了，所以不再考虑蓝牙没有打开的情况
 * 用一个scrolview来包含两个listview
 */
public class BluetoothDeviceActivity extends Activity {
    //device上面的控件
    Button btn_search, btn_close;
    ListView lv_pairedlist, lv_newlist;
    //存放蓝牙名称的两个ArrayAdapter
    ArrayAdapter<String> mPairedAdapter, mNewAdapter;
    //存放两个蓝牙地址的
    List<String> mPairedDevices = new ArrayList<>();
    List<String> mNewDevices = new ArrayList<>();
    //蓝牙适配器
    BluetoothAdapter mAdapter;
    //意图筛选器，设置监听的Action
    IntentFilter filter = new IntentFilter();
    //在上个Activity中获取设备地址的名称
    public static final String ADDRESS = "ADDRESS";
    public static final String NAME = "NAME";
    //设置蓝牙开启返回的对应code
    private static final int BLUETOOTH_REQUEST = 0x1;
    //设置判断是否开启过蓝牙搜索，只有开启过蓝牙搜索后搜索到为0才能添加没有数据
    private boolean isSearch;

    //要获取适配器中的内容已经匹配的内容，必须一开始就打开蓝牙
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bluetooth_device);
        //默认设置是没有找到的
        isSearch = false;
        //找到控件
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_close = (Button) findViewById(R.id.btn_close);
        lv_pairedlist = (ListView) findViewById(R.id.lv_pairedlist);
        lv_newlist = (ListView) findViewById(R.id.lv_newlist);
        //获得蓝牙适配器
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //默认设置结果为cancel，防止意外返回没有数据
        setResult(RESULT_CANCELED);
        //实例化两个Adapter
        mPairedAdapter = new ArrayAdapter<>(this, R.layout.device_item);
        mNewAdapter = new ArrayAdapter<>(this, R.layout.device_item);
        //获得已配对的蓝牙进行显示
        getPairedAdaper();
        //注册广播接受者，监听设备添加
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        //设置对每个item的监听并填充内容
        lv_newlist.setAdapter(mNewAdapter);
        lv_pairedlist.setAdapter(mPairedAdapter);
        lv_newlist.setOnItemClickListener(mItemlistener);
        lv_pairedlist.setOnItemClickListener(mItemlistener);
        //开始搜索蓝牙设备
        btn_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
                //搜索完毕后按钮消失
                v.setVisibility(GONE);
                mNewAdapter.clear();
                btn_close.setVisibility(VISIBLE);
            }
        });
        //停止搜索
        btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isDiscovering())
                    mAdapter.cancelDiscovery();
                v.setVisibility(GONE);
                btn_search.setVisibility(VISIBLE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "打开成功", Toast.LENGTH_SHORT).show();
                btn_search.setEnabled(true);
            } else {
                Toast.makeText(this, "请打开蓝牙", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取已绑定的蓝牙适配器
     */
    private void getPairedAdaper() {
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        //这里需要保证devices大于0
        if (devices.size() > 0) {
            findViewById(R.id.tv_pairedlist_title).setVisibility(VISIBLE);
            for (BluetoothDevice device : devices) {
                mPairedAdapter.add(device.getName());
                mPairedDevices.add(device.getAddress());
            }
            //在接下来添加没有适配过的仪器
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
        findViewById(R.id.tv_newlist_title).setVisibility(VISIBLE);
        if (mAdapter.isDiscovering())
            mAdapter.cancelDiscovery();
        //开始搜索
        mAdapter.startDiscovery();
        isSearch = true;
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

    /**
     * 点击Item后关闭当前Activity,然后返回蓝牙的地址给前一个Activity
     */
    OnItemClickListener mItemlistener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //点击时停止查找
            mAdapter.cancelDiscovery();
            String name=((TextView)view).getText().toString();
            //如果是没有项目的话，则不会进行下去
            if (name.equals("当前没有配对的数据")||name.equals("当前没有设备")){
                return;
            }
            Intent intent = new Intent();//创建一个空intent来存放数据
            String address;
            //通过adapter的类型来返回对应List中的蓝牙的地址
            if (parent.getAdapter().equals(mPairedAdapter))
                address=mPairedDevices.get(position);
            else
                address=mNewDevices.get(position);
            intent.putExtra(ADDRESS, address);
            intent.putExtra(NAME,name);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    /**
     * 长点击后可以删除该适配
     */

    //广播接收者接收广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//监听两个Action
            switch (action) {//当搜索结束时
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (mNewAdapter.getCount() == 0 && isSearch) {
                        mNewAdapter.add("当前没有设备");
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    //找到一个新的蓝牙设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//如果已经绑定就不用添加进列表了
                        mNewAdapter.add(device.getName()+"");
                        mNewDevices.add(device.getAddress());
                    }
                    break;
            }
        }
    };
}
