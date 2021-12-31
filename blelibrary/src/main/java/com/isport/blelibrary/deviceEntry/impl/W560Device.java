package com.isport.blelibrary.deviceEntry.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.isport.blelibrary.deviceEntry.interfaces.IBraceletW520;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.deviceEntry.interfaces.IWatch516;
import com.isport.blelibrary.deviceEntry.interfaces.IWatchW526;
import com.isport.blelibrary.deviceEntry.interfaces.IWatchW557;
import com.isport.blelibrary.entry.AutoSleep;
import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.managers.WatchW557BleManager;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author
 * @Date 2019/2/19
 * @Fuction
 */

public class W560Device extends BaseDevice implements IDeviceType, IWatch516, IWatchW526, IBraceletW520, IWatchW557 {

    /**
     * 名字 deviceName
     * mac地址 address
     * 信号值 rssi
     * 哪个类型的设备 deviceType
     */

    public W560Device(String name, String mac) {
        super();
        this.deviceName = name;
        this.address = mac;
        setType();
    }

    public W560Device(String mac) {
        super();
        this.address = mac;
        setType();
    }

    @Override
    public void disconnect(boolean reconnect) {
        WatchW557BleManager.getInstance().disconnect(reconnect);
    }


    public void unbind(String mac) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            BluetoothDevice bleDevice = bluetoothAdapter.getRemoteDevice(mac);
            if (bleDevice != null) {
                unpairDevice(bleDevice);
            }
        }
    }

    //反射来调用BluetoothDevice.removeBond取消设备的配对
    private void unpairDevice(BluetoothDevice device) {
        try {
            Logger.myLog("反射解绑");
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Logger.myLog(e.getMessage());
        }
    }

    @Override
    public void connect(boolean isConnectByUser) {
        WatchW557BleManager.getInstance().connectNRF(this, isConnectByUser);
    }

    @Override
    public BaseManager getManager(Context context) {
        return WatchW557BleManager.getInstance(context);
    }

    @Override
    public void getBattery() {
        WatchW557BleManager.getInstance().getBattery();
    }

    @Override
    public void exit() {
        WatchW557BleManager.getInstance().exit();
    }

    @Override
    public void close() {
        WatchW557BleManager.getInstance().close();
    }

    public void getDeviceVersion() {
        WatchW557BleManager.getInstance().getDeviceVersion();
    }

    @Override
    public void setType() {
        this.deviceType = TYPE_WATCH_W560;
    }

    @Override
    public void syncTodayData() {
        WatchW557BleManager.getInstance().get_daily_record(0, true);
    }

    @Override
    public void queryWatchFace() {
        WatchW557BleManager.getInstance().findWatchFace();
    }

    @Override
    public void read_status() {
        WatchW557BleManager.getInstance().read_status();
    }

    @Override
    public void set_general(boolean open24HeartRate, boolean isHeart) {

        if (isHeart) {
            WatchW557BleManager.getInstance().set_general(open24HeartRate, isHeart);
        } else {
            WatchW557BleManager.getInstance().set_general(open24HeartRate, isHeart);
        }
        // WatchW557BleManager.getInstance().set_general(open24HeartRate, isHeart);

    }

    @Override
    public void set_general(boolean open24HeartRate, boolean isCall, boolean isMessage) {
        WatchW557BleManager.getInstance().set_general(open24HeartRate, isCall, isMessage);
    }

    @Override
    public void get_general() {
        WatchW557BleManager.getInstance().get_general();
    }

    @Override
    public void set_user(int year, int month, int day, int sex, int weight, int height, int maxHeartRate, int
            minHeartRate) {
        WatchW557BleManager.getInstance().set_user(year, month, day, sex, weight, height, maxHeartRate,
                minHeartRate);
    }

    @Override
    public void get_user() {
        WatchW557BleManager.getInstance().get_user();
    }

    @Override
    public void set_calender() {
        WatchW557BleManager.getInstance().set_calender();
    }

    @Override
    public void get_calender() {
        WatchW557BleManager.getInstance().get_calender();
    }

    @Override
    public void set_alarm(boolean enable, int day, int hour, int min, int index) {
        WatchW557BleManager.getInstance().set_alarm(enable, day, hour, min, index);
    }

    public void set_w560_alarm(boolean enable, int day, int hour, int min, int index, String name) {
        WatchW557BleManager.getInstance().set_w560_alarm(enable, day, hour, min, index, name);
    }

    public void add_w560_alarm(int day, int hour, int min, String name) {
        WatchW557BleManager.getInstance().add_w560_alarm(day, hour, min, name);
    }

    public void delete_w560_alarm(int index) {
        WatchW557BleManager.getInstance().delete_w560_alarm(index);
    }

    @Override
    public void get_alarm(int index) {
        WatchW557BleManager.getInstance().getW560AlarmList();
    }

    @Override
    public void switch_mode(boolean inMode) {
        WatchW557BleManager.getInstance().switch_mode(inMode);
    }

    @Override
    public void adjust(int hour, int min) {
        WatchW557BleManager.getInstance().adjust(hour, min);
    }


    @Override
    public void set_sleep_setting(boolean isAutoSleep, boolean hasNotif, boolean disturb, int
            testStartTimeH, int testStartTimeM, int
                                          testEndTimeH, int testEndTimeM,
                                  int
                                          disturbStartTimeH, int disturbStartTimeM, int
                                          disturbEndTimeH, int disturbEndTimeM) {
        WatchW557BleManager.getInstance().set_sleep_setting(isAutoSleep, hasNotif, disturb,
                testStartTimeH, testStartTimeM,
                testEndTimeH, testEndTimeM,

                disturbStartTimeH, disturbStartTimeM,
                disturbEndTimeH, disturbEndTimeM);
    }

    @Override
    public void get_sleep_setting() {
        WatchW557BleManager.getInstance().get_sleep_setting();
    }


    @Override
    public void set_sedentary_time(int time, int startHour, int startMin, int endHour, int endMin) {
        WatchW557BleManager.getInstance().set_sedentary_time(time, startHour, startMin, endHour, endMin);
    }

    @Override
    public void get_sedentary_time() {
        WatchW557BleManager.getInstance().get_sedentary_time();
    }

    @Override
    public void send_notification(int type) {
        WatchW557BleManager.getInstance().send_notification(type);
    }

    @Override
    public void send_notificationN(String type) {
        WatchW557BleManager.getInstance().send_notification_N(type);
    }

    @Override
    public void set_handle(boolean enable) {
        WatchW557BleManager.getInstance().set_handle(enable);
    }

    @Override
    public void get_daily_record(int day) {
        WatchW557BleManager.getInstance().get_daily_record(day, true);
    }

    @Override
    public void clear_daily_record() {
        WatchW557BleManager.getInstance().clear_daily_record();
    }

    @Override
    public void getTestData() {
        WatchW557BleManager.getInstance().getTestData();
    }

    @Override
    public void get_exercise_data() {
        WatchW557BleManager.getInstance().get_exercise_data();
    }

    @Override
    public void clear_exercise_data() {
        WatchW557BleManager.getInstance().clear_exercise_data();
    }

    @Override
    public void set_default() {
        WatchW557BleManager.getInstance().set_default();
    }

    @Override
    public void set_sn_factory() {
        WatchW557BleManager.getInstance().set_sn_factory();
    }

    @Override
    public void set_sn_normalmode(int SN) {
        WatchW557BleManager.getInstance().set_sn_normalmode(SN);
    }

    @Override
    public void get_sn_data() {
        WatchW557BleManager.getInstance().get_sn_data();
    }

    @Override
    public void set_beltname() {
        WatchW557BleManager.getInstance().set_beltname();
    }

    @Override
    public void test_reset() {
        WatchW557BleManager.getInstance().test_reset();
    }

    @Override
    public void test_motorled() {
        WatchW557BleManager.getInstance().test_motorled();
    }

    @Override
    public void stop_test_motorled() {
        WatchW557BleManager.getInstance().stop_test_motorled();
    }

    @Override
    public void test_handle() {
        WatchW557BleManager.getInstance().test_handle();
    }

    @Override
    public void test_display() {
        WatchW557BleManager.getInstance().test_display();
    }

    @Override
    public void test_ohr() {
        WatchW557BleManager.getInstance().test_ohr();
    }

    @Override
    public void device_to_phone() {
        WatchW557BleManager.getInstance().device_to_phone();
    }

    @Override
    public void findWatch() {
        WatchW557BleManager.getInstance().findW526();
    }

    @Override
    public void setWatchbacklight(int leve, int time) {
        WatchW557BleManager.getInstance().W526setbacklight(leve, time);
    }

    @Override
    public void senddisturb(boolean enable, int starHour, int starMin, int endHour, int endMin) {
        WatchW557BleManager.getInstance().sendW526Disturb(enable, starHour, starMin, endHour, endMin);
    }

    @Override
    public void set_w526_raise_hand(int type, int startHour, int startMin, int endHour, int endMin) {
        WatchW557BleManager.getInstance().setW526LiftWrist(type, endHour, endMin, startHour, startMin);
    }

    @Override
    public void sendW526Messge(String title, String message, int type) {
        WatchW557BleManager.getInstance().sendW526Message(type, title, message);

    }

    @Override
    public void sendRealHrSwitch(boolean isOpen) {
        WatchW557BleManager.getInstance().open24HrSwitch(isOpen);
    }

    @Override
    public void setTargetStep(int step) {
        WatchW557BleManager.getInstance().sendW560TargetStep(step);
    }

    public void setTargetDistance(int distance) {
        WatchW557BleManager.getInstance().sendW560TargetDistance(distance);
    }

    public void setTargetCalorie(int calorie) {
        WatchW557BleManager.getInstance().sendW560TargetCalorie(calorie);
    }

    @Override
    public void meassureOxy(boolean isStart) {
        WatchW557BleManager.getInstance().measureOxy(isStart);

    }

    @Override
    public void meassureBlood(boolean isStart) {
        WatchW557BleManager.getInstance().measureBoold(isStart);
    }


    @Override
    public void w520SetDial(int enbale) {
        WatchW557BleManager.getInstance().w526SetWatchFace(enbale);
    }

    @Override
    public void setWeather(WristbandData wristbandData, List<WristbandForecast> list) {
        List<WristbandForecast> w560BWeather = new ArrayList<>();
        for(WristbandForecast wristbandForecast : list){

            String weatherId = wristbandForecast.getWeatherId();
            int weatherTag =  Constants.W81WeatherConfig.get(weatherId);
            //560的云
            if(weatherTag == 0 || weatherTag == 1 || weatherTag == 2 || weatherTag == 7){
                wristbandForecast.setWeatherId("FOGGY"); //1
            }

            if(weatherTag == 5){  //晴 0
                wristbandForecast.setWeatherId("CLOUDY");
            }

            if(weatherTag == 3 || weatherTag == 4){  //雨 3
                wristbandForecast.setWeatherId("RAINY");
            }

            if(weatherTag == 2){ //风 2
                wristbandForecast.setWeatherId("OVERCAST");
            }

            w560BWeather.add(wristbandForecast);
        }
        WatchW557BleManager.getInstance().setCmdW526Weather(wristbandData, w560BWeather);

    }

    @Override
    public void setSleepData(AutoSleep sleepData) {

    }

    @Override
    public void getSleepData() {

    }

    @Override
    public void setRaise307J(int state) {

    }

    @Override
    public void startTemp(boolean isStart) {
        WatchW557BleManager.getInstance().startMeasureTemp(isStart);
    }

    @Override
    public void setTempSub(int value) {

    }

    @Override
    public void getTempSub() {

    }

    @Override
    public void getRealHrSwitch() {
        Log.e("currentDevice", "W560Device");
        WatchW557BleManager.getInstance().get24HrSwitch();
    }


    @Override
    public void sendphoto() {
        WatchW557BleManager.getInstance().sendphoto();
    }

    @Override
    public void meassureOneHr(boolean isStart) {
        WatchW557BleManager.getInstance().startMessureOnceHr(isStart);
    }


    //读取目标步数
    public void readW560DeviceStepGoal(){
        WatchW557BleManager.getInstance().readDeviceGoal();
    }

    //设置音乐
    public void setMusic(String musicName,String allTime,String currTime){
        WatchW557BleManager.getInstance().sendMusicStatus(musicName,allTime,currTime);
    }


    public void getExerciseData(int num){
        WatchW557BleManager.getInstance().getExceData(num);
    }
}
