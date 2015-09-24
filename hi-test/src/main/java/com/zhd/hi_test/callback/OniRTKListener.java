package com.zhd.hi_test.callback;

import com.zhd.hi_test.module.MyLocation;
import com.zhd.hi_test.module.Satellite;

import java.util.ArrayList;

/**
 * Created by 2015032501 on 2015/9/23.
 */
public interface OniRTKListener {
    void onSatelliteinfo(ArrayList<Satellite> satellites);
    void onLocationinfo(MyLocation location);
}
