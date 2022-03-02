package com.isport.brandapp.device.bracelet;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.isport.blelibrary.utils.Constants;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.device.bracelet.braceletPresenter.WeatherPresenter;
import com.isport.brandapp.sport.location.LocationServiceHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseView;

public class ActivityWeatherSetting extends BaseTitleActivity implements BaseView {


    int deviceType = 0;

    WeatherPresenter weatherPresenter;


    Handler handler = new Handler();
    TextView tvLocation;
    TextView tvCity;
    TextView tvNoLocation;
    TextView tvCityTips;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_weather_setting;
    }

    @Override
    protected void initView(View view) {

        tvCityTips = view.findViewById(R.id.tv_current_city_tips);
        tvNoLocation = view.findViewById(R.id.no_location);
        tvCity = view.findViewById(R.id.tv_current_city);
        tvLocation = view.findViewById(R.id.tv_location);

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvLocation.getTag().equals("start")) {
                    if (!AppConfiguration.isConnected) {
                        ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                        return;
                    }
                    if (!NetUtils.hasNetwork()) {
                        ToastUtil.showTextToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                        return;
                    }
                    isLoction = false;
                    isOpenGps();
                }
            }
        });

    }

    public void isOpenGps() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            PublicAlertDialog.getInstance().showDialogNoCancle(false, UIUtils.getString(R.string.wheather_setting_title), UIUtils.getString(R.string.wheather_setting_content), this, UIUtils.getString(R.string.app_close), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
                    // ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
                }

                @Override
                public void cancel() {

                }
            });


        } else {
            requestPermission();
        }
    }

    private LocationServiceHelper mLSHelper;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.update_location_error:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (TextUtils.isEmpty(Constants.cityName)) {
                            showCurrentCityView(false, Constants.cityName);
                            return;
                        }
                        showCurrentCityView(true, Constants.cityName);
                        setLocationText(UIUtils.getString(R.string.weather_location), "start");
                        weatherPresenter.getWeather(Constants.mLocationLatitude, Constants.mLocationLongitude, Constants.cityName, deviceType);
                    }
                }, 2000);
                break;
            case MessageEvent.update_location:
                if (!isLoction) {
                    isLoction = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showCurrentCityView(true, Constants.cityName);
                            setLocationText(UIUtils.getString(R.string.weather_location), "start");

                            weatherPresenter.getWeather(Constants.mLocationLatitude, Constants.mLocationLongitude, Constants.cityName, deviceType);
                        }
                    }, 2000);
                }
                break;
        }
    }



    boolean isLoction = false;

    public void startLocation() {

        mLSHelper = new LocationServiceHelper(this);
        mLSHelper.startLocation();
        setLocationText(UIUtils.getString(R.string.weather_location_ing), "end");
    }


    private void requestPermission() {
        try {
            PermissionManageUtil permissionManage = new PermissionManageUtil(context);
            RxPermissions mRxPermission = new RxPermissions(this);
            if (!mRxPermission.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionManage.requestPermissions(new RxPermissions(this), Manifest.permission.ACCESS_FINE_LOCATION,
                        UIUtils.getString(R.string.permission_location0), new
                                PermissionManageUtil.OnGetPermissionListener() {


                                    @Override
                                    public void onGetPermissionYes() {
                                        startLocation();
                                    }

                                    @Override
                                    public void onGetPermissionNo() {
                                        ToastUtils.showToastLong(context, UIUtils.getString(R.string.location_permissions));
                                    }
                                });
            } else {
                startLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        weatherPresenter = new WeatherPresenter(this);
        deviceType = getIntent().getIntExtra("deviceType", 0);

        if (TextUtils.isEmpty(Constants.cityName)) {
            showCurrentCityView(false, "");
        } else {
            showCurrentCityView(true, Constants.cityName);

        }

        titleBarView.setTitle(UIUtils.getString(R.string.device_setting_weather));
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    public void showCurrentCityView(boolean isShowCityView, String strCityName) {
        if (isShowCityView) {
            tvCity.setVisibility(View.VISIBLE);
            tvCityTips.setVisibility(View.VISIBLE);
            tvNoLocation.setVisibility(View.GONE);
            tvCity.setText(strCityName);
        } else {
            tvCity.setVisibility(View.GONE);
            tvCityTips.setVisibility(View.GONE);
            tvNoLocation.setVisibility(View.VISIBLE);
        }
        //请求网络接口

        // weatherPresenter.getWeather(mLocationLatitude, mLocationLongitude, cityName, deviceType);

    }

    @Override
    public void onRespondError(String message) {

    }


    public void setLocationText(String str, String tag) {
        tvLocation.setTag(tag);
        tvLocation.setText(str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLSHelper != null) {
            mLSHelper.stopLocation();
        }
        EventBus.getDefault().unregister(this);
    }
}
