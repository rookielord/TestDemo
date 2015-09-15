package com.zhd.testdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;


import com.zhd.testdemo.R;

import java.io.File;


public class MainActivity extends ActionBarActivity implements OnClickListener{

    Button btn_project,btn_starmap;
    private static String mPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDirectory();
        btn_project= (Button) findViewById(R.id.btn_project);
        btn_starmap= (Button) findViewById(R.id.btn_starmap);
        btn_project.setOnClickListener(this);
        btn_starmap.setOnClickListener(this);
        setWindowValue();
    }

    private void setWindowValue() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        //获得宽和高,就是整个屏幕的宽和高
        int width = p.x;
        int height = p.y;
        SharedPreferences.Editor editor=getSharedPreferences("VALUE",MODE_PRIVATE).edit();
        editor.putInt("WIDTH",width);
        editor.putInt("HEIGHT",height);
        editor.commit();
    }

    private void createDirectory() {
        File ext_path = Environment.getExternalStorageDirectory();
        File file = new File(ext_path, "ZHD_TEST");
        //第一次安装，或判断是否存在
        if (!file.exists()){
            file.mkdir();
        }
        //创建Project目录
        File pro_file=new File(file.getPath()+"/Project");
        if (!pro_file.exists()){
            //第一次创建后就不会再第二次创建了
            pro_file.mkdir();
        }
        //最终获取其路径
        mPath=pro_file.getPath();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_project:
                //点击后启动项目管理Activity
                Intent project=new Intent("com.zhd.project.START");
                project.putExtra("path",mPath);
                startActivity(project);
                break;
            case R.id.btn_starmap:
                Intent starmap=new Intent(this,StarMapActivity.class);
                startActivity(starmap);
                break;
        }
    }
}
