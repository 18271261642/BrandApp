package com.isport.brandapp.repository;


import com.isport.blelibrary.db.table.w811w814.OneceHrMode;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.device.bracelet.braceletModel.DeviceMeasureDataImp;
import com.isport.brandapp.device.bracelet.braceletModel.IDeviceMeasureData;
import com.isport.brandapp.net.RetrofitClient;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.InitCommonParms;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;

import java.util.ArrayList;
import java.util.List;

import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;


public class OnceHrRepository {

    public static Observable<Integer> requstUpgradeData(String deviceId, String userId) {
        return new NetworkBoundResource<Integer>() {
            @Override
            public Observable<Integer> getFromDb() {
                return null;
            }

            @Override
            public Observable<Integer> getNoCacheData() {
                return null;
            }

            @Override
            public boolean shouldFetchRemoteSource() {
                return false;
            }

            @Override
            public boolean shouldStandAlone() {
                return false;
            }

            @Override
            public Observable<Integer> getRemoteSource() {
                List<OnceHrInfo> upgradeList = new ArrayList<>();
                try {
                    IDeviceMeasureData deviceMeasureData = new DeviceMeasureDataImp();

                    List<OneceHrMode> list = deviceMeasureData.uploadingOnceHrData(deviceId, userId, "0");
                   // Logger.myLog("measure_oxygen success:no upgrade1" + list + "userId：" + userId + " deviceId：" + deviceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    OneceHrMode oxygenMode;
                    OnceHrInfo info;

                    for (int i = 0; i < list.size(); i++) {
                        oxygenMode = list.get(i);
                        info = new OnceHrInfo();
                        info.setHeartValue(oxygenMode.getBloodOxygen() + "");
                        info.setDeviceId(oxygenMode.getDeviceId());
                        info.setTimestamp(oxygenMode.getTimestamp());
                        info.setUserId(oxygenMode.getUserId());
                        upgradeList.add(info);
                        // info
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (upgradeList.size() == 0) {
                        return Observable.create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                                emitter.onNext(-1);
                                emitter.onComplete();
                            }
                        });

                    } else {
                        InitCommonParms<List<OnceHrInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                        BaseUrl baseUrl = new BaseUrl();
                        if (DeviceTypeUtil.isContainW556OrW812B(deviceId)) {
                            return (Observable<Integer>) RetrofitClient.getInstance().postW526(parInitCommonParms
                                    .setPostBody(!(App.appType() == App.httpType)).setParms(upgradeList).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ONCE_HR_UPGRADE).getPostBody());
                        } else {
                            return (Observable<Integer>) RetrofitClient.getInstance().postW81(parInitCommonParms
                                    .setPostBody(!(App.appType() == App.httpType)).setParms(upgradeList).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ONCE_HR_UPGRADE).getPostBody());
                        }


                    }
                }


            }

            @Override
            public void saveRemoteSource(Integer remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<List<OnceHrInfo>> requstOnceHrData(String deviceId, String userId, int num) {
        return new NetworkBoundResource<List<OnceHrInfo>>() {
            @Override
            public Observable<List<OnceHrInfo>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<OnceHrInfo>> getNoCacheData() {
                return null;
            }

            @Override
            public boolean shouldFetchRemoteSource() {
                return false;
            }

            @Override
            public boolean shouldStandAlone() {
                return false;
            }

            @Override
            public Observable<List<OnceHrInfo>> getRemoteSource() {
                InitCommonParms<List<OxyInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = String.valueOf(num);
                baseUrl.deviceid=deviceId;
                Logger.myLog("getRemoteSource: baseUrl.userid" + userId + "baseUrl.extend1:" + num + "baseUrl.deviceid:" + deviceId);
                if (DeviceTypeUtil.isContainW556OrW812B(deviceId)) {
                    return (Observable<List<OnceHrInfo>>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ONCE_HR_NUM_DATA).getPostBody());
                } else {
                    return (Observable<List<OnceHrInfo>>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ONCE_HR_NUM_DATA).getPostBody());
                }

            }

            @Override
            public void saveRemoteSource(List<OnceHrInfo> remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<List<OnceHrInfo>> requstPageNumOnceHrData(String deviceId, String userId, int offset) {
        return new NetworkBoundResource<List<OnceHrInfo>>() {
            @Override
            public Observable<List<OnceHrInfo>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<OnceHrInfo>> getNoCacheData() {
                return null;
            }

            @Override
            public boolean shouldFetchRemoteSource() {
                return false;
            }

            @Override
            public boolean shouldStandAlone() {
                return false;
            }

            @Override
            public Observable<List<OnceHrInfo>> getRemoteSource() {
                InitCommonParms<List<OxyInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = String.valueOf(offset);
                baseUrl.deviceid = deviceId;
                if (DeviceTypeUtil.isContainW556OrW812B(deviceId)) {
                    return (Observable<List<OnceHrInfo>>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ONCE_HR_PAGE_NUM_DATA).getPostBody());
                } else {
                    return (Observable<List<OnceHrInfo>>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ONCE_HR_PAGE_NUM_DATA).getPostBody());
                }


            }

            @Override
            public void saveRemoteSource(List<OnceHrInfo> remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<List<OnceHrInfo>> requstNumOnceHrData(String deviceId, String userId, int num) {
        return new NetworkBoundResource<List<OnceHrInfo>>() {
            @Override
            public Observable<List<OnceHrInfo>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<OnceHrInfo>> getNoCacheData() {
                return null;
            }

            @Override
            public boolean shouldFetchRemoteSource() {
                return false;
            }

            @Override
            public boolean shouldStandAlone() {
                return false;
            }

            @Override
            public Observable<List<OnceHrInfo>> getRemoteSource() {
                InitCommonParms<List<OnceHrInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = String.valueOf(num);
                baseUrl.deviceid = deviceId;


                Logger.myLog("getRemoteSource: baseUrl.userid" + userId + "baseUrl.extend1:" + num + "baseUrl.deviceid:" + deviceId);
                if (DeviceTypeUtil.isContainW556OrW812B(deviceId)) {
                    return (Observable<List<OnceHrInfo>>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ONCE_HR_NUM_DATA).getPostBody());
                } else {
                    return (Observable<List<OnceHrInfo>>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ONCE_HR_NUM_DATA).getPostBody());
                }


            }

            @Override
            public void saveRemoteSource(List<OnceHrInfo> remoteSource) {

            }
        }.getAsObservable();
    }

}
