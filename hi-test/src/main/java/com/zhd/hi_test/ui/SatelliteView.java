package com.zhd.hi_test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.test.LoaderTestCase;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.activity.MainActivity;
import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.util.ViewUtil;

import java.lang.annotation.ElementType;
import java.security.Principal;
import java.util.List;

import static android.graphics.Paint.Align.CENTER;

/**
 * Created by 2015032501 on 2015/10/27.
 * 传入卫星信息进行画图
 */
public class SatelliteView extends View {

    private int mHeight;
    private int mWidth;
    private Paint mPaint = new Paint();
    private float mOffsetX = 0;
    private int mValue = 20;
    private float mCoorX = 20;//画图坐标轴的左下角的x坐标
    private float mCoorY;//画图坐标轴左下角的y坐标

    public SatelliteView(Context context) {
        super(context);
    }

    public SatelliteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SatelliteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 在这里获得宽和高
     * 并设置画图坐标轴的y坐标
     *
     * @param widthMeasureSpec  传入的宽的参数
     * @param heightMeasureSpec 传入的高的参数
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeight = measureHeight(heightMeasureSpec);
        mWidth = measureWidth(widthMeasureSpec);
        mCoorY = mHeight - 50;
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * @param heightMeasureSpec
     * @return 宽的长度，如果width设置为wrap_content则首先画300的宽度
     */
    private int measureHeight(int heightMeasureSpec) {
        int HspeMode = MeasureSpec.getMode(heightMeasureSpec);
        int HspeSize = MeasureSpec.getSize(heightMeasureSpec);
        if (HspeMode == MeasureSpec.EXACTLY) {//match_parent返回的是这个
            return HspeSize;
        } else if (HspeMode == MeasureSpec.AT_MOST) {
            return 300;
        } else {
            return HspeSize;
        }
    }

