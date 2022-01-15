package com.isport.blelibrary.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.BatteryStatus;
import com.htsmart.wristband2.bean.ConnectionState;
import com.htsmart.wristband2.bean.SyncDataRaw;
import com.htsmart.wristband2.bean.WristbandAlarm;
import com.htsmart.wristband2.bean.WristbandContacts;
import com.htsmart.wristband2.bean.WristbandNotification;
import com.htsmart.wristband2.bean.config.DrinkWaterConfig;
import com.htsmart.wristband2.bean.config.FunctionConfig;
import com.htsmart.wristband2.bean.config.HealthyConfig;
import com.htsmart.wristband2.bean.config.NotDisturbConfig;
import com.htsmart.wristband2.bean.config.NotificationConfig;
import com.htsmart.wristband2.bean.config.SedentaryConfig;
import com.htsmart.wristband2.bean.config.TurnWristLightingConfig;
import com.htsmart.wristband2.bean.data.BloodPressureData;
import com.htsmart.wristband2.bean.data.HeartRateData;
import com.htsmart.wristband2.bean.data.OxygenData;
import com.htsmart.wristband2.bean.data.SleepData;
import com.htsmart.wristband2.bean.data.TemperatureData;
import com.htsmart.wristband2.bean.data.TodayTotalData;
import com.htsmart.wristband2.packet.SyncDataParser;
import com.isport.blelibrary.db.parse.ParseData;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * F18手表，7018型号
 * Created by Admin
 * Date 2022/1/14
 */
public class Watch7018Manager extends BaseManager{

    private static final String TAG = "Watch7018Manager";

    private  static Watch7018Manager watch7018Manager;
    private static Context mContext;

    //数据操作类
    private final WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();

    private Disposable mStateDisposable;
    private Disposable mErrorDisposable;
    private ConnectionState mState = ConnectionState.DISCONNECTED;


    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int whatCode = msg.what;
            boolean isListenerNull = mBleReciveListeners == null;
            if(isListenerNull)
                return;
            if(whatCode == HandlerContans.mHandlerConnetSuccessState){  //连接成功
                if (mCurrentDevice != null) {
                    for (int i = 0; i < mBleReciveListeners.size(); i++) {
                        mBleReciveListeners.get(i).onConnResult(true, true, mCurrentDevice);
                    }
                }
            }

