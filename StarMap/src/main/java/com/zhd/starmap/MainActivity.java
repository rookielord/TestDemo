package com.zhd.starmap;


import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import static android.view.View.*;


public class MainActivity extends ActionBarActivity implements OnClickListener {


    Button btn_satellite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_satellite = (Button) findViewById(R.id.btn_satellite);
        btn_satellite.setOnClickListener(this);
        setWindowValue();
    }

    /**
     * 获得屏幕的宽和高
     */
    private void setWindowValue() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        //获得宽和高,就是整个屏幕的宽和高
        int width = p.x;
        int height = p.y;
        SharedPreferences sp=getSharedPreferences("VALUE",MODE_PRIVATE);
        sp.edit().putInt("WIDTH",width).commit();
        sp.edit().putInt("HEIGHT",height).commit();
    }

    //判断GPS是否开启，如果没有打开则告诉用户打开
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_satellite:
                Intent i = new Intent(this, GPSTestActivity.class);
                startActivity(i);
                break;
        }
    }
}
