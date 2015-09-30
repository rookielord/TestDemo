package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.hi_test.Data;
import com.zhd.hi_test.R;
import com.zhd.hi_test.db.Curd;
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
    //装入所有的自定义控件的集合
    protected List<MyScrollView> mHScrollViews = new ArrayList<MyScrollView>();
    //项目的对应表名
    private String mTableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        Data d = (Data) getApplication();
        mTableName = d.getmProject().getmTableName();
        initViews();
    }

    private void initViews() {
        //固定表头的textview
        MyScrollView headerScroll = (MyScrollView) findViewById(R.id.item_scroll_title);
        //添加头滑动事件，把xml控件上的自定义控件放入MyScrollView的集合
        mHScrollViews.add(headerScroll);
        //找到listview,并向里面填充数据
        //准备数据
        //数据库中
        Curd curd = new Curd(mTableName, this);
        Cursor cursor = curd.queryData(new String[]{"*"}, null, null, null, null, null);
        //创建Data来填充数据
        List<Map<String, String>> points = new ArrayList<>();
        while (cursor.moveToNext()) {
            Map<String, String> point = new HashMap<>();
            point.put("name", "pt" + cursor.getString(cursor.getColumnIndex("id")));//通过pt+id来得到name,其实只是id
            point.put("B", cursor.getString(cursor.getColumnIndex("B")));
            point.put("L", cursor.getString(cursor.getColumnIndex("L")));
            point.put("H", cursor.getString(cursor.getColumnIndex("H")));
            point.put("DES", cursor.getString(cursor.getColumnIndex("DES")));
            points.add(point);
        }
        //找到显示的listview
        mListView = (ListView) findViewById(R.id.scroll_list);
        //注意：这里是调用的自定义的Adapter，但是为什么继承的是SimpleAdapter,没有继承BaseAdapter
        SimpleAdapter adapter = new ScrollAdapter(this, points, R.layout.point_item
                , new String[]{"name", "B", "L", "H", "DES"}
                , new int[]{
                R.id.item_name
                , R.id.item_B
                , R.id.item_L
                , R.id.item_H
                , R.id.item_DES
        });
        mListView.setAdapter(adapter);
    }

    /**
     * 添加自定义的view
     *
     * @param hScrollView
     */
    public void addHViews(final MyScrollView hScrollView) {
        if (!mHScrollViews.isEmpty()) {//如果自定义控件集合不为空
            //获得其中的Scrollview的数量
            int size = mHScrollViews.size();
            MyScrollView scrollView = mHScrollViews.get(size - 1);
            //获得边缘显示的view
            final int scrollX = scrollView.getScrollX();
            //第一次满屏后，向下滑动，有一条数据在开始时未加入
            if (scrollX != 0) {
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

    /**
     * 当滑动时触发的事件
     * 遍历mHScrollViews中的元素，然后把新的l,r传给它
     * 即实现联动效果
     * 遍历每个
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        for (MyScrollView scrollView : mHScrollViews) {
            //防止重复滑动
            if (mTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }

    class ScrollAdapter extends SimpleAdapter {

        private List<? extends Map<String, ?>> datas;
        private int res;//资源布局文档R.layout.point_item
        private String[] from;//数据内容在datas中的Map的String
        private int[] to;//view上面对应的资源布局ID
        private Context context;

        //自定义函数传入的内容，
        public ScrollAdapter(Context context,
                             List<? extends Map<String, ?>> data, int resource,
                             String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.context = context;
            this.datas = data;//数据内容
            this.res = resource;//用来填充的布局文件
            this.from = from;//数据的来源
            this.to = to;//数据的存放对象
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //如果当前没有对象被销毁
//            View v = convertView;
            if (convertView == null) {
                //根据xml来创建view对象
                convertView = LayoutInflater.from(context).inflate(res, null);
                //这里找到的是自定义控件的HorizonscrollView，然后加入到集合里面，滑动时处理里面的自定义控件集合
                addHViews((MyScrollView) convertView.findViewById(R.id.item_scroll));
                //根据插入对应数据的长度来创建views，
                View[] views = new View[to.length];
                //找到views上面的对象，创建点击事件
                for (int i = 0; i < to.length; i++) {
                    //这里是找到textview
                    View tv = convertView.findViewById(to[i]);
//                    tv.setOnClickListener(clickListener);
                    views[i] = tv;
                }
                convertView.setTag(views);
            }
            //如果没有里面缓存数据没有被销毁，就获得其中的textview
            View[] holders = (View[]) convertView.getTag();
            int len = holders.length;
            //向里面填充数据
            for (int i = 0; i < len; i++) {
                ((TextView) holders[i]).setText(datas.get(position).get(from[i]).toString());
            }
            convertView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.setHeaderTitle("点信息操作");
                    menu.add(0, 0, 0, "修改");
                    menu.add(0, 1, 0, "删除");
                }
            });
            return convertView;
        }
    }

//    //测试点击的事件
//    protected View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Toast.makeText(ManageActivity.this, ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
//        }
//    };
}
