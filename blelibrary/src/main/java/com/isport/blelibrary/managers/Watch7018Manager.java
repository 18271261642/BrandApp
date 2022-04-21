package com.isport.blelibrary.managers;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import com.google.gson.Gson;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.BatteryStatus;
import com.htsmart.wristband2.bean.ConnectionState;
import com.htsmart.wristband2.bean.DialBinInfo;
import com.htsmart.wristband2.bean.DialUiInfo;
import com.htsmart.wristband2.bean.HealthyDataResult;
import com.htsmart.wristband2.bean.SyncDataRaw;
import com.htsmart.wristband2.bean.WristbandAlarm;
import com.htsmart.wristband2.bean.WristbandConfig;
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
import com.htsmart.wristband2.bean.data.SportItem;
import com.htsmart.wristband2.bean.data.StepData;
import com.htsmart.wristband2.bean.data.TemperatureData;
import com.htsmart.wristband2.bean.data.TodayTotalData;
import com.htsmart.wristband2.bean.weather.WeatherForecast;
import com.htsmart.wristband2.bean.weather.WeatherToday;
import com.htsmart.wristband2.dfu.DfuCallback;
import com.htsmart.wristband2.dfu.DfuManager;
import com.htsmart.wristband2.packet.SyncDataParser;
import com.isport.blelibrary.BleConstance;
import com.isport.blelibrary.db.CommonInterFace.DeviceMessureData;
import com.isport.blelibrary.db.action.W81Device.W81DeviceDataAction;
import com.isport.blelibrary.db.action.W81Device.W81DeviceEexerciseAction;
import com.isport.blelibrary.db.action.f18.F18DeviceSetAction;
import com.isport.blelibrary.db.parse.DeviceDataSave;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.F18StepHourMap;
import com.isport.blelibrary.db.table.f18.F18CommonDbBean;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DetailStepBean;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.db.table.f18.F18StepBean;
import com.isport.blelibrary.db.table.f18.listener.CommAlertListener;
import com.isport.blelibrary.db.table.f18.listener.F18GetBatteryListener;
import com.isport.blelibrary.db.table.f18.listener.F18LongSetListener;
import com.isport.blelibrary.db.table.w811w814.W81DeviceExerciseData;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.entry.F18AlarmAllListener;
import com.isport.blelibrary.entry.F18AppsStatusListener;
import com.isport.blelibrary.entry.F18CommStatusListener;
import com.isport.blelibrary.entry.F18ContactListener;
import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.entry.WristbandWeather;
import com.isport.blelibrary.interfaces.OnF18DialStatusListener;
import com.isport.blelibrary.result.impl.watch.DeviceMessureDataResult;
import com.isport.blelibrary.utils.AppLanguageUtil;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.StepArithmeticUtil;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.TimeUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static final String ACTION_KEY_PARAMS = "key_params";
    //数据同步完成
    public static final String SYNC_DATA_COMPLETE = "com.isport.blelibrary.managers.sync_complete";
    //同步完成，用于解绑操作
    public static final String SYNC_UNBIND_DATA_COMPLETE = "com.isport.blelibrary.managers.unbind_sync_complete";
    //连接成功
    public static final String F18_CONNECT_STATUS = "com.isport.blelibrary.managers.connected";
    //连接中
    public static final String F18_CONNECT_ING = "com.isport.blelibrary.managers.conning";


    //连接失败
    public static final String F18_DIS_CONNECTED_STATUS = "com.isport.blelibrary.managers.dis_connected";
    //锻炼数据保存完成，发送广播上传
    public static final String F18_EXERCISE_SYNC_COMPLETE = "com.isport.blelibrary.managers.exercise_complete";
    //拍照
    public static final String F18_TAKE_PICK_ACTION = "com.isport.blelibrary.managers.take_photo";

    private static Watch7018Manager watch7018Manager;
    private static Context mContext;

    //数据操作类
    private final WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();

    private Disposable mStateDisposable;
    private Disposable mErrorDisposable;
    private ConnectionState mState = ConnectionState.DISCONNECTED;

    private String userId;

    private int connStauts = 0x00;


    private F18HomeCountStepListener f18HomeCountStepListener;

    public void setF18HomeCountStepListener(F18HomeCountStepListener f18HomeCountStepListener) {
        this.f18HomeCountStepListener = f18HomeCountStepListener;
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int whatCode = msg.what;
            boolean isListenerNull = mBleReciveListeners == null;
            if (isListenerNull)
                return;
            if (whatCode == HandlerContans.mHandlerConnetSuccessState) {  //连接成功
                sendActionBroad(F18_CONNECT_STATUS,mWristbandManager.getConnectedAddress());
                Log.e(TAG,"---------第一次连接成功-----");
                setDeviceLanguage();
                getDeviceBattery();
                getCommonSet(); //连接成功获取一下设置相关

                if(mDeviceInformationTable == null)
                    mDeviceInformationTable = new DeviceInformationTable();
                if(mCurrentDevice.getAddress() != null){
                    mDeviceInformationTable.setMac(mCurrentDevice.getAddress());
                }

                mDeviceInformationTable.setDeviceId(mCurrentDevice.getDeviceName());
                ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, -1);

                if (mCurrentDevice != null) {
                    for (int i = 0; i < mBleReciveListeners.size(); i++) {
                        mBleReciveListeners.get(i).onConnResult(true, true, mCurrentDevice);
                    }
                }
            }

            if(whatCode == HandlerContans.mDevcieMeasureHrSuccess){ //手动心率测量
                closeMeasure();
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_once_hr, mCurrentDevice.getDeviceName()));
                }
            }


            //血压测量成功
            if(whatCode ==HandlerContans.mDevcieBloodPressureMessureSuccess){
                closeMeasure();
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_bloodpre, mCurrentDevice.getDeviceName()));
                }
            }

            //血氧测量成功
            if(whatCode == HandlerContans.mDevcieMeasureOxyenSuccess){
                closeMeasure();
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_oxygen, mCurrentDevice.getDeviceName()));
                }
            }

            if(whatCode == HandlerContans.mDeviceTempMeasure) {
                closeMeasure();
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.today_temp, mCurrentDevice.getDeviceName()));
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


           if(whatCode == HandlerContans.mHandlerConnetting) {  //连接中
               if ( mCurrentDevice != null)
                   for (int i = 0; i < mBleReciveListeners.size(); i++) {
                       Logger.myLog(TAG,"------HandlerContans.mHandlerConnettin--mCurrentDevice="+mCurrentDevice.toString());
                       mBleReciveListeners.get(i).onConnecting(mCurrentDevice);
                   }
           }

           //断开连接
           if(whatCode == HandlerContans.mHandlerConnetFailState){
               if (mCurrentDevice != null) {
                   for (int i = 0; i < mBleReciveListeners.size(); i++) {
                       mBleReciveListeners.get(i).onConnResult(false, false, mCurrentDevice);
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
      //  Log.e(TAG,"----isBindDevice="+isBindDevice+" userId="+mUserId+" "+mCurrentDevice.getDeviceName());
        if(mUserId == null)
            mUserId = userId;
        if(mUserId == null){
            return;
        }

        if(getConnStatus() == 0x02){  //正在连接中，不连接了
            return;
        }

        if(mWristbandManager.isConnected()){    //已经连接就不连接了
            return;
        }

        mWristbandManager.connect(bleMac, mUserId, true, true, mAge, mHeight, mWeight);
    }



    public int getConnStatus(){

        return connStauts;
    }


    public void connectDevice(BaseDevice baseDevice,boolean isBind){
        setConnectListener();
    }


    public boolean isConnected(){
        return mWristbandManager.isConnected();
    }

    //断开连接
    public void disConnectDevice() {
        Log.e(TAG,"-----手动断开连接="+(mWristbandManager.isConnected()));
        if(mWristbandManager.isConnected())
          mWristbandManager.close();
    }

    //解绑，解绑后需要使用bind模式
    @SuppressLint("CheckResult")
    public void unBindDevice() {
        if(!mWristbandManager.isConnected())
            return;
        mWristbandManager.userUnBind().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.e(TAG,"------解绑="+aBoolean);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG,"------Throwable="+throwable.getMessage());
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
                    connStauts = 0x01; //连接成功
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerConnetSuccessState, 1000);
                    setDeviceListener();
                }

                if (connectionState == ConnectionState.CONNECTING) {  //正在连接中
                    connStauts = 0x02; //正在连接
                    sendActionBroad(F18_CONNECT_ING,"");
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerConnetting,1000);
                }

                if (connectionState == ConnectionState.DISCONNECTED) {    //断开连接
                    sendActionBroad(F18_DIS_CONNECTED_STATUS,"");
                    connStauts = 0x00; //断开连接
                    mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetFailState);
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
                    Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(3 * 1000);
                }

                if(integer == WristbandManager.MSG_HUNG_UP_PHONE){  //手表上挂断电话
                    Intent intent = new Intent();
                    intent.setAction(BleConstance.W560_DIS_CALL_ACTION);
                    intent.putExtra(BleConstance.W560_PHONE_STATUS,2);
                    mContext.sendBroadcast(intent);

                }

                //调起相机拍照
                if (integer == WristbandManager.MSG_CAMERA_TAKE_PHOTO) {
                    sendActionBroad(F18_TAKE_PICK_ACTION,"");
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
                        Log.e(TAG,"------最近一次测量记录="+healthyDataResult.toString()+"\n"+healthyDataResult.getTemperatureBody());
                        if(healthyDataResult == null)
                            return;
                        int htV = healthyDataResult.getHeartRate();
                        if(htV != 0){
                            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureHrSuccess, 100);
                            DeviceDataSave.saveOneceHrData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),htV, System.currentTimeMillis(), String.valueOf(0));
                        }

                        //血压
                        int lBp = healthyDataResult.getDiastolicPressure();
                        int hBp = healthyDataResult.getSystolicPressure();
                        if(hBp !=0 && lBp != 0){
                            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieBloodPressureMessureSuccess,100);
                            DeviceDataSave.saveBloodPressureData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),  hBp, lBp,System.currentTimeMillis(), String.valueOf(0));
                        }


                        //血氧
                        if(healthyDataResult.getOxygen() != 0){
                            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureOxyenSuccess,100);
                            DeviceDataSave.saveOxyenModelData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getOxygen(), System.currentTimeMillis(), String.valueOf(0));
                        }


                        //温度
                        if(healthyDataResult.getTemperatureBody()!=0){
                            mHandler.sendEmptyMessageDelayed(HandlerContans.mDeviceTempMeasure,100);
                            DeviceDataSave.saveTempData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getTemperatureBody(), System.currentTimeMillis(), String.valueOf(0));

                        }


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

        measureDisposable = mWristbandManager.openHealthyRealTimeData(healthType,2)
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
                stringBuilder.append("心率="+healthyDataResult.getHeartRate()+"\n");
                stringBuilder.append("体温="+healthyDataResult.getTemperatureBody()+"\n");
                stringBuilder.append("血压="+healthyDataResult.getSystolicPressure()+"/"+healthyDataResult.getDiastolicPressure()+"\n");


                Log.e(TAG,"------手动测量="+stringBuilder.toString());

                if(healthType == WristbandManager.HEALTHY_TYPE_HEART_RATE){ //心率
                    int htV = healthyDataResult.getHeartRate();
                    if(htV == 0)
                        return;
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureHrSuccess, 100);
                    DeviceDataSave.saveOneceHrData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getHeartRate(), System.currentTimeMillis(), String.valueOf(0));
                }

                //血压
                if(healthType == WristbandManager.HEALTHY_TYPE_BLOOD_PRESSURE){
                    int lBp = healthyDataResult.getDiastolicPressure();
                    int hBp = healthyDataResult.getSystolicPressure();
                    if(hBp == 0 || lBp == 0)
                        return;
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieBloodPressureMessureSuccess,100);
                    DeviceDataSave.saveBloodPressureData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),  hBp, lBp,System.currentTimeMillis(), String.valueOf(0));
                }
                //血氧
                if(healthType == WristbandManager.HEALTHY_TYPE_OXYGEN){
                    if(healthyDataResult.getOxygen() == 0)
                        return;
                    closeMeasure();
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureOxyenSuccess,100);
                    DeviceDataSave.saveOxyenModelData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getOxygen(), System.currentTimeMillis(), String.valueOf(0));
                }
                //温度
                if(healthType == WristbandManager.HEALTHY_TYPE_TEMPERATURE){
                    if(healthyDataResult.getTemperatureBody()==0)
                        return;
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDeviceTempMeasure,100);
                    DeviceDataSave.saveTempData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getTemperatureBody(), System.currentTimeMillis(), String.valueOf(0));
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


    private int currStep;
    private float currDistance;
    private float currKcal ;

    public String[] getCurrCountStep(){
        return new String[]{currStep+"",currDistance/1000+"",currKcal/1000+""};
    }


    //同步设备数据
    @SuppressLint("CheckResult")
    public void syncDeviceData() {
        if(!mWristbandManager.isConnected())
            return;

        getDeviceLastHealthMeasure();

        setSyncStatusListener();
        mWristbandManager.syncData()
                .observeOn(Schedulers.io(), true)
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
                            analysisDetalStep(datas);
                        }

                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SPORT){    //运动数据 运动模式产生的数据
                            List<SportData> datas = SyncDataParser.parserSportData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            Log.e(TAG,"------Sport数据="+new Gson().toJson(datas));
                            analysisSportData(datas);
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
                }).doOnComplete(new Action() {  //数据同步完成
            @Override
            public void run() throws Exception {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendActionBroad(SYNC_DATA_COMPLETE,"");
                        sendActionBroad(SYNC_UNBIND_DATA_COMPLETE,"");
                    }
                },1000);
                Log.e(TAG,"-----数据同步完成---");

            }
        })
                .subscribe(new Action() {

            @Override
            public void run() throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG,"-----Throwable---="+throwable.getMessage());
            }
        });
    }


    //处理详细的计步数据 key = yyyy-MM-dd HH 格式日期
    private Map<String,F18StepBean> tempMap = new HashMap<>();
    private void analysisDetalStep(List<StepData> dataList){
        if(dataList==null || dataList.size() ==0)
            return;
        try {
            tempMap.clear();
            String userId = mUserId;
            String deviceId = mCurrentDevice.getDeviceName();

            for(StepData stepData : dataList){
                //天 yyyy-MM-dd格式
                String dayStr = DateUtil.getFormatTime(stepData.getTimeStamp(),"yyyy-MM-dd");
                F18StepBean tmpFb = new F18StepBean();
                tmpFb.setStep(stepData.getStep());
                tmpFb.setKcal(stepData.getCalories());
                tmpFb.setDistance(stepData.getDistance());
                F18DeviceSetAction.saveF18DeviceDetailStep(userId,deviceId,dayStr,stepData.getTimeStamp(),tmpFb,0);

            }

            saveEmptyDetailStep(userId,deviceId);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void saveEmptyDetailStep(String userId,String deviceId){
        try {
            //保存完成后查询，解析保存到数据库
            List<F18DetailStepBean> saveList = F18DeviceSetAction.getF18DetailList(userId,deviceId,DateUtil.getCurrDay());
            if(saveList != null){
                for(F18DetailStepBean fb : saveList){
                    String dayStr = DateUtil.getFormatTime(fb.getTimeLong(),"yyyy-MM-dd HH");
                    if(tempMap.get(dayStr)!=null){
                        F18StepBean fbs = tempMap.get(dayStr);
                        if(fbs != null){
                            F18StepBean tmpFb = new F18StepBean();
                            tmpFb.setStep(fbs.getStep()+fb.getStep());
                            tmpFb.setKcal(StepArithmeticUtil.addNumber(fbs.getKcal(),fb.getKcal()));
                            tmpFb.setDistance(StepArithmeticUtil.addNumber(fbs.getDistance(),fb.getDistance()));
                            tempMap.put(dayStr,tmpFb);
                        }

                    }else{
                        tempMap.put(dayStr,new F18StepBean(fb.getStep(),fb.getDistance(),fb.getKcal()));
                    }

                }
            }

            if(tempMap.isEmpty())
                return;
            Map<String, F18StepBean> hour24Map = F18StepHourMap.getF18HourMap();
            for(Map.Entry<String,F18StepBean> tm : tempMap.entrySet()){
                String tmpKey = DateUtil.getDateHourStr(tm.getKey());  //转换成 HH格式
                if(hour24Map.containsKey(tmpKey)){
                    hour24Map.put(tmpKey,tm.getValue());
                }

            }
            List<String> hourList = new ArrayList<>();
            for(Map.Entry<String,F18StepBean> ltMap : hour24Map.entrySet()){
                hourList.add(ltMap.getKey());
            }

            //排序
            Collections.sort(hourList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });

            List<String[]> stepArray = new ArrayList<>();
            for(int i = 0;i<hourList.size();i++){
                F18StepBean collStep = hour24Map.get(hourList.get(i));
                String[] stArr = new String[]{hourList.get(i),
                        String.valueOf(collStep.getStep()),String.valueOf(collStep.getDistance()),String.valueOf(collStep.getKcal())};
                stepArray.add(stArr);
            }
            Log.e(TAG,"------详细计步数据保存数据库="+new Gson().toJson(stepArray));

         //   F18DeviceSetAction.deleteF18DetailStepBean(userId,deviceId,DateUtil.getCurrDay());
            new W81DeviceDataAction().saveDeviceStepArrayData("0",mCurrentDevice.getDeviceName(),BaseManager.mUserId,null,DateUtil.getCurrDay(),new Gson().toJson(stepArray));

            F18DeviceSetAction.updateF18DetailStep(userId,deviceId,DateUtil.getCurrDay());

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //处理锻炼数据
    private void analysisSportData(List<SportData> datas) {
        if(datas == null || datas.isEmpty())
            return;
        for(SportData sportData : datas){
            W81DeviceExerciseData w81De = new W81DeviceExerciseData();
            w81De.setUserId(BaseManager.mUserId);
            w81De.setDeviceId(mCurrentDevice.getDeviceName());
            w81De.setWristbandSportDetailId("0");
            w81De.setDateStr(TimeUtils.getTimeByyyyyMMdd(sportData.getTimeStamp()));
            w81De.setStartTimestamp(sportData.getTimeStamp());
            w81De.setEndTimestamp(sportData.getTimeStamp()+(sportData.getDuration()*1000));
            w81De.setVaildTimeLength(sportData.getDuration()+"");
            w81De.setExerciseType(sportData.getSportType()+"");
            w81De.setTotalDistance(String.valueOf((sportData.getDistance() * 1000)));
            w81De.setTotalSteps(String.valueOf(sportData.getSteps()));
            w81De.setTotalCalories(String.valueOf( (int)sportData.getCalories()));

            //心率集合
            List<Integer> allHeartList = new ArrayList<>();
            //步数集合
            List<Integer> stepList = new ArrayList<>();
            //距离集合
            List<Integer> distanceList = new ArrayList<>();
            //卡路里集合
            List<Integer> caloriesList = new ArrayList<>();

            //平均心率
            int avgHeart = 0;
            int htCount = 0;
            List<Integer> tmpHeartList = new ArrayList<>();

            for(SportItem sportItem : sportData.getItems()){
                int htV = sportItem.getHeartRate();
                if(htV != 0){
                    tmpHeartList.add(htV);
                    htCount += htV;
                }
                allHeartList.add(sportItem.getHeartRate());
                stepList.add(sportItem.getSteps());
                distanceList.add((int) (sportItem.getDistance() * 1000));
                caloriesList.add((int) (sportItem.getCalories()));
            }

            avgHeart = (htCount == 0 || tmpHeartList.size() == 0) ? 0 : htCount / tmpHeartList.size();
            w81De.setHrArray(new Gson().toJson(allHeartList));
            w81De.setStepArray(new Gson().toJson(stepList));
            w81De.setDistanceArray(new Gson().toJson(distanceList));
            w81De.setCalorieArray(new Gson().toJson(caloriesList));
            w81De.setAvgHr(avgHeart == 0 ? "--":avgHeart+"");
            W81DeviceEexerciseAction action = new W81DeviceEexerciseAction();
            action.saveDefExerciseData(w81De);
        }

        sendActionBroad(F18_EXERCISE_SYNC_COMPLETE,"");

    }

    //监听同步状态信息
    @SuppressLint("CheckResult")
    private void setSyncStatusListener() {
        mWristbandManager.observerSyncDataState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {

                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == null)
                            return;

                        Log.e(TAG,"----同步状态码="+integer);

                        if (integer < 0) {      //同步失败
                            if (integer == WristbandManager.SYNC_STATE_FAILED_DISCONNECTED) {

                            }
                        }
                    }
                });

    }

    //当天汇总计步
    private void analysisCurrDayStep(TodayTotalData todayTotalData){

        //saveEmptyDetailStep(userId,mCurrentDevice.getDeviceName());
        this.currStep = todayTotalData.getStep();
        this.currKcal = todayTotalData.getCalorie();
        this.currDistance = todayTotalData.getDistance();
        if(f18HomeCountStepListener != null)
            f18HomeCountStepListener.backHomeCountData(todayTotalData.getStep(),todayTotalData.getDistance(),todayTotalData.getCalorie());
//
//        //保存到数据库
//        ThreadPoolUtils.getInstance().addTask(new Runnable() {
//            @Override
//            public void run() {
//                long currentTime = System.currentTimeMillis();
//                W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
//                int kcal = todayTotalData.getCalorie() ==0 ? 0 : todayTotalData.getCalorie();
//
//                kcal = kcal == 0 ? 0 : kcal / 1000;
//                w81DeviceDataAction.saveW81DeviceStepData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),
//                        "0", TimeUtils.getTimeByyyyyMMdd(currentTime), currentTime, todayTotalData.getStep(), todayTotalData.getDistance(), kcal, false);
//            }
//        });
    }

    //解析心率数据
    private void analysisHeartData(List<HeartRateData> datas) {
        Log.e(TAG,"--------心率数据="+new Gson().toJson(datas));
    }

    //解析血压数据
    private void analysisBloodData(List<BloodPressureData> bpList) {

    }

    //解析睡眠数据
    private void analysisSleepData(List<SleepData> sleepDataList) {
        if(sleepDataList == null)
            return;
        Log.e(TAG,"-----详细睡眠数据="+new Gson().toJson(sleepDataList));

        //F18DeviceSetAction.saveF18DeviceDetailStep(userId,mCurrentDevice.getDeviceName(),DateUtil.getCurrDay(),System.currentTimeMillis(),);


        List<SleepItemData> f18SleepList = sleepDataList.get(sleepDataList.size()-1).getItems();

        final ArrayList<ArrayList<String>> sleepDetail = new ArrayList<>();

        //总的睡眠时间
        long countSleep = sleepDataList.get(sleepDataList.size()-1).getTimeStamp();

        int len = f18SleepList.size();

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

            if(changeSleepStatus(sleepStatus) == 1){   //深睡  传后台是清醒
               // deepSleepTime +=intervalMinute;
                soberTime += intervalMinute;
            }

            if(changeSleepStatus(sleepStatus) == 2){  //浅睡
                lightSleepTime +=intervalMinute;
            }

            if(changeSleepStatus(sleepStatus) == 3){   //清醒 传后台是深睡
               // soberTime += intervalMinute;
                deepSleepTime +=intervalMinute;
            }

            //上传后台，1清醒；2浅睡；3深睡

            itemSleeep.add(changeSleepStatus(sleepData.getStatus()) + "");
            itemSleeep.add(CommonDateUtil.getTimeFromLong(startTime) + "");
            itemSleeep.add(CommonDateUtil.getTimeFromLong(endTime) + "");
            itemSleeep.add(intervalMinute + "");

            Log.e(TAG,"-----睡眠item="+new Gson().toJson(itemSleeep));
            sleepDetail.add(itemSleeep);
        }

        Log.e(TAG,"-----保存睡眠="+deepSleepTime +" "+ lightSleepTime + " "+soberTime);

        Gson gson = new Gson();
        W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
        w81DeviceDataAction.saveW81DeviceSleepData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),
                "0", TimeUtils.getTimeByyyyyMMdd(sleepDataList.get(sleepDataList.size()-1).getTimeStamp()), System.currentTimeMillis(), deepSleepTime+ lightSleepTime+soberTime, deepSleepTime, lightSleepTime, soberTime, gson.toJson(sleepDetail));


    }



    private int changeSleepStatus(int status){
        if(status == 1)
            return 3;
        if(status == 2)
            return 2;
        if(status == 3)
            return 1;
        return 0;
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
                        Log.e(TAG,"-----手表电量="+batteryStatus.getBatteryCount()+" "+batteryStatus.getPercentage());
                        if(mDeviceInformationTable == null){
                            mDeviceInformationTable = new DeviceInformationTable();
                        }
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


    //电量
    @SuppressLint("CheckResult")
    public void getDeviceBattery(F18GetBatteryListener f18GetBatteryListener) {
        mWristbandManager.requestBattery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BatteryStatus>() {
                    @Override
                    public void accept(BatteryStatus batteryStatus) throws Exception {
                        if(mCurrentDevice != null)
                            mCurrentDevice.setBattery(batteryStatus.getPercentage());
                        Log.e(TAG,"-----手表电量="+batteryStatus.getBatteryCount()+" "+batteryStatus.getPercentage());
                        if(f18GetBatteryListener != null)
                            f18GetBatteryListener.deviceBattery(batteryStatus.getPercentage());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });


        mWristbandManager.requestWristbandConfig().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WristbandConfig>() {

                    @Override
                    public void accept(WristbandConfig wristbandConfig) throws Exception {
                        //设备的版本
                        WristbandVersion wristbandVersion = wristbandConfig.getWristbandVersion();
                        if(f18GetBatteryListener != null)
                            f18GetBatteryListener.deviceVersionName(wristbandVersion.getApp());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    //设置语言
    @SuppressLint("CheckResult")
    public void setDeviceLanguage(){
        if(!mWristbandManager.isConnected())
            return;
        boolean isZh = AppLanguageUtil.isZh(mContext);
        mWristbandManager.setLanguage((byte) (isZh ? 0x01 : 0x03)).observeOn(AndroidSchedulers.mainThread())
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


    //获取通用设置
    @SuppressLint("CheckResult")
    public void getCommonSet() {
        try {
            if (!mWristbandManager.isConnected())
                return;
            Log.e(TAG,"--------获取设置-----");
            mWristbandManager.requestWristbandConfig().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<WristbandConfig>() {
                       
                        @Override
                        public void accept(WristbandConfig wristbandConfig) throws Exception {
                            saveDeviceMsgData(wristbandConfig);
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

    private void saveDeviceMsgData(WristbandConfig wristbandConfig){
        try {
          if(wristbandConfig == null)
              return;
            F18DeviceSetData f18DeviceSetData = null;

            F18CommonDbBean f18CommonDbBean = F18DeviceSetAction.querySingleBean(mUserId,mCurrentDevice.getDeviceName(), F18DbType.F18_DEVICE_SET_TYPE);

            if(f18CommonDbBean != null){
                String str = f18CommonDbBean.getTypeDataStr();
                f18DeviceSetData = new Gson().fromJson(str,F18DeviceSetData.class);
            }
            if(f18DeviceSetData == null){
                f18DeviceSetData = new F18DeviceSetData();
            }

            //设备的版本
            WristbandVersion wristbandVersion = wristbandConfig.getWristbandVersion();

            Log.e(TAG,"-----固件版本="+wristbandVersion.getRawVersion()+"\n"+wristbandVersion.toString());

            //是否开启勿扰
            boolean isNDT = wristbandVersion.isExtNotDisturb();
            //自动心率开关
            boolean isHeart = wristbandVersion.isHeartRateEnabled();

            FunctionConfig functionConfig =wristbandConfig.getFunctionConfig();
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


            ///勿扰

            //步数目标
            f18DeviceSetData.setStepGoal(f18DeviceSetData.getStepGoal() == 0 ? 6000 : f18DeviceSetData.getStepGoal());
            //距离目标
            f18DeviceSetData.setDistanceGoal(f18DeviceSetData.getDistanceGoal() == 0 ? 1000 : f18DeviceSetData.getDistanceGoal());
            f18DeviceSetData.setKcalGoal(f18DeviceSetData.getKcalGoal() == 0 ? 10 : f18DeviceSetData.getKcalGoal());

            //固件版本
            f18DeviceSetData.setDeviceVersionName(wristbandVersion.getApp() );

            //定时检测
            f18DeviceSetData.setContinuMonitor(f18DeviceSetData.getContinuMonitor()==null ? "" : f18DeviceSetData.getContinuMonitor());
            //喝水提醒
            f18DeviceSetData.setDrinkAlert(f18DeviceSetData.getDrinkAlert() == null ? "未开启" : f18DeviceSetData.getDrinkAlert());
            //闹钟
            f18DeviceSetData.setAlarmCount(f18DeviceSetData.getAlarmCount());
            //久坐
            f18DeviceSetData.setLongSitStr(f18DeviceSetData.getLongSitStr() == null ? "未开启" : f18DeviceSetData.getLongSitStr());
            //抬腕亮屏
            f18DeviceSetData.setTurnWrist(f18DeviceSetData.getTurnWrist() == null ? "未开启" : f18DeviceSetData.getTurnWrist());
//            //勿扰模式
//            f18DeviceSetData.setDNT(f18DeviceSetData.getDNT() == null ? "未开启" : f18DeviceSetData.getDNT());
            f18DeviceSetData.setIs24Heart(isHeart);
            f18DeviceSetData.setTimeStyle(is24Hour ? 0 : 1);
            f18DeviceSetData.setTempStyle(isTemp ? 1 : 0);
            f18DeviceSetData.setIsKmUnit(isKmUnit ? 1 : 0);
            f18DeviceSetData.setCityName(!isWeather ? "未开启" : "已开启");
            f18DeviceSetData.setStrengthMeasure(isStrength);
            F18DeviceSetAction.saveOrUpdateF18DeviceSet(mUserId, mCurrentDevice.getDeviceName(),  F18DbType.F18_DEVICE_SET_TYPE, new Gson().toJson(f18DeviceSetData));


        }catch (Exception e){
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

    public void setUserId(String userId){
        this.userId = userId;
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
    @SuppressLint("CheckResult")
    public void setDrinkData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int intervalTime) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            DrinkWaterConfig drinkWaterConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getDrinkWaterConfig();
            drinkWaterConfig.setEnable(isOpen);
            drinkWaterConfig.setStart(startHour * 60 + startMinute);
            drinkWaterConfig.setEnd(endHour * 60 + endMinute);
            drinkWaterConfig.setInterval(intervalTime);
            mWristbandManager.setDrinkWaterConfig(drinkWaterConfig).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {

                        }
                    }, new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("sample", "", throwable);

                        }
                    });;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //获取久坐提醒
    public void getSedentaryConfig(F18LongSetListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            SedentaryConfig sedentaryConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getSedentaryConfig();
            //开关
            boolean isOpen = sedentaryConfig.isEnable();
            //免打扰
            boolean isDnt = sedentaryConfig.isNotDisturbEnable();
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
                commAlertListener.backCommF18AlertData(isOpen, isDnt,startHour, startMinute, endHour, endMinute, intervalTime);
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
    @SuppressLint("CheckResult")
    public void setSedentaryConfig(boolean isOpen, boolean isDnt,int startHour, int startMinute, int endHour, int endMinute, int intervalTime) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            SedentaryConfig sedentaryConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getSedentaryConfig();
            sedentaryConfig.setEnable(isOpen);
            sedentaryConfig.setNotDisturbEnable(isDnt);
            sedentaryConfig.setStart(startHour * 60 + startMinute);
            sedentaryConfig.setEnd(endHour * 60 + endMinute);
            sedentaryConfig.setInterval(intervalTime);
            mWristbandManager.setSedentaryConfig(sedentaryConfig).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {

                        }
                    }, new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("sample", "", throwable);

                        }
                    });
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
    @SuppressLint("CheckResult")
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
            mWristbandManager.setTurnWristLightingConfig(turnWristLightingConfig)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {

                        }
                    }, new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("sample", "", throwable);

                        }
                    });
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
    @SuppressLint("CheckResult")
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
            mWristbandManager.setHealthyConfig(healthyConfig).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {

                        }
                    }, new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("sample", "", throwable);

                        }
                    });
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
    @SuppressLint("CheckResult")
    public void getNotDisturbConfig(F18LongSetListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;

            mWristbandManager.requestNotDisturbConfig().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<NotDisturbConfig>() {
                        @Override
                        public void accept(NotDisturbConfig notDisturbConfig) throws Exception {
                            if (notDisturbConfig == null)
                                return;
                            boolean isOpen = notDisturbConfig.isEnablePeriodTime();
                            //全天开启
                            boolean isAllDayOpen = notDisturbConfig.isEnableAllDay();
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
                                commAlertListener.backCommF18AlertData(isOpen, isAllDayOpen,startHour, startMinute, endHour, endMinute, 0);
                            }
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


    //设置勿扰设置
    @SuppressLint("CheckResult")
    public void setNotDisturbConfig(boolean isOpen, boolean isAllDay,int startHour, int startMinute, int endHour, int endMinute) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            NotDisturbConfig notDisturbConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotDisturbConfig();
            notDisturbConfig.setEnablePeriodTime(isOpen);
            //全天开启
            notDisturbConfig.setEnableAllDay(isAllDay);
            //开始时间
            notDisturbConfig.setStart(startHour * 60 + startMinute);
            //结束时间
            notDisturbConfig.setEnd(endHour * 60 + endMinute);
            mWristbandManager.setNotDisturbConfig(notDisturbConfig).subscribeOn(AndroidSchedulers.mainThread())
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
    @SuppressLint("CheckResult")
    public void setWeatherData(String cityName, WristbandWeather wristbandWeather) {
       if(!mWristbandManager.isConnected())
           return;
       HashMap<String,Byte> wM = f18WeatherMap();
       List<WeatherForecast> weatherForecasts = new ArrayList<>();
        List<WristbandForecast> forecastList = wristbandWeather.getForecast15Days();
        if(forecastList.isEmpty())
            return;
        WristbandData wristbandData = wristbandWeather.getCondition();
        WeatherToday weatherToday = new WeatherToday();
        weatherToday.setWeatherCode(!wM.containsKey(wristbandData.getWeatherId()) ? 0x00 : wM.get(wristbandData.getWeatherId()));
        weatherToday.setCurrentTemperature(stringToInteger(wristbandData.getTemp()));

        if(forecastList.size()>8){
            forecastList = forecastList.subList(0,7);
        }
       for(WristbandForecast wf : forecastList){

           WeatherForecast weatherForecast = new WeatherForecast();
           weatherForecast.setWeatherCode(!wM.containsKey(wf.getWeatherId()) ? 0x00 : wM.get(wf.getWeatherId()));
           weatherForecast.setHighTemperature(stringToInteger(wf.getHighTemperature()));
           weatherForecast.setLowTemperature(stringToInteger(wf.getLowTemperature()));
           weatherForecasts.add(weatherForecast);
       }
       mWristbandManager.setWeather(cityName,2,weatherToday,weatherForecasts).observeOn(AndroidSchedulers.mainThread())
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


    private int stringToInteger(String str){
        try {
            return Integer.parseInt(str);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    private  HashMap<String,Byte> f18WeatherMap(){
        HashMap<String,Byte> hashMap = new HashMap<>();
        hashMap.put("CLOUDY", (byte) 0x02);
        hashMap.put("FOGGY", (byte) 0x0c);
        hashMap.put("OVERCAST", (byte) 0x03);
        hashMap.put("RAINY", (byte) 0x06);
        hashMap.put("SNOWY", (byte) 0x0a);
        hashMap.put("SUNNY", (byte) 0x01);
        hashMap.put("SANDSTORM", (byte) 0x0b);
        hashMap.put("HAZE", (byte) 0x0c);
        return hashMap;
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
    @SuppressLint("CheckResult")
    public void setAppsNotices(int flag, boolean isOpen) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            NotificationConfig notificationConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotificationConfig();
            notificationConfig.setFlagEnable(flag, isOpen);
            mWristbandManager.setNotificationConfig(notificationConfig) .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {

                        }
                    }, new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("sample", "", throwable);

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //消息推送
    @SuppressLint("CheckResult")
    public void sendNoticeToDevice(byte type, String name, String content) {
        WristbandNotification wristbandNotification = new WristbandNotification();
        wristbandNotification.setName(name);
        wristbandNotification.setType( type);
        wristbandNotification.setContent(content);
        mWristbandManager.sendWristbandNotification(wristbandNotification).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("sample", "", throwable);

                    }
                });;
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

    @SuppressLint("CheckResult")
    public void readDeviceDial(){
        if(!mWristbandManager.isConnected())
            return;
        mWristbandManager.requestDialBinInfo().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DialBinInfo>() {
                    @Override
                    public void accept( DialBinInfo dialBinInfo) throws Exception {

                        Log.e(TAG,"---dialInfo="+new Gson().toJson(dialBinInfo));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

        mWristbandManager.requestDialUiInfo().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DialUiInfo>() {
                    @Override
                    public void accept(DialUiInfo dialUiInfo) throws Exception {
                        Log.e(TAG,"---DialUiInfo="+new Gson().toJson(dialUiInfo));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

    }





    //发送表盘
    public void setF18DialToDevice(File dialFile, Byte bt, OnF18DialStatusListener onF18DialStatusListener){
        DfuManager dfuManager = new DfuManager(mContext);
        dfuManager.setDfuCallback(new DfuCallback() {
            @Override
            public void onError(int errorType, int errorCode) {
                Log.e(TAG,"--表盘升级----onError-------="+errorType +" "+errorCode);
                if(onF18DialStatusListener != null)
                    onF18DialStatusListener.onError(errorType,errorCode);
            }

            @Override
            public void onStateChanged(int state, boolean cancelable) {
                Log.e(TAG,"--表盘升级-----onStateChanged------="+state +" "+cancelable);
                if(onF18DialStatusListener != null)
                    onF18DialStatusListener.onStateChanged(state,cancelable);
            }

            @Override
            public void onProgressChanged(int progress) {
                Log.e(TAG,"--表盘升级------onProgressChanged-----="+progress);
                if(onF18DialStatusListener != null)
                    onF18DialStatusListener.onProgressChanged(progress);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG,"--表盘升级-----onSuccess------");
                if(onF18DialStatusListener != null)
                    onF18DialStatusListener.onSuccess();
            }
        });
        dfuManager.init();
        if(onF18DialStatusListener != null)
            onF18DialStatusListener.startDial();
        dfuManager.upgradeDial(dialFile.getAbsolutePath(),bt);

    }


    //设置自定义表盘的字体颜色样式
    @SuppressLint("CheckResult")
    public void setCusTxtColorStyle(int position, byte[] bytPosition){
        if(!mWristbandManager.isConnected())
            return;
        mWristbandManager.setDialComponents(position,bytPosition).observeOn(AndroidSchedulers.mainThread())
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


    //进入OTA模式
    @SuppressLint("CheckResult")
    public void intoDufStatus(){
        if(!mWristbandManager.isConnected())
            return;
        mWristbandManager.requestEnterOTA().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    //设置升级
    public void startOta(String dfuPath, OnF18DialStatusListener onF18DialStatusListener){
        if(!mWristbandManager.isConnected())
            return;

        WristbandVersion wristbandVersion = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getWristbandVersion();
        boolean isUpdate = wristbandVersion.isUpgradeFirmwareSilent();

        DfuManager dfuManager = new DfuManager(mContext);
        dfuManager.setDfuCallback(new DfuCallback() {
            @Override
            public void onError(int i, int i1) {
                Log.e(TAG,"--固件升级-----onError------="+i +" "+i1);
                if(onF18DialStatusListener !=null)
                    onF18DialStatusListener.onError(i,i);
            }

            @Override
            public void onStateChanged(int i, boolean b) {
                Log.e(TAG,"--固件升级-----onStateChanged------="+ i +" "+b);
                if(onF18DialStatusListener !=null)
                    onF18DialStatusListener.onStateChanged(i,b);
            }

            @Override
            public void onProgressChanged(int i) {
                Log.e(TAG,"--固件升级-----onProgressChanged------="+i);
                if(onF18DialStatusListener !=null)
                    onF18DialStatusListener.onProgressChanged(i);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG,"--固件升级-----onSuccess------");
                if(onF18DialStatusListener !=null)
                    onF18DialStatusListener.onSuccess();
            }
        });


        dfuManager.init();
        dfuManager.upgradeFirmware(dfuPath);
    }


    //发送广播
    private void sendActionBroad(String action,String params){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(ACTION_KEY_PARAMS,params);
        mContext.sendBroadcast(intent);
    }

}


