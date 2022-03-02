package com.isport.brandapp.device.f18;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isport.blelibrary.db.action.W81Device.W81DeviceDataAction;
import com.isport.blelibrary.db.action.f18.F18DeviceSetAction;
import com.isport.blelibrary.db.parse.DeviceDataSave;
import com.isport.blelibrary.db.table.DeviceTempUnitlTable;
import com.isport.blelibrary.db.table.F18StepHourMap;
import com.isport.blelibrary.db.table.f18.F18DetailStepBean;
import com.isport.blelibrary.db.table.f18.F18StepBean;
import com.isport.blelibrary.db.table.w811w814.W81DeviceDetailData;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.StepArithmeticUtil;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.device.UpdateSuccessBean;
import com.isport.brandapp.device.W81Device.IW81DeviceDataModel;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.bracelet.braceletModel.IW311SettingModel;
import com.isport.brandapp.device.bracelet.braceletModel.W311ModelSettingImpl;
import com.isport.brandapp.device.watch.bean.WatchInsertBean;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.home.presenter.W81DataPresenter;
import com.isport.brandapp.repository.BPRepository;
import com.isport.brandapp.repository.ExerciseRepository;
import com.isport.brandapp.repository.OnceHrRepository;
import com.isport.brandapp.repository.OxygenRepository;
import com.isport.brandapp.repository.TempRepository;
import com.isport.brandapp.repository.W81DeviceDataRepository;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.bean.TempInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Admin
 * Date 2022/1/20
 */
public class F18HomePresenter extends BasePresenter<F18HomeView> {

    private static final String TAG = "F18HomePresenter";

    private F18HomeView view;

    private IW81DeviceDataModel iw81DeviceDataModel;
    private W81DataPresenter w81DataPresenter;

    public F18HomePresenter(F18HomeView f18HomeView) {
        this.view = f18HomeView;
        iw81DeviceDataModel = new W81DeviceDataModelImp();
        if(w81DataPresenter == null)
            w81DataPresenter = new W81DataPresenter(f18HomeView);
    }


