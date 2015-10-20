package com.zhd.hi_test.module;

/**
 * Created by 2015032501 on 2015/10/20.
 * 1.名称
 * 2.图片:文档或文件夹
 * 3.其路径
 */
public class MyFile {
    private String name;
    private int image_soure;
    private String path;

    public MyFile(String name, int image_soure, String path) {
        this.name = name;
        this.image_soure = image_soure;
        this.path =path ;
    }

    public String getName() {
        return name;
    }

    public int getImage_soure() {
        return image_soure;
    }

    public String getPath() {
        return path;
    }
}
