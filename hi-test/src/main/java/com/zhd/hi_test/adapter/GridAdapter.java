package com.zhd.hi_test.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhd.hi_test.R;
import com.zhd.hi_test.callback.IiconCallback;
import com.zhd.hi_test.module.Icon;

import java.util.ArrayList;

/**
 * Created by 2015032501 on 2015/9/19.
 * 在这里根据传过来的ID来进行其它九宫图的各个界面的填充，然后使用回调来对各个Item功能实现
 */
public class GridAdapter extends BaseAdapter {
    //设置所有的显示数据


    private ArrayList<Icon> mSource = new ArrayList<>();
    private Context mContext;
    private IiconCallback mIicon;

    public void setmIicon(IiconCallback mIicon) {
        this.mIicon = mIicon;
    }

    /**
     * 根据传过来的PageNum来生成对应的资源变量
     *
     * @return
     */
    public GridAdapter(Context context, ArrayList<Icon> resource) {
        this.mContext = context;
        this.mSource = resource;
    }

    class ViewHolder {
        TextView itemText;
        ImageView itemImage;
    }


    @Override
    public int getCount() {
        return mSource.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Icon icon = mSource.get(position);
        ViewHolder holder;
        final int index = position;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.itemImage = (ImageView) convertView.findViewById(R.id.iv_itemimage);
            holder.itemText = (TextView) convertView.findViewById(R.id.tv_itemtext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemImage.setImageResource(icon.getIcon_image_soure());
        holder.itemText.setText(icon.getIcon_name());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIicon.Onclick(icon);
            }
        });
        return convertView;
    }

}
