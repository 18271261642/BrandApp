package com.isport.blelibrary.managers;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
 * F18?????????7018??????
 * Created by Admin
 * Date 2022/1/14
 */
public class Watch7018Manager extends BaseManager {

    private static final String TAG = "Watch7018Manager";

    public static final String ACTION_KEY_PARAMS = "key_params";
    //??????????????????
    public static final String SYNC_DATA_COMPLETE = "com.isport.blelibrary.managers.sync_complete";
    //?????????????????????????????????
    public static final String SYNC_UNBIND_DATA_COMPLETE = "com.isport.blelibrary.managers.unbind_sync_complete";
    //????????????
    public static final String F18_CONNECT_STATUS = "com.isport.blelibrary.managers.connected";
    //?????????
    public static final String F18_CONNECT_ING = "com.isport.blelibrary.managers.conning";


    //????????????
    public static final String F18_DIS_CONNECTED_STATUS = "com.isport.blelibrary.managers.dis_connected";
    //?????????????????????????????????????????????
    public static final String F18_EXERCISE_SYNC_COMPLETE = "com.isport.blelibrary.managers.exercise_complete";
    //??????
    public static final String F18_TAKE_PICK_ACTION = "com.isport.blelibrary.managers.take_photo";


    //??????????????????????????????
    public static final String SYNC_DEVICE_DATA_ING = "com.isport.blelibrary.managers.sync_start";

    private static Watch7018Manager watch7018Manager;
    private static Context mContext;

    //???????????????
    private final WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();


    private ConnectionState mState = ConnectionState.DISCONNECTED;

    private String userId;

    private int connStauts = 0x00;

    private F18SyncStatus f18SyncStatus;

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
            if(mCurrentDevice == null || mCurrentDevice.getDeviceName() == null)
                return;
            if (whatCode == HandlerContans.mHandlerConnetSuccessState) {  //????????????
                sendActionBroad(F18_CONNECT_STATUS,mWristbandManager.getConnectedAddress());
                Log.e(TAG,"---------?????????????????????-----");
                //?????????????????????????????????????????????????????????
                if(f18SyncStatus != F18SyncStatus.SYNC_DIAL_ING){
                    setDeviceLanguage();
                    getDeviceBattery();
                    getCommonSet(); //????????????????????????????????????
                    syncDeviceData("");  //??????????????????????????????????????????
                }
                if(mDeviceInformationTable == null)
                    mDeviceInformationTable = new DeviceInformationTable();
                if(mCurrentDevice.getAddress() != null){
                    mDeviceInformationTable.setMac(mCurrentDevice.getAddress());
                }
                if(mCurrentDevice.getDeviceName() != null){
                    mDeviceInformationTable.setDeviceId(mCurrentDevice.getDeviceName());
                }

                ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, -1);

