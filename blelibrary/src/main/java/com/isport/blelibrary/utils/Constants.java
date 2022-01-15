package com.isport.blelibrary.utils;

import android.view.KeyEvent;

import com.crrepa.ble.conn.type.CRPWeatherId;
import com.isport.blelibrary.entry.WristbandWeather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @Author
 * @Date 2018/9/30
 * @Fuction
 */

public class Constants {

    public static boolean W55XHrState = false;
    public static float mLocationLatitude, mLocationLongitude, speed;
    public static int gpstype,locationType;
    public static float accuracy;
    public static String cityName = "", currentCountry = "";

    public static WristbandWeather wristbandWeather;

    public static HashMap<String, Integer> W81WeatherConfig = new HashMap<>();
    public static HashMap<String, Integer> W520WeatherConfig = new HashMap<>();


    static {
        W81WeatherConfig.put("CLOUDY", CRPWeatherId.CLOUDY);
        W81WeatherConfig.put("FOGGY", CRPWeatherId.FOGGY);
        W81WeatherConfig.put("OVERCAST", CRPWeatherId.OVERCAST);
        W81WeatherConfig.put("RAINY", CRPWeatherId.RAINY);
        W81WeatherConfig.put("SNOWY", CRPWeatherId.SNOWY);
        W81WeatherConfig.put("SUNNY", CRPWeatherId.SUNNY);
        W81WeatherConfig.put("SANDSTORM", CRPWeatherId.SANDSTORM);
        W81WeatherConfig.put("HAZE", CRPWeatherId.HAZE);

        /**
         * 天气编号：00：无 01：晴天 02：多云 03：阴天 04：雨天 05：雷 雨 06：有雪 07：有风  08：有雾/霾 09：沙尘暴 空气质量（AQI）00：无 01:优 02：良 03：轻度 04：中度 05：重 度 06：严重
         */
        W520WeatherConfig.put("CLOUDY", 2);
        W520WeatherConfig.put("FOGGY", 8);//有雾/霾
        W520WeatherConfig.put("OVERCAST", 3);//阴天
        W520WeatherConfig.put("RAINY", 4);//雨天
        W520WeatherConfig.put("SNOWY", 6);//有雪
        W520WeatherConfig.put("SUNNY", 1);
        W520WeatherConfig.put("SANDSTORM", 9);
        W520WeatherConfig.put("HAZE", 8);//有雾/霾


    }


    public static volatile Boolean isDFU = false;//是否是升级模式
    public static volatile Boolean tempConnected = false;//是否是升级模式
    public static volatile Boolean isSyncUnbind = false;//是否是升级模式
    public static volatile Boolean isSyncData = false;//是否在上传数据
    public static String SHOWSCALEDATA = "showScaleData";//是否是升级模式
    /**
     * if you want to show log, let it be true ,or false
     * before you release the app, set it false first!
     */


    public static int SYNC_HEART_STATE_SUCCESS = 1;
    public static int SYNC_HEART_STATE_FAIL = 0;
    public static int SYNC_HEART_NODATA = 2;


    public static String defStartTime = "07:00";
    public static String defEndTime = "22:00";


    public static int W516defPerid = 1;
    public static int W81defPerid = 60;
    public static int W520defPerid = 30;

    public static int defStarHour = 7;
    public static int defStartMin = 0;

    public static int defEndHour = 22;
    public static int defEndMin = 0;
    public static String defStrStarHour = "7";
    public static String defStrStartMin = "0";

    public static String defStrEndHour = "22";
    public static String defStrEndMin = "0";


    public static boolean MUSIC_DEFAULT = true;


    /**
     * play media
     */
    public static int MEDIA_PLAY = KeyEvent.KEYCODE_MEDIA_PLAY;
    /**
     * stop media
     */
    public static int MEDIA_STOP = KeyEvent.KEYCODE_MEDIA_STOP;
    /**
     * previous
     */
    public static int MEDIA_PREVIOUS = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
    /**
     * next
     */
    public static int MEDIA_NEXT = KeyEvent.KEYCODE_MEDIA_NEXT;
    /**
     * pause
     */
    public static int MEDIA_PAUSE = KeyEvent.KEYCODE_MEDIA_PAUSE;


    public static boolean IS_DEBUG = true;

    public static volatile boolean CAN_RECONNECT = false;

    //    public static String SCALE_FILTER = "Chipsea";
    public static String SCALE_FILTER = "MZ";
    public static String SLEEP_FILTER = "Z2";
    public static String BRAND_FILTER = "W311";
    public static String BRAND_W311N_FILTER = "W311";
    public static String BRAND_W307J_FILTER = "W307J";
    public static String BRAND_520_FILTER = "W520";
    public static String WATCH_812_FILTER = "W812";
    public static String WATCH_813_FILTER = "W813";

    public static String WATCH_556_FILTER = "W556";
    public static String WATCH_557_FILTER = "W557";
    public static String WATCH_560B_FILTER = "W560B";
    public static String WATCH_560_FILTER = "W560";
    public static String WATCH_W560_FILTER_2 = "FT_ReflexSW";

    //F18
    public static String WATCH_7018_FILTER = "F18";

