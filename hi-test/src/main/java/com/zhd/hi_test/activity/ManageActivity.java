package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.R;
import com.zhd.hi_test.ui.MyScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 2015032501 on 2015/9/19.
 * 进行点位数据的查询和显示，并且可以对数据进行增删改查
 */
public class ManageActivity extends Activity {
    //用来填充的数据的
    private ListView mListView;
    //找到自定义控件
    public HorizontalScrollView mTouchView;
    //装入所有的HScrollView
    protected List<MyScrollView> mHScrollViews =new ArrayList<MyScrollView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll);
        initViews();
    }

    private void initViews() {
        //可以滑动的表头
        MyScrollView headerScroll = (MyScrollView) findViewById(R.id.item_scroll_title);
        //添加头滑动事件，把xml控件上的自定义控件放入MyScrollView的集合
        mHScrollViews.add(headerScroll);
        //找到listview,并向里面填充数据
        //准备数据
        List<Map<String, String>> datas = new ArrayList<Map<String,String>>();
        Map<String, String> data = null;
        mListView = (ListView) findViewById(R.id.scroll_list);
        for(int i = 0; i < 100; i++) {
            data = new HashMap<String, String>();
            data.put("title", "Title_" + i);
            data.put("data_" + 1, "Date_" + 1 + "_" +i );
            data.put("data_" + 2, "Date_" + 2 + "_" +i );
            data.put("data_" + 3, "Date_" + 3 + "_" +i );
            data.put("data_" + 4, "Date_" + 4 + "_" +i );
            data.put("data_" + 5, "Date_" + 5 + "_" +i );
            data.put("data_" + 6, "Date_" + 6 + "_" +i );
            datas.add(data);
        }
        //注意：这里是调用的自定义的Adapter，但是为什么继承的是SimpleAdapter,没有继承BaseAdapter
        SimpleAdapter adapter = new ScrollAdapter(this, datas, R.layout.item
                , new String[] { "title", "data_1", "data_2", "data_3", "data_4", "data_5", "data_6", }
                , new int[] { R.id.item_title
                , R.id.item_data1
                , R.id.item_data2
                , R.id.item_data3
                , R.id.item_data4
                , R.id.item_data5
                , R.id.item_data6 });
        mListView.setAdapter(adapter);
    }

    public void addHViews(final MyScrollView hScrollView) {
        if(!mHScrollViews.isEmpty()) {
            int size = mHScrollViews.size();
            MyScrollView scrollView = mHScrollViews.get(size - 1);
            final int scrollX = scrollView.getScrollX();
            //第一次满屏后，向下滑动，有一条数据在开始时未加入
            if(scrollX != 0) {
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        //当listView刷新完成之后，把该条移动到最终位置
                        hScrollView.scrollTo(scrollX, 0);
                    }
                });
            }
        }
        mHScrollViews.add(hScrollView);
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt){
        for(MyScrollView scrollView : mHScrollViews) {
            //防止重复滑动
            if(mTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }

    class ScrollAdapter extends SimpleAdapter {

        private List<? extends Map<String, ?>> datas;
        private int res;
        private String[] from;
        private int[] to;
        private Context context;
        public ScrollAdapter(Context context,
                             List<? extends Map<String, ?>> data, int resource,
                             String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.context = context;
            this.datas = data;
            this.res = resource;
            this.from = from;
            this.to = to;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v == null) {
                v = LayoutInflater.from(context).inflate(res, null);
                //第一次初始化的时候装进来
                addHViews((MyScrollView) v.findViewById(R.id.item_scroll));
                View[] views = new View[to.length];
                for(int i = 0; i < to.length; i++) {
                    View tv = v.findViewById(to[i]);;
                    tv.setOnClickListener(clickListener);
                    views[i] = tv;
                }
                v.setTag(views);
            }
            View[] holders = (View[]) v.getTag();
            int len = holders.length;
            for(int i = 0 ; i < len; i++) {
                ((TextView)holders[i]).setText(this.datas.get(position).get(from[i]).toString());
            }
            return v;
        }
    }

    //测试点击的事件
    protected View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(ManageActivity.this, ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
        }
    };
}
