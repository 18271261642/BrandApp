package com.isport.brandapp.home.presenter;

import android.text.TextUtils;

import com.isport.blelibrary.db.action.W81Device.W81DeviceDataAction;
import com.isport.blelibrary.db.action.s002.S002_DetailDataModelAction;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.device.UpdateSuccessBean;
import com.isport.brandapp.device.W81Device.IW81DeviceDataModel;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.watch.bean.WatchHistoryNBean;
import com.isport.brandapp.device.watch.bean.WatchHistoryNList;
import com.isport.brandapp.device.watch.bean.WatchInsertBean;
import com.isport.brandapp.device.watch.view.WatchSleepView;
import com.isport.brandapp.repository.BPRepository;
import com.isport.brandapp.repository.OnceHrRepository;
import com.isport.brandapp.repository.OxygenRepository;
import com.isport.brandapp.repository.S002DeviceDataRepository;
import com.isport.brandapp.repository.TempRepository;
import com.isport.brandapp.repository.W81DeviceDataRepository;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.ExerciseInfo;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.bean.TempInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BaseView;

public class W81DataPresenter {


    BaseView view;
    IW81DeviceDataModel iw81DeviceDataModel;
    WatchSleepView watchSleepView;


    public W81DataPresenter(BaseView view) {
        iw81DeviceDataModel = new W81DeviceDataModelImp();
        this.view = view;
    }




    public void getupdateRope(String deviceId, String userId) {
        S002DeviceDataRepository.requstUpgradeRopeData(deviceId, userId).as(view.bindAutoDispose()).subscribe(new BaseObserver<String>(BaseApp.getApp(), false) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

                Logger.myLog("getupdateRope ExceptionHandle" + e.code);
                if(e.code == 2000){
                    //删除数据库
                    S002_DetailDataModelAction.deletAll();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.ROPE_DATA_UPGRADE_SUCCESS));
                //

            }

