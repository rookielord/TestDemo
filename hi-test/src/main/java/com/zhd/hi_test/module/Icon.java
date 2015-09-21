package com.zhd.hi_test.module;

import android.content.Intent;

/**
 * Created by 2015032501 on 2015/9/19.
 * 所有GridView中每个View的信息。其中包含
 * 名称，图片，id，跳转意图
 */
public class Icon {
    private String icon_name;
    private int icon_image_soure;
    private int icon_id;
    private Intent icon_intent;

    public Icon(String icon_name, int icon_image_soure, int icon_id, Intent icon_intent) {
        this.icon_name = icon_name;
        this.icon_image_soure = icon_image_soure;
        this.icon_id = icon_id;
        this.icon_intent = icon_intent;
    }

    public Intent getIcon_intent() {
        return icon_intent;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public int getIcon_image_soure() {
        return icon_image_soure;
    }

    public int getIcon_id() {
        return icon_id;
    }
}
