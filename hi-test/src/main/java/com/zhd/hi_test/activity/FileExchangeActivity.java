package com.zhd.hi_test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.FileAdapter;
import com.zhd.hi_test.interfaces.OnFileListener;
import com.zhd.hi_test.module.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 2015032501 on 2015/10/16.
 * 1.首先跳转到当前目录下，遍历文件和文件夹
 * 2.每次跳转都会显示当前的路径
 */
public class FileExchangeActivity extends Activity {

    //控件
    Spinner sp_out_style;
    TextView tv_current_path;
    EditText et_out_name;
    Button btn_confirm;
    GridView gv_file_list;
    //当前的路径
    private String mCurpath;
    //用于填充gridview的数据源
    private List<MyFile> mfiles = new ArrayList<>();
    //数据的填充器
    FileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_exchange);
        init();
        mAdapter=new FileAdapter(this,mfiles);
        //实现回调
        mAdapter.setmListener(new OnFileListener() {
            @Override
            public void onFileSelected(MyFile file) {
                mCurpath=file.getPath();
                mfiles.clear();
                getAll(mCurpath);
                mAdapter.notifyDataSetChanged();
            }
        });
        gv_file_list.setAdapter(mAdapter);
    }

    private void init() {
        sp_out_style = (Spinner) findViewById(R.id.sp_out_style);
        tv_current_path = (TextView) findViewById(R.id.tv_current_path);
        et_out_name = (EditText) findViewById(R.id.out_name);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        gv_file_list = (GridView) findViewById(R.id.gv_file_list);
        //将当前的路径显示出来
        mCurpath = Const.getmPath() + "/" + Const.getmProject().getmName();
        getAll(mCurpath);
    }

    /**
     * 获取当前目录下的所有文件和文件夹并将其填充进FileAdapter中
     *
     * @param mCurpath
     */
    private void getAll(String mCurpath) {
        //将当前的路径赋值给textview
        tv_current_path.setText(mCurpath);
        //首先添加向上跳转的按钮,即截取
        addPrevious();
        //当前目录下的文件的名称
        String[] documents = new File(mCurpath).list();
        //当前的文档对象
        File curFile = null;
        //判断属性，添加进List
        for (String name : documents) {
            curFile = new File(mCurpath+"/"+name);
            if (curFile.isDirectory())
                mfiles.add(new MyFile(curFile.getName(), R.mipmap.ic_file_documents, curFile.getPath()));
            if (curFile.isFile())
                mfiles.add(new MyFile(curFile.getName(), R.mipmap.ic_file_floppy, curFile.getPath()));
        }
        //创建File
    }

    /**
     * 查询/最后一次出现的位置
     */
    private void addPrevious() {
        //1.判断“/”最后出现的位置，如果是第一位，就不会添加
        int index = mCurpath.lastIndexOf("/");
        if (index != 0) {
            String previous = mCurpath.substring(0, index);
            mfiles.add(new MyFile("..", R.mipmap.ic_file_documents, previous));
        }
    }
}
