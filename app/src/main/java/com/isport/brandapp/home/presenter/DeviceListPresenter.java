package com.isport.brandapp.home.presenter;

import android.util.Log;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.DeviceTypeTableAction;
import com.isport.blelibrary.db.action.scale.Scale_FourElectrode_DataModelAction;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.scale.Scale_FourElectrode_DataModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.S002BDevice;
import com.isport.blelibrary.deviceEntry.impl.W307JDevice;
import com.isport.blelibrary.deviceEntry.impl.W311Device;
import com.isport.blelibrary.deviceEntry.impl.W520Device;
import com.isport.blelibrary.deviceEntry.impl.W526Device;
import com.isport.blelibrary.deviceEntry.impl.W557Device;
import com.isport.blelibrary.deviceEntry.impl.W560BDevice;
import com.isport.blelibrary.deviceEntry.impl.W560Device;
import com.isport.blelibrary.deviceEntry.impl.W7018Device;
import com.isport.blelibrary.deviceEntry.impl.W812BDevice;
import com.isport.blelibrary.deviceEntry.impl.W812Device;
import com.isport.blelibrary.deviceEntry.impl.W813Device;
import com.isport.blelibrary.deviceEntry.impl.W814Device;
import com.isport.blelibrary.deviceEntry.impl.W817Device;
import com.isport.blelibrary.deviceEntry.impl.W819Device;
import com.isport.blelibrary.deviceEntry.impl.W910Device;
import com.isport.blelibrary.deviceEntry.impl.Watch516Device;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.bean.AdviceBean;
import com.isport.brandapp.home.bean.MainDeviceBean;
import com.isport.brandapp.home.bean.ScacleBean;
import com.isport.brandapp.home.bean.http.BindDevice;
import com.isport.brandapp.home.bean.http.BindDeviceList;
import com.isport.brandapp.home.bean.http.ScaleHistoryData;
import com.isport.brandapp.home.view.DeviceListView;
import com.isport.brandapp.R;
import com.isport.brandapp.arithmetic.WeightStandardImpl;
import com.isport.brandapp.bean.BrandBean;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bean.SleepBean;
import com.isport.brandapp.bean.SportBean;
import com.isport.brandapp.bean.WeightBean;
import com.isport.brandapp.bind.model.DeviceOptionImple;
import com.isport.brandapp.bind.model.DeviceResultCallBack;
import com.isport.brandapp.device.history.util.HistoryParmUtil;
import com.isport.brandapp.parm.db.ProgressShowParms;
import com.isport.brandapp.parm.http.ScaleParms;
import com.isport.brandapp.repository.AdviceListRepository;
import com.isport.brandapp.repository.CustomRepository;
import com.isport.brandapp.repository.MainResposition;
import com.isport.brandapp.util.DeviceDataUtil;
import com.isport.brandapp.util.InitCommonParms;
import com.isport.brandapp.util.RequestCode;

import java.util.ArrayList;
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
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.entry.bean.BaseParms;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author
 * @Date 2018/10/12
 * @Fuction 主页数据处理
 */

public class DeviceListPresenter extends BasePresenter<DeviceListView> {

    private static final String TAG = "DeviceListPresenter";

    DeviceListView view;
    DeviceOptionImple deviceOptionImple;

    public DeviceListPresenter(DeviceListView view) {
        deviceOptionImple = new DeviceOptionImple();
        deviceOptionImple = new DeviceOptionImple();
        this.view = view;
    }

    //*****************************************待整理****************************************//

