package com.isport.brandapp.bind.presenter;


import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_24HDataModelAction;
import com.isport.blelibrary.db.action.sleep.Sleep_Sleepace_DataModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_24HDataModelAction;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24HDataModel;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_DataModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_24HDataModel;
import com.isport.blelibrary.gen.Sleep_Sleepace_DataModelDao;
import com.isport.blelibrary.gen.Watch_W516_24HDataModelDao;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.bean.http.UpdateWatchResultBean;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.bean.BindInsertOrUpdateBean;
import com.isport.brandapp.bind.view.BindBaseView;
import com.isport.brandapp.device.UpdateSuccessBean;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.history.util.HistoryParmUtil;
import com.isport.brandapp.device.watch.bean.WatchInsertBean;
import com.isport.brandapp.parm.db.DeviceTypeParms;
import com.isport.brandapp.repository.SleepRepository;
import com.isport.brandapp.repository.UpdateResposition;
import com.isport.brandapp.repository.W311Repository;
import com.isport.brandapp.repository.W81DeviceDataRepository;
import com.isport.brandapp.repository.WatchRepository;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.action.BaseAction;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author
 * @Date 2018/10/15
 * @Fuction
 */

public class BindPresenter extends BasePresenter<BindBaseView> {


    private int mCurrentSleepIndex;
    private List<Sleep_Sleepace_DataModel> mSleep_Sleepace_DataModel;
    private int mCurrentWatchIndex;
    private List<Watch_W516_24HDataModel> mWatch_W516_24HDataModel;


    private int mCurrentW311Indx;
    private List<Bracelet_W311_24HDataModel> mBracelet_W311_24HDataModel;

    private BindBaseView view;
    W81DeviceDataModelImp iw81DeviceDataModel;

    public BindPresenter(BindBaseView view) {
        iw81DeviceDataModel = new W81DeviceDataModelImp();

        this.view = view;
    }

    public void onRespondError(String message) {
        ToastUtils.showToast(context, message);
    }


