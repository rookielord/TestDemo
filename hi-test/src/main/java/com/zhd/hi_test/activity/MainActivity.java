package com.zhd.hi_test.activity;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.MyPagerAdapter;
import com.zhd.hi_test.interfaces.IConnect;
import com.zhd.hi_test.module.InnerGPSConnect;
import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.MyProject;
import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.util.FileUtil;
import com.zhd.hi_test.util.Infomation;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewPager.*;

/**
 * Created by 2015032501 on 2015/9/18.
 * 这里是主界面，包含下面的滑动窗口和ViewTable。
 * ViewTable里面存放的是4个activity
 * 最先创建的就是这个页面。
 * <p/>
 * 考虑到需要获取的信息，需要设置handler来获取location
 */
public class MainActivity extends Activity {


    Context context = null;//当前context,用于跳转
    LocalActivityManager manager = null;//用于管理当前显示的Activity
    //控件
    ViewPager pager = null;
    TextView t1, t2, t3;
    //include上面的内容
    TextView tv_name, tv_sate, tv_use_sate, tv_result, tv_age_time, tv_connect, tv_PDOP;
    ImageView image_solution;
    List<TextView> tv_list;
    LinearLayout ll_satellite;
    //连点两次退出
    private long mFirsttime = 0;
    private static final int INTERVAL = 2000;

