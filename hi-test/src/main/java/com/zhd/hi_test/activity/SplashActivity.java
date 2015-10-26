package com.zhd.hi_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.util.LoginHelper;
import com.zhd.hi_test.util.SafePreference;
import com.zhd.hi_test.util.ViewHelper;

/**
 * Created by 2015032501 on 2015/10/20.
 */
public class SplashActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //版本号
        TextView tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号:" + ViewHelper.getVersion(this));

        RelativeLayout rl_splash_bg = (RelativeLayout) findViewById(R.id.rl_splash_bg);
        rl_splash_bg.setBackgroundResource(R.drawable.log);
        //启动动画
        AnimationDrawable animation = (AnimationDrawable) rl_splash_bg.getBackground();
        animation.start();
        //检测读取升级信息,开始解析数据
        LoginHelper.getInstance(this).loginConnect();
    }
}
