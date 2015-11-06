package com.zhd.hi_test.util;

import java.util.ArrayList;

/**
 * Created by 2015032501 on 2015/11/6.
 * <p/>
 * 1.传入数据
 * 2.获得$符和*号的位置集合
 */
public class ByteArray {

    private byte[] temps;

    /**
     * 根据传入的数据量返回符号位置的集合
     * @param buffer 数据
     * @param delimiter 符号
     * @return
     */
    public ArrayList<Integer> getSignAllLocs(byte[] buffer, byte delimiter) {
        ArrayList<Integer> locations = new ArrayList<>();
        int len = buffer.length;
        for (int i = 0; i < len; i++) {
            if (delimiter == buffer[i]) {
                locations.add(i);
            }
        }
        return locations;
    }

    /**
     * 添加数据
     */
    public void append(){

    }

    /**
     * 移除信息
     */
    public void remove(){

    }



}
