package com.zhd.hi_test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 2015032501 on 2015/10/27.
 */
public class LegendView extends View {
    private int mHeight;
    private int mWidth;
    private Paint mPaint = new Paint();
    int width = 20;
    int height = 5;
    int space = 10;
    //竖直方向画图，x方向的值是不变的
    int start_x = 5;
    int end_x = 5 + width;
    int start_y = 5;
    int end_y = 5 + height;

    public LegendView(Context context) {
        super(context);
    }

    public LegendView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LegendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeight = measureHeight(heightMeasureSpec);
        mWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
    }

    private int measureHeight(int heightMeasureSpec) {
        int HspeMode = MeasureSpec.getMode(heightMeasureSpec);
        int HspeSize = MeasureSpec.getSize(heightMeasureSpec);
        if (HspeMode == MeasureSpec.EXACTLY) {
            return HspeSize;
        } else if (HspeMode == MeasureSpec.AT_MOST) {
            return 100;
        } else {
            return HspeSize;
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int WspeMode = MeasureSpec.getMode(widthMeasureSpec);
        int WspeSize = MeasureSpec.getSize(widthMeasureSpec);
        if (WspeMode == MeasureSpec.EXACTLY) {
            return WspeSize;
        } else if (WspeMode == MeasureSpec.AT_MOST) {
            return 200;
        } else {
            return WspeSize;
        }
    }

    /**
     * switch (point.getmLevel()) {
     * case 0:
     * mPaint.setColor(Color.RED);
     * break;
     * case 1:
     * mPaint.setColor(Color.parseColor("#FFF1745F"));
     * break;
     * case 2:
     * mPaint.setColor(Color.parseColor("#FFF0C17C"));
     * break;
     * case 3:
     * mPaint.setColor(Color.parseColor("#FFC4FFB9"));
     * break;
     * case 4:
     * mPaint.setColor(Color.GREEN);
     * break;
     * <p/>
     * int level = 0;
     * if (snr >= 0 && snr < 20) {
     * level = 0;
     * } else if (snr >= 20 && snr < 40) {
     * level = 1;
     * } else if (snr >= 40 && snr < 60) {
     * level = 2;
     * } else if (snr >= 60 && snr < 80) {
     * level = 3;
     * } else if (snr >= 80) {
     * level = 4;
     * }
     *
     * @param canvas
     */

    @Override
    protected void onDraw(Canvas canvas) {
//        mHeight = canvas.getHeight();
//        mWidth = canvas.getWidth();
//        0
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        canvas.drawRect(start_x, start_y, end_x, end_y, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText("0-20", end_x, end_y, mPaint);
//        1
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FFE4D668"));
        canvas.drawRect(start_x, start_y += height + space, end_x, end_y += space + height, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText("20-36", end_x, end_y, mPaint);
//        2
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FF99C867"));
        canvas.drawRect(start_x, start_y += height + space, end_x, end_y += space + height, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText("36-60", end_x, end_y, mPaint);
//        3
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FFC4FFB9"));
        canvas.drawRect(start_x, start_y += height + space, end_x, end_y += space + height, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText("60-80", end_x, end_y, mPaint);
//        4
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(start_x, start_y += height + space, end_x, end_y += space + height, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText(">80", end_x, end_y, mPaint);

        //每次画完需要将其start_x和start_y设为初始值,不然会重复增加
         start_x = 5;
         end_x = 5 + width;
         start_y = 5;
         end_y = 5 + height;
    }
}
