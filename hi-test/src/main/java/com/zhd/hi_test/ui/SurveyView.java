package com.zhd.hi_test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.DrawPoint;
import com.zhd.hi_test.module.MyPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/22.
 * 这是位置数据显示到自定义View的图上
 */
public class SurveyView extends View {

    //控件的属性
    private int mHeight;
    private int mWidth;
    private Context mContext;
    //需要画的点集合
    List<DrawPoint> drawPoints = new ArrayList<DrawPoint>();
    //作为基准的点的N,E坐标
    private static double mN;
    private static double mE;


    public SurveyView(Context context) {
        super(context);
        mContext = context;
    }

    public SurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SurveyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    /**
     * 将点集合传过来
     * 点击添加的时候传入
     * 如果当前点击，
     * @param points
     */
    public void setPoints(List<MyPoint> points) {
        if (points!=null){//获得参考点
            MyPoint point=points.get(0);
            setCenterValue(point.getmN(),point.getmE());
        }
        //将

    }

    public void setLocation(MyPoint point){

    }

    /**
     * 设置参考点的坐标
     * @param N
     * @param E
     */
    private void setCenterValue(double N, double E) {
        mN = N;
        mE = E;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这里获得屏幕的宽和高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = measureWidth(widthMeasureSpec);
        mHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    private int measureHeight(int heightMeasureSpec) {
        int HspeMode = MeasureSpec.getMode(heightMeasureSpec);
        int HspeSize = MeasureSpec.getSize(heightMeasureSpec);
        if (HspeMode == MeasureSpec.EXACTLY) {
            return HspeSize;
        } else if (HspeMode == MeasureSpec.AT_MOST) {
            return HspeSize;
        } else {
            return HspeSize;
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int WspeMode = MeasureSpec.getMode(widthMeasureSpec);
        int WspeSize = MeasureSpec.getSize(widthMeasureSpec);
        if (WspeMode == MeasureSpec.EXACTLY) {//具体的宽和高
            return WspeSize;
        } else if (WspeMode == MeasureSpec.AT_MOST) {//wrap_content
            return WspeSize;
        } else {
            return WspeSize;//match_parent
        }
    }

    /**
     * 1.没有已知点：显示用户的当前位置
     * 2.有已知点：显示用户当前位置和已知点
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