            @Override
            public void onNext(String watchHistoryNBean) {
                Logger.myLog("getupdateRope success" + watchHistoryNBean);

                // saveStepData(watchHistoryNBean, true);

                EventBus.getDefault().post(new MessageEvent(MessageEvent.ROPE_DATA_UPGRADE_SUCCESS));
            }
        });
    }

    public void getW81MonthStep(String deviceId, String userId, String dataType, Long time) {

        W81DeviceDataRepository.requstMonthW81Data(deviceId, userId, dataType, time).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryNList>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryNList watchHistoryNBean) {

                saveStepData(watchHistoryNBean, true);


            }
        });

    }

    public void getW81MothSleep(String deviceId, String userId, String dataType, Long time) {
        W81DeviceDataRepository.requstMonthW81Data(deviceId, userId, dataType, time).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryNList>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryNList watchHistoryNBean) {
                saveSleepData(watchHistoryNBean, true);
            }
        });
    }

    public void getW81MonthHr(String deviceId, String userId, String dataType, Long time) {
        W81DeviceDataRepository.requstMonthW81Data(deviceId, userId, dataType, time).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryNList>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryNList watchHistoryNBean) {
                saveHrData(watchHistoryNBean, true);
            }
        });

    }

    public void getW81NumStep(String deviceId, String userId, String dataType, String dataNum) {

        W81DeviceDataRepository.requstGetNumberW81Data(deviceId, userId, dataType, dataNum).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryNList>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryNList watchHistoryNBean) {

                Logger.myLog("WatchHistoryNList:" + watchHistoryNBean);
                saveStepData(watchHistoryNBean, false);
            }
        });


    }

    public void getW81NumSleep(String deviceId, String userId, String dataType, String dataNum) {
        W81DeviceDataRepository.requstGetNumberW81Data(deviceId, userId, dataType, dataNum).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryNList>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryNList watchHistoryNBean) {
                saveSleepData(watchHistoryNBean, false);
            }
        });


    }

    public void getW81NumHr(String deviceId, String userId, String dataType, String dataNum) {
        W81DeviceDataRepository.requstGetNumberW81Data(deviceId, userId, dataType, dataNum).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryNList>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryNList watchHistoryNBean) {

                saveHrData(watchHistoryNBean, false);
            }
        });


    }

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

    public void saveSleepData(WatchHistoryNList watchHistoryNBean, boolean isMonth) {

        try {
            if (watchHistoryNBean != null && watchHistoryNBean.getList() != null) {
                List<WatchHistoryNBean> list = watchHistoryNBean.getList();
                WatchHistoryNBean bean;
                for (int i = 0; i < list.size(); i++) {
                    bean = list.get(i);
                    //String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, int totalTime,
                    //                              int restfulTime,
                    //                              int lightTime,
                    //                              int soberTime, String sleepDetail

                    int totalSleepTime = TextUtils.isEmpty(bean.getTotalSleepTime()) ? 0 : Integer.parseInt(bean.getTotalSleepTime());
                    int totalrestfulTime = TextUtils.isEmpty(bean.getTotalDeep()) ? 0 : Integer.parseInt(bean.getTotalDeep());
                    int totallightTime = TextUtils.isEmpty(bean.getTotalLight()) ? 0 : Integer.parseInt(bean.getTotalLight());
                    int totalsoberTime = 0;
                    if (TextUtils.isEmpty(bean.getSleepDetailArray())) {
                        totalSleepTime = 0;
                        totalrestfulTime = 0;
                        totallightTime = 0;
                        totalsoberTime = 0;
                    }
                    iw81DeviceDataModel.saveSleepData(bean.getDeviceId(), bean.getUserId(), bean.getWristbandSportDetailId(), bean.getDateStr(), System.currentTimeMillis(), totalSleepTime, totalrestfulTime, totallightTime, totalsoberTime, bean.getSleepDetailArray());
                }
                if (!isMonth) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_sleep));
                }
            }

        } catch (Exception e) {
            Logger.myLog(e.toString());
        }

    }

    public void saveStepData(WatchHistoryNList watchHistoryNBean, boolean isMonth) {
        try {
            if (watchHistoryNBean != null && watchHistoryNBean.getList() != null) {
                List<WatchHistoryNBean> list = watchHistoryNBean.getList();
                WatchHistoryNBean bean;
                for (int i = 0; i < list.size(); i++) {
                    bean = list.get(i);
                    Logger.myLog("saveStepData:-------" + bean);
                    if (bean.getTotalSteps() == 0) {
                        continue;
                    }
                    //String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, int step, int dis, int cal
                    iw81DeviceDataModel.saveStepData(bean.getDeviceId(), bean.getUserId(), bean.getWristbandSportDetailId(), bean.getDateStr(), System.currentTimeMillis(), bean.getTotalSteps(), (int) Float.parseFloat(bean.getTotalDistance()), (int) Float.parseFloat(bean.getTotalCalories()), true);
                    if(isMonth){
                        new W81DeviceDataAction().saveDeviceStepArrayData("网络获取保存",bean.getDeviceId(), bean.getUserId(),bean.getWristbandSportDetailId(), bean.getDateStr(),bean.getStepDetailArray());
                    }

                }
                if (!isMonth) {
                    Logger.myLog("MessageEvent.update_step-------");
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_step));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.myLog(e.toString());
        }
    }

    public void saveHrData(WatchHistoryNList watchHistoryNBean, boolean isMonth) {
        try {
            if (watchHistoryNBean != null && watchHistoryNBean.getList() != null) {
                List<WatchHistoryNBean> list = watchHistoryNBean.getList();
                WatchHistoryNBean bean;
                for (int i = 0; i < list.size(); i++) {
                    bean = list.get(i);
                    Logger.myLog(" saveW81DeviceHrData hasHr:" + bean + TextUtils.isEmpty(bean.getHeartRateDetailArray()));
                    //String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, int step, int dis, int cal
                    if (!TextUtils.isEmpty(bean.getHeartRateDetailArray())) {
                        iw81DeviceDataModel.saveHrData(bean.getDeviceId(), bean.getUserId(), bean.getWristbandSportDetailId(), bean.getDateStr(), System.currentTimeMillis(), bean.getHeartRateDetailArray(), 5);
                    }
                }

                if (!isMonth) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_hr));
                }

            }

        } catch (Exception e) {
            Logger.myLog(e.toString());
        }

    }


    public void saveOxyGen(OxyInfo oxyInfo) {
        iw81DeviceDataModel.saveOxygenData(oxyInfo);
    }

    public void saveOnceHr(OnceHrInfo onceHrInfo) {
        iw81DeviceDataModel.saveOnceHrData(onceHrInfo);
    }

    public void saveBloodPressure(BPInfo bpInfo) {
        iw81DeviceDataModel.saveBloodPresureData(bpInfo);
    }

    public void saveTempData(TempInfo tempInfo) {
        iw81DeviceDataModel.saveTempData(tempInfo);
    }

    public void saveExeciseData(ExerciseInfo exerciseInfo) {
        iw81DeviceDataModel.saveExeciseData(exerciseInfo);
    }


    public void getBloodPressure() {
        BPRepository.requstNumBPData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<BPInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<BPInfo> infos) {
                if (infos != null && infos.size() > 0) {
                    for (int i = 0; i < infos.size(); i++) {
                        saveBloodPressure(infos.get(i));
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

    public void getTempData() {
        TempRepository.requstNumTempData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<TempInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<TempInfo> infos) {
                if (infos != null && infos.size() > 0) {
                    for (int i = 0; i < infos.size(); i++) {
                        saveTempData(infos.get(i));
                    }
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_temp));

                }
                Logger.myLog("getTempData:" + infos);
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

    public void getNumOxyGen() {
        OxygenRepository.requstNumOxygenData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<OxyInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<OxyInfo> infos) {

                if (infos != null && infos.size() > 0) {
                    for (int i = 0; i < infos.size(); i++) {
                        saveOxyGen(infos.get(i));
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

    public void getNumOnceHr() {
        OnceHrRepository.requstNumOnceHrData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<OnceHrInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<OnceHrInfo> infos) {
                if (infos != null && infos.size() > 0) {
                    for (int i = 0; i < infos.size(); i++) {
                        saveOnceHr(infos.get(i));
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

}

