package com.isport.blelibrary.deviceEntry.impl;

import android.content.Context;

import com.isport.blelibrary.deviceEntry.interfaces.IBraceletW311;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.deviceEntry.interfaces.ITarget;
import com.isport.blelibrary.deviceEntry.interfaces.IWatchW557;
import com.isport.blelibrary.entry.AlarmEntry;
import com.isport.blelibrary.entry.DisplaySet;
import com.isport.blelibrary.entry.NotificationMsg;
import com.isport.blelibrary.entry.SedentaryRemind;
import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.managers.Watch7018Manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * Date 2022/1/14
 */
public class W7018Device extends BaseDevice implements IDeviceType, ITarget, IBraceletW311, IWatchW557 {

    public W7018Device(String name,String mac) {
        super();
        this.deviceName = name;
        this.address = mac;
        setType();
    }

    public W7018Device(String name) {
        super();
        this.deviceName = name;
        setType();
    }


    @Override
    public void disconnect(boolean reConnect) {
        Watch7018Manager.getWatch7018Manager().disConnectDevice();
    }

    @Override
    public void connect(boolean isConnectByUser) {
        Watch7018Manager.getWatch7018Manager().connectDevice(this.getAddress(),isConnectByUser);
    }

    @Override
    public BaseManager getManager(Context context) {
        return Watch7018Manager.getWatch7018Manager(context);
    }

    @Override
    public void exit() {

    }

    @Override
    public void getBattery() {
        Watch7018Manager.getWatch7018Manager().getDeviceBattery();
    }

    @Override
    public void setType() {
        this.deviceType = TYPE_WATCH_7018;
    }

    @Override
    public void syncTodayData() {

    }

    @Override
    public void queryWatchFace() {

    }

    @Override
    public void setDeviceGoalStep(int targetStep) {

    }

    @Override
    public void setTimeFormat(int timeFormat) {
        Watch7018Manager.getWatch7018Manager().setTimeStyle(timeFormat == 0);
    }

    @Override
    public void sendMessage(String message, int messageType) {

    }

    @Override
    public void showSwitchCameraView() {

    }

    @Override
    public void sendOtherMessageSwitch(boolean isSwitch) {

    }

    @Override
    public void measureBloodPressure(boolean isState) {
        Watch7018Manager.getWatch7018Manager().measureHealthData(4,isState);
    }

    @Override
    public void measureOxygenBlood(boolean isState) {
        Watch7018Manager.getWatch7018Manager().measureHealthData(2,isState);
    }

    @Override
    public void measureOnceHrData(boolean isState) {
        Watch7018Manager.getWatch7018Manager().measureHealthData(1,isState);
    }

    @Override
    public void w81QeryAlarmList() {

    }

    @Override
    public void setTodayWeather(WristbandData weather, String city) {

    }

    @Override
    public void set15DayWeather(List<WristbandForecast> weather) {

    }

    @Override
    public void getVersion() {

    }

    //开始测量温度
    @Override
    public void startTemp(boolean isStart) {
        Watch7018Manager.getWatch7018Manager().measureHealthData(32,isStart);
    }

    @Override
    public void setTempSub(int value) {

    }

    @Override
    public void getTempSub() {

    }

    @Override
    public void getRealHrSwitch() {

    }

    @Override
    public void set_userinfo() {

    }

    @Override
    public void set_wear(boolean wristMode) {

    }

    @Override
    public void set_alar() {

    }

    @Override
    public void set_disPlay(DisplaySet displaySet) {

    }

    @Override
    public void get_display() {

    }

    @Override
    public void set_not_disturb(boolean open, int startHour, int startMin, int endHour, int endMin) {
        Watch7018Manager.getWatch7018Manager().setNotDisturbConfig(open,startHour,startMin,endHour,endHour);
    }

    @Override
    public void get_not_disturb() {
        Watch7018Manager.getWatch7018Manager().getNotDisturbConfig();
    }

    @Override
    public void get_sedentary_reminder() {
       // Watch7018Manager.getWatch7018Manager().getSedentaryConfig();
    }

    @Override
    public void set_sendentary_reminder(List<SedentaryRemind> list) {

    }

    @Override
    public void set_hr() {

    }

    @Override
    public void find_bracelet() {
        Watch7018Manager.getWatch7018Manager().findDevices();
    }

    @Override
    public void lost_to_remind(boolean isOpen) {

    }

    @Override
    public void set_lift_wrist_to_view_info() {

    }

    @Override
    public void play_bracelet() {

    }

    @Override
    public void set_is_open_raise_hand(boolean isOpen) {

    }

    @Override
    public void set_raise_hand(int type, int startHour, int startMin, int endHour, int endMin) {

    }

    @Override
    public void set_defult() {

    }

    @Override
    public void set_hr_setting(boolean isOpen) {

    }

    @Override
    public void set_hr_setting(boolean isOpen, int time) {

    }

    @Override
    public void sync_data() {

    }

    @Override
    public void w311_send_phone(String comming_phone, String name) {

    }

    @Override
    public void w311_send_msge(NotificationMsg msg) {

    }

    @Override
    public void setAlarmList(ArrayList<AlarmEntry> list) {

    }

    @Override
    public void getAlarmList() {

    }
}
