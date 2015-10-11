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


import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.ProjectAdapter;
import com.zhd.hi_test.callback.OnProjectListener;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.module.Project;
import com.zhd.hi_test.util.Method;

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
    TextView tv_name, tv_coordinate, tv_time, tv_lasttime;

    //加载过来的项目，判断是否有项目
    private String mPath;
    private List<Project> mProjects;
    private boolean mHasProject;

    //当前选中的项目，是用来显示当前选中的project的
    private Project mProject;
    private ProjectAdapter mpa;
    private Data d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        //首先查看是否当前项目已经打开
        d = (Data) getApplication();
        //全局路径
        mPath = d.getmPath();
        mProject = d.getmProject();
        //找控件
        lv = (ListView) findViewById(R.id.lv);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_coordinate = (TextView) findViewById(R.id.tv_coordinate);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_lasttime = (TextView) findViewById(R.id.tv_lasttime);
        //获得Project目录下所有的Project文件对象
        mHasProject = HasProject(mPath);
        //如果已经打开项目则获取并显示
        if (mProject != null)
            showProjectInfo(mProject);
        //判断是否有项目内容
        if (mHasProject) {
            //获得当前的Project目录下所有的project对象
            mProjects = getProjectInstance(mPath);
            //将对象传给适配器，然后对item内容进行填充,只要点击了就会将当前项目的信息显示
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
            Toast.makeText(ProjectActivity.this, "当前没有项目", Toast.LENGTH_LONG).show();
        }
    }

    private void showProjectInfo(Project project) {
        tv_name.setText(project.getmName());
        tv_coordinate.setText(project.getmCoordinate());
        tv_time.setText(project.getmTime());
        tv_lasttime.setText(project.getmLastTime());
    }

    //第一步查看是否有新建项目
    private boolean HasProject(String mPath) {
        File files = new File(mPath);
        //将project对象中的配置文件进行读取，并创建Project对象
        File[] contents = files.listFiles();
        if (contents.length > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 第二步根据路径来获取对应的配置文件来创建Project对象，并填充进集合
     *
     * @param path
     * @return
     */
    private List<Project> getProjectInstance(String path) {
        List<Project> projectList = new ArrayList<Project>();
        //对应路径下的config.txt文件进行读取，并创建project对象
        String[] proPaths = new File(path).list();//这个只会得到对应的文件夹名，没有路径
        for (String proName : proPaths) {
            //拼接config.txt路径
            File config = new File(path + "/" + proName, "config.txt");
            //读取存入的project对象
            ObjectInputStream in = null;
            try {
                //设置文件读取流
                in = new ObjectInputStream(new FileInputStream(config));
                Project p = (Project) in.readObject();
                projectList.add(p);
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
        }
        return projectList;
    }

    //这里使用menu来进行新建,创建Menu菜单项目
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //点击创建后出现的menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //直接在这里创建而不去调用自定义的Custom
        switch (id) {
            case R.id.item_create:
                LayoutInflater inflater = getLayoutInflater();
                //这里实现创建项目,使用dialog来创建
                View view = null;
                view = inflater.inflate(R.layout.project_dialog, null);
                //填充Spinner的数据
                final Spinner sp = (Spinner) view.findViewById(R.id.sp_coordinate);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.coordinate));
                sp.setAdapter(adapter);
                final View finalView = view;
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText et1 = (EditText) finalView.findViewById(R.id.et_pro_name);
                                String pro_name = et1.getText().toString();
                                boolean isRight = Method.checkMsg(pro_name);
                                String[] mConfigs = new String[5];
                                if (isRight) {
                                    mConfigs[0] = pro_name;//获得项目名称
                                } else {
                                    Toast.makeText(ProjectActivity.this, "请输入正确的项目名称", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                EditText et2 = (EditText) finalView.findViewById(R.id.et_pro_back);
                                String pro_back = et2.getText().toString();
                                mConfigs[1] = pro_back;//项目备注
                                String add_time = Method.getCurrentTime();
                                mConfigs[2] = add_time;//添加时间
                                mConfigs[3] = add_time;//第一次创建的时间就是最近的打开时间
                                //获得其中的坐标系统
                                mConfigs[4] = String.valueOf(sp.getSelectedItem().toString());
                                boolean res = Method.createProject(mPath, mConfigs, ProjectActivity.this);
                                if (res) {
                                    Toast.makeText(ProjectActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                    //刷新
                                    refresh();
                                    //创建的时候会赋给全局变量
                                } else
                                    Toast.makeText(ProjectActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.show();
                break;
            case R.id.item_delte:
                if (mProject != null) {
                    deleteProject();
                } else {
                    Toast.makeText(ProjectActivity.this, "请选择项目", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                d.setmProject(mProject);
                Toast.makeText(this, "打开成功", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (mProject != null)
                    deleteProject();
                else
                    Toast.makeText(this, "请选择项目", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteProject() {
        //删除前相对比全局变量的project是否是删除的project
        if (mProject.equals(d.getmProject())){
            d.setmProject(null);
        }
        //删除表
        Curd curd = new Curd(mProject.getmTableName(), this);
        curd.dropTable(mProject.getmTableName());
        //删除项目文件
        File file = new File(mPath + "/" + mProject.getmName());
        Method.deleteDirectory(file);
        //提示
        Toast.makeText(ProjectActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
        //这里刷新只会重读ProjectAdapter，但Adapter中projects对象的数量没有变化，可以在这里对
        //删除Adapter里面的mProjects数据
        refresh();
        mProject = null;
    }

    //重新刷新整个Activity来获取文件列表
    public void refresh() {
        onCreate(null);
    }

}
