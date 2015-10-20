package com.zhd.hi_test.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.zhd.hi_test.R;
import com.zhd.hi_test.activity.MainActivity;
import com.zhd.hi_test.module.UpdateBean;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 2015032501 on 2015/10/20.
 */
public class LoginHelper {
    private static LoginHelper login;//单例模式
    private Activity context;
    private UpdateBean bean;

    private final int UPDATA = 11;//更新
    private final int CONNECTERROR = 12;//连接服务器失败
    private final int SERVICEERROR = 13;//服务器出错
    private final int DOWNLOADERROR = 14;//下载失败
    private ProgressDialog pd;

    private LoginHelper(Activity context) {
        this.context = context;
    }

    //获得单例
    public static LoginHelper getInstance(Activity context) {
        if (login == null) {
            login = new LoginHelper(context);
        }
        return login;
    }

    /**
     * 连接服务器，是在线程中进行
     */
    public void loginConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();
    }

    /**
     * 访问服务器
     */
    protected void connect() {
        String apkurl = context.getResources().getString(R.string.apkurl);
        Message msg = new Message();
        try {
            URL url = new URL(apkurl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            if (con.getResponseCode() == 200) {
                //连接成功
                bean = XmlParseUtil.getUpdataInfo(con.getInputStream());
                if (bean != null) {
                    if (bean.getVersion().equals(ViewHelper.getVersion(context))) {
                        //无需更新,已经是最新版本,进入主界面
                        enterMain();
                    } else {
                        //有新版本,需要更新,弹开提示
                        msg.what = UPDATA;
                        handler.sendMessage(msg);
                    }
                }
            } else {
                //连接失败,服务器出错
                msg.what = SERVICEERROR;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //服务器连不上
            msg.what = CONNECTERROR;
            handler.sendMessage(msg);
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATA://进行更新提示
                    updateTipDialog();
                    break;
                case CONNECTERROR:
                    Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    enterMain();
                    break;
                case SERVICEERROR:
                    Toast.makeText(context, "服务器出错", Toast.LENGTH_SHORT).show();
                    enterMain();
                    break;
                case DOWNLOADERROR:
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 进入主界面
     */
    private void enterMain() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * 提示用户升级
     */
    protected void updateTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("升级提示");
        builder.setMessage(bean.getDes());
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateApk();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterMain();
            }
        });
        builder.create().show();
    }

    /**
     * 下载更新apk
     */
    protected void updateApk() {
        //在下载的时候,显示一个进度条:动画  下载了多少
        pd = new ProgressDialog(context);
        pd.setTitle("正在下载...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();

        //开启线程，获得下载文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = DownloadHelper.getApkFile(bean.getURL(), pd);
                //如果当前获取文件显示下载出错
                pd.dismiss();
                if (file == null) {
                    //下载失败
                    Message msg = new Message();
                    msg.what = DOWNLOADERROR;
                    handler.sendMessage(msg);

                } else {
                    //进行安装
                    /**
                     * <intent-filter>
                     <action android:name="android.intent.action.VIEW" />
                     <category android:name="android.intent.category.DEFAULT" />
                     <data android:scheme="content" />
                     <data android:scheme="file" />
                     <data android:mimeType="application/vnd.android.package-archive" />
                     </intent-filter>
                     */
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    context.startActivity(intent);
                    context.finish();
                }
            }
        }).start();


    }

    public void destory() {
        login = null;
    }

}
