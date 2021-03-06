package com.isport.brandapp.home.presenter;

import android.text.TextUtils;

import com.blankj.utilcode.util.NetworkUtils;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.action.DeviceTypeTableAction;
import com.isport.blelibrary.db.action.scale.Scale_FourElectrode_DataModelAction;
import com.isport.blelibrary.db.action.sleep.Sleep_Sleepace_DataModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_24HDataModelAction;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.DeviceTempUnitlTable;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_RealTimeData;
import com.isport.blelibrary.db.table.scale.Scale_FourElectrode_DataModel;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_DataModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_24HDataModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.W307JDevice;
import com.isport.blelibrary.deviceEntry.impl.W311Device;
import com.isport.blelibrary.deviceEntry.impl.W520Device;
import com.isport.blelibrary.deviceEntry.impl.W526Device;
import com.isport.blelibrary.deviceEntry.impl.W557Device;
import com.isport.blelibrary.deviceEntry.impl.W560BDevice;
import com.isport.blelibrary.deviceEntry.impl.W560Device;
import com.isport.blelibrary.deviceEntry.impl.W812BDevice;
import com.isport.blelibrary.deviceEntry.impl.W812Device;
import com.isport.blelibrary.deviceEntry.impl.W813Device;
import com.isport.blelibrary.deviceEntry.impl.W814Device;
import com.isport.blelibrary.deviceEntry.impl.W817Device;
import com.isport.blelibrary.deviceEntry.impl.W819Device;
import com.isport.blelibrary.deviceEntry.impl.W910Device;
import com.isport.blelibrary.deviceEntry.impl.Watch516Device;
import com.isport.blelibrary.gen.Sleep_Sleepace_DataModelDao;
import com.isport.blelibrary.gen.Watch_W516_24HDataModelDao;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.bean.ScacleBean;
import com.isport.brandapp.home.bean.db.HeartRateMainData;
import com.isport.brandapp.home.bean.db.SleepMainData;
import com.isport.brandapp.home.bean.db.StandardMainData;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.bean.http.BindDevice;
import com.isport.brandapp.home.bean.http.BindDeviceList;
import com.isport.brandapp.home.bean.http.ScaleHistoryData;
import com.isport.brandapp.home.bean.http.SleepHistoryData;
import com.isport.brandapp.home.bean.http.UpdateWatchResultBean;
import com.isport.brandapp.home.bean.http.WatchHistoryData;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.home.bean.http.WristbandHrHeart;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.home.view.FragmentDataView;
import com.isport.brandapp.arithmetic.WeightStandardImpl;
import com.isport.brandapp.bean.BrandBean;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bean.SleepBean;
import com.isport.brandapp.bean.SportBean;
import com.isport.brandapp.bind.model.DeviceOptionImple;
import com.isport.brandapp.bind.model.DeviceResultCallBack;
import com.isport.brandapp.device.UpdateSuccessBean;
import com.isport.brandapp.device.W81Device.IW81DeviceDataModel;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.bracelet.braceletModel.IW311DataModel;
import com.isport.brandapp.device.bracelet.braceletModel.IW311SettingModel;
import com.isport.brandapp.device.bracelet.braceletModel.W311DataModelImpl;
import com.isport.brandapp.device.bracelet.braceletModel.W311ModelSettingImpl;
import com.isport.brandapp.device.history.util.HistoryParmUtil;
import com.isport.brandapp.device.sleep.bean.SleepHistoryNBean;
import com.isport.brandapp.device.sleep.bean.SleepHistoryNList;
import com.isport.brandapp.device.watch.bean.WatchHistoryNBean;
import com.isport.brandapp.device.watch.bean.WatchHistoryNList;
import com.isport.brandapp.device.watch.watchModel.IW516Model;
import com.isport.brandapp.device.watch.watchModel.W516Model;
import com.isport.brandapp.parm.db.ProgressShowParms;
import com.isport.brandapp.parm.http.ScaleParms;
import com.isport.brandapp.parm.http.SleepHistoryParms;
import com.isport.brandapp.parm.http.WatchHistoryParms;
import com.isport.brandapp.repository.CustomRepository;
import com.isport.brandapp.repository.ExerciseRepository;
import com.isport.brandapp.repository.MainResposition;
import com.isport.brandapp.repository.ScaleRepository;
import com.isport.brandapp.repository.SleepRepository;
import com.isport.brandapp.repository.WatchRepository;
import com.isport.brandapp.sport.response.SportRepository;
import com.isport.brandapp.util.DeviceDataUtil;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.InitCommonParms;
import com.isport.brandapp.util.RequestCode;
import com.isport.brandapp.util.SportAcacheUtil;
import com.isport.brandapp.util.W311DeviceDataUtil;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.bean.TempInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonnet.net.PostBody;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.entry.WatchRealTimeData;
import brandapp.isport.com.basicres.entry.WatchTargetBean;
import brandapp.isport.com.basicres.entry.bean.BaseParms;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.CommonDateUtil;

/**
 * @Author
 * @Date 2018/10/12
 * @Fuction ??????????????????
 */

public class FragmentDataPresenter extends BasePresenter<FragmentDataView> implements IFragmentDataPresenter {

    private static final String TAG = "FragmentDataPresenter";

    IW311DataModel iw311DataModel;
    W516Model w516Model;
    IW81DeviceDataModel w81DeviceDataModel;
    IW311SettingModel w311ModelSetting;


    public FragmentDataPresenter(FragmentDataView view) {
        this.view = view;
        deviceOptionImple = new DeviceOptionImple();

        iw311DataModel = new W311DataModelImpl();
        w516Model = new W516Model();
        w311ModelSetting = new W311ModelSettingImpl();
        w81DeviceDataModel = new W81DeviceDataModelImp();
        if (w81DataPresenter == null) {
            w81DataPresenter = new W81DataPresenter(view);
        }
    }

    //*****************************************?????????****************************************//

    /**
     * show???????????? ??????????????????????????????????????????????????????list??????????????????????????????????????????????????????
     * ??????????????????????????????????????????
     *
     * @param isFirstDisplayDB
     * @param show
     */
    @Override
    public void getDeviceList(boolean isFirstDisplayDB, boolean show, boolean setWatchDefault) {

        Logger.myLog(TAG,"-----getDL="+isFirstDisplayDB+"\n"+show+"\n"+setWatchDefault);
       /* if (true) {
            HashMap<Integer, DeviceBean> deviceBeanHashMap = new HashMap<>();
            mActView.get().successGetDeviceListFormHttp(deviceBeanHashMap, show, setWatchDefault);
            return;
        }*/
        MainResposition<BindDeviceList, BaseParms, BaseUrl, ProgressShowParms> mainResposition = new MainResposition<>();
        InitCommonParms<BaseParms, BaseUrl, ProgressShowParms> commonParms = new InitCommonParms<>();
        BaseParms parms = new BaseParms();


        parms.setUserId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        parms.setInterfaceId("0");
        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.BASIC;
        baseUrl.url2 = JkConfiguration.Url.DEVICE;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYUSERID;
        baseUrl.userid = TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp());
        ProgressShowParms deviceIdParms = new ProgressShowParms();
        deviceIdParms.requestCode = RequestCode.Request_getBindDeviceList;
        deviceIdParms.show = show;
        //?????????????????????????????????????????????????????????
        //?????????????????????????????????
        mainResposition.requst(commonParms.setPostBody(isFirstDisplayDB ? isFirstDisplayDB : !NetUtils.hasNetwork() || !(App.appType() == App.httpType)).setParms(parms)
                .setBaseUrl(baseUrl)
                .setBaseDbParms(deviceIdParms).setType(JkConfiguration.RequstType
                        .GET_BIND_DEVICELIST)
                .getPostBody())
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<BindDeviceList>(BaseApp.getApp(), false) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

