package com.zhd.hi_test.module;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zhd.hi_test.Const;
import com.zhd.hi_test.interfaces.OnInformationListener;

import java.util.List;

/**
 * Created by 2015032501 on 2015/11/4.
 * 用于发送跟新数据
 */
public class DataTransport {

    /**
     * 主要是用于回调，在Activity中实现更新，调用其方法然后在Activity中实现
     */
    private OnInformationListener mListener;

    /**
     * 用于刷新消息
     */
    private final Handler mHandler;

    public DataTransport(Looper looper, OnInformationListener listener) {
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
            case Const.TYPE_LOCATION:
                mListener.onLocationChange((MyLocation) msg.obj);
                break;
            case Const.TYPE_SATELLITE:
                mListener.onSatelliteChange((List<Satellite>) msg.obj);
                break;
            case Const.TYPE_DATE:
                mListener.onDateChange((UTCDate) msg.obj);
                break;
            case Const.TYPE_CLEAR:
                mListener.clearMessage();
                break;
            case Const.TYPE_PDOP:
                mListener.onPDOPChange((String) msg.obj);
        }
    }
}
