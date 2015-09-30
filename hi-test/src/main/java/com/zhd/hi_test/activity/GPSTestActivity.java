package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.zhd.hi_test.Constant;
import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.ui.StarView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/10.
 */
public class GPSTestActivity extends Activity {


    private LocationManager mManager;
    private int minTime = 5000;
    private int minDistance = 5;
    //控件对象
    StarView my_view;
    TextView tv_satellite, tv_locB, tv_locL, tv_locH, tv_connect;
    //所使用的位置提供器
    private String mProvider;
    //返回是否打开GPS服务
    private static final int GPS_REQUEST = 1;
    private List<String> mProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstest);
        //找控件
        my_view = (StarView) findViewById(R.id.my_view);
        tv_satellite = (TextView) findViewById(R.id.tv_satellite);
        tv_locB = (TextView) findViewById(R.id.tv_LocB);
        tv_locL = (TextView) findViewById(R.id.tv_LocL);
        tv_locH = (TextView) findViewById(R.id.tv_locH);
        tv_connect = (TextView) findViewById(R.id.tv_connect);
        //根据所选连接方式来获得对应的数据
        Data d = (Data) getApplication();
        int connect = d.getmConnectType();
        if (connect==Constant.InnerGPSConnect) {
            GPSinit();
            tv_connect.setText("内置GPS");
        } else if(connect==Constant.BlueToothConncet){
            tv_connect.setText("蓝牙");
            //进行从IRTK中获得数据进行处理
        }else {
            tv_connect.setText("仪器尚未连接");
        }
    }

    private LocationListener mLocListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double altitude = location.getAltitude();
            double latitude = location.getLatitude();
            tv_locB.setText(String.valueOf(latitude));
            tv_locL.setText(String.valueOf(longitude));
            tv_locH.setText(String.valueOf(altitude));
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

    /**
     * 这是GPS卫星监听
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
                    List<GpsSatellite> satelliteList = new ArrayList<GpsSatellite>();
                    while (it.hasNext() && SatelliteNum <= maxSatellite) {//判断条件1.有卫星数据2.小于最大卫星接收数
                        GpsSatellite s = it.next();
                        satelliteList.add(s);
                        SatelliteNum++;
                    }
                    tv_satellite.setText(String.valueOf(satelliteList.size()));
                    my_view.SetSatetllite(satelliteList);
                    my_view.invalidate();
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


    @Override
    protected void onPause() {
        if (mManager != null) {
            mManager.removeUpdates(mLocListener);
            mManager.removeGpsStatusListener(mListener);
        }
        super.onPause();
    }

    private void GPSinit() {
        mManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mProviders = mManager.getProviders(true);
        if (mProviders.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS服务已经打开", Toast.LENGTH_SHORT).show();
            mProvider = LocationManager.GPS_PROVIDER;
            mManager.addGpsStatusListener(mListener);
            mManager.requestLocationUpdates(mProvider, minTime, minDistance, mLocListener);
        } else {
            Toast.makeText(getApplicationContext(), "请打开GPS服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, GPS_REQUEST);
        }
    }

    /**
     * 判断是否打开GPS服务，然后打开打开界面
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GPS_REQUEST:
                //判断是否打开，没有打开则提示打开并关闭当前界面，判断的话只能用当前是否包含GPS服务
                mProviders = mManager.getProviders(true);
                if (mProviders.contains(LocationManager.GPS_PROVIDER)) {
                    mManager.addGpsStatusListener(mListener);
                    mManager.requestLocationUpdates(mProvider, minTime, minDistance, mLocListener);
                } else {
                    Toast.makeText(this, "请打开GPS服务", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
