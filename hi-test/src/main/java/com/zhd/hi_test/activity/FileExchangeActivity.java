package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.FileAdapter;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.interfaces.OnFileListener;
import com.zhd.hi_test.module.MyFile;
import com.zhd.hi_test.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 2015032501 on 2015/10/16.
 * 1.首先跳转到当前目录下，遍历文件和文件夹
 * 2.每次跳转都会显示当前的路径
 */
public class FileExchangeActivity extends Activity implements View.OnClickListener {

    //控件
    Spinner sp_out_style;
    TextView tv_current_path;
    EditText et_out_name;
    Button btn_confirm;
    GridView gv_file_list;
    //当前的路径
    private static File mCurfile;
    //用于填充gridview的数据源
    private List<MyFile> mfiles = new ArrayList<>();
    //数据的填充器
    FileAdapter mAdapter;
    //导出文件的名称
    private String mFileName;
    //点击自定义导出后，返回的requestCode
    private static final int CUSTOM_REQUEST = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_exchange);
        //判断是否打开项目
        if (Const.getmProject() == null) {
            Toast.makeText(this, "请打开项目", Toast.LENGTH_SHORT).show();
            return;
        }
        init();
        mAdapter = new FileAdapter(this, mfiles);
        //实现回调
        mAdapter.setmListener(new OnFileListener() {
            @Override
            public void onFileSelected(MyFile file) {
                mCurfile = file.getFile();
                getAll(mCurfile);
                mAdapter.notifyDataSetChanged();
            }
        });
        gv_file_list.setAdapter(mAdapter);
        btn_confirm.setOnClickListener(this);
    }

    private void init() {
        sp_out_style = (Spinner) findViewById(R.id.sp_out_style);
        String[] outtypes = getResources().getStringArray(R.array.outtype);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outtypes);
        //设置下拉菜单的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_out_style.setAdapter(adapter);
        tv_current_path = (TextView) findViewById(R.id.tv_current_path);
        et_out_name = (EditText) findViewById(R.id.out_name);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        gv_file_list = (GridView) findViewById(R.id.gv_file_list);
        //如果当前是文件则获取其所在目录，如果是文件夹则获取上级目录
        mCurfile = Const.getmProject().getmConfig().getParentFile();
        getAll(mCurfile);
    }

    /**
     * 获取当前目录下的所有文件和文件夹并将其填充进FileAdapter中
     *
     * @param curfile
     */
    private void getAll(File curfile) {
        //如果是文件的话，则不做任何操作
        if (curfile.isFile())
            return;
        mfiles.clear();
        //将当前的路径赋值给textview
        tv_current_path.setText(curfile.getPath());
        //首先添加向上跳转的按钮,即截取
        addPrevious();
        //当前目录下的文件的File对象
        File[] files = curfile.listFiles();
        //如果里面没有文件则返回
        if (files == null)
            return;
        //判断属性，添加进List
        for (File file : files) {
            if (file.isDirectory())
                mfiles.add(new MyFile(file.getName(), R.mipmap.ic_file_documents, file));
            if (file.isFile())
                mfiles.add(new MyFile(file.getName(), R.mipmap.ic_file_floppy, file));
        }
    }

    /**
     * 添加上级目录的跳转
     */
    private void addPrevious() {
        //1.判断“/”最后出现的位置，如果是第一位，就不会添加
        File previous = mCurfile.getParentFile();
        if (previous != null) {
            mfiles.add(new MyFile("..", R.mipmap.ic_file_documents, previous));
        }
    }

    /**
     * 点击后，获取文件名称是否符合规范，如果不符合则不执行
     * 符合则根据spinner的position来决定导出的类型
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                //通过导出格式来决定是否跳转
                mFileName = getName();
                if (mFileName == null)
                    return;
                getOutType(sp_out_style.getSelectedItemPosition());
                break;
        }
    }

    /**
     * 根据Spinner中的position来判断选中那种导出方式
     *
     * @param position
     */
    private void getOutType(int position) {
        Intent intent;
        switch (position) {
            case 0://自定义格式，跳转到自定义选择界面,这里只能传入路径，如果是传入file的话就代表已经生成了
                intent = new Intent("com.zhd.output.START");
                intent.putExtra("path", mCurfile.getPath() + "/" + mFileName + ".txt");
                startActivityForResult(intent, CUSTOM_REQUEST);
                break;
            case 1://南方cass格式
                OutPutCass();
                break;
        }
    }

    /**
     * 判断所输入的名字是否符合规范
     *
     * @return
     */
    private String getName() {
        String name = et_out_name.getText().toString();
        if (FileUtil.checkMsg(name, mCurfile.getPath(), 2)) {
            return name;
        }
        Toast.makeText(this, "请输入正确的项目名称", Toast.LENGTH_SHORT).show();
        return null;
    }

    /**
     * 导出CASS内容
     */
    private void OutPutCass() {
        try {
            Curd curd = new Curd(Const.getmProject().getmTableName(), this);
            Cursor cursor = curd.queryData(new String[]{"id,N,E,Z"});
            StringBuilder sb = new StringBuilder();
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    //拼接sb
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String N = cursor.getString(cursor.getColumnIndex("N"));
                    String E = cursor.getString(cursor.getColumnIndex("E"));
                    String Z = cursor.getString(cursor.getColumnIndex("Z"));
                    sb.append(id + ",," + N + "," + E + "," + Z + "\n");
                }
            }
            cursor.close();
            //写入当前的路径
            FileUtil.writeFileByString(mCurfile.getPath() + "/" + mFileName + ".txt", sb.toString());
            //刷新当前目录的内容
            Toast.makeText(this, "导出CASS成功", Toast.LENGTH_SHORT).show();
            getAll(mCurfile);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 如果自定义导出成功,则会刷新该界面
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CUSTOM_REQUEST:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    getAll(new File(path).getParentFile());
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
