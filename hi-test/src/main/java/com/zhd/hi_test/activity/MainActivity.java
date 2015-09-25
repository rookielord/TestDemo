package com.zhd.hi_test.activity;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.MyPagerAdapter;
import com.zhd.hi_test.util.Method;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewPager.*;

/**
 * Created by 2015032501 on 2015/9/18.
 */
public class MainActivity extends Activity {


    Context context = null;//当前context,用于跳转
    LocalActivityManager manager = null;//用于管理当前显示的Activity
    //控件
    ViewPager pager = null;
    TabHost tabHost = null;
    TextView t1, t2, t3, t4;
    private long mFirsttime=0;
    private static final int INTERVAL=2000;

    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private ImageView cursor;// 动画图片
    private List<TextView> tv_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取屏幕的参数和项目文件夹路径,并传给全局变量
        Method.getWindowValue(this);
        Data d = (Data) getApplication();
        d.setmPath(Method.createDirectory(this));

        context = MainActivity.this;
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        InitImageView();
        initTextView();
        initPagerViewer();

    }

    /**
     * 初始化标题,标题添加监听
     */
    private void initTextView() {
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);
        t3 = (TextView) findViewById(R.id.text3);
        t4 = (TextView) findViewById(R.id.text4);
        t1.setBackgroundColor(Color.BLUE);
        tv_list = new ArrayList<>();
        tv_list.add(t1);
        tv_list.add(t2);
        tv_list.add(t3);
        tv_list.add(t4);
        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
        t3.setOnClickListener(new MyOnClickListener(2));
        t4.setOnClickListener(new MyOnClickListener(4));
    }

    /**
     * 初始化PageViewer
     * 用跳转意图来获取View,然后向这里的Intent传入PageID,方便GridView进行填充其内容；
     */
    private void initPagerViewer() {
        pager = (ViewPager) findViewById(R.id.viewpage);
        final ArrayList<View> list = new ArrayList<View>();
        Intent intent = new Intent(context, GridActivity.class);
        intent.putExtra("PageNum", 1);
        list.add(getView("A", intent));

        Intent intent2 = new Intent(context, GridActivity.class);
        intent2.putExtra("PageNum", 2);
        list.add(getView("B", intent2));

        Intent intent3 = new Intent(context, GridActivity.class);
        intent3.putExtra("PageNum", 3);
        list.add(getView("C", intent3));

        Intent intent4 = new Intent(context, GridActivity.class);
        intent4.putExtra("PageNum", 4);
        list.add(getView("D", intent4));

        pager.setAdapter(new MyPagerAdapter(list));
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 初始化动画
     * 1.找到图片控件2.找到图片资源3.
     */
    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.roller)
                .getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 4 - bmpW) / 2;// 计算偏移量,
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
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

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;// 页卡1 -> 页卡3 偏移量
        int three = one * 3;//页面1->页面4偏移量

        @Override
        public void onPageSelected(int arg0) {
            setTextBackgroundColor();
            Animation animation = null;

            switch (arg0) {
                case 0://选择了第一页
                    if (currIndex == 1) {//跳转到第二页
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {//跳转第三页
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    } else if (currIndex == 3) {
                        animation = new TranslateAnimation(three, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    } else if (currIndex == 3) {
                        animation = new TranslateAnimation(three, one, 0, 0);
                    }
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    } else if (currIndex == 3) {
                        animation = new TranslateAnimation(three, two, 0, 0);
                    }
                    break;
                case 3:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, three, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, three, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, three, 0, 0);
                    }
                    break;
            }
            //设置当前页面选中编号，在选中编号中进行
            currIndex = arg0;
            tv_list.get(arg0).setBackgroundColor(Color.BLUE);
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            setTextBackgroundColor();
            v.setBackgroundColor(Color.BLUE);
            pager.setCurrentItem(index);
        }
    }
    private void setTextBackgroundColor() {
        for (TextView tv : tv_list) {
            tv.setBackgroundColor(Color.parseColor("#ffa6a8f8"));
        }
    }
    /**
     * 点两次退出，计算两次的点击的时间
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis()-mFirsttime>INTERVAL)
        {
            mFirsttime=System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        }else {
            //关闭蓝牙
            BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
            if (adapter.isEnabled())
                adapter.disable();
            finish();
        }
    }
}
