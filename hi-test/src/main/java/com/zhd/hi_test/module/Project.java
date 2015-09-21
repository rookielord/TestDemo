package com.zhd.hi_test.module;

import java.io.File;

/**
 * Created by 2015032501 on 2015/9/7.
 * 一个项目对象拥有的属性和对应的方法
 * 名称
 * 创建时间
 * 备注内容
 * 对应数据库中的表
 * 配置文件(以上4个都会被写入其中)
 */
public class Project {
    private String mName;
    private String mTime;
    private String mBackup;
    private File mConfig;
    private String mTableName;
    private String mLastTime;

    public Project(String mName, String mBackup, String mTime, String mLastTime, String mTableName, File mConfig) {
        this.mName = mName;
        this.mTime = mTime;
        this.mBackup = mBackup;
        this.mConfig = mConfig;
        this.mTableName = mTableName;
        this.mLastTime = mLastTime;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmBackup() {
        return mBackup;
    }

    public void setmBackup(String mBackup) {
        this.mBackup = mBackup;
    }

    public String getmTableName() {
        return mTableName;
    }

    public void setmTableName(String mTableName) {
        this.mTableName = mTableName;
    }

    public File getmConfig() {
        return mConfig;
    }

    public void setmConfig(File mConfig) {
        this.mConfig = mConfig;
    }

    public String getmLastTime() {
        return mLastTime;
    }

    public void setmLastTime(String mLastTime) {
        this.mLastTime = mLastTime;
    }
}
