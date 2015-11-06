package com.zhd.hi_test.util;

/**
 * Created by 2015032501 on 2015/10/16.
 * 用于消息处理的静态类
 */
public class ProgressInfo {
    /**
     * 获得不完整的数据，如果没有找到$符号的话，则返回整个读到的byte[] buffer，其长度为num
     * 注意$符号位置的情况：
     * 1.$不存在，2.$位于第一位，整条数据都是不完整的，返回<读到的>整条数据
     * 3.$正常位置，返回$位置之后<读到的>数据4.$位于最后一位，不会返回数据
     *
     * @param buffer
     * @param loc
     * @param num
     * @return
     */
    public static byte[] getUncomplete(byte[] buffer, int loc, int num) {
        byte[] temp;
        if (loc == -2 || loc == -1) {//不存在$的情况和$位置是第一位的情况
            temp = new byte[num];
            for (int i = 0; i < num; i++) {
                temp[i] = buffer[i];
            }
        } else {
            int length = num - loc;//正确
            temp = new byte[length];//创建不完整数据的长度
            for (int i = 0; i < length; i++) {//赋值
                temp[i] = buffer[i + loc];
            }
        }
        return temp;
    }

    /**
     * 通过$的位置来创建完整的byte[] complete
     * 需要注意不包含$符号的情况，需要分情况讨论
     * 1.不包含$符号和2.$符号为第一位==当条数据都不完整
     * 3.最后一位和4.通常位置==获得$之前的数据
     *
     * @param buffer
     * @param loc
     * @return
     */
    public static byte[] getComplete(byte[] buffer, int loc) {
        if (loc == -2 || loc == -1) {
            return null;
        }
        byte[] temp = new byte[loc];
        for (int i = 0; i < loc; i++) {
            temp[i] = buffer[i];
        }
        return temp;
    }

    /**
     * 这里进行 本次查询到完整的数据，以及上次查词到的不完整的数据的拼接
     * 注意：
     * 1.第一次进行合并时，uncompleteInfo为null,需要返回完整数据
     * 2.有时会有没有包含$符的情况，则整个buffer都是不完整的数据，让其和下一次进行拼接，其返回null
     * 3.首先判断是否有completeinfo[]然后再判断uncompleteinfo
     *
     * @param completeInfo
     * @param uncompleteInfo
     * @return
     */
    public static byte[] mergeInfo(byte[] completeInfo, byte[] uncompleteInfo) {
        if (completeInfo == null)
            return null;
        if (uncompleteInfo == null)
            return completeInfo;
        byte[] useinfo = new byte[completeInfo.length + uncompleteInfo.length];
        System.arraycopy(uncompleteInfo, 0, useinfo, 0, uncompleteInfo.length);
        System.arraycopy(completeInfo, 0, useinfo, uncompleteInfo.length, completeInfo.length);
        return useinfo;
    }

    /**
     * 最后一个$位置的4种状态：
     * 1.通常状态，介于(0,length-1)之间；
     * 2.不存在，返回值为-2
     * 3.位于首位，返回值为-1
     * 4.位于末尾，返回值为length-1
     *
     * @param buffer
     * @param num
     * @return
     */
    public static int getLastLocation(byte[] buffer, int num) {
        byte Fdelimiter = 0x24;//$符号的byte
        int location = -1;//默认没有找到$符的位置
        for (int i = 0; i < num; i++) {
            if (buffer[i] == Fdelimiter && i > location) {//判断条件1.当前位置大于位置2.确定是$符
                location = i;
            }
        }
        //因为获取到的是$之前的数据，而找到的是$符号的位置，所以要-1
        return location - 1;
    }

}
