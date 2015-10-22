package com.zhd.hi_test.module;

import java.io.File;

/**
 * Created by 2015032501 on 2015/10/20.
 * 1.名称
 * 2.图片:文档或文件夹
 * 3.其路径
 */
public class MyFile {
    private String name;
    private int image_soure;
    private File file;

    public MyFile(String name, int image_soure, File file) {
        this.name = getShortName(name);
        this.image_soure = image_soure;
        this.file =file ;
    }

    private String getShortName(String name) {
        if (name.length()>10){
            return name.substring(0,7)+"..";
        }
        return name;
    }

    public String getName() {
        return name;
    }

    public int getImage_soure() {
        return image_soure;
    }

    public File getFile() {
        return file;
    }
}