    /**
     * match_parent返回的都是EXACTLY
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int WspeMode = MeasureSpec.getMode(widthMeasureSpec);
        int WspeSize = MeasureSpec.getSize(widthMeasureSpec);
        if (WspeMode == MeasureSpec.EXACTLY) {
            return WspeSize;
        } else if (WspeMode == MeasureSpec.AT_MOST) {
            if (Const.satellites.size() == 0)
                return 300;
            else {
                return 40 * Const.satellites.size();
            }
        } else {
            return WspeSize;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(mCoorX, 20, mCoorX, mCoorY, mPaint);
        canvas.drawText("Pnr", mCoorX - 6, mCoorY + 10, mPaint);
        canvas.drawText("Snr", mCoorX, 14, mPaint);
//        画线会出问题
        int offset = (int) (mCoorY / 3);
        for (int i = 1; i <= 3; i++) {
            canvas.drawLine(mCoorX, offset * i , mWidth, offset * i , mPaint);
        }
        //2.画图例
        drawLegend(canvas);
        //3.画每个卫星的信噪比
        drawSatellite(canvas);
    }

    private void drawSatellite(Canvas canvas) {
        float start_x = 25;//开始画图的位置
        float offset = 10;//每个柱状物之间的间隔
        float width = 30;//每个柱状物的宽度
        float end_y = mCoorY;//固定的y轴坐标
        float draw_x = 0;//实际上画的x的位置
        float draw_y = 0;
        float end_draw_x = 0;
        float add_height = 0;//根据性噪比，需要其高度增加
        //根据信噪比判断颜色
        int i = 0;//当前是第几号卫星，从0开始
        for (Satellite s : Const.satellites) {
            int level = ViewUtil.snrToSignalLevel(s.getSnr());
            switch (level) {
                case 0:
                    mPaint.setColor(Color.RED);
                    add_height = 10;
                    break;
                case 1:
                    mPaint.setColor(Color.parseColor("#ffc4c845"));
                    add_height = 20;
                    break;
                case 2:
                    mPaint.setColor(Color.parseColor("#FFC4FFB9"));
                    add_height = 20;
                    break;
                case 3:
                    mPaint.setColor(Color.parseColor("#ff97ff82"));
                    add_height = 20;
                    break;
                case 4:
                    mPaint.setColor(Color.GREEN);
                    add_height = 80;
                    break;
            }
//            画矩形
//            第一次画的snr和pnr的draw_x==start_x
            draw_x = start_x + width * i + i * offset + mOffsetX;
            draw_y = end_y - s.getSnr();
            end_draw_x = draw_x + width;
            if (draw_x < mCoorX) {
                draw_x = mCoorX;
            }
            if (end_draw_x < mCoorX) {
                end_draw_x = mCoorX;
            }
            if (s.getSnr() == 0) {
                canvas.drawRect(draw_x, draw_y, end_draw_x, end_y, mPaint);
            } else {
                canvas.drawRect(draw_x, draw_y - add_height - mValue, end_draw_x, end_y, mPaint);
            }

//            画卫星的信噪比
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(14);
            mPaint.setTextAlign(CENTER);
//            这里draw_x=mCoorX=20,
//            只有当draw_x>start_x的时候才会去画信噪比
            if (draw_x > mCoorX) {
                if (s.getSnr() == 0) {
                    canvas.drawText(String.valueOf(s.getSnr()), draw_x + 13, draw_y - 5, mPaint);
                } else {
                    canvas.drawText(String.valueOf(s.getSnr()), draw_x + 13, draw_y - add_height - 5 - mValue, mPaint);
                }
//            画卫星的Pnr，卫星的编号
                mPaint.setTextAlign(CENTER);
                canvas.drawText(String.valueOf(s.getPrn()), start_x + draw_x - 13, end_y + 10, mPaint);
            }
            i++;
        }
    }


    private void drawLegend(Canvas canvas) {
        //y轴不变，x轴变
        int start_x = 20;//固定
        int start_y = mHeight - 30;//固定
        int end_y = mHeight - 20;//固定

        int end_x = start_x + mValue;//这个只有第一次添加了，其他的都没有添加,所以导致了其它的很短


        int text_y = end_y + 15;
//        0,红色部分，增幅为10，除了0以外，所有的高度增加10
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        canvas.drawRect(start_x, start_y, (end_x = end_x + 10), end_y, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText("20", end_x - 10, text_y, mPaint);

//        1,黄色,增幅为20
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#ffc4c845"));
        canvas.drawRect(end_x, start_y, (end_x = end_x + mValue + 20), end_y, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText("36", end_x - 10, text_y, mPaint);
//        2,浅绿色，增幅为20
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FFC4FFB9"));
        canvas.drawRect(end_x, start_y, (end_x = end_x + mValue + 20) + mValue, end_y, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText("60", end_x - 10, text_y, mPaint);
//        3浅绿色部分，增幅为20
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#ff97ff82"));
        canvas.drawRect(end_x, start_y, (end_x = end_x + mValue + 20) + mValue, end_y, mPaint);
        mPaint.setColor(Color.BLACK);

        mPaint.setTextSize(15);
        canvas.drawText("80", end_x - 10, text_y, mPaint);
//        4.纯绿色增幅50
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(end_x, start_y, (end_x + 80), end_y, mPaint);
        mPaint.setColor(Color.BLACK);

    }

    /**
     * 偏移量的限制，平移量因该是由卫星所画的长度决定。
     * 所以需要获得具体画了多少个卫星，计算其总长度，但在没有连接状态下，其长度为空间本身
     *
     * @param OffsetX
     */
    public void setOffset(float OffsetX) {
        float width = mWidth;
        mOffsetX += OffsetX;
        if (mOffsetX > 0)
            mOffsetX = 0;
//        进行平移的时候需要限制其平移量
        if (Const.satellites.size() != 0)
            width = 40 * Const.satellites.size();
        if (mOffsetX < -width)
            mOffsetX = -width;
    }
}
