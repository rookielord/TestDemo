package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.GridAdapter;
import com.zhd.hi_test.callback.IiconCallback;
import com.zhd.hi_test.module.Icon;
import com.zhd.hi_test.util.Method;

import java.util.ArrayList;


/**
 * Created by 2015032501 on 2015/9/19.
 */
public class GridActivity extends Activity {

    //这里创建所有的图标资源，然后通过传入的PageID来决定传入使用哪些进行进行填充
    private static ArrayList<Icon> AllSource = new ArrayList<Icon>();
    static {
        AllSource.add(new Icon("项目管理", R.drawable.ic_root_file_proj, 0, new Intent("com.zhd.project.START")));
        AllSource.add(new Icon("星图展示", R.drawable.ic_root_file_param, 1, new Intent("com.zhd.starmap.START")));
        AllSource.add(new Icon("数据管理", R.drawable.ic_root_file_data, 2, new Intent("com.zhd.manage.START")));
        AllSource.add(new Icon("仪器连接", R.drawable.ic_root_device_mag, 3, new Intent("com.zhd.bluetooth.START")));
        AllSource.add(new Icon("数据采集", R.drawable.ic_root_survey_point, 4, new Intent("com.zhd.survey.START")));
    }

    private GridView gridView;
    int PageID;
    GridAdapter myAdapter = null;
    private static final String TAG = "LIJIAJI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_main);
        //在储存卡创建文件夹，并将路径设为全局变量
        Method.createDirectory(this);
        Method.getWindowValue(this);
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
                myAdapter = new GridAdapter(this, list1);
                break;
            case 2:
                ArrayList<Icon> list2 = new ArrayList<>();
                list2.add(AllSource.get(1));
                myAdapter = new GridAdapter(this, list2);
                break;
            case 3:
                ArrayList<Icon> list3 = new ArrayList<>();
                list3.add(AllSource.get(2));
                list3.add(AllSource.get(3));
                myAdapter = new GridAdapter(this, list3);
                break;
            case 4:
                ArrayList<Icon> list4 = new ArrayList<>();
                list4.add(AllSource.get(4));
                myAdapter = new GridAdapter(this, list4);
                break;
        }
        myAdapter.setmIicon(new IiconCallback() {
            @Override
            public void Onclick(Icon icon) {
                startActivity(icon.getIcon_intent());
            }
        });

    }


}
