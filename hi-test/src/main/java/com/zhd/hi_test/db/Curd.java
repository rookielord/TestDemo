package com.zhd.hi_test.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by 2015032501 on 2015/9/8.
 */
public class Curd {
    private String mTablename;
    private MySqliteOpenHelper mSQoh;
    private SQLiteDatabase mDb;

    public Curd(String tablename, Context context) {
        mTablename = tablename;
        mSQoh = new MySqliteOpenHelper(context, 1);
        //获得可以操作的数据库对象
        mDb = mSQoh.getWritableDatabase();
    }

    //创建项目对应的表
    public void createTable(String Tablename) {
        String create_table = "CREATE TABLE " + Tablename + "( " +
                "id integer primary key autoincrement," +
                "name varchar(10) ," +
                "B varchar(32) ," +
                "L varchar(32) ," +
                "H varchar(32) ," +
                "N varchar(32) ," +
                "E varchar(32) ," +
                "Z varchar(32) ," +
                "time varchar(32)," +
                "type tinyint(1)," +
                "height char(5)," +
                "NRMS varchar(32)," +
                "ERMS varchar(32)," +
                "ZRMS varchar(32)," +
                "DES text);";
        mDb.execSQL(create_table);
    }

    //删除项目时，需要读取其配置文件中的表名，先删除表然后再删除项目文件
    public void dropTable(String Tablename) {
        String drop_table = "DROP TABLE IF EXISTS " + Tablename;
        mDb.execSQL(drop_table);
    }

    //插入操作
    public boolean insertData(List<ContentValues> cvList) {
        long res;
        for (ContentValues cv : cvList) {
            res = mDb.insert(mTablename, null, cv);
            if (res == -1)
                return false;
        }
        return true;
    }

    //删除操作
    public Boolean deleteData(String id) {
        //SQLiteDatabase sd = mSQoh.getWritableDatabase();
        int res = mDb.delete(mTablename, "id=?", new String[]{id});
        if (res != 1) {
            return false;
        }
        return true;
    }

    //修改操作
    public Boolean UpdateData(String id, ContentValues cv) {
//        SQLiteDatabase sd = mSQoh.getWritableDatabase();
        int res = mDb.update(mTablename, cv, "id=?", new String[]{id});
        if (res == 0) {
            return false;
        } else {
            return true;
        }
    }

    //查询操作
    public Cursor queryData(String[] columns, String selection,
                            String[] selectionArgs, String groupBy, String having,
                            String orderBy) {
        SQLiteDatabase sd = mSQoh.getReadableDatabase();
        Cursor cursor = sd.query(mTablename, columns, selection, selectionArgs,
                groupBy, having, orderBy);
        return cursor;
    }
    public Cursor queryData(String[] columns,String group) {
        SQLiteDatabase sd = mSQoh.getReadableDatabase();
        Cursor cursor = sd.query(mTablename,columns,null,null,null,null,group);
        return cursor;
    }
}
