package com.zhd.hi_test;

import android.app.Application;

/**
 * Created by 2015032501 on 2015/9/18.
 * 全局变量
 */
public class Data extends Application {
    private String mName;
    public void setName(String name){
        this.mName=name;
    }
    public String getName(){
        return this.mName;
    }

    @Override
    public void onCreate() {
        mName="天空的城";
        super.onCreate();
    }
}
