package com.zhd.switchview;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import java.util.List;


public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener{
    private ViewPager mvpNewsList;
    private RadioGroup mrgChannel;
    private HorizontalScrollView mhvChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mhvChannel= (HorizontalScrollView) findViewById(R.id.hvChannel);
        mrgChannel= (RadioGroup) findViewById(R.id.rgChannel);
        mvpNewsList= (ViewPager) findViewById(R.id.vpNewsList);
        //对RadioGroup设置监听
        mrgChannel.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mvpNewsList.setCurrentItem(checkedId);
            }
        });
        //对ViewPaper设置监听,设置的是PageChangeListener
        mvpNewsList.setOnPageChangeListener(this);
        iniTab();//初始化导航滑动条
        initViewPaper();//初始化滑动的内容页
        mrgChannel.check(0);//默认选中的radioButton对象是第一个
    }

    private void initViewPaper() {

    }

    private void iniTab() {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
