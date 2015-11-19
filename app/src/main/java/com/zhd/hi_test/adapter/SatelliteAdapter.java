package com.zhd.hi_test.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhd.hi_test.R;
import com.zhd.hi_test.module.Satellite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/10/27.
 */
public class SatelliteAdapter extends ArrayAdapter {

    public SatelliteAdapter(Context context, int resource) {
        super(context, resource);
    }

    class ViewHolder {
        TextView pnr;
        TextView snr;
        TextView ele;
        TextView azi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Satellite s = (Satellite) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.satellite_item, null);
            holder = new ViewHolder();
            holder.pnr = (TextView) convertView.findViewById(R.id.tv_pnr);
            holder.snr = (TextView) convertView.findViewById(R.id.tv_snr);
            holder.azi = (TextView) convertView.findViewById(R.id.tv_azimuth);
            holder.ele = (TextView) convertView.findViewById(R.id.tv_elevation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.pnr.setText(String.valueOf(s.getPrn()));
        holder.snr.setText(String.valueOf(s.getSnr()));
        holder.azi.setText(String.valueOf(s.getAzimuth()));
        holder.ele.setText(String.valueOf(s.getElevation()));

        return convertView;
    }
}
