package com.zhd.hi_test.ui;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 2015032501 on 2015/10/28.
 */
public class OffsetListener implements View.OnTouchListener {

    private float mStartX;
    private int mPointNum = 0;
    private float mOffsetX = 0;
    private SatelliteView mView;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mView = (SatelliteView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN://第一个手指按上去
                mStartX = event.getX();
                mPointNum = 1;
                break;
            case MotionEvent.ACTION_UP://唯一一个手指松开
                mPointNum = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP://第二根手指松开
                mPointNum -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN://第一个之后的手指放上去时，包括第二个，第三个……等都会调用该事件
                mPointNum += 1;
                break;
            case MotionEvent.ACTION_MOVE://手指移动时发动
                if (mPointNum == 1) {
                    offsetspacing(event);
                }
                break;
        }
        return true;
    }

    private void offsetspacing(MotionEvent event) {
        mOffsetX = event.getX() - mStartX;
        if (Math.abs(mOffsetX) > 1) {//如果偏移量大于1才进行平移，设置灵敏度
            mView.setOffset(mOffsetX);
        }
        mStartX = event.getX();
        mView.invalidate();
    }
}

