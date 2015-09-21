package com.zhd.hi_test.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by 2015032501 on 2015/9/21.
 */
public class InterceptView extends LinearLayout {
    public InterceptView(Context context) {
        super(context);
    }

    public InterceptView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //拦截对子控件的Touch事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
        //return super.onTouchEvent(event);
    }
}
