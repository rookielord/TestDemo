package com.zhd.hi_test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zhd.hi_test.module.DrawPoint;
import com.zhd.hi_test.module.MyPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/22.
 * 1.主要是将当前点的坐标转化成屏幕上的坐标
 * 2.在画图时：
 * 1)有已采集点，就会以最早采集点为中心点，画出其它的点
 * 2)没有其它点，会以当前点为屏幕中心
 */
public class SurveyView extends View {
    //自定义控件的宽和高
    private int mHeight;
    private int mWidth;
    //用来存放临时MyPoints的集合，因为在传输过来的时候无法获得宽和高，当前控件还没有获得宽和高
    List<MyPoint> mPoints;
    //需要画的点集合
    List<DrawPoint> drawPoints = new ArrayList<>();
    //我的位置
    DrawPoint Mypoint;
    MyPoint myPoint;
    //作为基准的点的N,E坐标和控件中心位置的坐标
    private MyPoint mREFpoint;
    //屏幕中心
    private static float mCenterX;
    private static float mCenterY;
    //画笔
    private Paint mPaint = new Paint();
    //本次缩放的比例和本次的平移量
    private float mScale = 1.0f;
    private float mOffsetx = 0;
    private float mOffsety = 0;
    //最新打点的位置
    private DrawPoint mLastPont;

    public void setmOffsets(float Offsetx, float Offsety) {
        this.mOffsetx = Offsetx;
        this.mOffsety = Offsety;
    }

    public void setmScale(float Scale) {
        this.mScale = Scale;
    }

    public SurveyView(Context context) {
        super(context);
    }

    public SurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurveyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 主要用于当前点的居中
     * 计算参考点与当前点的距离差值，从参考点到当前点的平移量，从参考点平移到当前点，并更新其它点的坐标
     * 注意：需要处理没有参考点的情况，虽然一开始就是居中的情况。
     *
     * @param point 当前位置点的坐标
     */
    public void SetCurrentLocation(MyPoint point) {
        if (mPoints != null) {
            mOffsetx = (float) (mREFpoint.getmN() - point.getmN()) * mScale;
            mOffsety = (float) (mREFpoint.getmE() - point.getmE()) * mScale;
        }
    }

    /**
     * 将采集到的点集传送过来，并以最开始的点作为参考点
     * @param points 根据数据库倒序查询出来的点
     */
    public void setPoints(List<MyPoint> points) {
        if (points != null) {
            setCenterValue(points.get(points.size() - 1));
            mPoints = points;
        }
    }

    /**
     * 从外界传入缩放的比例
     * 并控制其大小
     *
     * @param Scale 传入的缩放比例
     */
    public void setScale(float Scale) {
        mScale *= Scale;
        if (mScale > 30) {
            mScale = 30;
        } else if (mScale < 1 / 30)
            mScale = 1 / 30;
    }

    /**
     * 从外界传入平移的量
     *
     * @param offsetX x方向平移量
     * @param offsetY y方向平移量
     */
    public void setOffset(float offsetX, float offsetY) {
        mOffsetx += offsetX;
        mOffsety += offsetY;
    }

    /**
     * 主要功能，将大地坐标N,E,转化为屏幕坐标x,y.
     *
     * 每次在更新画布点集合之前需要对画布点集合进行清空
     * 这样就不会累加
     * 这
     * 就算是进行缩放的时候也会涉及到平移
     * 1.更新所有点的坐标，包括现在点也要进行重绘
     * 2.在更新的时候会牵扯到上一次的平移，我在进行放大的时候会重新加载这个
     */
    private void updatePoints() {
        //1.当前有位置才会绘制当前点
        if (myPoint != null) {
            if (mPoints == null) {//如果没有参考点的情况，即没有已知点的情况
                Mypoint = new DrawPoint(mCenterX, mCenterY);
            } else {//根据根据传进来的当前位置，获得在屏幕上的对应坐标
                float myX = (float) (mCenterX + (myPoint.getmN() - mREFpoint.getmN()) * mScale) + mOffsetx;
                float myY = (float) (mCenterY + (myPoint.getmE() - mREFpoint.getmE()) * mScale) + mOffsety;
                Mypoint = new DrawPoint(myX, myY);
            }
        }
        //2.更新传入的在数据库中已有的点，如果没有则不进行下面的操作
        //3.更新数据库中点在屏幕上的坐标
        //3.1清除所有的画在图上的点
        //2.更新已有点的位置
        if (mPoints == null)
            return;
        drawPoints.clear();
        for (MyPoint myPoint : mPoints) {
            //要在自定义控件上画图的集合
            float x = (float) (mCenterX + (myPoint.getmN() - mREFpoint.getmN()) * mScale) + mOffsetx;
            float y = (float) (mCenterY + (myPoint.getmE() - mREFpoint.getmE()) * mScale) + mOffsety;
            DrawPoint p = new DrawPoint(x, y, myPoint.getName());
            drawPoints.add(p);
        }
        //4.获得最后一个点在屏幕上的坐标
        mLastPont = drawPoints.get(0);
    }

