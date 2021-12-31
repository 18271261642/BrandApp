package com.isport.brandapp.home.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.bracelet.braceletPresenter.WeatherPresenter;
import com.isport.brandapp.home.bean.SportLastDataBean;
import com.isport.brandapp.home.bean.SportLastDataBeanList;
import com.isport.brandapp.home.presenter.FragmentSportPresenter;
import com.isport.brandapp.home.view.FragmentSportView;
import com.isport.brandapp.sport.SportReportActivity;
import com.isport.brandapp.sport.SportSettingActivity;
import com.isport.brandapp.sport.location.LocationServiceHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * 底部菜单，运动页面
 * @Author
 * @Date 2019/1/10
 * @Fuction
 */

public class FragmentSport extends BaseMVPFragment<FragmentSportView, FragmentSportPresenter> implements FragmentSportView, View.OnClickListener {


    LocationServiceHelper locationServiceHelper;

    WeatherPresenter weatherPresenter;

    boolean isChina;

    int sportType;

    MapView mMapView = null;


    ImageView ivSportHistory, iv_sport_setting;
    AMap aMap = null;
    FragmentLeftSport leftSport1, leftSport2, leftSport3, leftSport4;
    //Fragment集合
    private ArrayList<Fragment> mFagments = new ArrayList<>();

