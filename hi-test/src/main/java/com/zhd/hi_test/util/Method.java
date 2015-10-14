package com.zhd.hi_test.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;


import com.zhd.hi_test.Data;
import com.zhd.hi_test.activity.MainActivity;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.module.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 2015032501 on 2015/9/8.
 * 用来存放一些静态方法来做一些小功能，例如获得时间或者输入内容检查……
 */
public class Method {

    public static boolean checkMsg(String msg, String path) {
        boolean check1, check2, check3=false;
        check1 = msg.trim().isEmpty();
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(msg);
        check2 = m.find();
        String[] proName = new File(path).list();
        if (proName.length > 0) {
            for (String name : proName) {
                if (name.equals(msg))
                    check3 = true;
            }
        }
        if (check1 || check2||check3) {
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
     * 进行大改，使用objectOutputStream
     *
     * @param path
     * @param configs  [0]:名字;[1]备注;[2]创建时间;[3]最后使用时间 [4]坐标系统  新增[5]高斯投影带的
     * @param activity
     * @return
     */
    public static boolean createProject(String path, String[] configs, Activity activity) {
        File pro_file = new File(path + "/" + configs[0]);
        Data d = (Data) activity.getApplication();
        if (!pro_file.exists()) {
            pro_file.mkdir();
            //写入配置文件
            File config_file = new File(pro_file, "config.txt");
            ObjectOutputStream out = null;
            try {
                //写入文件流
                out = new ObjectOutputStream(new FileOutputStream(config_file));
                //写入内容
                String tableName = createTableName();
                //创建project对象,最后一个是配置文件的File路径
                Project p = new Project(configs[0], configs[1], configs[2], configs[3], configs[4], tableName, config_file, configs[5]);
                out.writeObject(p);
                //将其设为全局变量
                d.setmProject(p);
                //并创建对应的数据库表
                Curd curd = new Curd(tableName, activity);
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
    public static void createDirectory(Activity activity) {
        Data d = (Data) activity.getApplication();
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
        d.setmPath(mPath);
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
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(project.getmConfig()));
            //写入内容
            project.setmLastTime(getCurrentTime());
            out.writeObject(project);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 所以最好加一次判断，确定这是第一次执行
     * [0]:名字;[1]备注;[2]创建时间;[3]最后使用时间
     *
     * @param activity
     */
    public static void createDefaultProject(Activity activity) {
        //获得路径
        Data d = (Data) activity.getApplication();
        //获得当前是否第一次运行
        SharedPreferences sp = activity.getSharedPreferences("VALUE", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        boolean isFirst = sp.getBoolean("isFirst", true);
        //获得全局变量的Project路径
        if (isFirst) {
            String path = d.getmPath();//path是指Project的位置
            String time = Method.getCurrentTime();
            String[] configs = new String[]{"default", "默认创建", time, time, "北京54坐标系", "3度带"};
            //创建默认项目
            Method.createProject(path, configs, activity);
            //然后读取config.txt来创建项目
            Project p = Method.getDefaultProject(path);
            //设置为全局变量
            d.setmProject(p);
            //设置其为false，不是第一次启动
            editor.putBoolean("isFirst", false);
            editor.commit();
        }
    }


    public static Project getDefaultProject(String path) {

        File config = new File(path + "/" + "default", "config.txt");
        Project p = null;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(config));
            p = (Project) in.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return p;
    }

    public static void savelastProject(Project p, Activity activity) {
        ObjectOutputStream oos = null;
        File file = new File(activity.getFilesDir(), "last.txt");
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            p.setmLastTime(Method.getCurrentTime());
            oos.writeObject(p);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void setLastProject(Activity mainActivity) {
        Data d = (Data) mainActivity.getApplication();
        //先检测是否存在
        File file = new File(mainActivity.getFilesDir(), "last.txt");
        if (file.exists()) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                fis = new FileInputStream(file);
                //读取对象
                ois = new ObjectInputStream(fis);
                Project p = (Project) ois.readObject();
                //设置为全局变量
                d.setmProject(p);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (ois != null)
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    public static Project getLastProject(Activity mainActivity) {
        Project p = null;
        //先检测是否存在
        File file = new File(mainActivity.getFilesDir(), "last.txt");
        if (file.exists()) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                fis = new FileInputStream(file);
                //读取对象
                ois = new ObjectInputStream(fis);
                p = (Project) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (ois != null)
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return p;
    }

    public static double degreeToRadian(double degree) {
        return (degree * Math.PI) / 180.0d;
    }
}
