package com.zhd.hi_test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.util.Infomation;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by 2015032501 on 2015/9/22.
 * 通过根据连接方式，来获得对应的数据信息
 * 包括：1.与RTK的蓝牙连接 2.与内部GPS的连接方式：
 * 以上两种方式通过设备连接=》全局变量=》这里获取的方式来获得
 */
public class SurveyActivity extends Activity {

    //读取内容
    private OutputStream out;
    //获得对应的连接对象
    private static final String TAG = "Survey";
    private TextView tv_info;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MyLocation location = (MyLocation) msg.obj;
                    tv_info.append("当前的位置信息是" + location.getB() + "\t" + location.getL() + "\t" + location.getH() + "\t\n");
                    break;
                case 2:
                    GetSattelites((ArrayList<Satellite>) msg.obj);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        tv_info = (TextView) findViewById(R.id.tv_info);
        Data d = (Data) getApplication();
        Infomation.setHandler(mHandler);
        //根据data中获取连接方式，判断是内置GPS还是RTK连接
    }


    public synchronized void GetSattelites(ArrayList<Satellite> satellites) {
        StringBuilder sb = new StringBuilder();
        String type = "";
        if (satellites.size()>0) {
            for (Satellite s : satellites) {
                switch (s.getmType()) {
                    case 1:
                        type = "GPS";
                        break;
                    case 2:
                        type = "GLONASS";
                        break;
                    case 3:
                        type = "BD";
                        break;
                }
                sb.append("卫星种类:" + type + "\n" +
                        "卫星的高度角" + s.getmElevation() + "\n" +
                        "卫星的方位角" + s.getmAzimuth() + "\n" +
                        "卫星的编号" + s.getmSnr() + "\n" +
                        "卫星的信噪比" + s.getmPrn() + "\n\n");
            }
            tv_info.append(sb.toString());
        }
    }

    @Override
    protected void onDestroy() {
        Infomation.setHandler(null);
        super.onDestroy();
    }
}
