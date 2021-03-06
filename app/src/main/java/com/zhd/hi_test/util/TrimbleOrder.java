package com.zhd.hi_test.util;

/**
 * 定义所有天宝的命令
 * Created by 2015032501 on 2015/9/17.
 */
public class TrimbleOrder {
    /**
     * 关闭COM1所有输出
     */
    public static final byte[] CLOSE_COM1 = {0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x04, (byte) 0xFF, 0x00, 0x00, 0x00, 0x7F, 0x03};
    /**
     * 关闭COM2输出
     */
    public static final byte[] CLOSE_COM2 = {0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x04, (byte) 0xFF, 0x01, 0x00, 0x00, (byte) 0x80, 0x03};

    /**
     * 关闭CAN输出
     */
    public static final byte[] CLOSE_CAN = {0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x04, (byte) 0xFF, 0x04, 0x00, 0x00, (byte) 0x83, 0x03};

    /**
     * COM1 1S/次输出BLH坐标:
     */

    public static final byte[] BLH_LOC = {0x00, 0x64, 0x0F, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x02, 0x00, (byte) 0x93, 0x03};


    /**
     * COM1 1S/次输出XYZ坐标:
     */
    public static final byte[] XYZ_LOC = {0x02, 0x00, 0x64, 0x0F, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x03, 0x00, (byte) 0x94, 0x03};

    /**
     * 输出
     */
    public static final byte[] GPGGA = {0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x04, 0x06, 0x00, 0x03, 0x00, (byte) 0x89, 0x03};

    /**
     * COM1 1S/次输出TIME、锁定卫星数、坐标、PDOP值：
     * 这个解析同样乱码
     */
    public static final byte[] SATELLITE = {0x02, 0x00, 0x64, 0x1F, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x02, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x09, 0x00, (byte) 0xE1, 0x03};

    /**
     * COM1 1S/次输出TIME、PDOP值、锁定卫星数：
     * 这个解析乱码
     */
    public static final byte[] SATELLITE2 = {0x02, 0x00, 0x64, 0x1F, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x09, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x21, 0x00, 0x00, 0x03};

    /**
     * 卫星数据
     */
    public static final byte[] GPGSV = {0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x04, 0x12, 0x00, 0x03, 0x00, (byte) 0x95, 0x03};

    /**
     * GPZDA这个命令可以获得当前的时间数据
     */
    public static final byte[] GPZDA = {0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x04, 0x08, 0x00, 0x03, 0x00, (byte) 0x8B, 0x03};

    /**
     *
     */
    public static final byte[] GPGSA = {0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x04, 0x26, 0x00, 0x03, 0x00, (byte) 0xA9, 0x03};

    public static final byte[] PDOP = {0x02, 0x00, 0x64, 0x0F, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x06, 0x0A, 0x00, 0x03, 0x00, 0x09, 0x00, (byte) 0x9A, 0x03};
}
