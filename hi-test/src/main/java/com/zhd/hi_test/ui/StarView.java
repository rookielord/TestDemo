package com.zhd.hi_test.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.GpsSatellite;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.zhd.hi_test.module.StarPoint;

import java.security.interfaces.ECKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/9.
 * 自定义一个控件，然后在其上面画图，
 */
public class StarView extends View {
    private static final String TAG = "StarView";

    //卫星点的类别，这个没有用，现在将卫星全部接收到再说
    //设置画笔对象
    private Paint mPaint;
    //设置需要画的点的集合,用一个listView就可以了，每次画完后清空
    //private Map<Integer,StarPoint> mPoints=new Hashtable<>();
    List<StarPoint> mPoints = new ArrayList<>();
    //画背景的圆的大小,这里是写死的需要重新弄
    private static int mRadius;
    //屏幕的宽和高
    private int mWidth;
    private int mHeight;
    //画卫星图像的大小
    private static float msRadius;
    //测试代码


    /**
     * 这里是实现将卫星对象转化成我要在图像上显示的卫星对象
     *
     * @param gpsSatellites 传输过来的卫星对象
     */
    public void SetSatetllite(List<GpsSatellite> gpsSatellites) {
        for (GpsSatellite satellite : gpsSatellites) {
            //判断定位使用的卫星
            if (satellite.usedInFix()) {
                //获得高度角
                float elevation = satellite.getElevation();
                //获得方位角
                double azimuth = satellite.getAzimuth();
                //计算当前位置距离圆心的距离,根据平分获得
                double r2 = mRadius * ((90.0f - elevation) / 90.0f);
                //以(mRadius,mRadius)为参考点，然后根据方位角位置进行判断所在象限，然后对X,Y进行修改
                //需要进行修改为平面直角坐标系的角度进行转化,转化为弧度
                double radian = degreeToRadian(360 - azimuth + 90);
                //这个就是转换坐标,就以第一象限作为参考
                double x = mRadius + Math.cos(radian) * r2;//x方向上的增量
                double y = mRadius - Math.sin(radian) * r2;//为什么是减去，这不是第一现象的做法吗
                //获得x,y方向上的变化后的值
                //获得卫星的信噪比，并分级绘制
                int snr = (int) satellite.getSnr();
                int level = snrToSignalLevel(snr);
                //卫星编号
                int num = satellite.getPrn();
                StarPoint p = new StarPoint(x - msRadius / 2, y - msRadius / 2, num, level);
                //mPoints.put(num,p);
                mPoints.add(p);
            }
        }
    }

    public StarView(Context context) {
        super(context);
    }

    //性噪比判断
    private int snrToSignalLevel(float snr) {
        int level = 0;
        if (snr >= 0 && snr < 16) {
            level = 0;
        } else if (snr >= 16 && snr < 36) {
            level = 1;
        } else if (snr >= 36) {
            level = 2;
        }
        return level;
    }

