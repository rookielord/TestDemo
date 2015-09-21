package com.zhd.hi_test.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.zhd.hi_test.R;
import com.zhd.hi_test.activity.ProjectListActivity;
import com.zhd.hi_test.callback.IProject;
import com.zhd.hi_test.module.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 2015032501 on 2015/9/7.
 *
 */
public class ProjectAdapter extends BaseAdapter {
    //回调函数来获取选中
    private IProject mP;

    private OnClickListener mClick;

    public void setmP(IProject mP) {
        this.mP = mP;
    }

    //闯过来所有Project对象
    private List<Project> mProjects;
    private Context mContext;
    private static String TAG = "ProjectAdapter";
    //用来存放所有的Radio状态
    Map<String, Boolean> states = new HashMap<String, Boolean>();

    public ProjectAdapter(List<Project> Projects, Context context) {
        this.mProjects = Projects;
        this.mContext = context;
    }

    //定义一个Viewholder,用来存放layout上面的控件对象
    class ViewHolder {
        TextView pro_name, pro_back, pro_time;
        RadioButton radio;
    }

    @Override
    public int getCount() {
        return mProjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mProjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //获得填充数据
        Project p = mProjects.get(position);
        ViewHolder holder;
        final int index=position;
        //获得布局填充器
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //就是缓存view被销毁
        if (convertView == null) {
            //使用内容填充器填充对象
            convertView = inflater.inflate(R.layout.pro_item, null);
            //创建holder对象
            holder = new ViewHolder();
            //将layout上面的控件给holder中的控件属性
            holder.pro_name = (TextView) convertView.findViewById(R.id.tv_proname);
            holder.pro_back = (TextView) convertView.findViewById(R.id.tv_proback);
            holder.pro_time = (TextView) convertView.findViewById(R.id.tv_protime);
            //设置给Tag属性
            convertView.setTag(holder);
        } else {
            //如果缓存对象没有被消灭就获取
            holder = (ViewHolder) convertView.getTag();
        }
        //设置Holder里面的内容,因为没有被销毁，所以控件内容都还在
        holder.pro_name.setText(p.getmName());
        holder.pro_back.setText(p.getmBackup());
        holder.pro_time.setText(p.getmTime());
        //获取RadioButton对象,在外面获取
        final RadioButton radio = (RadioButton) convertView.findViewById(R.id.rb);
        //给Holder中的radio附上对象
        holder.radio = radio;
        //这里是将选中的view获得到
        final View finalConvertView = convertView;
        //这里将所有radioButton的状态赋值为false,在点击事件开始之前下面已经将states赋值
        //对radio控件实施监听，点击后会点击后，全部设置为false
        mClick=new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String key : states.keySet()) {//点击radioButton按钮时会将触发事件，先将所有的states设为false
                    states.put(key,false);
                }
                states.put(String.valueOf(index), true);//将目前选中的radioButton赋值给states
                mP.getItemPosition(index);
                ProjectAdapter.this.notifyDataSetChanged();
            }
        };

        holder.radio.setOnClickListener(mClick);

        //第一次将所有的states状态都设为false，以后则会将选中的和未被选中的一起拿进去
        boolean res = false;
        if (states.get(String.valueOf(position)) == null
                || states.get(String.valueOf(position)) == false) {//判断条件，当前position位置的states不存在或者为false
            res = false;
            //就向其中添加<position,false>
            states.put(String.valueOf(position), false);
        } else
            res = true;//如果为里面有选中的则一直为true,第一次肯定是都不选中的
        holder.radio.setChecked(res);
        convertView.setOnClickListener(mClick);
        //这里是这里是根据convertView来创建的菜单
        convertView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("项目操作");
                menu.add(0, 0, 0, "打开项目");
                menu.add(0, 1, 0, "删除项目");
            }
        });
        return convertView;
    }

}
