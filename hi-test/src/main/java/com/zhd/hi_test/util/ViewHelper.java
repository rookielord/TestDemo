package com.zhd.hi_test.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by 2015032501 on 2015/10/20.
 * 获取代码版本号
 */
public class ViewHelper {
    public static String getVersion(Context context) {
        //首先获得PackageManager
        PackageManager pm = context.getPackageManager();
        //获得当前包名信息
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
