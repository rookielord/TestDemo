package com.zhd.hi_test.module;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.interfaces.Joinable;
import com.zhd.hi_test.util.ByteArray;
import com.zhd.hi_test.util.ProgressInfo;
import com.zhd.hi_test.util.TrimbleOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 2015032501 on 2015/11/4.
 * <p/>
 * 1.负责对于数据的处理
 * 2.负责对数据进行发送
 * 3.不再Activity中使用handler进行内容更新
 * 4.只要dd，就可以对消息进行发送
 * 5.remove就可以
 * 6.需要在这里使用mConnect的方法来进行数据的更新吗
 * 7.做成单例模式看看
 * <p/>
 * 每次创建的时候，都需要检测在全局变量中是否存在ConnectManager对象，如果存在则获取
 */
public class ConnectManager {

    /**
     * 1.用来对应listener和DataTransport，方便其注销和获取信息
     * 2.这里才进行对应数据的更改，不再BlueToothConnect中进行操作
     */
//    private Map<OnInformationListener, DataTransport> mListeners = new HashMap<>();
    private HashMap<Activity, Handler> mHandlers = new HashMap<>();
    private String TAG = "INFO";
    private Joinable mJoinable;
    private List<Integer> dollars = new ArrayList<>();
    private List<Integer> asterisks = new ArrayList<>();
    private byte dollar = 0x24;
    private byte asterisk = 0x2A;

