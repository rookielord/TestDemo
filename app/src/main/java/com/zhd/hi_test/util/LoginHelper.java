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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by 2015032501 on 2015/10/20.
 */
public class LoginHelper {
    private static final int NETWORKERROR = 1;//网络错误
    private static final int CONNETERROR = 2;//连接出错
    private static final int UPDATA = 3;//开始更新
    private static final int URLERROR = 4;//URL地址解析出错
    private static final int SERVICEERROR = 5;//服务器或资源没有找到
    private static final int DOWNLOADERROR = 6;//下载失败
    private static final int XmlERROR = 7;//xml解析出错
    private static final int ENTER_MAIN = 8;//进入主界面
    private static LoginHelper mLogin;//单例模式
    //主要是用于弹出Toast
    private Activity mContext;
    //升级对象
    private UpdateBean mBean;


    //弹出的升级对话框
    private ProgressDialog pd;

    private LoginHelper(Activity context) {
        this.mContext = context;
    }

    //获得单例
    public static LoginHelper getInstance(Activity context) {
        if (mLogin == null) {
            mLogin = new LoginHelper(context);
        }
        return mLogin;
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
     * 访问服务器，并让连接过程最少保持2s,如果不满2s则增加到2s,超过2s则不管
     */
    protected void connect() {
        String urlsource = mContext.getResources().getString(R.string.apkurl);
        Message msg = Message.obtain();
        try {
            URL url = new URL(urlsource);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int resultCode = conn.getResponseCode();
            if (resultCode == 200) {//有返回值
                InputStream is = conn.getInputStream();
                mBean = XmlParseUtil.getUpdataInfo(is);
                if (mBean != null) {//版本相同，进入主界面
                    if (ViewHelper.getVersion(mContext).equals(mBean.getVersion())) {
                        msg.what = ENTER_MAIN;
                    } else {//不相同，弹出升级窗口
                        msg.what = UPDATA;
                    }
                } else {//XML解析错误
                    msg.what = XmlERROR;
                }
            } else {//连接错误
                msg.what = CONNETERROR;
            }
        } catch (MalformedURLException e) {//URL地址错误
            e.printStackTrace();
            msg.what = URLERROR;

        } catch (IOException e) {//网络错误
            e.printStackTrace();
            msg.what = NETWORKERROR;
        } finally {
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ENTER_MAIN:
                    enterMain();
                    break;
                case NETWORKERROR:
                    Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    enterMain();
                    break;
                case CONNETERROR:
                    Toast.makeText(mContext, mContext.getString(R.string.connect_error), Toast.LENGTH_SHORT).show();
                    enterMain();
                    break;
                case UPDATA://进行更新提示
                    updateTipDialog();
                    break;
                case URLERROR:
                    Toast.makeText(mContext, mContext.getString(R.string.url_error), Toast.LENGTH_SHORT).show();
                    enterMain();
                    break;
                case SERVICEERROR:
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    enterMain();
                    break;
                case DOWNLOADERROR:
                    Toast.makeText(mContext, mContext.getString(R.string.download_error), Toast.LENGTH_SHORT).show();
                    break;
                case XmlERROR:
                    Toast.makeText(mContext, mContext.getString(R.string.xml_error), Toast.LENGTH_SHORT).show();
                    enterMain();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    /**
     * 进入主界面
     */
    private void enterMain() {
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
        mContext.finish();
    }

    /**
     * 提示用户升级
     */
    protected void updateTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.update_title));
        builder.setMessage(mBean.getDes());
        builder.setPositiveButton(mContext.getString(R.string.update_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateApk();

            }
        });
        builder.setNegativeButton(mContext.getString(R.string.update_cancel), new DialogInterface.OnClickListener() {
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
        pd = new ProgressDialog(mContext);
        pd.setTitle(mContext.getString(R.string.download_title));
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        //开启线程，获得下载文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = DownloadHelper.getApkFile(mBean.getURL(), pd);
                //如果当前获取文件显示下载出错
                pd.dismiss();
                if (file == null) {
                    //下载失败
                    Message msg = new Message();
                    msg.what = DOWNLOADERROR;
                    mHandler.sendMessage(msg);

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
                    mContext.startActivity(intent);
                    mContext.finish();
                }
            }
        }).start();
    }

    public void destory() {
        mLogin = null;
    }

}
