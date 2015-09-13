package com.zhd.switchview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by juiz on 2015/9/13.
 */
public class PageFragmentAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList;
    private FragmentManager fm;

    public PageFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fm=fm;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        //return fragmentList.get(position%fragmentList.size());
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        //return super.getItemPosition(object);
        return POSITION_NONE;//没有找到child重新加载
    }
}
