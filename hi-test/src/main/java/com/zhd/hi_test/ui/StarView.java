package com.zhd.hi_test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.location.GpsSatellite;
import android.util.AttributeSet;
import android.view.View;


import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.module.StarPoint;
import com.zhd.hi_test.util.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/9.
 * 自定义一个控件，然后在其上面画图，
 */
public class StarView extends View {
    //设置画笔对象
    private Paint mPaint = new Paint();
    //设置需要画的点的集合,用一个listView就可以了，每次画完后清空
    List<StarPoint> mPoints = new ArrayList<>();
    //画背景的圆的大小,这里是写死的需要重新弄
    private static int mRadius;
    //屏幕的宽和高
    private int mWidth;
    private int mHeight;
    //画卫星图像的大小
    private static float msRadius;
    //画卫星图的中心位置
    private int mX;
    private int mY;

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StarView(Context context) {
        super(context);
    }

    public StarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 传入的卫星的信息数据，转化为屏幕上坐标点的数据
     *
     * @param Satellites 传入的卫星数据，因为分为内置GPS和IRTK传过来的自定义类型的卫星
     * @param infoType   传入卫星数据的种类，1:内置GPS的数据，2:IRTK自定义类的数据
     */
    public void SetSatetllite(List<? extends Object> Satellites, int infoType) {
        //如果是内置GPS的数据类型
        if (infoType == 1) {
            for (Object sa : Satellites) {
                //转化对象
                GpsSatellite satellite = (GpsSatellite) sa;
                //获得高度角
                float elevation = satellite.getElevation();
                //获得方位角
                float azimuth = satellite.getAzimuth();
                //计算当前位置距离圆心的距离,根据平分获得
                float r2 = mRadius * ((90.0f - elevation) / 90.0f);
                //以(mRadius,mRadius)为参考点，然后根据方位角位置进行判断所在象限，然后对X,Y进行修改
                //需要进行修改为平面直角坐标系的角度进行转化,转化为弧度
                double radian = Coordinate.degreeToRadian(360 - azimuth + 90);
                //这个就是转换坐标,就以第一象限作为参考
                double x = mX + Math.cos(radian) * r2;//x方向上的增量
                double y = mY - Math.sin(radian) * r2;//为什么是减去，这不是第一现象的做法吗
                //获得x,y方向上的变化后的值
                //获得卫星的信噪比，并分级绘制
                int snr = (int) satellite.getSnr();
                int level = snrToSignalLevel(snr);
                //卫星编号
                int prn = satellite.getPrn();
                //卫星种类
                int type = getSatelliteType(prn);
                StarPoint p = new StarPoint(x - msRadius / 2, y - msRadius / 2, prn, level, type);
                mPoints.add(p);
            }
        } else if (infoType == 2) {//从IRTK那里传来的数据
            for (Object sa : Satellites) {
                //转化对象
                Satellite satellite = (Satellite) sa;
                //获得高度角
                float elevation = Float.valueOf(satellite.getmElevation());
                //获得方位角
                float azimuth = Float.valueOf(satellite.getmAzimuth());
                //计算当前位置距离圆心的距离,根据平分获得
                double r2 = mRadius * ((90.0f - elevation) / 90.0f);
                //以(mRadius,mRadius)为参考点，然后根据方位角位置进行判断所在象限，然后对X,Y进行修改
                //需要进行修改为平面直角坐标系的角度进行转化,转化为弧度
                double radian = Coordinate.degreeToRadian(360 - azimuth + 90);
                //这个就是转换坐标,就以第一象限作为参考
                double x = mX + Math.cos(radian) * r2;//x方向上的增量
                double y = mY - Math.sin(radian) * r2;//为什么是减去，这不是第一现象的做法吗
                //获得x,y方向上的变化后的值
                //获得卫星的信噪比，并分级绘制
                int snr = Integer.valueOf(satellite.getmSnr());
                int level = snrToSignalLevel(snr);
                //卫星编号
                int prn = Integer.valueOf(satellite.getmPrn());
                //这里获得卫星的种类
                int type = satellite.getmType();
                StarPoint p = new StarPoint(x - msRadius / 2, y - msRadius / 2, prn, level, type);
                mPoints.add(p);
            }
        }
    }

    private int getSatelliteType(int prn) {
        int type = -1;
        if (prn >= 1 && prn < 33) {
            type = Satellite.GPS;
        } else if (prn >= 120 && prn < 152) {
            type = Satellite.SBAS;
        } else if (prn >= 65 && prn < 97) {
            type = Satellite.GLONASS;
        } else if (prn >= 161) {
            type = Satellite.BD;
        }
        return type;
    }

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


