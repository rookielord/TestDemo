package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.module.InnerGPSConnect;
import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.MyPoint;
import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.module.UTCDate;
import com.zhd.hi_test.ui.SurveyView;
import com.zhd.hi_test.ui.ZoomListener;
import com.zhd.hi_test.util.Coordinate;
import com.zhd.hi_test.util.Infomation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/22.
 * 一、开始画图的时候
 * 1.开始画图时的点位：以数据点库为中心，然后绘制其它的点
 * 2.点击整体居中：以数据库点为中心，将平移量和缩放量都设为初始值
 * 3.点当前点居中：以当前点为数据库点为中心，
 * 然后求出当前点距离数据库点的距离，平移量设置平移量.缩放量不变
 * 注意：BUG:在以当前点居中后，再点击添加点后，会跳到初始点的位置。因为在更新点库的时候又是以数据库第一点为中心来画图了
 * 二、没有开始画图
 * 1.开始画图时的点位：以数据库点为中心
 * 2.点击整体居中：以数据库第一个点，平移量缩放量都设为初始值
 * 3.点当前点居中：没有反应
 */
public class SurveyActivity extends Activity implements OnClickListener {

    //控件
    TextView tv_B, tv_L, tv_H, tv_N, tv_E, tv_Z, tv_time, tv_date, tv_satellite, tv_PDOP, tv_age, tv_solution, tv_usesate;
    Button btn_add;
    ImageView image_add, image_zoom_in, image_zoom_out,
            image_zoom_center, image_zoom_all, image_compass, image_baidumap;
    LinearLayout ll_part1, ll_part2, ll_layout1;
    //用来存放当前位置的东西半球和南北半球数据
    private static String mDireB;
    private static String mDireL;
    //自定义控件更新点的集合,包括当前点的位置
    List<MyPoint> myPoints = new ArrayList<>();
    SurveyView surveyView;
    private MyPoint myPoint;
    //滑动需要的控件和数值
    LinearLayout pointLayout;
    ViewPager viewPager;
    //当前选中index
    private int mCurrentIndex;
    //存放图像点的集合
    private ImageView[] dots;
    //对应的布局文件
    private List<View> mViews = new ArrayList<>();
    //数据库操作
    private Curd mCurd;
    private int mid;
    private Cursor mCursor;
    //从AddActivity中返回，判断是否添加后更新点集合
    private static final int ADD_RESULT = 0;
    //指南针的控件相关
    private Sensor mSensor;
    private SensorManager mSensorManager;
    //方向向量数组

