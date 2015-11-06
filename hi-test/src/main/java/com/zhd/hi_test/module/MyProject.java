package com.zhd.hi_test.module;

import java.io.File;
import java.io.Serializable;

/**
 * Created by 2015032501 on 2015/9/7.
 * 一个项目对象拥有的属性和对应的方法
 * 名称
 * 创建时间
 * 备注内容
 * 对应坐标系
 * 对应数据库中的表
 * 配置文件(以上4个都会被写入其中)
 * 考虑到下一次自动打开项目，需要将最后打开的项目对象写入file/last.txt,之后读取该对象，让其为全局project对象
 */
public class MyProject implements Serializable {
    private String mName;
    private String mTime;
    private String mBackup;
    private File mConfig;
    private String mTableName;
    private String mLastTime;
    private String mCoordinate;
    private String mGuass;

    public String getmGuass() {
        return mGuass;
    }

    public void setmGuass(String mGuass) {
        this.mGuass = mGuass;
    }



    /**
     * 创建顺序,更改最后的时间不用获得，而是获得配置文件config的最后更改时间。其它的使用
     * @param mName 名称0
     * @param mBackup 备注1
     * @param mTime 创建时间2
     * @param mLastTime 最后使用时间3
     * @param mCoordinate 坐标系统 4
     * @param mTableName 创建的表名5
     * @param mConfig config.txt文件位置6
     * @param mGuass 所采用的高斯投影带数
     */
    public MyProject(String mName, String mBackup, String mTime, String mLastTime, String mCoordinate, String mTableName, File mConfig, String mGuass) {
        this.mName = mName;
        this.mTime = mTime;
        this.mBackup = mBackup;
        this.mConfig = mConfig;
        this.mTableName = mTableName;
        this.mLastTime = mLastTime;
        this.mCoordinate = mCoordinate;
        this.mGuass = mGuass;
    }

    public String getmCoordinate() {
        return mCoordinate;
    }

    public void setmCoordinate(String mCoordinate) {
        this.mCoordinate = mCoordinate;
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