            //电量
            if(whatCode == HandlerContans.mHandlerbattery){
                if (mCurrentDevice != null) {
                    for (int i = 0; i < mBleReciveListeners.size(); i++) {
                        mBleReciveListeners.get(i).onBattreyOrVersion(mCurrentDevice);
                    }
                }
            }
        }
    };


    public static Watch7018Manager getWatch7018Manager(Context context){
        mContext = context;
        synchronized (Watch7018Manager.class){
            if(watch7018Manager == null){
                watch7018Manager = new Watch7018Manager();
                initHandler();
            }
        }
        return watch7018Manager;
    }

    public static Watch7018Manager getWatch7018Manager(){
        synchronized (Watch7018Manager.class){
            if(watch7018Manager == null){
                watch7018Manager = new Watch7018Manager();
                initHandler();
            }
        }
        return watch7018Manager;
    }

    private static void initHandler() {

    }

    private Watch7018Manager(){

    }


    /**
     * 连接设备
     * @param bleMac Mac地址
     * @param isBind 是否是绑定或登录，用户第一次连接时需要绑定，后面只需要登录，如果再次被其它用户绑定，则会清除手表数据
     *
     */
    public void connectDevice(String bleMac,boolean isBind){
        setConnectListener();
        mWristbandManager.connect(bleMac,"1",isBind, true,25,175,60);
    }

    //断开连接
    public void disConnectDevice(){
        mWristbandManager.close();
    }

    //解绑，解绑后需要使用bind模式
    public void unBindDevice(){
        mWristbandManager.userUnBind().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    //设置连接监听
    @SuppressLint("CheckResult")
    private void setConnectListener(){
        mWristbandManager.observerConnectionState().subscribe(new Consumer<ConnectionState>() {

            @Override
            public void accept(ConnectionState connectionState) throws Exception {
                Log.e(TAG,"------连接状态="+connectionState.toString());
                if(connectionState == ConnectionState.CONNECTED){   //连接成功
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerConnetSuccessState,1000);
                }

                if(connectionState == ConnectionState.CONNECTING){  //正在连接中

                }

                if(connectionState == ConnectionState.DISCONNECTED){    //断开连接

                }
            }
        });

    }


    @SuppressLint("CheckResult")
    public void setDeviceListener(){
        mWristbandManager.observerWristbandMessage().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if(integer == WristbandManager.MSG_FIND_PHONE){     //查找手机

                }

                //调起相机拍照
                if(integer == WristbandManager.MSG_CAMERA_TAKE_PHOTO){

                }

                //控制音乐播放或暂停
                if(integer == WristbandManager.MSG_MEDIA_PLAY_PAUSE){

                }

                //音乐下一首
                if(integer == WristbandManager.MSG_MEDIA_NEXT){

                }

                //音乐上一首
                if(integer == WristbandManager.MSG_MEDIA_PREVIOUS){

                }

                //控制app音乐加大
                if(integer == WristbandManager.MSG_MEDIA_VOLUME_UP){

                }

                //控制app音量减小
                if(integer == WristbandManager.MSG_MEDIA_VOLUME_DOWN){

                }


            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    //同步设备数据
    @SuppressLint("CheckResult")
    public void syncDeviceData(){
        mWristbandManager.syncData().observeOn(Schedulers.io(),true)
                .flatMapCompletable(new Function<SyncDataRaw, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull SyncDataRaw syncDataRaw) throws Exception {

                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_HEART_RATE) {  //心率数据
                            List<HeartRateData> datas = SyncDataParser.parserHeartRateData(syncDataRaw.getDatas());
                            analysisHeartData(datas);
                        }

                        //血压数据
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_BLOOD_PRESSURE) {
                            List<BloodPressureData> bloodData = SyncDataParser.parserBloodPressureData(syncDataRaw.getDatas());
                            analysisBloodData(bloodData);
                        }

                        //血氧数据
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_OXYGEN) {
                            List<OxygenData> datas = SyncDataParser.parserOxygenData(syncDataRaw.getDatas());

                        }
                        //睡眠数据
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SLEEP) {
                            List<SleepData> sleepDataList = SyncDataParser.parserSleepData(syncDataRaw.getDatas(),syncDataRaw.getConfig());
                            analysisSleepData(sleepDataList);
                        }

                        //汇总运动数据
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_TOTAL_DATA) {
                            TodayTotalData data = SyncDataParser.parserTotalData(syncDataRaw.getDatas());

                        }
                        //温度
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_TEMPERATURE) {
                            List<TemperatureData> temperatureDataList = SyncDataParser.parserTemperatureData(syncDataRaw.getDatas());
                            if (temperatureDataList != null && temperatureDataList.size() > 0) {
                                //TODO save data
                            }
                        }

                        return Completable.complete();
                    }
                }).subscribe(new Action() {

            @Override
            public void run() throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    //解析心率数据
    private void analysisHeartData(List<HeartRateData> datas){

    }

    //解析血压数据
    private void analysisBloodData(List<BloodPressureData> bpList){

    }

    //解析睡眠数据
    private void analysisSleepData(List<SleepData> sleepDataList){

    }


    //读取手表上的联系人
    @SuppressLint("CheckResult")
    public void readDeviceContact(){
        mWristbandManager.requestContactsList().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<WristbandContacts>>() {
                    @Override
                    public void accept(List<WristbandContacts> wristbandContacts) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    //设置联系人
    public void setDeviceContact(){

    }



    //电量
    @SuppressLint("CheckResult")
    public void getDeviceBattery(){
        mWristbandManager.requestBattery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BatteryStatus>() {
                    @Override
                    public void accept(BatteryStatus batteryStatus) throws Exception {
                        mDeviceInformationTable.setBattery(batteryStatus.getPercentage());
                        ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, 0);
                        mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerbattery, times);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    //时间格式
    public void setTimeStyle(boolean is24Hour){
        FunctionConfig functionConfig = mWristbandManager.getWristbandConfig().getFunctionConfig();
        functionConfig.setFlagEnable(FunctionConfig.FLAG_HOUR_STYLE,is24Hour);
    }

    //设置用户信息
    public void setUserInfo(boolean sex,int age,float height,float weight){
        mWristbandManager.setUserInfo(sex,age,height,weight);
    }

    //公英制
    public void setKmUnit(boolean isKm){
        FunctionConfig functionConfig = mWristbandManager.getWristbandConfig().getFunctionConfig();
        functionConfig.setFlagEnable(FunctionConfig.FLAG_LENGTH_UNIT,isKm);
    }

    //温度单位 是否是华摄氏度，true为华摄氏度，false为摄氏度
    public void setTemperUnit(boolean isTemp){
        FunctionConfig functionConfig = mWristbandManager.getWristbandConfig().getFunctionConfig();
        functionConfig.setFlagEnable(FunctionConfig.FLAG_TEMPERATURE_UNIT,isTemp);
    }





    /**
     * 设置语言
     * 语言支持https://github.com/htangsmart/FitCloudPro-SDK-Android/blob/master/LanguageType.png
     * 0x00-默认；0x01-中文简体；0x02-中文繁体；0x03-英文 0x09日语
     */
    public void setDeviceLanguage(byte language){
        mWristbandManager.setLanguage(language);
    }

    //查找手环
    public void findDevices(){
        mWristbandManager.findWristband();
    }

    //设置运动目标
    public void setDeviceSportGoal(int step,int distance,int kcal){
        mWristbandManager.setExerciseTarget(step,distance,kcal);
    }




    //获取喝水提醒
    public void getDrinkData(){
        DrinkWaterConfig drinkWaterConfig =  mWristbandManager.getWristbandConfig().getDrinkWaterConfig();
        //开关
        boolean isOpen = drinkWaterConfig.isEnable();
        //开始时间
        int startTime = drinkWaterConfig.getStart();
        //结束时间
        int endTime = drinkWaterConfig.getEnd();

        //开始小时
        int startHour = startTime / 60;
        //开始分钟
        int startMinute = startTime % 60;

        //结束小时
        int endHour = endTime / 60;
        int endMinute = endTime % 60;
        //间隔
        int intervalTime = drinkWaterConfig.getInterval();


    }


    //设置喝水提醒
    public void setDrinkData(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute,int intervalTime){
        DrinkWaterConfig drinkWaterConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getDrinkWaterConfig();
        drinkWaterConfig.setEnable(isOpen);
        drinkWaterConfig.setStart(startHour*60+startMinute);
        drinkWaterConfig.setEnd(endHour*60+endMinute);
        drinkWaterConfig.setInterval(intervalTime);
    }

    //获取久坐提醒
    public void getSedentaryConfig(){
        SedentaryConfig sedentaryConfig = mWristbandManager.getWristbandConfig().getSedentaryConfig();
        //开关
        boolean isOpen = sedentaryConfig.isEnable();
        //开始时间
        int startTime = sedentaryConfig.getStart();
        //结束时间
        int endTime = sedentaryConfig.getEnd();

        //开始小时
        int startHour = startTime / 60;
        //开始分钟
        int startMinute = startTime % 60;

        //结束小时
        int endHour = endTime / 60;
        int endMinute = endTime % 60;

        //间隔
        int intervalTime = sedentaryConfig.getInterval();

    }

    //设置久坐提醒
    public void setSedentaryConfig(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute,int intervalTime){
        SedentaryConfig sedentaryConfig = mWristbandManager.getWristbandConfig().getSedentaryConfig();
        sedentaryConfig.setEnable(isOpen);
        sedentaryConfig.setStart(startHour * 60 + startMinute);
        sedentaryConfig.setEnd(endHour * 60 + endMinute);
        sedentaryConfig.setInterval(intervalTime);

    }

    //获取转腕亮屏
    public void getTurnWristLightingConfig(){
        TurnWristLightingConfig turnWristLightingConfig = mWristbandManager.getWristbandConfig().getTurnWristLightingConfig();
        //开关
        boolean isOpen = turnWristLightingConfig.isEnable();
        //开始时间
        int startTime = turnWristLightingConfig.getStart();
        //结束时间
        int endTime = turnWristLightingConfig.getEnd();

        //开始小时
        int startHour = startTime / 60;
        //开始分钟
        int startMinute = startTime % 60;

        //结束小时
        int endHour = endTime / 60;
        int endMinute = endTime % 60;

    }

    //设置转腕亮屏
    public void setTurnWristLightingConfig(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute){
        TurnWristLightingConfig turnWristLightingConfig = mWristbandManager.getWristbandConfig().getTurnWristLightingConfig();
         turnWristLightingConfig.setEnable(isOpen);
        //开始时间
        turnWristLightingConfig.setStart(startHour * 60 + startMinute);
        //结束时间
        turnWristLightingConfig.setEnd(endHour * 60 + endMinute);

    }

    //获取连续监测
    public void getHealthyConfig(){
        HealthyConfig healthyConfig = mWristbandManager.getWristbandConfig().getHealthyConfig();
        //开关
        boolean isOpen = healthyConfig.isEnable();
        //开始时间
        int startTime = healthyConfig.getStart();
        //结束时间
        int endTime = healthyConfig.getEnd();

        //开始小时
        int startHour = startTime / 60;
        //开始分钟
        int startMinute = startTime % 60;

        //结束小时
        int endHour = endTime / 60;
        int endMinute = endTime % 60;

    }


    //设置连续监测
    public void setHealthyConfig(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute){
        HealthyConfig healthyConfig = mWristbandManager.getWristbandConfig().getHealthyConfig();
        //开关
        healthyConfig.setEnable(isOpen);
        //开始时间
        healthyConfig.setStart(startHour * 60 + startMinute);
        //结束时间
        healthyConfig.setEnd(endHour * 60 + endMinute);

    }

    //获取勿扰设置
    public void getNotDisturbConfig(){
        NotDisturbConfig notDisturbConfig = mWristbandManager.getWristbandConfig().getNotDisturbConfig();
        boolean isOpen = notDisturbConfig.isEnablePeriodTime();
        //开始时间
        int startTime = notDisturbConfig.getStart();
        //结束时间
        int endTime = notDisturbConfig.getEnd();

        //开始小时
        int startHour = startTime / 60;
        //开始分钟
        int startMinute = startTime % 60;

        //结束小时
        int endHour = endTime / 60;
        int endMinute = endTime % 60;


    }


    //设置勿扰设置
    public void setNotDisturbConfig(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute){
        NotDisturbConfig notDisturbConfig = mWristbandManager.getWristbandConfig().getNotDisturbConfig();
        notDisturbConfig.setEnablePeriodTime(isOpen);

        //开始时间
        notDisturbConfig.setStart(startHour * 60 + startMinute);
        //结束时间
        notDisturbConfig.setEnd(endHour * 60 + endMinute);


    }

    /**
     * 天气设置
     *  状态序号
     *  0x00 未知
     * 0x01 晴天
     * 0x02 多云
     * 0x03 阴天
     * 0x04 阵雨
     * 0x05 雷阵雨、雷阵雨伴有冰雹
     * 0x06 小雨
     * 0x07 中雨、大雨、暴雨
     * 0x08 雨加雪、冻雨
     * 0x09 小雪
     * 0x0a 大雪、暴雪
     * 0x0b 沙尘暴、浮尘
     * 0x0c 雾、雾霾
     *
     *
     */
    public void setWeatherData(String cityName){
       // mWristbandManager.setWeather()
    }


    //设置消息提醒开关
    public void setAppsNotices(){
        NotificationConfig notificationConfig = mWristbandManager.getWristbandConfig().getNotificationConfig();

    }

    //消息推送
    public void sendNoticeToDevice(int type,String name,String content){
        WristbandNotification wristbandNotification = new WristbandNotification();
        wristbandNotification.setName(name);
        wristbandNotification.setType((byte) type);
        wristbandNotification.setContent(content);
        mWristbandManager.sendWristbandNotification(wristbandNotification);
    }

    //获取闹钟
    @SuppressLint("CheckResult")
    public void getDeviceAlarmList(){
        mWristbandManager.requestAlarmList()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<WristbandAlarm>>() {
                    @Override
                    public void accept(List<WristbandAlarm> wristbandAlarms) throws Exception {


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    //设置闹钟
    public void setDeviceAlarm(){
        //mWristbandManager.setAlarmList();
    }
}
