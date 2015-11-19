package com.zhd.hi_test.interfaces;

import java.io.IOException;

/**
 * Created by 2015032501 on 2015/11/6.
 *
 * 1.只有蓝牙连接才实现
 * 2.内置GPS使用别的方法
 */
public interface Joinable {

    /**
     * 建立当前的连接
     */
    void startConnect();

    /**
     * 发送信息
     */
    void sendMessage(byte[] order) throws IOException;

    /**
     * 刷新命令
     */
    void flushMessage() throws IOException;

    /**
     * @param buffer buffer为所读取到的内容
     * @return buffer中的数据的擦很难过度
     */
    int readMessage(byte[] buffer) throws IOException;

    /**
     * 断开连接
     */
    void disconnect() throws IOException;

}
