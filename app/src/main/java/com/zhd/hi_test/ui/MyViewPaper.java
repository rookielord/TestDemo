package com.zhd.hi_test.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 2015032501 on 2015/10/27.
 */
public class MyViewPaper extends ViewPager {

    //静止平移
    private boolean onScroll = false;

    public void setOnScroll(boolean onScroll) {
        this.onScroll = onScroll;
    }

    public MyViewPaper(Context context) {
        super(context);
    }

    public MyViewPaper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onScroll)
            return super.onTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (onScroll)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }


    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

}
