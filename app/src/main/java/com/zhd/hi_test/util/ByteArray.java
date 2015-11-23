package com.zhd.hi_test.util;

import android.media.projection.MediaProjectionManager;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015032501 on 2015/11/6.
 * <p/>
 * 1.传入数据
 * 2.获得$符和*号的位置集合
 * 3.然后就可以根据*的位置来取数据
 * 1)*和$的位置相等==      完整数据
 * 2)*的数量大于$的数量==  不正常的残缺数据
 * 3)*的数量小于$的数量==  正常的残缺数据
 * <p/>
 * 这里只负责数据的拼接，处理在外面进行，返回进行处理的数据
 * 5.第二次读取出来的数据会拼接在前一次的后面
 * <p/>
 * <p/>
 * 1.两个方面：1.数据拼接 2.数据处理
 */
public class ByteArray {
    //进行处理的Byte数据,第一次肯定为null
    private static byte[] mTemps=null;
    private static List<Integer> mAsterisks = new ArrayList<>();
    private static List<Integer> mDollars = new ArrayList<>();
    private static final byte dollar = 0x24;
    private static final byte asterisk = 0x2A;
    private static byte[] mCurrent=null;
    private static List<byte[]> mMessage = new ArrayList<>();


    /**
     * 1.进行数据拼接
     * 2.获得其中的位置
     * 3.进行之前数据的拼接
     * 4.删除已经处理好了的数据，留下剩下的数据将其置于处理数据顶部
     *
     * @param buffer 缓冲区的内容
     * @param num    读取到缓冲区的长度
     */
    public static void append(byte[] temp) {

        //获得数据的长度
        int len = temp.length;
        //新的数据
        byte[] new_array=new byte[temp.length+mCurrent.length];
        //向里面添加数据：1.上次不完整的 2.本次的数据
        for (int i = 0; i < len; i++) {
            if (temp[i] == dollar)
                mDollars.add(i);
            if (temp[i] == asterisk)
                mAsterisks.add(i);}

    }

    /**
     * 用于生成临时的byte[]拼接字段
     */
    private static void storeMessage() {
        //获得*最后的位置
        int last_loc = mAsterisks.get(mAsterisks.size() - 1);
        //获得其应该创建的长度
        mTemps = new byte[mCurrent.length - last_loc];
        //向mTemps里面填充内容
        System.arraycopy(mCurrent, last_loc, mTemps, 0, mTemps.length);
    }


    /**
     *
     * @return
     */
    public static byte[] getMessage() {
        byte[] useInfo;
        if (mTemps != null) {
            useInfo = new byte[mTemps.length + mCurrent.length];
            System.arraycopy(mTemps, 0, useInfo, 0, mTemps.length);
            System.arraycopy(mCurrent, 0, useInfo, mTemps.length - 1, mCurrent.length);
        } else
            useInfo = mCurrent;
        return new byte[0];
    }


}
