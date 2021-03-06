package com.zhd.hi_test.module;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.Connectable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.location.GpsStatus.*;

/**
 * Created by 2015032501 on 2015/10/17.
 * 用于内置GPS的连接状态
 */
public class InnerGPSConnect implements Connectable {

    private LocationManager mManager;
    private List<String> mProviders;
    private String mProvider;
    private static final int mMinTime = 1000;
    private static final int mMinDistance = 0;
    private static Handler mHandler;
    private Activity mActivity;
    private static final String TAG = "GPS_TEST";
    private static final int GPS_REQUEST = 1;

    /**
     * 内置GPS的位置监听字段
     */
    private LocationListener mLocListener;

    /**
     * 卫星状态的监听字段
     */
    private Listener mListener;


    public InnerGPSConnect(Activity activity) {
        mActivity = activity;
        mManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }


    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 打开GPS操作
     */
    @Override
    public void startConnect() {
        GPSinit();
    }


    /**
     * 因为在实例化私有字段的时候就会传输数据,所以发送数据和读取数据其实是一回事
     * 但是为了区别还是在这里添加位置监听
     */
    @Override
    public void sendMessage() {
    }

    /**
     * 当前是在字段中重写方法来，在这里添加卫星监听,开始注册监听
     */
    @Override
    public void readMessage() {
        if (!Const.Info.isConnected())
            return;
        mLocListener = new LocationListener() {
            //这里可以先获得最后的位置信息，再获得当前的位置信息。定位了就不会调用
            @Override
            public void onLocationChanged(Location location) {
                //先判断Bundle是否存在
                int fixnum = 0;
                if (location.getExtras().get("satellites") != null) {
                    fixnum = (int) location.getExtras().get("satellites");
                }
                if (mHandler != null) {
                    MyLocation loc = new MyLocation(location.getLatitude(),
                            location.getLongitude(), location.getAltitude(), location.getTime(), fixnum);
                    Message m1 = Message.obtain();
                    m1.what = 1;
                    m1.obj = loc;
                    mHandler.sendMessage(m1);

                    UTCDate t = new UTCDate(location.getTime());
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

        mListener = new Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GPS_EVENT_FIRST_FIX:
                        Log.d(TAG, "卫星第一次锁定");
                        break;
                    case GPS_EVENT_SATELLITE_STATUS:
                        Log.d(TAG, "卫星的状态");
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
                        ArrayList<Satellite> satelliteList = new ArrayList<>();
                        //
                        while (it.hasNext() && SatelliteNum <= maxSatellite) {//判断条件1.有卫星数据2.小于最大卫星接收数
                            GpsSatellite s = it.next();
                            Satellite sate = ConvertToSatellite(s);
                            satelliteList.add(sate);
                            SatelliteNum++;
                        }
                        //只有在有handler的情况下才进行数据传输，因为卫星数据分两套解析所以，需要对mesaage携带的数据进行赋值
                        if (mHandler != null) {
                            Object satellitesInfo = satelliteList.clone();
                            Message message = Message.obtain();
                            message.what = 2;
                            message.obj = satellitesInfo;
                            mHandler.sendMessage(message);
                        }
                        break;
                    //GPS定位启动
                    case GPS_EVENT_STARTED:
                        Log.d(TAG, "定位启动");
                        break;
                    case GPS_EVENT_STOPPED:
                        Log.d(TAG, "定位结束");
                        break;
                }
            }
        };
        mManager.requestLocationUpdates(mProvider, mMinTime, mMinDistance, mLocListener);
        mManager.addGpsStatusListener(mListener);
    }

    /**
     * 将GpsSatellite对象转化为Satellite
     *
     * @param s
     * @return
     */
    private Satellite ConvertToSatellite(GpsSatellite s) {
        String elevation = String.valueOf(s.getElevation());
        String azimuth = String.valueOf(s.getAzimuth());
        String snr = String.valueOf(s.getSnr());
        String prn = String.valueOf(s.getPrn());
        //创建Satellite对象
        int type = getSatelliteType(s.getPrn());
        Satellite con_s = new Satellite(prn, elevation, azimuth, snr, type);
        return con_s;
    }


    @Override
    public void breakConnect() {
        if (!Const.Info.isConnected())
            return;
        if (mListener != null)
            mManager.removeGpsStatusListener(mListener);
        if (mLocListener != null)
            mManager.removeUpdates(mLocListener);
        Const.Info.SetInfo(Const.NoneConnect, false, mActivity.getString(R.string.unconnected));
        if (mHandler!=null)
        mHandler.sendEmptyMessage(Const.TYPE_CLEAR);
        Const.HasDataInfo = false;
        Const.HasPDOP = false;
    }

    /**
     * 判断GPS定位是否打开的操作
     */
    private void GPSinit() {
        mProviders = mManager.getProviders(true);
        if (mProviders.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(mActivity, R.string.GPS_confirm, Toast.LENGTH_SHORT).show();
            mProvider = LocationManager.GPS_PROVIDER;
            ((Button) mActivity.findViewById(R.id.btn_connect)).setText(R.string.disconnect);
            ((TextView) mActivity.findViewById(R.id.tv_device_info)).setText(R.string.innergps);
            Const.Info.SetInfo(Const.InnerGPSConnect, true, mActivity.getString(R.string.innergps));
        } else {//这里是对ConnectActivity进行操作
            Toast.makeText(mActivity, R.string.open_GPS, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mActivity.startActivityForResult(intent, GPS_REQUEST);
        }
    }

    private int getSatelliteType(int prn) {
        int type = -1;
        if (prn >= 1 && prn < 33) {
            type = Satellite.GPS;
        } else if (prn >= 120 && prn < 152) {
            type = Satellite.SBAS;
        } else if (prn >= 65 && prn < 97) {
            type = Satellite.GLONASS;
        } else if (prn >= 161) {
            type = Satellite.BD;
        }
        return type;
    }

}
