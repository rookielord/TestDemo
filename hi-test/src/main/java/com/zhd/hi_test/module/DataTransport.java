package com.zhd.hi_test.module;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zhd.hi_test.interfaces.InformationListener;

import java.util.List;

/**
 * Created by 2015032501 on 2015/11/4.
 * 用于发送跟新数据
 */
public class DataTransport {
    /**
     * 主要是使用looper来创建对应的handler
     */
    private Looper mLooper;

    /**
     * 主要是用于回调，在Activity中实现更新，调用其方法然后在Activity中实现
     */
    private InformationListener mListener;

    /**
     * 用于刷新消息
     */
    private Handler mHandler;

    private static final int TYPE_LOCATION = 1;
    private static final int TYPE_SATELLITE = 2;
    private static final int TYPE_DATE = 3;
    private static final int TYPE_CLEAR=4;

    public DataTransport(Looper looper, InformationListener listener) {
        this.mLooper = looper;
        this.mListener = listener;
        mHandler=new Handler(looper){
            @Override
            public void handleMessage(Message msg) {
                _handleMessage(msg);
            }
        };
    }

    public Handler getHandler() {
        return mHandler;
    }

    private void _handleMessage(Message msg) {
        switch (msg.what){
            case TYPE_LOCATION:
                mListener.onLocationChange((MyLocation) msg.obj);
                break;
            case TYPE_SATELLITE:
                mListener.onSatelliteChange((List<Satellite>) msg.obj);
                break;
            case TYPE_DATE:
                mListener.onDateChange((UTCDate) msg.obj);
                break;
            case TYPE_CLEAR:
                mListener.clearMessage();
                break;
        }
    }
}
