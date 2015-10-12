package com.zhd.hi_test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zhd.hi_test.module.DrawPoint;
import com.zhd.hi_test.module.MyPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/22.
 * 这是位置数据显示到自定义View的图上
 * 有些变量需要进行存储，在第二次加载的时候获得
 */
public class SurveyView extends View {

    //控件的属性
    private int mHeight;
    private int mWidth;
    //用来存放临时MyPoints的集合，因为在传输过来的时候无法获得宽和高
    List<MyPoint> temp;
    //需要画的点集合
    List<DrawPoint> drawPoints;
    //我的位置
    DrawPoint Mypoint;
    //作为基准的点的N,E坐标和控件中心位置的坐标
    private static double mReferenceN;
    private static double mReferenceE;
    //屏幕中心
    private static float mCenterX;
    private static float mCenterY;
    //画布中心
    private static float mCanvasCenterX;
    private static float mCanvasCenterY;
    //画笔
    private Paint mPaint;
    //本次缩放的比例和本次的平移量
    private static float mScale = 1.0f;
    private static float mOffsetX = 0;
    private static float mOffsety = 0;
    //最新打点的位置
    private DrawPoint mLastPont;


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
     * 将点集合传过来
     * 点击添加的时候传入
     * 如果点击则调用该方法，并刷新自定义控件
     * 这个会先执行，这个会先将数据传过来,但我还没有获取宽和高
     * 所以当前只能将中心点的mN和mE设置成功
     *
     * @param points
     */
    public void setPoints(List<MyPoint> points) {
        if (points != null) {//获得参考点
            MyPoint point = points.get(0);
            setCenterValue(point.getmN(), point.getmE());
            temp = points;
            updatePoints();
        }
    }

    /**
     * 传入比例尺后，会对当前的点的集合和我的位置进行重绘的位置进行重绘
     * 对绘制点集合的坐标进行处理
     *
     * @param Scale
     */
    public void setmScale(float Scale) {
        mScale *= Scale;
        updatePoints();
    }

    /**
     * 再次对当前点的进行更新
     *
     * @param
     */
    public void setOffset(float offsetX, float offsetY) {
        mOffsetX = offsetX;
        mOffsety = offsetY;
        updatePoints();
    }

    private void updatePoints() {
        drawPoints = new ArrayList<>();
        float x = 0;
        float y = 0;
        for (MyPoint point : temp) {
            //要在自定义控件上画图的集合
            x = (float) (mCenterX + (point.getmN() - mReferenceN) * mScale)+mOffsetX;
            y = (float) (mCenterY + (point.getmE() - mReferenceE) * mScale)+mOffsety;
            DrawPoint p = new DrawPoint(x, y, point.getName());
            drawPoints.add(p);
        }
        mLastPont = drawPoints.get(drawPoints.size() - 1);//获得最后一个点的位置
    }

    /**
     * 传入当前点的位置
     * 并根据参考点来画其位置
     * 如果不存在参考点，则当前点的位置为中心点的位置
     *
     * @param point
     */
    public void setLocation(MyPoint point) {
        if (mReferenceN == 0.0d || mReferenceE == 0.0d)
            Mypoint = new DrawPoint(mCenterX, mCenterY);
        else
            Mypoint = new DrawPoint((float)
                    (mCenterX + (point.getmN() - mReferenceN) * mScale)+mOffsetX,
                    (float) (mCenterY + (point.getmE() - mReferenceE) * mScale)+mOffsety);
    }

    /**
     * 设置参考点的坐标
     *
     * @param N
     * @param E
     */
    private void setCenterValue(double N, double E) {
        mReferenceN = N;
        mReferenceE = E;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这里获得屏幕的宽和高
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
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = new Paint();
        //1.画我的位置
        DrawMypoint(canvas);
        //2.画其它点
        DrawPoints(canvas);
    }

    private void DrawMypoint(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        if (Mypoint != null) {
            canvas.drawCircle(Mypoint.getmX(), Mypoint.getmY(), 5, mPaint);
            mPaint.setColor(Color.BLACK);
            if (mLastPont!=null)
            canvas.drawLine(Mypoint.getmX(), Mypoint.getmY(), mLastPont.getmX(), mLastPont.getmY(), mPaint);
        }
    }

    /**
     * 遍历DrawPoints中的元素，然后将其画在图片上
     * 以当前点为中心画两条线交叉
     * 这里需要对传输过来的点集合进行处理，并绘制在地图上
     *
     * @param canvas
     */
    private void DrawPoints(Canvas canvas) {
        float x = 0;
        float y = 0;
        if (temp == null)
            return;
        for (DrawPoint point : drawPoints) {
            mPaint.setColor(Color.BLACK);
            //画图的点的集合
            x = point.getmX();
            y = point.getmY();
            float[] points = new float[]{x, y - 10, x, y + 10, x - 10, y, x + 10, y};
            canvas.drawLines(points, mPaint);
            //字体居中
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(14);
            canvas.drawText(point.getmName(), x, y - 10, mPaint);
        }
    }
}
