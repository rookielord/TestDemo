package com.zhd.starmap.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GpsSatellite;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zhd.starmap.module.StarPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
    private static int mRadius = 150;
    //屏幕的宽和高
    private int mWidth;
    private int mHeight;

    //画整个背景图的半径大小

    /**
     * 这里是实现将卫星对象转化成我要在图像上显示的卫星对象
     *
     * @param gpsSatellites 传输过来的卫星对象
     */
    public void SetSatetllite(List<GpsSatellite> gpsSatellites) {
        for (GpsSatellite satellite : gpsSatellites) {
            //判断得到的是GPS卫星
            if (satellite.usedInFix()) {
                //得到高度角，
                float elevation = satellite.getElevation();
                //通过仰角，计算出这个卫星应该绘制到离圆心多远的位置，这里用的是角度的比值
                double r2 = mRadius * ((90.0f - elevation) / 90.0f);
                //得到方位角（与正北向也就是Y轴顺时针方向的夹角，注意我们通常几何上的角度
                //是与X轴正向的逆时针方向的夹角）,在计算X，Y座标的三角函数时，要做转换
                double azimuth = satellite.getAzimuth();

                //转换成XY座标系中的夹角,方位角是与正北向也就是Y轴顺时针方向的夹角，
                //注意我们通常几何上的角度是与X轴正向的逆时针方向的夹角）,
                //在计算X，Y座标的三角函数时，要做转换
                double radian = degreeToRadian(360 - azimuth + 90);
                double x = mRadius + Math.cos(radian) * r2;
                double y = mRadius + Math.sin(radian) * r2;

                //得到卫星图标的半径
                int sr = mRadius;
                //以x,y为中心绘制卫星图标
                //在卫星图标的位置上绘出文字（卫星编号及信号强度）
                //信号强度
                int snr = (int) satellite.getSnr();
                //卫星编号
                //String info = String.format("#%s_%s", satellite.getPrn(), snr);
                int num = satellite.getPrn();
                //创建画图点对象,用hashMap来存储，不会存在不同名的键值对,如果出现同名的会自动覆盖
                StarPoint p = new StarPoint(x, y, num);
                //mPoints.put(num,p);
                mPoints.add(p);
            }
        }
    }

    public StarView(Context context) {
        super(context);

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
            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.FILL);
            float x = (float) point.getmX();
            float y = (float) point.getmY();
            canvas.drawCircle(x, y, 20, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(20);
            canvas.drawText(String.valueOf(point.getmNum()), x - 5, y - 5, mPaint);
        }
    }

    private void drawStarbackground(Canvas canvas) {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        //这里绘制星空的圆形
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        int r = mRadius / 3;
        //画圆圈和画角度
        for (int i = 1; i <= 3; i++) {
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            if (i == 3)
                mPaint.setColor(Color.RED);
            canvas.drawCircle(mRadius, mRadius, r * i, mPaint);
//            mPaint.setStyle(Paint.Style.FILL);
            //画线
            mPaint.setColor(Color.WHITE);
            float[] points = {mRadius, 0, mRadius, 2 * mRadius,
                    0, mRadius, 2 * mRadius, mRadius};
            canvas.drawLines(points, mPaint);
            //画数字
            for (int j = 0; j <= 3; j++) {
                canvas.drawText(String.valueOf((i * 30)), mRadius, r * i, mPaint);
            }
        }

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
        //这里获取屏幕的宽和高,并设置半径的高
        getWindowValue();
        //获得xml文件中对自定义控件的设置
        int MeasureWidth = measureWidth(widthMeasureSpec);
        int MeasureHeight = measureHeight(heightMeasureSpec);
        //将修改后的宽和高赋值给自定义控件
        setMeasuredDimension(MeasureWidth, MeasureHeight);
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
        if (mWidth == 0 || mHeight == 0) {
            Log.e("GPS", "屏幕参数出错");
        }
        mRadius = mWidth / 2 - 10;
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
