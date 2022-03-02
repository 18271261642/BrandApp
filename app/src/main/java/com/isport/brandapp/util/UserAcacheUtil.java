package com.isport.brandapp.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.brandapp.device.bracelet.playW311.bean.PlayBean;

import java.util.ArrayList;
import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.ACache;
import phone.gym.jkcq.com.commonres.common.AllocationApi;

public class UserAcacheUtil {


    public static void removeUserInfo(String key) {

        if (TextUtils.isEmpty(key)) {
            return;
        }
        ACache aCache = ACache.get(BaseApp.getApp());
        aCache.remove(AllocationApi.BaseUrl + key);
        aCache.clear();
    }

    public static void clearAll() {
        SyncCacheUtils.clearSysData(BaseApp.getApp());
        SyncCacheUtils.clearSetting(BaseApp.getApp());
        SyncCacheUtils.clearFirstBindW311(BaseApp.getApp());
        SyncCacheUtils.clearSysData(BaseApp.getApp());
        ACache aCache = ACache.get(BaseApp.getApp());
        aCache.clear();
    }


    public static void savePlayBandInfo(int key, List<PlayBean> list) {
        try {
            if (!TextUtils.isEmpty("devcieplay" + key)) {
                ACache aCache = ACache.get(BaseApp.getApp());
                Gson mGson = new Gson();
                Logger.myLog("mGson.toJson(list)" + mGson.toJson(list));
                aCache.put("devcieplay" + key, mGson.toJson(list));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<PlayBean> getPlayBandImagelist(int deviceType) {
        ArrayList<PlayBean> playBeans = new ArrayList<>();
        try {
            ACache aCache = ACache.get(BaseApp.getApp());
            String getValue = aCache.getAsString("devcieplay" + deviceType);
            playBeans = new Gson().fromJson(getValue, new TypeToken<ArrayList<PlayBean>>() {
            }.getType());
            Logger.myLog("mGson.toJson(list) getPlayBandImagelist" + playBeans);

        } catch (Exception e) {
            playBeans = new ArrayList<>();

        } finally {
            if (playBeans == null) {
                playBeans = new ArrayList<>();
            }
            return playBeans;
        }
    }


    public static boolean getFirstOpenApp() {
        ACache aCache = ACache.get(BaseApp.getApp());
        String first = aCache.getAsString("isFirstOpenApp");
        if (TextUtils.isEmpty(first)) {
            return true;
        } else {
            return false;
        }
    }

    public static void saveFirstOpenApp(boolean isFisrt) {
        ACache aCache = ACache.get(BaseApp.getApp());
        aCache.put("isFirstOpenApp", isFisrt);
    }


    public static void saveHrRemind() {
        ACache aCache = ACache.get(BaseApp.getApp());
        aCache.put("hr_remind", "true", 10);
    }


    public static void saveRopeTime(int sec) {

        Logger.myLog("saveRopeTime" + sec);
        ACache aCache = ACache.get(BaseApp.getApp());
        aCache.put("rope_time", "true", sec);
    }

    public static boolean isRopeTime() {
        ACache aCache = ACache.get(BaseApp.getApp());
        String result = aCache.getAsString("rope_time");
        if (result == null || TextUtils.isEmpty(result) || !result.equals("true")) {
            return true;
        }
        return false;
    }

    public static boolean isHrRemind() {
        ACache aCache = ACache.get(BaseApp.getApp());
        String result = aCache.getAsString("hr_remind");
        if (result == null || TextUtils.isEmpty(result) || !result.equals("true")) {
            return true;
        }
        return false;
    }

    public static boolean isPaceRemind() {
        ACache aCache = ACache.get(BaseApp.getApp());
        String result = aCache.getAsString("pace_remind");
        if (result == null || TextUtils.isEmpty(result) || !result.equals("true")) {
            return true;
        }
        return false;
    }

    public static void savePaceRemind() {
        ACache aCache = ACache.get(BaseApp.getApp());
        aCache.put("pace_remind", "true", 15);
    }

}
