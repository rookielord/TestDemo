package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.module.InnerGPSConnect;
import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.ui.StarView;
import com.zhd.hi_test.util.Infomation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/10.
 * 在OnCreate中并没有传输数据进StarView里面
 */
public class GPSActivity extends Activity {

    private static final String TAG="GPS_TEST";
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
                //位置数据，因为都是用的同一个类型所以不转移
                case 1:
                    MyLocation myLocation = (MyLocation) msg.obj;
                    tv_locB.setText(String.valueOf(myLocation.getmB()));
                    tv_locL.setText(String.valueOf(myLocation.getmL()));
                    tv_locH.setText(String.valueOf(myLocation.getmH()));
                    break;
                //卫星数据，分两种来解析
                case 2:
                    if (msg.arg1==1){
                        ArrayList<GpsSatellite> satelliteList= (ArrayList<GpsSatellite>) msg.obj;
                        tv_satellite.setText(String.valueOf(satelliteList.size()));
                        my_view.SetSatetllite(satelliteList,msg.arg1);
                        my_view.invalidate();
                    }else if (msg.arg1==2){
                        ArrayList<Satellite> satellites= (ArrayList<Satellite>) msg.obj;
                        tv_satellite.setText(String.valueOf(satellites.size()));
                        my_view.SetSatetllite(satellites, msg.arg1);
                        my_view.invalidate();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        //找控件
        my_view = (StarView) findViewById(R.id.my_view);
        tv_satellite = (TextView) findViewById(R.id.tv_satellite);
        tv_locB = (TextView) findViewById(R.id.tv_LocB);
        tv_locL = (TextView) findViewById(R.id.tv_LocL);
        tv_locH = (TextView) findViewById(R.id.tv_locH);
        tv_connect = (TextView) findViewById(R.id.tv_connect);
        //根据所选连接方式来获得对应的数据
        int connect = Const.getmConnectType();
        if (connect== Const.InnerGPSConnect) {
            GPSinit();
            tv_connect.setText("内置GPS");
            InnerGPSConnect.setmHandler(mHandler);
        } else if(connect== Const.BlueToothConncet){
            tv_connect.setText("蓝牙");
            Infomation.setHandler(mHandler);
            //进行从IRTK中获得数据进行处理
        }else {
            tv_connect.setText("仪器尚未连接");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void GPSinit() {
        mManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mProviders = mManager.getProviders(true);
        if (!mProviders.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "请打开GPS服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("com.zhd.connect.START");
            startActivity(intent);
        }
    }

}
