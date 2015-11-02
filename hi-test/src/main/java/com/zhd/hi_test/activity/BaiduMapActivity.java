package com.zhd.hi_test.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.zhd.hi_test.Const;
import com.zhd.hi_test.R;
import com.zhd.hi_test.db.Curd;
import com.zhd.hi_test.util.Coordinate;

import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.map.MyLocationConfiguration.*;
import static com.baidu.mapapi.utils.CoordinateConverter.*;

/**
 * Created by 2015032501 on 2015/10/22.
 * 1.将采集点中的数据读取出来
 * 2.将dd:mm:ss.ssss的格式转化为dd.ddddd的格式
 * 3.将点集合数据放进去，然后话点的数据
 */
public class BaiduMapActivity extends Activity implements OnClickListener {

    //控件
    MapView mapview;
    Button btn_location;
    //百度地图对象
    private BaiduMap mBaiduMap;
    //百度定位客户端
    LocationClient mLocClient;
    //用来监听Location位置变化的监听器
    private MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;//定位状态，罗盘，普通，跟踪普通代表可以任意移动，跟随代表锁定当前
    boolean isFirstLoc = true;// 是否首次定位，每次打开时都应该是当前点
    //存放用来画点的集合
    private List<LatLng> points = new ArrayList<>();
    //在图上用的mark
    private BitmapDescriptor mMarkicon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());//加载jar类型包，不然xml中的标签不会被识别
        setContentView(R.layout.activity_baidumap);
        //获取在屏幕上需要画圆的内容
        mMarkicon= BitmapDescriptorFactory.fromResource(R.mipmap.ic_solution_rtki);
        init();
        //
        drawMap();
    }

    /**
     * 将点集合画在图上
     * 每次画图后会清空所有的点
     */
    private void drawMap() {
        //注意画圆大小应该随着比例尺变化
        //如果是画线段的话？
        //根据图形化一个
        for (LatLng point : points) {
            OverlayOptions ooA = new MarkerOptions().position(point).icon(mMarkicon)
                    .zIndex(9).draggable(true);
            mBaiduMap.addOverlay(ooA);
        }

    }
    private void init() {
        btn_location = (Button) findViewById(R.id.btn_location);
        btn_location.setOnClickListener(this);
        // 地图初始化
        mapview = (MapView) findViewById(R.id.BDMapView);
        mBaiduMap = mapview.getMap();
        //使用的当前定位模式
        mCurrentMode = LocationMode.NORMAL;
        btn_location.setText(getString(R.string.normal));
        //启动定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //创建定位客户端
        mLocClient = new LocationClient(this);
        //注册定位监听和设置定位方式
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//设置定位间隔时间
        mLocClient.setLocOption(option);
        mLocClient.start();
        queryPoints();
    }

    /**
     * 查询数据库中的点的集合并添加到points集合中去
     */
    private void queryPoints() {
        Curd curd = new Curd(Const.getmProject().getmTableName(), this);
        Cursor cursor = curd.queryData(new String[]{"B", "L","DireB","DireL"});
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                //获得B,L进行转化
                double b = Coordinate.getDegreeFromSQL(cursor.getString(cursor.getColumnIndex("B"))
                        , cursor.getString(cursor.getColumnIndex("DireB")));
                double l = Coordinate.getDegreeFromSQL(cursor.getString(cursor.getColumnIndex("L"))
                        , cursor.getString(cursor.getColumnIndex("DireL")));
                //将GPS坐标转化为BD09ll
                CoordinateConverter converter  = new CoordinateConverter();
                converter.from(CoordType.GPS);
                LatLng lng = new LatLng(b, l);
                converter.coord(lng);
                LatLng desLatLng = converter.convert();
                points.add(desLatLng);
            }
            cursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_location:
                switch (mCurrentMode) {
                    case NORMAL:
                        btn_location.setText(getString(R.string.follow));
                        mCurrentMode = LocationMode.FOLLOWING;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, null));
                        break;
                    case COMPASS:
                        btn_location.setText(getString(R.string.normal));
                        mCurrentMode = LocationMode.NORMAL;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, null));
                        break;
                    case FOLLOWING:
                        btn_location.setText(getString(R.string.compass));
                        mCurrentMode = LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, null));
                        break;
                }
                break;
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // mapview销毁后不在处理新接收的位置
            if (bdLocation == null || mapview == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())// 获取定位精度,默认值0.0f
                    .direction(100)//gps定位结果时，行进的方向，单位度
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            //创建lcoData对象，将其显示到百度地图上
            mBaiduMap.setMyLocationData(locData);
            //如果是第一次定位,就需要显示那个点的图像
            if (isFirstLoc) {
                isFirstLoc = false;
                //创建在地图上点的坐标
                LatLng ll = new LatLng(bdLocation.getLatitude(),
                        bdLocation.getLongitude());
                //将当前点显示到地图上
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    @Override
    protected void onPause() {
        mapview.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mapview.onDestroy();
        mapview = null;
        //回收图片资源
        mMarkicon.recycle();
        super.onDestroy();
    }
}
