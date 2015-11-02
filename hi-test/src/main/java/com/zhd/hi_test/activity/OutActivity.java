package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.util.FileUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by 2015032501 on 2015/10/21.
 * 用于选择页面导出的内容,首先从数据库中获得可以导出的列，然后用checkbox,添加进
 */
public class OutActivity extends Activity implements View.OnClickListener {
    //控件
    LinearLayout ll_select_item;
    Button btn_out;
    //数据库查询用
    private Curd mCurd;
    private String mFliePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out);
        mFliePath = getIntent().getStringExtra("path");
        init();
        setResult(RESULT_CANCELED);
    }

    private void init() {
        ll_select_item = (LinearLayout) findViewById(R.id.ll_select_item);
        btn_out = (Button) findViewById(R.id.btn_out);
        //进行数据库中字段的查询，动态向linearlayout中添加checkbox
        mCurd = new Curd(Const.getmProject().getmTableName(), this);
        Cursor cursor = mCurd.queryData(new String[]{"*"});
        btn_out.setOnClickListener(this);
        String[] columnNames = cursor.getColumnNames();
        //动态生成checkbox
        CheckBox checkBox;
        for (int i = 0; i < columnNames.length; i++) {
            checkBox = new CheckBox(this);
            checkBox.setText(columnNames[i]);
            checkBox.setTextSize(18);
            checkBox.setTextColor(Color.parseColor("#ff0099cc"));
            checkBox.setPadding(10,10,10,10);
            ll_select_item.addView(checkBox, i);
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_out:
                boolean res = WriteFile();
                if (res) {
                    Toast.makeText(this, R.string.output_success, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, R.string.output_failure, Toast.LENGTH_SHORT).show();
                break;
        }
        finish();
    }

    /**
     * 1.遍历ll中的checkbox
     * 2.获取选中的checkbox的内容
     * 3.然后再进行查询
     * 4.拼接字符串
     * 5.写入内容
     *
     * @return
     */
    private boolean WriteFile() {
        try {
            int num = ll_select_item.getChildCount();
            //获得所有的选择的checkbox中的内容
            List<String> columns = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                CheckBox v = (CheckBox) ll_select_item.getChildAt(i);
                if (v.isChecked())
                    columns.add(v.getText().toString());
            }
            String[] selected_item = columns.toArray(new String[columns.size()]);
            //查询内容
            Cursor cursor = mCurd.queryData(selected_item);
            StringBuilder sb = new StringBuilder();
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    //把查询出来内容中的所有数据都拿出来
                    for (int k = 0; k < cursor.getColumnCount(); k++) {
                        sb.append(cursor.getString(k) + ",");
                    }
                    sb.substring(0,sb.length()-3);//把“，”删除
                    sb.append("\n");
                }
            }
            cursor.close();
            //写入当前的路径
            FileUtil.writeFileByString(mFliePath, sb.toString());
            Intent intent = new Intent();
            intent.putExtra("path", mFliePath);
            setResult(RESULT_OK, intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
