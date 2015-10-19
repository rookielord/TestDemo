package com.zhd.hi_test.interfaces;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 2015032501 on 2015/10/16.
 * 用于实现连接的接口
 * 所有的连接（除了内置GPS以外都是设置对socket进行处理
 *
 */
public interface IConnect {

    //连接
    void startConnect();

    //发送命令
    void sendMessage();

    //读取信息
    void readMessage();

    //断开连接
    void breakConnect();
}
