package com.zhd.iotest;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class MainActivity extends FragmentActivity {

    private Button btn_write, btn_read,btn_jump;
    private TextView tv_content;
    private FileOutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_content = (TextView) findViewById(R.id.tv_content);
        btn_write = (Button) findViewById(R.id.btn_output);
        btn_jump= (Button) findViewById(R.id.btn_jump);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteFile();
            }
        });
        btn_read = (Button) findViewById(R.id.btn_input);
        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //readFile();
                readBybytes();
            }
        });
        btn_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.zhd.viewPaper.START");
                startActivity(intent);
            }
        });
    }

    //可以了
    private void WriteFile() {
        try {
            FileOutputStream out=new FileOutputStream(getFilesDir().getPath()+"/a.txt",true);
            String msg="无存的证明";
            out.write(msg.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置缓冲char[]来读取内容，然后进行存储
     */
    private void readBybytes() {
        File file=new File(getFilesDir(),"a.txt");
        try {
            FileInputStream in=new FileInputStream(file);
            InputStreamReader reader=new InputStreamReader(in);
            //如果是InputStream的话，用的是char[]而不是byte数组
            char[] buffer=new char[12];
            int num=0;
            StringBuilder sb=new StringBuilder();
            while ((num=reader.read(buffer))!=-1){
                String msg=new String(buffer,0,num);
                sb.append(msg);
            }
            tv_content.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() {
        File file = new File(getFilesDir(), "b.txt");
        try {
            FileInputStream in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String msg = "";
            StringBuilder sb = new StringBuilder();
            while ((msg = reader.readLine()) != null) {
                sb.append(msg);
            }
            tv_content.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void copyFile() {
        //大数据量都需要边读边写
        File infile = new File(getFilesDir(), "a.txt");
        File outfile = new File(getFilesDir(), "b.txt");
        FileInputStream in=null;
        FileOutputStream out=null;
        //String msg = "这是写入的内容";
        try {
            in = new FileInputStream(infile);
            out = new FileOutputStream(outfile);
            byte[] buffer = new byte[1024];
            //设置实际读取的量
            int num = 0;
            while ((num = in.read(buffer)) != -1) {
                out.write(buffer, 0, num);
                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
