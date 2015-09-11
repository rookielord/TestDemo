package com.zhd.starmap;

import android.app.Activity;
import android.app.Notification;
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

import com.zhd.starmap.ui.StarView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/10.
 */
public class GPSTestActivity extends Activity implements OnClickListener {

    private static final String TAG = "GPS";
    private LocationManager mManager;
    private int minTime = 5000;
    private int minDistance = 5;
    //控件对象
    TextView tv_gps, tv_satlitte;
    Button btn_loc;
    StarView my_view;
    //所使用的位置提供器
    private String mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstest);
        //找控件
        tv_gps = (TextView) findViewById(R.id.tv_gps);
        tv_satlitte = (TextView) findViewById(R.id.tv_satellite_info);
        btn_loc = (Button) findViewById(R.id.btn_loc_close);
        my_view = (StarView) findViewById(R.id.my_view);
        //设置按钮监听
        btn_loc.setOnClickListener(this);
        //GPS初始设定
        GPSinit();
        mManager.addGpsStatusListener(mListener);
        mManager.requestLocationUpdates(mProvider, minTime, minDistance, mLocListener);
    }
    //1.获取位置服务(暂时不考虑位置信息)
    //2.设置卫星监听(没有调用到)
    //3.打印内容

    private LocationListener mLocListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longtitude = location.getLongitude();
            double altitude = location.getAltitude();
            double latitude = location.getLatitude();
            //Log.d(TAG,latitude+";"+longtitude+";"+altitude);

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
     * 这是GPS卫星监听的型
     */
    private GpsStatus.Listener mListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            //获取卫星对象
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    tv_gps.setText("卫星锁定了");
                    Log.d(TAG, "卫星第一次锁定");
                    break;
                //卫星状态改变,当定位信息启动一次，那么它就会调用一次
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d(TAG, "卫星的状态");
                    //获取当前接收到的卫星情况
                    GpsStatus status = mManager.getGpsStatus(null);
                    //迭代接口，只有实现了这个接口才能实现其迭代器对象,可以对数据进行修改
                    Iterable<GpsSatellite> satellites = status.getSatellites();
                    //获得迭代对象，然后进行遍历
                    Iterator<GpsSatellite> it = satellites.iterator();
                    //获得最大的卫星数量，对接收的数量进行限制
                    int maxSatellite = status.getMaxSatellites();
                    Log.d(TAG,"获得的最大卫星数"+maxSatellite);
                    int SatelliteNum = 0;
                    //这里创建需要进行传递的对象
                    List<GpsSatellite> satelliteList = new ArrayList<GpsSatellite>();
                    while (it.hasNext() && SatelliteNum <= maxSatellite) {//判断条件1.有卫星数据2.小于最大卫星接收数
                        GpsSatellite s = it.next();
                        satelliteList.add(s);
                        SatelliteNum++;
                    }
                    my_view.SetSatetllite(satelliteList);
                    my_view.invalidate();
                    break;
                //GPS定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "定位启动");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "定位结束");
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loc_close://移除卫星监听和位置监听，位置和卫星算是一起的
                if (mManager != null) {
                    mManager.removeUpdates(mLocListener);
                    mManager.removeGpsStatusListener(mListener);
                }
                break;
        }
    }

    //注销监听


    @Override
    protected void onPause() {
        if (mManager != null) {
            mManager.removeUpdates(mLocListener);
            mManager.removeGpsStatusListener(mListener);
        }
    }

    private void GPSinit() {
        mManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = mManager.getProviders(true);
        providers = mManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS服务已经打开", Toast.LENGTH_SHORT).show();
            mProvider = LocationManager.GPS_PROVIDER;
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
        }
    }

}
