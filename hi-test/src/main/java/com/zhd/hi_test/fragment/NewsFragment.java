package com.zhd.hi_test.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

/**
 * Created by 2015032501 on 2015/9/18.
 */
public class NewsFragment extends Fragment {
    //获取的参数
    private String mWeburl;
    private String mName;

    //用来生成的View
    private View mView;

    /**
     * 设置这个Fragment关联的Activity,如果不设置则在哪个Activity中调用就自动绑定当前Activity
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * 设置获取Bundle中传入的数据，对当前对象中的数据赋值
     * @param args
     */
    @Override
    public void setArguments(Bundle args) {
        mName=args.getString("name");
        mWeburl=args.getString("weburl");
    }

    /**
     * 通过布局文件来生成View然后返回View
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView!=null){
            TextView tv=new TextView(getActivity());
            tv.setText(mName);
            tv.setTextSize(18);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mView=tv;
        }
        //这是是向其中添加一个TextView
        ViewGroup parent= (ViewGroup) mView.getParent();
        if (parent!=null){
            parent.removeView(mView);
        }
        return mView;

    }
}