                if (mCurrentDevice != null) {
                    for (int i = 0; i < mBleReciveListeners.size(); i++) {
                        mBleReciveListeners.get(i).onConnResult(true, true, mCurrentDevice);
                    }
                }
            }

            if(whatCode == HandlerContans.mDevcieMeasureHrSuccess){ //??????????????????
                closeMeasure();
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_once_hr, mCurrentDevice.getDeviceName()));
                }
            }


            //??????????????????
            if(whatCode ==HandlerContans.mDevcieBloodPressureMessureSuccess){
                closeMeasure();
                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                    mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_bloodpre, mCurrentDevice.getDeviceName()));
                }
            }

            //??????????????????
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


            //??????
            if (whatCode == HandlerContans.mHandlerbattery) {
                if (mCurrentDevice != null) {
                    for (int i = 0; i < mBleReciveListeners.size(); i++) {
                        mBleReciveListeners.get(i).onBattreyOrVersion(mCurrentDevice);
                    }
                }
            }


           if(whatCode == HandlerContans.mHandlerConnetting) {  //?????????
               if ( mCurrentDevice != null)
                   for (int i = 0; i < mBleReciveListeners.size(); i++) {
                       Logger.myLog(TAG,"------HandlerContans.mHandlerConnettin--mCurrentDevice="+mCurrentDevice.toString());
                       mBleReciveListeners.get(i).onConnecting(mCurrentDevice);
                   }
           }

           //????????????
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


    public F18SyncStatus getF18SyncStatus(){
        return f18SyncStatus;
    }

    public void setF18SyncStatus(F18SyncStatus f18SyncStatus) {
        this.f18SyncStatus = f18SyncStatus;
    }

    public WristbandManager getmWristbandManager(){
        return mWristbandManager;
    }

    /**
     * ????????????
     *
     * @param bleMac Mac??????
     * @param isBind ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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

        if(getConnStatus() == 0x02){  //??????????????????????????????
            return;
        }

        if(mWristbandManager.isConnected()){    //???????????????????????????
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

    //????????????
    public void disConnectDevice() {
        Log.e(TAG,"-----??????????????????="+(mWristbandManager.isConnected()));
        if(mWristbandManager.isConnected())
          mWristbandManager.close();
    }

    //??????????????????????????????bind??????
    @SuppressLint("CheckResult")
    public void unBindDevice() {
        if(!mWristbandManager.isConnected())
            return;
        mWristbandManager.userUnBind().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.e(TAG,"------??????="+aBoolean);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG,"------Throwable="+throwable.getMessage());
            }
        });
    }


    //??????????????????
    @SuppressLint("CheckResult")
    private void setConnectListener() {
        mWristbandManager.observerConnectionState().subscribe(new Consumer<ConnectionState>() {

            @Override
            public void accept(ConnectionState connectionState) throws Exception {
                Log.e(TAG, "------????????????=" + connectionState.toString());
                if (connectionState == ConnectionState.CONNECTED) {   //????????????
                    connStauts = 0x01; //????????????
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerConnetSuccessState, 1000);
                    setDeviceListener();
                }

                if (connectionState == ConnectionState.CONNECTING) {  //???????????????
                    connStauts = 0x02; //????????????
                    sendActionBroad(F18_CONNECT_ING,"");
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerConnetting,1000);
                }

                if (connectionState == ConnectionState.DISCONNECTED) {    //????????????
                    sendActionBroad(F18_DIS_CONNECTED_STATUS,"");
                    connStauts = 0x00; //????????????
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
                Log.e(TAG, "-----????????????=" + integer);
                if (integer == WristbandManager.MSG_FIND_PHONE) {     //????????????
                    Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(3 * 1000);
                }

                if(integer == WristbandManager.MSG_HUNG_UP_PHONE){  //?????????????????????
                    Intent intent = new Intent();
                    intent.setAction(BleConstance.W560_DIS_CALL_ACTION);
                    intent.putExtra(BleConstance.W560_PHONE_STATUS,2);
                    mContext.sendBroadcast(intent);

                }

                //??????????????????
                if (integer == WristbandManager.MSG_CAMERA_TAKE_PHOTO) {
                    sendActionBroad(F18_TAKE_PICK_ACTION,"");
                }

                //???????????????????????????
                if (integer == WristbandManager.MSG_MEDIA_PLAY_PAUSE) {

                }

                //???????????????
                if (integer == WristbandManager.MSG_MEDIA_NEXT) {

                }

                //???????????????
                if (integer == WristbandManager.MSG_MEDIA_PREVIOUS) {

                }

                //??????app????????????
                if (integer == WristbandManager.MSG_MEDIA_VOLUME_UP) {

                }

                //??????app????????????
                if (integer == WristbandManager.MSG_MEDIA_VOLUME_DOWN) {

                }


            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    //???????????????????????????Mac??????
    public String getConnectedMac() {
        if (!mWristbandManager.isConnected())
            return null;
        return mWristbandManager.getConnectedAddress();
    }


    //????????????????????????????????????
    @SuppressLint("CheckResult")
    public void getDeviceLastHealthMeasure(){
        if(!mWristbandManager.isConnected())
            return;
        mWristbandManager.requestLatestHealthy().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HealthyDataResult>() {
                    @Override
                    public void accept(HealthyDataResult healthyDataResult) throws Exception {
                        Log.e(TAG,"------????????????????????????="+healthyDataResult.toString()+"\n"+healthyDataResult.getTemperatureBody());
                        if(healthyDataResult == null)
                            return;
                        int htV = healthyDataResult.getHeartRate();
                        if(htV != 0){
                            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureHrSuccess, 100);
                            DeviceDataSave.saveOneceHrData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),htV, System.currentTimeMillis(), String.valueOf(0));
                        }

                        //??????
                        int lBp = healthyDataResult.getDiastolicPressure();
                        int hBp = healthyDataResult.getSystolicPressure();
                        if(hBp !=0 && lBp != 0){
                            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieBloodPressureMessureSuccess,100);
                            DeviceDataSave.saveBloodPressureData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),  hBp, lBp,System.currentTimeMillis(), String.valueOf(0));
                        }


                        //??????
                        if(healthyDataResult.getOxygen() != 0){
                            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureOxyenSuccess,100);
                            DeviceDataSave.saveOxyenModelData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getOxygen(), System.currentTimeMillis(), String.valueOf(0));
                        }


                        //??????
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
     * ????????????????????????
     * WristbandVersion#isHeartRateEnabled() --> WristbandManager#HEALTHY_TYPE_HEART_RATE ??????
     *
     * WristbandVersion#isOxygenEnabled() --> WristbandManager#HEALTHY_TYPE_OXYGEN  ??????
     *
     * WristbandVersion#isBloodPressureEnabled() --> WristbandManager#HEALTHY_TYPE_BLOOD_PRESSURE  ??????
     *
     * WristbandVersion#isRespiratoryRateEnabled() --> WristbandManager#HEALTHY_TYPE_RESPIRATORY_RATE ?????????
     *
     * WristbandVersion#isTemperatureEnabled() --> WristbandManager#HEALTHY_TYPE_TEMPERATURE  ??????
     * @param healthType
     */
    @SuppressLint("CheckResult")
    public void measureHealthData(int healthType,boolean isStart){
        Log.e(TAG,"--hearlty="+healthType);
        if(!mWristbandManager.isConnected())
            return;

        if(measureDisposable != null && !measureDisposable.isDisposed()){
            //????????????
            measureDisposable.dispose();
        }

        if(!isStart)
            return;

        measureDisposable = mWristbandManager.openHealthyRealTimeData(healthType,2)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable disposable) throws Exception {
                    Log.e(TAG,"------????????????--");
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
                stringBuilder.append("??????="+healthyDataResult.getHeartRate()+"\n");
                stringBuilder.append("??????="+healthyDataResult.getTemperatureBody()+"\n");
                stringBuilder.append("??????="+healthyDataResult.getSystolicPressure()+"/"+healthyDataResult.getDiastolicPressure()+"\n");


                Log.e(TAG,"------????????????="+stringBuilder.toString());

                if(healthType == WristbandManager.HEALTHY_TYPE_HEART_RATE){ //??????
                    int htV = healthyDataResult.getHeartRate();
                    if(htV == 0)
                        return;
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureHrSuccess, 100);
                    DeviceDataSave.saveOneceHrData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getHeartRate(), System.currentTimeMillis(), String.valueOf(0));
                }

                //??????
                if(healthType == WristbandManager.HEALTHY_TYPE_BLOOD_PRESSURE){
                    int lBp = healthyDataResult.getDiastolicPressure();
                    int hBp = healthyDataResult.getSystolicPressure();
                    if(hBp == 0 || lBp == 0)
                        return;
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieBloodPressureMessureSuccess,100);
                    DeviceDataSave.saveBloodPressureData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),  hBp, lBp,System.currentTimeMillis(), String.valueOf(0));
                }
                //??????
                if(healthType == WristbandManager.HEALTHY_TYPE_OXYGEN){
                    if(healthyDataResult.getOxygen() == 0)
                        return;
                    closeMeasure();
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureOxyenSuccess,100);
                    DeviceDataSave.saveOxyenModelData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId), healthyDataResult.getOxygen(), System.currentTimeMillis(), String.valueOf(0));
                }
                //??????
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


    //??????????????????
    public void closeMeasure(){
        if(!mWristbandManager.isConnected())
            return;
        if(measureDisposable != null && !measureDisposable.isDisposed()){
            //????????????
            measureDisposable.dispose();
        }
    }


    private int currStep;
    private float currDistance;
    private float currKcal ;

    public String[] getCurrCountStep(){
        return new String[]{currStep+"",currDistance/1000+"",currKcal/1000+""};
    }


    //?????????????????? isUnBind = "0 ??????
    @SuppressLint("CheckResult")
    public void syncDeviceData(String isUnBind) {
        if(!mWristbandManager.isConnected())
            return;
        //?????????????????????????????????
        //????????????????????????????????????
        if(f18SyncStatus == F18SyncStatus.SYNC_ING)
            return;

        sendActionBroad(SYNC_DEVICE_DATA_ING,"start");
        f18SyncStatus = F18SyncStatus.SYNC_ING;
        getDeviceLastHealthMeasure();

        setSyncStatusListener();
        mWristbandManager.syncData()
                .observeOn(Schedulers.io(), true)
                .flatMapCompletable(new Function<SyncDataRaw, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull SyncDataRaw syncDataRaw) throws Exception {

                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_HEART_RATE) {  //????????????
                            List<HeartRateData> datas = SyncDataParser.parserHeartRateData(syncDataRaw.getDatas());
                            analysisHeartData(datas);
                        }

                        //????????????
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_BLOOD_PRESSURE) {
                            List<BloodPressureData> bloodData = SyncDataParser.parserBloodPressureData(syncDataRaw.getDatas());
                            analysisBloodData(bloodData);
                        }

                        //????????????
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_OXYGEN) {
                            List<OxygenData> datas = SyncDataParser.parserOxygenData(syncDataRaw.getDatas());
                            Log.e(TAG,"-----????????????="+new Gson().toJson(datas));
                        }
                        //????????????
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SLEEP) {
                            List<SleepData> sleepDataList = SyncDataParser.parserSleepData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            analysisSleepData(sleepDataList);
                        }

                        //??????????????????
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_TOTAL_DATA) {
                            TodayTotalData todayTotalData = SyncDataParser.parserTotalData(syncDataRaw.getDatas());
                            Log.e(TAG,"-----??????????????????="+todayTotalData.toString());
                            analysisCurrDayStep(todayTotalData);
                        }

                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_STEP){  //????????????
                            List<StepData> datas = SyncDataParser.parserStepData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            Log.e(TAG,"------??????????????????="+new Gson().toJson(datas));
                            analysisDetalStep(datas);
                        }

                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SPORT){    //???????????? ???????????????????????????
                            List<SportData> datas = SyncDataParser.parserSportData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            Log.e(TAG,"------Sport??????="+new Gson().toJson(datas));
                            analysisSportData(datas);
                        }

                        //??????
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_TEMPERATURE) {
                            List<TemperatureData> temperatureDataList = SyncDataParser.parserTemperatureData(syncDataRaw.getDatas());
                            if (temperatureDataList != null && temperatureDataList.size() > 0) {
                                Log.e(TAG,"---????????????="+new Gson().toJson(temperatureDataList));
                            }
                        }

                        return Completable.complete();
                    }
                }).doOnComplete(new Action() {  //??????????????????
            @Override
            public void run() throws Exception {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //??????????????????
                        sendActionBroad(F18_EXERCISE_SYNC_COMPLETE,"");
                        operateF18Sleep(isUnBind);
                    }
                },500);
                Log.e(TAG,"-----??????????????????---");

            }
        })
                .subscribe(new Action() {

            @Override
            public void run() throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                f18SyncStatus = F18SyncStatus.SYNC_COMPLETE;
                Log.e(TAG,"-----Throwable---="+throwable.getMessage());
            }
        });
    }




    //??????????????????????????? key = yyyy-MM-dd HH ????????????
    private Map<String,F18StepBean> tempMap = new HashMap<>();
    private void analysisDetalStep(List<StepData> dataList){
        if(dataList==null || dataList.size() ==0)
            return;
        try {
            tempMap.clear();
            String userId = mUserId;
            String deviceId = mCurrentDevice.getDeviceName();

            for(StepData stepData : dataList){
                //??? yyyy-MM-dd??????
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
            //????????????????????????????????????????????????
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
                String tmpKey = DateUtil.getDateHourStr(tm.getKey());  //????????? HH??????
                if(hour24Map.containsKey(tmpKey)){
                    hour24Map.put(tmpKey,tm.getValue());
                }

            }
            List<String> hourList = new ArrayList<>();
            for(Map.Entry<String,F18StepBean> ltMap : hour24Map.entrySet()){
                hourList.add(ltMap.getKey());
            }

            //??????
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
                        String.valueOf(collStep.getStep()),String.valueOf(collStep.getDistance() * 1000 ),String.valueOf(collStep.getKcal())};
                stepArray.add(stArr);
            }
            Log.e(TAG,"------?????????????????????????????????="+new Gson().toJson(stepArray));

         //   F18DeviceSetAction.deleteF18DetailStepBean(userId,deviceId,DateUtil.getCurrDay());
            new W81DeviceDataAction().saveDeviceStepArrayData("0",mCurrentDevice.getDeviceName(),BaseManager.mUserId,"0",DateUtil.getCurrDay(),new Gson().toJson(stepArray));

            F18DeviceSetAction.updateF18DetailStep(userId,deviceId,DateUtil.getCurrDay());

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //??????????????????
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

            //????????????
            List<Integer> allHeartList = new ArrayList<>();
            //????????????
            List<Integer> stepList = new ArrayList<>();
            //????????????
            List<Integer> distanceList = new ArrayList<>();
            //???????????????
            List<Integer> caloriesList = new ArrayList<>();

            //????????????
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
            w81De.setAvgHr(avgHeart == 0 ? null : avgHeart+"");
            W81DeviceEexerciseAction action = new W81DeviceEexerciseAction();
            action.saveDefExerciseData(w81De);
        }

     //   sendActionBroad(F18_EXERCISE_SYNC_COMPLETE,"");

    }

    //????????????????????????
    @SuppressLint("CheckResult")
    private void setSyncStatusListener() {
        mWristbandManager.observerSyncDataState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {

                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == null)
                            return;

                        Log.e(TAG,"----???????????????="+integer);

                        if (integer < 0) {      //????????????
                            if (integer == WristbandManager.SYNC_STATE_FAILED_DISCONNECTED) {

                            }
                        }
                    }
                });

    }

    //??????????????????
    private void analysisCurrDayStep(TodayTotalData todayTotalData){

        //saveEmptyDetailStep(userId,mCurrentDevice.getDeviceName());
        this.currStep = todayTotalData.getStep();
        this.currKcal = todayTotalData.getCalorie();
        this.currDistance = todayTotalData.getDistance();
        if(f18HomeCountStepListener != null)
            f18HomeCountStepListener.backHomeCountData(todayTotalData.getStep(),todayTotalData.getDistance(),todayTotalData.getCalorie());
    }

    //??????????????????
    private void analysisHeartData(List<HeartRateData> datas) {
        Log.e(TAG,"--------????????????="+new Gson().toJson(datas));
    }

    //??????????????????
    private void analysisBloodData(List<BloodPressureData> bpList) {

    }


    private void syncComplete(String isUnBind){
        f18SyncStatus = F18SyncStatus.SYNC_COMPLETE;
        sendActionBroad(SYNC_DATA_COMPLETE,"");
        //???????????????
        if(!TextUtils.isEmpty(isUnBind) && isUnBind.equals("0")){
            sendActionBroad(SYNC_UNBIND_DATA_COMPLETE,isUnBind);
        }
    }

    private void operateF18Sleep(String isUnBind){
        try {
        ArrayList<F18CommonDbBean> f18CommonDbBeanLt = F18DeviceSetAction.queryListBean(userId,mCurrentDevice.getDeviceName(), "",F18DbType.F18_DEVICE_SLEEP_TYPE);
        if(f18CommonDbBeanLt == null || f18CommonDbBeanLt.isEmpty()){
            syncComplete(isUnBind);
            return;
        }

        Log.e(TAG,"-----??????????????????="+new Gson().toJson(f18CommonDbBeanLt));
        //???????????????????????????
        String lastDay;
        F18CommonDbBean lastTimeB = f18CommonDbBeanLt.get(f18CommonDbBeanLt.size()-1);
        List<SleepData> lastT = new Gson().fromJson(lastTimeB.getTypeDataStr(),new TypeToken<List<SleepData>>(){}.getType());
        if(lastT == null || lastT.isEmpty()){
            syncComplete(isUnBind);
            return;
        }

        lastDay = DateUtil.getFormatTime(lastT.get(lastT.size()-1).getTimeStamp(),"yyyy-MM-dd");
        if(lastT.isEmpty()){
            lastDay = DateUtil.getCurrDay();
        }

        List<SleepItemData> f18SleepList = new ArrayList<>();
        for(F18CommonDbBean f18CommonDbBean : f18CommonDbBeanLt){
            String tmpStr = f18CommonDbBean.getTypeDataStr();
            if(tmpStr == null)
                continue;
            List<SleepData> sleepDataList = new Gson().fromJson(tmpStr,new TypeToken<List<SleepData>>(){}.getType());

            if(sleepDataList != null && !sleepDataList.isEmpty()){
                for(SleepData sleepData : sleepDataList){
                    if(DateUtil.getFormatTime(sleepData.getTimeStamp(),"yyyy-MM-dd").equals(lastDay)){
                        f18SleepList.addAll(sleepData.getItems());
                    }
                }
            }
        }

        if(f18SleepList.isEmpty()){
            syncComplete(isUnBind);
            return;
        }

        analysSleep(f18SleepList,isUnBind,lastDay);
        }catch (Exception e){
            e.printStackTrace();
            syncComplete(isUnBind);
        }
    }


    private void analysSleep(List<SleepItemData> lt,String isUnBind,String day){
        List<SleepItemData> f18SleepList = lt;

        final ArrayList<ArrayList<String>> sleepDetail = new ArrayList<>();

        //??????????????????
        // long countSleep = f18SleepList.get(f18SleepList.size()-1).g.getTimeStamp();

        int len = f18SleepList.size();

        //??????
        int deepSleepTime = 0;
        //??????
        int lightSleepTime = 0;
        //??????
        int soberTime = 0;

        for (int i = 0; i < len; i++) {
            ArrayList<String> itemSleeep = new ArrayList<>();
            SleepItemData sleepData = f18SleepList.get(i);
            int sleepStatus = sleepData.getStatus();
            long startTime = sleepData.getStartTime();
            long endTime = sleepData.getEndTime();
            //??????
            long intervalTime = endTime-startTime;
            //??????
            int intervalMinute = (int) (intervalTime / 1000 /60);

            if(changeSleepStatus(sleepStatus) == 1){   //??????  ??????????????????
                // deepSleepTime +=intervalMinute;
                soberTime += intervalMinute;
            }

            if(changeSleepStatus(sleepStatus) == 2){  //??????
                lightSleepTime +=intervalMinute;
            }

            if(changeSleepStatus(sleepStatus) == 3){   //?????? ??????????????????
                // soberTime += intervalMinute;
                deepSleepTime +=intervalMinute;
            }

            //???????????????1?????????2?????????3??????

            itemSleeep.add(changeSleepStatus(sleepData.getStatus()) + "");
            itemSleeep.add(CommonDateUtil.getTimeFromLong(startTime) + "");
            itemSleeep.add(CommonDateUtil.getTimeFromLong(endTime) + "");
            itemSleeep.add(intervalMinute + "");

            Log.e(TAG,"-----??????item="+new Gson().toJson(itemSleeep));
            sleepDetail.add(itemSleeep);
        }

        Gson gson = new Gson();
        W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
        w81DeviceDataAction.saveW81DeviceSleepData(mCurrentDevice.getDeviceName(), String.valueOf(BaseManager.mUserId),
                "0", day, System.currentTimeMillis(), deepSleepTime+ lightSleepTime, deepSleepTime, lightSleepTime, soberTime, gson.toJson(sleepDetail));


        syncComplete(isUnBind);

        Log.e(TAG,"-----????????????="+deepSleepTime +" "+ lightSleepTime + " "+soberTime+" "+new Gson().toJson(sleepDetail));
    }


    //??????????????????
    private void analysisSleepData(List<SleepData> sleepDataList) {
        if(sleepDataList == null)
            return;
        Log.e(TAG,"-----??????????????????="+new Gson().toJson(sleepDataList));

        F18DeviceSetAction.saveOrUpdateF18DeviceSet(userId,mCurrentDevice.getDeviceName(),mCurrentDevice.getDeviceName(),F18DbType.F18_DEVICE_SLEEP_TYPE,DateUtil.getCurrDay(),new Gson().toJson(sleepDataList));
        //F18DeviceSetAction.saveF18DeviceDetailStep(userId,mCurrentDevice.getDeviceName(),DateUtil.getCurrDay(),System.currentTimeMillis(),);
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

    //???????????????????????????
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


    //??????
    @SuppressLint("CheckResult")
    public void getDeviceBattery() {
        mWristbandManager.requestBattery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BatteryStatus>() {
                    @Override
                    public void accept(BatteryStatus batteryStatus) throws Exception {
                        if(mCurrentDevice != null)
                            mCurrentDevice.setBattery(batteryStatus.getPercentage());
                        Log.e(TAG,"-----????????????="+batteryStatus.getBatteryCount()+" "+batteryStatus.getPercentage());
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


    //??????
    @SuppressLint("CheckResult")
    public void getDeviceBattery(F18GetBatteryListener f18GetBatteryListener) {
        mWristbandManager.requestBattery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BatteryStatus>() {
                    @Override
                    public void accept(BatteryStatus batteryStatus) throws Exception {
                        if(mCurrentDevice != null)
                            mCurrentDevice.setBattery(batteryStatus.getPercentage());
                        Log.e(TAG,"-----????????????="+batteryStatus.getBatteryCount()+" "+batteryStatus.getPercentage());
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
                        //???????????????
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


    //????????????
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


    //??????????????????
    @SuppressLint("CheckResult")
    public void getCommonSet() {
        try {
            if (!mWristbandManager.isConnected())
                return;
            Log.e(TAG,"--------????????????-----");
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

            //???????????????
            WristbandVersion wristbandVersion = wristbandConfig.getWristbandVersion();

            Log.e(TAG,"-----????????????="+wristbandVersion.getRawVersion()+"\n"+wristbandVersion.toString());

            //??????????????????
            boolean isNDT = wristbandVersion.isExtNotDisturb();
            //??????????????????
            boolean isHeart = wristbandVersion.isHeartRateEnabled();

            FunctionConfig functionConfig =wristbandConfig.getFunctionConfig();
            //?????????24????????? true?????????????????????false?????????????????????
            boolean is24Hour = functionConfig.isFlagEnable(FunctionConfig.FLAG_HOUR_STYLE);
            //????????? true????????????false?????????
            boolean isKmUnit = functionConfig.isFlagEnable(FunctionConfig.FLAG_LENGTH_UNIT);
            //???????????? true?????????????????????false????????????
            boolean isTemp = functionConfig.isFlagEnable(FunctionConfig.FLAG_TEMPERATURE_UNIT);
            //????????????
            boolean isWeather = functionConfig.isFlagEnable(FunctionConfig.FLAG_WEATHER_SWITCH);
            //???????????? true????????????false?????????
            boolean isStrength = functionConfig.isFlagEnable(FunctionConfig.FLAG_STRENGTHEN_TEST);


            //????????????
            f18DeviceSetData.setStepGoal(f18DeviceSetData.getStepGoal() == 0 ? 6000 : f18DeviceSetData.getStepGoal());
            //????????????
            f18DeviceSetData.setDistanceGoal(f18DeviceSetData.getDistanceGoal() == 0 ? 1000 : f18DeviceSetData.getDistanceGoal());
            f18DeviceSetData.setKcalGoal(f18DeviceSetData.getKcalGoal() == 0 ? 10 : f18DeviceSetData.getKcalGoal());

            //????????????
            f18DeviceSetData.setDeviceVersionName(wristbandVersion.getApp() );

            //????????????
            f18DeviceSetData.setContinuMonitor(f18DeviceSetData.getContinuMonitor()==null ? "" : f18DeviceSetData.getContinuMonitor());
            //????????????
            f18DeviceSetData.setDrinkAlert(f18DeviceSetData.getDrinkAlert() == null ? noOpen(): f18DeviceSetData.getDrinkAlert());
            //??????
            f18DeviceSetData.setAlarmCount(f18DeviceSetData.getAlarmCount());
            //??????
            f18DeviceSetData.setLongSitStr(f18DeviceSetData.getLongSitStr() == null ? noOpen() : f18DeviceSetData.getLongSitStr());
            //????????????
            f18DeviceSetData.setTurnWrist(f18DeviceSetData.getTurnWrist() == null ? noOpen() : f18DeviceSetData.getTurnWrist());
            f18DeviceSetData.setIs24Heart(isHeart);
            f18DeviceSetData.setTimeStyle(is24Hour ? 0 : 1);
            f18DeviceSetData.setTempStyle(isTemp ? 1 : 0);
            f18DeviceSetData.setIsKmUnit(isKmUnit ? 1 : 0);
            f18DeviceSetData.setCityName(!isWeather ? noOpen() : "?????????");
            f18DeviceSetData.setStrengthMeasure(isStrength);

            //????????????
            f18DeviceSetData.setDrinkAlert(getDrinkData());
            //????????????
            f18DeviceSetData.setLongSitStr(getSedentaryConfig());
            //????????????
            f18DeviceSetData.setTurnWrist(getTurnWristLightingConfig());
            //??????
            f18DeviceSetData.setDNT(getNotDisturbConfig());
            //????????????
            f18DeviceSetData.setContinuMonitor(getHealthyConfig());

            F18DeviceSetAction.saveOrUpdateF18DeviceSet(mUserId, mCurrentDevice.getDeviceName(),  F18DbType.F18_DEVICE_SET_TYPE, new Gson().toJson(f18DeviceSetData));


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private String noOpen(){
        return "?????????";
    }

    //??????????????????
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

    //??????????????????
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


    //????????????
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

    //??????????????????
    public void setUserInfo(boolean sex, int age, float height, float weight) {
        mWristbandManager.setUserInfo(sex, age, height, weight);
    }

    //?????????
    public void setKmUnit(boolean isKm) {
        if (!mWristbandManager.isConnected())
            return;
        FunctionConfig functionConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getFunctionConfig();
        functionConfig.setFlagEnable(FunctionConfig.FLAG_LENGTH_UNIT, isKm);
        mWristbandManager.setFunctionConfig(functionConfig);

    }

    //???????????? ????????????????????????true??????????????????false????????????
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
     * ????????????
     * ????????????https://github.com/htangsmart/FitCloudPro-SDK-Android/blob/master/LanguageType.png
     * 0x00-?????????0x01-???????????????0x02-???????????????0x03-?????? 0x09??????
     */
    public void setDeviceLanguage(byte language) {
        if (mWristbandManager.isConnected())
            mWristbandManager.setLanguage(language);
    }

    //????????????
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

    //??????????????????
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


    //??????????????????
    private String getDrinkData() {
        if (!mWristbandManager.isConnected())
            return noOpen();
        DrinkWaterConfig drinkWaterConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getDrinkWaterConfig();
        //??????
        boolean isOpen = drinkWaterConfig.isEnable();
        //????????????
        int startTime = drinkWaterConfig.getStart();
        //????????????
        int endTime = drinkWaterConfig.getEnd();

        //????????????
        int startHour = startTime / 60;
        //????????????
        int startMinute = startTime % 60;

        //????????????
        int endHour = endTime / 60;
        int endMinute = endTime % 60;

        if(!isOpen){
            return noOpen(); //?????????
        }

        return CommonDateUtil.formatHourMinute(startHour,startMinute)+"~"+CommonDateUtil.formatHourMinute(endHour,endMinute);
    }


    //??????????????????
    public void getDrinkData(CommAlertListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            DrinkWaterConfig drinkWaterConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getDrinkWaterConfig();
            //??????
            boolean isOpen = drinkWaterConfig.isEnable();
            //????????????
            int startTime = drinkWaterConfig.getStart();
            //????????????
            int endTime = drinkWaterConfig.getEnd();

            //????????????
            int startHour = startTime / 60;
            //????????????
            int startMinute = startTime % 60;

            //????????????
            int endHour = endTime / 60;
            int endMinute = endTime % 60;
            //??????
            int intervalTime = drinkWaterConfig.getInterval();

            Log.e(TAG, "------????????????=" + isOpen + " " + startTime + " " + endTime);
            if (commAlertListener != null)
                commAlertListener.backCommAlertData(isOpen, startHour, startMinute, endHour, endMinute, intervalTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //??????????????????
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

    //??????????????????
    public void getSedentaryConfig(F18LongSetListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            SedentaryConfig sedentaryConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getSedentaryConfig();
            //??????
            boolean isOpen = sedentaryConfig.isEnable();
            //?????????
            boolean isDnt = sedentaryConfig.isNotDisturbEnable();
            //????????????
            int startTime = sedentaryConfig.getStart();
            //????????????
            int endTime = sedentaryConfig.getEnd();

            //????????????
            int startHour = startTime / 60;
            //????????????
            int startMinute = startTime % 60;

            //????????????
            int endHour = endTime / 60;
            int endMinute = endTime % 60;
            //??????
            int intervalTime = sedentaryConfig.getInterval();
            Log.e(TAG, "-------????????????=" + startTime + " " + endTime + "" + intervalTime);
            if (commAlertListener != null)
                commAlertListener.backCommF18AlertData(isOpen, isDnt,startHour, startMinute, endHour, endMinute, intervalTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //??????????????????
    private String getSedentaryConfig() {
        try {
            if (!mWristbandManager.isConnected())
                return noOpen();
            SedentaryConfig sedentaryConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getSedentaryConfig();
            //??????
            boolean isOpen = sedentaryConfig.isEnable();
            //????????????
            int startTime = sedentaryConfig.getStart();
            //????????????
            int endTime = sedentaryConfig.getEnd();

            //????????????
            int startHour = startTime / 60;
            //????????????
            int startMinute = startTime % 60;

            //????????????
            int endHour = endTime / 60;
            int endMinute = endTime % 60;
            if(!isOpen){
                return noOpen();
            }

            return CommonDateUtil.formatHourMinute(startHour,startMinute)+"~"+CommonDateUtil.formatHourMinute(endHour,endMinute);
        } catch (Exception e) {
            e.printStackTrace();
            return noOpen();
        }
    }


    //??????????????????
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

    //??????????????????
    private String getTurnWristLightingConfig() {
        if (!mWristbandManager.isConnected())
            return noOpen();
        TurnWristLightingConfig turnWristLightingConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getTurnWristLightingConfig();
        //??????
        boolean isOpen = turnWristLightingConfig.isEnable();
        //????????????
        int startTime = turnWristLightingConfig.getStart();
        //????????????
        int endTime = turnWristLightingConfig.getEnd();

        //????????????
        int startHour = startTime / 60;
        //????????????
        int startMinute = startTime % 60;

        //????????????
        int endHour = endTime / 60;
        int endMinute = endTime % 60;
        if(!isOpen){
            return noOpen();
        }
        return  CommonDateUtil.formatHourMinute(startHour,startMinute)+"~"+CommonDateUtil.formatHourMinute(endHour,endMinute);
    }


    //??????????????????
    public void getTurnWristLightingConfig(CommAlertListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            TurnWristLightingConfig turnWristLightingConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getTurnWristLightingConfig();
            if (turnWristLightingConfig == null)
                return;
            //??????
            boolean isOpen = turnWristLightingConfig.isEnable();
            //????????????
            int startTime = turnWristLightingConfig.getStart();
            //????????????
            int endTime = turnWristLightingConfig.getEnd();

            //????????????
            int startHour = startTime / 60;
            //????????????
            int startMinute = startTime % 60;

            //????????????
            int endHour = endTime / 60;
            int endMinute = endTime % 60;
            if (commAlertListener != null)
                commAlertListener.backCommAlertData(isOpen, startHour, startMinute, endHour, endMinute, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //??????????????????
    @SuppressLint("CheckResult")
    public void setTurnWristLightingConfig(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            TurnWristLightingConfig turnWristLightingConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getTurnWristLightingConfig();
            turnWristLightingConfig.setEnable(isOpen);
            //????????????
            turnWristLightingConfig.setStart(startHour * 60 + startMinute);
            //????????????
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

    //??????????????????
    public String getHealthyConfig() {
        if (!mWristbandManager.isConnected())
            return noOpen();
        HealthyConfig healthyConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getHealthyConfig();
        //??????
        boolean isOpen = healthyConfig.isEnable();
        //????????????
        int startTime = healthyConfig.getStart();
        //????????????
        int endTime = healthyConfig.getEnd();

        //????????????
        int startHour = startTime / 60;
        //????????????
        int startMinute = startTime % 60;

        //????????????
        int endHour = endTime / 60;
        int endMinute = endTime % 60;
        if(!isOpen){
            return  noOpen();
        }
        return CommonDateUtil.formatHourMinute(startHour,startMinute)+"~"+CommonDateUtil.formatHourMinute(endHour,endMinute);
    }


    //??????????????????
    public void getHealthyConfig(CommAlertListener commAlertListener) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            HealthyConfig healthyConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getHealthyConfig();
            //??????
            boolean isOpen = healthyConfig.isEnable();
            //????????????
            int startTime = healthyConfig.getStart();
            //????????????
            int endTime = healthyConfig.getEnd();

            //????????????
            int startHour = startTime / 60;
            //????????????
            int startMinute = startTime % 60;

            //????????????
            int endHour = endTime / 60;
            int endMinute = endTime % 60;
            if (commAlertListener != null)
                commAlertListener.backCommAlertData(isOpen, startHour, startMinute, endHour, endMinute, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //??????????????????
    @SuppressLint("CheckResult")
    public void setHealthyConfig(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            HealthyConfig healthyConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getHealthyConfig();
            //??????
            healthyConfig.setEnable(isOpen);
            //????????????
            healthyConfig.setStart(startHour * 60 + startMinute);
            //????????????
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

    //??????????????????
    private String getNotDisturbConfig() {
        try {
            if (!mWristbandManager.isConnected())
                return noOpen();
            NotDisturbConfig notDisturbConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotDisturbConfig();
            boolean isOpen = notDisturbConfig.isEnablePeriodTime();
            //????????????
            int startTime = notDisturbConfig.getStart();
            //????????????
            int endTime = notDisturbConfig.getEnd();

            //????????????
            int startHour = startTime / 60;
            //????????????
            int startMinute = startTime % 60;

            //????????????
            int endHour = endTime / 60;
            int endMinute = endTime % 60;

            if(!isOpen){
                return noOpen();
            }

            return  CommonDateUtil.formatHourMinute(startHour,startMinute)+"~"+CommonDateUtil.formatHourMinute(endHour,endMinute);
        } catch (Exception e) {
            e.printStackTrace();
            return noOpen();
        }
    }



    //??????????????????
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
                            //????????????
                            boolean isAllDayOpen = notDisturbConfig.isEnableAllDay();
                            //????????????
                            int startTime = notDisturbConfig.getStart();
                            //????????????
                            int endTime = notDisturbConfig.getEnd();

                            //????????????
                            int startHour = startTime / 60;
                            //????????????
                            int startMinute = startTime % 60;

                            //????????????
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


    //??????????????????
    @SuppressLint("CheckResult")
    public void setNotDisturbConfig(boolean isOpen, boolean isAllDay,int startHour, int startMinute, int endHour, int endMinute) {
        try {
            if (!mWristbandManager.isConnected())
                return;
            NotDisturbConfig notDisturbConfig = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getNotDisturbConfig();
            notDisturbConfig.setEnablePeriodTime(isOpen);
            //????????????
            notDisturbConfig.setEnableAllDay(isAllDay);
            //????????????
            notDisturbConfig.setStart(startHour * 60 + startMinute);
            //????????????
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
     * ????????????
     * ????????????
     * 0x00 ??????
     * 0x01 ??????
     * 0x02 ??????
     * 0x03 ??????
     * 0x04 ??????
     * 0x05 ?????????????????????????????????
     * 0x06 ??????
     * 0x07 ????????????????????????
     * 0x08 ??????????????????
     * 0x09 ??????
     * 0x0a ???????????????
     * 0x0b ??????????????????
     * 0x0c ????????????
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






    //??????app????????????
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

    //????????????????????????
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

    //????????????
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

    //????????????
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


    //????????????
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

    //???????????????
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


    //???????????????
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





    //????????????
    public void setF18DialToDevice(File dialFile, Byte bt, OnF18DialStatusListener onF18DialStatusListener){
        setF18SyncStatus(F18SyncStatus.SYNC_DIAL_ING);
        DfuManager dfuManager = new DfuManager(mContext);
        dfuManager.setDfuCallback(new DfuCallback() {
            @Override
            public void onError(int errorType, int errorCode) {
                Log.e(TAG,"--????????????----onError-------="+errorType +" "+errorCode);
                if(onF18DialStatusListener != null)
                    onF18DialStatusListener.onError(errorType,errorCode);
                setF18SyncStatus(F18SyncStatus.SYNC_DIAL_FAIL);
            }

            @Override
            public void onStateChanged(int state, boolean cancelable) {
                Log.e(TAG,"--????????????-----onStateChanged------="+state +" "+cancelable);
                if(onF18DialStatusListener != null)
                    onF18DialStatusListener.onStateChanged(state,cancelable);
            }

            @Override
            public void onProgressChanged(int progress) {
                Log.e(TAG,"--????????????------onProgressChanged-----="+progress);
                if(onF18DialStatusListener != null)
                    onF18DialStatusListener.onProgressChanged(progress);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG,"--????????????-----onSuccess------");
                if(onF18DialStatusListener != null)
                    onF18DialStatusListener.onSuccess();
                setF18SyncStatus(F18SyncStatus.SYNC_DIAL_COMPLETE);
            }
        });
        dfuManager.init();
        if(onF18DialStatusListener != null)
            onF18DialStatusListener.startDial();
        dfuManager.upgradeDial(dialFile.getAbsolutePath(),bt);

    }


    //??????????????????????????????????????????
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


    //??????OTA??????
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


    //????????????
    public void startOta(String dfuPath, OnF18DialStatusListener onF18DialStatusListener){
        if(!mWristbandManager.isConnected())
            return;

        WristbandVersion wristbandVersion = Objects.requireNonNull(mWristbandManager.getWristbandConfig()).getWristbandVersion();
        boolean isUpdate = wristbandVersion.isUpgradeFirmwareSilent();

        DfuManager dfuManager = new DfuManager(mContext);
        dfuManager.setDfuCallback(new DfuCallback() {
            @Override
            public void onError(int i, int i1) {
                Log.e(TAG,"--????????????-----onError------="+i +" "+i1);
                if(onF18DialStatusListener !=null)
                    onF18DialStatusListener.onError(i,i);
            }

            @Override
            public void onStateChanged(int i, boolean b) {
                Log.e(TAG,"--????????????-----onStateChanged------="+ i +" "+b);
                if(onF18DialStatusListener !=null)
                    onF18DialStatusListener.onStateChanged(i,b);
            }

            @Override
            public void onProgressChanged(int i) {
                Log.e(TAG,"--????????????-----onProgressChanged------="+i);
                if(onF18DialStatusListener !=null)
                    onF18DialStatusListener.onProgressChanged(i);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG,"--????????????-----onSuccess------");
                if(onF18DialStatusListener !=null)
                    onF18DialStatusListener.onSuccess();
            }
        });


        dfuManager.init();
        dfuManager.upgradeFirmware(dfuPath);
    }


    //????????????
    private void sendActionBroad(String action,String params){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(ACTION_KEY_PARAMS,params);
        mContext.sendBroadcast(intent);
    }

}


