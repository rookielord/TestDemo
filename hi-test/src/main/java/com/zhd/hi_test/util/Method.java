package com.zhd.hi_test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;


import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.module.Project;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

    /**
     * @param path
     * @param configs [0]:名字;[1]备注;[2]创建时间;[3]最后使用时间;[4]创建的表名
     * @param context
     * @return
     */
    public static boolean createProject(String path, String[] configs, Context context) {
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
                String tableName = createTableName();
                String msg = configs[0] + ";" + configs[1] + ";" + configs[2] + ";" + configs[3] + ";" + tableName;
                out.write(msg.getBytes());
                //并创建对应的数据库表
                Curd curd = new Curd(tableName, context);
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

    public static String createTableName() {
        String mTableName = "project";
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyymmddHHmmss");
        mTableName += format.format(date);
        return mTableName;
    }

    //删除项目
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

    //在储存卡上创建文件夹
    public static String createDirectory(Context context) {
        String mPath = null;
        File ext_path = Environment.getExternalStorageDirectory();
        File file = new File(ext_path, "ZHD_TEST");
        //第一次安装，或判断是否存在
        if (!file.exists()) {
            file.mkdir();
        }
        //创建Project目录
        File pro_file = new File(file.getPath() + "/Project");
        if (!pro_file.exists()) {
            //第一次创建后就不会再第二次创建了
            pro_file.mkdir();
        }
        //最终获取其路径,并将其赋值给全局变量
        mPath = pro_file.getPath();
        SharedPreferences.Editor sp = context.getSharedPreferences("VALUE", context.MODE_PRIVATE).edit();
        sp.putString("path", mPath);
        sp.commit();
        return mPath;
    }

    //获取数据参数
    public static int[] getWindowValue(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        //获得宽和高,就是整个屏幕的宽和高
        int width = p.x;
        int height = p.y;
        int[] values = {width, height};
        return values;

    }

    /**
     * [0]:名字;[1]备注;[2]创建时间;[3]最后使用时间;[4]创建的表名
     *
     * @param project
     */
    public static void updateProject(Project project) {
        BufferedWriter bw=null;
        try {
             bw= new BufferedWriter(new FileWriter(project.getmConfig()));
            //拼接信息
            String time = Method.getCurrentTime();
            String msg = project.getmName() + ";" +
                    project.getmBackup() + ";" +
                    project.getmTime() + ";" +
                    time + ";" +
                    project.getmTableName();
            //写入内容
            bw.write(msg);
            bw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
