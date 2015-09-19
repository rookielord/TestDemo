package com.zhd.hi_test.adapter;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/19.
 */
public class MyPagerAdapter extends PagerAdapter {

    List<View> list = new ArrayList<View>();

    public MyPagerAdapter(ArrayList<View> list) {
        this.list = list;
    }

    /**
     * 销毁其中添加进Viewpager的View对象
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        ViewPager pViewPager = ((ViewPager) container);//转化为ViewPaper来进行管理
        pViewPager.removeView(list.get(position));//移除选择小鬼的View对象
    }

    /**
     * 必定重写方法重写方法
     * @param arg0 现在在界面上显示的Activity的View对象
     * @param arg1 从instantiateItem()返回获取的Object对象
     * @return
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    /**
     * 必定重写的方法，返回其中的数量，
     * @return
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * 在这里实现Viewpager中View的添加(即Activity的添加)
     *
     * @param arg0 从View对象就是Viewpager
     * @param arg1 当前选中的排序进入的顺序ID,然后进行视图View即Activity的添加
     * @return
     */
    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ViewPager pViewPager = ((ViewPager) arg0);
        pViewPager.addView(list.get(arg1));
        return list.get(arg1);
    }

}
