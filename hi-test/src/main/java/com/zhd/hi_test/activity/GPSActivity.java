package com.zhd.hi_test.activity;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.zhd.hi_test.Constant;
import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.ui.StarView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/10.
 * 在OnCreate中并没有传输数据进StarView里面
 */
public class GPSActivity extends Activity {


    private LocationManager mManager;
    //控件对象
    StarView my_view;
    TextView tv_satellite, tv_locB, tv_locL, tv_locH, tv_connect;
    //返回是否打开GPS服务
    private List<String> mProviders;
    //创建handler，用来接受数据
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //位置数据
                case 1:
                    MyLocation location= (MyLocation) msg.obj;
                    tv_locB.setText(String.valueOf(location.getmB()));
                    tv_locL.setText(String.valueOf(location.getmL()));
                    tv_locH.setText(String.valueOf(location.getmH()));
                    break;
                //卫星数据
                case 2:
                    ArrayList<GpsSatellite> satelliteList= (ArrayList<GpsSatellite>) msg.obj;
                    tv_satellite.setText(String.valueOf(satelliteList.size()));
                    my_view.SetSatetllite(satelliteList);
                    my_view.invalidate();
                    break;
            }
            super.handleMessage(msg);
        }
    };

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
            //这里将handler传递给ConnetActivity,来接受数据。主要是这一句话，将handler传过去后就会一直运行。如果没有传递过去则没有问题
            ConnectActivity.setmHandler(mHandler);
        } else if(connect==Constant.BlueToothConncet){
            tv_connect.setText("蓝牙");
            ConnectActivity.setmHandler(mHandler);
            //进行从IRTK中获得数据进行处理
        }else {
            tv_connect.setText("仪器尚未连接");
        }
    }

    //
    @Override
    protected void onStop() {
        Log.d(Constant.GPS_TAG,"设置为空");
        ConnectActivity.setmHandler(null);
        super.onStop();
    }

    private void GPSinit() {
        mManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mProviders = mManager.getProviders(true);
        if (mProviders.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS服务已经打开", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "请打开GPS服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("com.zhd.connect.START");
            startActivity(intent);
        }
    }

}
