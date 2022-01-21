package com.isport.blelibrary.db.table.f18;

/**
 * F18设置相关bean
 * Created by Admin
 * Date 2022/1/17
 */

public class F18DeviceSetData {


    //userId
    private String userId;
    //设备id
    private String deviceMac;
    //计步目标
    private int stepGoal;
    //距离目标
    private int distanceGoal;
    //卡路里目标
    private int kcalGoal;
    //已设置闹钟个数
    private int alarmCount;
    //电量
    private int battery;
    //联系人设置了几个
    private int contactNumber;
    //时间格式0-12小时，1-24小时制
    private int timeStyle;
    //天气设置，城市
    private String cityName;
    //温度单位,0-摄氏度；1-华摄氏度
    private int tempStyle;
    //公英制
    private int isKmUnit;
    //24小时心率是否打开
    private boolean is24Heart;
    //加强测量开关
    private boolean strengthMeasure;
    //连续监测 未开启为0，开启后展示时间 eg:12:10~13:00
    private String continuMonitor;
    //抬腕亮屏 未开启为0，开启后展示时间 eg:12:10~13:00
    private String turnWrist;
    //来电提醒
    private boolean isPhoneAlert;
    //短信提醒
    private boolean isSmsAlert;
    //APP消息提醒总开关
    private boolean allAppMsgStatus;
    //APP消息提醒 个数
    private int appMsgs;
    //久坐提醒 未开启是为0；开启后展示时间 eg:12:10~13:00
    private String longSitStr;
    //勿扰模式 未开启为0，开启后展示时间 eg:12:10~13:00
    private String DNT;
    //喝水提醒 未开启为0，开启后展示时间 eg:12:10~13:00
    private String drinkAlert;
    //固件版本名称
    private String deviceVersionName;
    //固件版本
    private int deviceVersionCode;

    //预留一个属性
    private String overStr;


    public F18DeviceSetData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getDistanceGoal() {
        return distanceGoal;
    }

    public void setDistanceGoal(int distanceGoal) {
        this.distanceGoal = distanceGoal;
    }

    public int getKcalGoal() {
        return kcalGoal;
    }

    public void setKcalGoal(int kcalGoal) {
        this.kcalGoal = kcalGoal;
    }

    public int getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(int contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getTimeStyle() {
        return timeStyle;
    }

    public void setTimeStyle(int timeStyle) {
        this.timeStyle = timeStyle;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getTempStyle() {
        return tempStyle;
    }

    public void setTempStyle(int tempStyle) {
        this.tempStyle = tempStyle;
    }

    public int getIsKmUnit() {
        return isKmUnit;
    }

    public void setIsKmUnit(int isKmUnit) {
        this.isKmUnit = isKmUnit;
    }

    public boolean isIs24Heart() {
        return is24Heart;
    }

    public void setIs24Heart(boolean is24Heart) {
        this.is24Heart = is24Heart;
    }

    public boolean isStrengthMeasure() {
        return strengthMeasure;
    }

    public void setStrengthMeasure(boolean strengthMeasure) {
        this.strengthMeasure = strengthMeasure;
    }

    public String getContinuMonitor() {
        return continuMonitor;
    }

    public void setContinuMonitor(String continuMonitor) {
        this.continuMonitor = continuMonitor;
    }

    public String getTurnWrist() {
        return turnWrist;
    }

    public void setTurnWrist(String turnWrist) {
        this.turnWrist = turnWrist;
    }

    public boolean isPhoneAlert() {
        return isPhoneAlert;
    }

    public void setPhoneAlert(boolean phoneAlert) {
        isPhoneAlert = phoneAlert;
    }

    public boolean isSmsAlert() {
        return isSmsAlert;
    }

    public void setSmsAlert(boolean smsAlert) {
        isSmsAlert = smsAlert;
    }

    public boolean isAllAppMsgStatus() {
        return allAppMsgStatus;
    }

    public void setAllAppMsgStatus(boolean allAppMsgStatus) {
        this.allAppMsgStatus = allAppMsgStatus;
    }

    public int getAppMsgs() {
        return appMsgs;
    }

    public void setAppMsgs(int appMsgs) {
        this.appMsgs = appMsgs;
    }

    public String getLongSitStr() {
        return longSitStr;
    }

    public void setLongSitStr(String longSitStr) {
        this.longSitStr = longSitStr;
    }

    public String getDNT() {
        return DNT;
    }

    public void setDNT(String DNT) {
        this.DNT = DNT;
    }

    public String getDrinkAlert() {
        return drinkAlert;
    }

    public void setDrinkAlert(String drinkAlert) {
        this.drinkAlert = drinkAlert;
    }

    public String getDeviceVersionName() {
        return deviceVersionName;
    }

    public void setDeviceVersionName(String deviceVersionName) {
        this.deviceVersionName = deviceVersionName;
    }

    public int getDeviceVersionCode() {
        return deviceVersionCode;
    }

    public void setDeviceVersionCode(int deviceVersionCode) {
        this.deviceVersionCode = deviceVersionCode;
    }

    public String getOverStr() {
        return overStr;
    }

    public void setOverStr(String overStr) {
        this.overStr = overStr;
    }

    @Override
    public String toString() {
        return "F18DeviceSetData{" +
                "userId='" + userId + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", stepGoal=" + stepGoal +
                ", distanceGoal=" + distanceGoal +
                ", kcalGoal=" + kcalGoal +
                ", alarmCount=" + alarmCount +
                ", battery=" + battery +
                ", contactNumber=" + contactNumber +
                ", timeStyle=" + timeStyle +
                ", cityName='" + cityName + '\'' +
                ", tempStyle=" + tempStyle +
                ", isKmUnit=" + isKmUnit +
                ", is24Heart=" + is24Heart +
                ", strengthMeasure=" + strengthMeasure +
                ", continuMonitor='" + continuMonitor + '\'' +
                ", turnWrist='" + turnWrist + '\'' +
                ", isPhoneAlert=" + isPhoneAlert +
                ", isSmsAlert=" + isSmsAlert +
                ", allAppMsgStatus=" + allAppMsgStatus +
                ", appMsgs=" + appMsgs +
                ", longSitStr='" + longSitStr + '\'' +
                ", DNT='" + DNT + '\'' +
                ", drinkAlert='" + drinkAlert + '\'' +
                ", deviceVersionName='" + deviceVersionName + '\'' +
                ", deviceVersionCode=" + deviceVersionCode +
                ", overStr='" + overStr + '\'' +
                '}';
    }
}