    @Override
    protected void onDraw(Canvas canvas) {
        getWindowValue();
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
                    mPaint.setColor(Color.RED);
                    break;
                case 1:
                    mPaint.setColor(Color.parseColor("#FFCDBE3F"));
                    break;
                case 2:
                    mPaint.setColor(Color.GREEN);
                    break;
            }
            float x = (float) point.getmX();
            float y = (float) point.getmY();
            //根据种类画形状
            mPaint.setStyle(Paint.Style.FILL);
            switch (point.getmType()) {
                case Satellite.GPS://gps圆形
                    canvas.drawCircle(x, y, msRadius, mPaint);
                    break;
                case Satellite.GLONASS://glonass三角形
                    Path path = new Path();
                    path.moveTo(x, y - msRadius * 2);// 此点为多边形的起点
                    path.lineTo(x - msRadius, y + msRadius / 2);
                    path.lineTo(x + msRadius, y + msRadius / 2);
                    path.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path, mPaint);
                    break;
                case Satellite.BD://bd矩形
                    canvas.drawRect(x - msRadius, y - msRadius, x + msRadius, y + msRadius, mPaint);
                    break;
                case Satellite.SBAS://SBAS画红圈
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(x, y, msRadius + 4, paint);
                    canvas.drawCircle(x, y, msRadius, mPaint);
                    break;
            }
            //画卫星的编号
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(msRadius);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText(String.valueOf(point.getmNum()), x, y, mPaint);
        }
    }

    private void drawStarbackground(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        //这里绘制星空的圆形
        canvas.drawCircle(mX, mY, mRadius, mPaint);
        //画圆圈和分割线
        int r;//画圆的半径，根据高度角来获得对应的边长
        for (int i = 0; i <= 3; i++) {
            r = (int) (mRadius * Math.cos(Coordinate.degreeToRadian(i * 30)));//高度角分别是0,30,60,90值分别是r,1/2r……
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mX, mY, r, mPaint);
            if (i == 0) {
                mPaint.setTextSize(20);
                mPaint.setColor(Color.parseColor("#FF0044"));
                canvas.drawText("N", mX, mY - r + 10, mPaint);
            }
            canvas.drawText(String.valueOf(30 * i), mX, mY - r, mPaint);
        }
        //画线,根据极坐标画线
        drawdivideLines(canvas, 12);
    }

    private void drawdivideLines(Canvas canvas, int divideNum) {
        mPaint.setColor(Color.WHITE);
        //将360度等分化为个数，然后分别求得各个点的x,y坐标
        float[] ptr = new float[divideNum * 4];//每条线都有4个点
        double divide = 2 * Math.PI / divideNum;//每个角的弧度值
        int divideAngel = 360 / divideNum;
        //计算坐标点的值，通过极坐标求得
        //根据坐标角获得x,y的值，但需要考虑到不同象限的加减值,经过考虑只有y轴为相反值
        double x;
        double y;
        for (int i = 0; i < divideNum; i++) {
            x = mX + mRadius * Math.cos(divide * i);
            y = mY - mRadius * Math.sin(divide * i);
            ptr[4 * i] = mX;
            ptr[4 * i + 1] = mY;
            ptr[4 * i + 2] = (float) x;
            ptr[4 * i + 3] = (float) y;
            //这里画字
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(20);
            mPaint.setTextAlign(Paint.Align.CENTER);
            if (i < 4)//0-3
                canvas.drawText(String.valueOf(90 - divideAngel * i), (float) x, (float) y, mPaint);
            else
                canvas.drawText(String.valueOf(360 - divideAngel * (i - 3)), (float) x, (float) y, mPaint);
        }
        mPaint.setColor(Color.WHITE);
        canvas.drawLines(ptr, mPaint);
    }

    /**
     * 这是用来设置
     * 1获取屏幕的宽和高，定义自定义控件的大小
     * 2自定义控件的宽和高，如果不设置的话只能在xml文档中写死，最好是在这里设置
     * 宽度是fill_parent,不用管,但高度却需要设置成warp_content
     * 画的中心是通过屏幕获取，然后半径是通过控件的宽度获取
     *
     * @param widthMeasureSpec  自定义控件的宽度
     * @param heightMeasureSpec 自定义控件的高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeight = measureHeight(heightMeasureSpec);
        mWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    private int measureHeight(int heightMeasureSpec) {
        int HspeMode = MeasureSpec.getMode(heightMeasureSpec);
        int HspeSize = MeasureSpec.getSize(heightMeasureSpec);
        if (HspeMode == MeasureSpec.EXACTLY) {
            return HspeSize;
        } else if (HspeMode == MeasureSpec.AT_MOST) {
            return mRadius * 2;
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
            return mRadius * 2;
        } else {
            return WspeSize;
        }
    }

    /**
     * 从sp获得宽和高，并且根据宽和高设置画圆的半径
     * 本想用sp来获得，但是考虑到屏幕转化，需要重新获取宽高, 最好获取控件的宽和高
     * 获得宽和高控件的宽和高和整个屏幕的宽和高获得屏幕的宽和高
     * 理解有误，如果只有一个控件，则用屏幕的宽和高来代表图片是没有问题的，但是如果有多个控件就不可以了.
     * 总的来说，就以控件为中心来进行画图，不用考虑屏幕的宽和高。因为控件自适应匹配屏幕，只要以控件canvas的宽和高
     * 来画图就可以了,相对于控件来进行画图就可以了
     */
    private void getWindowValue() {
        int length = (mWidth < mHeight) ? mWidth : mHeight;
        mRadius = length / 2 - 30;
        mX = mWidth / 2;
        mY = mHeight / 2;
        msRadius = (float) 0.06d * mRadius;
    }

}