    public void deletCurrentDay(String deviceId, int currentType) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (currentType == JkConfiguration.DeviceType.WATCH_W516) {
                    Watch_W516_24HDataModelAction.delCurretentDay(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), deviceId);
                } else if (currentType == JkConfiguration.DeviceType.BRAND_W311) {
                    Bracelet_W311_24HDataModelAction.delCurretentDay(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), deviceId);
                }
            }
        });
    }

    public void unBind(DeviceBean deviceBean, boolean dirctUnbind) {
        SyncCacheUtils.clearSetting(BaseApp.getApp());
        SyncCacheUtils.clearStartSync(BaseApp.getApp());
        SyncCacheUtils.clearSysData(BaseApp.getApp());
        if (deviceBean == null) {
            return;
        }
        deletCurrentDay(deviceBean.deviceName, deviceBean.currentType);
        //scanModel.unBind(userId, deviceId, deviceType);
        UpdateResposition<Integer, BindInsertOrUpdateBean, BaseUrl, DeviceTypeParms> customRepository = new
                UpdateResposition();
        customRepository.update(HistoryParmUtil.setDevice(deviceBean)).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(context, true) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                //假网状态
                SyncCacheUtils.setUnBindState(false);
                NetProgressObservable.getInstance().hide();
            }

            @Override
            public void onNext(Integer s) {
                SyncCacheUtils.setUnBindState(false);
                NetProgressObservable.getInstance().hide();
                AppSP.putString(context, AppSP.FORM_DFU, "false");
                Logger.myLog("解绑成功");
                ISportAgent.getInstance().deleteDeviceType(deviceBean.deviceType, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                //需要把数据的数据删除

                BleAction.deletAll();
                BaseAction.dropDatas();
                //如果是311获取是516需要把同步的信息和设置的信息给清除
                if (deviceBean.deviceType == JkConfiguration.DeviceType.WATCH_W516 || deviceBean.deviceType == JkConfiguration.DeviceType.BRAND_W311) {
                    SyncCacheUtils.clearSysData(BaseApp.getApp());
                    SyncCacheUtils.clearSetting(BaseApp.getApp());
                    SyncCacheUtils.saveFirstBindW311(BaseApp.getApp());
                    SyncCacheUtils.clearSysData(BaseApp.getApp());
                }
                view.onUnBindSuccess();
                if (deviceBean.deviceType == JkConfiguration.DeviceType.SLEEP) {
                    Logger.myLog("睡眠带解绑成功");
                    TokenUtil.getInstance().saveSleepTime(BaseApp.getApp(), "");
                }
            }
        });
    }

    public void updateSportData(Wristbandstep mWristbandstep, DeviceBean deviceBean) {


        WatchRepository.requst(mWristbandstep).as(view.bindAutoDispose()).subscribe(new BaseObserver<UpdateWatchResultBean>(context) {
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
            public void onNext(UpdateWatchResultBean updateWatchResultBean) {
                NetProgressObservable.getInstance().hide();
                if (view != null) {
                    view.updateWatchDataSuccess(deviceBean);
                }
            }
        });
    }

    public void updateSleepHistoryData(DeviceBean deviceBean) {
        if (deviceBean == null) {
            return;
        }
        if (!(App.appType() == App.httpType)) {
            if (view != null) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_SLEEP_DATA_SUCCESS));
                view.updateSleepDataSuccess(deviceBean);
            }
        } else {
            List<Sleep_Sleepace_DataModel> sleep_sleepace_dataModels =
                    Sleep_Sleepace_DataModelAction
                            .findSleep_Sleepace_DataModelByDeviceIdAndTimeTamp1(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),
                                    App.getSleepBindTime());
            Logger.myLog("getSleepBindTime == " + App.getSleepBindTime());
            if (sleep_sleepace_dataModels != null) {
                mSleep_Sleepace_DataModel = sleep_sleepace_dataModels;
                mCurrentSleepIndex = 0;
                updateSleepHistory(deviceBean);
            } else {
                if (view != null)
                    view.updateSleepDataSuccess(deviceBean);
            }
        }
        //model.updateSleepHistoryData(sleepHistoryDataResult, name);
    }

    private void updateSleepHistory(DeviceBean deviceBean) {
        if (deviceBean == null) {
            return;
        }
        Sleep_Sleepace_DataModel sleep_sleepace_dataModel = mSleep_Sleepace_DataModel.get(mCurrentSleepIndex);
        SleepRepository.requst(sleep_sleepace_dataModel)
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<UpdateSuccessBean>(context) {
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
                    public void onNext(UpdateSuccessBean updateSleepReportBean) {
                        NetProgressObservable.getInstance().hide();
                        if (view != null) {
                            Sleep_Sleepace_DataModelDao sleep_sleepace_dataModelDao = BleAction
                                    .getSleep_Sleepace_DataModelDao();
                            sleep_sleepace_dataModel.setReportId(updateSleepReportBean.getPublicId());
                            sleep_sleepace_dataModelDao.update(sleep_sleepace_dataModel);
                            mCurrentSleepIndex++;
                            if (mCurrentSleepIndex > mSleep_Sleepace_DataModel.size() - 1) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent
                                        .UPDATE_SLEEP_DATA_SUCCESS));
                                view.updateSleepDataSuccess(deviceBean);
                            } else {
                                updateSleepHistory(deviceBean);
                            }
                        }
                    }
                });
    }

    public void updateWatchHistoryData(DeviceBean deviceBean, boolean isUpdateToday) {
        if (deviceBean == null) {
            if (view != null) {
                view.updateFail();
            }
            Constants.isSyncData = false;
            return;
        }
        if (!(App.appType() == App.httpType)) {
            if (view != null) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_WATCH_DATA_SUCCESS));
                view.updateWatchHistoryDataSuccess(deviceBean);
            }
        } else {
            //上传那些满1440的天
            List<Watch_W516_24HDataModel> dataList = Watch_W516_24HDataModelAction
                    .findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp
                            (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),
                                    App.getWatchBindTime(), AppConfiguration.braceletID, isUpdateToday);
            if (dataList != null) {
                mWatch_W516_24HDataModel = dataList;
                mCurrentWatchIndex = 0;
                updateWatchHistory(deviceBean);
                for (int i = 0; i < dataList.size(); i++) {
                    Logger.myLog("dataList == " + dataList.get(i).toString());
                }
            } else {
                if (view != null) {
                    Constants.isSyncData = false;
                    view.updateWatchHistoryDataSuccess(deviceBean);
                }
            }
        }
        //model.updateSleepHistoryData(sleepHistoryDataResult, name);
    }


    public void updateMainBraceletW311HistoryData(DeviceBean deviceBean, boolean isUpdateToday) {
        if (!(App.appType() == App.httpType)) {
            if (view != null) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_BRACELET_DATA_SUCCESS));
                view.updateWatchHistoryDataSuccess(deviceBean);
            }
        } else {
            //上传那些满1440的天
            //App.setBraceletBindTime(0);
            if (deviceBean == null) {
                if (view != null) {
                    view.updateFail();
                }
                return;
            }
            List<Bracelet_W311_24HDataModel> dataList = Bracelet_W311_24HDataModelAction
                    .findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp
                            (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),
                                    App.getBraceletBindTime(), deviceBean.deviceID, isUpdateToday);
            if (dataList != null) {
                //  Logger.myLog("updateBraceletW311HistoryData:" + dataList + "App.getBraceletBindTime():" + App.getBraceletBindTime());
                mBracelet_W311_24HDataModel = dataList;
                mCurrentWatchIndex = 0;
                updateMainW311History(deviceBean);
                for (int i = 0; i < dataList.size(); i++) {
                    Logger.myLog("dataList == " + dataList.get(i).toString());
                }
            } else {
                if (view != null)
                    view.updateWatchHistoryDataSuccess(deviceBean);
            }
        }
    }


    public synchronized void getNoUpgradeW81DevcieDetailData(DeviceBean deviceBean, String userId, String deviceId, String defWriId) {

        //需要上传当天的数据
        List<WatchInsertBean> upgradeList = iw81DeviceDataModel.getAllNoUpgradeW81DeviceDetailData(deviceId, userId, defWriId, true);

        //上传的距离为米
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

        if (view != null) {
            view.updateWatchHistoryDataSuccess(deviceBean);
        }

    }

    public void updateBraceletW311HistoryData(DeviceBean deviceBean, boolean isUpdateToday) {
        if (deviceBean == null) {
            if (view != null) {
                view.updateFail();
            }
            Constants.isSyncData = false;
            return;
        }
        if (!(App.appType() == App.httpType)) {
            if (view != null) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_BRACELET_DATA_SUCCESS));
                view.updateWatchHistoryDataSuccess(deviceBean);
                Constants.isSyncData = false;
            }
        } else {


            if (DeviceTypeUtil.isContainW81(deviceBean.currentType) || DeviceTypeUtil.isContainF18(deviceBean.getCurrentType())) {
                getNoUpgradeW81DevcieDetailData(deviceBean, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), deviceBean.deviceID, "0");
                return;
            }
            //上传那些满1440的天
            List<Bracelet_W311_24HDataModel> dataList = Bracelet_W311_24HDataModelAction
                    .findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp
                            (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),
                                    App.getBraceletBindTime(), deviceBean.deviceID, isUpdateToday);
            Logger.myLog("updateBraceletW311HistoryData:" + dataList + "App.getBraceletBindTime():" + App.getBraceletBindTime());
            if (dataList != null) {
                mBracelet_W311_24HDataModel = dataList;
                mCurrentWatchIndex = 0;
                updateW311History(deviceBean);
                for (int i = 0; i < dataList.size(); i++) {
                    Logger.myLog("dataList == " + dataList.get(i).toString());
                }
            } else {
                if (view != null)
                    view.updateWatchHistoryDataSuccess(deviceBean);
            }
        }
        //model.updateSleepHistoryData(sleepHistoryDataResult, name);
    }


    public void updateW311History(DeviceBean deviceBean) {
        Logger.myLog("updateBraceletHistory == 上传数据" + mCurrentWatchIndex);
        if (mBracelet_W311_24HDataModel == null) {

            if (view != null) {

                view.updateFail();
            }
            Constants.isSyncData = false;
            return;
        }
        if (mBracelet_W311_24HDataModel.size() == 0 && mBracelet_W311_24HDataModel.size() - 1 < mCurrentW311Indx) {
            Constants.isSyncData = false;
            return;
        }
        Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = mBracelet_W311_24HDataModel.get(mCurrentW311Indx);

        // Logger.myLog("updateBraceletHistory == 上传数据" + bracelet_w311_24HDataModel.toString());
        Constants.isSyncData = true;
        W311Repository.requst(bracelet_w311_24HDataModel)
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<UpdateSuccessBean>(context) {
                    @Override
                    protected void hideDialog() {

                    }

                    @Override
                    protected void showDialog() {

                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable e) {
                        Constants.isSyncData = false;
                        if (view != null) {
                            view.updateFail();
                        }

                    }

                    @Override
                    public void onNext(UpdateSuccessBean updateSleepReportBean) {
                        NetProgressObservable.getInstance().hide();
                        Constants.isSyncData = false;
                        Logger.myLog("UpdateSuccessBean == updateSleepReportBean" + updateSleepReportBean.toString());
                            /*Bracelet_W311_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                                    .getsBracelet_w311_24HDataModelDao();*/
                        bracelet_w311_24HDataModel.setReportId(updateSleepReportBean.getPublicId());
                        // Logger.myLog("UpdateSuccessBean == " + bracelet_w311_24HDataModel.toString());
                        Bracelet_W311_24HDataModelAction.saveOrUpdateHttp(bracelet_w311_24HDataModel, updateSleepReportBean.getTime());
                        // watch_w516_24HDataModelDao.update(bracelet_w311_24HDataModel);
                        //设置手环更新的时间
                        BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_SYNCTIME, DateUtil.dataToString(new Date(updateSleepReportBean.getTimestamp()), "yyyy-MM-dd"));
                        BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, DateUtil.dataToString(new Date(updateSleepReportBean.getTimestamp()), "yyyy-MM-dd"));
                        App.setBraceletBindTime(updateSleepReportBean.getTimestamp());
                        mCurrentWatchIndex++;
                        if (mCurrentWatchIndex > mBracelet_W311_24HDataModel.size() - 1) {
                            EventBus.getDefault().post(new MessageEvent(MessageEvent
                                    .UPDATE_WATCH_DATA_SUCCESS));
                            view.updateWatchHistoryDataSuccess(deviceBean);
                        } else {
                            updateW311History(deviceBean);
                        }
                    }

                });
    }

    public void updateMainW311History(DeviceBean deviceBean) {

        Constants.isSyncData = true;
        if (mBracelet_W311_24HDataModel == null || deviceBean == null) {
            return;
        }
        if (mBracelet_W311_24HDataModel.size() == 0 && mBracelet_W311_24HDataModel.size() - 1 < mCurrentW311Indx) {
            return;
        }
        Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = mBracelet_W311_24HDataModel.get(mCurrentW311Indx);

        //  Logger.myLog("updateBraceletHistory == 上传数据" + bracelet_w311_24HDataModel.toString());

        W311Repository.requst(bracelet_w311_24HDataModel)
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<UpdateSuccessBean>(context) {
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
                    public void onNext(UpdateSuccessBean updateSleepReportBean) {
                        Constants.isSyncData = true;
                        NetProgressObservable.getInstance().hide();
                        Logger.myLog("UpdateSuccessBean == updateSleepReportBean" + updateSleepReportBean.toString());
                        if (view != null) {
                            /*Bracelet_W311_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                                    .getsBracelet_w311_24HDataModelDao();*/
                            bracelet_w311_24HDataModel.setReportId(updateSleepReportBean.getPublicId());
                            //Logger.myLog("UpdateSuccessBean == " + bracelet_w311_24HDataModel.toString());
                            Bracelet_W311_24HDataModelAction.saveOrUpdateHttp(bracelet_w311_24HDataModel, updateSleepReportBean.getTime());
                            // watch_w516_24HDataModelDao.update(bracelet_w311_24HDataModel);
                            //设置手环更新的时间
                            App.setBraceletBindTime(updateSleepReportBean.getTimestamp());
                            mCurrentWatchIndex++;
                            if (mCurrentWatchIndex > mBracelet_W311_24HDataModel.size() - 1) {
                                //view.updateWatchHistoryDataSuccess(deviceBean);
                            } else {
                                updateW311History(deviceBean);
                            }
                        }
                    }
                });
    }


    private void updateWatchHistory(DeviceBean deviceBean) {
        if (deviceBean == null) {
            if (view != null) {
                view.updateFail();
                Constants.isSyncData = false;
            }
            return;
        }
        Logger.myLog("updateWatchHistory == 上传数据" + mCurrentWatchIndex);
        Watch_W516_24HDataModel watch_w516_24HDataModel = mWatch_W516_24HDataModel.get(mCurrentWatchIndex);

        Constants.isSyncData = true;
        WatchRepository.requst(watch_w516_24HDataModel)
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<UpdateSuccessBean>(context) {
                    @Override
                    protected void hideDialog() {

                    }

                    @Override
                    protected void showDialog() {

                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable e) {
                        Constants.isSyncData = false;
                        if (view != null) {
                            view.updateFail();
                        }
                    }

                    @Override
                    public void onNext(UpdateSuccessBean updateSleepReportBean) {
                        Constants.isSyncData = false;
                        NetProgressObservable.getInstance().hide();
                        Logger.myLog("UpdateSuccessBean == " + updateSleepReportBean.toString());
                        if (view != null) {
                            Watch_W516_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                                    .getWatch_W516_24HDataModelDao();
                            watch_w516_24HDataModel.setReportId(updateSleepReportBean.getPublicId());
                            watch_w516_24HDataModelDao.update(watch_w516_24HDataModel);
                            App.setWatchBindTime(updateSleepReportBean.getTimestamp());
                            mCurrentWatchIndex++;
                            if (mCurrentWatchIndex > mWatch_W516_24HDataModel.size() - 1) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent
                                        .UPDATE_WATCH_DATA_SUCCESS));
                                view.updateWatchHistoryDataSuccess(deviceBean);
                            } else {
                                updateWatchHistory(deviceBean);
                            }
                        }
                    }
                });
    }
}
