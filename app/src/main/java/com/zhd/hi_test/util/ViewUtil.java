package com.zhd.hi_test.util;

/**
 * Created by 2015032501 on 2015/10/28.
 * 卫星信息的转化
 */
public class ViewUtil {
    public static int snrToSignalLevel(float snr) {
        int level = 0;
        if (snr >= 0 && snr < 20) {
            level = 0;
        } else if (snr >= 20 && snr < 36) {
            level = 1;
        } else if (snr >= 36 && snr < 60) {
            level = 2;
        } else if (snr >= 60 && snr < 80) {
            level = 3;
        } else if (snr >= 80) {
            level = 4;
        }
        return level;
    }
}