    /**
     * 传入当前点的位置
     * 并根据参考点来画其位置
     * 如果不存在参考点，则当前点的位置为中心点的位置
     */
    public void setMyLocation(MyPoint p) {
        myPoint = p;
    }

    private void setCenterValue(MyPoint myPoint) {
        mREFpoint = myPoint;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这里获得屏幕的宽和高,然后获得屏幕的中心点
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = measureWidth(widthMeasureSpec);
        mHeight = measureHeight(heightMeasureSpec);
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
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
        if (WspeMode == MeasureSpec.EXACTLY) {//具体的宽和高,match_parent
            return WspeSize;
        } else if (WspeMode == MeasureSpec.AT_MOST) {//wrap_content
            return WspeSize;
        } else {
            return WspeSize;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAntiAlias(true);
        //1.在这里更新点
        updatePoints();
        //1.画我的位置
        DrawMypoint(canvas);
        //2.画其它点
        DrawPoints(canvas);
        //3.画比例尺
        DrawScale(canvas);
    }

    /**
     * 以比例尺右下点的坐标，为startx和starty
     * 宽度为40像素
     *
     * @param canvas
     */
    private void DrawScale(Canvas canvas) {
        int startx = mWidth - 30;
        int starty = mHeight - 5;
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3);
        canvas.drawLine(startx, starty, startx, starty - 5, mPaint);//第一条线
        canvas.drawLine(startx, starty, startx - 30, starty, mPaint);//第二条线
        canvas.drawLine(startx - 30, starty, startx - 30, starty - 5, mPaint);//第三条线
        DecimalFormat df = new DecimalFormat("0.00");//保留小数位
        canvas.drawText(df.format(mScale) + "m", startx - 15, starty - 6, mPaint);
    }


    /**
     * 1.画出当前点的位置
     * 2.画出当前点和上一个点的连线
     * 判断条件，当前位置点不为空，拥有上一个点
     *
     * @param canvas
     */
    private void DrawMypoint(Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(1);
        if (Mypoint != null) {
            canvas.drawCircle(Mypoint.getmX(), Mypoint.getmY(), 5, mPaint);
            mPaint.setColor(Color.BLACK);
            if (mLastPont != null)
                canvas.drawLine(Mypoint.getmX(), Mypoint.getmY(), mLastPont.getmX(), mLastPont.getmY(), mPaint);
        }
    }

    /**
     * 遍历DrawPoints中的元素，然后将其画在图片上
     * 以当前点为中心画两条线交叉
     * 这里需要对传输过来的点集合进行处理，并绘制在地图上
     * @param canvas
     */
    private void DrawPoints(Canvas canvas) {
        float x;
        float y;
        if (mPoints == null)
            return;
        for (DrawPoint point : drawPoints) {
            mPaint.setColor(Color.BLACK);
            mPaint.setAntiAlias(true);
            //画图的点的集合
            x = point.getmX();
            y = point.getmY();
            float[] points = new float[]{x, y - 10, x, y + 10, x - 10, y, x + 10, y};
            mPaint.setStrokeWidth(2);
            canvas.drawLines(points, mPaint);
            //字体居中
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(14);
            canvas.drawText(point.getmName(), x, y - 10, mPaint);
        }
    }
}