    //线程通信
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MyLocation myLocation = (MyLocation) msg.obj;
                    tv_B.setText(myLocation.getmB());
                    tv_L.setText(myLocation.getmL());
                    tv_H.setText(myLocation.getmH());
                    tv_time.setText(myLocation.getmTime());
                    tv_age.setText(myLocation.getmAge());
                    tv_solution.setText(myLocation.getmQuality());
                    tv_usesate.setText(myLocation.getmUseSate());
                    mDireB = myLocation.getmDireB();
                    mDireL = myLocation.getmDireL();
                    double b = Double.valueOf(myLocation.getmProgressB());
                    double l = Double.valueOf(myLocation.getmProgressL());
                    HashMap<String, String> info = Coordinate.getCoordinateXY(b, l, Const.getmProject());
                    tv_N.setText(info.get("n").toString());
                    tv_E.setText(info.get("e").toString());
                    tv_Z.setText(myLocation.getmH());
                    if (!Const.HasDataInfo)
                        tv_date.setText(UTCDate.getDefaultTime());
                    if (!Const.HasPDOP)
                        tv_PDOP.setText("0.0");
                    //需要将当前点的数据传过去,当前点没有名称，因为是现在的位置
                    myPoint = new MyPoint("", Double.valueOf(info.get("n")), Double.valueOf(info.get("e")));
                    surveyView.setMyLocation(myPoint);
                    //重绘
                    surveyView.invalidate();
                    break;
                case 2:
                    List<Satellite> satellites1 = (List<Satellite>) msg.obj;
                    tv_satellite.setText(String.valueOf(satellites1.size()));
                    break;
                case 3://如果没有日期过来的话，如果没有解析蓝牙的GPZDA数据时，其仍然不会有更新
                    Const.HasDataInfo = true;
                    UTCDate time = (UTCDate) msg.obj;
                    tv_date.setText(time.getmCurrentDate());
                    break;
                case 4:
                    Const.HasPDOP = true;
                    tv_PDOP.setText(msg.obj.toString());
                    break;
            }
        }
    };

    //指南针事件监听
    SensorEventListener mListener = new SensorEventListener() {

        private float predegree = 0;

        public void onSensorChanged(SensorEvent event) {
            float degree = event.values[0];// 数组中的第一个数是方向值
            //以自身为中心进行旋转
            RotateAnimation anim = new RotateAnimation(predegree, -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(200);
            image_compass.startAnimation(anim);
            predegree = -degree;//记录这一次的起始角度作为下次旋转的初始角度
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        //指南针可用
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //获取方向传感器
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        image_compass = (ImageView) findViewById(R.id.image_compass);

        if (Const.getmProject() == null) {
            Toast.makeText(this, "请打开项目", Toast.LENGTH_SHORT).show();
            return;
        }
        //初始化页面
        init();
        //初始化下面的点
        initDoc();
        mCurd = new Curd(Const.getmProject().getmTableName(), this);
        iniSurveyView();
        if (Const.getmConnectType() == Const.BlueToothConncet) {
            Infomation.setHandler(mHandler);
        } else if (Const.getmConnectType() == Const.InnerGPSConnect) {
            InnerGPSConnect.setmHandler(mHandler);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册传感器
        mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void addPoint() {
        //每次添加前先获得当前末尾的id
        mid = mCurd.getLastID();
        List<ContentValues> values = new ArrayList<>();
        ContentValues cv = new ContentValues();
        cv.put("id", mid + 1);
        cv.put("B", tv_B.getText().toString());
        cv.put("L", tv_L.getText().toString());
        cv.put("H", tv_H.getText().toString());
        cv.put("N", tv_N.getText().toString());
        cv.put("E", tv_E.getText().toString());
        cv.put("Z", tv_Z.getText().toString());
        cv.put("time", tv_date.getText().toString() + " " + tv_time.getText().toString());
        cv.put("DireB", mDireB);
        cv.put("DireL", mDireL);
        cv.put("DES", "");
        cv.put("height", String.valueOf(Const.getMheight()));
        values.add(cv);
        boolean res = mCurd.insertData(values);
        if (res) {
            Toast.makeText(SurveyActivity.this, "pt" + (mid + 1) + "添加成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SurveyActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 这个方法会在控件尚未初始化的时候将其传过去，即传过去的时候还没有能获得自定义控件的宽和高
     */
    private void iniSurveyView() {
        //找到自定义控件
        surveyView = (SurveyView) findViewById(R.id.survey_view);
        surveyView.setOnTouchListener(new ZoomListener());
        //判断数据库中是否有点和传输数据
        refreshPoints();
    }

    /**
     * 1.对数据库中是否有点进行判断
     * 2.进行points点集合的更新，获取添加进数据库的新的点的集合
     * 3.每次进行更新前，需要清空点的集合
     * 4.在刷新的时候，需要判断
     */
    private void refreshPoints() {
        myPoints.clear();
        mCursor = mCurd.queryData(new String[]{"*"}, "id desc");
        if (mCursor.getCount() != 0) {
            while (mCursor.moveToNext()) {
                //获取数据存入其中
                double N = Double.valueOf(mCursor.getString(mCursor.getColumnIndex("N")));
                double E = Double.valueOf(mCursor.getString(mCursor.getColumnIndex("E")));
                String name = "pt" + mCursor.getString(mCursor.getColumnIndex("id"));
                MyPoint p = new MyPoint(name, N, E);
                myPoints.add(p);
            }
            mCursor.close();
            surveyView.setPoints(myPoints);
            surveyView.invalidate();
        }
    }

    /**
     * 将ViewPaper找到，并设置其填充的内容
     */
    private void init() {
        image_add = (ImageView) findViewById(R.id.image_add);
        image_zoom_center = (ImageView) findViewById(R.id.image_zoom_center);
        image_zoom_in = (ImageView) findViewById(R.id.image_zoom_in);
        image_zoom_out = (ImageView) findViewById(R.id.image_zoom_out);
        image_zoom_all = (ImageView) findViewById(R.id.image_zoom_all);
        image_baidumap = (ImageView) findViewById(R.id.image_baidumap);
        image_baidumap.setOnClickListener(this);
        btn_add = (Button) findViewById(R.id.btn_add_point);
        image_add.setOnClickListener(this);
        image_zoom_in.setOnClickListener(this);
        image_zoom_out.setOnClickListener(this);
        image_zoom_center.setOnClickListener(this);
        image_zoom_all.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.first_vp);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.survey_layout1, null);
        View view2 = inflater.inflate(R.layout.survey_layout2, null);
//        第一页中的数据
        tv_B = (TextView) view1.findViewById(R.id.tv_B);
        tv_L = (TextView) view1.findViewById(R.id.tv_L);
        tv_H = (TextView) view1.findViewById(R.id.tv_H);
        tv_solution = (TextView) view1.findViewById(R.id.tv_solution);
        tv_N = (TextView) view1.findViewById(R.id.tv_N);
        tv_E = (TextView) view1.findViewById(R.id.tv_E);
        tv_Z = (TextView) view1.findViewById(R.id.tv_Z);
        tv_time = (TextView) view1.findViewById(R.id.tv_time);
        tv_date = (TextView) view1.findViewById(R.id.tv_date);
        ll_layout1 = (LinearLayout) view1.findViewById(R.id.ll_layout1);
        ll_layout1.setOnClickListener(this);
        ll_part1 = (LinearLayout) view1.findViewById(R.id.ll_part1);
        ll_part2 = (LinearLayout) view1.findViewById(R.id.ll_part2);
//        第二页中的数据
        tv_satellite = (TextView) view2.findViewById(R.id.tv_satellite);
        tv_PDOP = (TextView) view2.findViewById(R.id.tv_PDOP);
        tv_age = (TextView) view2.findViewById(R.id.tv_age);
        tv_usesate = (TextView) view2.findViewById(R.id.tv_usesate);
        mViews.add(view1);
        mViews.add(view2);
        //设置填充内容，以及页面改变的监听
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            //选中页面时，改变下面点
            @Override
            public void onPageSelected(int position) {
                setDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 初始化下面的点
     */
    private void initDoc() {
        pointLayout = (LinearLayout) findViewById(R.id.point_layout);
        dots = new ImageView[mViews.size()];
        //将所有的点的图像对象找到并存入
        for (int i = 0; i < mViews.size(); i++) {
            dots[i] = (ImageView) pointLayout.getChildAt(i);
        }
        //默认选中的当前页面
        mCurrentIndex = 0;
        dots[mCurrentIndex].setBackgroundResource(R.mipmap.dian_down);
    }

    /**
     * 当滚动的时候更换点的背景图
     */
    private void setDots(int position) {
        if (position < 0 || position > mViews.size() - 1
                || mCurrentIndex == position) {
            return;
        }
        //当前选中位置改变
        dots[position].setBackgroundResource(R.mipmap.dian_down);
        //之前选中位置变为没被选中的状态
        dots[mCurrentIndex].setBackgroundResource(R.mipmap.dian);
        mCurrentIndex = position;
    }

    private PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //这个得到的对象上一个函数的进行比较
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViews.get(position));
            return mViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_RESULT:
                //如果添加成功，则进行数据的刷新
                if (resultCode == RESULT_OK) {
                    refreshPoints();
                    surveyView.setPoints(myPoints);
                    surveyView.invalidate();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 在关闭的时候需要进行的操作：
     * 1.surveyView的参考点清空，不然这次的参考点会影响到下次的测量
     * 2.不再解析数据
     * 3.不再接收内置GPS的数据
     * <p>
     * 注意问题：如果是在OnDestroy的情况下，在返回到MainActivity的时候不会立刻执行OnDestroy
     * 在过一段时候才会执行，这样会导致Activity
     */
    @Override
    protected void onDestroy() {
        if (mListener != null)
            mSensorManager.unregisterListener(mListener);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_add://直接进行数据采集
                if (Const.IsConnected) {
                    addPoint();
                    refreshPoints();
                    surveyView.invalidate();
                }
                break;
            case R.id.btn_add_point://将信息获取到，然后跳转到另外一个界面来,采集数据
                if (Const.IsConnected) {
                    Intent intent = new Intent("com.zhd.addPoint.START");
                    intent.putExtra("B", tv_B.getText().toString());
                    intent.putExtra("L", tv_L.getText().toString());
                    intent.putExtra("H", tv_H.getText().toString());
                    intent.putExtra("N", tv_N.getText().toString());
                    intent.putExtra("E", tv_E.getText().toString());
                    intent.putExtra("Z", tv_Z.getText().toString());
                    intent.putExtra("time", tv_date.getText().toString() + " " + tv_time.getText().toString());
                    intent.putExtra("DireB", mDireB);
                    intent.putExtra("DireL", mDireL);
                    startActivityForResult(intent, ADD_RESULT);
                }
                break;
            case R.id.image_zoom_in://传入放大比例
                surveyView.setScale(1.5f);
                surveyView.invalidate();
                break;
            case R.id.image_zoom_out://传入缩小比例
                surveyView.setScale(0.5f);
                surveyView.invalidate();
                break;
            case R.id.image_zoom_center://当前点居中
                if (myPoint != null) {
                    refreshPoints();
                    surveyView.SetCurrentLocation(myPoint);
                    surveyView.invalidate();
                }
                break;
            case R.id.image_zoom_all:
                surveyView.setmOffsets(0, 0);
                surveyView.setmScale(1);
                surveyView.invalidate();
                break;
            case R.id.ll_layout1:
                if (ll_part1.getVisibility() == View.GONE) {
                    ll_part1.setVisibility(View.VISIBLE);
                    ll_part2.setVisibility(View.GONE);
                } else {
                    ll_part1.setVisibility(View.GONE);
                    ll_part2.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.image_baidumap:
                Intent intent = new Intent("com.zhd.baidumap.START");
                startActivity(intent);
                break;
        }
    }
}
