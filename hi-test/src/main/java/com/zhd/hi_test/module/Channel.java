package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/9/18.
 */
public class Channel {
    private int mID;
    private String mName;
    private int mOrder;
    private String mWebUrl;

    public Channel(int mID, String mName, int mOrder, String mWebUrl) {
        this.mID = mID;
        this.mName = mName;
        this.mOrder = mOrder;
        this.mWebUrl = mWebUrl;
    }

    public int getmID() {
        return mID;
    }

    public String getmName() {
        return mName;
    }

    public int getmOrder() {
        return mOrder;
    }

    public String getmWebUrl() {
        return mWebUrl;
    }
}
