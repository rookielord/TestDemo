package com.zhd.hi_test;

import android.app.Application;

import com.zhd.hi_test.module.Project;

/**
 * Created by 2015032501 on 2015/9/18.
 * 全局变量
 * 包含项目对象Project
 * 包含ZHD_TEST路径
 */
public class Data extends Application {
    private Project mProject;
    private String mPath;

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public Project getmProject() {
        return mProject;
    }

    public void setmProject(Project mProject) {
        this.mProject = mProject;
    }
}
