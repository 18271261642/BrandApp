package com.isport.brandapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.isport.blelibrary.utils.Constants;

import java.util.HashMap;

/**
 * @Author
 * @Date 2018/10/17
 * @Fuction
 */

public class AppSP {

    //是否查看过隐私政策或第一次打开app
    public static String IS_FIRST_OPEN_APP = "is_first_app";


    public static String APP_CONFIG = "SCALE_CONFIG";
    public static String DEVICE_CURRENTDEVICETYPE = "DEVICE_CURRENTDEVICETYPE";//当前设备type
    public static String DEVICE_CURRENTNAME = "DEVICE_CURRENTNAME";//当前设备的name
    public static String SCALE_MAC = "SCALE_MAC";
    public static String SLEEP_MAC = "SLEEP_MAC";
    public static String WATCH_MAC = "WATCH_MAC";
    public static String IS_FIRST = "IS_FIRST";
    public static String CAN_RECONNECT = "CAN_RECONNECT";
    public static String FORM_DFU = "FORM_DFU";

    //F18保存Mac
    public static final String F18_SAVE_MAC = "f18_mac";

    //目标类型key,0：运动目标；1：距离目标：2：卡路里目标
    public static final String DEVICE_GOAL_KEY = "device_goal_key";
    //目标

    //电话提醒开关 0 关，1 开
    public static final String F18_PHONE_ALERT = "f18_phone_status";

    public static HashMap macMap = new HashMap<String, String>();

    private static AppSP sInstance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(APP_CONFIG, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public static void putString(Context context, String key, String defValue) {
        init(context);
        editor.putString(key, defValue).commit();
    }

    public static void putInt(Context context, String key, int defValue) {
        init(context);
        editor.putInt(key, defValue).commit();
    }

    public static void putBoolean(Context context, String key, boolean defValue) {
        init(context);
        if (AppSP.CAN_RECONNECT.equals(key)) {
            Constants.CAN_RECONNECT = defValue;
        }
        editor.putBoolean(key, defValue).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        init(context);
        return sharedPreferences.getInt(key, defValue);
    }

    public static String getString(Context context, String key, String defValue) {
        init(context);
        return sharedPreferences.getString(key, defValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        init(context);
        return sharedPreferences.getBoolean(key, defValue);
    }
}
