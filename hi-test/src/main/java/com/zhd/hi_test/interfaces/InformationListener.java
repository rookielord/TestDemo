package com.zhd.hi_test.interfaces;

import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.Satellite;
import com.zhd.hi_test.module.UTCDate;

import java.util.List;

/**
 * Created by 2015032501 on 2015/11/4.
 */
public interface InformationListener {
    /**
     * 用于在主界面更新位置信息
     *
     * @param location
     */
    void onLocationChange(MyLocation location);

    /**
     * 用于在主界面更新卫星信息
     *
     * @param satellites
     */
    void onSatelliteChange(List<Satellite> satellites);

    /**
     * 更新日期信息
     *
     * @param date
     */
    void onDateChange(UTCDate date);

    /**
     * 清空所有的信息
     */
    void clearMessage();

}
