package com.zhd.hi_test.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.ProjectAdapter;
import com.zhd.hi_test.interfaces.OnProjectListener;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.module.MyProject;
import com.zhd.hi_test.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 2015032501 on 2015/9/7.
 * 对项目文件进行操作，通过全局变量获得路径和当前选中项目
 */
public class ProjectActivity extends Activity {
    //控件
    ListView lv;
    TextView tv_name, tv_coordinate, tv_guass, tv_lasttime;

    //加载过来的项目，判断是否有项目
    private String mPath;
    private List<MyProject> mProjects;
    private boolean mHasProject;

    //当前选中的项目，是用来显示当前选中的project的
    private MyProject mProject;
    private ProjectAdapter mpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        //首先查看是否当前项目已经打开
        //全局路径
        mPath = Const.getPath();
        mProject = Const.getmProject();
        //找控件：1.进行填充的listview
        lv = (ListView) findViewById(R.id.lv);
        //进行项目信息显示
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_coordinate = (TextView) findViewById(R.id.tv_coordinate);
        tv_guass = (TextView) findViewById(R.id.tv_guass);
        tv_lasttime = (TextView) findViewById(R.id.tv_lasttime);
        //获得Project目录下所有的Project文件对象
        mHasProject = HasProject(mPath);
        //如果已经打开项目则获取并显示
        if (mProject != null)
            showProjectInfo(mProject);
        //判断是否有项目内容
        if (mHasProject) {
            //获得当前的Project目录下所有的project对象
            mProjects = FileUtil.getProjectInstance(mPath);
            if (mProject != null) {//当没有上一次project的情况
                int loc = FileUtil.getDefaultProLoc(mProjects);
                mProjects.remove(loc);
                mProjects.add(0, mProject);
            }
            //将对象传给适配器，然后对item内容进行填充,只要点击了就会将当前项目的信息显示,当点击后就将当前项目显示到上面去
            mpa = new ProjectAdapter(mProjects, this);
            mpa.setmP(new OnProjectListener() {
                @Override
                public void getItemPosition(int position) {
                    mProject = mProjects.get(position);
                    showProjectInfo(mProject);
                }
            });
            //第三步通过适配器，将项目显示到ListView上
            lv.setAdapter(mpa);
        } else {
            Toast.makeText(ProjectActivity.this, R.string.project_none, Toast.LENGTH_SHORT).show();
        }
        //进行数据库中脏数据表删除
        Curd curd = new Curd("sqlite_master", this);
        curd.removeDirtyTable(mProjects);
    }

    private void showProjectInfo(MyProject myProject) {
        tv_name.setText(myProject.getmName());
        tv_coordinate.setText(myProject.getmCoordinate());
        tv_guass.setText(myProject.getmGuass());
        tv_lasttime.setText(myProject.getmLastTime());
    }

    //第一步查看是否有新建项目
    private boolean HasProject(String mPath) {
        File files = new File(mPath);
        //将project对象中的配置文件进行读取，并创建Project对象
        File[] contents = files.listFiles();
        return contents.length > 0;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //直接在这里创建而不去调用自定义的Custom
        switch (id) {
            case R.id.item_create:
                LayoutInflater inflater = getLayoutInflater();
                //这里实现创建项目,使用dialog来创建
                View view;
                view = inflater.inflate(R.layout.project_dialog, null);
                //填充坐标系和高斯投影带数据
                final Spinner sp_coordinate = (Spinner) view.findViewById(R.id.sp_coordinate);
                final Spinner sp_guass = (Spinner) view.findViewById(R.id.sp_guass);
                ArrayAdapter<String> coordinate_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        getResources().getStringArray(R.array.coordinate));
                ArrayAdapter<String> guass_adpter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        getResources().getStringArray(R.array.guass));
                coordinate_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                guass_adpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_coordinate.setAdapter(coordinate_adapter);
                sp_guass.setAdapter(guass_adpter);
                final View finalView = view;
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText et1 = (EditText) finalView.findViewById(R.id.et_pro_name);
                                String pro_name = et1.getText().toString();
                                boolean isRight = FileUtil.checkMsg(pro_name, mPath, 1);
                                String[] mConfigs = new String[6];
                                if (isRight) {
                                    mConfigs[0] = pro_name;//获得项目名称
                                } else {
                                    Toast.makeText(ProjectActivity.this, R.string.proname_hint, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                EditText et2 = (EditText) finalView.findViewById(R.id.et_pro_back);
                                String pro_back = et2.getText().toString();
                                mConfigs[1] = pro_back;//项目备注
                                String add_time = FileUtil.getCurrentTime();
                                mConfigs[2] = add_time;//添加时间
                                mConfigs[3] = add_time;//第一次创建的时间就是最近的打开时间
                                //获得其中的坐标系统
                                mConfigs[4] = String.valueOf(sp_coordinate.getSelectedItem().toString());
                                //获取其中的高斯带数
                                mConfigs[5] = String.valueOf(sp_guass.getSelectedItem().toString());
                                boolean res = FileUtil.createProject(mPath, mConfigs, ProjectActivity.this);
                                if (res) {
                                    Toast.makeText(ProjectActivity.this, R.string.create_success, Toast.LENGTH_SHORT).show();
                                    //刷新
                                    refresh();
                                    //创建的时候会赋给全局变量
                                } else
                                    Toast.makeText(ProjectActivity.this, R.string.create_failure, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null).create();
                dialog.show();
                break;
            case R.id.item_delte:
                if (mProject != null) {
                    //OptionItem进行删除，会删除当前radioButton选中的
                    deleteProject();
                } else {
                    Toast.makeText(ProjectActivity.this, R.string.select_project, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                //如果当前有打开项目，则更新最后的时间
                if (Const.getmProject() != null) {
                    FileUtil.updateProject(Const.getmProject());
                }
                Const.setProject(mProject);
                Toast.makeText(this, R.string.project_open_success, Toast.LENGTH_SHORT).show();
                refresh();
                break;
            case 1:
                if (mProject != null)
                    deleteProject();
                else
                    Toast.makeText(this, R.string.select_project, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteProject() {
        //注意：关闭后打开得到的Application中的project对象和当前选中的mProject对象是不一样的，只能根据名称进行判断
        //如果名称相同则当前项目为空
        if (Const.getmProject() != null) {
            if (Const.getmProject().getmName().equals(mProject.getmName())) {
                Toast.makeText(this, R.string.delete_warning, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        MyProject p = FileUtil.getLastProject(this);
        if (p != null) { //如果当前删除项目是最后一个打开项目
            if (p.getmName().equals(mProject.getmName())) {
                File file = new File(this.getFilesDir(), "last.txt");
                file.delete();
            }
        }
        //删除表
        Curd curd = new Curd(mProject.getmTableName(), this);
        curd.dropTable(mProject.getmTableName());
        //删除项目文件
        File file = new File(mPath + "/" + mProject.getmName());
        FileUtil.deleteDirectory(file);
        //提示
        Toast.makeText(ProjectActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
        //这里刷新只会重读ProjectAdapter，但Adapter中projects对象的数量没有变化，可以在这里对
        //删除Adapter里面的mProjects数据
        refresh();
        mProject = null;
    }

    //重新刷新整个Activity来获取文件列表
    public void refresh() {
//        onCreate(null);
        mProjects = FileUtil.getProjectInstance(mPath);
        int loc = FileUtil.getDefaultProLoc(mProjects);
        mProjects.remove(loc);
        mProjects.add(0, Const.getmProject());
        mpa.setmMyProjects(mProjects);
        showProjectInfo(Const.getmProject());
        mProject = Const.getmProject();
    }

    /**
     * 当程序退出时，将当前项目的打开时间进行跟新
     * 将当前项目对象写入一个文件中，表示上次打开的内容
     * 重新写入程序
     */
    @Override
    protected void onDestroy() {
        MyProject p = Const.getmProject();
        if (p != null) {
            FileUtil.updateProject(p);
            FileUtil.savelastProject(p, this);
        }
        //将全局变量project清空
        super.onDestroy();
    }

}
