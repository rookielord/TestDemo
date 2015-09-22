package com.zhd.hi_test.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.adapter.ProjectAdapter;
import com.zhd.hi_test.callback.IProject;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.module.Project;
import com.zhd.hi_test.util.Method;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 2015032501 on 2015/9/7.
 * 对项目文件进行操作，通过全局变量获得路径和当前选中项目
 */
public class ProjectListActivity extends Activity {
    private ListView mlv;
    private String mPath;
    private List<Project> mProjects;
    private boolean mHasProject;
    private String[] mConfigs = new String[4];
    private Project mProject;
    private TextView tv_proinfo;
    private ProjectAdapter pa;
    private Data d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        d = (Data) getApplication();
        mPath = d.getmPath();
        mProject = d.getmProject();
        mlv = (ListView) findViewById(R.id.lv);
        tv_proinfo = (TextView) findViewById(R.id.tv_proinfo);
        //获得Project目录下所有的Project文件对象
        mHasProject = HasProject(mPath);
        //如果已经打开项目则获取并显示
        if (mProject != null)
            showProjectInfo(mProject);
        if (mHasProject) {
            mProjects = getProjectInstance(mPath);
            //将对象传给适配器，然后对item内容进行填充
            pa = new ProjectAdapter(mProjects, getApplicationContext());
            pa.setmP(new IProject() {
                @Override
                public void getItemPosition(int position) {
                    mProject = mProjects.get(position);
                    showProjectInfo(mProject);
                }
            });
            //第三步通过适配器，将项目显示到ListView上
            mlv.setAdapter(pa);
        } else {
            Toast.makeText(ProjectListActivity.this, "当前没有项目", Toast.LENGTH_LONG).show();
        }
    }


    private void showProjectInfo(Project project) {
        String msg =
                "项目的名称:\t" + project.getmName() + "\t\n" +
                        "项目的对应表格:\t" + project.getmTableName() + "\t\n" +
                        "项目的创建时间:\t" + project.getmTime() + "\t\n" +
                        "行最近使用时间:\t" + project.getmLastTime();
        tv_proinfo.setText(msg);
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

    //第二步根据路径来获取对应的配置文件来创建Project对象，并填充进集合
    private List<Project> getProjectInstance(String path) {
        List<Project> projectList = new ArrayList<Project>();
        //对应路径下的config.txt文件进行读取，并创建project对象
        String[] proPaths = new File(path).list();//这个只会得到对应的文件夹名，没有路径
        for (String proPath : proPaths) {
            //创建当前目录的File对象，用来创建Project对象
            File pro_file = new File(path + "/" + proPath);
            //拼接config.txt路径
            File config = new File(path + "/" + proPath, "config.txt");
            //读取内容，拼接字符串
            FileInputStream in = null;
            StringBuilder sb = new StringBuilder();
            String msg = "";
            try {
                //设置文件读取流
                in = new FileInputStream(config);
                //根据BufferdReader来读取
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                //读取内容
                while ((msg = br.readLine()) != null) {
                    sb.append(msg);
                }
                //读取完后进行分割
                String[] messges = sb.toString().split(";");
                //创建project对象
                Project p = new Project(messges[0], messges[1], messges[2], messges[3], messges[4], pro_file);
                projectList.add(p);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
                view = inflater.inflate(R.layout.dialog_sign, null);
                final View finalView = view;
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText et1 = (EditText) finalView.findViewById(R.id.et_pro_name);
                                String pro_name = et1.getText().toString();
                                boolean isRight = Method.checkMsg(pro_name);
                                if (isRight) {
                                    mConfigs[0] = pro_name;//获得项目名称
                                } else {
                                    Toast.makeText(ProjectListActivity.this, "请输入正确的项目名称", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                EditText et2 = (EditText) finalView.findViewById(R.id.et_pro_back);
                                String pro_back = et2.getText().toString();
                                mConfigs[1] = pro_back;//项目备注
                                String add_time = Method.getCurrentTime();
                                mConfigs[2] = add_time;//添加时间
                                mConfigs[3] = add_time;//第一次创建的时间就是最近的打开时间
                                //创建文件夹，写入config.txt配置文件,如果写在外面会先执行这个,在点击item的时候就会执行
                                //而我必须在点击确定后才能执行这段代码，所以在外面执行全为空
                                //获得添加的项目对象
                                boolean res = Method.createProject(mPath, mConfigs, getApplicationContext());
                                if (res) {
                                    Toast.makeText(ProjectListActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                    //刷新
                                    refresh();
                                    //全局变量中
                                    d.setmProject(mProject);
                                    mProject = null;
                                } else
                                    Toast.makeText(ProjectListActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                            }
                        })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
        dialog.show();
        break;
        case R.id.item_delte:
        if (mProject != null) {
            deleteProject();
        } else {
            Toast.makeText(ProjectListActivity.this, "请选择项目", Toast.LENGTH_SHORT).show();
        }
        break;
    }

    return super.

    onOptionsItemSelected(item);

}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                d.setmProject(mProject);
                Toast.makeText(this, "打开成功", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (mProject!=null)
                deleteProject();
                else
                Toast.makeText(this,"请选择项目",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteProject() {
        //删除表
        Curd curd = new Curd(mProject.getmTableName(), getApplicationContext());
        curd.dropTable(mProject.getmTableName());
        //删除项目文件
        File file = new File(mPath + "/" + mProject.getmName());
        Method.deleteDirectory(file);
        //提示
        Toast.makeText(ProjectListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
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
