package com.zhd.hi_test.module;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.interfaces.Joinable;
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
     * 每个Activity对应一个的handler
     */
    private HashMap<Activity, Handler> mHandlers = new HashMap<>();
    /**
     * 获取的数据源
     */
    private Joinable mJoinable;
    /**
     * $符号的位置
     */
    private List<Integer> mDollars = new ArrayList<>();
    /**
     * *符号的位置
     */
    private List<Integer> mAsterisks = new ArrayList<>();
    /**
     * $符号的ascii码
     */
    private byte dollar = 0x24;
    /**
     * *符号的ascii码
     */
    private byte asterisk = 0x2A;

    /**
     * 传入连接对象，然后开始连接
     *
     * @param joinable 连接对象
     */
    public void setJoinable(Joinable joinable) {
        mJoinable = joinable;
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
        } catch (IOException | InterruptedException e) {
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
     */
    public synchronized void removeListener(Activity activity) {
        mHandlers.remove(activity);
    }

    /**
     * 断开连接
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
            byte[] buffer = new byte[1024];
//            byte[] completeInfo;
//            byte[] incompleteInfo = null;
//            byte[] useInfo;
            //上次残留的数据,肯定是有的
            byte[] lastInfo = null;
            //用于进行处理的数据，应该去掉其不完整的数据
            byte[] all;
            //最后一个$的位置
            int loc;
            //不包含不完整数据的数据
            byte[] useInfo;
            try {
                while ((num = mJoinable.readMessage(buffer)) != -1) {
                    //清空上一次的位置信息
                    mAsterisks.clear();
                    mDollars.clear();
                    //实际读取到的数据
                    byte[] temp = new byte[num];
                    System.arraycopy(buffer, 0, temp, 0, num);
                    //数据的拼接,得到用于处理的数据
                    if (lastInfo != null) {//上次遗留+本次完整
                        all = new byte[lastInfo.length + num];
                        System.arraycopy(lastInfo, 0, all, 0, lastInfo.length);
                        System.arraycopy(temp, 0, all, lastInfo.length, num);
                    } else
                        all = temp;
                    int length = all.length;
                    //根据$符号来获得之前的数据,
                    loc = getLastLocation(all);
                    //获得不完整的内容
                    lastInfo = new byte[length - loc];
                    //向不完整内容里面填充内容,从最后的$符号开始
                    System.arraycopy(all, loc, lastInfo, 0, lastInfo.length);
                    //可以用原有的数据吗?删除不完整的，再找一次完整的数据
                    useInfo = new byte[length - lastInfo.length];
                    //对完整的数据进行遍历
                    int use_length = useInfo.length;
                    System.arraycopy(all, 0, useInfo, 0, useInfo.length);
                    for (int i = 0; i < use_length; i++) {
                        if (useInfo[i] == dollar)
                            mDollars.add(i);
                        if (useInfo[i] == asterisk)
                            mAsterisks.add(i);
                    }

                    //*的数量大于$的数量==  不正常的残缺数据
//                    if (mAsterisks.size() > mDollars.size()) {
//                        for (int i = 0; i < mDollars.size(); i++) {
//                            int len = mAsterisks.get(i + 1)+2 - mDollars.get(i)+1;
//                            byte[] message = new byte[len];
//                            System.arraycopy(all, mDollars.get(i), message, 0, len);
//                            String line = new String(message);
//                            Log.d(TAG, line);
//                        }
//                    } else {
//                        if (mAsterisks.get(0) > mDollars.get(0)) {
//                            //*和$的位置相等==
//                            // 1.完整数据
//                            //*的数量小于$的数量==  正常的残缺数据
//                            for (int i = 0; i < mAsterisks.size(); i++) {
//                                int len = mAsterisks.get(i)+2 - mDollars.get(i)+1;
//                                byte[] message = new byte[len];
//                                //因为这是根据
//
//                                System.arraycopy(all, mDollars.get(i), message, 0, len);
//                                String line = new String(message);
//                                Log.d(TAG, line);
//                            }
//                        } else {//错位的数据 2.*和$位置错位的数据
//                            for (int i = 1; i < mAsterisks.size(); i++) {
//                                int len = mAsterisks.get(i)+2 - mDollars.get(i - 1)+1;
//                                byte[] message = new byte[len];
//                                //因为这是根据
//                                System.arraycopy(all, mDollars.get(i - 1), message, 0, len);
//                                String line = new String(message);
//                                Log.d(TAG, line);
//
//                            }
//                        }
//                    }
                    //只有在有*的情况下才进行处理
                    if (mAsterisks.size() > 0) {
                        if (mAsterisks.size() > mDollars.size()) {//*的数量大于$的数量，即在最开始的$前有个*号
                            for (int i = 0; i < mDollars.size(); i++) {
                                int len = mAsterisks.get(i + 1) + 2 - mDollars.get(i) + 1;
                                byte[] message = new byte[len];
                                System.arraycopy(all, mDollars.get(i), message, 0, len);
                                String line = new String(message);
                                handlerMessage(line);
                            }
                        } else {//正常数据:*的数量小于$的数量，因为在之前获取的是去除不完整数据的部分，所以错位的情况会归为第一类
                            for (int i = 0; i < mAsterisks.size(); i++) {
                                int len = mAsterisks.get(i) + 2 - mDollars.get(i) + 1;
                                byte[] message = new byte[len];
                                System.arraycopy(all, mDollars.get(i), message, 0, len);
                                String line = new String(message);
                                handlerMessage(line);
                            }
                        }
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
                //发送消息更新
                for (Handler handler : mHandlers.values()) {
                    handler.sendEmptyMessage(Const.TYPE_CLEAR);
                }
                Const.Info.SetInfo(Const.NoneConnect, false, "仪器断开连接");
                e.printStackTrace();
                mHandlers.clear();
            }
        }

    }

    private void handlerMessage(String line) {
        String[] info = line.split(",");
        switch (info[0]) {
            case "$GPGGA":
                getLoactionInfo(line);
                break;
            case "$GPGSV":
                getSatelliteInfo(line);
                break;
            case "$GLGSV":
                getSatelliteInfo(line);
                break;
            case "$BDGSV":
                getSatelliteInfo(line);
                break;
            case "$GPZDA":
                getTimeInfo(line);
                break;
            case "$GNGSA":
                getPDOP(line);
                break;
            case "$GPGSA":
                getPDOP(line);
                break;
        }
    }

    /**
     * 注意，通过正则捕捉的都是位于$和*之中的数据，现在统一对$和*xx之间的数据进行处理
     */

    private Pattern GGA_pattern = Pattern.compile("\\$GPGGA.*");
    private Pattern GSV_pattern = Pattern.compile("(\\$GPGSV|\\$GLGSV|\\$BDGSV).*");
    private Pattern GPZDA_pattern = Pattern.compile("\\$GPZDA.*");
    private Pattern GPGSA_pattern = Pattern.compile("(\\$GNGSA|\\$GPGSA).*");

    private void setInputMsg(String msg) {
        Matcher macher;
        if (mHandlers.size() == 0)
            return;
        //获得卫星信息
        macher = GSV_pattern.matcher(msg);
        while (macher.find()) {
            getSatelliteInfo(macher.group());
        }
        //获取位置的信息
        macher = GGA_pattern.matcher(msg);
        while (macher.find()) {
            getLoactionInfo(macher.group());
        }
        //获得时间信息
        macher = GPZDA_pattern.matcher(msg);
        while (macher.find()) {
            getTimeInfo(macher.group());
        }
        macher = GPGSA_pattern.matcher(msg);
        while (macher.find()) {
            getPDOP(macher.group());
        }
    }

    private void getPDOP(String group) {
        String[] value = group.split("\\*");
        String[] info = value[0].split(",");
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
        String[] value = group.split("\\*");
        String[] info = value[0].split(",");
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
        MyLocation location;
        for (Handler handler : mHandlers.values()) {
            location = new MyLocation(B, L, H, BDire, LDire, time, quality, age, useSatenum);
            Message m = Message.obtain();
            m.obj = location;
            m.what = Const.TYPE_LOCATION;
            handler.sendMessage(m);
        }
    }

    private void getSatelliteInfo(String group) {
        ArrayList<Satellite> satellites = new ArrayList<>();

        //1.获得类型,都需要将*给取消
        String[] value = group.split("\\*");
        String[] info = value[0].split(",");
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
        switch (str_type) {
            case "$GPGSV":
                type = Satellite.GPS;
                break;
            case "$GLGSV":
                type = Satellite.GLONASS;
                break;
            case "$BDGSV":
                type = Satellite.BD;
                break;
        }
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
            satellites.add(s);
        }
        //1.5传输出去
        //1.5.1这里进行判断，如果currentnum<allnum的话，就不会发送而继续添加，只有当currentnum==allnum才发送
        //赋值集合，如果使用的是同一个集合的话会出现同步错误，因为线程一边在加，然后一边在取,就会造成这个错误
        Object temp;
        if (curerntnum == allnum) {
            for (Handler handler : mHandlers.values()) {
                temp = satellites.clone();
                Message m = Message.obtain();
                m.obj = temp;
                m.what = Const.TYPE_SATELLITE;
                handler.sendMessage(m);
            }
            satellites.clear();
        }
    }

    private void getTimeInfo(String group) {
        String[] value = group.split("\\*");
        String[] info = value[0].split(",");
        //没有数据时，长度依然为7，所以只能用内容来判断
        if (info[1].equals(""))
            return;
        String day = info[2];
        String month = info[3];
        String year = info[4];
        UTCDate time = new UTCDate(day, month, year);

        for (Handler handler : mHandlers.values()) {
            Message m = Message.obtain();
            m.obj = time;
            m.what = Const.TYPE_DATE;
            handler.sendMessage(m);
        }
    }

    public int getLastLocation(byte[] buffer) {
        int location = 0;//默认没有找到$符的位置
        int num = buffer.length;
        for (int i = 0; i < num; i++) {
            if (buffer[i] == dollar && i > location) {//判断条件1.当前位置大于位置2.确定是$符
                location = i;
            }
        }
        return location;
    }


//    public int getLastLocation(byte[] buffer, int length) {
//        for (int i = 0; i < length; i++) {
//            if (buffer[i] == dollar)
//                mDollars.add(i);
//            if (buffer[i] == asterisk)
//                mAsterisks.add(i);
//        }
//        //如果没有找到$的位置，则整条数据都是不完整的,即整条数据都是不完整的的
//        if (mDollars.size() == 0)
//            return 0;
//        //如果mDollars的长度大于0，则删除最后一位$的位置
//        mDollars.remove(mDollars.size() - 1);
//        return mDollars.get(mDollars.size() - 1);
//        //需要删除
//    }
}
