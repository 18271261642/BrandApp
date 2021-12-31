package com.isport.brandapp.bean;

import java.io.Serializable;

public class DeviceBean implements Serializable {

    /**
     * "deviceId": 29,
     * "deviceType": 0,
     * "mac": "1",
     * "deviceName": "2",
     * "deviceImgurl": null
     */
    public int deviceId;
    public int deviceType;
    public String mac;
    public String deviceName;
    public String deviceID;//本地设备唯一标示
    public String deviceImgurl;
    public long timeTamp;//绑定时间戳
    public int battery;

    public int currentType;
    public boolean connectState;
    public SportBean sportBean;
    public SleepBean sleepBean;
    public WeightBean weightBean;
    public BrandBean brandBean;
    public String scanName;
    public int resId;
    public int index;
    public int resBg;


    public DeviceBean() {

    }


    public DeviceBean(int currentType, String scanName, int resId) {
        this.currentType = currentType;
        this.scanName = scanName;
        this.resId = resId;
    }
    public DeviceBean(int currentType,int bgRes, String scanName, int resId) {
        this.currentType = currentType;
        this.scanName = scanName;
        this.resId = resId;
        this.resBg=bgRes;
    }
    public DeviceBean(int currentType, String scanName, int resId,int index) {
        this.currentType = currentType;
        this.scanName = scanName;
        this.resId = resId;
        this.index=index;
    }

    public DeviceBean(int currentType) {
        this.currentType = currentType;
    }

    public DeviceBean(int currentType, String name) {
        this.currentType = currentType;
        this.deviceName = name;
    }

    public DeviceBean(int currentType, String name, String scanName, int resId) {
        this.currentType = currentType;
        this.deviceName = name;
        this.scanName = scanName;
        this.resId = resId;
        this.index=index;
    }


    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceImgurl() {
        return deviceImgurl;
    }

    public void setDeviceImgurl(String deviceImgurl) {
        this.deviceImgurl = deviceImgurl;
    }

    public long getTimeTamp() {
        return timeTamp;
    }

    public void setTimeTamp(long timeTamp) {
        this.timeTamp = timeTamp;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getCurrentType() {
        return currentType;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
    }

    public boolean isConnectState() {
        return connectState;
    }

    public void setConnectState(boolean connectState) {
        this.connectState = connectState;
    }

    public SportBean getSportBean() {
        return sportBean;
    }

    public void setSportBean(SportBean sportBean) {
        this.sportBean = sportBean;
    }

    public SleepBean getSleepBean() {
        return sleepBean;
    }

    public void setSleepBean(SleepBean sleepBean) {
        this.sleepBean = sleepBean;
    }

    public WeightBean getWeightBean() {
        return weightBean;
    }

    public void setWeightBean(WeightBean weightBean) {
        this.weightBean = weightBean;
    }

    public BrandBean getBrandBean() {
        return brandBean;
    }

    public void setBrandBean(BrandBean brandBean) {
        this.brandBean = brandBean;
    }

    public String getScanName() {
        return scanName;
    }

    public void setScanName(String scanName) {
        this.scanName = scanName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getResBg() {
        return resBg;
    }

    public void setResBg(int resBg) {
        this.resBg = resBg;
    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "deviceId=" + deviceId +
                ", deviceType=" + deviceType +
                ", mac='" + mac + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceID='" + deviceID + '\'' +
                ", deviceImgurl='" + deviceImgurl + '\'' +
                ", timeTamp=" + timeTamp +
                ", battery=" + battery +
                ", currentType=" + currentType +
                ", connectState=" + connectState +
                ", sportBean=" + sportBean +
                ", sleepBean=" + sleepBean +
                ", weightBean=" + weightBean +
                ", brandBean=" + brandBean +
                ", scanName='" + scanName + '\'' +
                ", ResId=" + resId +
                '}';
    }
}
