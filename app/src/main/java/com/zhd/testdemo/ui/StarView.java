package com.zhd.testdemo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zhd.testdemo.R;

import static android.graphics.Paint.*;

/**
 * Created by 2015032501 on 2015/9/9.
 */
public class StarView extends View {

    private static final int DEFAULT_COLOR=0XFF00FF00;
    private static final float DEFAULT_SIZE=14;
    Paint mPaint;
    public StarView(Context context) {
        super(context);
    }

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint=new Paint();
        //这里获取自定义控件中的属性
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.StarView);
        //分别获取自定义控件的配置值
        int color=array.getColor(R.styleable.StarView_TextColor,DEFAULT_COLOR);
        float size=array.getDimension(R.styleable.StarView_TextSize,DEFAULT_SIZE);
        //配置画笔,属性信息是从xml文件中获得的
        mPaint.setTextSize(size);
        mPaint.setColor(color);
        //调用recycle使其下次调用控件的时候不会受影响
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置填充
        mPaint.setStyle(Style.FILL);
        //画图
        canvas.drawRect(10,10,200,200,mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawText("我是被画出阿里的",10,120,mPaint);
    }
}
