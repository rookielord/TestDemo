package com.zhd.hi_test.util;

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
    private static ArrayList<Integer> mLocations = new ArrayList<>();
    private static List<Integer> mAsterisks = new ArrayList<>();
    private static byte dollar = 0x24;
    private static byte asterisk = 0x2A;


    public static ArrayList<Integer> getLocations(byte[] buffer, byte delimiter, int num) {
        mLocations.clear();
        byte[] temp = new byte[num];
        System.arraycopy(buffer, 0, temp, 0, num);
        int len = temp.length;
        for (int i = 0; i < len; i++) {
            if (temp[i] == delimiter)
                mLocations.add(i);
        }
        return mLocations;
    }

    /**
     * 1.对mBuffer的内容进行赋值
     * 2.获得其中的位置
     * 3.进行之前数据的拼接
     * 4.删除已经处理好了的数据，留下剩下的数据
     *
     * @param buffer 缓冲区的内容
     * @param num    读取到缓冲区的长度
     */
    public static void append(byte[] buffer, int num) {


    }


}
