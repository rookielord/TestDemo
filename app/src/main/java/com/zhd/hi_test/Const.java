package com.zhd.hi_test;



import com.zhd.hi_test.interfaces.Connectable;
import com.zhd.hi_test.module.ConnectInfo;
import com.zhd.hi_test.module.ConnectManager;
import com.zhd.hi_test.module.MyProject;


/**
 * Created by 2015032501 on 2015/9/18.
 * 全局变量
 * 包含项目对象Project
 * 包含ZHD_TEST路径
 */
public class Const {
    public static final int BlueToothConncet = 1;
    public static final int InnerGPSConnect = 2;
    public static final int NoneConnect = 0;
    public static final int TYPE_LOCATION = 1;
    public static final int TYPE_SATELLITE = 2;
    public static final int TYPE_DATE = 3;
    public static final int TYPE_PDOP=4;
    public static final int TYPE_CLEAR=5;
    public static final int TYPE_ADD =6;
    //判断弹出ProgressDialog
    public static final int TYPE_UPDATE = 5;
    public static final String PFNAME = "config";//SharePreference文件名
    public static final String ISUPDATA = "updata";//是否检查版本更新
    //当前打开的项目对象
    private static MyProject mProject;
    //HI_TEST的路径
    private static String mPath;
    private static ConnectManager mManager;
    private static Connectable mConnect;
    //判断当前是否有PDOP数据
    public static boolean HasPDOP = false;
    //是否有Data数据过来
    public static boolean HasDataInfo = false;
    public static float Height = 0;
    public static ConnectInfo Info = ConnectInfo.getInstance();

    public static Connectable getConnect() {
        return mConnect;
    }

    public static void setJoin(Connectable join) {
        Const.mConnect = join;
    }

    public static void setManager(ConnectManager manager) {
        Const.mManager = manager;
    }

    public static ConnectManager getManager() {
        return mManager;
    }

    public static MyProject getProject() {
        return mProject;
    }

    public static void setProject(MyProject mMyProject) {
        Const.mProject = mMyProject;
    }

    public static String getPath() {
        return mPath;
    }

    public static void setPath(String mPath) {
        Const.mPath = mPath;
    }
}
