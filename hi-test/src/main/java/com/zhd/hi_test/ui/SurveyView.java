package com.zhd.hi_test.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 2015032501 on 2015/9/22.
 * 这是位置数据显示到自定义View的图上
 */
public class SurveyView extends View{

    private int mHeight;
    private int mWidth;
    public SurveyView(Context context) {
        super(context);
    }

    public SurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurveyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这里获得屏幕的宽和高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
