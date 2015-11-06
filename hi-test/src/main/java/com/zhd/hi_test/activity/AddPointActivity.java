package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.db.Curd;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加和修改都用一个页面，只能改一些数据，不能更改BLH数据
 */
public class AddPointActivity extends Activity {
    //控件
    TextView tv_B, tv_L, tv_H, tv_N, tv_E, tv_Z, tv_DireB, tv_DireL,tv_time;
    EditText et_des, et_height, et_name;
    TextView tv_add, tv_back;

    //添加进数据库中需要的东西
    private String mTableName;
    private Context mContext;
    private Curd curd;
    //目标高，只要设置了一次，其它的都和它一样了
    private double mheight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpoint);
        //默认设置没有进行添加
        setResult(RESULT_CANCELED);
        //找到控件
        tv_B = (TextView) findViewById(R.id.tv_B);
        tv_L = (TextView) findViewById(R.id.tv_L);
        tv_H = (TextView) findViewById(R.id.tv_H);
        tv_N = (TextView) findViewById(R.id.tv_N);
        tv_E = (TextView) findViewById(R.id.tv_E);
        tv_Z = (TextView) findViewById(R.id.tv_Z);
        tv_time= (TextView) findViewById(R.id.tv_time);
        tv_DireB= (TextView) findViewById(R.id.tv_DireB);
        tv_DireL= (TextView) findViewById(R.id.tv_DireL);
        et_des = (EditText) findViewById(R.id.et_des);
        et_height = (EditText) findViewById(R.id.et_height);
        et_name = (EditText) findViewById(R.id.et_name);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_back = (TextView) findViewById(R.id.tv_back);
        //获取全局变量中表的信息
        mTableName = Const.getmProject().getmTableName();
        mContext = this;
        curd = new Curd(mTableName, mContext);
        //根据传过来的数据TextView
        Intent intent = getIntent();
        //判断是添加还是修改
        final String id = intent.getStringExtra("ID");
        if (id == null) {//添加
            //在添加栏目中获得目标高,无论有否，都将仪高赋给editview
            mheight = Const.getheight();
            et_height.setText(String.valueOf(mheight));
            //获得最后的插入id，然后拼接成点号
            final int lastID = curd.getLastID();
            et_name.setText("pt" + (lastID + 1));
            tv_B.setText(intent.getStringExtra("B"));
            tv_L.setText(intent.getStringExtra("L"));
            tv_H.setText(intent.getStringExtra("H"));
            tv_N.setText(intent.getStringExtra("N"));
            tv_E.setText(intent.getStringExtra("E"));
            tv_Z.setText(intent.getStringExtra("Z"));
            tv_time.setText(intent.getStringExtra("time"));
            tv_DireB.setText(intent.getStringExtra("DireB"));
            tv_DireL.setText(intent.getStringExtra("DireL"));
            tv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取信息，添加进数据库
                    List<ContentValues> values = new ArrayList<ContentValues>();
                    ContentValues cv = new ContentValues();
                    cv.put("id", lastID + 1);
                    cv.put("B", tv_B.getText().toString());
                    cv.put("L", tv_L.getText().toString());
                    cv.put("H", tv_H.getText().toString());
                    cv.put("N", tv_N.getText().toString());
                    cv.put("E", tv_E.getText().toString());
                    cv.put("Z", tv_Z.getText().toString());
                    cv.put("DES", et_des.getText().toString());
                    cv.put("height", et_height.getText().toString());
                    cv.put("DireB", tv_DireB.getText().toString());
                    cv.put("DireL", tv_DireL.getText().toString());
                    cv.put("time",tv_time.getText().toString());
                    values.add(cv);
                    //这里将mheight重新赋值给全局变量
                    Const.setheight(Double.parseDouble(et_height.getText().toString()));
                    boolean res = curd.insertData(values);
                    if (res) {
                        Toast.makeText(AddPointActivity.this, R.string.add_success, Toast.LENGTH_SHORT).show();
                        //添加成功后关闭该页面
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddPointActivity.this, R.string.add_failure, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {//修改
            tv_add.setText(R.string.update);
            et_name.setText("pt" + id);
            Cursor cursor = curd.queryData(new String[]{"*"}, "id=?", new String[]{id});
            while (cursor.moveToNext()) {
                tv_B.setText(cursor.getString(cursor.getColumnIndex("B")));
                tv_L.setText(cursor.getString(cursor.getColumnIndex("L")));
                tv_H.setText(cursor.getString(cursor.getColumnIndex("H")));
                tv_N.setText(cursor.getString(cursor.getColumnIndex("N")));
                tv_E.setText(cursor.getString(cursor.getColumnIndex("E")));
                tv_Z.setText(cursor.getString(cursor.getColumnIndex("Z")));
                tv_DireB.setText(cursor.getString(cursor.getColumnIndex("DireB")));
                tv_DireL.setText(cursor.getString(cursor.getColumnIndex("DireL")));
                tv_time.setText(cursor.getString(cursor.getColumnIndex("time")));
                et_des.setText(cursor.getString(cursor.getColumnIndex("DES")));
                et_height.setText(cursor.getString(cursor.getColumnIndex("height")));
            }
            cursor.close();
            //将获取到的数据进行更新
            tv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues cv = new ContentValues();
                    cv.put("DES", et_des.getText().toString());
                    cv.put("height", et_height.getText().toString());
                    boolean res = curd.UpdateData(id, cv);
                    if (res) {
                        Toast.makeText(AddPointActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
                        //添加成功设置修改成功，并关闭当前页面
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddPointActivity.this, R.string.update_failure, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPointActivity.this.finish();
            }
        });
    }
}
