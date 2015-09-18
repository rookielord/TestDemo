package com.zhd.hi_test.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * Created by 2015032501 on 2015/9/18.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFraglist;
    private FragmentManager mManager;

    public FragmentAdapter(FragmentManager fm, List<Fragment> mFraglist) {
        super(fm);
        this.mFraglist = mFraglist;
        this.mManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return mFraglist.get(position);
    }

    @Override
    public int getCount() {
        return mFraglist.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 传入FragmentList然后改变
     * @param fragments
     */
    public void setFragments(List<Fragment> fragments) {
        if(this.mFraglist != null){
            FragmentTransaction ft = mManager.beginTransaction();
            for(Fragment f:this.mFraglist){
                ft.remove(f);
            }
            ft.commit();//提交了自然就没有用了
            ft=null;
            mManager.executePendingTransactions();
        }
        this.mFraglist = fragments;//将传入的FragmentList传入这个对象的内容
        notifyDataSetChanged();//刷新
    }
}
