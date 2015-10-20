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
        tv_splash_version.setText("版本号:"+ ViewHelper.getVersion(this));

        RelativeLayout rl_splash_bg = (RelativeLayout) findViewById(R.id.rl_splash_bg);
        rl_splash_bg.setBackgroundResource(R.drawable.log);
        AnimationDrawable animation= (AnimationDrawable) rl_splash_bg.getBackground();
        animation.start();
        //启动的帧动画
        //获取是否需要升级
        if(SafePreference.getBoolean(this, Const.ISUPDATA)){//需要升级的情况
            LoginHelper.getInstance(this).loginConnect();
        }else{
            //不进行http请求,检查版本,整个过程是在其它线程中执行
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);//如果在ui线程,会阻塞ui线程
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //ui线程中执行的
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            SplashActivity.this.startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    });

                }
            }).start();
        }



    }

    @Override
    protected void onDestroy() {
        LoginHelper.getInstance(this).destory();
        super.onDestroy();
    }

}
