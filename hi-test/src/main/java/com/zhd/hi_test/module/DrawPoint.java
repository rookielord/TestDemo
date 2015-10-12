package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/9/21.
 * 这个是自定义控件画图时使用的，
 * 有图片上的x,y
 * 1.先在数据库中查寻最后一条数据
 * 2.以最后一条数据的x,y作为原点
 * 3.其它的点与它相减得到差量，然后在图上进行修改
 */
public class DrawPoint {
    //这是画图的时候用的
    private float mX;
    private float mY;
    private String mName;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public DrawPoint(float mX, float mY, String mName) {
        this.mX = mX;
        this.mY = mY;
        this.mName = mName;
    }

    public DrawPoint(float mX, float mY) {
        this.mX = mX;
        this.mY = mY;
    }

    public float getmX() {
        return mX;
    }

    public void setmX(float mX) {
        this.mX = mX;
    }

    public float getmY() {
        return mY;
    }

    public void setmY(float mY) {
        this.mY = mY;
    }
}
