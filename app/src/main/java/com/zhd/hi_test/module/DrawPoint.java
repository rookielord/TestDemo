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

    public String getName() {
        return mName;
    }


    public DrawPoint(float X, float Y, String name) {
        this.mX = X;
        this.mY = Y;
        this.mName = name;
    }

    public DrawPoint(float X, float Y) {
        this.mX = X;
        this.mY = Y;
    }

    public float getX() {
        return mX;
    }


    public float getY() {
        return mY;
    }

}
