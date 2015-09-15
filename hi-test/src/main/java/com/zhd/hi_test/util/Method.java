package com.zhd.hi_test.util;

import android.content.Context;


import com.zhd.hi_test.db.Curd;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 2015032501 on 2015/9/8.
 * 用来存放一些静态方法来做一些小功能，例如获得时间或者输入内容检查……
 */
public class Method {

    public static boolean checkMsg(String msg) {
        boolean check1, check2;
        check1 = msg.trim().isEmpty();
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(msg);
        check2 = m.find();
        if (check1 || check2) {
            return false;
        } else {
            return true;
        }
    }

    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static boolean createDirectory(String path, String[] configs,Context context) {
        File pro_file = new File(path + "/" + configs[0]);
        if (!pro_file.exists()) {
            pro_file.mkdir();
            //写入配置文件
            File config_file = new File(pro_file, "config.txt");
            OutputStream out = null;
            try {
                //写入文件流
                out = new BufferedOutputStream(new FileOutputStream(config_file));
                //写入内容
                String tableName=createTableName();
                String msg = configs[0] + ";" + configs[1] + ";" + configs[2]+";"+tableName;
                out.write(msg.getBytes());
                //并创建对应的数据库表
                Curd curd=new Curd(tableName,context);
                curd.createTable(tableName);
                //写入
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                //关闭流
                if (out != null)
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return true;
        } else {
            return false;
        }
    }

    public static String createTableName(){
        String mTableName="project";
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat format=new SimpleDateFormat("yyyymmddHHmmss");
        mTableName+=format.format(date);
        return mTableName;
    }

    public static void deleteDirectory(File del_directory) {
        if (del_directory.isDirectory()) {
            File[] files = del_directory.listFiles();
            for (File file : files) {
                //因为在目录下还会有目录，所以先递归调用
                deleteDirectory(file);
                file.delete();
                //删除完子目录和子文件后删除自己
            }
            del_directory.delete();
        } else {
            del_directory.delete();
        }
    }

}
