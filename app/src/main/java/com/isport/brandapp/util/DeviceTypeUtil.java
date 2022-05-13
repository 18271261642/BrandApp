package com.isport.brandapp.util;

import android.content.Context;
import android.text.TextUtils;

import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.bean.DeviceBean;

import java.util.ArrayList;

import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class DeviceTypeUtil {
    public static boolean isContainBrand() {
        if (AppConfiguration.deviceMainBeanList.size() > 0
                && (
                AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J)
        )) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainBrand(int type) {
        if (
                type == JkConfiguration.DeviceType.BRAND_W311 ||
                        type == JkConfiguration.DeviceType.Brand_W520
                        || type == JkConfiguration.DeviceType.BRAND_W307J
        ) {
            return true;
        } else {
            return false;
        }
    }


    public static String getRopeCurrentBindDeviceName() {
        String deviceName = "";
        if (AppConfiguration.deviceMainBeanList != null && AppConfiguration.deviceMainBeanList.size() > 0) {

            if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.ROPE_SKIPPING)) {
                deviceName = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.ROPE_SKIPPING).deviceName;
            }

        }
        return deviceName;
    }

    public static String getCurrentBindDeviceName() {
        String deviceName = "";
        if (AppConfiguration.deviceMainBeanList != null && AppConfiguration.deviceMainBeanList.size() > 0) {
            ArrayList<DeviceBean> list = new ArrayList<>();
            list.addAll(AppConfiguration.deviceMainBeanList.values());
            for (int i = 0; i < list.size(); i++) {
                DeviceBean deviceBean = list.get(i);
                if (deviceBean.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                    continue;
                } else {
                    deviceName = deviceBean.deviceName;
                }
            }
        }
        return deviceName;
    }

    public static int getCurrentBindDeviceType() {
        int deviceType = JkConfiguration.DeviceType.WATCH_W516;
        if (AppConfiguration.deviceBeanList != null && AppConfiguration.deviceBeanList.size() > 0) {
            ArrayList<DeviceBean> list = new ArrayList<>();
            list.addAll(AppConfiguration.deviceBeanList.values());
            for (int i = 0; i < list.size(); i++) {
                DeviceBean deviceBean = list.get(i);
                if (deviceBean.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                    continue;
                } else {
                    deviceType = deviceBean.deviceType;
                }
            }
        }
        return deviceType;
    }

    public static boolean isContainWrishBrand(int currentType) {
        if (currentType == JkConfiguration.DeviceType.BRAND_W311
                || currentType == JkConfiguration.DeviceType.Brand_W520
                || currentType == JkConfiguration.DeviceType.BRAND_W307J) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isContainWatchOrBracelet() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0 &&
                (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W814)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W813)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W819)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W817)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J)
                )) {
            return true;

        } else {
            return false;
        }
    }

    public static String getW81DeviceName(String name, String mac) {
        return name;
    }

    public static String getW81Mac(String name) {

        if(name.contains("BL")){    //名字为BL-F18X-XXX
            name = name.substring(3);
        }

        String macs[] = name.split("-");
        String mac = "";
        if (macs.length == 2) {
            mac = macs[1];
        }
        if (TextUtils.isEmpty(mac) || mac.length() != 12) {
            return "";
        }

        return Utils.resetDeviceMac(mac);
    }

    public static String getW526Mac(String name, int deviceType) {
        Logger.myLog(" getW526Mac:" + name + ",mac---------:" + name);
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        String macs;
        if (name.contains(" ")) {
            String[] strMacs = name.split(" ");
            if (strMacs.length == 2) {
                macs = strMacs[1];
            } else {
                return "";
            }
            //return "";
        } else {
            if (deviceType == JkConfiguration.DeviceType.Watch_W812B) {
                macs = name.substring(5, name.length());
            } else {
                macs = name.substring(4, name.length());
            }
        }
        macs = macs.trim();
        Logger.myLog(" getW526Mac:" + name + ",mac---------:" + name + "macs.lenght:" + macs);
        if (TextUtils.isEmpty(macs) || macs.length() != 12) {
            return "";
        }

        String strMac = Utils.resetDeviceMac(macs);
        Logger.myLog(" getW526Mac W526 CD0E3C9F72:" + name + ",mac---------:" + strMac);
        return strMac;
    }


    public static boolean isContainW516OrW311() {
        if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311) || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW516OrW520() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516) || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isW520X(int type) {
        if (type == JkConfiguration.DeviceType.Brand_W520
                || type == JkConfiguration.DeviceType.BRAND_W307J
        ) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean deviceWatch(int type) {
        if (type == JkConfiguration.DeviceType.WATCH_W516
                || type == JkConfiguration.DeviceType.Watch_W813
                || type == JkConfiguration.DeviceType.Watch_W556
                || type == JkConfiguration.DeviceType.Watch_W819
                || type == JkConfiguration.DeviceType.Watch_W812
                || type == JkConfiguration.DeviceType.Watch_W910
                || type == JkConfiguration.DeviceType.Watch_W817
                || type == JkConfiguration.DeviceType.Watch_W557
                || type == JkConfiguration.DeviceType.Watch_W812B
                || type == JkConfiguration.DeviceType.Watch_W560
                || type == JkConfiguration.DeviceType.Watch_W560B
                || type == JkConfiguration.DeviceType.Watch_F18
        ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean deviceBrand(int type) {
        if (type == JkConfiguration.DeviceType.BRAND_W311
                || type == JkConfiguration.DeviceType.Brand_W520
                || type == JkConfiguration.DeviceType.BRAND_W307J
                || type == JkConfiguration.DeviceType.Brand_W814
                || type == JkConfiguration.DeviceType.Brand_W811) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContaintW81(int deviceType) {
        return isContainW81(deviceType);
    }

    public static boolean isContainF18(){
        return null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0 && AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_F18);
    }

    public static boolean isConnectF18(int deviceType){
        return deviceType == JkConfiguration.DeviceType.Watch_F18;
    }

    public static boolean isContainF18(int deviceType){
        return null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0 && AppConfiguration.deviceMainBeanList.containsKey(deviceType) && (AppConfiguration.deviceMainBeanList.get(deviceType).getDeviceType() == JkConfiguration.DeviceType.Watch_F18);
    }


    public static boolean isContainWatch() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516) ||
                AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)
        )) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainWatchW556() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && (
                AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)
                        || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)
        )) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainBody() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0 && AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainBody(int deviceType) {
        if (deviceType == JkConfiguration.DeviceType.BODYFAT) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isContainRope() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0 && AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.ROPE_SKIPPING)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainRope(int currentType) {
        if (currentType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isContainW814W813W819(int currentType) {
        if (currentType == JkConfiguration.DeviceType.Brand_W814
                || currentType == JkConfiguration.DeviceType.Watch_W813
                || currentType == JkConfiguration.DeviceType.Watch_W819) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW812W817W819(int currentType) {
        if (currentType == JkConfiguration.DeviceType.Watch_W812
                || currentType == JkConfiguration.DeviceType.Watch_W817
                || currentType == JkConfiguration.DeviceType.Watch_W819
                || currentType == JkConfiguration.DeviceType.Watch_W910
        ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW307J() {

        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J))) {

            return true;
        }
        return false;
    }

    public static boolean isContainW81() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W813)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W814)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W819)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W817)
        ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW81(int currentType) {
        if (currentType == JkConfiguration.DeviceType.Watch_W812
                || currentType == JkConfiguration.DeviceType.Watch_W813
                || currentType == JkConfiguration.DeviceType.Watch_W819
                || currentType == JkConfiguration.DeviceType.Brand_W814
                || currentType == JkConfiguration.DeviceType.Watch_W910
                || currentType == JkConfiguration.DeviceType.Watch_W817
        ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW55X(int currentType) {
        if (currentType == JkConfiguration.DeviceType.Watch_W557
                || currentType == JkConfiguration.DeviceType.Watch_W556
                || currentType == JkConfiguration.DeviceType.Watch_W812B
                || currentType == JkConfiguration.DeviceType.Watch_W560B
                || currentType == JkConfiguration.DeviceType.Watch_W560
                || currentType == JkConfiguration.DeviceType.ROPE_SKIPPING
        ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW55X() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)
        )) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW55XX() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)
                ||AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)
        )) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW557() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)) {
            return true;
        } else {
            return false;
        }
    }

    //W516 W311 W520 W560 W557 W812B  有心率
    public static boolean isContainsHr() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)
        )) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isShowRealSwitch() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && ((AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)
        )
        )) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW556OrW812B() {
        if (null != AppConfiguration.deviceMainBeanList && AppConfiguration.deviceMainBeanList.size() > 0
                && (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)
                || AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)
        )) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainW556OrW812B(String deviceName) {
        if (TextUtils.isEmpty(deviceName)) {
            return false;
        }
        String[] names = deviceName.split(" ");
        if (names[0].equals(Constants.WATCH_812B_FILTER)
                || names[0].equals(Constants.WATCH_556_FILTER)
                || names[0].equals(Constants.WATCH_560B_FILTER)) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isContainF18(String deviceName) {
        if (TextUtils.isEmpty(deviceName)) {
            return false;
        }
        if(deviceName.startsWith("BL")){
            return true;
        }

        String[] names = deviceName.split("-");
        return names[0].equals(Constants.WATCH_7018_FILTER);
    }


    public static boolean isContainWatch(int current) {
        if (current == JkConfiguration.DeviceType.WATCH_W516
                || current == JkConfiguration.DeviceType.Watch_W556
                || current == JkConfiguration.DeviceType.Watch_W557
                || current == JkConfiguration.DeviceType.Watch_W812B
                || current == JkConfiguration.DeviceType.Watch_W560B
                || current == JkConfiguration.DeviceType.Watch_W560
        ) {
            return true;
        } else {
            return false;
        }
    }


    public static void clearDevcieInfo(Context context) {
        AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
        AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, "");
        AppSP.putString(context, AppSP.WATCH_MAC, "");

    }


    public static DeviceBean getRopeDevice() {
        DeviceBean mDeviceBean = null;
        if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.ROPE_SKIPPING)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.ROPE_SKIPPING);
        }
        return mDeviceBean;
    }

    public static DeviceBean getBindDevcie() {
        DeviceBean mDeviceBean = null;
        if(AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_F18)){
            return AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_F18);
        }

        if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.WATCH_W516);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.BRAND_W311);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Brand_W520);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W812);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W813)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W813);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W819)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W819);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W910);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W556);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W814)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Brand_W814);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W817)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W817);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W557);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W812B);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W560B);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_W560);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.BRAND_W307J);
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.ROPE_SKIPPING)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.ROPE_SKIPPING);
        }
        return mDeviceBean;
    }
}
