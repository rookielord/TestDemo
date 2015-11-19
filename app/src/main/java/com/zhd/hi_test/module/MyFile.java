package com.zhd.hi_test.module;

import java.io.File;

/**
 * Created by 2015032501 on 2015/10/20.
 * 1.名称
 * 2.图片:文档或文件夹
 * 3.其路径
 */
public class MyFile {
    private String mName;
    private int mImage;
    private File mFile;

    public MyFile(String name, int image, File file) {
        this.mName = getShortName(name);
        this.mImage = image;
        this.mFile =file ;
    }

    private String getShortName(String name) {
        if (name.length()>10){
            return name.substring(0,7)+"..";
        }
        return name;
    }

    public String getName() {
        return mName;
    }

    public int getImage() {
        return mImage;
    }

    public File getFile() {
        return mFile;
    }
}
