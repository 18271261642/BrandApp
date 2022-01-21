package com.isport.blelibrary.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.BatteryStatus;
import com.htsmart.wristband2.bean.ConnectionState;
import com.htsmart.wristband2.bean.HealthyDataResult;
import com.htsmart.wristband2.bean.SyncDataRaw;
import com.htsmart.wristband2.bean.WristbandAlarm;
import com.htsmart.wristband2.bean.WristbandContacts;
import com.htsmart.wristband2.bean.WristbandNotification;
import com.htsmart.wristband2.bean.WristbandVersion;
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
import com.htsmart.wristband2.bean.data.SleepItemData;
import com.htsmart.wristband2.bean.data.SportData;
import com.htsmart.wristband2.bean.data.StepData;
import com.htsmart.wristband2.bean.data.TemperatureData;
import com.htsmart.wristband2.bean.data.TodayTotalData;
import com.htsmart.wristband2.packet.SyncDataParser;
import com.isport.blelibrary.db.CommonInterFace.DeviceMessureData;
import com.isport.blelibrary.db.action.W81Device.W81DeviceDataAction;
import com.isport.blelibrary.db.action.f18.F18DeviceSetAction;
import com.isport.blelibrary.db.parse.DeviceDataSave;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.db.table.f18.listener.CommAlertListener;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.entry.F18AlarmAllListener;
import com.isport.blelibrary.entry.F18AppsStatusListener;
import com.isport.blelibrary.entry.F18CommStatusListener;
import com.isport.blelibrary.entry.F18ContactListener;
import com.isport.blelibrary.result.impl.watch.DeviceMessureDataResult;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.TimeUtils;
import java.util.ArrayList;
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
public class Watch7018Manager extends BaseManager {

    private static final String TAG = "Watch7018Manager";

    private static Watch7018Manager watch7018Manager;
    private static Context mContext;

    //数据操作类
    private final WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();

