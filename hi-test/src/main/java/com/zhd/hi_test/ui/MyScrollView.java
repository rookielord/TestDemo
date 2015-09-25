package com.zhd.hi_test.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.zhd.hi_test.activity.ManageActivity;




/**
 * 自定义的 滚动控件
 * 重载了 onScrollChanged（滚动条变化）,监听每次的变化通知给
 * 可使用 AddOnScrollChangedListener
 */
public class MyScrollView extends HorizontalScrollView {

    //创建一个这个控件相关联的Activity
    private ManageActivity activity;

    //将当前Context,转化为ManageActivity,
    public MyScrollView(Context context) {
        super(context);
        activity = (ManageActivity) context;
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (ManageActivity) context;
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (ManageActivity) context;
    }

    /**
     * 点击时候触发
     * 把当前activity的HorizontalScrollview的控件对象设置为
     * 当前控件（即可以在activity中操作自定义控件)是否等于findviewbyID找到自定义控件?
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //进行触摸赋值
        activity.mTouchView = this;
        return super.onTouchEvent(ev);
    }

    /**
     * 当滑动的时候触发，如果
     *
     * @param l 现在的left,距离控件左侧的距离
     * @param t 现在的top,距离控件顶端的距离
     * @param oldl 以前的……
     * @param oldt 以前的……
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (activity.mTouchView == this) {//如果自定义控件被滑动时
            activity.onScrollChanged(l, t, oldl, oldt);
        } else {
            super.onScrollChanged(l, t, oldl, oldt);
        }
    }
}
