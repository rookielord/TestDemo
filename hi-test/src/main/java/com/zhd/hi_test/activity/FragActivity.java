package com.zhd.hi_test.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;

import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.FragmentAdapter;
import com.zhd.hi_test.db.TabDb;
import com.zhd.hi_test.fragment.NewsFragment;
import com.zhd.hi_test.module.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/14.
 */
public class FragActivity extends FragmentActivity implements OnPageChangeListener{
    private ViewPager vp_content;
    private RadioGroup rg_channel;
    private HorizontalScrollView hsv_nav;

    private List<Fragment> mFragmentList=new ArrayList<>();
    private FragmentAdapter mAdapter;
    private ViewPager mViewPaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        iniView();
    }

    /**
     * 找到对应的控件以及调用初始化ViewPaper和iniTab
     */
    private void iniView() {
        vp_content= (ViewPager) findViewById(R.id.vp_content);
        rg_channel= (RadioGroup) findViewById(R.id.rg_channel);
        hsv_nav= (HorizontalScrollView) findViewById(R.id.hsc_title);
//        rg_channel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                vp_content.setCurrentItem(checkedId);
//            }
//        });
        //对界面改变设置监听
        //vp_content.setOnPageChangeListener(this);
        //初始化上方的导航条
        //iniTab();
        //初始化ViewPaper
        //iniViewPaper();
        //默认选中的导航条
       // rg_channel.check(0);
    }

    private void iniViewPaper() {
        List<Channel> channelList=TabDb.getSelectedChannel();
        for(int i=0;i<channelList.size();i++){
            NewsFragment frag=new NewsFragment();
            Bundle bundle=new Bundle();
            bundle.putString("weburl", channelList.get(i).getmWebUrl());
            bundle.putString("name", channelList.get(i).getmName());
            frag.setArguments(bundle);
            mFragmentList.add(frag);
        }
        mAdapter=new FragmentAdapter(this.getSupportFragmentManager(),mFragmentList);
        mViewPaper.setAdapter(mAdapter);
    }

    private void iniTab() {
        List<Channel> channelList=TabDb.getSelectedChannel();
        for(int i=0;i<channelList.size();i++){
            RadioButton rb=(RadioButton)LayoutInflater.from(this).
                    inflate(R.layout.tab_rb, null);
            rb.setId(i);
            rb.setText(channelList.get(i).getmName());
            RadioGroup.LayoutParams params=new
                    RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            rg_channel.addView(rb, params);
        }
    }

    private View getTabView(int idx){
        //将布局文件金信通填充成View对象
        View view= LayoutInflater.from(this).inflate(R.layout.footer_tab,null);
        //直接转化成TextView进行设置
        ((TextView)view.findViewById(R.id.tvTab)).setText(TabDb.getTabsTxt()[idx]);
        if(idx==0){
            //如果是默认没有选择的话，默认进行选择
            ((TextView)view.findViewById(R.id.tvTab)).setTextColor(Color.RED);
            ((ImageView)view.findViewById(R.id.ivImg)).setImageResource(TabDb.getTabsImgLight()[idx]);
        }else{
            //如果idx不为0的话，则将其它的设置没有选中
            ((ImageView)view.findViewById(R.id.ivImg)).setImageResource(TabDb.getTabsImg()[idx]);
        }
        return view;
    }

    /**
     * 滑动ViewPager时调整ScroollView的位置以便显示按钮
     * @param id
     */
    private void setTab(int id){
        RadioButton rb=(RadioButton)rg_channel.getChildAt(id);
        rb.setChecked(true);
        int left=rb.getLeft();
        int width=rb.getMeasuredWidth();
        DisplayMetrics metrics=new DisplayMetrics();
        super.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth=metrics.widthPixels;
        int len=left+width/2-screenWidth/2;
        hsv_nav.smoothScrollTo(len, 0);//滑动ScroollView
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