    public void setJoinable(Joinable joinable) {
        mJoinable = joinable;
        mJoinable.startConnect();
        //如果这是蓝牙连接
        //发送命令
        try {
            mJoinable.sendMessage(TrimbleOrder.CLOSE_COM1);
            Thread.sleep(300);
            mJoinable.sendMessage(TrimbleOrder.GPGGA);
            Thread.sleep(300);
            mJoinable.sendMessage(TrimbleOrder.GPGSV);
            Thread.sleep(300);
            mJoinable.sendMessage(TrimbleOrder.GPZDA);
            Thread.sleep(300);
            mJoinable.sendMessage(TrimbleOrder.GPGSA);
            Thread.sleep(300);
            mJoinable.flushMessage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //读取信息
        new DealThread().start();
    }


    /**
     * 这里就创建DataTransport对象，然后将其存入HashMap中
     */
    public synchronized void registerListener(Handler handler, Activity activity) {
        //发送命令
        mHandlers.put(activity, handler);
    }

    /**
     * 1.根据listener发送清空信息的命令
     * 2.移除监听
     *
     * @throws IOException
     */
    public synchronized void removeListener(Activity activity) {
        mHandlers.remove(activity);
    }

    /**
     * 1.先对所有的连接都发送清空信息
     * 2.断开连接
     *
     * @throws IOException
     */
    public void closeConnect() {
        try {
            mJoinable.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Handler handler : mHandlers.values()) {
            handler.sendEmptyMessage(Const.TYPE_CLEAR);
        }
        //注销所有的监听
        mHandlers.clear();
    }


    private class DealThread extends Thread {
        @Override
        public void run() {
            int num;
            byte[] buffer = new byte[1024 * 4];
//            byte[] completeInfo;
//            byte[] incompleteInfo = null;
//            byte[] useInfo;
            try {
                while ((num = mJoinable.readMessage(buffer)) != -1) {
                    /**
                     * 数据的处理应该在这里进行。
                     * 1.传入新的读取到的数据
                     * 2.返回的是拼接到的完整数据
                     * 3.然后将得到的数据长度
                     */
                    dollars = ByteArray.getLocations(buffer, dollar, num);
                    asterisks = ByteArray.getLocations(buffer, asterisk, num);
                    int len = asterisks.size();
                    for (int i = 0; i < len; i++) {
                        int length = asterisks.get(i) - dollars.get(i);
                        byte[] temp=new byte[length];
                        System.arraycopy(buffer,dollars.get(i),temp,0,length);
                        String info=new String(temp);
                        Log.d(TAG,info);
                    }
//                    //获取$最后的位置
//                    int loc = ProgressInfo.getLastLocation(buffer, num);
//                    //获取最后$之前的数据的所有数据
//                    completeInfo = ProgressInfo.getComplete(buffer, loc);
//                    //拼接之前的不完整的数据，得到的完整的数据
//                    useInfo = ProgressInfo.mergeInfo(completeInfo, incompleteInfo);
//                    //获取不完整的数据
//                    incompleteInfo = ProgressInfo.getUncomplete(buffer, loc, num);
//                    //首先要发指令，让其发送位置和卫星信息
//                    if (useInfo != null) {
//                        String msg1 = new String(useInfo);
//                        //注意，显示数据是不完善的，经过调试后发现是完整拼接
//                        Log.d(TAG, msg1);
//                        setInputMsg(msg1);
//                    }

                }
            } catch (IOException e) {
                //连接出现异常，发送清空消息的命令
                e.printStackTrace();
                for (Handler handler : mHandlers.values()) {
                    handler.sendEmptyMessage(Const.TYPE_CLEAR);
                }
                mHandlers.clear();
            }
        }
    }


    private static Pattern GGA_pattern = Pattern.compile("\\$GPGGA.*?(?=\\*)");
    private static Pattern GSV_pattern = Pattern.compile("(\\$GPGSV|\\$GLGSV|\\$BDGSV).*?(?=\\*)");
    private static Pattern GPZDA_pattern = Pattern.compile("\\$GPZDA.*?(?=\\*)");
    private static Pattern GPGSA_pattern = Pattern.compile("(\\$GNGSA|\\$GPGSA).*?(?=\\*)");
    //存放对应的数据
    private static ArrayList<Satellite> mSatellites = new ArrayList<>();
    private static MyLocation mLocation;
    private static UTCDate mTime;
    //用来存放临时的数据然后发送过去
    private static Object mTemps;
    //用来获取对应的字段
    private static Matcher mMacher;

    private void setInputMsg(String msg) {
        if (mHandlers.size() == 0)
            return;
        //获得卫星信息
        mMacher = GSV_pattern.matcher(msg);
        while (mMacher.find()) {
            getSatelliteInfo(mMacher.group());
        }
        //获取位置的信息
        mMacher = GGA_pattern.matcher(msg);
        while (mMacher.find()) {
            getLoactionInfo(mMacher.group());
        }
        //获得时间信息
        mMacher = GPZDA_pattern.matcher(msg);
        while (mMacher.find()) {
            getTimeInfo(mMacher.group());
        }
        mMacher = GPGSA_pattern.matcher(msg);
        while (mMacher.find()) {
            getPDOP(mMacher.group());
        }
    }

    private void getPDOP(String group) {
        String[] info = group.split(",");
        if (info.length == 1 || info[1].equals(""))
            return;
        String PDOP = info[info.length - 2];

        for (Handler handler : mHandlers.values()) {
            Message m = Message.obtain();
            m.obj = PDOP;
            m.what = Const.TYPE_PDOP;
            handler.sendMessage(m);
        }
    }

    private void getLoactionInfo(String group) {
        //1.获得位置信息和时间
        String[] info = group.split(",");
        //注意，刚刚开机时是没有定位的，GGA数据都为空，对B是否有值进行判断,没有值则不进行穿件location对象
        if (info.length == 1 || info[1].equals(""))
            return;
        String useSatenum = info[7];
        String time = info[1];
        String B = info[2];
        String BDire = info[3];
        String L = info[4];
        String LDire = info[5];
        //定位质量
        int quality = Integer.valueOf(info[6]);
        String H = info[9];
        //差分龄期,必须是完整的GGA数据才会有，即开始是没有的
        String age;
        if (info.length < 15)
            age = "0";
        else
            age = info[13];
        //坐标点

        for (Handler handler : mHandlers.values()) {
            mLocation = new MyLocation(B, L, H, BDire, LDire, time, quality, age, useSatenum);
            Message m = Message.obtain();
            m.obj = mLocation;
            m.what = Const.TYPE_LOCATION;
            handler.sendMessage(m);
        }
    }

    private void getSatelliteInfo(String group) {
        //1.获得类型
        String[] info = group.split(",");
        //没有数据返回
        if (info.length == 1 || info[1].equals(""))
            return;
        //1.1获得该时间的总共条数（总的GSV语句电文数）
        int allnum = Integer.parseInt(info[1]);
        //1.2获得GSV是该条目的第几条
        int curerntnum = Integer.parseInt(info[2]);
        //1.3解析该时间段的数据
        //2.获得该数据中有多长，即有多少个卫星
        int num = (info.length - 4) / 4;
        //3.根据卫星数量来进行循环
        String str_type = info[0];
        int type = -1;
        if (str_type.equals("$GPGSV"))
            type = Satellite.GPS;
        else if (str_type.equals("$GLGSV"))
            type = Satellite.GLONASS;
        else if (str_type.equals("$BDGSV"))
            type = Satellite.BD;
        //4.获取对应数据创建卫星对象,注意空值。需要对空值进行判断
        Satellite s;
        for (int i = 0; i < num; i++) {
            int loc = 4 * (i + 1);
            //1.4创建卫星数据传入list集合中
            //最后一个信噪比可能为空值，如果为空值则赋值0,表示没有收到信号
            if (info[loc + 3].equals("")) {
                info[loc + 3] = "0";
            }
            s = new Satellite(info[loc], info[loc + 1], info[loc + 2], info[loc + 3], type);
            mSatellites.add(s);
        }
        //1.5传输出去
        //1.5.1这里进行判断，如果currentnum<allnum的话，就不会发送而继续添加，只有当currentnum==allnum才发送
        //赋值集合，如果使用的是同一个集合的话会出现同步错误，因为线程一边在加，然后一边在取,就会造成这个错误
        if (curerntnum == allnum) {

            for (Handler handler : mHandlers.values()) {
                mTemps = mSatellites.clone();
                Message m = Message.obtain();
                m.obj = mTemps;
                m.what = Const.TYPE_SATELLITE;
                handler.sendMessage(m);
            }
            mSatellites.clear();
        }
    }

    private void getTimeInfo(String group) {
        String[] info = group.split(",");
        //没有数据时，长度依然为7，所以只能用内容来判断
        if (info[1].equals(""))
            return;
        String day = info[2];
        String month = info[3];
        String year = info[4];
        mTime = new UTCDate(day, month, year);

        for (Handler handler : mHandlers.values()) {
            Message m = Message.obtain();
            m.obj = mTime;
            m.what = Const.TYPE_DATE;
            handler.sendMessage(m);
        }
    }
}
