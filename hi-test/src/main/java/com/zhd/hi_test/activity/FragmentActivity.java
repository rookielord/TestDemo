package com.zhd.hi_test.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;


import com.zhd.hi_test.R;
import com.zhd.hi_test.db.TabDb;

/**
 * Created by 2015032501 on 2015/9/14.
 */
public class FragmentActivity extends Activity implements OnTabChangeListener{

    FragmentTabHost mTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_main);
        //找到tabhost对应安卓系统的
        mTabHost= (FragmentTabHost) findViewById(android.R.id.tabhost);
        //mTabHost.setup(this,getFragmentManager(),R.id.content_layout);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(this);
        initTab();
    }

    private void initTab() {
        String tabs[]=TabDb.getTabsTxt();
        for(int i=0;i<tabs.length;i++){
            TabHost.TabSpec tabSpec=mTabHost.newTabSpec(tabs[i]).setIndicator(getTabView(i));
            //进行添加，对应控件的添加
            mTabHost.addTab(tabSpec, TabDb.getFragments()[i],null);
            //设置对应tab对应的tab
            mTabHost.setTag(i);
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

    @Override
    public void onTabChanged(String tabId) {
        updateTab();

    }

    private void updateTab() {
        TabWidget tabw=mTabHost.getTabWidget();
        //遍历其中所有的元素
        for(int i=0;i<tabw.getChildCount();i++){
            //获得view
            View view=tabw.getChildAt(i);
            ImageView iv=(ImageView)view.findViewById(R.id.ivImg);
            //如果当前选中的Tab等于i
            if(i==mTabHost.getCurrentTab()){
                ((TextView)view.findViewById(R.id.tvTab)).setTextColor(Color.RED);
                iv.setImageResource(TabDb.getTabsImgLight()[i]);
            }else{
                ((TextView)view.findViewById(R.id.tvTab)).setTextColor(getResources().getColor(Color.GRAY));
                iv.setImageResource(TabDb.getTabsImg()[i]);
            }

        }
    }
}