    /**
     * show是否展示 菊花，因为刚从绑定页面回来要请求设备list会取消弹窗，所以这次让连接的弹窗展示
     * 单机版和网络版返回要区分开来
     *
     * @param isFirstDisplayDB
     * @param show
     */
    public void getDeviceList(boolean isFirstDisplayDB, boolean show, boolean setWatchDefault, boolean isConnect) {



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
        //没有网络的情况，直接查询本地数据库数据
        //首先判断是否先展示本地
        mainResposition.requst(commonParms.setPostBody(isFirstDisplayDB ? isFirstDisplayDB : NetUtils.hasNetwork() ?
                !(App.appType() == App.httpType) : true).setParms(parms)
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

                //需要重新去请求
                Logger.myLog("getDeviceList：" + e.toString());
            }

            @Override
            public void onNext(BindDeviceList bean) {
                if (!show) {
                    Logger.myLog("BindDeviceList 成功 取消弹窗");
                    NetProgressObservable.getInstance().hide("getDeviceList");
                } else {
                    Logger.myLog("BindDeviceList 成功 不取消弹窗");
                }
                HashMap<Integer, DeviceBean> deviceBeanHashMap = new HashMap<>();
                ArrayList<MainDeviceBean> list = new ArrayList<>();
                //把本地数据的数据删除，不然有脏数据
                if (!isFirstDisplayDB && NetUtils.hasNetwork())
                    DeviceTypeTableAction.deleteAllDevices();

                if (bean != null && bean.list != null) {
                    MainDeviceBean mainDeviceBean;
                    DeviceBean deviceBean;
                    BindDevice bindDevice;
                    /*if (bean.list.size() > 0) {
                        //只有请求网络时才去更新

                    }*/
                    for (int i = 0; i < bean.list.size(); i++) {
                        Logger.myLog(TAG,"deviceBeanHashMap=" + bean.list.get(i).toString());
                        // 0:手环，1：体脂称，2：睡眠带
                        deviceBean = new DeviceBean();
                        bindDevice = bean.list.get(i);
                        mainDeviceBean = new MainDeviceBean();
                        deviceBean.deviceType = bindDevice.getDeviceTypeId();
                        mainDeviceBean.setDeviceType(bindDevice.getDeviceTypeId());
                        mainDeviceBean.setDevicename(bindDevice.getDevicetName());
                        list.add(mainDeviceBean);
                        deviceBean.deviceName = bindDevice.getDevicetName();
                        //deviceBean.deviceID=bindDevice.getDevicetName();
                        //现在接口是没有给mac的，更为deviceId，
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
                        //createTime是绑定的时间戳，timestamp是上传数据之后最后一条数据日期的时间戳   绑定之前的数据不要
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
                                //W516、W556、W557、W812B
                                deviceBean.timeTamp = bindDevice.getTimestamp();
                            }
                        }
                        int deviceType = bean.list.get(i).getDeviceTypeId();

                        switch (deviceType) {

                            case JkConfiguration.DeviceType.BODYFAT:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_mz);
                                break;
                            case JkConfiguration.DeviceType.BRAND_W311:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w311);
                                break;
                            case JkConfiguration.DeviceType.Brand_W520:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w520);
                                break;
                            case JkConfiguration.DeviceType.BRAND_W307J:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_307j);
                                break;
                            case JkConfiguration.DeviceType.WATCH_W516:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w516);
                                break;
                            case JkConfiguration.DeviceType.Brand_W814:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w814);
                                break;
                            case JkConfiguration.DeviceType.Watch_W812:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w812);
                                break;
                            case JkConfiguration.DeviceType.Watch_W813:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w813);
                                break;
                            case JkConfiguration.DeviceType.Watch_W819:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w819);
                                break;
                            case JkConfiguration.DeviceType.Watch_W910:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w910);
                                break;
                            case JkConfiguration.DeviceType.Watch_W556:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w526);
                                break;
                            case JkConfiguration.DeviceType.Watch_W557:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w557);
                                break;
                            case JkConfiguration.DeviceType.Watch_W817:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w817);
                                break;
                            case JkConfiguration.DeviceType.Watch_W812B:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w812b);
                                break;
                            case JkConfiguration.DeviceType.Watch_W560B:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_w560);
                                break;
                            case JkConfiguration.DeviceType.Watch_W560:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_w560);
                                break;
                            case JkConfiguration.DeviceType.ROPE_SKIPPING:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_s002);
                                break;

                            case JkConfiguration.DeviceType.Watch_F18:
                                mainDeviceBean.setDeviceRes(R.drawable.icon_main_device_list_f18);
                                break;
                        }

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
                                Logger.myLog("BleSPUtils.WATCH_LAST_SYNCTIME" + BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, "2018-0201"));
                                //BleSPUtils.putString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, "2019-07-29");
                                deviceBean.currentType = JkConfiguration.DeviceType.Watch_W556;
                                deviceBean.deviceID = bindDevice.getDevicetName();
                                deviceBean.sportBean = new SportBean();
                                deviceBean.connectState = false;
                                AppConfiguration.braceletID = deviceBean.deviceID;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);
                                break;
                            case JkConfiguration.DeviceType.BODYFAT:
                                deviceBean.currentType = JkConfiguration.DeviceType.BODYFAT;
                                deviceBean.deviceID = Utils.resetDeviceMac(bindDevice.getDeviceId());
                                deviceBean.weightBean = new WeightBean();
                                deviceBean.connectState = false;
                                deviceBeanHashMap.put(deviceBean.currentType, deviceBean);
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
                                Logger.myLog("bindDevice.getCreateTime()" + DateUtil.dataToString(new Date(deviceBean.timeTamp), "yyyy-MM-dd") + " BleSPUtils.putString:" + lastSyncTime);
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
                        // TODO: 2019/1/25 查询到绑定设备数据，更新到本地,可能存在在其他手机登录解绑的情况，再次回来时，要更新数据库，删除之前的数据
                        //先删除所有数据，然后一个一个更新进去
                        //只有请求网络时才去更新
                        if (!isFirstDisplayDB && NetUtils.hasNetwork())
                            DeviceTypeTableAction.updateOrDelete(bean.list.get(i).getDeviceTypeId(), deviceBean.mac,
                                    deviceBean.deviceID, TokenUtil
                                            .getInstance().getPeopleIdStr(BaseApp.getApp()), deviceBean.deviceName);
                    }
                }
                if (view != null) {
                    if (isFirstDisplayDB) {

                        Logger.myLog("view=" + view);
                        view.successGetDeviceListFormDB(deviceBeanHashMap, list, show, setWatchDefault, isConnect);
                    } else {
                        Logger.myLog("view=" + view);
                        view.successGetDeviceListFormHttp(deviceBeanHashMap, list, show, setWatchDefault, isConnect);
                    }
                }
            }
        });
    }


    public void getMainScaleDataFromHttp(boolean show) {
        /**
         * 体脂称数据
         */
        CustomRepository<ScaleHistoryData, ScaleParms, BaseUrl, BaseDbPar> mainLoadResposition = new
                CustomRepository<>();
        PostBody<ScaleParms, BaseUrl, BaseDbPar> scaleHistoryData = HistoryParmUtil.getScaleHistoryData(1, 1, show);
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
                Logger.myLog("ScaleHistoryData 不取消弹窗" + e.toString());
            }

            @Override
            public void onNext(ScaleHistoryData mScaleHistoryData) {
                if (!show) {
                    Logger.myLog("ScaleHistoryData 取消弹窗");
                    NetProgressObservable.getInstance().hide();
                } else {
                    Logger.myLog("ScaleHistoryData 不取消弹窗");
                }
                Logger.myLog("获取主页Scale成功 == " + mScaleHistoryData.toString());


                if (mScaleHistoryData.getList().size() == 0) {
                    //没有数据的情况
                    if (isViewAttached()) {
                        getMainScaleDataFromDB(false, false);
                    }
                } else {
                    //获取到首页的数据，更新到本地
                    UserInfoBean bean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp
                            ()));
                    if (bean == null) {
                        return;
                    }
                    String[] split = bean.getBirthday().split("-");
                    String weight = bean.getWeight().contains(".") ? bean.getWeight().split("\\.")[0] : bean.getWeight();
                    //设置用户信息到SDK
                    ISportAgent.getInstance().setUserInfo(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(weight), Float.parseFloat(bean.getHeight()),
                            bean.getGender().equals("Male") ? 1 : 0,
                            TimeUtils.getAge(bean.getBirthday()), bean.getUserId());

                    ScaleHistoryData.ScaleList scaleList = null;
                    Scale_FourElectrode_DataModel scale_fourElectrode_dataModel = null;
                    for (int i = 0; i < mScaleHistoryData.getList().size(); i++) {
                        //把本地没有的存起来
                        scaleList = mScaleHistoryData.getList().get(i);
//                        private double BWP;//水含量%
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
     * 从DB获取主页Scale数据
     *
     * @param show
     */
    public void getMainScaleDataFromDB(boolean show, boolean isPass) {


        Logger.myLog("getMainScaleDataFromDB:-------------------");


        //查询体脂称数据
        Observable.create(new ObservableOnSubscribe<List<Scale_FourElectrode_DataModel>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Scale_FourElectrode_DataModel>> emitter) throws Exception {

                List<Scale_FourElectrode_DataModel> scaleFourElectrodeDataModelByDeviceId =
                        Scale_FourElectrode_DataModelAction.findScaleFourElectrodeDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 1);
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

                if (view != null) {
                    if (!show)
                        NetProgressObservable.getInstance().hide("getMainData");

                    if (AppConfiguration.deviceBeanList == null || !AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                        scale_fourElectrode_dataModel = null;
                    }

                    view.successGetMainScaleDataFromDB(scacleBeans,
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


    public void connectDevice(String currentName, String watchMac, int deviceType, boolean show, boolean isConnectByUser) {
        Log.e(TAG,"--------DeviceMan连接="+currentName +" "+watchMac + " "+deviceType + " "+show +" "+isConnectByUser);

        BaseDevice device = null;
        switch (deviceType) {
            case JkConfiguration.DeviceType.Watch_F18:
                device = new W7018Device(currentName,watchMac);
                break;
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
            case JkConfiguration.DeviceType.ROPE_SKIPPING: {
                device = new S002BDevice(currentName, watchMac);
            }
            break;
        }
        if (deviceOptionImple != null) {
            Constants.CAN_RECONNECT = true;
            deviceOptionImple.connect(device, show, isConnectByUser);
        }
    }


    public void scan(int type, boolean isScale) {


        if (deviceOptionImple != null) {
            deviceOptionImple.scan(type, isScale, new DeviceResultCallBack() {
                @Override
                public void onScanResult(ArrayList<BaseDevice> mBleDevices) {

                }

                @Override
                public void onScanResult(Map<String, BaseDevice> listDevicesMap) {
                    if (view != null) {
                        view.onScan(listDevicesMap);
                    }
                }


                @Override
                public void onScanFinish() {
                    if (view != null) {
                        view.onScanFinish();
                    }
                }
            });

        }
    }

    public void getAdviceList() {
        AdviceListRepository.requestAdviceList().as(view.bindAutoDispose()).subscribe(new BaseObserver<List<AdviceBean>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(List<AdviceBean> adviceList) {
                if (adviceList != null && adviceList.size() > 0) {
                    if (view != null) {
                        view.getAdviceList(adviceList);
                    }
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
                Logger.myLog("getAdviceList onError" + e.toString());
            }

        });
    }

    public void cancelScan() {
        if (deviceOptionImple != null) {
            deviceOptionImple.cancelScan();
        }
    }


}
