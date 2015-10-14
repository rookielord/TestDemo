package com.zhd.hi_test.ui;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 2015032501 on 2015/10/12.
 * 实现缩放
 */
public class ZoomListener implements View.OnTouchListener {

    //在屏幕上点的数量
    private int mPointNum = 0;
    //当两个点在屏幕时的距离
    private float moldDist = 0;
    //当第一根手指放上去的位置
    private float mStartX = 0;
    private float mStartY = 0;
    //平移量
    private float mOffsetX = 0;
    private float mOffsetY = 0;

    //我的自定义控件
    private SurveyView mView;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mView = (SurveyView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN://第一个手指按上去
                mPointNum = 1;
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_UP://唯一一个手指松开
                mPointNum = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP://第二根手指松开
                mPointNum -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN://第一个之后的手指放上去时，包括第二个，第三个……等都会调用该事件
                moldDist = spacing(event);
                mPointNum += 1;
                break;
            case MotionEvent.ACTION_MOVE://手指移动时发动
                if (mPointNum >= 2) {
                    float newDist = spacing(event);
                    if (newDist > moldDist + 1) {
                        zoom(newDist / moldDist);
                        moldDist = newDist;
                    }
                    if (newDist < moldDist - 1) {
                        zoom(newDist / moldDist);
                        moldDist = newDist;
                    }
                }
                if (mPointNum == 1) {
                    offsetspacing(event);
                }
                break;
        }
        return true;
    }

    private void offsetspacing(MotionEvent event) {
        mOffsetX= event.getX()-mStartX;
        mOffsetY= event.getY()-mStartY;
        mView.setOffset(mOffsetX,mOffsetY);
        mStartX=event.getX();
        mStartY=event.getY();
        mView.invalidate();
    }
    /**
     * 将其比例缩放效果的大小传过去
     *
     * @param f
     */
    private void zoom(float f) {
        mView.setmScale(f);
        mView.invalidate();
    }

    /**
     * @param event 屏幕上发生点击事件的位置
     * @return 返回两者之间的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