                //?????????????????????
                Logger.myLog(TAG,"getDeviceList???" + e.toString());
            }

            @Override
            public void onNext(BindDeviceList bean) {
                if (!show) {
                    Logger.myLog(TAG,"BindDeviceList ?????? ????????????");
                    NetProgressObservable.getInstance().hide("getDeviceList");
                } else {
                    Logger.myLog(TAG,"BindDeviceList ?????? ???????????????");
                }
                HashMap<Integer, DeviceBean> deviceBeanHashMap = new HashMap<>();
                //???????????????????????????????????????????????????
                if (!isFirstDisplayDB && NetUtils.hasNetwork())
                    DeviceTypeTableAction.deleteAllDevices();

                if (bean != null && bean.list != null) {
                    DeviceBean deviceBean;
                    BindDevice bindDevice;
                    /*if (bean.list.size() > 0) {
                        //?????????????????????????????????

                    }*/
                    for (int i = 0; i < bean.list.size(); i++) {


                        Logger.myLog(TAG,"deviceBeanHashMap" + bean.list.get(i).toString());
                        // 0:?????????1???????????????2????????????
                        deviceBean = new DeviceBean();
                        bindDevice = bean.list.get(i);
                        if (bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                            continue;
                        }
                        deviceBean.deviceType = bindDevice.getDeviceTypeId();
                        deviceBean.deviceName = bindDevice.getDevicetName();
                        //deviceBean.deviceID=bindDevice.getDevicetName();
                        //????????????????????????mac????????????deviceId???
                        if (bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.BODYFAT) {
                            deviceBean.mac = Utils.resetDeviceMac(bindDevice.getDeviceId());
                        } else {
                            deviceBean.mac = bindDevice.getMac();
                            App.setDeviceBindTime(bindDevice.getCreateTime());
                            // BaseManager.deviceBindTime = bindDevice.getCreateTime();
                            ISportAgent.getInstance().setDeviceBindTime(bindDevice.getCreateTime());
                            Logger.myLog(TAG,"requstUpgradeExerciseData App.getWatchBindTime():" + bindDevice.getCreateTime());
                        }
                        // Logger.myLog("BleSPUtils.WATCH_LAST_SYNCTIME"+BleSPUtils.getString(BaseApp.getApp(),BleSPUtils.WATCH_LAST_SYNCTIME,"2018-0201"));


                        Logger.myLog(TAG,"BleSPUtils.WATCH_LAST_SYNCTIME" + bindDevice.getCreateTime() + ":" + DateUtil.dataToString(new Date(bindDevice.getCreateTime()), "yyyy-MM-dd") + "bindDevice.getTimestamp()" + bindDevice.getTimestamp() + ":" + DateUtil.dataToString(new Date(bindDevice.getTimestamp()), "yyyy-MM-dd") + "bean.list.get(i).getDeviceTypeId():" + bean.list.get(i).getDeviceTypeId());
                        //createTime????????????????????????timestamp?????????????????????????????????????????????????????????   ???????????????????????????
                        if (bindDevice.getCreateTime() > bindDevice.getTimestamp() &&
                                bindDevice.getCreateTime() - bindDevice.getTimestamp() > 24 * 60 * 60 * 1000) {
                            if (bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.BRAND_W311 || bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.Brand_W520) {
                                deviceBean.timeTamp = bindDevice.getCreateTime();
                            } else {
                                deviceBean.timeTamp = bindDevice.getCreateTime() - 24 * 60 * 60 * 1000;
                            }
                        } else {
                            if (bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.BRAND_W311 || bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.Brand_W520) {
                                deviceBean.timeTamp = bindDevice.getTimestamp() + 24 * 60 * 60 * 1000;
                                if (deviceBean.timeTamp > System.currentTimeMillis()) {
                                    deviceBean.timeTamp = deviceBean.timeTamp - 24 * 60 * 60 * 1000;
                                }
                            } else {
                                //W516???W556???W557???W812B
                                deviceBean.timeTamp = bindDevice.getTimestamp();
                            }
                        }
                        int deviceType = bean.list.get(i).getDeviceTypeId();
                        switch (deviceType) {
                            case JkConfiguration.DeviceType.WATCH_W516:
                                App.setWatchBindTime(deviceBean.timeTamp);
                                BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd"));
                                deviceBean.currentType = JkConfiguration.DeviceType.WATCH_W516;
                                deviceBean.deviceID = bindDevice.getDevicetName();
                                AppConfiguration.braceletID = deviceBean.deviceID;
                                deviceBean.sportBean = new SportBean();
                                deviceBean.connectState = false;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);
                                break;

                            case JkConfiguration.DeviceType.Watch_W556:

                                App.setWatchBindTime(deviceBean.timeTamp);
                                BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd"));
                                Logger.myLog(TAG,"BleSPUtils.WATCH_LAST_SYNCTIME" + BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, "2018-0201"));
                                //BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, "2019-07-29");
                                deviceBean.currentType = JkConfiguration.DeviceType.Watch_W556;
                                deviceBean.deviceID = bindDevice.getDevicetName();
                                deviceBean.sportBean = new SportBean();
                                deviceBean.connectState = false;
                                AppConfiguration.braceletID = deviceBean.deviceID;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);
                                break;
                            case JkConfiguration.DeviceType.BODYFAT:
                                /*deviceBean.currentType = JkConfiguration.DeviceType.BODYFAT;
                                deviceBean.deviceID = Utils.resetDeviceMac(bindDevice.getDeviceId());
                                deviceBean.weightBean = new WeightBean();
                                deviceBean.connectState = false;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);*/
                                break;
                            case JkConfiguration.DeviceType.SLEEP:
                                deviceBean.currentType = JkConfiguration.DeviceType.SLEEP;
                                deviceBean.deviceID = bindDevice.getDeviceId();
                                deviceBean.sleepBean = new SleepBean();
                                deviceBean.connectState = false;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);
                                break;
                            case JkConfiguration.DeviceType.BRAND_W311:
                            case JkConfiguration.DeviceType.Brand_W520:
                            case JkConfiguration.DeviceType.BRAND_W307J:
                                //SyncCacheUtils.clearFirstBindW311(BaseApp.getApp());
                                App.setBraceletBindTime(deviceBean.timeTamp);
                                deviceBean.deviceID = bindDevice.getDevicetName();
                                AppConfiguration.braceletID = deviceBean.deviceID;
                                BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_SYNCTIME, DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd"));
                                BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd"));
                                //BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_SYNCTIME, "2018-08-13");
                                //BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, "2018-08-13");
                                String lastSyncTime = BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, "");
                                Logger.myLog(TAG,"bindDevice.getCreateTime()" + DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd") + " BleSPUtils.putString:" + lastSyncTime);
                                deviceBean.currentType = deviceType;
                                deviceBean.brandBean = new BrandBean();
                                deviceBean.connectState = false;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);
                                break;
                            case JkConfiguration.DeviceType.Brand_W811:
                            case JkConfiguration.DeviceType.Brand_W814:
                            case JkConfiguration.DeviceType.Watch_W812:
                            case JkConfiguration.DeviceType.Watch_W817:
                            case JkConfiguration.DeviceType.Watch_W813:
                            case JkConfiguration.DeviceType.Watch_W819:
                            case JkConfiguration.DeviceType.Watch_W910:
                                App.setBraceletBindTime(deviceBean.timeTamp);
                                deviceBean.currentType = deviceType;
                                deviceBean.deviceID = bindDevice.getDevicetName();
                                AppConfiguration.braceletID = deviceBean.deviceID;
                                deviceBean.brandBean = new BrandBean();
                                deviceBean.connectState = false;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);
                            default:
                                AppConfiguration.braceletID = deviceBean.deviceID;
                                App.setWatchBindTime(deviceBean.timeTamp);
                                BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd"));
                                deviceBean.currentType = deviceType;
                                deviceBean.deviceID = bindDevice.getDevicetName();
                                AppConfiguration.braceletID = deviceBean.deviceID;
                                deviceBean.sportBean = new SportBean();
                                deviceBean.connectState = false;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);
                                break;

                        }
                        // TODO: 2019/1/25 ?????????????????????????????????????????????,???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        //??????????????????????????????????????????????????????
                        //?????????????????????????????????
                        if (!isFirstDisplayDB && NetUtils.hasNetwork())
                            DeviceTypeTableAction.updateOrDelete(bean.list.get(i).getDeviceTypeId(), deviceBean.mac,
                                    deviceBean.deviceID, TokenUtil
                                            .getInstance().getPeopleIdStr(BaseApp.getApp()), deviceBean.deviceName);
                    }
                }
                if (isViewAttached()) {
                    if (isFirstDisplayDB) {
                        // {deviceName='W812', rssi=0, address='F1:B2:A7:23:6B:E4', deviceType=812, scanRecord=null}
                       /* DeviceBean deviceBean = new DeviceBean();
                        deviceBean.deviceName = "W812" + "-" + "F1B2A7236BE4";
                        deviceBean.mac = "F1:B2:A7:23:6B:E4";
                        deviceBean.deviceType = 812;
                        deviceBean.deviceID = "W812" + "-" + "F1B2A7236BE4";
                        deviceBean.currentType = JkConfiguration.DeviceType.Watch_W812;
                        deviceBean.brandBean = new BrandBean();
                        deviceBeanHashMap.put(JkConfiguration.DeviceType.Watch_W812, deviceBean);*/
                        mActView.get().successGetDeviceListFormDB(deviceBeanHashMap, show, setWatchDefault);
                    } else {
                      /*  if (deviceBeanHashMap.containsKey(JkConfiguration.DeviceType.BRAND_W311)) {
                            if(!SyncCacheUtils.getFirstBindW311State(BaseApp.getApp())){
                                SyncCacheUtils.clearFirstBindW311(BaseApp.getApp());
                            }
                        } else {
                            //SyncCacheUtils.saveFirstBindW311(BaseApp.getApp());
                        }*/
                       /* DeviceBean deviceBean = new DeviceBean();
                        deviceBean.deviceName = "W812" + "-" + "F1B2A7236BE4";
                        deviceBean.mac = "F1:B2:A7:23:6B:E4";
                        deviceBean.deviceType = 812;
                        deviceBean.currentType = JkConfiguration.DeviceType.Watch_W812;
                        deviceBean.brandBean = new BrandBean();
                        deviceBean.deviceID = "W812" + "-" + "F1B2A7236BE4";
                        deviceBeanHashMap.put(JkConfiguration.DeviceType.Watch_W812, deviceBean);*/
                        mActView.get().successGetDeviceListFormHttp(deviceBeanHashMap, show, setWatchDefault);
                    }
                }
            }
        });
    }

    /**
     * ??????W516???????????????????????????
     */

    public void getWatchStepLastTwoData(boolean isConnect) {
        IW516Model iw516Model = new W516Model();
        Observable.create(new ObservableOnSubscribe<WatchSportMainData>() {
            @Override
            public void subscribe(ObservableEmitter<WatchSportMainData> emitter) throws Exception {
                WatchSportMainData mWatchSportMainData = iw516Model.getWatchStepLastTwoData(AppConfiguration.isConnected);
                emitter.onNext(mWatchSportMainData);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new Consumer<WatchSportMainData>() {
            @Override
            public void accept(WatchSportMainData wristbandstep) throws Exception {
                if (view != null) {
                    view.successGetMainLastStepDataForDB(wristbandstep);
                }
            }
        });
    }
    //*************************??????????????????**********************************

    /**
     * ??????W311???????????????????????????
     */

    public void getW516StepLastTowData() {
        getDeviceStepLastTwoData(JkConfiguration.DeviceType.WATCH_W516);
    }

    public void getW311StepLastTwoData() {
        getDeviceStepLastTwoData(JkConfiguration.DeviceType.BRAND_W311);
    }

    public void getW81StepLastTwoData() {
        getDeviceStepLastTwoData(JkConfiguration.DeviceType.Watch_W812);
    }

    public void getDeviceStepLastTwoData(int currentType) {
        Observable.create(new ObservableOnSubscribe<WatchSportMainData>() {
            @Override
            public void subscribe(ObservableEmitter<WatchSportMainData> emitter) throws Exception {
                WatchSportMainData mWatchSportMainData = null;
                Logger.myLog(TAG,"getDeviceStepLastTwoData currentType:" + currentType + "userId:" + TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()) + " ,AppConfiguration.braceletID:" + AppConfiguration.braceletID + ",AppConfiguration.isConnected???" + AppConfiguration.isConnected);
                if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                    mWatchSportMainData = iw311DataModel.getW311hStepLastTwoData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID, AppConfiguration.isConnected);
                } else if (DeviceTypeUtil.isContainWatch(currentType)) {
                    mWatchSportMainData = w516Model.getWatchStepLastTwoData(AppConfiguration.isConnected);
                } else if (DeviceTypeUtil.isContainW81(currentType)) {
                    mWatchSportMainData = w81DeviceDataModel.getLastStepData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, DeviceTypeUtil.isContainW81());
                }
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


    //*************************??????????????????**********************************

    //*************************??????????????????**********************************

    /**
     * w311??????2???????????????
     */

    public void getW311HrLastTwoData() {
        getDeviceHrLastTwoData(JkConfiguration.DeviceType.BRAND_W311);

    }

    public void getW516HrLastTwoData() {
        getDeviceHrLastTwoData(JkConfiguration.DeviceType.WATCH_W516);

    }


    public void getDevcieOxygenData() {
        Observable.create(new ObservableOnSubscribe<OxyInfo>() {
            @Override
            public void subscribe(ObservableEmitter<OxyInfo> emitter) throws Exception {
                OxyInfo info = w81DeviceDataModel.getOxygenLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

                Logger.myLog(TAG,"getOxygenLastData:" + info);

                emitter.onNext(info);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<OxyInfo>(BaseApp.getApp(), false) {
            @Override
            public void onNext(OxyInfo oxyInfo) {
                if (view != null) {
                    Logger.myLog(TAG,"getOxygenLastData: successGetMainLastOxgenData" + oxyInfo);
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

    public void getDevcieOnceHrData() {
        Observable.create(new ObservableOnSubscribe<OnceHrInfo>() {
            @Override
            public void subscribe(ObservableEmitter<OnceHrInfo> emitter) throws Exception {
                OnceHrInfo info = w81DeviceDataModel.getOneceHrLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

                Logger.myLog(TAG,"getOxygenLastData:" + info);

                emitter.onNext(info);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<OnceHrInfo>(BaseApp.getApp(), false) {
            @Override
            public void onNext(OnceHrInfo oxyInfo) {
                if (view != null) {
                    Logger.myLog(TAG,"getOxygenLastData: successGetMainLastOxgenData" + oxyInfo);
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

    public void getDeviceBloodPressure() {
        Observable.create(new ObservableOnSubscribe<BPInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BPInfo> emitter) throws Exception {
                BPInfo info = w81DeviceDataModel.getBloodPressureLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
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


    public void startqeustData() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isA = NetworkUtils.isAvailable();
                emitter.onNext(isA);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean tempInfo) {
                /*if (view != null) {
                    view.successGetMainLastTempValue(tempInfo);
                }*/
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
        Observable.create(new ObservableOnSubscribe<TempInfo>() {
            @Override
            public void subscribe(ObservableEmitter<TempInfo> emitter) throws Exception {
                TempInfo info = w81DeviceDataModel.getTempInfoeLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
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


    public void getDeviceHrLastTwoData(int currentType) {
        IW311DataModel iw311DataModel = new W311DataModelImpl();
        Observable.create(new ObservableOnSubscribe<HeartRateMainData>() {
            @Override
            public void subscribe(ObservableEmitter<HeartRateMainData> emitter) throws Exception {
                HeartRateMainData mHeartRateMainData = null;
                if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                    mHeartRateMainData = iw311DataModel.getWatchHeartRteLastTwoData();
                } else if (DeviceTypeUtil.isContainWatch(currentType)) {
                    mHeartRateMainData = w516Model.getWatchHeartRteLastTwoData();
                } else if (DeviceTypeUtil.isContainW81(currentType)) {
                    mHeartRateMainData = w81DeviceDataModel.getLastHrData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
                }
                if (mHeartRateMainData == null) {
                    mHeartRateMainData = new HeartRateMainData();
                }
                emitter.onNext(mHeartRateMainData);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<HeartRateMainData>(BaseApp.getApp(), false) {
            @Override
            public void onNext(HeartRateMainData heartRateMainData) {
                if (view != null) {
                    view.successGetMainLastHrDataForDb(heartRateMainData);
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
    //*************************??????????????????**********************************


    //************************* ??????????????????**********************************

    /**
     * ???DB????????????W516??????
     *
     * @param show
     */
    public void getMainW516StandDataFromDB(boolean show) {
        //???????????????,??????????????????
        if (AppConfiguration.deviceBeanList.size() > 0 && DeviceTypeUtil.isContainWatch()) {
            //getWeekDate(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID, show, JkConfiguration.DeviceType.WATCH_W516);
        }
    }


    /**
     * ???DB????????????W311??????
     **/
    public void getMainW311StandDataFromDB(boolean show) {
        //???????????????,??????????????????
        if (AppConfiguration.deviceBeanList.size() > 0 && AppConfiguration.deviceBeanList.containsKey
                (JkConfiguration.DeviceType.BRAND_W311)) {
            //getWeekDate(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID, show, JkConfiguration.DeviceType.BRAND_W311);
        }
    }


    /**
     * ????????????????????????
     *
     * @param userid
     * @param deviceId
     * @param show
     */
    @Override
    public void getWeekDate(String userid, String deviceId, boolean show, int currentType) {
        IW516Model model = new W516Model();
        IW311DataModel iw311DataModel = new W311DataModelImpl();
        Observable.create(new ObservableOnSubscribe<WatchTargetBean>() {
            @Override
            public void subscribe(ObservableEmitter<WatchTargetBean> emitter) throws Exception {
                WatchTargetBean wristbandstep = model.getWatchTargetStep(deviceId, userid);
                if (wristbandstep == null) {
                    wristbandstep = new WatchTargetBean();
                    wristbandstep.setTargetStep(6000);
                }
                emitter.onNext(wristbandstep);
                emitter.onComplete();

            }
        }).map(new Function<WatchTargetBean, StandardMainData>() {
            @Override
            public StandardMainData apply(WatchTargetBean watchTargetBean) throws Exception {
                if (DeviceTypeUtil.isContainBrand()) {
                    return iw311DataModel.getBraceletWeekData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), watchTargetBean.getTargetStep());
                } else if (DeviceTypeUtil.isContainWatch()) {
                    return model.getWatchWeekData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), watchTargetBean.getTargetStep());
                } else {
                    return new StandardMainData();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<StandardMainData>(BaseApp.getApp(), false) {
            @Override
            public void onNext(StandardMainData standardMainData) {
               /* if (!show)
                    NetProgressObservable.getInstance().hide();
                if (view != null) {
                    view.successWeekSport(standardMainData);
                }*/
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

    //************************* ??????????????????**********************************

    /**
     * ???DB????????????Scale??????
     *
     * @param show
     */
    public void getMainScaleDataFromDB(boolean show, boolean isPass) {


        Logger.myLog(TAG,"getMainScaleDataFromDB:-------------------");


        //?????????????????????
        Observable.create(new ObservableOnSubscribe<List<Scale_FourElectrode_DataModel>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Scale_FourElectrode_DataModel>> emitter) throws Exception {

                List<Scale_FourElectrode_DataModel> scaleFourElectrodeDataModelByDeviceId =
                        Scale_FourElectrode_DataModelAction.findScaleFourElectrodeDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7);
                if (scaleFourElectrodeDataModelByDeviceId == null) {
                    scaleFourElectrodeDataModelByDeviceId = new ArrayList<>();
                }
                emitter.onNext(scaleFourElectrodeDataModelByDeviceId);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<Scale_FourElectrode_DataModel>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(List<Scale_FourElectrode_DataModel> list) {
                WeightStandardImpl weightStandard = new WeightStandardImpl();
                Scale_FourElectrode_DataModel scale_fourElectrode_dataModel = null;
                ArrayList<ScacleBean> scacleBeans = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    ScacleBean scacleBean = new ScacleBean();
                    Scale_FourElectrode_DataModel scale_fourElectrode_dataModel1 = list.get(i);
                    scacleBean.setBMI((float) scale_fourElectrode_dataModel1.getBMI());
                    scacleBean.setWeight(scale_fourElectrode_dataModel1.getWeight());
                    scacleBean.setStrDate(TimeUtils.getTimeByYYMMDDHHMM(scale_fourElectrode_dataModel1.getTimestamp()));
                    ArrayList<String> result = weightStandard.bmiStandardWithValue(scale_fourElectrode_dataModel1.getBMI());
                    scacleBean.setColor(result.get(1));
                    scacleBean.setStander(result.get(0));
                    scacleBeans.add(scacleBean);
                }
                if (list.size() > 0) {
                    scale_fourElectrode_dataModel = list
                            .get(list.size() - 1);
                }

                if (isViewAttached()) {
                    if (!show)
                        NetProgressObservable.getInstance().hide("getMainData");

                    if (AppConfiguration.deviceBeanList == null || !AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                        scale_fourElectrode_dataModel = null;
                    }
                    mActView.get().successGetMainScaleDataFromDB(scacleBeans,
                            scale_fourElectrode_dataModel, show);
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

        // }

    }

    //*************************??????????????????**********************************

    /**
     * ???DB???????????????W516???????????????
     */

    public void getMainW516SleepDataFromDB(boolean show) {
        if (DeviceTypeUtil.isContainWatch()) {
            getDeviceSleepDataFromDB(show, JkConfiguration.DeviceType.WATCH_W516);

        }
    }


    /**
     * ???DB???????????????W311???????????????
     */

    public void getMainW311SleepDataFromDB(boolean show) {
        if (DeviceTypeUtil.isContainBrand()) {
            getDeviceSleepDataFromDB(show, JkConfiguration.DeviceType.BRAND_W311);
        }
    }


    public void getDeviceSleepDataFromDB(boolean show, int currentType) {

        Logger.myLog(TAG,"-------????????????----getDeviceSleepDataFromDB----=currentType="+currentType);

        Observable.create(new ObservableOnSubscribe<SleepMainData>() {
            @Override
            public void subscribe(ObservableEmitter<SleepMainData> emitter) throws Exception {
                SleepMainData sleepMainData = null;
                if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                    if (!TextUtils.isEmpty(AppConfiguration.braceletID)) {
                        sleepMainData = iw311DataModel.getWatchSleepDayLastFourData(AppConfiguration.braceletID);
                    }
                } else if (DeviceTypeUtil.isContainWatch(currentType)) {
                    // W516Model w516Model = new W516Model();
                    if (!TextUtils.isEmpty(AppConfiguration.braceletID)) {
                        sleepMainData = w516Model.getWatchSleepDayLastFourData(AppConfiguration.braceletID);
                    }
                } else if (DeviceTypeUtil.isContainW81(currentType)) {
                    if (!TextUtils.isEmpty(AppConfiguration.braceletID)) {
                        WatchSleepDayData watchSleepDayData = w81DeviceDataModel.getLastSleepData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, true);
                        sleepMainData = new SleepMainData();
                        sleepMainData.setMinute(watchSleepDayData.getTotalSleepTime());
                        sleepMainData.setLastSyncDate(watchSleepDayData.getDateStr());
                    }
                }
                if (sleepMainData == null) {
                    sleepMainData = new SleepMainData();
                    sleepMainData.setMinute(0);
                    sleepMainData.setLastSyncTime(0);
                    sleepMainData.setCompareSleepTime(0);
                }
                emitter.onNext(sleepMainData);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<SleepMainData>(BaseApp.getApp(), false) {
            @Override
            public void onNext(SleepMainData sleepMainData) {
                if (view != null) {
                     Logger.myLog("TAG","--------getMainW311SleepDataFromDB" + sleepMainData.toString());
                    view.successGetMainBraceletSleepFromDB(
                            sleepMainData, show);
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


    //*************************??????????????????**********************************

    /**
     * ???DB????????????W311?????? ?????????????????????????????????
     */


    public void getMainW311DataFromDB(boolean show) {
        if (DeviceTypeUtil.isContainBrand()) {
            getW311StepLastTwoData();
            getW311HrLastTwoData();
            getMainW311SleepDataFromDB(show);
            //getWeekDate(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID, false, JkConfiguration.DeviceType.BRAND_W311);
        }
    }


    /**
     * ???DB????????????W516??????  ????????????????????????
     *
     * @param show
     */
    public void getMainW516DataFromDB(boolean show) {
        //??????W516?????????????????????????????????

        if (DeviceTypeUtil.isContainWatch()) {
            getDeviceStepLastTwoData(JkConfiguration.DeviceType.WATCH_W516);
            getDeviceHrLastTwoData(JkConfiguration.DeviceType.WATCH_W516);
            getDeviceSleepDataFromDB(show, JkConfiguration.DeviceType.WATCH_W516);
            getW557Data();
            if (DeviceTypeUtil.isContainW556OrW812B()) {
                getDeviceBloodPressure();
                getDevcieOxygenData();
                getDevcieOnceHrData();
            }


        }
    }

    public void getmainW81DataFromDb(boolean show) {

        Logger.myLog(TAG," getmainW81DataFromDb DeviceTypeUtil.isContainW81()" + DeviceTypeUtil.isContainW81() + "AppConfiguration.braceletID:" + AppConfiguration.braceletID);

        if (DeviceTypeUtil.isContainW81()) {
            getDeviceStepLastTwoData(JkConfiguration.DeviceType.Watch_W812);
            getDeviceHrLastTwoData(JkConfiguration.DeviceType.Watch_W812);
            getDeviceSleepDataFromDB(show, JkConfiguration.DeviceType.Watch_W812);
            getDeviceBloodPressure();
            getDevcieOxygenData();
            getDevcieOnceHrData();
        }
    }


    /**
     * ???DB????????????Sleep??????
     *
     * @param show
     */
    public void getMainSleepDataFromDB(boolean show) {
        //?????????????????????
        List<Sleep_Sleepace_DataModel> sleep_sleepace_dataModelsByDeviceId = Sleep_Sleepace_DataModelAction
                .findSleep_Sleepace_DataModelsByDeviceId(TokenUtil.getInstance().getPeopleIdStr
                        (BaseApp.getApp()));
        if (sleep_sleepace_dataModelsByDeviceId != null) {
            for (int i = 0; i < sleep_sleepace_dataModelsByDeviceId.size(); i++) {
                Logger.myLog(TAG,"sleep_sleepace_dataModelsByDeviceId == " + sleep_sleepace_dataModelsByDeviceId.get(i)
                        .toString());
            }
        }
        Sleep_Sleepace_DataModel sleep_Sleepace_DataModel = null;
        SleepMainData sleepMainData = null;
        if (sleep_sleepace_dataModelsByDeviceId != null) {
            sleepMainData = new SleepMainData();
            sleep_Sleepace_DataModel = sleep_sleepace_dataModelsByDeviceId.get(0);
            if (sleep_sleepace_dataModelsByDeviceId.size() == 1) {
                //????????????
                Sleep_Sleepace_DataModel sleep_sleepace_dataModel = sleep_sleepace_dataModelsByDeviceId.get(0);
                sleepMainData.setLastSyncTime(sleep_sleepace_dataModel.getTimestamp());
                sleepMainData.setMinute(sleep_sleepace_dataModel.getDeepSleepAllTime());
                sleepMainData.setCompareSleepTime(0);
            } else {
                //???????????????????????????
                Sleep_Sleepace_DataModel sleep_sleepace_dataModel = sleep_sleepace_dataModelsByDeviceId.get(0);
                Sleep_Sleepace_DataModel sleep_sleepace_dataModelLast = sleep_sleepace_dataModelsByDeviceId.get(1);
                sleepMainData.setLastSyncTime(sleep_sleepace_dataModel.getTimestamp());
                sleepMainData.setMinute(sleep_sleepace_dataModel.getDeepSleepAllTime());
                sleepMainData.setCompareSleepTime(sleep_sleepace_dataModel.getDeepSleepAllTime() -
                        sleep_sleepace_dataModelLast.getDeepSleepAllTime());
            }
        }

        if (isViewAttached()) {
            if (!show)
                NetProgressObservable.getInstance().hide("getMainData");
            mActView.get().successGetMainSleepaceDataFromDB(sleep_Sleepace_DataModel,
                    sleepMainData, show);
        }
    }

    /**
     * ??????????????????????????????????????????,?????????????????????????????????????????????????????????
     *
     * @param show
     */
    public void getMainDataFromDB(boolean show) {
        getMainScaleDataFromDB(false, false);
        getMainW311DataFromDB(false);
        getMainW516DataFromDB(false);
        getmainW81DataFromDb(false);

    }

    /**
     * ???????????????????????????,???????????????????????????????????????????????????,??????????????????
     */
    @Override
    public void getSportHomeData() {
        SportRepository.getMainHomeSportData().as(view.bindAutoDispose()).subscribe(new BaseObserver<String>(BaseApp.getApp(), false) {
            @Override
            public void onNext(String resultHomeSportData) {
                NetProgressObservable.getInstance().hide();


                if (isViewAttached()) {
                    view.successSportMainData(resultHomeSportData);
                    //??????????????????
                    SportAcacheUtil.saveSportHomeData(resultHomeSportData);
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
                //???????????????????????????????????????
                String resultHomeSportData = SportAcacheUtil.getSportHomeData();
                if (TextUtils.isEmpty(resultHomeSportData)) {
                    if (isViewAttached()) {
                        view.successSportMainData("0");
                    }
                }

            }
        });
    }

    /**
     * ???Http????????????Scale??????
     *
     * @param show
     */
    public void getMainScaleDataFromHttp(boolean show) {
        /**
         * ???????????????
         */
        CustomRepository<ScaleHistoryData, ScaleParms, BaseUrl, BaseDbPar> mainLoadResposition = new
                CustomRepository<>();
        PostBody<ScaleParms, BaseUrl, BaseDbPar> scaleHistoryData = HistoryParmUtil.getScaleHistoryData(1, 7, show);
        mainLoadResposition.requst(scaleHistoryData)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<ScaleHistoryData>(BaseApp.getApp(), false) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                Logger.myLog(TAG,"ScaleHistoryData ???????????????" + e.toString());
            }

            @Override
            public void onNext(ScaleHistoryData mScaleHistoryData) {
                if (!show) {
                    Logger.myLog(TAG,"ScaleHistoryData ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog(TAG,"ScaleHistoryData ???????????????");
                }
                Logger.myLog(TAG,"????????????Scale?????? == " + mScaleHistoryData.toString());


                if (mScaleHistoryData.getList().size() == 0) {
                    //?????????????????????
                    if (isViewAttached()) {
                        getMainScaleDataFromDB(false, false);
                    }
                } else {
                    //??????????????????????????????????????????
                    UserInfoBean bean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp
                            ()));
                    if (bean == null) {
                        return;
                    }
                    String[] split = bean.getBirthday().split("-");
                    String weight = bean.getWeight().contains(".") ? bean.getWeight().split("\\.")[0] : bean.getWeight();
                    //?????????????????????SDK
                    ISportAgent.getInstance().setUserInfo(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(weight), Float.parseFloat(bean.getHeight()),
                            bean.getGender().equals("Male") ? 1 : 0,
                            TimeUtils.getAge(bean.getBirthday()), bean.getUserId());

                    ScaleHistoryData.ScaleList scaleList = null;
                    Scale_FourElectrode_DataModel scale_fourElectrode_dataModel = null;
                    for (int i = 0; i < mScaleHistoryData.getList().size(); i++) {
                        //???????????????????????????
                        scaleList = mScaleHistoryData.getList().get(i);
//                        private double BWP;//?????????%
                        scale_fourElectrode_dataModel = DeviceDataUtil.getScale_fourElectrode_dataModel(scaleList, bean);
                        Scale_FourElectrode_DataModel scaleFourElectrodeDataModelByDeviceIdAndTimeTamp =
                                Scale_FourElectrode_DataModelAction
                                        .findScaleFourElectrodeDataModelByDeviceIdAndTimeTamp
                                                (scale_fourElectrode_dataModel.getTimestamp(), TokenUtil
                                                        .getInstance().getPeopleIdInt(BaseApp.getApp()));
                        //Logger.myLog("scaleFourElectrodeDataModelByDeviceIdAndTimeTamp: i" + i + "--------" + scaleFourElectrodeDataModelByDeviceIdAndTimeTamp + "------scaleList:" + scaleList);
                        if (scaleFourElectrodeDataModelByDeviceIdAndTimeTamp == null) {
                            ParseData.saveScaleFourElectrodeData(scale_fourElectrode_dataModel);
                        }
                    }
                    getMainScaleDataFromDB(false, false);

                }
            }
        });
    }


    /**
     * ???Http?????????W311?????????
     */
    public void getMainW311DataFromHttp(boolean show) {


        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        /**
         * ??????14??????????????????,?????????????????????????????????
         */


//        post /wristband/wristbandSportDetail/selectByPageSize

        /**
         * ???????????????????????????,???????????????????????????
         */
        CustomRepository<WatchHistoryData, WatchHistoryParms, BaseUrl, BaseDbPar> watchPageHistory = new
                CustomRepository<>();
        PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> watchPageHistoryDataList = HistoryParmUtil
                .getPageBraceletHistoryByTimeTampData(TimeUtils.getTodayyyyyMMdd(), 1, 2, JkConfiguration.WatchDataType.HEARTRATE, AppConfiguration.braceletID);
        watchPageHistory.requst(watchPageHistoryDataList)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryData>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryData watchHistoryData) {
                if (!show) {
                    Logger.myLog(TAG,"WatchHistoryData ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog(TAG,"WatchHistoryData ???????????????");
                }

                Logger.myLog(TAG,"getMainW311DataFromHttp ????????????HR 2???WatchHistoryNList?????? == " + watchHistoryData.toString());
                if (watchHistoryData.getList() == null || (watchHistoryData.getList() != null && watchHistoryData
                        .getList().size() == 0)) {
                    //??????????????????
                } else {
                    //??????????????????
                    List<WatchHistoryNBean> list = watchHistoryData.getList();
                    for (int i = 0; i < list.size(); i++) {
                        WatchHistoryNBean watchHistoryNBean = list.get(i);
                        W311DeviceDataUtil.getBracelet_W311_24HDataModel(watchHistoryNBean, JkConfiguration.WatchDataType.HEARTRATE);
                    }
                }

                if (isViewAttached()) {
                    mActView.get().successWatchHistoryDataFormHttp(false, JkConfiguration.WatchDataType.HEARTRATE);
                }
                if (isViewAttached()) {

                    getDeviceHrLastTwoData(JkConfiguration.DeviceType.BRAND_W311);
                    // mActView.get().successWatchHeartRateHistoryDataFormHttp(JkConfiguration.DeviceType.BRAND_W311);
                }
            }
        });

        //14??????step
        PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> watchPageStepHistoryDataList = HistoryParmUtil
                .getPageBraceletHistoryByTimeTampData(TimeUtils.getTodayyyyyMMdd(), 1, 14, JkConfiguration.WatchDataType.STEP, AppConfiguration.braceletID);
        watchPageHistory.requst(watchPageStepHistoryDataList)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryData>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryData watchHistoryData) {
                if (!show) {
                    Logger.myLog(TAG,"WatchHistoryData ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog(TAG,"WatchHistoryData ???????????????");
                }

                // Logger.myLog("getMainW311DataFromHttp ????????????Step 14???WatchHistoryNList?????? == " + watchHistoryData.toString());
                if (watchHistoryData.getList() == null || (watchHistoryData.getList() != null && watchHistoryData
                        .getList().size() == 0)) {
                    //??????????????????
                } else {
                    //??????????????????
                    List<WatchHistoryNBean> list = watchHistoryData.getList();
                    for (int i = 0; i < list.size(); i++) {
                        WatchHistoryNBean watchHistoryNBean = list.get(i);
                        W311DeviceDataUtil.getBracelet_W311_24HDataModel(watchHistoryNBean, JkConfiguration.WatchDataType.STEP);
                    }
                }
                if (isViewAttached()) {
                    mActView.get().successWatchHistoryDataFormHttp(false, JkConfiguration.WatchDataType.STEP);
                }
            }
        });
        /**
         * ???????????????????????????,???????????????????????????
         */
        PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> watchPageSleepHistoryDataList = HistoryParmUtil
                .getPageBraceletHistoryByTimeTampData(TimeUtils.getTodayyyyyMMdd(), 1, 4, JkConfiguration.WatchDataType.SLEEP, AppConfiguration.braceletID);
        watchPageHistory.requst(watchPageSleepHistoryDataList)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryData>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryData watchHistoryData) {
                if (!show) {
                    Logger.myLog(TAG,"WatchHistoryData ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog(TAG,"WatchHistoryData ???????????????");
                }
                // Logger.myLog("getMainW311DataFromHttp ???????????????????????????,??????????????????????????? == " + watchHistoryData.toString());
                if (watchHistoryData.getList() == null || (watchHistoryData.getList() != null && watchHistoryData
                        .getList().size() == 0)) {
                    //??????????????????
                } else {
                    //??????????????????
                    List<WatchHistoryNBean> list = watchHistoryData.getList();
                    for (int i = 0; i < list.size(); i++) {
                        WatchHistoryNBean watchHistoryNBean = list.get(i);
                        W311DeviceDataUtil.getBracelet_W311_24HDataModel(watchHistoryNBean, JkConfiguration.WatchDataType.SLEEP);
                    }
                }
                if (isViewAttached()) {
                    mActView.get().successWatchHistoryDataFormHttp(false, JkConfiguration.WatchDataType.SLEEP);
                }
            }
        });
        DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.STEP, JkConfiguration.DeviceType.BRAND_W311, BaseApp.getApp());
        DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.SLEEP, JkConfiguration.DeviceType.BRAND_W311, BaseApp.getApp());
        DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.HEARTRATE, JkConfiguration.DeviceType.BRAND_W311, BaseApp.getApp());
    }

    /**
     * ??????W81???????????????
     */

    W81DataPresenter w81DataPresenter;

    public void getMainW81DataFromHttp(boolean show) {

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);


        w81DataPresenter.getW81NumStep(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "0", "1");
        w81DataPresenter.getW81NumHr(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "1", "1");
        w81DataPresenter.getW81NumSleep(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "2", "2");


        w81DataPresenter.getBloodPressure();
        w81DataPresenter.getNumOxyGen();

        w81DataPresenter.getNumOnceHr();
        getTodaySumExc(JkConfiguration.DeviceType.Watch_W812);
        //0 ????????? 1 ?????? 2 ??????
        w81DataPresenter.getW81MonthStep(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "0", instance.getTimeInMillis());
        w81DataPresenter.getW81MonthHr(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "1", instance.getTimeInMillis());
        w81DataPresenter.getW81MothSleep(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "2", instance.getTimeInMillis());

    }


    public void getTodaySumExc(int deviceTpe) {
        getTodaySum(deviceTpe);
    }

    public void getTodaySum(int deviceType) {
        if (TextUtils.isEmpty(AppConfiguration.braceletID)) {
            return;
        }
        getExerciseTodaySum(deviceType);
    }

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


    /**
     * ???Http????????????W516??????
     *
     * @param show
     */
    public void getMainW516DataFromHttp(boolean show) {

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        /**
         * ??????14??????????????????,?????????????????????????????????
         */
        CustomRepository<WatchHistoryNList, WatchHistoryParms, BaseUrl, BaseDbPar> watchHistory = new
                CustomRepository<>();
        PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> watchHistoryDataList = HistoryParmUtil
                .get14DayWatchHistoryByTimeTampData(14, JkConfiguration.WatchDataType.STEP, AppConfiguration.braceletID);
        watchHistory.requst(watchHistoryDataList)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryNList>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryNList watchHistoryNList) {
                if (!show) {
                    Logger.myLog(TAG,"getMainW516DataFromHttp WatchHistoryNList ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog(TAG,"getMainW516DataFromHttp WatchHistoryNList ???????????????");
                }
                //  Logger.myLog("getMainW516DataFromHttp ????????????14??? Step WatchHistoryNList?????? == " + watchHistoryNList.toString());
                if (watchHistoryNList.getList() == null || (watchHistoryNList.getList() != null && watchHistoryNList
                        .getList().size() == 0)) {
                    //??????????????????
                } else {
                    //??????????????????
                    List<WatchHistoryNBean> list = watchHistoryNList.getList();
                    for (int i = 0; i < list.size(); i++) {
                        WatchHistoryNBean watchHistoryNBean = list.get(i);
                        DeviceDataUtil.getWatch_W516_24HDataModel(watchHistoryNBean, JkConfiguration.WatchDataType.STEP);
                    }
                }
                if (isViewAttached()) {
                    mActView.get().successWatchHistoryDataFormHttp(show, JkConfiguration.WatchDataType.STEP);
                }
            }
        });

//        post /wristband/wristbandSportDetail/selectByPageSize

        /**
         * ???????????????????????????,???????????????????????????
         */
        CustomRepository<WatchHistoryData, WatchHistoryParms, BaseUrl, BaseDbPar> watchPageHistory = new
                CustomRepository<>();
        PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> watchPageHistoryDataList = HistoryParmUtil
                .getPageWatchHistoryByTimeTampData(TimeUtils.getTodayyyyyMMdd(), 1, 2, JkConfiguration.WatchDataType.HEARTRATE, AppConfiguration.braceletID);
        watchPageHistory.requst(watchPageHistoryDataList)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryData>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryData watchHistoryData) {
                if (!show) {
                    Logger.myLog(TAG,"WatchHistoryData ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog(TAG,"WatchHistoryData ???????????????");
                }

                // Logger.myLog("getMainW516DataFromHttp ????????????HR 2???WatchHistoryNList?????? == " + watchHistoryData.toString());
                if (watchHistoryData.getList() == null || (watchHistoryData.getList() != null && watchHistoryData
                        .getList().size() == 0)) {
                    //??????????????????
                } else {
                    //??????????????????
                    List<WatchHistoryNBean> list = watchHistoryData.getList();
                    //Logger.myLog("getMainW516DataFromHttp ????????????HR 2???WatchHistoryNList?????? ==" + list.toString());
                    for (int i = 0; i < list.size(); i++) {
                        WatchHistoryNBean watchHistoryNBean = list.get(i);
                        DeviceDataUtil.getWatch_W516_24HDataModel(watchHistoryNBean, JkConfiguration.WatchDataType.HEARTRATE);
                    }
                }
                if (isViewAttached()) {
                    //mActView.get().successWatchHeartRateHistoryDataFormHttp();
                    mActView.get().successWatchHistoryDataFormHttp(show, JkConfiguration.WatchDataType.HEARTRATE);
                    //getDeviceHrLastTwoData(JkConfiguration.DeviceType.WATCH_W516);
                }
            }
        });
        /**
         * ???????????????????????????,???????????????????????????
         */

        CustomRepository<WatchHistoryData, WatchHistoryParms, BaseUrl, BaseDbPar> watchPageSleepHistory = new
                CustomRepository<>();
        PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> watchPageSleepHistoryDataList = HistoryParmUtil
                .getPageWatchHistoryByTimeTampData(TimeUtils.getTodayyyyyMMdd(), 1, 2, JkConfiguration.WatchDataType.SLEEP, AppConfiguration.braceletID);
        watchPageSleepHistory.requst(watchPageSleepHistoryDataList)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryData>(BaseApp.getApp(), false) {
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
            public void onNext(WatchHistoryData watchHistoryData) {
                if (!show) {
                    Logger.myLog(TAG,"WatchHistoryData ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog(TAG,"WatchHistoryData ???????????????");
                }

                if (watchHistoryData.getList() == null || (watchHistoryData.getList() != null && watchHistoryData
                        .getList().size() == 0)) {
                    //??????????????????
                } else {
                    //??????????????????
                    List<WatchHistoryNBean> list = watchHistoryData.getList();
                    //  Logger.myLog("getMainW516DataFromHttp ??????????????????????????? ==" + list);
                    for (int i = 0; i < list.size(); i++) {
                        WatchHistoryNBean watchHistoryNBean = list.get(i);
                        Logger.myLog("getMainW516DataFromHttp ??????????????????????????? == watchHistoryNBean:" + watchHistoryNBean.getDateStr() + ",detail:" + watchHistoryNBean.getSleepDetailArray());
                        //
                        DeviceDataUtil.getWatch_W516_24HDataModel(watchHistoryNBean, JkConfiguration.WatchDataType.SLEEP);
                    }
                }
                if (isViewAttached()) {
                    // getMainW516DataFromDB(false);
                    mActView.get().successWatchHistoryDataFormHttp(show, JkConfiguration.WatchDataType.SLEEP);
                    // getDeviceSleepDataFromDB(show, JkConfiguration.DeviceType.WATCH_W516);
                    // mActView.get().successWatchHeartRateHistoryDataFormHttp();
                }
            }
        });


        DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.STEP, JkConfiguration.DeviceType.WATCH_W516, BaseApp.getApp());
        DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.SLEEP, JkConfiguration.DeviceType.WATCH_W516, BaseApp.getApp());
        DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.HEARTRATE, JkConfiguration.DeviceType.WATCH_W516, BaseApp.getApp());
        getW557http();
        if (DeviceTypeUtil.isContainW556OrW812B()) {
            w81DataPresenter.getBloodPressure();
            w81DataPresenter.getNumOxyGen();
            w81DataPresenter.getNumOnceHr();
        }

    }


    public void getW557http() {
        //????????????W55??????????????????????????????????????????
        if (TextUtils.isEmpty(AppConfiguration.braceletID)) {
            return;
        }
        if (AppConfiguration.braceletID.contains("W557")) {
            w81DataPresenter.getTempData();
        }

    }


    public void getW557Data() {
        //????????????W55??????????????????????????????????????????
        if (TextUtils.isEmpty(AppConfiguration.braceletID)) {
            return;
        }
        if (AppConfiguration.braceletID.contains("W557")) {
            getTempData();
        }

    }


    @Override
    public void scan(int type, boolean isScale) {


        if (deviceOptionImple != null) {
            deviceOptionImple.scan(type, isScale, new DeviceResultCallBack() {
                @Override
                public void onScanResult(ArrayList<BaseDevice> mBleDevices) {

                }

                @Override
                public void onScanResult(Map<String, BaseDevice> listDevicesMap) {
                    if (isViewAttached()) {
                        mActView.get().onScan(listDevicesMap);
                    }
                }


                @Override
                public void onScanFinish() {
                    if (isViewAttached()) {
                        mActView.get().onScanFinish();
                    }
                }
            });

        }
    }

    @Override
    public void connectDevice(BaseDevice device, boolean show, boolean isConnectByUser) {
        if (deviceOptionImple != null) {
            deviceOptionImple.connect(device, show, isConnectByUser);
        }
    }

    @Override
    public void connectDevice(String currentName, String watchMac, int deviceType, boolean show, boolean isConnectByUser) {
        BaseDevice device = null;
        switch (deviceType) {
            case JkConfiguration.DeviceType.BRAND_W311: {
                device = new W311Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Brand_W520: {
                device = new W520Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.BRAND_W307J: {
                device = new W307JDevice(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.WATCH_W516: {
                device = new Watch516Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W812: {
                device = new W812Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Brand_W814: {
                device = new W814Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W813: {
                device = new W813Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W819: {
                device = new W819Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W910: {
                device = new W910Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W556: {
                device = new W526Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W817: {
                device = new W817Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W557: {
                device = new W557Device(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W812B: {
                device = new W812BDevice(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W560B: {
                device = new W560BDevice(currentName, watchMac);
            }
            break;
            case JkConfiguration.DeviceType.Watch_W560: {
                device = new W560Device(currentName, watchMac);
            }
            break;
        }
        if (deviceOptionImple != null) {
            deviceOptionImple.connect(device, show, isConnectByUser);
        }
    }


    @Override
    public void cancelScan() {
        if (deviceOptionImple != null) {
            deviceOptionImple.cancelScan();
        }
    }


    /**
     * ???Http????????????Sleepace??????
     *
     * @param show
     */
    public void getMainSleepaceDataDataFromHttp(boolean show) {
        /**
         * ???????????????
         */
        CustomRepository<SleepHistoryData, ScaleParms, BaseUrl, BaseDbPar> mainLoadSleepResposition = new
                CustomRepository<>();
        PostBody<ScaleParms, BaseUrl, BaseDbPar> sleepHistoryData = HistoryParmUtil.getSleepHistoryData(1, 2);
        mainLoadSleepResposition.requst(sleepHistoryData)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<SleepHistoryData>(BaseApp.getApp(), false) {
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
            public void onNext(SleepHistoryData mSleepHistoryData) {
                if (!show) {
                    Logger.myLog("SleepHistoryData ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog("SleepHistoryData ???????????????");
                }
                Logger.myLog("????????????Sleep?????? == " + mSleepHistoryData.toString());
                SleepMainData sleepMainData = null;
                SleepHistoryData.SleepList sleepList = null;
                Sleep_Sleepace_DataModel sleep_sleepace_dataModel = null;
                if (mSleepHistoryData.getList().size() == 0) {
                    //?????????????????????
                    if (isViewAttached()) {
                        mActView.get().successSleepHistoryDataFormHttp(sleepMainData, null);
                    }
                } else {
                    sleepMainData = new SleepMainData();
                    if (mSleepHistoryData.getList().size() == 1) {
                        sleepList = mSleepHistoryData.getList().get(0);
                        sleep_sleepace_dataModel = DeviceDataUtil.getSleep_Sleepace_DataModel(sleepList);
                        sleepMainData.setLastSyncTime(Long.parseLong(sleepList.getTimestamp()) / 1000);
                        sleepMainData.setMinute(sleepList.getDeepSleepDuration());
                        sleepMainData.setCompareSleepTime(0);
                    } else if (mSleepHistoryData.getList().size() == 2) {
                        //????????????
                        sleepList = mSleepHistoryData.getList().get(0);
                        sleep_sleepace_dataModel = DeviceDataUtil.getSleep_Sleepace_DataModel(sleepList);
                        //?????????
                        SleepHistoryData.SleepList sleepListLast = mSleepHistoryData.getList().get(1);
                        sleepMainData.setLastSyncTime(Long.parseLong(sleepList.getTimestamp()) / 1000);
                        sleepMainData.setMinute(sleepList.getDeepSleepDuration());
                        sleepMainData.setCompareSleepTime(sleepList.getDeepSleepDuration() -
                                sleepListLast.getDeepSleepDuration());
                    }
                    Sleep_Sleepace_DataModel sleep_sleepace_dataModelByDeviceIdAndTimeTamp =
                            Sleep_Sleepace_DataModelAction
                                    .findSleep_Sleepace_DataModelByDeviceIdAndTimeTamp
                                            (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),
                                                    sleep_sleepace_dataModel.getTimestamp());
                    //????????????????????????????????????????????????
                    if (sleep_sleepace_dataModelByDeviceIdAndTimeTamp == null) {
                        ParseData.saveSleep_Sleepace_DataModel(sleep_sleepace_dataModel);
                    }
                    if (isViewAttached()) {
                        mActView.get().successSleepHistoryDataFormHttp(sleepMainData, sleep_sleepace_dataModel);
                    }
                }
            }
        });
        /**
         * ???????????????????????????????????????????????????
         */
        CustomRepository<SleepHistoryNList, SleepHistoryParms, BaseUrl, BaseDbPar> sleepHistory = new
                CustomRepository<>();
        //??????????????????0????????????,?????????
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        PostBody<SleepHistoryParms, BaseUrl, BaseDbPar> sleepHistoryDataList = HistoryParmUtil.getSleepHistoryByTimeTampData(instance.getTimeInMillis());
        sleepHistory.requst(sleepHistoryDataList)
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<SleepHistoryNList>(BaseApp.getApp(), false) {
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
            public void onNext(SleepHistoryNList mSleepHistoryData) {
                if (!show) {
                    Logger.myLog("SleepHistoryNList ????????????");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog("SleepHistoryNList ???????????????");
                }

                Logger.myLog("????????????SleepHistoryNList?????? == " + mSleepHistoryData.toString());
                if (mSleepHistoryData.getList() == null || (mSleepHistoryData.getList() != null && mSleepHistoryData.getList().size() == 0)) {
                    //??????????????????
                } else {
                    //??????????????????
                    List<SleepHistoryNBean> list = mSleepHistoryData.getList();
                    for (int i = 0; i < list.size(); i++) {
                        SleepHistoryNBean sleepHistoryNBean = list.get(i);
                        Sleep_Sleepace_DataModel sleep_sleepace_dataModel1 = DeviceDataUtil.getSleep_Sleepace_DataModel1
                                (sleepHistoryNBean);
                        Sleep_Sleepace_DataModel sleep_sleepace_dataModelByDeviceIdAndTimeTamp =
                                Sleep_Sleepace_DataModelAction
                                        .findSleep_Sleepace_DataModelByDeviceIdAndTimeTamp
                                                (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp
                                                                ()),
                                                        sleep_sleepace_dataModel1.getTimestamp());
                        //????????????????????????????????????????????????
                        if (sleep_sleepace_dataModelByDeviceIdAndTimeTamp == null) {
                            ParseData.saveSleep_Sleepace_DataModel(sleep_sleepace_dataModel1);
                        }
                    }
                }
            }
        });
    }


    //*****************************************?????????****************************************//

    FragmentDataView view;
    DeviceOptionImple deviceOptionImple;
    private int mCurrentIndex;
    private List<Scale_FourElectrode_DataModel> mAllScaleUnUpdate;
    private int mCurrentSleepIndex;
    private List<Sleep_Sleepace_DataModel> mSleep_Sleepace_DataModel;
    private int mCurrentWatchIndex;
    private List<Watch_W516_24HDataModel> mWatch_W516_24HDataModel;


    public void onRespondError(String message) {
        ToastUtils.showToast(context, message);
    }

    /**
     * ??????????????????,???????????????????????????
     *
     * @param
     */
    public void updateScaleHistoryData(boolean show) {
        mAllScaleUnUpdate = Scale_FourElectrode_DataModelAction.getAllUnUpdate
                (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        if (mAllScaleUnUpdate != null) {
            mCurrentIndex = 0;
            //???????????????????????????
            updateScaleHistory(mAllScaleUnUpdate.get(mCurrentIndex), show);
        }
    }

    private void updateScaleHistory(Scale_FourElectrode_DataModel scale_FourElectrode_DataModel, boolean show) {
        Logger.myLog("????????? updateScaleHistory");
        //mUpdateReportModelImp.updateReport(mScaleCalculateBean);
        ScaleRepository.requst(scale_FourElectrode_DataModel).as(view.bindAutoDispose()).subscribe(new BaseObserver<UpdateSuccessBean>(BaseApp.getApp(), false) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                if (isViewAttached()) {
                    mActView.get().onRespondError(e.message);
                }
            }

            @Override
            public void onNext(UpdateSuccessBean updateSuccessBean) {
                //??????????????????reportId
                scale_FourElectrode_DataModel.setReportId(updateSuccessBean.getPublicId());
                BleAction.getScale_FourElectrode_DataModelDao().update(scale_FourElectrode_DataModel);
                mCurrentIndex++;
                if (mCurrentIndex <= mAllScaleUnUpdate.size() - 1) {
                    updateScaleHistory(mAllScaleUnUpdate.get(mCurrentIndex), show);
                } else {
                    if (isViewAttached()) {
                        mActView.get().updateScaleHistoryDataSuccess(updateSuccessBean);
                    }
                }

            }
        });
    }


    @Override
    public void updateSportData(Wristbandstep mWristbandstep) {
        //  model.updateSportData(mWristbandstep);

        WatchRepository.requst(mWristbandstep).as(view.bindAutoDispose()).subscribe(new BaseObserver<UpdateWatchResultBean>(BaseApp.getApp(), false) {
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
//                if (isViewAttached()) {
//                    mActView.get().updateSuccess();
//                }
            }
        });
    }

    @Override
    public void updateSleepHistoryData(boolean show) {
        if (!(App.appType() == App.httpType)) {
            if (isViewAttached()) {
//                mActView.get().updateSleepHistoryDataSuccess(null);
            }
        } else {
            List<Sleep_Sleepace_DataModel> sleep_sleepace_dataModels =
                    Sleep_Sleepace_DataModelAction
                            .findSleep_Sleepace_DataModelByDeviceIdAndTimeTamp1
                                    (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),
                                            App.getSleepBindTime());
            Logger.myLog("getSleepBindTime == " + App.getSleepBindTime());
            if (sleep_sleepace_dataModels != null) {
                mSleep_Sleepace_DataModel = sleep_sleepace_dataModels;
                mCurrentSleepIndex = 0;
                updateSleepHistory(show);
            } else {
//                if (isViewAttached())
//                    mActView.get().updateSleepHistoryDataSuccess(null);
            }
        }
        //model.updateSleepHistoryData(sleepHistoryDataResult, name);
    }


    @Override
    public void updateWatchHistoryData(boolean show) {
        if (!(App.appType() == App.httpType)) {
//            if (isViewAttached()) {
//                mActView.get().updateWatchHistoryDataSuccess(null);
//            }
        } else {
            //???????????????1440??????
            List<Watch_W516_24HDataModel> dataList = Watch_W516_24HDataModelAction
                    .findWatch_W516_Watch_W516_24HDataModelByTimeTamp
                            (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),
                                    App.getWatchBindTime());
            if (dataList != null) {
                mWatch_W516_24HDataModel = dataList;
                mCurrentWatchIndex = 0;
                updateWatchHistory(show);
                for (int i = 0; i < dataList.size(); i++) {
                    Logger.myLog("??????Watch data == " + dataList.get(i).toString());
                }
            } else {
//                if (isViewAttached())
//                    mActView.get().updateWatchHistoryDataSuccess(null);
            }
        }
        //model.updateSleepHistoryData(sleepHistoryDataResult, name);
    }

    @Override
    public void syncUdateTime() {
        MainResposition<BindDeviceList, BaseParms, BaseUrl, ProgressShowParms> mainResposition = new MainResposition<>();
        InitCommonParms<BaseParms, BaseUrl, ProgressShowParms> commonParms = new InitCommonParms<>();
        BaseParms parms = new BaseParms();
        parms.setUserId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        parms.setInterfaceId("0");
        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.BASIC;
        baseUrl.url2 = JkConfiguration.Url.DEVICE;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYUSERID;
        baseUrl.userid = TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp());
        ProgressShowParms deviceIdParms = new ProgressShowParms();
        deviceIdParms.requestCode = RequestCode.Request_getBindDeviceList;
        deviceIdParms.show = false;
        //?????????????????????????????????????????????????????????
        //?????????????????????????????????
        mainResposition.requst(commonParms.setPostBody(false).setParms(parms)
                .setBaseUrl(baseUrl)
                .setBaseDbParms(deviceIdParms).setType(JkConfiguration.RequstType
                        .GET_BIND_DEVICELIST)
                .getPostBody())
                .as(view.bindAutoDispose()).subscribe(new BaseObserver<BindDeviceList>(BaseApp.getApp(), false) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

                //?????????????????????
                Logger.myLog("getDeviceList???" + e.toString());
            }

            @Override
            public void onNext(BindDeviceList bean) {
                //???????????????????????????????????????????????????
                if (bean != null && bean.list != null) {
                    DeviceBean deviceBean;
                    BindDevice bindDevice;
                    /*if (bean.list.size() > 0) {
                        //?????????????????????????????????

                    }*/
                    for (int i = 0; i < bean.list.size(); i++) {
                        Logger.myLog("deviceBeanHashMap" + bean.list.get(i).toString());
                        // 0:?????????1???????????????2????????????
                        deviceBean = new DeviceBean();
                        bindDevice = bean.list.get(i);
                        deviceBean.deviceType = bindDevice.getDeviceTypeId();
                        deviceBean.deviceName = bindDevice.getDevicetName();
                        //????????????????????????mac????????????deviceId???
                        if (bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.BODYFAT) {
                            deviceBean.mac = Utils.resetDeviceMac(bindDevice.getDeviceId());
                        } else {
                            deviceBean.mac = bindDevice.getMac();
                            App.setDeviceBindTime(bindDevice.getCreateTime());
                            // BaseManager.deviceBindTime = bindDevice.getCreateTime();
                            ISportAgent.getInstance().setDeviceBindTime(bindDevice.getCreateTime());
                            Logger.myLog("requstUpgradeExerciseData App.getWatchBindTime():" + bindDevice.getCreateTime());
                        }
                        // Logger.myLog("BleSPUtils.WATCH_LAST_SYNCTIME"+BleSPUtils.getString(BaseApp.getApp(),BleSPUtils.WATCH_LAST_SYNCTIME,"2018-0201"));


                        Logger.myLog("BleSPUtils.WATCH_LAST_SYNCTIME" + bindDevice.getCreateTime() + ":" + DateUtil.dataToString(new Date(bindDevice.getCreateTime()), "yyyy-MM-dd") + "bindDevice.getTimestamp()" + bindDevice.getTimestamp() + ":" + DateUtil.dataToString(new Date(bindDevice.getTimestamp()), "yyyy-MM-dd") + "bean.list.get(i).getDeviceTypeId():" + bean.list.get(i).getDeviceTypeId());
                        //createTime????????????????????????timestamp?????????????????????????????????????????????????????????   ???????????????????????????
                        if (bindDevice.getCreateTime() > bindDevice.getTimestamp() &&
                                bindDevice.getCreateTime() - bindDevice.getTimestamp() > 24 * 60 * 60 * 1000) {
                            if (bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.BRAND_W311 || bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.Brand_W520) {
                                deviceBean.timeTamp = bindDevice.getCreateTime();
                            } else {
                                deviceBean.timeTamp = bindDevice.getCreateTime() - 24 * 60 * 60 * 1000;
                            }
                        } else {
                            if (bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.BRAND_W311 || bindDevice.getDeviceTypeId() == JkConfiguration.DeviceType.Brand_W520) {
                                deviceBean.timeTamp = deviceBean.timeTamp + 24 * 60 * 60 * 1000;
                                if (deviceBean.timeTamp > System.currentTimeMillis()) {
                                    deviceBean.timeTamp = deviceBean.timeTamp - 24 * 60 * 60 * 1000;
                                }
                            } else {
                                //W516???W556???W557???W812B
                                deviceBean.timeTamp = bindDevice.getTimestamp();
                            }
                        }
                        int deviceType = bean.list.get(i).getDeviceTypeId();
                        switch (deviceType) {
                            case JkConfiguration.DeviceType.BODYFAT:
                                break;
                            case JkConfiguration.DeviceType.SLEEP:
                                break;

                            case JkConfiguration.DeviceType.BRAND_W311:
                            case JkConfiguration.DeviceType.Brand_W520:

                                //SyncCacheUtils.clearFirstBindW311(BaseApp.getApp());
                                BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_SYNCTIME, DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd"));
                                BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd"));
                                break;
                            default:
                                App.setWatchBindTime(deviceBean.timeTamp);
                                BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd"));
                                break;

                        }
                        // TODO: 2019/1/25 ?????????????????????????????????????????????,???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        //??????????????????????????????????????????????????????
                        //?????????????????????????????????
                    }
                }

            }
        });
    }


    private void updateSleepHistory(boolean show) {
        Sleep_Sleepace_DataModel sleep_sleepace_dataModel = mSleep_Sleepace_DataModel.get(mCurrentSleepIndex);
        SleepRepository.requst(sleep_sleepace_dataModel)
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<UpdateSuccessBean>(BaseApp.getApp(), false) {
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
                        if (!show)
                            NetProgressObservable.getInstance().hide();

                        if (isViewAttached()) {
                            Sleep_Sleepace_DataModelDao sleep_sleepace_dataModelDao = BleAction
                                    .getSleep_Sleepace_DataModelDao();
                            sleep_sleepace_dataModel.setReportId(updateSleepReportBean.getPublicId());
                            sleep_sleepace_dataModelDao.update(sleep_sleepace_dataModel);
                            mCurrentSleepIndex++;
                            if (mCurrentSleepIndex > mSleep_Sleepace_DataModel.size() - 1) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent
                                        .UPDATE_SLEEP_DATA_SUCCESS));
                                mActView.get().updateSleepHistoryDataSuccess(updateSleepReportBean);
                            } else {
                                updateSleepHistory(show);
                            }
                        }
                    }
                });
    }


    private void updateWatchHistory(boolean show) {
        Watch_W516_24HDataModel watch_w516_24HDataModel = mWatch_W516_24HDataModel.get(mCurrentWatchIndex);
        Logger.myLog("updateWatchHistory == ????????????" + mCurrentWatchIndex + " watch_w516_24HDataModel == " + watch_w516_24HDataModel.toString());
        WatchRepository.requst(watch_w516_24HDataModel)
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<UpdateSuccessBean>(BaseApp.getApp(), false) {
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
                        if (!show)
                            NetProgressObservable.getInstance().hide();

                        Logger.myLog("updateWatchHistory == ???????????? UpdateSuccessBean == " + updateSleepReportBean.toString());
                        if (isViewAttached()) {
                            Watch_W516_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                                    .getWatch_W516_24HDataModelDao();
                            watch_w516_24HDataModel.setReportId(updateSleepReportBean.getPublicId());
                            watch_w516_24HDataModelDao.update(watch_w516_24HDataModel);
                            App.setWatchBindTime(updateSleepReportBean.getTimestamp());
                            mCurrentWatchIndex++;
                            if (mCurrentWatchIndex > mWatch_W516_24HDataModel.size() - 1) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent
                                        .UPDATE_WATCH_DATA_SUCCESS));
                                mActView.get().updateWatchHistoryDataSuccess(updateSleepReportBean);
                            } else {
                                updateWatchHistory(show);
                            }
                        }
                    }
                });
    }

    public void getDeviceCurrentDaySleepData(String dateStr, String deviceId, int deviceType) {
        Logger.myLog("getWatchDayData");
        Observable.create(new ObservableOnSubscribe<WatchSleepDayData>() {
            @Override
            public void subscribe(ObservableEmitter<WatchSleepDayData> emitter) throws Exception {
                if (deviceType == JkConfiguration.DeviceType.BRAND_W311) {
                    WatchSleepDayData watchSleepDayData = iw311DataModel.getWatchSleepDayData(dateStr, deviceId);
                    if (watchSleepDayData != null) {
                        emitter.onNext(watchSleepDayData);
                    } else {
                        emitter.onNext(new WatchSleepDayData());
                    }
                } else if (deviceType == JkConfiguration.DeviceType.WATCH_W516) {
                    WatchSleepDayData watchSleepDayData = w516Model.getWatchSleepDayData(dateStr, deviceId);
                    if (watchSleepDayData != null) {
                        emitter.onNext(watchSleepDayData);
                    } else {
                        emitter.onNext(new WatchSleepDayData());
                    }
                }
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<WatchSleepDayData>(BaseApp.getApp(), false) {
            @Override
            public void onNext(WatchSleepDayData watchSleepDayData) {
                if (view != null) {
                    view.successDaySleepData(watchSleepDayData, deviceType);
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
                    view.successDaySleepData(null, deviceType);
                }
            }
        });
    }


    public void getDeviceCurrentDayHrDetailData(String strDate, int currentType) {
        Observable.create(new ObservableOnSubscribe<WristbandHrHeart>() {
            @Override
            public void subscribe(ObservableEmitter<WristbandHrHeart> emitter) throws Exception {
                WristbandHrHeart wristbandstep;
                if (DeviceTypeUtil.isContainWatch(currentType)) {
                    wristbandstep = w516Model.getDayHrData(strDate);
                } else if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                    wristbandstep = iw311DataModel.getDayHrData(strDate, AppConfiguration.braceletID);
                } else {
                    wristbandstep = new WristbandHrHeart();
                }
                emitter.onNext(wristbandstep);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<WristbandHrHeart>(BaseApp.getApp()) {
            @Override
            public void onNext(WristbandHrHeart wristbandHrHeart) {
                if (view != null) {
                    view.successDayHrData(wristbandHrHeart, currentType);
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


    public void getDeviceCurerntDayData(String userid, String strDate, String deviceId, int currentType) {
        Observable.create(new ObservableOnSubscribe<Wristbandstep>() {
            @Override
            public void subscribe(ObservableEmitter<Wristbandstep> emitter) throws Exception {
                Wristbandstep wristbandstep = null;
                boolean isCurrentDay = true;
                Calendar calendar = Calendar.getInstance();
                if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                    wristbandstep = iw311DataModel.getW311SportData(userid, strDate, deviceId);
                    if (isCurrentDay) {
                        Bracelet_W311_RealTimeData bracelet_w311_realTimeData = iw311DataModel.getRealTimeData(userid, deviceId);
                        if (bracelet_w311_realTimeData != null) {
                            wristbandstep.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                            wristbandstep.setStepKm(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                            wristbandstep.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));

                        }
                        wristbandstep.setMothAndDay(strDate);
                    }

                } else if (DeviceTypeUtil.isContainWatch(currentType)) {
                    wristbandstep = w516Model.getWatchDayData(strDate);
                    if (true) {
                        WatchRealTimeData bracelet_w311_realTimeData = w516Model.getRealWatchData(deviceId);
                        if (bracelet_w311_realTimeData != null) {
                            wristbandstep.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                            wristbandstep.setStepKm(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                            wristbandstep.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                        }
                        wristbandstep.setMothAndDay(strDate);
                    }
                } else {
                    wristbandstep = new Wristbandstep();
                }
                emitter.onNext(wristbandstep);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Wristbandstep>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Wristbandstep wristbandstep) {
                if (view != null) {
                    view.successDaySportData(wristbandstep);
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
                    view.successDaySportData(null);
                }

            }

        });
        if (DeviceTypeUtil.isContainW55X()) {
            getTodaySumExc(JkConfiguration.DeviceType.Watch_W556);
        }


    }


}
