package com.zhd.hi_test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 2015032501 on 2015/10/19.
 */
public class CompassView extends View {

    //定义一个Path对象以用于绘图
    private Paint mPaint = new Paint();

    public CompassView(Context context) {
        super(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float mDegree;

    public void setmDegree(float degree) {
        this.mDegree = degree;
    }

    /**
     * 画两个三角形大小一致，指向相反，
     *
     * @param mCanvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //得到画布的宽度和高度
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        //获得最短的边，用于保证图像整个都在其中
        int length = h < w ? h : w;
        //将坐标系平移到画布中央
        int cx = w / 2;
        int cy = h / 2;
        Path path1 = new Path();
        path1.moveTo(cx, cy - length / 2);
        path1.lineTo(cx - length / 5, cy);
        path1.lineTo(cx + length / 5, cy);
        path1.close();
        Path path2 = new Path();
        path2.moveTo(cx, cy + length / 2);
        path2.lineTo(cx - length / 5, cy);
        path2.lineTo(cx + length / 5, cy);
        path2.close();
        //设置画笔消除锯齿
        mPaint.setAntiAlias(true);
        //设置笔刷颜色为黑色
        //图像反转
        Matrix mat = new Matrix();
        mat.setTranslate(15, 100);
        mat.preRotate(-mDegree, 145, 145);
        //绘制Path
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(path1, mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawPath(path2, mPaint);
    }
}
