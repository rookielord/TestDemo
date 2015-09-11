package com.zhd.mytest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);

        //创建集合来对象来遍历
        //ArrayList
        List<String> mylist = new ArrayList<String>();
        for (int i = 0; i < 1000; i++) {
            mylist.add("当前内容是" + i);
        }
        //HashMap
        Map<Integer, String> mymap = new HashMap<Integer, String>();
        for (int i = 0; i < 1000; i++) {
            mymap.put(i, "当前内容是" + i);
        }
        //String[]
        String[] strings = new String[1000];
        for (int i = 0; i < 1000; i++) {
            strings[i] = "当前的内容是" + i;
        }
        //使用for遍历得到时间
        String test1 = testForloop(mylist);
        String test2 = testForloop(mymap);
        String test3 = testForloop(strings);
        //使用迭代器来遍历
        //要将所有的遍历对象都继承,Iterable,Iterator,可迭代器接口和迭代器接口
        Iterator<String> iterator1 = mylist.iterator();
        String itest = testIterator(iterator1);
        tv.setText("对于ListView进行For循环所消耗的时间" + test1 + "\n" +
                "对于HashMap进行For循环所消耗的时间" + test2 + "\n" +
                "对于String[]进行For循环所消耗的时间" + test3 + "\n" +
                "对于ListView进行For循环所消耗的时间" + itest + "\n");
    }

    /**
     * 这是使用
     *
     * @param iterator
     * @return
     */
    private String testIterator(Iterator<String> iterator) {
        String l = null;
        long start = System.currentTimeMillis();
        while (iterator.hasNext()) {
            l = iterator.next();
        }
        long end = System.currentTimeMillis();
        return String.valueOf(end - start);
    }


    /**
     * 以下两种都是采用最基础的for循环来进行遍历
     * 但是根据传入参数的不同需要重写方法，增加了代码的重复率
     * 而且如果根据传入集合类型的不同还会有不同的遍历方式
     *
     * @param mymap
     */

    private String testForloop(Map<Integer, String> mymap) {
        String l = null;
        long start = System.currentTimeMillis();
        for (int i = 0; i < mymap.size(); i++) {
            l = mymap.get(i);
        }
        long end = System.currentTimeMillis();
        return String.valueOf(end - start);
    }

    private String testForloop(List<String> mylist) {
        String l = null;
        long start = System.currentTimeMillis();
        for (int i = 0; i < mylist.size(); i++) {
            l = mylist.get(i);
        }
        long end = System.currentTimeMillis();
        return String.valueOf(end - start);
    }

    private String testForloop(String[] list) {
        String l = null;
        long start = System.currentTimeMillis();
        for (int i = 0; i < list.length; i++) {
            l = list[i];
        }
        long end = System.currentTimeMillis();
        return String.valueOf(end - start);
    }

}
