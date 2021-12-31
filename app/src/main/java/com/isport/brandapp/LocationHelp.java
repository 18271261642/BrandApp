package com.isport.brandapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;

import org.greenrobot.eventbus.EventBus;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.UIUtils;

public class LocationHelp {

    public float mLocationLatitude, mLocationLongitude, speed;
    String city;
    public float accuracy;
    public int gpsstate;
    public int type;
    public AMapLocationClientOption option;
    public MyLocationStyle myLocationStyle;
    public static volatile AMapLocationClient mLocationClient = null;

    static LocationHelp instance;

    private NotificationManager notiManager;

    public static LocationHelp getInstance() {
        if (instance == null) {
            synchronized (ISportAgent.class) {
                if (instance == null) {
                    instance = new LocationHelp();
                }
            }
        }
        return instance;
    }


    public void initLocation() {
        if (mLocationClient != null) {
            return;
        }
        //初始化定位

        mLocationClient = new AMapLocationClient(BaseApp.getApp());

        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        mLocationLatitude = (float) amapLocation.getLatitude();
                        mLocationLongitude = (float) amapLocation.getLongitude();
                        gpsstate = amapLocation.getGpsAccuracyStatus();
                        accuracy = amapLocation.getAccuracy();
                        speed = amapLocation.getSpeed();
                        type = 0;
                        if (accuracy == 0) {
                            type = 0;
                        } else if (accuracy > 0 && accuracy <= 65) {
                            type = 3;
                        } else if (accuracy > 65 && accuracy <= 200) {
                            type = 2;

                        } else {
                            type = 1;
                        }
                        city = amapLocation.getCity();
                        if (TextUtils.isEmpty(city)) {
                            return;
                        }
                        Constants.speed = speed;
                        Constants.mLocationLongitude = mLocationLongitude;
                        Constants.mLocationLatitude = mLocationLatitude;
                        Constants.cityName = city;
                        Constants.currentCountry = amapLocation.getCountry();

//                        Log.e("BaseApp AmapError", " Constants.cityName:"
//                                + Constants.cityName + ",  Constants.mLocationLongitude "
//                                + Constants.mLocationLongitude + "Constants.mLocationLatitude :" + Constants.mLocationLatitude + "type:" + type + "accuracy:" + accuracy);

                        EventBus.getDefault().post(new MessageEvent(MessageEvent.update_location));

                        mLocationClient.stopLocation();
                    } else {

                        EventBus.getDefault().post(new MessageEvent(MessageEvent.update_location_error));
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
//                        Log.e("BaseApp AmapError", "location Error, ErrCode:"
//                                + amapLocation.getErrorCode() + ", errInfo:"
//                                + amapLocation.getErrorInfo() + "type:" + type);
                    }
                }
            }
        });
        option = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        if (AppUtil.isZh(BaseApp.getApp())) {
            option.setGeoLanguage(AMapLocationClientOption.GeoLanguage.ZH);
        } else {
            option.setGeoLanguage(AMapLocationClientOption.GeoLanguage.EN);
        }
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        //option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        // option.setOnceLocation(true);
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        option.setInterval(3000);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(3000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(UIUtils.getResources(), R.drawable.icon_mark));
        myLocationStyle.myLocationIcon(bitmapDescriptor);
        // MyLocationStyle.LOCATION_TYPE_MAP_ROTATE
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        //  myLocationStyle.strokeColor(getResources().getColor(R.color.transparent));
        //  myLocationStyle.radiusFillColor(getResources().getColor(R.color.transparent));
        myLocationStyle.showMyLocation(true);
       // startBack();
        //endBack(true);

    }


    public void endBack(boolean isStart) {
        //mLocationClient.disableBackgroundLocation(isStart);
    }

    public void startBack() {
        Notification.Builder builder = new Notification.Builder(BaseApp.getApp().getApplicationContext()); //获取一个Notification构造器
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel("notification_id", "bonlala", NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notiManager = (NotificationManager) BaseApp.getApp().getSystemService(BaseApp.getApp().NOTIFICATION_SERVICE);
            notiManager.createNotificationChannel(notificationChannel);
            builder.setChannelId("notification_id");
        }
        builder.setAutoCancel(true);//用户点击就自动消失
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        mLocationClient.enableBackgroundLocation(112, notification);
        // notiManager.notify(110 /* ID of notification */, notification);  //这就是那个
        // 参数一：唯一的通知标识；参数二：通知消息。
    }

    public void startOnece() {
        if (mLocationClient != null) {
            if (mLocationClient.isStarted()) {
                mLocationClient.stopLocation();
                mLocationClient.startLocation();
            } else {
                mLocationClient.startLocation();
            }
        }
    }

    public void startLocation() {

        Logger.myLog("Constants.mLocationLatitude:");

        if (mLocationClient != null) {
            if (mLocationClient.isStarted()) {
                mLocationClient.stopLocation();
                mLocationClient.startLocation();
            } else {
                mLocationClient.startLocation();
            }
        }

    }

    public void stopLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stopLocation();
        }
    }

}
