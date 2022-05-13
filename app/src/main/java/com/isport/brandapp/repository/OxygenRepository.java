package com.isport.brandapp.repository;


import com.isport.blelibrary.db.table.w811w814.OxygenMode;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.device.bracelet.braceletModel.DeviceMeasureDataImp;
import com.isport.brandapp.device.bracelet.braceletModel.IDeviceMeasureData;
import com.isport.brandapp.net.RetrofitClient;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.InitCommonParms;
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


public class OxygenRepository {

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
                List<OxyInfo> upgradeList = new ArrayList<>();
                try {
                    IDeviceMeasureData deviceMeasureData = new DeviceMeasureDataImp();

                    List<OxygenMode> list = deviceMeasureData.uploadingOxyenData(deviceId, userId, "0");
                  //  Logger.myLog("measure_oxygen success:no upgrade1" + list + "userId：" + userId + " deviceId：" + deviceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    OxygenMode oxygenMode;
                    OxyInfo info;

                    for (int i = 0; i < list.size(); i++) {
                        oxygenMode = list.get(i);
                        info = new OxyInfo();
                        info.setBoValue(oxygenMode.getBloodOxygen());
                        info.setDeviceId(oxygenMode.getDeviceId());
                        info.setTimestamp(oxygenMode.getTimestamp());
                        info.setUserId(oxygenMode.getUserId());
                        upgradeList.add(info);
                        // info
                    }
                } catch (Exception e) {

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
                        InitCommonParms<List<OxyInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                        BaseUrl baseUrl = new BaseUrl();
                        if (DeviceTypeUtil.isContainW556OrW812B(deviceId)) {
                            return (Observable<Integer>) RetrofitClient.getInstance().postW526(parInitCommonParms
                                    .setPostBody(!(App.appType() == App.httpType)).setParms(upgradeList).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.OXYGEN_UPGRADE).getPostBody());
                        } else {
                            return (Observable<Integer>) RetrofitClient.getInstance().postW81(parInitCommonParms
                                    .setPostBody(!(App.appType() == App.httpType)).setParms(upgradeList).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.OXYGEN_UPGRADE).getPostBody());
                        }


                    }
                }


            }

            @Override
            public void saveRemoteSource(Integer remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<List<OxyInfo>> requstNumOxygenData(String deviceId, String userId, int num) {
        return new NetworkBoundResource<List<OxyInfo>>() {
            @Override
            public Observable<List<OxyInfo>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<OxyInfo>> getNoCacheData() {
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
            public Observable<List<OxyInfo>> getRemoteSource() {
                InitCommonParms<List<OxyInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = String.valueOf(num);
                baseUrl.deviceid = deviceId;


                Logger.myLog("getRemoteSource: baseUrl.userid" + userId + "baseUrl.extend1:" + num + "baseUrl.deviceid:" + deviceId);

                if (DeviceTypeUtil.isContainW556OrW812B(deviceId)) {
                    return (Observable<List<OxyInfo>>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.OXYGEN_NUM_DATA).getPostBody());
                } else {
                    return (Observable<List<OxyInfo>>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.OXYGEN_NUM_DATA).getPostBody());
                }


            }

            @Override
            public void saveRemoteSource(List<OxyInfo> remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<List<OxyInfo>> requstPageNumOxygenData(String deviceId, String userId, int offset) {
        return new NetworkBoundResource<List<OxyInfo>>() {
            @Override
            public Observable<List<OxyInfo>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<OxyInfo>> getNoCacheData() {
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
            public Observable<List<OxyInfo>> getRemoteSource() {
                InitCommonParms<List<OxyInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = String.valueOf(offset);
                baseUrl.deviceid = deviceId;
                if (DeviceTypeUtil.isContainW556OrW812B(deviceId)) {
                    return (Observable<List<OxyInfo>>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.OXYGEN_PAGE_NUM_DATA).getPostBody());

                } else {
                    return (Observable<List<OxyInfo>>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.OXYGEN_PAGE_NUM_DATA).getPostBody());
                }


            }

            @Override
            public void saveRemoteSource(List<OxyInfo> remoteSource) {

            }
        }.getAsObservable();
    }


}