    //获取最近一次的计步汇总数据
    public void getDeviceStepLastTwoData(int currentType) {
        Observable.create(new ObservableOnSubscribe<WatchSportMainData>() {
            @Override
            public void subscribe(ObservableEmitter<WatchSportMainData> emitter) throws Exception {
                WatchSportMainData mWatchSportMainData = null;
                Logger.myLog(TAG,"getDeviceStepLastTwoData currentType:" + currentType + "userId:" + TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()) + " ,AppConfiguration.braceletID:" + AppConfiguration.braceletID + ",AppConfiguration.isConnected：" + AppConfiguration.isConnected);

                mWatchSportMainData = iw81DeviceDataModel.getLastStepData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, DeviceTypeUtil.isContainW81());

                if (mWatchSportMainData == null) {
                    mWatchSportMainData = new WatchSportMainData();
                }
                emitter.onNext(mWatchSportMainData);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchSportMainData>(BaseApp.getApp(), false) {
            @Override
            public void onNext(WatchSportMainData watchSportMainData) {
                if (view != null) {
                    view.successGetMainLastStepDataForDB(watchSportMainData);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });
    }

    //获取最近一次的单次测量心率
    public void getDevcieOnceHrData() {
        Observable.create(new ObservableOnSubscribe<OnceHrInfo>() {
            @Override
            public void subscribe(ObservableEmitter<OnceHrInfo> emitter) throws Exception {
                OnceHrInfo info = iw81DeviceDataModel.getOneceHrLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

                Logger.myLog(TAG,"getDevcieOnceHrData:" + info);

                emitter.onNext(info);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<OnceHrInfo>(BaseApp.getApp(), false) {
            @Override
            public void onNext(OnceHrInfo oxyInfo) {
                if (view != null) {
                    Logger.myLog(TAG,"getDevcieOnceHrData: successGetMainLastOxgenData" + oxyInfo);
                    view.successGetMainLastOnceHrData(oxyInfo);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });

    }


    public void getDeviceLastNetHeart(){
        OnceHrRepository.requstOnceHrData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<List<OnceHrInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<OnceHrInfo> infos) {
                if (isViewAttached() && infos != null && infos.size()>0) {
                    mActView.get().successGetMainLastOnceHrData(infos.get(infos.size()-1));
                }
                Logger.myLog("getOxyenNumData:" + infos);
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });

    }


    //获取最新一次的血压数据
    public void getDeviceBloodPressure() {
        Observable.create(new ObservableOnSubscribe<BPInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BPInfo> emitter) throws Exception {
                BPInfo info = iw81DeviceDataModel.getBloodPressureLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                emitter.onNext(info);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<BPInfo>(BaseApp.getApp(), false) {
            @Override
            public void onNext(BPInfo oxyInfo) {
                if (view != null) {
                    view.successGetMainLastBloodPresuure(oxyInfo);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });

    }

    //获取最近一次的血氧数据
    public void getDevcieOxygenData() {
        Observable.create(new ObservableOnSubscribe<OxyInfo>() {
            @Override
            public void subscribe(ObservableEmitter<OxyInfo> emitter) throws Exception {
                OxyInfo info = iw81DeviceDataModel.getOxygenLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

                Logger.myLog(TAG,"----getOxygenLastData:=" + info);

                emitter.onNext(info);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<OxyInfo>(BaseApp.getApp(), false) {
            @Override
            public void onNext(OxyInfo oxyInfo) {
                if (view != null) {
                    Logger.myLog(TAG,"最近一次血氧: successGetMainLastOxgenData=" + oxyInfo.toString());
                    view.successGetMainLastOxgenData(oxyInfo);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });

    }

    //获取体温
    public void getTempData() {
        Observable.create(new ObservableOnSubscribe<TempInfo>() {
            @Override
            public void subscribe(ObservableEmitter<TempInfo> emitter) throws Exception {
                TempInfo info = iw81DeviceDataModel.getTempInfoeLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                IW311SettingModel w311ModelSetting = new W311ModelSettingImpl();
                DeviceTempUnitlTable table = w311ModelSetting.getTempUtil(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
                if (table != null) {
                    info.setTempUnitl(table.getTempUnitl());
                } else {
                    info.setTempUnitl("0");
                }

                emitter.onNext(info);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<TempInfo>(BaseApp.getApp(), false) {
            @Override
            public void onNext(TempInfo tempInfo) {
                if (view != null) {
                    view.successGetMainLastTempValue(tempInfo);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });

    }

    //获取最新保存的睡眠
    public void getWatchSleepLastData() {
        Observable.create(new ObservableOnSubscribe<WatchSleepDayData>() {
            @Override
            public void subscribe(ObservableEmitter<WatchSleepDayData> emitter) throws Exception {
                WatchSleepDayData watchSleepDayData = null;
                watchSleepDayData = iw81DeviceDataModel.getLastSleepData( TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),AppConfiguration.braceletID, true);
                if (watchSleepDayData == null) {
                    watchSleepDayData = new WatchSleepDayData();
                }
                emitter.onNext(watchSleepDayData);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchSleepDayData>(BaseApp.getApp(), false) {
            @Override
            public void onNext(WatchSleepDayData watchSleepDayData) {
                Logger.myLog(TAG,"------watchSleepDayData="+watchSleepDayData.toString());
                if (view != null) {
                    view.successGetMainLastSleepValue(watchSleepDayData);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });
    }

    //上传体温数据
    public synchronized void updateTempData(String deviceId, String userId) {
        Logger.myLog(TAG,"updateTempData success:no deviceId" + deviceId + "userId：" + userId);
        TempRepository.requstUpgradeTempData(deviceId, userId).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(BaseApp.getApp()) {
            @Override
            protected void hideDialog() {
            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

            @Override
            public void onNext(Integer integer) {
                if (integer != -1) {
                    ThreadPoolUtils.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            IW81DeviceDataModel iw81DeviceDataModel = new W81DeviceDataModelImp();
                            iw81DeviceDataModel.updateWriId(deviceId, userId, 3);
                        }
                    });

                }
            }
        });
    }


    //上传血氧数据
    public void uploadOxyData(String deviceId,String userId){
        OxygenRepository.requstUpgradeData(deviceId, userId).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(BaseApp.getApp(), false) {

            @Override
            public void onNext(Integer updateSuccessBean) {
                if (updateSuccessBean != -1) {
                    ThreadPoolUtils.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            IW81DeviceDataModel iw81DeviceDataModel = new W81DeviceDataModelImp();
                            iw81DeviceDataModel.updateWriId(deviceId, userId, 1);
                        }
                    });

                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {
            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

                Logger.myLog(e.message);

            }
        });
    }

    //上传血压数据
    public void uploadF18BloodData(String deviceId,String userId){
        BPRepository.requstUpgradeData(deviceId, userId).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(BaseApp.getApp(), false) {

            @Override
            public void onNext(Integer updateSuccessBean) {
                Logger.myLog("updateBloodPressureWridId upgradeBPData" + updateSuccessBean);
                if (updateSuccessBean != -1) {
                    ThreadPoolUtils.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            IW81DeviceDataModel iw81DeviceDataModel = new W81DeviceDataModelImp();
                            iw81DeviceDataModel.updateWriId(deviceId, userId, 0);
                        }
                    });
                }
            }

            @Override
            protected void hideDialog() {
            }

            @Override
            protected void showDialog() {
            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                Logger.myLog("upgradeBPData:" + e.toString());
            }
        });

    }


    //上传一次心率数据
    public synchronized void upgradeOnceHrData(String deviceId, String userId) {
        OnceHrRepository.requstUpgradeData(deviceId, userId).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Integer updateSuccessBean) {
                if (updateSuccessBean != -1) {
                    ThreadPoolUtils.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            IW81DeviceDataModel iw81DeviceDataModel = new W81DeviceDataModelImp();
                            iw81DeviceDataModel.updateWriId(deviceId, userId, 2);
                        }
                    });

                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

                Logger.myLog("upgradeOnceHrData:" + e.toString());

            }
        });

    }


    //上传锻炼数据
    public synchronized void upgradeExeciseData(int devicetype, String deviceId, String userId) {
        ExerciseRepository.requstUpgradeExerciseData(devicetype, deviceId, userId).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Integer updateSuccessBean) {
                //如果成功需要去更新所有的
                if (updateSuccessBean != -1) {
                    ThreadPoolUtils.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            IW81DeviceDataModel iw81DeviceDataModel = new W81DeviceDataModelImp();
                            iw81DeviceDataModel.updateWriId(deviceId, userId, 2);
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.update_exercise));
                        }
                    });

                }

            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });

    }

    //获取当天的锻炼数据
    public void getExerciseTodaySum(int deviceType) {
        ExerciseRepository.requestTodayExerciseData(deviceType, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), System.currentTimeMillis(), AppConfiguration.braceletID).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(BaseApp.getApp(), false) {

            @Override
            public void onNext(Integer infos) {
                if (view != null) {
                    view.successGetMainTotalAllTime(infos);
                }
                Logger.myLog(TAG,"getExerciseTodaySum:" + infos);
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });
    }


    //上传数据
    public synchronized void getNoUpgradeW81DevcieDetailData(String userId, String deviceId, String defWriId, boolean isToday) {

        List<WatchInsertBean> upgradeList = iw81DeviceDataModel.getAllNoUpgradeW81DeviceDetailData(deviceId, userId, defWriId, isToday);
        Logger.myLog("getNoUpgradeW81DevcieDetailData:" + userId + ",deviceId:" + deviceId + ",defWriId:" + defWriId + upgradeList);
        WatchInsertBean watchInsertBean;
        for (int i = 0; i < upgradeList.size(); i++) {
            Constants.isSyncData = true;
            watchInsertBean = upgradeList.get(i);
            WatchInsertBean finalWatchInsertBean = watchInsertBean;
            W81DeviceDataRepository.requstUpgradeW81Data(watchInsertBean).as(view.bindAutoDispose()).subscribe(new BaseObserver<UpdateSuccessBean>(BaseApp.getApp(), false) {
                @Override
                protected void hideDialog() {

                }

                @Override
                protected void showDialog() {

                }

                @Override
                public void onError(ExceptionHandle.ResponeThrowable e) {
                    Constants.isSyncData = false;
                }

                @Override
                public void onNext(UpdateSuccessBean updateSuccessBean) {
                    //需要去更新 id;
                    Constants.isSyncData = false;
                    Logger.myLog("UpdateSuccessBean:" + userId + ",deviceId:" + deviceId + ",updateSuccessBean.getPublicId():" + updateSuccessBean.getPublicId() + "finalWatchInsertBean.getDateStr()" + finalWatchInsertBean.getDateStr());
                    iw81DeviceDataModel.updateWriId(finalWatchInsertBean.getDeviceId(), finalWatchInsertBean.getUserId(), finalWatchInsertBean.getDateStr(), String.valueOf(updateSuccessBean.getPublicId()));
                }
            });
        }

    }

    //获取血压，用于首页展示，先从后台获取再存到数据库，然后再展示
    public void getBloodPressure() {
        BPRepository.requstNumBPData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<BPInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<BPInfo> infos) {
                if (infos != null && infos.size() > 0) {
                    for (int i = 0; i < infos.size(); i++) {
                        BPInfo bpInfo = infos.get(i);
                        DeviceDataSave.saveBloodPressureData(bpInfo.getDeviceId(), bpInfo.getUserId(), bpInfo.getSpValue(), bpInfo.getDpValue(), bpInfo.getTimestamp(), bpInfo.getWristbandBloodPressureId());
                    }
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_bloodpre));

                }

                Logger.myLog("getBpNumData:" + infos);
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });

    }

    //获取血氧，先获取再村本地，再展示
    public void getNumOxyGen() {
        OxygenRepository.requstNumOxygenData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<OxyInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<OxyInfo> infos) {

                if (infos != null && infos.size() > 0) {
                    for (int i = 0; i < infos.size(); i++) {
                        OxyInfo oxyInfo = infos.get(i);
                        DeviceDataSave.saveOxyenModelData(oxyInfo.getDeviceId(), oxyInfo.getUserId(), oxyInfo.getBoValue(), oxyInfo.getTimestamp(), oxyInfo.getWristbandBloodOxygenId());
                    }

                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_oxygen));
                }

               /* if (view != null) {
                    mActView.get().getOxyHistoryDataSuccess(infos);
                }
                Logger.myLog("getOxyenNumData:" + infos);*/
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });

    }



    public void getNumNetOnceHr() {
        OnceHrRepository.requstNumOnceHrData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<OnceHrInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<OnceHrInfo> infos) {
                if (infos != null && infos.size() > 0) {
                    for (int i = 0; i < infos.size(); i++) {
                        OnceHrInfo onceHrInfo = infos.get(i);
                        DeviceDataSave.saveOneceHrData(onceHrInfo.getDeviceId(), onceHrInfo.getUserId(), Integer.valueOf(onceHrInfo.getHeartValue()), onceHrInfo.getTimestamp(), String.valueOf(0));
                    }

                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_oncehr));
                }

               /* if (view != null) {
                    mActView.get().getOxyHistoryDataSuccess(infos);
                }
                Logger.myLog("getOxyenNumData:" + infos);*/
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });

    }


    public void getNetTempData() {
        TempRepository.requstNumTempData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<TempInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<TempInfo> infos) {
                if (infos != null && infos.size() > 0) {
                    for (int i = 0; i < infos.size(); i++) {
                        TempInfo tempInfo = infos.get(i);
                        DeviceDataSave.saveTempData(tempInfo.getDeviceId(), tempInfo.getUserId(), Float.valueOf(tempInfo.getCentigrade()), tempInfo.getTimestamp(), String.valueOf(0));
                    }
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_temp));

                }
                Logger.myLog(TAG,"getTempData:" + new Gson().toJson(infos));
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });

    }


    public void getAllDeviceDetailData(){
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);


        Calendar monthC = Calendar.getInstance();
        int currMonth = monthC.get(Calendar.MONTH);

        Log.e(TAG,"------月="+currMonth);
        w81DataPresenter.getW81MonthStep(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "0", monthC.getTimeInMillis());
        Calendar yesMonthC = Calendar.getInstance();



        //0 是步数 1 心率 2 睡眠
        w81DataPresenter.getW81MonthStep(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "0",instance.getTimeInMillis());
        w81DataPresenter.getW81MonthHr(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "1", instance.getTimeInMillis());
        w81DataPresenter.getW81MothSleep(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "2", instance.getTimeInMillis());
    }



    //处理昨天的详细计步数据
    private Map<String, F18StepBean> tempMap = new HashMap<>();
    public void dealWithYesDayStep(String userId,String deviceId){
        try {
            tempMap.clear();
            //先查询本地的数据库，是否有保存昨天的数据
            List<F18DetailStepBean> saveList = F18DeviceSetAction.getF18DetailList(userId,deviceId, DateUtil.getYestDay());
            Log.e(TAG,"-----是否有昨天的数据="+new Gson().toJson(saveList));
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
            Log.e(TAG,"-------昨天整理完成的数据="+new Gson().toJson(stepArray));
            //查询下本地的数据库是否存在
            W81DeviceDetailData yesW81 = new W81DeviceDataAction().getW81DeviceDetialData(deviceId,userId,DateUtil.getYestDay());
            if(yesW81 != null){
                String stepArr = yesW81.getStepArray();
                if(stepArr != null && !stepArr.equals("[]")){
                    List<String[]> yesStepList = new Gson().fromJson(stepArr,new TypeToken<List<String[]>>(){}.getType());
                    Log.e(TAG,"------本地的数据库="+new Gson().toJson(yesStepList));
                    //本地数据库有，与原始整理的数据合并
                    if(yesStepList.size() == stepArray.size()){
                        List<String[]> yesResultList = new ArrayList<>();
                        for(int i = 0;i<yesStepList.size();i++){
                            String[] sourceItem = yesStepList.get(i);
                            String[] localItem = stepArray.get(i);

                            //合并
                            String[] resultStr = new String[4];
                            //小时
                            resultStr[0] = sourceItem[0];
                            //步数
                            resultStr[1] = (Integer.parseInt(sourceItem[1])+Integer.parseInt(localItem[1]))+"";
                            //距离
                            resultStr[2] = StepArithmeticUtil.addNumber(Float.parseFloat(sourceItem[2]),Float.parseFloat(localItem[2]))+"";
                            //卡路里
                            resultStr[3] = StepArithmeticUtil.addNumber(Float.parseFloat(sourceItem[3]),Float.parseFloat(localItem[3]))+"";
                            yesResultList.add(resultStr);
                        }

                        Log.e(TAG,"-----已经组装完成的数据="+new Gson().toJson(yesResultList));
                        if(yesResultList.isEmpty())
                            return;
                        new W81DeviceDataAction().saveDeviceStepArrayData(deviceId, userId,null,DateUtil.getYestDay(),new Gson().toJson(yesResultList));

                        F18DeviceSetAction.deleteF18DetailStepBean(userId,deviceId,DateUtil.getYestDay());

                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
