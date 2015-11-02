package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.GPSAdapter;
import com.zhd.hi_test.adapter.SatelliteAdapter;
import com.zhd.hi_test.module.InnerGPSConnect;
import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.ui.MyViewPaper;
import com.zhd.hi_test.ui.OffsetListener;
import com.zhd.hi_test.ui.SatelliteView;
import com.zhd.hi_test.ui.StarView;
import com.zhd.hi_test.util.Infomation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/10.
 * 在OnCreate中并没有传输数据进StarView里面
 */
public class GPSActivity extends Activity implements OnClickListener, SeekBar.OnSeekBarChangeListener {
    private LocationManager mManager;
    //控件对象
    StarView my_view;
    TextView tv_satellite, tv_locB, tv_locL, tv_locH, tv_connect, tv_PDOP, tv_set_angel;
    EditText et_ele_angel;
    LinearLayout ll_gps_title;
    ListView lv_satellite;
    MyViewPaper viewPager;
    SatelliteView my_sate_view;
    SeekBar sb_ele_angel;
    //添加进viewpaper中的view
    private List<View> mViews = new ArrayList<>();
    //当前
    private int mCurrentIndex = 0;
    private TextView[] textViews;
    //卫星数据的Adapter
    private SatelliteAdapter mAdapter;

    private List<String> mProviders;
    //创建handler，用来接受数据
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //位置数据，因为都是用的同一个类型所以不转移
                case 1:
                    MyLocation myLocation = (MyLocation) msg.obj;
                    tv_locB.setText(String.valueOf(myLocation.getmB()));
                    tv_locL.setText(String.valueOf(myLocation.getmL()));
                    tv_locH.setText(String.valueOf(myLocation.getmH()));
                    if (!Const.HasPDOP)
                        tv_PDOP.setText(getString(R.string.default_PDOP));
                    break;
                case 2:
                    ArrayList<Satellite> satellites = (ArrayList<Satellite>) msg.obj;
                    my_view.SetSatetllite(satellites);
                    mAdapter.clear();
                    mAdapter.addAll(Const.satellites);
                    mAdapter.notifyDataSetChanged();
                    tv_satellite.setText(String.valueOf(Const.satellites.size()));
                    my_sate_view.invalidate();
                    my_view.invalidate();
                    break;
                case 4:
                    Const.HasPDOP = true;
                    tv_PDOP.setText(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        //找到首页的控件
        tv_connect = (TextView) findViewById(R.id.tv_connect);
        et_ele_angel = (EditText) findViewById(R.id.et_ele_angel);
        tv_satellite = (TextView) findViewById(R.id.tv_satellite);
        tv_locB = (TextView) findViewById(R.id.tv_LocB);
        tv_locL = (TextView) findViewById(R.id.tv_LocL);
        tv_locH = (TextView) findViewById(R.id.tv_locH);
        tv_PDOP = (TextView) findViewById(R.id.tv_PDOP);
        tv_set_angel = (TextView) findViewById(R.id.tv_set_angle);
        tv_set_angel.setOnClickListener(this);
        //找在加载页上面的控件
        initTitle();
        mAdapter = new SatelliteAdapter(this, R.layout.satellite_item);
        lv_satellite.setAdapter(mAdapter);
        //根据所选连接方式来获得对应的数据
        int connect = Const.getmConnectType();
        if (connect == Const.InnerGPSConnect) {
            GPSinit();
            tv_connect.setText(getString(R.string.innergps));
            InnerGPSConnect.setmHandler(mHandler);
        } else if (connect == Const.BlueToothConncet) {
            tv_connect.setText(R.string.bluetooth);
            Infomation.setHandler(mHandler);
            //进行从IRTK中获得数据进行处理
        } else {
            tv_connect.setText(getString(R.string.unconnect));
        }
    }

    private void initTitle() {
        ll_gps_title = (LinearLayout) findViewById(R.id.ll_gps_title);
        viewPager = (MyViewPaper) findViewById(R.id.vp_satellite);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.gps_layout1, null);
        View view2 = inflater.inflate(R.layout.gps_layout2, null);
        View view3 = inflater.inflate(R.layout.gps_layout3, null);
        mViews.add(view1);
        mViews.add(view2);
        mViews.add(view3);
//将LinearLayout中的TextView获得
        textViews = new TextView[mViews.size()];
        for (int i = 0; i < mViews.size(); i++) {
            textViews[i] = (TextView) ll_gps_title.getChildAt(i);
            textViews[i].setOnClickListener(new MyOnClickListener(i));
        }
//将页面内容添加进viewPaper
        viewPager.setAdapter(new GPSAdapter(mViews));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentText(position);
                mCurrentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//第一页星空图
        my_view = (StarView) view1.findViewById(R.id.my_view);
        sb_ele_angel = (SeekBar) view1.findViewById(R.id.sb_ele_angel);
        sb_ele_angel.setOnSeekBarChangeListener(this);
//第二页数据表
        lv_satellite = (ListView) view2.findViewById(R.id.lv_satellite);
//第三页的自定义控件
        my_sate_view = (SatelliteView) view3.findViewById(R.id.my_sate_view);
        my_sate_view.setOnTouchListener(new OffsetListener());
    }

    private void setCurrentText(int position) {
        if (position < 0 || position > mViews.size() - 1
                || mCurrentIndex == position) {
            return;
        }
        //设置当前选中页的颜色
        textViews[position].setBackgroundColor(Color.parseColor("#FFA3A3A3"));
        textViews[position].setTextColor(Color.parseColor("#FFFFB265"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set_angle:
                setAngel();
                break;
        }
    }

    private void setAngel() {
        if (et_ele_angel.getText().length() > 0) {
            float elevation = Float.valueOf(et_ele_angel.getText().toString());
            if (elevation >= 0 && elevation <= 90) {
                my_view.setmElev_mask(elevation);
            } else {
                Toast.makeText(this, getString(R.string.ele_range), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.ele_length), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        et_ele_angel.setText(progress + "");
        setAngel();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    class MyOnClickListener implements OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }
        @Override
        public void onClick(View v) {
            setTextBackgroundColor();
            viewPager.setCurrentItem(index);
            setCurrentText(index);
        }
    }

    /**
     * 将所有的textview都设置为默认颜色
     */
    private void setTextBackgroundColor() {
        for (TextView tv : textViews) {
            tv.setBackgroundResource(R.drawable.gps_title);
            tv.setTextColor(getResources().getColor(R.color.textcolor));
        }
    }

    private void GPSinit() {
        mManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mProviders = mManager.getProviders(true);
        if (!mProviders.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), getString(R.string.open_GPS), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("com.zhd.connect.START");
            startActivity(intent);
        }
    }

}
