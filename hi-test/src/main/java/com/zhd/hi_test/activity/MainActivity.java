package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class MainActivity extends Activity implements OnClickListener {

    private String mPath;
    private Button mPro, mStarmap, mFragment, mBluetooth, btn_filein, btn_fileout;
    private TextView tv_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPro = (Button) findViewById(R.id.btn_project);
        mStarmap = (Button) findViewById(R.id.btn_starmap);
        mFragment = (Button) findViewById(R.id.btn_fragment);
        mBluetooth = (Button) findViewById(R.id.btn_bluetooth);
        btn_filein = (Button) findViewById(R.id.btn_filein);
        btn_fileout = (Button) findViewById(R.id.btn_fileout);
        tv_file = (TextView) findViewById(R.id.tv_file);
        mPro.setOnClickListener(this);
        mStarmap.setOnClickListener(this);
        mFragment.setOnClickListener(this);
        mBluetooth.setOnClickListener(this);
        btn_filein.setOnClickListener(this);
        btn_fileout.setOnClickListener(this);
        createDirectory();
        getWindowValue();
    }

    //在储存卡上创建文件夹
    private void createDirectory() {
        File ext_path = Environment.getExternalStorageDirectory();
        File file = new File(ext_path, "ZHD_TEST");
        //第一次安装，或判断是否存在
        if (!file.exists()) {
            file.mkdir();
        }
        //创建Project目录
        File pro_file = new File(file.getPath() + "/Project");
        if (!pro_file.exists()) {
            //第一次创建后就不会再第二次创建了
            pro_file.mkdir();
        }
        //最终获取其路径,并将其赋值给全局变量
        mPath = pro_file.getPath();
        SharedPreferences.Editor sp = getSharedPreferences("VALUE", MODE_PRIVATE).edit();
        sp.putString("path", mPath);
        sp.commit();
    }

    private void getWindowValue() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        //获得宽和高,就是整个屏幕的宽和高
        int width = p.x;
        int height = p.y;
        SharedPreferences sp = getSharedPreferences("VALUE", MODE_PRIVATE);
        sp.edit().putInt("WIDTH", width).commit();
        sp.edit().putInt("HEIGHT", height).commit();
    }

    @Override
    public void onClick(View v) {
        //跳转意图
        switch (v.getId()) {
            case R.id.btn_project:
                Intent intent1 = new Intent("com.zhd.project.START");
                intent1.putExtra("path", mPath);
                startActivity(intent1);
                break;
            case R.id.btn_starmap:
                Intent intent2 = new Intent(this, GPSTestActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_fragment:
                Intent intent3 = new Intent("com.zhd.viewPaper.START");
                startActivity(intent3);
                break;
            case R.id.btn_bluetooth:
                Intent intent4 = new Intent(this, BlueToothActivity.class);
                startActivity(intent4);
                break;
            case R.id.btn_filein:
                File myfile = new File(getFilesDir().getPath() + "/new.txt");
                try {
                    FileInputStream in = new FileInputStream(myfile);
                    byte[] buffer = new byte[1024];
                    while (in.read() != -1) {

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_fileout:
                File file = new File(getFilesDir(), "new.txt");
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