    private Disposable mStateDisposable;
    private Disposable mErrorDisposable;
    private ConnectionState mState = ConnectionState.DISCONNECTED;


    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int whatCode = msg.what;
            boolean isListenerNull = mBleReciveListeners == null;
            if (isListenerNull)
                return;
            if (whatCode == HandlerContans.mHandlerConnetSuccessState) {  //连接成功
                if (mCurrentDevice != null) {
                    for (int i = 0; i < mBleReciveListeners.size(); i++) {
                        mBleReciveListeners.get(i).onConnResult(true, true, mCurrentDevice);
                    }
                }
            }
            if(whatCode == HandlerContans.mDevcieMeasureHrSuccess){ //手动心率测量
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_once_hr, mCurrentDevice.getDeviceName()));
                }
            }


            //血压测量成功
            if(whatCode ==HandlerContans.mDevcieBloodPressureMessureSuccess){
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_bloodpre, mCurrentDevice.getDeviceName()));
                }
            }

            //血氧测量成功
            if(whatCode == HandlerContans.mDevcieMeasureOxyenSuccess){
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_oxygen, mCurrentDevice.getDeviceName()));
                }
            }

            //电量
            if (whatCode == HandlerContans.mHandlerbattery) {
                if (mCurrentDevice != null) {
                    for (int i = 0; i < mBleReciveListeners.size(); i++) {

                        mBleReciveListeners.get(i).onBattreyOrVersion(mCurrentDevice);
                    }
                }
            }
        }
    };


    public static Watch7018Manager getWatch7018Manager(Context context) {
        mContext = context;
        synchronized (Watch7018Manager.class) {
            if (watch7018Manager == null) {
                watch7018Manager = new Watch7018Manager();
                initHandler();
            }
        }
        return watch7018Manager;
    }

    public static Watch7018Manager getWatch7018Manager() {
        synchronized (Watch7018Manager.class) {
            if (watch7018Manager == null) {
                watch7018Manager = new Watch7018Manager();
                initHandler();
            }
        }
        return watch7018Manager;
    }

    private static void initHandler() {

    }

    private Watch7018Manager() {

    }


    public WristbandManager getmWristbandManager(){
        return mWristbandManager;
    }

    /**
     * 连接设备
     *
     * @param bleMac Mac地址
     * @param isBind 是否是绑定或登录，用户第一次连接时需要绑定，后面只需要登录，如果再次被其它用户绑定，则会清除手表数据
     */
    public void connectDevice(String bleMac, boolean isBind) {
        setConnectListener();
        boolean isBindDevice = mWristbandManager.isBindOrLogin();
        Log.e(TAG,"----isBindDevice="+isBindDevice);
        mWristbandManager.connect(bleMac, "1", isBindDevice, true, 25, 175, 60);
    }

    public void connectDevice(BaseDevice baseDevice,boolean isBind){
        setConnectListener();
    }



    //断开连接
    public void disConnectDevice() {
        mWristbandManager.close();
    }

    //解绑，解绑后需要使用bind模式
    @SuppressLint("CheckResult")
    public void unBindDevice() {
        mWristbandManager.userUnBind().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
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
    private void setConnectListener() {
        mWristbandManager.observerConnectionState().subscribe(new Consumer<ConnectionState>() {

            @Override
            public void accept(ConnectionState connectionState) throws Exception {
                Log.e(TAG, "------连接状态=" + connectionState.toString());
                if (connectionState == ConnectionState.CONNECTED) {   //连接成功
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerConnetSuccessState, 1000);
                    setDeviceListener();
                }

                if (connectionState == ConnectionState.CONNECTING) {  //正在连接中

                }

                if (connectionState == ConnectionState.DISCONNECTED) {    //断开连接

                }
            }
        });

    }


    @SuppressLint("CheckResult")
    public void setDeviceListener() {
        mWristbandManager.observerWristbandMessage().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "-----设置监听=" + integer);
                if (integer == WristbandManager.MSG_FIND_PHONE) {     //查找手机

                }

                //调起相机拍照
                if (integer == WristbandManager.MSG_CAMERA_TAKE_PHOTO) {

                }

                //控制音乐播放或暂停
                if (integer == WristbandManager.MSG_MEDIA_PLAY_PAUSE) {

                }

                //音乐下一首
                if (integer == WristbandManager.MSG_MEDIA_NEXT) {

                }

                //音乐上一首
                if (integer == WristbandManager.MSG_MEDIA_PREVIOUS) {

                }

                //控制app音乐加大
                if (integer == WristbandManager.MSG_MEDIA_VOLUME_UP) {

                }

                //控制app音量减小
                if (integer == WristbandManager.MSG_MEDIA_VOLUME_DOWN) {

                }


            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    //获取已经连接的设备Mac地址
    public String getConnectedMac() {
        if (!mWristbandManager.isConnected())
            return null;
        return mWristbandManager.getConnectedAddress();
    }


    //获取最近一次健康测量记录
    @SuppressLint("CheckResult")
    public void getDeviceLastHealthMeasure(){
        if(!mWristbandManager.isConnected())
            return;
        mWristbandManager.requestLatestHealthy().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HealthyDataResult>() {
                    @Override
                    public void accept(HealthyDataResult healthyDataResult) throws Exception {
                        Log.e(TAG,"------最近一次测量记录="+healthyDataResult.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    private Disposable measureDisposable;

    /**
     *
     * 手动测量健康数据
     * WristbandVersion#isHeartRateEnabled() --> WristbandManager#HEALTHY_TYPE_HEART_RATE 心率
     *
     * WristbandVersion#isOxygenEnabled() --> WristbandManager#HEALTHY_TYPE_OXYGEN  血氧
     *
     * WristbandVersion#isBloodPressureEnabled() --> WristbandManager#HEALTHY_TYPE_BLOOD_PRESSURE  血压
     *
     * WristbandVersion#isRespiratoryRateEnabled() --> WristbandManager#HEALTHY_TYPE_RESPIRATORY_RATE 呼吸率
     *
     * WristbandVersion#isTemperatureEnabled() --> WristbandManager#HEALTHY_TYPE_TEMPERATURE  温度
     * @param healthType
     */
    @SuppressLint("CheckResult")
    public void measureHealthData(int healthType,boolean isStart){
        Log.e(TAG,"--hearlty="+healthType);
        if(!mWristbandManager.isConnected())
            return;

        if(measureDisposable != null && !measureDisposable.isDisposed()){
            //结束测量
            measureDisposable.dispose();
        }

        if(!isStart)
            return;

        measureDisposable = mWristbandManager.openHealthyRealTimeData(healthType,1)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable disposable) throws Exception {
                    Log.e(TAG,"------测量停止--");
                }
            }).doOnTerminate(new Action() {
            @Override
            public void run() throws Exception {

            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {

            }
        }).subscribe(new Consumer<HealthyDataResult>() {
            @Override
            public void accept(HealthyDataResult healthyDataResult) throws Exception {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("手动测量心率:"+healthyDataResult.getHeartRate()+"\n");
                stringBuilder.append("手动测量血压="+healthyDataResult.getDiastolicPressure()+"/"+healthyDataResult.getSystolicPressure()+"\n");
                stringBuilder.append("手动测量血氧="+healthyDataResult.getOxygen()+"\n");
                stringBuilder.append("呼吸率="+healthyDataResult.getRespiratoryRate()+"\n");
                stringBuilder.append("温度1="+healthyDataResult.getTemperatureBody()+"\n");
                stringBuilder.append("温度2="+healthyDataResult.getTemperatureWrist());
                Log.e(TAG,"-----手动测量数据="+stringBuilder.toString());
                if(healthType == WristbandManager.HEALTHY_TYPE_HEART_RATE){ //心率
                    int htV = healthyDataResult.getHeartRate();
                    if(htV == 0)
                        return;
                    DeviceDataSave.saveOneceHrData(mWristbandManager.getConnectedDevice().getName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getHeartRate(), System.currentTimeMillis(), String.valueOf(0));
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureHrSuccess, 500);
                }

                if(healthType == WristbandManager.HEALTHY_TYPE_BLOOD_PRESSURE){
                    int hBp = healthyDataResult.getDiastolicPressure();
                    int lBp = healthyDataResult.getSystolicPressure();
                    if(hBp == 0 && lBp == 0)
                        return;
                    DeviceDataSave.saveBloodPressureData(mWristbandManager.getConnectedDevice().getName(), String.valueOf(BaseManager.mUserId), hBp, lBp, System.currentTimeMillis(), String.valueOf(0));
                }

                if(healthType == WristbandManager.HEALTHY_TYPE_OXYGEN){
                    if(healthyDataResult.getOxygen() == 0)
                        return;
                    DeviceDataSave.saveOxyenModelData(mWristbandManager.getConnectedDevice().getName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getOxygen(), System.currentTimeMillis(), String.valueOf(0));
                }

                if(healthType == WristbandManager.HEALTHY_TYPE_TEMPERATURE){
                    if(healthyDataResult.getTemperatureWrist()==0)
                        return;
                    DeviceDataSave.saveTempData(mWristbandManager.getConnectedDevice().getName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getTemperatureWrist(), System.currentTimeMillis(), String.valueOf(0));
                }

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    //结束手动测量
    public void closeMeasure(){
        if(!mWristbandManager.isConnected())
            return;
        if(measureDisposable != null && !measureDisposable.isDisposed()){
            //结束测量
            measureDisposable.dispose();
        }
    }



    //同步设备数据
    @SuppressLint("CheckResult")
    public void syncDeviceData() {
        if(!mWristbandManager.isConnected())
            return;
        setSyncStatusListener();
        mWristbandManager.syncData().observeOn(Schedulers.io(), true)
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
                            Log.e(TAG,"-----血氧数据="+new Gson().toJson(datas));
                        }
                        //睡眠数据
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SLEEP) {
                            List<SleepData> sleepDataList = SyncDataParser.parserSleepData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            analysisSleepData(sleepDataList);
                        }

                        //汇总运动数据
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_TOTAL_DATA) {
                            TodayTotalData todayTotalData = SyncDataParser.parserTotalData(syncDataRaw.getDatas());
                            Log.e(TAG,"-----汇总运动数据="+todayTotalData.toString());
                            analysisCurrDayStep(todayTotalData);
                        }

                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_STEP){  //详细步数
                            List<StepData> datas = SyncDataParser.parserStepData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            Log.e(TAG,"------详细计步数据="+new Gson().toJson(datas));
                        }

                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SPORT){    //运动数据 运动模式产生的数据
                            List<SportData> datas = SyncDataParser.parserSportData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            Log.e(TAG,"------Sport数据="+new Gson().toJson(datas));
                        }

                        //温度
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_TEMPERATURE) {
                            List<TemperatureData> temperatureDataList = SyncDataParser.parserTemperatureData(syncDataRaw.getDatas());
                            if (temperatureDataList != null && temperatureDataList.size() > 0) {
                                Log.e(TAG,"---温度数据="+new Gson().toJson(temperatureDataList));
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

    //监听同步状态信息
    @SuppressLint("CheckResult")
    private void setSyncStatusListener() {
        mWristbandManager.observerSyncDataState()
                .subscribe(new Consumer<Integer>() {

                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == null)
                            return;
                        if (integer < 0) {      //同步失败
                            if (integer == WristbandManager.SYNC_STATE_FAILED_DISCONNECTED) {

                            }
                        }
                    }
                });

    }

    //当天汇总计步
    private void analysisCurrDayStep(TodayTotalData todayTotalData){
        //保存到数据库
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
                int kcal = todayTotalData.getCalorie() ==0 ? 0 : todayTotalData.getCalorie()/10000;
                w81DeviceDataAction.saveW81DeviceStepData(mWristbandManager.getConnectedDevice().getName(), String.valueOf(BaseManager.mUserId),
                        "0", TimeUtils.getTimeByyyyyMMdd(currentTime), currentTime, todayTotalData.getStep(), todayTotalData.getDistance(), kcal, false);
            }
        });
    }

    //解析心率数据
    private void analysisHeartData(List<HeartRateData> datas) {
        Log.e(TAG,"--------心率数据="+new Gson().toJson(datas));
    }

    //解析血压数据
    private void analysisBloodData(List<BloodPressureData> bpList) {
        Log.e(TAG,"--------血压数据="+new Gson().toJson(bpList));
    }

    //解析睡眠数据
    private void analysisSleepData(List<SleepData> sleepDataList) {
        Log.e(TAG,"--------睡眠数据="+new Gson().toJson(sleepDataList));

        List<SleepItemData> f18SleepList = sleepDataList.get(sleepDataList.size()-1).getItems();

        final ArrayList<ArrayList<String>> sleepDetail = new ArrayList<>();

        int len = sleepDataList.size();

        //深睡
        int deepSleepTime = 0;
        //浅睡
        int lightSleepTime = 0;
        //清醒
        int soberTime = 0;

        for (int i = 0; i < len; i++) {
            ArrayList<String> itemSleeep = new ArrayList<>();
            SleepItemData sleepData = f18SleepList.get(i);
            int sleepStatus = sleepData.getStatus();
            long startTime = sleepData.getStartTime();
            long endTime = sleepData.getEndTime();
            //间隔
            long intervalTime = endTime-startTime;
            //分钟
            int intervalMinute = (int) (intervalTime / 1000 /60);

            if(sleepStatus == 1){   //深睡
                deepSleepTime +=intervalMinute;
            }

            if(sleepStatus == 2){  //浅睡
                lightSleepTime +=intervalMinute;
            }

            if(sleepStatus == 3){   //清醒
                soberTime += intervalMinute;
            }


            itemSleeep.add(sleepData.getStatus() + "");
            itemSleeep.add(CommonDateUtil.getLongMinute(sleepData.getStartTime()) + "");
            itemSleeep.add(CommonDateUtil.getLongMinute(sleepData.getEndTime()) + "");
            itemSleeep.add(CommonDateUtil.getLongMinute(sleepData.getEndTime()-sleepData.getStartTime()) + "");

            Log.e(TAG,"-----睡眠item="+new Gson().toJson(itemSleeep));
            sleepDetail.add(itemSleeep);
        }

        int finalDeepSleepTime = deepSleepTime;
        int finalLightSleepTime = lightSleepTime;
        int finalSoberTime = soberTime;
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
                w81DeviceDataAction.saveW81DeviceSleepData(mWristbandManager.getConnectedDevice().getName(), String.valueOf(BaseManager.mUserId),
                        "0", TimeUtils.getTimeByyyyyMMdd(sleepDataList.get(sleepDataList.size()-1).getTimeStamp()), System.currentTimeMillis(), finalDeepSleepTime+ finalLightSleepTime+finalSoberTime, finalDeepSleepTime, finalLightSleepTime, finalSoberTime, gson.toJson(sleepDetail));
            }
        });

    }


    //读取手表上的联系人
    @SuppressLint("CheckResult")
    public void readDeviceContact() {
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


    //电量
    @SuppressLint("CheckResult")
    public void getDeviceBattery() {
        mWristbandManager.requestBattery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BatteryStatus>() {
                    @Override
                    public void accept(BatteryStatus batteryStatus) throws Exception {
                        if(mCurrentDevice != null)
                            mCurrentDevice.setBattery(batteryStatus.getPercentage());
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


    //获取通用设置
    public void getCommonSet() {
        try {
            if (!mWristbandManager.isConnected())
                return;
            F18DeviceSetData f18DeviceSetData = new F18DeviceSetData();

            WristbandVersion wristbandVersion = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getWristbandVersion();
            //是否开启勿扰
            boolean isNDT = wristbandVersion.isExtNotDisturb();
            //自动心率开关
            boolean isHeart = wristbandVersion.isHeartRateEnabled();

            FunctionConfig functionConfig = mWristbandManager.getWristbandConfig().getFunctionConfig();
            //是否是24小时制 true为十二小时制，false为二十四小时制
            boolean is24Hour = functionConfig.isFlagEnable(FunctionConfig.FLAG_HOUR_STYLE);
            //公英制 true为英制，false为公制
            boolean isKmUnit = functionConfig.isFlagEnable(FunctionConfig.FLAG_LENGTH_UNIT);
            //温度单位 true为华氏摄氏度，false为摄氏度
            boolean isTemp = functionConfig.isFlagEnable(FunctionConfig.FLAG_TEMPERATURE_UNIT);
            //天气开关
            boolean isWeather = functionConfig.isFlagEnable(FunctionConfig.FLAG_WEATHER_SWITCH);
            //加强测量 true为开启，false为关闭
            boolean isStrength = functionConfig.isFlagEnable(FunctionConfig.FLAG_STRENGTHEN_TEST);

            f18DeviceSetData.setDNT(isNDT ? "已开启" : "未开启");
            f18DeviceSetData.setIs24Heart(isHeart);
            f18DeviceSetData.setTimeStyle(is24Hour ? 0 : 1);
            f18DeviceSetData.setTempStyle(isTemp ? 1 : 0);
            f18DeviceSetData.setIsKmUnit(isKmUnit ? 1 : 0);
            f18DeviceSetData.setCityName(!isWeather ? "未开启" : "已开启");
            f18DeviceSetData.setStrengthMeasure(isStrength);
            F18DeviceSetAction.saveOrUpdateF18DeviceSet(mUserId, mWristbandManager.getConnectedAddress(), mWristbandManager.getConnectedDevice().getName(), F18DbType.F18_DEVICE_SET_TYPE, "", new Gson().toJson(f18DeviceSetData));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置加强测量
    @SuppressLint("CheckResult")
    public void setStrengthMeasure(boolean isOpen) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            FunctionConfig functionConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getFunctionConfig();
            functionConfig.setFlagEnable(FunctionConfig.FLAG_STRENGTHEN_TEST, isOpen);
            mWristbandManager.setFunctionConfig(functionConfig).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action() {
                @Override
                public void run() throws Exception {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //进入遥控拍照
    @SuppressLint("CheckResult")
    public void intoTakePhotoStatus(boolean isInto) {
        if (!mWristbandManager.isConnected())
            return;
        mWristbandManager.setCameraStatus(isInto)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    //时间格式
    @SuppressLint("CheckResult")
    public void setTimeStyle(boolean is24Hour) {
        if (!mWristbandManager.isConnected())
            return;
        FunctionConfig functionConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getFunctionConfig();
        functionConfig.setFlagEnable(FunctionConfig.FLAG_HOUR_STYLE, is24Hour);
        mWristbandManager.setFunctionConfig(functionConfig).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });

    }

    //设置用户信息
    public void setUserInfo(boolean sex, int age, float height, float weight) {
        mWristbandManager.setUserInfo(sex, age, height, weight);
    }

    //公英制
    public void setKmUnit(boolean isKm) {
        if (!mWristbandManager.isConnected())
            return;
        FunctionConfig functionConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getFunctionConfig();
        functionConfig.setFlagEnable(FunctionConfig.FLAG_LENGTH_UNIT, isKm);
        mWristbandManager.setFunctionConfig(functionConfig);

    }

    //温度单位 是否是华摄氏度，true为华摄氏度，false为摄氏度
    @SuppressLint("CheckResult")
    public void setTemperUnit(boolean isTemp) {
        if (!mWristbandManager.isConnected())
            return;
        FunctionConfig functionConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getFunctionConfig();
        functionConfig.setFlagEnable(FunctionConfig.FLAG_TEMPERATURE_UNIT, isTemp);
        mWristbandManager.setFunctionConfig(functionConfig).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    /**
     * 设置语言
     * 语言支持https://github.com/htangsmart/FitCloudPro-SDK-Android/blob/master/LanguageType.png
     * 0x00-默认；0x01-中文简体；0x02-中文繁体；0x03-英文 0x09日语
     */
    public void setDeviceLanguage(byte language) {
        if (mWristbandManager.isConnected())
            mWristbandManager.setLanguage(language);
    }

    //查找手环
    @SuppressLint("CheckResult")
    public void findDevices() {
        if (mWristbandManager.isConnected())
            mWristbandManager.findWristband().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                @Override
                public void run() throws Exception {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });
    }

    //设置运动目标
    @SuppressLint("CheckResult")
    public void setDeviceSportGoal(int step, int distance, int kcal) {
        if (!mWristbandManager.isConnected())
            return;
        mWristbandManager.setExerciseTarget(step, distance, kcal).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {

            }
        }, new Consumer<Throwable>() {

            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    //获取喝水提醒
    public void getDrinkData() {
        DrinkWaterConfig drinkWaterConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getDrinkWaterConfig();
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

        Log.e(TAG, "------喝水提醒=" + isOpen + " " + startTime + " " + endTime);

    }


    //获取喝水提醒
    public void getDrinkData(CommAlertListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            DrinkWaterConfig drinkWaterConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getDrinkWaterConfig();
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

            Log.e(TAG, "------喝水提醒=" + isOpen + " " + startTime + " " + endTime);
            if (commAlertListener != null)
                commAlertListener.backCommAlertData(isOpen, startHour, startMinute, endHour, endMinute, intervalTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //设置喝水提醒
    public void setDrinkData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int intervalTime) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            DrinkWaterConfig drinkWaterConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getDrinkWaterConfig();
            drinkWaterConfig.setEnable(isOpen);
            drinkWaterConfig.setStart(startHour * 60 + startMinute);
            drinkWaterConfig.setEnd(endHour * 60 + endMinute);
            drinkWaterConfig.setInterval(intervalTime);
            mWristbandManager.setDrinkWaterConfig(drinkWaterConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //获取久坐提醒
    public void getSedentaryConfig(CommAlertListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            SedentaryConfig sedentaryConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getSedentaryConfig();
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
            Log.e(TAG, "-------久坐提醒=" + startTime + " " + endTime + "" + intervalTime);
            if (commAlertListener != null)
                commAlertListener.backCommAlertData(isOpen, startHour, startMinute, endHour, endMinute, intervalTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //获取久坐提醒
    public void getSedentaryConfig() {
        try {
            if (!mWristbandManager.isConnected())
                return;
            SedentaryConfig sedentaryConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getSedentaryConfig();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置久坐提醒
    public void setSedentaryConfig(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int intervalTime) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            SedentaryConfig sedentaryConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getSedentaryConfig();
            sedentaryConfig.setEnable(isOpen);
            sedentaryConfig.setStart(startHour * 60 + startMinute);
            sedentaryConfig.setEnd(endHour * 60 + endMinute);
            sedentaryConfig.setInterval(intervalTime);
            mWristbandManager.setSedentaryConfig(sedentaryConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取转腕亮屏
    public void getTurnWristLightingConfig() {
        if (!mWristbandManager.isConnected())
            return;
        TurnWristLightingConfig turnWristLightingConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getTurnWristLightingConfig();
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


    //获取转腕亮屏
    public void getTurnWristLightingConfig(CommAlertListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            TurnWristLightingConfig turnWristLightingConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getTurnWristLightingConfig();
            if (turnWristLightingConfig == null)
                return;
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
            if (commAlertListener != null)
                commAlertListener.backCommAlertData(isOpen, startHour, startMinute, endHour, endMinute, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置转腕亮屏
    public void setTurnWristLightingConfig(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            TurnWristLightingConfig turnWristLightingConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getTurnWristLightingConfig();
            turnWristLightingConfig.setEnable(isOpen);
            //开始时间
            turnWristLightingConfig.setStart(startHour * 60 + startMinute);
            //结束时间
            turnWristLightingConfig.setEnd(endHour * 60 + endMinute);
            mWristbandManager.setTurnWristLightingConfig(turnWristLightingConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //获取连续监测
    public void getHealthyConfig() {
        if (!mWristbandManager.isConnected())
            return;
        HealthyConfig healthyConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getHealthyConfig();
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


    //获取连续监测
    public void getHealthyConfig(CommAlertListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            HealthyConfig healthyConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getHealthyConfig();
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
            if (commAlertListener != null)
                commAlertListener.backCommAlertData(isOpen, startHour, startMinute, endHour, endMinute, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //设置连续监测
    public void setHealthyConfig(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            HealthyConfig healthyConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getHealthyConfig();
            //开关
            healthyConfig.setEnable(isOpen);
            //开始时间
            healthyConfig.setStart(startHour * 60 + startMinute);
            //结束时间
            healthyConfig.setEnd(endHour * 60 + endMinute);
            mWristbandManager.setHealthyConfig(healthyConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //获取勿扰设置
    public void getNotDisturbConfig() {
        try {
            if (!mWristbandManager.isConnected())
                return;
            NotDisturbConfig notDisturbConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotDisturbConfig();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取勿扰设置
    public void getNotDisturbConfig(CommAlertListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            NotDisturbConfig notDisturbConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotDisturbConfig();
            if (notDisturbConfig == null)
                return;
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
            if (commAlertListener != null) {
                commAlertListener.backCommAlertData(isOpen, startHour, startMinute, endHour, endMinute, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //设置勿扰设置
    public void setNotDisturbConfig(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            NotDisturbConfig notDisturbConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotDisturbConfig();
            notDisturbConfig.setEnablePeriodTime(isOpen);

            //开始时间
            notDisturbConfig.setStart(startHour * 60 + startMinute);
            //结束时间
            notDisturbConfig.setEnd(endHour * 60 + endMinute);
            mWristbandManager.setNotDisturbConfig(notDisturbConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 天气设置
     * 状态序号
     * 0x00 未知
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
     */
    public void setWeatherData(String cityName) {
        //mWristbandManager.setWeather()
    }

    //获取app消息状态
    public void readAppsStatus(F18AppsStatusListener f18AppsStatusListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            NotificationConfig notificationConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotificationConfig();
            if (f18AppsStatusListener != null)
                f18AppsStatusListener.backF18AppsStatus(notificationConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //设置消息提醒开关
    public void setAppsNotices(int flag, boolean isOpen) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            NotificationConfig notificationConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotificationConfig();
            notificationConfig.setFlagEnable(flag, isOpen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //消息推送
    public void sendNoticeToDevice(int type, String name, String content) {
        WristbandNotification wristbandNotification = new WristbandNotification();
        wristbandNotification.setName(name);
        wristbandNotification.setType((byte) type);
        wristbandNotification.setContent(content);
        mWristbandManager.sendWristbandNotification(wristbandNotification);
    }

    //获取闹钟
    @SuppressLint("CheckResult")
    public void getDeviceAlarmList(F18AlarmAllListener f18AlarmAllListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            mWristbandManager.requestAlarmList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                        }
                    })
                    .subscribe(new Consumer<List<WristbandAlarm>>() {
                        @Override
                        public void accept(List<WristbandAlarm> alarms) throws Exception {
                            if (f18AlarmAllListener != null)
                                f18AlarmAllListener.backAllDeviceAlarm(alarms);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置闹钟
    @SuppressLint("CheckResult")
    public void setDeviceAlarm(List<WristbandAlarm> list, F18CommStatusListener commStatusListener) {
        try {
            if(!mWristbandManager.isConnected())
                return;
            mWristbandManager.setAlarmList(list).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            if(commStatusListener != null)
                                commStatusListener.isSetStatus(true,"");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(commStatusListener != null)
                                commStatusListener.isSetStatus(false,throwable.getMessage());
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //读取通讯录
    @SuppressLint("CheckResult")
    public void redDeviceContact(F18ContactListener f18ContactListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            mWristbandManager.requestContactsList().observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {

                        }
                    })
                    .subscribe(new Consumer<List<WristbandContacts>>() {
                        @Override
                        public void accept(List<WristbandContacts> wristbandContacts) throws Exception {
                            if (f18ContactListener != null)
                                f18ContactListener.onContactAllData(wristbandContacts);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置通讯录
    @SuppressLint("CheckResult")
    public void setDeviceContact(List<WristbandContacts> contactsList) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            mWristbandManager.setContactsList(contactsList).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //    //设置手表识别的联系人
//    public void setDeviceContactCreate(WristbandContacts wristbandContacts){
//        try {
//            if(!mWristbandManager.isConnected())
//                return;
//            WristbandContacts.create()
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }}

}


