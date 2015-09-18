package com.zhd.hi_test.db;

import com.zhd.hi_test.R;
import com.zhd.hi_test.fragment.DataFragment;
import com.zhd.hi_test.fragment.PointFragment;
import com.zhd.hi_test.fragment.ProjectFragment;
import com.zhd.hi_test.fragment.StarViewFragment;
import com.zhd.hi_test.module.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/14.
 * 用来存放所有FragmentTab的信息的内容，都是静态字段
 */
public class TabDb {
    //文字类型
    public static String[] getTabsTxt(){
        String[] tabs={"项目管理","星空图","点位管理","数据传输"};
        return tabs;
    }
    //一般图像
    public static int[] getTabsImg(){
        int[] ids={R.drawable.foot_news_normal,R.drawable.foot_read_normal,R.drawable.foot_vdio_normal,R.drawable.foot_fond_normal};
        return ids;
    }
    //选中图像资源
    public static int[] getTabsImgLight(){
        int[] ids={R.drawable.foot_news_light,R.drawable.foot_read_light,R.drawable.foot_vdio_light,R.drawable.foot_found_light};
        return ids;
    }
    //所有的Fragments类
    public static Class[] getFragments(){
        Class[] clz={ProjectFragment.class,StarViewFragment.class,PointFragment.class,DataFragment.class};
        return clz;
    }
    /**
     * 设置Channel中的内容，设置为静态字段，然后使用static块，向里面添加内容
     * 之后再写一个静态方法放回其值
     * 注意：因为是静态方法，所以会在使用该类的时候被调用static内的东西
     */
    private static List<Channel> channelList=new ArrayList<Channel>();
    static {
        channelList.add(new Channel(1, "头条", 0, ""));
        channelList.add(new Channel(2,"娱乐",0,""));
        channelList.add(new Channel(3,"体育",0,""));
        channelList.add(new Channel(4,"财经",0,""));
        channelList.add(new Channel(5,"热点",0,""));
        channelList.add(new Channel(6,"科技",0,""));
        channelList.add(new Channel(7,"图片",0,""));
        channelList.add(new Channel(8,"汽车",0,""));
        channelList.add(new Channel(9,"时尚",0,""));
    }
    public static  List<Channel> getSelectedChannel(){
        return channelList;
    }
}
