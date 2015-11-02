package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.IconAdapter;
import com.zhd.hi_test.interfaces.OnIconChangeListener;
import com.zhd.hi_test.module.Icon;

import java.util.ArrayList;


/**
 * Created by 2015032501 on 2015/9/19.
 * 这里是显示滑动的页面，每个页面包括图标信息和跳转的intent
 */
public class GridActivity extends Activity {

    private ArrayList<Icon> AllSource = new ArrayList<>();

    //控件
    GridView gridView;
    private int PageID;
    private IconAdapter myAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        AllSource.add(new Icon(getString(R.string.project_manage), R.mipmap.ic_root_file_proj, new Intent("com.zhd.project.START")));
        AllSource.add(new Icon(getString(R.string.data_manage), R.mipmap.ic_root_file_data, new Intent("com.zhd.manage.START")));
        AllSource.add(new Icon(getString(R.string.data_exchange), R.mipmap.ic_root_file_exchange, new Intent("com.zhd.file_exchange.START")));
        AllSource.add(new Icon(getString(R.string.device_connect), R.mipmap.ic_root_device_mag, new Intent("com.zhd.connect.START")));
        AllSource.add(new Icon(getString(R.string.star_map), R.mipmap.ic_root_file_param, new Intent("com.zhd.star_map.START")));
        AllSource.add(new Icon(getString(R.string.data_acquisition), R.mipmap.ic_root_survey_point, new Intent("com.zhd.survey.START")));
        getMyadapter();
        gridView = (GridView) findViewById(R.id.project_gridview);
        gridView.setAdapter(myAdapter);
    }

    private void getMyadapter() {
        Intent intent = getIntent();
        PageID = intent.getIntExtra("PageNum", -1);
        //这里根据ID传入对应的资源
        switch (PageID) {
            case 1:
                ArrayList<Icon> list1 = new ArrayList<>();
                list1.add(AllSource.get(0));
                list1.add(AllSource.get(1));
                myAdapter = new IconAdapter(this, list1);
                break;
            case 2:
                ArrayList<Icon> list2 = new ArrayList<>();
                list2.add(AllSource.get(2));
                list2.add(AllSource.get(3));
                myAdapter = new IconAdapter(this, list2);
                break;
            case 3:
                ArrayList<Icon> list3 = new ArrayList<>();
                list3.add(AllSource.get(4));
                list3.add(AllSource.get(5));
                myAdapter = new IconAdapter(this, list3);
                break;
        }
        myAdapter.setmIicon(new OnIconChangeListener() {
            @Override
            public void OnIconclick(Icon icon) {
                startActivity(icon.getIcon_intent());
            }
        });

    }


}