    public static String WATCH_819_FILTER = "W819";
    public static String WATCH_910_FILTER = "P 4";
    public static String WATCH_9101_FILTER = "P";
    public static String WATCH_9102_FILTER = "P4";
    public static String BRAND_814_FILTER = "W814";
    public static String BRAND_811_FILTER = "W811";
    public static String WATCH_817_FILTER = "W817";
    public static String WATCH_812B_FILTER = "W812B";
    public static String ROPE_S002_FILTER = "S002";
    public static String DFU_MODE = "DFU";
    public static String DFU_W814_MODE = "HUNTERSUN-BLE";
    //    public static String SLEEP_FILTER="Sleepace";
    public static String WATCH_FILTER = "W516";

    public static int SCAN_DURATION = 10 * 1000;
    public static int CONNECT_DURATION = 10 * 1000;
    public static int COMMON_DURATION = 1 * 1000;

    public static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public final static UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    public final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString
            ("00002A19-0000-1000-8000-00805f9b34fb");

    public final static UUID DEVICEINFORMATION_SERVICE = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    public final static UUID FIRMWAREREVISION_CHARACTERISTIC = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb");

    //体脂称
    //非锁定数据
    public static final UUID MAIN_SERVICE_UUID = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID WRITE_CHAR_UUID = UUID
            .fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    public static final UUID NOTIFY_CHAR_UUID = UUID
            .fromString("0000fff1-0000-1000-8000-00805f9b34fb");

    //锁定数据
    public static final UUID LOCK_MAIN_SERVICE_UUID = UUID
            .fromString("0000181b-0000-1000-8000-00805f9b34fb");
    public static final UUID LOCK_INDICATE_CHAR_UUID = UUID
            .fromString("00002a9c-0000-1000-8000-00805f9b34fb");
    //睡眠带，SDK已经封装

    //诺嘉源手表
    public static final UUID HPLUS_SERVICE_UUID = UUID
            .fromString("14701820-620a-3973-7c78-9cfff0876abd");
    public static final UUID HPLUS_WRITE_UUID = UUID
            .fromString("14702856-620a-3973-7c78-9cfff0876abd");
    public static final UUID HPLUS_NOTIFY_UUID = UUID
            .fromString("14702853-620a-3973-7c78-9cfff0876abd");

    //W516
    public static final UUID mainW516UUID = UUID.fromString("7658fd00-878a-4350-a93e-da553e719ed0");
    public static final UUID sendW516UUID = UUID.fromString("7658fd01-878a-4350-a93e-da553e719ed0");
    public static final UUID responceW516UUID = UUID.fromString("7658fd02-878a-4350-a93e-da553e719ed0");
    public static final UUID receiveDataW516UUID = UUID.fromString("7658fd03-878a-4350-a93e-da553e719ed0");
    public static final UUID realTimeDataW516UUID = UUID.fromString("7658fd04-878a-4350-a93e-da553e719ed0");

    /**
     * Heart rate service
     */
    public final static UUID HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    public final static UUID HEART_RATE_CHARACTERISTIC = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

    public static List<String> strPkNames = new ArrayList<String>();

    static {
        strPkNames.add(Constants.KEY_13_PACKAGE);
        strPkNames.add(Constants.KEY_14_PACKAGE);
        strPkNames.add(Constants.KEY_15_PACKAGE);
        strPkNames.add(Constants.KEY_15_PACKAGE_1);
        strPkNames.add(Constants.KEY_15_PACKAGE_2);
        strPkNames.add(Constants.KEY_16_PACKAGE);
        strPkNames.add(Constants.KEY_17_PACKAGE);
        strPkNames.add(Constants.KEY_18_PACKAGE);
        strPkNames.add(Constants.KEY_19_PACKAGE);
        strPkNames.add(Constants.KEY_1A_PACKAGE);
        strPkNames.add(Constants.KEY_1B_PACKAGE);
    }

    public final static String KEY_13_PACKAGE = "com.tencent.mobileqq";///qq
    public final static String KEY_14_PACKAGE = "com.tencent.mm";///wechat
    public final static String KEY_15_PACKAGE = "com.skype.raider";///skype
    public final static String KEY_15_PACKAGE_1 = "com.skype.polaris";///skype
    public final static String KEY_15_PACKAGE_2 = "com.skype.rover";///skype
    public final static String KEY_16_PACKAGE = "com.facebook.katana";//facebook
    public final static String KEY_17_PACKAGE = "com.twitter.android";//twitter
    public final static String KEY_18_PACKAGE = "com.linkedin.android";//linkin
    public final static String KEY_19_PACKAGE = "com.instagram.android";//instagram
    public final static String KEY_1A_PACKAGE = "life.inovatyon.ds";
    public final static String KEY_1B_PACKAGE = "com.whatsapp";

    //W311

    public static final UUID UUID_MAIN_SERVICE = UUID.fromString("d0a2ff00-2996-d38b-e214-86515df5a1df");

    public static final UUID UUID_SEND_DATA_CHAR = UUID.fromString("7905ff01-b5ce-4e99-a40f-4b1e122d00d0");
    public static final UUID UUID_RECEIVE_DATA_CHAR = UUID.fromString("7905ff02-b5ce-4e99-a40f-4b1e122d00d0");
    public static final UUID UUID_REALTIME_RECEIVE_DATA_CHAR = UUID.fromString("7905ff04-b5ce-4e99-a40f-4b1e122d00d0");

}