    private RadioButton tv_sport_outside, tv_sport_indoor, tv_sport_bike, tv_sport_walk;
    private View view_indoor;

    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_sport;
    }

    @Override
    protected void initView(View view) {

        mMapView = view.findViewById(R.id.map);


        view_indoor = view.findViewById(R.id.view_indoor);
        tv_sport_outside = view.findViewById(R.id.tv_sport_outside);
        iv_sport_setting = view.findViewById(R.id.iv_sport_setting);
        ivSportHistory = view.findViewById(R.id.iv_sport_history);
        tv_sport_indoor = view.findViewById(R.id.tv_sport_indoor);
        tv_sport_bike = view.findViewById(R.id.tv_sport_bike);
        tv_sport_walk = view.findViewById(R.id.tv_sport_walk);


        if (App.isUserGoogleMap()) {

            mMapView.setVisibility(View.GONE);

        } else {
            mMapView.onCreate(savedInstanceState);
            //aMap.setCli
        }

        locationServiceHelper = new LocationServiceHelper(getActivity());
        locationServiceHelper.startLocation();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mFragmentManager == null) {
            mFragmentManager = this.getChildFragmentManager();
        }
    }

    private void addToList(Fragment fragment) {
        if (fragment != null) {
            mFagments.add(fragment);
        }

        Logger.myLog("fragmentList数量" + mFagments.size());
    }

    /*添加fragment*/
    private void addFragment(Fragment fragment) {

        /*判断该fragment是否已经被添加过  如果没有被添加  则添加*/
        if (!fragment.isAdded()) {
            mFragmentManager.beginTransaction().add(R.id.sport_fragment, fragment).commitAllowingStateLoss();
            /*添加到 fragmentList*/
            mFagments.add(fragment);
        }
    }


    @Override
    protected void initData() {
        initTab();
        isChina = true;
        requestPermission();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        isPause = false;
        Logger.myLog("Constants.cityName onResume isHidden" + isHidden);
        // mFragPresenter.getAllLastData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        if (!isHidden) {
            startqeustData();
            // LocationHelp.getInstance().startLocation();
        }
        if (App.isUserGoogleMap()) {
            setUpMap();
        }

    }

    boolean isHidden, isPause;
    //显示后加载
    long lastClickTime = 0;
    long currentClickTime = 0;
    Handler handler = new Handler();


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.myLog("AmapError: Constants.mLocationLatitude hidden" + hidden);
        isHidden = hidden;
        if (!isHidden) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.show_sport_location));
            locationServiceHelper.startLocation();
            Logger.myLog("FragmentNewData onHiddenChanged" + isHidden + "lastclick:" + (currentClickTime) + "lastClickTime:" + lastClickTime + "----" + (currentClickTime - lastClickTime));
            currentClickTime = System.currentTimeMillis();
            if (lastClickTime == 0) {
                lastClickTime = System.currentTimeMillis();
                startqeustData();
            } else {
                Logger.myLog("FragmentNewData onHiddenChanged2" + isHidden + "lastclick:" + (currentClickTime) + "lastClickTime:" + lastClickTime + "----" + (currentClickTime - lastClickTime));
                if (currentClickTime - lastClickTime > 10 * 1000) {
                    lastClickTime = currentClickTime;
                    startqeustData();
                }
            }

        } else {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.hide_sport_location));
            locationServiceHelper.stopLocation();
        }
        //Logger.myLog("fragmentNewData:onHiddenChanged:" + hidden);
    }

    private void startqeustData() {

        mFragPresenter.getAllLastData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

        if (aMap == null) {
            aMap = mMapView.getMap();
            //aMap.setMapType(AMap.MAP_TYPE_NIGHT);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            //aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            if (App.isZh(UIUtils.getContext())) {
                aMap.setMapLanguage(AMap.CHINESE);
            } else {
                aMap.setMapLanguage(AMap.ENGLISH);
            }
           /* handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LocationHelp.getInstance().startLocation();
                }
            }, 1000);*/

            MyLocationStyle myLocationStyle;
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.SPORT_UPDATE_SUCESS:
                //mFragPresenter.getAllLastData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                break;

            case MessageEvent.update_location:
                //可在其中解析amapLocation获取相应内容。
                Logger.myLog("Constants.cityName" + "isHidden:" + isHidden + ",isPause:" + isPause);


                if (isHidden || isPause) {
                    Logger.myLog("Constants.cityName" + "isHidden:" + isHidden + ",isPause:" + isPause + ",,return");
                    if (isHidden == true && isPause == false) {
                    }
                    return;
                }
                if (TextUtils.isEmpty(Constants.currentCountry)) {
                    isChina = true;
                } else {
                    if (Constants.currentCountry.equals("中国") || Constants.currentCountry.equals("China")) {
                        isChina = true;
                    } else {
                        isChina = false;
                    }
                }
                if (!isChina) {
//                    googlemapView.clear();
//                    com.google.android.gms.maps.model.LatLng sydney = new com.google.android.gms.maps.model.LatLng(com.isport.blelibrary.utils.Constants.mLocationLatitude, com.isport.blelibrary.utils.Constants.mLocationLongitude);
//                    com.google.android.gms.maps.CameraUpdate cu = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(sydney, 17);
//                    googlemapView.moveCamera(cu);
//                   /* googlemapView.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(sydney)
//                            .title("Marker in Sydney"));*/
//                    com.google.android.gms.maps.model.MarkerOptions markerOption = new com.google.android.gms.maps.model.MarkerOptions();
//                    markerOption.position(sydney);
//                    // markerOption.position(Constants.XIAN);
//                    // markerOption.title("西安市").snippet("DefaultMarker");
//                    markerOption.draggable(true);//设置Marker可拖动
//                    markerOption.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                            .decodeResource(getResources(), R.drawable.icon_mark)));
//                    googlemapView.addMarker(markerOption);
                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    //googlemapView.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(sydney));

                }

                if (isFirst) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(com.isport.blelibrary.utils.Constants.mLocationLatitude,
                            com.isport.blelibrary.utils.Constants.mLocationLatitude), 17);
                    // weatherPresenter.getWeather(com.isport.blelibrary.utils.Constants.mLocationLatitude, com.isport.blelibrary.utils.Constants.mLocationLongitude, com.isport.blelibrary.utils.Constants.cityName, 814);

                    if (aMap != null) {
                        aMap.moveCamera(cu);
                        isFirst = false;
                    }


                } else {
                  /*  addRectangle(new LatLng(com.isport.blelibrary.utils.Constants.mLocationLatitude,
                            com.isport.blelibrary.utils.Constants.mLocationLatitude));*/
                }
                break;
        }
    }


    public void addRectangle(LatLng latLng) {
        // 绘制一个长方形
        aMap.addPolygon(new PolygonOptions()
                .addAll(createRectangle(latLng, 0.0001, 0.0001))
                .fillColor(Color.parseColor("#FFCBCB"))
                // 线的宽度取消
                .strokeWidth(0)
        );
        // 虚线组成一个长方形
        aMap.addPolyline(new PolylineOptions()
                .addAll(createRectangle(latLng, 100, 100))
                .width(10)
                .setDottedLine(true)
                .color(Color.parseColor("#F45A5A")));
    }

    /**
     * 生成一个长方形的四个坐标点
     */
    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        // TODO: 2018/5/19 添加最后一个点,组成闭合,不然会少一边
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
        // 矩形的四个点
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
        return latLngs;
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACTIVITY_RECOGNITION},0x03);
            }
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.BODY_SENSORS},0x00);
        }

        PermissionManageUtil permissionManage = new PermissionManageUtil(getActivity());
        RxPermissions mRxPermission = new RxPermissions(this);
        if (!mRxPermission.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionManage.requestPermissions(new RxPermissions(this), Manifest.permission.ACCESS_FINE_LOCATION,
                    UIUtils.getString(R.string.permission_location1), new
                            PermissionManageUtil.OnGetPermissionListener() {


                                @Override
                                public void onGetPermissionYes() {

                                }

                                @Override
                                public void onGetPermissionNo() {
                                }
                            });

        }
    }

    private void initTab() {
        //添加Fragment
        view_indoor.setVisibility(View.GONE);
        sportType = JkConfiguration.SportType.sportOutRuning;
        leftSport1 = new FragmentLeftSport();
        Bundle bundle = new Bundle();
        bundle.putInt("sportType", JkConfiguration.SportType.sportOutRuning);
        leftSport1.setArguments(bundle);
        addToList(leftSport1);


        addFragment(leftSport1);
        showFragment(leftSport1);
    }

    @Override
    protected void initEvent() {
        tv_sport_outside.setOnClickListener(this);
        tv_sport_indoor.setOnClickListener(this);
        tv_sport_bike.setOnClickListener(this);
        tv_sport_walk.setOnClickListener(this);


        iv_sport_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), SportSettingActivity.class);
                intent.putExtra("sportType", sportType);
                startActivity(intent);


            }
        });
        ivSportHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SportReportActivity.class);
                startActivity(intent);

            }
        });

    }


    @Override
    public void onClick(View v) {
        try {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            switch (v.getId()) {
                case R.id.tv_sport_outside:
                    view_indoor.setVisibility(View.GONE);
                    sportType = JkConfiguration.SportType.sportOutRuning;
                    addFragment(leftSport1);
                    showFragment(leftSport1);
                    break;
                case R.id.tv_sport_indoor:
                    view_indoor.setVisibility(View.VISIBLE);
                    sportType = JkConfiguration.SportType.sportIndoor;
                    if (leftSport2 == null) {
                        leftSport2 = new FragmentLeftSport();
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("sportType", JkConfiguration.SportType.sportIndoor);
                        leftSport2.setArguments(bundle2);
                        addToList(leftSport2);
                    }
                    addFragment(leftSport2);
                    showFragment(leftSport2);
                    break;
                case R.id.tv_sport_bike:
                    view_indoor.setVisibility(View.GONE);
                    sportType = JkConfiguration.SportType.sportBike;
                    if (leftSport3 == null) {
                        leftSport3 = new FragmentLeftSport();
                        Bundle bundle3 = new Bundle();
                        bundle3.putInt("sportType", JkConfiguration.SportType.sportBike);
                        leftSport3.setArguments(bundle3);
                        addToList(leftSport3);
                    }
                    addFragment(leftSport3);
                    showFragment(leftSport3);
                    break;
                case R.id.tv_sport_walk:
                    view_indoor.setVisibility(View.GONE);
                    sportType = JkConfiguration.SportType.sportWalk;
                    if (leftSport4 == null) {
                        leftSport4 = new FragmentLeftSport();
                        Bundle bundle4 = new Bundle();
                        bundle4.putInt("sportType", JkConfiguration.SportType.sportWalk);
                        leftSport4.setArguments(bundle4);
                        addToList(leftSport4);
                    }
                    addFragment(leftSport4);
                    showFragment(leftSport4);
                    break;
                default:
                    break;
            }
            ft.commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
        locationServiceHelper.stopLocation();
        mMapView.onPause();
    }


    private void setUpMap() {
//        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_mapview)).getMapAsync(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (googlemapView != null) {
//            googlemapView.clear();
//        }
        if (aMap != null) {
            aMap.clear();
        }
        if (locationServiceHelper != null) {
            locationServiceHelper.stopLocation();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    public void success(SportLastDataBeanList lastDataBeanList) {
        if (lastDataBeanList != null && lastDataBeanList.getList() != null) {
            for (int i = 0; i < lastDataBeanList.getList().size(); i++) {
                SportLastDataBean lastDataBean = lastDataBeanList.getList().get(i);
                AppConfiguration.sportLastDataBeanHashMap.put(lastDataBean.getType(), lastDataBean);


            }

            for (int i = 0; i < mFagments.size(); i++) {
                FragmentLeftSport fragment = (FragmentLeftSport) mFagments.get(i);
                if (fragment.isAdded()) {
                    ((FragmentLeftSport) mFagments.get(i)).updateValue();
                }
            }
        }
    }


    @Override
    protected FragmentSportPresenter createPersenter() {
        weatherPresenter = new WeatherPresenter(this);
        return new FragmentSportPresenter(this);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        if (googlemapView != null) {
//            return;
//        }
//        googlemapView = googleMap;
//        //startDemo();
//
//
//    }
//
//    protected com.google.android.gms.maps.GoogleMap getMap() {
//        return googlemapView;
//    }

    private final static String LINE = "rvumEis{y[}DUaBGu@EqESyCMyAGGZGdEEhBAb@DZBXCPGP]Xg@LSBy@E{@SiBi@wAYa@AQGcAY]I]KeBm@_Bw@cBu@ICKB}KiGsEkCeEmBqJcFkFuCsFuCgB_AkAi@cA[qAWuAKeB?uALgB\\eDx@oBb@eAVeAd@cEdAaCp@s@PO@MBuEpA{@R{@NaAHwADuBAqAGE?qCS[@gAO{Fg@qIcAsCg@u@SeBk@aA_@uCsAkBcAsAy@AMGIw@e@_Bq@eA[eCi@QOAK@O@YF}CA_@Ga@c@cAg@eACW@YVgDD]Nq@j@}AR{@rBcHvBwHvAuFJk@B_@AgAGk@UkAkBcH{@qCuAiEa@gAa@w@c@o@mA{Ae@s@[m@_AaCy@uB_@kAq@_Be@}@c@m@{AwAkDuDyC_De@w@{@kB_A}BQo@UsBGy@AaA@cLBkCHsBNoD@c@E]q@eAiBcDwDoGYY_@QWEwE_@i@E}@@{BNaA@s@EyB_@c@?a@F}B\\iCv@uDjAa@Ds@Bs@EyAWo@Sm@a@YSu@c@g@Mi@GqBUi@MUMMMq@}@SWWM]C[DUJONg@hAW\\QHo@BYIOKcG{FqCsBgByAaAa@gA]c@I{@Gi@@cALcEv@_G|@gAJwAAUGUAk@C{Ga@gACu@A[Em@Sg@Y_AmA[u@Oo@qAmGeAeEs@sCgAqDg@{@[_@m@e@y@a@YIKCuAYuAQyAUuAWUaA_@wBiBgJaAoFyCwNy@cFIm@Bg@?a@t@yIVuDx@qKfA}N^aE@yE@qAIeDYaFBW\\eBFkANkANWd@gALc@PwAZiBb@qCFgCDcCGkCKoC`@gExBaVViDH}@kAOwAWe@Cg@BUDBU`@sERcCJ{BzFeB";

    protected void startDemo() {
        //List<com.google.android.gms.maps.model.LatLng> decodedPath = PolyUtil.decode(LINE);

        //getMap().addPolyline(new com.google.android.gms.maps.model.PolylineOptions().addAll(decodedPath));

        //getMap().moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(-33.8256, 151.2395), 12));
    }

    @Override
    public void initImmersionBar() {
       /* ImmersionBar.with(this).titleBar(R.id.iv_sport_history)
                .init();*/
        ImmersionBar.with(this).statusBarDarkFont(true)
                .init();
    }


    private LatLongData addLatLongData(String lng, double d) {
        LatLongData mLatLongData = new LatLongData();
        String[] lat = lng.split("\\,");
        mLatLongData.setSpeer("" + d);
        mLatLongData.setLongitude(Float.parseFloat(lat[0]));
        mLatLongData.setLattitude(Float.parseFloat(lat[1]));

        return mLatLongData;
    }


    boolean isFirst = true;

    String strCurrentLocation;



      /*  if(location!=null){

        }
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        //if (location.hashCode() == 0) {
        sb.append("定位成功" + "\n");
        // sb.append("定位类型: " + location.gett() + "\n");

        location.getSpeed();
        sb.append("经    度    : " + location.getLongitude() + "\n");
        sb.append("纬    度    : " + location.getLatitude() + "\n");
*/

    /**
     * 20-10m -（19=<zoom<20）
     * 19-10m -（19=<zoom<20）
     * 18-25m -（18=<zoom<19）
     * 17-50m -（17=<zoom<18）
     * 16-100m -（16=<zoom<17）
     * 15-200m -（15=<zoom<16）
     * 14-500m -（14=<zoom<15）
     * 13-1km -（13=<zoom<14）
     * 12-2km -（12=<zoom<13）
     * 11-5km -（11=<zoom<12）
     * 10-10km -（10=<zoom<11）
     * 9-20km -（9=<zoom<10）
     * 8-30km -（8=<zoom<9）
     * 7-50km -（7=<zoom<8）
     * 6-100km -（6=<zoom<7）
     * 5-200km -（5=<zoom<6）
     * 4-500km -（4=<zoom<5）
     * 3-1000km -（3=<zoom<4）
     * 2-1000km -（3=<zoom<4）
     * ---------------------
     *//*
        //aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude
        ()), 15));//  通过定位后获得的经纬度 为地图添加中心点 和地图比例  数字越小地图显示越多
        //aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude
        ()), 15));//  通过定位后获得的经纬度 为地图添加中心点 和地图比例  数字越小地图显示越多
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(location.getLatitude(),
        location.getLongitude()), 16, 0, 0)));

        sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
        sb.append("提供者    : " + location.getProvider() + "\n");

        sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
        sb.append("角    度    : " + location.getBearing() + "\n");
        //定位完成的时间
        sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
        // } else {
        //定位失败
        sb.append("定位失败" + "\n");
        sb.append("错误码:" + location.hashCode() + "\n");
           *//* sb.append("错误信息:" + location.has() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");*//*
        // }
        // sb.append("***定位质量报告***").append("\n");
        // sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
        // sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus()))
        .append("\n");
        // sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
        // sb.append("****************").append("\n");
        //定位之后的回调时间
        sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
       *//* tvResult.setText("签到失败");
        tvResult.setText(sb.toString());*/


    /**
     * 根据速度区间，把轨迹点归类到不同均值列表中
     *
     * @param list
     * @return
     */
    public static List<LatLongData> getSpeerDateList(List<LatLongData> list) {
        if (list == null || list.size() == 0) {
            return null;
        }

        for (LatLongData item : list) {
            if (Float.parseFloat(item.getSpeer()) < 6) {
                item.setSpeer(String.valueOf(5));
            } else if (Float.parseFloat(item.getSpeer()) < 7
                    && Float.parseFloat(item.getSpeer()) >= 6) {
                item.setSpeer(String.valueOf(6));
            } else if (Float.parseFloat(item.getSpeer()) < 9
                    && Float.parseFloat(item.getSpeer()) >= 7) {
                item.setSpeer(String.valueOf(8));
            } else {
                item.setSpeer(String.valueOf(9));
            }
        }

        return list;

    }

    Polyline polyline;

    private void drawLines() {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(39.999391, 116.135972));
        latLngs.add(new LatLng(39.898323, 116.057694));
        latLngs.add(new LatLng(39.900430, 116.265061));
        latLngs.add(new LatLng(39.955192, 116.140092));
        aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));


    }


    private void drawMark() {
        MarkerOptions markerOption = new MarkerOptions();
        // markerOption.position(Constants.XIAN);
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");

        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable
                        .icon_mark)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//        markerOption.setFlat(true);//设置marker平贴地图效果
    }


    float zoom;
    boolean mIsAmapDisplay;
    /*private void changeToGoogleMapView() {
        zoom = aMap.getCameraPosition().zoom;
        mLocationLatitude = aMap.getCameraPosition().target.latitude;
        mLocationLongitude = aMap.getCameraPosition().target.longitude;
        mapbtn.setText("To Amap");
        mIsAmapDisplay = false;
        mGoogleMapView = new com.google.android.gms.maps.MapView(this, new GoogleMapOptions()
                .camera(new com.google.android.gms.maps.model
                        .CameraPosition(new com.google.android.gms.maps.model.LatLng(latitude, longitude), zoom, 0, 0)));
        mGoogleMapView.onCreate(null);
        mGoogleMapView.onResume();
        mContainerLayout.addView(mGoogleMapView, mParams);
        mGoogleMapView.getMapAsync(this);
        handler.sendEmptyMessageDelayed(0,500);
    }

    private void changeToAmapView() {
        zoom = googlemap.getCameraPosition().zoom;
        latitude = googlemap.getCameraPosition().target.latitude;
        longitude = googlemap.getCameraPosition().target.longitude;
        mapbtn.setText("To Google");
        mAmapView = new TextureMapView(this, new AMapOptions()
                .camera(new com.amap.api.maps.model.CameraPosition(new LatLng(latitude,longitude),zoom,0,0)));
        mAmapView.onCreate(null);
        mAmapView.onResume();
        mContainerLayout.addView(mAmapView, mParams);
        mGoogleMapView.animate()
                .alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mGoogleMapView.setVisibility(View.GONE);
                mContainerLayout.removeView(mGoogleMapView);
                if (mGoogleMapView != null) {
                    mGoogleMapView.onDestroy();
                }
            }
        });
        aMap.setOnCameraChangeListener(this);
        mIsAmapDisplay = true;
    }*/


    FragmentManager mFragmentManager;

    Fragment currentFragment;

    /*显示fragment*/
    private void showFragment(Fragment fragment) {
        for (Fragment frag : mFagments) {
            if (frag != fragment) {
                /*先隐藏其他fragment*/
                mFragmentManager.beginTransaction().hide(frag).commitAllowingStateLoss();
            }
        }
        currentFragment = fragment;
        mFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();

    }

}
