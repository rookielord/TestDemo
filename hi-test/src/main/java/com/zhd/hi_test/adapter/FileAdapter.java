package com.zhd.hi_test.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.OnFileListener;
import com.zhd.hi_test.interfaces.OnIconChangeListener;
import com.zhd.hi_test.module.Icon;
import com.zhd.hi_test.module.MyFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/9/19.
 * 这是根据传过来的MyFile文件进行填充
 */
public class FileAdapter extends BaseAdapter {
    //设置所有的显示数据
    private List<MyFile> mSource = new ArrayList<>();
    private Context mContext;
    private OnFileListener mListener;

    public void setmListener(OnFileListener mListener) {
        this.mListener = mListener;
    }

    public FileAdapter(Context context, List<MyFile> resource) {
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
        final MyFile file = mSource.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.itemImage = (ImageView) convertView.findViewById(R.id.iv_itemimage);
            holder.itemText = (TextView) convertView.findViewById(R.id.tv_itemtext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemImage.setImageResource(file.getImage_soure());
        holder.itemText.setText(file.getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFileSelected(file);
            }
        });
        return convertView;
    }

}
