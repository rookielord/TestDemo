package com.zhd.hi_test.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhd.hi_test.Constant;
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

    //控件
    TextView tv_info,tv_B,tv_L,tv_H;
    Button btn_add;
    //线程通信
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MyLocation location = (MyLocation) msg.obj;
                    tv_B.setText(location.getmB());
                    tv_L.setText(location.getmL());
                    tv_H.setText(location.getmH());
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
        tv_B= (TextView) findViewById(R.id.tv_B);
        tv_L= (TextView) findViewById(R.id.tv_L);
        tv_H= (TextView) findViewById(R.id.tv_H);
        //将信息获取到，然后跳转到另外一个界面来
        btn_add= (Button) findViewById(R.id.btn_add_point);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.zhd.addPoint.START");
                //将信息传递过去tv_B.getText().toString()
                intent.putExtra("B",tv_B.getText().toString());
                intent.putExtra("L",tv_L.getText().toString());
                intent.putExtra("H",tv_H.getText().toString());
                startActivity(intent);
            }
        });
        Data d = (Data) getApplication();
        if (d.getConnectType()== Constant.BlueToothConncet){
            Infomation.setHandler(mHandler);
        }else if (d.getConnectType()==Constant.InnerGPSConnect){

        }

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
                sb.append(
                        "卫星种类:" + type + "\n" +
                        "卫星的高度角" + s.getmElevation() + "\n" +
                        "卫星的方位角" + s.getmAzimuth() + "\n" +
                        "卫星的编号" + s.getmSnr() + "\n" +
                        "卫星的信噪比" + s.getmPrn() + "\n\n");
            }
            tv_info.setText(sb.toString());
        }
    }

    @Override
    protected void onDestroy() {
        Infomation.setHandler(null);
        super.onDestroy();
    }
}
