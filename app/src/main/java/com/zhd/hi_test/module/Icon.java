package com.zhd.hi_test.module;

import android.content.Intent;

/**
 * Created by 2015032501 on 2015/9/19.
 * 所有GridView中每个View的信息。其中包含
 * 名称，图片，id，跳转意图
 */
public class Icon {
    private String mName;
    private int mImage;
    private Intent mIntent;

    public Icon(String name, int image, Intent intent) {
        this.mName = name;
        this.mImage = image;
        this.mIntent = intent;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public String getName() {
        return mName;
    }

    public int getImage() {
        return mImage;
    }

}
