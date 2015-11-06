package com.zhd.hi_test.module;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zhd.hi_test.interfaces.Connectable;
import com.zhd.hi_test.interfaces.InformationListener;
import com.zhd.hi_test.util.Infomation;
import com.zhd.hi_test.util.ProgressInfo;
import com.zhd.hi_test.util.TrimbleOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 2015032501 on 2015/11/4.
 * <p/>
 * 1.负责对于数据的处理
 * 2.负责对数据进行发送
 * 3.不再Activity中使用handler进行内容更新
 * 4.只要dd，就可以对消息进行发送
 * 5.remove就可以
 * 6.需要在这里使用mConnect的方法来进行数据的更新吗
 */
public class ConnectManager {

    /**
     * 1.用来对应listener和DataTransport，方便其注销和获取信息
     * 2.这里才进行对应数据的更改，不再BlueToothConnect中进行操作
     */
    private Map<InformationListener, DataTransport> mListeners = new HashMap<>();
    private Context mContext;
    private Connectable mConnect;
    private String TAG = "BLUETEST";


    public ConnectManager(Context context, Connectable connect) {
        this.mContext = context;
        this.mConnect = connect;
        //开始进行数据连接
        connect.startConnect();
        //发送命令
        try {
            connect.sendMessage(TrimbleOrder.CLOSE_COM1);
            connect.sendMessage(TrimbleOrder.GPGGA);
            connect.sendMessage(TrimbleOrder.GPGSV);
            connect.sendMessage(TrimbleOrder.GPZDA);
            connect.flushMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //读取信息
        new DealThread().start();
    }

    //传入进来的Listener是一致的
    public void registerListener(InformationListener listener) {
        //发送命令
        DataTransport transport = new DataTransport(mContext.getMainLooper(), listener);
        mListeners.put(listener, transport);
    }

    /**
     * 断开连接
     * 1.断开连接
     * 2.根据listener发送清空信息的命令
     * 3.移除监听
     *
     * @param listener
     * @throws IOException
     */
    public void removeListener(InformationListener listener) throws IOException {
        mConnect.disconnect();
        DataTransport dt = mListeners.get(listener);
        dt.getHandler().sendEmptyMessage(4);
        mListeners.remove(listener);
    }


    //1.分别获取$和*的位置的集合,
    //2.判断$和*的数量
    //3.
//                      1)*和$的位置相等==      完整数据
//                      2)*的数量大于$的数量==  不正常的残缺数据
//                      3)*的数量小于$的数量==  正常的残缺数据
    //4.第二次读取出来的数据会拼接在前一次的后面
    private class DealThread extends Thread {
        @Override
        public void run() {
            int num;
            byte[] buffer = new byte[1024 * 4];
            byte[] completeInfo;
            byte[] incompleteInfo = null;
            byte[] useInfo;
            try {
                while ((num = mConnect.readMessage(buffer)) != -1) {
                    //获取$最后的位置
                    int loc = ProgressInfo.getLastLocation(buffer, num);
                    //获取最后$之前的数据的所有数据
                    completeInfo = ProgressInfo.getComplete(buffer, loc);
                    //拼接之前的不完整的数据，得到的完整的数据
                    useInfo = ProgressInfo.mergeInfo(completeInfo, incompleteInfo);
                    //获取不完整的数据
                    incompleteInfo = ProgressInfo.getUncomplete(buffer, loc, num);
                    //首先要发指令，让其发送位置和卫星信息
                    if (useInfo != null) {
                        String msg1 = new String(useInfo);
                        //注意，显示数据是不完善的，经过调试后发现是完整拼接
                        Log.d(TAG, msg1);
                        Infomation.setmInputMsg(msg1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
