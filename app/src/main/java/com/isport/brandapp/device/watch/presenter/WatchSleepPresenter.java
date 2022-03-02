package com.isport.brandapp.device.watch.presenter;

import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.device.W81Device.IW81DeviceDataModel;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.bracelet.braceletModel.IW311DataModel;
import com.isport.brandapp.device.bracelet.braceletModel.W311DataModelImpl;
import com.isport.brandapp.device.watch.view.WatchSleepView;
import com.isport.brandapp.device.watch.watchModel.W516Model;
import com.isport.brandapp.util.DeviceTypeUtil;

import java.util.ArrayList;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @创建者 bear
 * @创建时间 2019/3/9 16:21
 * @描述
 */
public class WatchSleepPresenter extends BasePresenter<WatchSleepView> {

    private static final String TAG = "WatchSleepPresenter";

    private final W516Model iw516Model;
    private final IW311DataModel iw311DataModel;
    private final WatchSleepView view;
    private final IW81DeviceDataModel iw81DeviceDataModel;

    public WatchSleepPresenter(WatchSleepView view) {
        this.view = view;
        iw516Model = new W516Model();
        iw311DataModel = new W311DataModelImpl();
        iw81DeviceDataModel = new W81DeviceDataModelImp();

    }

    public void getWatchDayData(String dateStr, String deviceId, int deviceType, String userId) {
        Logger.myLog("getWatchDayData");
        Observable.create(new ObservableOnSubscribe<WatchSleepDayData>() {
            @Override
            public void subscribe(ObservableEmitter<WatchSleepDayData> emitter) throws Exception {
                WatchSleepDayData watchSleepDayData = null;
                if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
                    watchSleepDayData = iw311DataModel.getWatchSleepDayData(dateStr, deviceId);

                } else if (DeviceTypeUtil.isContainWatch(deviceType)) {
                    watchSleepDayData = iw516Model.getWatchSleepDayData(dateStr, deviceId);

                } else if (DeviceTypeUtil.isContainW81(deviceType) || DeviceTypeUtil.isContainF18(deviceId)) {
                    watchSleepDayData = iw81DeviceDataModel.getStrDateSleepData(userId, deviceId, dateStr);
                }
                if (watchSleepDayData != null) {
                    emitter.onNext(watchSleepDayData);
                } else {
                    emitter.onNext(new WatchSleepDayData());
                }
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchSleepDayData>(BaseApp.getApp(), false) {
            @Override
            public void onNext(WatchSleepDayData watchSleepDayData) {
                if (view != null) {
                    view.successDayDate(watchSleepDayData);
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

    /**
     * 获取最近一次有数据的WatchSleepDayData
     */
    public void getWatchLastData(String deviceId, String userId, Integer deviceType) {
        Logger.myLog(TAG,"getWatchLastData-==="+deviceId+" "+userId+" "+deviceType);
        Observable.create(new ObservableOnSubscribe<WatchSleepDayData>() {
            @Override
            public void subscribe(ObservableEmitter<WatchSleepDayData> emitter) throws Exception {
                WatchSleepDayData watchSleepDayData = null;
                if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
                    watchSleepDayData = iw311DataModel.getWatchSleepDayLastData(deviceId);

                } else if (DeviceTypeUtil.isContainWatch(deviceType)) {
                    watchSleepDayData = iw516Model.getWatchSleepDayLastData(deviceId);

                } else if (DeviceTypeUtil.isContainW81(deviceType) || DeviceTypeUtil.isContainF18(deviceId)) {
                    watchSleepDayData = iw81DeviceDataModel.getLastSleepData(userId, deviceId, false);
                }
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
                    view.successDayDate(watchSleepDayData);
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

    /**
     * 获取有数据的天的list
     *
     * @param strYearAndMonth
     */
    public void getMonthDataStrDate(String strYearAndMonth, int deviceType, String deviceId, String userId) {
        Logger.myLog(TAG,"---查询这个月的数据 == " + strYearAndMonth);
        Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> emitter) throws Exception {
                ArrayList<String> strDates = null;
                if (DeviceTypeUtil.isContainWatch(deviceType)) {
                    strDates = iw516Model.getSleepMonthData(deviceId, strYearAndMonth);
                } else if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
                    strDates = iw311DataModel.getSleepMonthData(strYearAndMonth, deviceId);
                } else if (DeviceTypeUtil.isContainW81(deviceType) || DeviceTypeUtil.isContainF18(deviceType)) {
                    strDates = iw81DeviceDataModel.getSleepMonthData(strYearAndMonth, userId, deviceId);
                }

                Logger.myLog(TAG,"----getMonthDataStrDate:"+strDates);
                if (strDates == null) {
                    strDates = new ArrayList<>();
                }
                emitter.onNext(strDates);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<ArrayList<String>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(ArrayList<String> list) {
                if (view != null) {
                    view.successMonthDate(list);
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

                if (view != null) {
                    view.successMonthDate(new ArrayList<>());
                }
            }

        });
    }
}
