package com.zhd.hi_test.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.OnIconChangeListener;
import com.zhd.hi_test.module.Icon;

import java.util.ArrayList;

/**
 * Created by 2015032501 on 2015/9/19.
 * 在这里根据穿过来的数据进行内容的填充
 */
public class IconAdapter extends BaseAdapter {
    //设置所有的显示数据
    private ArrayList<Icon> mSource = new ArrayList<>();
    private Context mContext;
    private OnIconChangeListener mIicon;

    public void setmIicon(OnIconChangeListener mIicon) {
        this.mIicon = mIicon;
    }

    public IconAdapter(Context context, ArrayList<Icon> resource) {
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
                mIicon.OnIconclick(icon);
            }
        });
        return convertView;
    }

}
