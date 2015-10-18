package com.zhd.hi_test.module;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Constant;
import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.IConnect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 2015032501 on 2015/10/17.
 * 用于内置GPS的连接状态
 */
public class InnerGPSConnect implements IConnect {

    private LocationManager mManager;
    private List<String> mProviders;
    private String mProvider;
    private int minTime = 1000;
    private int minDistance = 0;
    private Handler mHandler;
    private Context mContext;

    //显示连接状态和连接对象的Textview
    TextView tv_connect, tv_info;

    /**
     * 内置GPS的位置监听字段
     */
    private LocationListener mLocListener;

    /**
     * 卫星状态的监听字段
     */
    private GpsStatus.Listener mListener;


    /**
     * 进行对象的传输
     *
     * @param handler
     */
    public InnerGPSConnect(Handler handler, Context context) {
        mHandler = handler;
        mContext = context;
        mManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        tv_connect = (TextView) Data.getmActivity().findViewById(R.id.tv_connect);
    }

    //判断打开GPS操作
    @Override
    public void startConnect() {
        GPSinit();
    }

    //因为在实例化私有字段的时候就会传输数据,所以发送数据和读取数据其实是一回事
    //但是为了区别还是在这里添加位置监听
    @Override
    public void sendMessage() {
        if (!Data.isConnected())
            return;
        mLocListener = new LocationListener() {
            //这里可以先获得最后的位置信息，再获得当前的位置信息。定位了就不会调用
            @Override
            public void onLocationChanged(Location location) {
                String longitude = String.valueOf(location.getLongitude());
                String altitude = String.valueOf(location.getAltitude());
                String latitude = String.valueOf(location.getLatitude());
                long time = location.getTime();
                //在有handler的情况下才进行数据传输
                if (mHandler != null) {
                    MyLocation loc = new MyLocation(latitude, longitude, altitude, time);
                    Message m1 = Message.obtain();
                    m1.what = 1;
                    m1.obj = loc;
                    mHandler.sendMessage(m1);

                    UTCDate t = new UTCDate(time);
                    Message m2 = Message.obtain();
                    m2.what = 3;
                    m2.obj = t;
                    mHandler.sendMessage(m2);
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

    //当前是在字段中重写方法来，在这里添加卫星监听
    @Override
    public void readMessage() {
        mListener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        Log.d(Constant.GPS_TAG, "卫星第一次锁定");
                        break;
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
                        //只有在有handler的情况下才进行数据传输，因为卫星数据分两套解析所以，需要对mesaage携带的数据进行赋值
                        if (mHandler != null) {
                            Object satellitesInfo = satelliteList.clone();
                            Message message = Message.obtain();
                            message.what = 2;
                            message.obj = satellitesInfo;
                            message.arg1 = 1;//确保是内置GPS的卫星数据
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
    }

    @Override
    public void breakConnect() {
        if (mListener != null)
            mManager.removeGpsStatusListener(mListener);
        if (mLocListener != null)
            mManager.removeUpdates(mLocListener);
        Data.setIsConnected(false);

    }

    private void GPSinit() {
        mProviders = mManager.getProviders(true);
        if (mProviders.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(mContext, "GPS服务已经打开", Toast.LENGTH_SHORT).show();
            mProvider = LocationManager.GPS_PROVIDER;
            mManager.requestLocationUpdates(mProvider, minTime, minDistance, mLocListener);
            mManager.addGpsStatusListener(mListener);
            //这里才能算上GPS连上了
            Data.setmConnectType(Constant.InnerGPSConnect);
            ((TextView) Data.getmActivity().findViewById(R.id.btn_connect)).setText("断开");
            Data.setIsConnected(true);
            Data.setmInfo("内置GPS");
        } else {//这里是对ConnectActivity进行操作
            Toast.makeText(mContext, "请打开GPS服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            Data.getmActivity().startActivityForResult(intent, Data.GPS_REQUEST);
        }
    }
}
