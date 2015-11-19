package com.zhd.hi_test.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.OnProjectListener;
import com.zhd.hi_test.module.MyProject;

import java.util.List;

/**
 * Created by 2015032501 on 2015/9/7.
 */
public class ProjectAdapter extends BaseAdapter {
    //回调函数来获取选中
    private OnProjectListener mP;

    private View.OnLongClickListener mClick;

    private Context mContext;

    public void setmP(OnProjectListener mP) {
        this.mP = mP;
    }

    private List<MyProject> mProjects;

    //让当前项目打开项目的名称和列表中项目进行比较，然后选中项目背景变色

    public ProjectAdapter(List<MyProject> projects, Context context) {
        this.mProjects = projects;
        this.mContext = context;
    }

    public void setmProjects(List<MyProject> projects) {
        this.mProjects = projects;
        ProjectAdapter.this.notifyDataSetChanged();
    }

    //定义一个Viewholder,用来存放layout上面的控件对象
    class ViewHolder {
        TextView pro_name, pro_back, pro_addtime;
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
        MyProject p = mProjects.get(position);
        ViewHolder holder;
        final int index = position;
        //获得布局填充器
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //就是缓存view被销毁
        if (convertView == null) {
            //使用内容填充器填充对象
            convertView = inflater.inflate(R.layout.project_item, null);
            //创建holder对象
            holder = new ViewHolder();
            //将layout上面的控件给holder中的控件属性，需要赋值的属性
            holder.pro_name = (TextView) convertView.findViewById(R.id.tv_proname);
            holder.pro_back = (TextView) convertView.findViewById(R.id.tv_proback);
            holder.pro_addtime = (TextView) convertView.findViewById(R.id.tv_addtime);
            //设置给Tag属性
            convertView.setTag(holder);
        } else {
            //如果缓存对象没有被消灭就获取
            holder = (ViewHolder) convertView.getTag();
        }
        //设置Holder里面的内容,因为没有被销毁，所以控件内容都还在
        holder.pro_name.setText(p.getName());
        holder.pro_back.setText(p.getBackup());
        holder.pro_addtime.setText(p.getTime());
        //这里是将选中的view获得到
        //这里将所有radioButton的状态赋值为false,在点击事件开始之前下面已经将states赋值
        //对radio控件实施监听，点击后会点击后，全部设置为false

        mClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mP.onClick(index);
                ProjectAdapter.this.notifyDataSetChanged();
                return false;
            }
        };
//        holder.radio.setOnClickListener(mClick);
        //第一次将所有的states状态都设为false，以后则会将选中的和未被选中的一起拿进去
        //在这里进行判断当前的mProject是否等于convertview的project,但是在滑动的时候未销毁的convertview会影响
        convertView.setBackgroundColor(Color.TRANSPARENT);
        if (Const.getProject() != null) {
            if (p.getName().equals(Const.getProject().getName())) {
                convertView.setBackgroundResource(R.drawable.project_selected);
            }
        }
        convertView.setOnLongClickListener(mClick);
        //这里是这里是根据convertView来创建的菜单
        convertView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(R.string.project_conduct);
                menu.add(0, 0, 0, R.string.project_open);
                menu.add(0, 1, 0, R.string.project_delete);
                menu.add(0, 2, 0, R.string.project_show);
            }
        });
        return convertView;
    }

}