    /**
     * 这里可以获得一些自定义控件的属性，但目前还不需要
     *
     * @param context
     * @param attrs
     */
    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 绘制地图控件，在这里将传过来的卫星数据解析，然后在外面调用到invadate()来重绘
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "绘图" + mPoints.size());
        //1.绘制整个背景
        drawStarbackground(canvas);
        //2.绘制星星
        drawSatellites(canvas);
        //3.清空Listview
        mPoints.clear();
    }

    private void drawSatellites(Canvas canvas) {

        for (StarPoint point : mPoints) {
            switch (point.getmLevel()) {
                case 0:
                    //Color.parseColor("#FF34CA3C")
                    mPaint.setColor(Color.RED);
                    break;
                case 1:
                    //parseColor("#FFC6C077")
                    mPaint.setColor(Color.parseColor("#FFCDBE3F"));
                    break;
                case 2:
                    //parseColor("FF32D034")
                    mPaint.setColor(Color.GREEN);
                    break;
            }
            mPaint.setStyle(Paint.Style.FILL);
            float x = (float) point.getmX();
            float y = (float) point.getmY();
            canvas.drawCircle(x, y, (int) msRadius, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(msRadius);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText(String.valueOf(point.getmNum()), x-5 , y+5 , mPaint);
        }
    }

    private void drawStarbackground(Canvas canvas) {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        //这里绘制星空的圆形
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        //画圆圈和分割线
        int r = 0;//画圆的半径，根据高度角来获得对应的边长
        for (int i = 0; i <= 3; i++) {
            r = (int) (mRadius * Math.cos(degreeToRadian(i * 30)));//高度角分别是0,30,60,90值分别是r,1/2r……
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mRadius, mRadius, r, mPaint);
            if (i == 0) {
                mPaint.setTextSize(20);
                mPaint.setColor(Color.parseColor("#FF0044"));
                canvas.drawText("N", mRadius, mRadius - r + 10, mPaint);
            }
            canvas.drawText(String.valueOf(30 * i), mRadius, mRadius - r, mPaint);
        }
        //画线,根据极坐标画线
        drawdivideLines(canvas, 12);

    }

    private void drawdivideLines(Canvas canvas, int divideNum) {
        mPaint.setColor(Color.WHITE);
        //将360度等分化为个数，然后分别求得各个点的x,y坐标
        float[] ptr = new float[divideNum * 4];//每条线都有4个点
        double divide = 2 * Math.PI / divideNum;//每个角的弧度值
        int divideAngel=360/divideNum;
        //计算坐标点的值，通过极坐标求得
        //根据坐标角获得x,y的值，但需要考虑到不同象限的加减值,经过考虑只有y轴为相反值
        double x = 0;
        double y = 0;
        for (int i = 0; i < divideNum; i++) {
            x = mRadius+mRadius * Math.cos(divide * i);
            y = mRadius-mRadius * Math.sin(divide * i);
            ptr[4*i]=mRadius;
            ptr[4*i+1]=mRadius;
            ptr[4*i+2]=(float)x;
            ptr[4*i+3]=(float)y;
            //这里画字
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(20);
            if (i>=0&&i<=3){
                x-=15;
                y+=10;
            }
            else if(i>=4&&i<=6) {
                y+=5;
            }
            else if(i>=7&&i<=9){
                y-=5;
            }
            else {
                x-=30;
            }
            canvas.drawText(String.valueOf(divideAngel*i),(float)x,(float)y,mPaint);
        }
        mPaint.setColor(Color.WHITE);
        canvas.drawLines(ptr, mPaint);
    }


    /**
     * 这是用来设置
     * 1获取屏幕的宽和高，定义自定义控件的大小
     * 2自定义控件的宽和高，如果不设置的话只能在xml文档中写死，最好是在这里设置
     * 宽度是fill_parent,不用管,但高度却需要设置成warp_content
     *
     * @param widthMeasureSpec  自定义控件的宽度
     * @param heightMeasureSpec 自定义控件的高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getWindowValue();
        int height = measureHeight(heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureHeight(int heightMeasureSpec) {
        int HspeMode = MeasureSpec.getMode(heightMeasureSpec);
        int HspeSize = MeasureSpec.getSize(heightMeasureSpec);
        if (HspeMode == MeasureSpec.EXACTLY) {
            return HspeSize;
        } else if (HspeMode == MeasureSpec.AT_MOST) {//根据获取的屏幕宽高来设置其大小
            return mRadius * 2;
        } else {
            return 0;
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int WspeMode = MeasureSpec.getMode(widthMeasureSpec);
        int WspeSize = MeasureSpec.getSize(widthMeasureSpec);
        if (WspeMode == MeasureSpec.EXACTLY) {
            return WspeSize;
        } else if (WspeMode == MeasureSpec.AT_MOST) {
            return mRadius * 2;
        } else {
            return 0;
        }
    }

    /**
     * 从sp获得宽和高，并且根据宽和高设置画圆的半径
     */
    private void getWindowValue() {
        SharedPreferences sp = getContext().getSharedPreferences("VALUE", Context.MODE_PRIVATE);
        mWidth = sp.getInt("WIDTH", 0);
        mHeight = sp.getInt("HEIGHT", 0);
        Log.d("VALUE", mWidth + ";" + mHeight);
        if (mWidth == 0 || mHeight == 0) {
            Log.e("GPS", "屏幕参数出错");
            return;
        }
        //获得最短的长度
        int length = mWidth < mHeight ? mWidth : mHeight;
        mRadius = length / 2 - 10;
        msRadius = (float) 0.06d * mRadius;
    }

    /**
     * 弧度转换公式
     *
     * @param degree
     * @return
     */
    private double degreeToRadian(double degree) {
        return (degree * Math.PI) / 180.0d;
    }
}
