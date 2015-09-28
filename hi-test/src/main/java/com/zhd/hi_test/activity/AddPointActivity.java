package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.db.Curd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/28.
 *
 */
public class AddPointActivity extends Activity {
    //控件
    TextView tv_B,tv_L,tv_H;
    EditText et_des,et_height;
    Button btn_add;

    //添加进数据库中需要的东西
    private String mTableName;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpoint);
        //找到控件
        tv_B= (TextView) findViewById(R.id.tv_B);
        tv_L= (TextView) findViewById(R.id.tv_L);
        tv_H= (TextView) findViewById(R.id.tv_H);
        et_des= (EditText) findViewById(R.id.et_des);
        et_height= (EditText) findViewById(R.id.et_height);
        btn_add= (Button) findViewById(R.id.btn_add);
        //根据传过来的数据TextView
        Intent intent=getIntent();
        tv_B.setText(intent.getStringExtra("B"));
        tv_L.setText(intent.getStringExtra("L"));
        tv_H.setText(intent.getStringExtra("H"));
        //获取全局变量中表的信息
        Data d=(Data)getApplication();
        mTableName=d.getmProject().getmTableName();
        mContext=this;
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取信息，添加进数据库
                Curd curd=new Curd(mTableName,mContext);
                List<ContentValues> values=new ArrayList<ContentValues>();
                ContentValues cv=new ContentValues();
                cv.put("B",tv_B.getText().toString());
                cv.put("L",tv_L.getText().toString());
                cv.put("H",tv_H.getText().toString());
                cv.put("DES",et_des.getText().toString());
                cv.put("height", et_height.getText().toString());
                values.add(cv);
                boolean res=curd.insertData(values);
                if (res){
                    Toast.makeText(AddPointActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddPointActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
