package com.zhd.hi_test.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义的 滚动控件
 * 重载了 onScrollChanged（滚动条变化）,监听每次的变化通知给 观察(此变化的)观察者
 * 可使用 AddOnScrollChangedListener 来订阅本控件的 滚动条变化
 */
public class MyScrollView extends HorizontalScrollView {

    private static final String TAG = "LIJIAJI";
    private ScrollViewObserver mScrollViewObserver = new ScrollViewObserver();

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param l    现在的水平位置
     * @param t    现在的竖直位置
     * @param oldl 以前的
     * @param oldt 以前的
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mScrollViewObserver != null) {
            mScrollViewObserver.NotifyOnScrollChanged(l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        android.util.Log.d(TAG, "触摸事件");
        return super.onTouchEvent(ev);
    }

    /**
     * 订阅 本控件 的 滚动条变化事件
     */
    public void AddOnScrollChangedListener(OnScrollChangedListener listener) {
        mScrollViewObserver.AddOnScrollChangedListener(listener);
    }

    /**
     * 取消 订阅 本控件 的 滚动条变化事件
     */
    public void RemoveOnScrollChangedListener(OnScrollChangedListener listener) {
        mScrollViewObserver.RemoveOnScrollChangedListener(listener);
    }

    /**
     * 当发生了滚动事件时
     */
    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }


    /**
     * 观察者
     * 对每个控件都产生监听吗？
     */
    public static class ScrollViewObserver {
        List<OnScrollChangedListener> mList;

        public ScrollViewObserver() {
            super();
            mList = new ArrayList<OnScrollChangedListener>();
        }

        public void AddOnScrollChangedListener(OnScrollChangedListener listener) {
            mList.add(listener);
        }

        public void RemoveOnScrollChangedListener(
                OnScrollChangedListener listener) {
            mList.remove(listener);
        }

        public void NotifyOnScrollChanged(int l, int t, int oldl, int oldt) {
            if (mList == null || mList.size() == 0) {
                return;
            }
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i) != null) {
                    mList.get(i).onScrollChanged(l, t, oldl, oldt);
                }
            }
        }
    }

}
