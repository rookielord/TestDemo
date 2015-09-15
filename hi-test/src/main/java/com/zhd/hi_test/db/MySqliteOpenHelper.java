package com.zhd.hi_test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 2015032501 on 2015/9/8.
 * 创建数据库
 */
public class MySqliteOpenHelper extends SQLiteOpenHelper {
    private static final String mDBname="Point.db";
    public MySqliteOpenHelper(Context context) {
        super(context, mDBname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
