package com.isport.brandapp.device.f18;

import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.device.W81Device.IW81DeviceDataModel;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.repository.BPRepository;
import com.isport.brandapp.repository.OxygenRepository;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.bean.TempInfo;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
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

    IW81DeviceDataModel iw81DeviceDataModel;

    public F18HomePresenter(F18HomeView f18HomeView) {
        this.view = f18HomeView;
        iw81DeviceDataModel = new W81DeviceDataModelImp();
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
//                DeviceTempUnitlTable table = w311ModelSetting.getTempUtil(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
//                if (table != null) {
//                    info.setTempUnitl(table.getTempUnitl());
//                } else {
//                    info.setTempUnitl("0");
//                }
                info.setTempUnitl("1");

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
                watchSleepDayData = iw81DeviceDataModel.getLastSleepData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), false);
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



}
