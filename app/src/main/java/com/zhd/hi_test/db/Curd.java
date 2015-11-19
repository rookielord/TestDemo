package com.zhd.hi_test.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhd.hi_test.module.MyProject;

import java.util.ArrayList;
import java.util.List;


public class Curd {
    private String mTablename;
    private SQLiteDatabase mWB, mRB;
    private Cursor mCursor;
    private MySqliteOpenHelper mSQhelper;

    public Curd(String tablename, Context context) {
        mTablename = tablename;
        mSQhelper = new MySqliteOpenHelper(context, 1);
        //获得可以操作的数据库对象
        mWB = mSQhelper.getWritableDatabase();
        mRB = mSQhelper.getReadableDatabase();
    }

    //创建项目对应的表
    public void createTable(String tablename) {
        String create_table = "CREATE TABLE " + tablename + "( " +
                "id integer primary key," +
                "name varchar(32)," +
                "B varchar(32) ," +
                "L varchar(32) ," +
                "H varchar(20) ," +
                "N varchar(32) ," +
                "E varchar(32) ," +
                "Z varchar(20) ," +
                "time varchar(32)," +
                "type tinyint(1)," +
                "DireB char(1)," +
                "DireL char(1)," +
                "height float(10)," +
                "DES text);";
        mWB.execSQL(create_table);
    }

    //删除项目时，需要读取其配置文件中的表名，先删除表然后再删除项目文件
    public void dropTable(String tablename) {
        String drop_table = "DROP TABLE IF EXISTS " + tablename;
        mWB.execSQL(drop_table);
    }

    //插入操作
    public boolean insertData(List<ContentValues> cvList) {
        long res;
        for (ContentValues cv : cvList) {
            res = mWB.insert(mTablename, null, cv);
            if (res == -1)
                return false;
        }
        return true;
    }

    //删除操作
    public Boolean deleteData(String id) {
        int res = mWB.delete(mTablename, "id=?", new String[]{id});
        return res != 0;
    }

    //修改操作
    public Boolean UpdateData(String id, ContentValues cv) {
        int res = mWB.update(mTablename, cv, "id=?", new String[]{id});
        return res != 0;
    }

    //查询操作，涉及全部的操作
    public Cursor queryData(String[] columns, String selection,
                            String[] selectionArgs, String groupBy, String having,
                            String orderBy) {
        mCursor = mRB.query(mTablename, columns, selection, selectionArgs,
                groupBy, having, orderBy);
        return mCursor;
    }

    //添加order_by
    public Cursor queryData(String[] columns, String orderby) {
        mCursor = mRB.query(mTablename, columns, null, null, null, null, orderby, null);
        return mCursor;
    }

    //添加where条件
    public Cursor queryData(String[] columns, String selection,
                            String[] selectionArgs) {
        mCursor = mRB.query(mTablename, columns, selection, selectionArgs, null, null, null, null);
        return mCursor;
    }

    //直接查询字段
    public Cursor queryData(String[] columns) {
        mCursor = mRB.query(mTablename, columns, null, null, null, null, null, null);
        return mCursor;
    }

    //获得所有的id的最大的值,考虑其中没有值的情况
    public int getLastID() {
        int str_id = 0;
        int row = 0;
        //查询其中的数量
        mCursor = mRB.rawQuery("select count(*) as num from " + mTablename, null);
        if (mCursor.moveToFirst())
            row = mCursor.getInt(0);
        if (row != 0) {
            mCursor = mRB.rawQuery("select max(id) from " + mTablename, null);
            if (mCursor.moveToFirst()) {
                str_id = mCursor.getInt(0);
            }
            mCursor.close();
        }
        return str_id;
    }

    /**
     * 1.获取当前已有项目的表名，如果为空则所有的表都需要删除
     * 2.将数据中的所有project%表获得
     * 3.如果
     */
    /**
     * 读取已建项目中的表名，并和
     *
     * @param projects
     */
    public void removeDirtyTable(List<MyProject> projects) {
        List<String> delete_table = new ArrayList<>();//删除的表
        List<String> tables = new ArrayList<>();//项目中的表
        for (MyProject p : projects) {
            tables.add(p.getTableName());
        }
//        使用API进行查询的时候会把关键字给替换掉，所以反而不用加''来取消关键字
        mCursor = this.queryData(new String[]{"*"}, " name like ? and type=?", new String[]{"project%", "table"});
        if (mCursor.moveToFirst())
            do {
                String table_name = mCursor.getString(mCursor.getColumnIndex("name"));
                if (!(tables.contains(table_name)))
                    delete_table.add(table_name);
            }
            while (mCursor.moveToNext());
        mCursor.close();
        if (delete_table.size() > 0) {
            for (String table : delete_table) {
                dropTable(table);
            }
        }
    }
}
