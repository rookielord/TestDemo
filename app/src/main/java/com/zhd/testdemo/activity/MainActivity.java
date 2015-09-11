package com.zhd.testdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
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
