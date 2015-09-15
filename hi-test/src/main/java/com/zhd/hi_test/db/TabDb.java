package com.zhd.hi_test.db;

import com.zhd.hi_test.R;
import com.zhd.hi_test.fragment.DataFragment;
import com.zhd.hi_test.fragment.PointFragment;
import com.zhd.hi_test.fragment.ProjectFragment;
import com.zhd.hi_test.fragment.StarViewFragment;

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
}
