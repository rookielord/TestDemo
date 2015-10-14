package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Constant;
import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.MyPoint;
import com.zhd.hi_test.ui.SurveyView;
import com.zhd.hi_test.ui.ZoomListener;
import com.zhd.hi_test.util.Coordinate;
import com.zhd.hi_test.util.Infomation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/22.
 * 通过根据连接方式，来获得对应的数据信息
 * 包括：1.与RTK的蓝牙连接 2.与内部GPS的连接方式：
 * 以上两种方式通过设备连接=》全局变量=》这里获取的方式来获得
 */
public class SurveyActivity extends Activity implements OnClickListener{

    private Data d;
    //控件
    TextView tv_B, tv_L, tv_H, tv_N, tv_E, tv_Z, tv_time;
    Button btn_add;
    ImageView image_add,image_zoom_in,image_zoom_out,image_zoom_center;
    //用来存放当前位置的东西半球和南北半球数据
    private static String mDireB;
    private static String mDireL;
    //自定义控件更新点的集合
    List<MyPoint> points = new ArrayList<>();
    SurveyView surveyView;
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
    //线程通信
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MyLocation location = (MyLocation) msg.obj;
                    tv_B.setText(location.getmB());
                    tv_L.setText(location.getmL());
                    tv_H.setText(location.getmH());
                    tv_time.setText(location.getmTime());
                    mDireB=location.getmDireB();
                    mDireL=location.getmDireL();
                    double b = Double.valueOf(location.getmProgressB());
                    double l = Double.valueOf(location.getmProgressL());
                    HashMap<String, Double> info = Coordinate.getCoordinateXY(b, l, d.getmProject());
                    tv_N.setText(info.get("n").toString());
                    tv_E.setText(info.get("e").toString());
                    tv_Z.setText(location.getmH());
                    //需要将当前点的数据传过去,当前点没有名称，因为是现在的位置
                    MyPoint point = new MyPoint("", info.get("n"), info.get("e"));
                    surveyView.setLocation(point);
                    //重绘
                    surveyView.invalidate();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        //获得表名来进行数据的操作
        d = (Data) getApplication();
        if (d.getmProject()==null){
            Toast.makeText(this,"请打开项目",Toast.LENGTH_SHORT).show();
            return;
        }
        mCurd = new Curd(d.getmProject().getmTableName(), this);
        //获得最后一个id的名称
        //初始化滑动界面的内容
        init();
        //对自定义控件中的数据进行初始化，注意:这会将数据在
        iniSurveyView();
        //初始化下面的点
        initDoc();
        if (d.getmConnectType() == Constant.BlueToothConncet) {
            Infomation.setHandler(mHandler);
        } else if (d.getmConnectType() == Constant.InnerGPSConnect) {
            ConnectActivity.setmHandler(mHandler);
        }

    }

    private void addPoint() {
        //每次添加前先获得当前末尾的id
        mid = mCurd.getLastID();
        List<ContentValues> values = new ArrayList<ContentValues>();
        ContentValues cv = new ContentValues();
        cv.put("id", mid + 1);
        cv.put("B", tv_B.getText().toString());
        cv.put("L", tv_L.getText().toString());
        cv.put("H", tv_H.getText().toString());
        cv.put("N", tv_N.getText().toString());
        cv.put("E", tv_E.getText().toString());
        cv.put("Z", tv_Z.getText().toString());
        cv.put("time",tv_time.getText().toString());
        cv.put("DireB", mDireB);
        cv.put("DireL", mDireL);
        cv.put("DES", "");
        cv.put("height", String.valueOf(d.getMheight()));
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
     */
    private void refreshPoints() {
        points.clear();
        mCursor = mCurd.queryData(new String[]{"*"}, "id desc", null);
        if (mCursor.getCount()!=0) {
            while (mCursor.moveToNext()) {
                //获取数据存入其中
                double N = Double.valueOf(mCursor.getString(mCursor.getColumnIndex("N")));
                double E = Double.valueOf(mCursor.getString(mCursor.getColumnIndex("E")));
                String name = "pt" + mCursor.getString(mCursor.getColumnIndex("id"));
                MyPoint p = new MyPoint(name, N, E);
                points.add(p);
            }
            mCursor.close();
            //这里传入的是为了方便绘制的，只包含有N,E,Z的值，其实也只需要N,E和id
            surveyView.setPoints(points);
            surveyView.invalidate();
        }
    }

    /**
     * 将ViewPaper找到，并设置其填充的内容
     */
    private void init() {
        image_add = (ImageView) findViewById(R.id.image_add);
        image_zoom_center= (ImageView) findViewById(R.id.image_center);
        image_zoom_in= (ImageView) findViewById(R.id.image_zoom_in);
        image_zoom_out= (ImageView) findViewById(R.id.image_zoom_out);
        btn_add = (Button) findViewById(R.id.btn_add_point);
        image_add.setOnClickListener(this);
        image_zoom_in.setOnClickListener(this);
        image_zoom_out.setOnClickListener(this);
        image_zoom_center.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.first_vp);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.survey_layout1, null);
        View view2 = inflater.inflate(R.layout.survey_layout2, null);
        tv_B = (TextView) view1.findViewById(R.id.tv_B);
        tv_L = (TextView) view1.findViewById(R.id.tv_L);
        tv_H = (TextView) view1.findViewById(R.id.tv_H);
        tv_time = (TextView) view1.findViewById(R.id.tv_time);
        tv_N = (TextView) view2.findViewById(R.id.tv_N);
        tv_E = (TextView) view2.findViewById(R.id.tv_E);
        tv_Z = (TextView) view2.findViewById(R.id.tv_Z);
        mViews.add(view1);
        mViews.add(view2);
        //设置填充内容，以及页面改变的监听
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

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
        dots[mCurrentIndex].setBackgroundResource(R.drawable.dian_down);
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
        dots[position].setBackgroundResource(R.drawable.dian_down);
        //之前选中位置变为没被选中的状态
        dots[mCurrentIndex].setBackgroundResource(R.drawable.dian);
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
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        Infomation.setHandler(null);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_add://直接进行数据采集
                if (d.isConnected()) {
                    addPoint();
                    refreshPoints();
                }
                break;
            case R.id.btn_add_point://将信息获取到，然后跳转到另外一个界面来,采集数据
                if (d.isConnected()) {
                    Intent intent = new Intent("com.zhd.addPoint.START");
                    intent.putExtra("B", tv_B.getText().toString());
                    intent.putExtra("L", tv_L.getText().toString());
                    intent.putExtra("H", tv_H.getText().toString());
                    intent.putExtra("N", tv_N.getText().toString());
                    intent.putExtra("E", tv_E.getText().toString());
                    intent.putExtra("Z", tv_Z.getText().toString());
                    intent.putExtra("time", tv_time.getText().toString());
                    intent.putExtra("DireB", mDireB);
                    intent.putExtra("DireL", mDireL);
                    startActivityForResult(intent, ADD_RESULT);
                }
                break;
            case R.id.image_zoom_in://传入放大比例
                surveyView.setmScale(1.5f);
                surveyView.invalidate();
                break;
            case R.id.image_zoom_out://传入缩小比例
                surveyView.setmScale(0.5f);
                surveyView.invalidate();
                break;
            case R.id.image_center://初始化使其居中
                surveyView.mOffsetx=0;
                surveyView.mOffsety=0;
                surveyView.invalidate();
                break;
        }
    }
}