    //在其它地方退出后，会将对应中清除，所以在bluetooth中是没有handler中
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MyLocation myLocation = (MyLocation) msg.obj;
                    //根据解类型来设置对应的图片
                    tv_result.setText(myLocation.getmQuality());
                    tv_use_sate.setText(myLocation.getmUseSate());
                    tv_age_time.setText(myLocation.getmAge());
                    if (!Const.HasPDOP)
                        tv_PDOP.setText("0.0");
                    setSolutionImage(myLocation.getmQuality());
                    break;
                case 2:
                    List<Satellite> satellites = (List<Satellite>) msg.obj;
                    tv_sate.setText(String.valueOf(satellites.size()));
                    break;
                case 4:
                    Const.HasPDOP = true;
                    tv_PDOP.setText(msg.obj.toString());
                    break;
            }
            return false;
        }
    });

    private void setSolutionImage(String s) {
        switch (s) {
            case "无解":
                image_solution.setImageResource(R.mipmap.ic_solution_none);
                break;
            case "GPS固定解":
                image_solution.setImageResource(R.mipmap.ic_solution_fix);
                break;
            case "不同GPS固定解":
                image_solution.setImageResource(R.mipmap.ic_solution_rtd);
                break;
            case "实时差分固定解":
                image_solution.setImageResource(R.mipmap.ic_solution_rtd);
                break;
            case "实时差分浮动解":
                image_solution.setImageResource(R.mipmap.ic_solution_rtkf);
                break;
            case "GPS单点定位":
                image_solution.setImageResource(R.mipmap.ic_android);
                break;
        }
    }

    //动画跳转
    private int currIndex = 0;// 当前页卡编号

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置全局变量，创建project文件夹，并且创建项目
        FileUtil.createDirectory();
        //创建默认文件夹并选中
        FileUtil.createDefaultProject(this);
        //获取上一次打开的项目,将其设置为全局变量
        FileUtil.setLastProject(this);
        context = MainActivity.this;
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        initTextView();
        initPagerViewer();
        initInfo();
    }

    /**
     * 无法获取解状态等信息，即iniInfo只有在加载的时候才会被调用，而Actiivity一直在被加载中
     */
    private void initInfo() {
        if (Const.getmProject() != null) {
            String name = setProjectName(Const.getmProject().getmName());
            tv_name.setText(name);
        } else {
            tv_name.setText("未选择项目");
        }
        if (Const.IsConnected) {//只有当前是连接状态才发送hanlder
            if (Const.getmConnectType() == Const.BlueToothConncet) {
                Infomation.setHandler(mHandler);
                tv_connect.setText("蓝牙连接");
            } else {
                InnerGPSConnect.setmHandler(mHandler);
                tv_connect.setText("内置GPS连接");
            }
        } else {
            tv_result.setText("无解");
            tv_connect.setText("仪器未连接");
            image_solution.setImageResource(R.mipmap.ic_solution_none);
            tv_age_time.setText("0.0");
            tv_use_sate.setText("0");
            tv_sate.setText("0");
            tv_PDOP.setText("0.0");
        }
    }

    private String setProjectName(String name) {
        if (name.length() > 10) {
            return name.substring(0, 5) + "..";
        }
        return name;
    }

    /**
     * 初始化标题,标题添加监听
     */
    private void initTextView() {
        tv_name = (TextView) findViewById(R.id.tv_pro_name);
        tv_result = (TextView) findViewById(R.id.tv_result);
        tv_sate = (TextView) findViewById(R.id.tv_sate);
        tv_use_sate = (TextView) findViewById(R.id.tv_use_sate);
        tv_age_time = (TextView) findViewById(R.id.tv_age_time);
        tv_connect = (TextView) findViewById(R.id.tv_connect);
        tv_PDOP = (TextView) findViewById(R.id.tv_PDOP);
        image_solution = (ImageView) findViewById(R.id.image_solution);
        ll_satellite = (LinearLayout) findViewById(R.id.ll_satellite);
        ll_satellite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GPSActivity.class);
                startActivity(intent);
            }
        });
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);
        t3 = (TextView) findViewById(R.id.text3);
        tv_list = new ArrayList<>();
        tv_list.add(t1);
        tv_list.add(t2);
        tv_list.add(t3);
        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
        t3.setOnClickListener(new MyOnClickListener(2));
    }

    /**
     * 初始化PageViewer
     * 用跳转意图来获取View,然后向这里的Intent传入PageID,方便GridView进行填充其内容；
     */
    private void initPagerViewer() {
        pager = (ViewPager) findViewById(R.id.viewpage);
        final ArrayList<View> list = new ArrayList<>();
        Intent intent = new Intent(context, GridActivity.class);
        intent.putExtra("PageNum", 1);
        list.add(getView("A", intent));

        Intent intent2 = new Intent(context, GridActivity.class);
        intent2.putExtra("PageNum", 2);
        list.add(getView("B", intent2));

        Intent intent3 = new Intent(context, GridActivity.class);
        intent3.putExtra("PageNum", 3);
        list.add(getView("C", intent3));

        pager.setAdapter(new MyPagerAdapter(list));
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 通过activity获取视图
     *
     * @param id
     * @param intent
     * @return
     */
    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            setTextBackgroundColor();
            //设置当前页面选中编号，在选中编号中进行
            currIndex = arg0;
            tv_list.get(currIndex).setBackgroundColor(Color.parseColor("#FF35E6F6"));
            tv_list.get(currIndex).setTextColor(Color.BLACK);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    /**
     * 点击更改paper中的值
     */
    public class MyOnClickListener implements OnClickListener {
        private int index = 0;
        public MyOnClickListener(int i) {
            index = i;
        }
        @Override
        public void onClick(View v) {
            setTextBackgroundColor();
            pager.setCurrentItem(index);
        }
    }

    /**
     * 先全部设置一次默认颜色
     */
    private void setTextBackgroundColor() {
        for (TextView tv : tv_list) {
            tv.setBackgroundColor(Color.parseColor("#ff2aa5b5"));
            tv.setTextColor(Color.parseColor("#ffffffff"));
        }
    }

    /**
     * 点两次退出，计算两次的点击的时间
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mFirsttime > INTERVAL) {
            mFirsttime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    /**
     * 可用
     */
    @Override
    protected void onResume() {
        super.onResume();
        initInfo();
    }

    /**
     * 1.当程序退出时，将当前项目的打开时间进行跟新
     * 2.将当前项目对象写入一个文件中，表示上次打开的内容
     * 3.重新写入object对象
     * 4.清空连接状态
     */
    @Override
    protected void onDestroy() {
        MyProject p = Const.getmProject();
        if (p != null) {
            FileUtil.updateProject(p);
            FileUtil.savelastProject(p, this);
        }
        //清空所有的情况
        clearAll();
        super.onDestroy();
    }

    /**
     * 1.清空连接信息
     * 2.清空当前打开的项目
     * 3.断开连接
     */
    private void clearAll() {
        Const.setmInfo(null);
        Const.setmProject(null);
        IConnect connect = Const.getmConnect();
        if (connect != null)
            connect.breakConnect();
    }
}
