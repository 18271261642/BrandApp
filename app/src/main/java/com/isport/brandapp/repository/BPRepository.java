package com.isport.brandapp.repository;


import com.isport.blelibrary.db.table.w811w814.BloodPressureMode;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.device.bracelet.braceletModel.DeviceMeasureDataImp;
import com.isport.brandapp.device.bracelet.braceletModel.IDeviceMeasureData;
import com.isport.brandapp.device.f18.dial.F18DialBean;
import com.isport.brandapp.net.RetrofitClient;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.InitCommonParms;
import com.isport.brandapp.wu.bean.BPInfo;
import java.util.ArrayList;
import java.util.List;
import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;


public class BPRepository {

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
                List<BPInfo> upgradeList = new ArrayList<>();
                try {

                    IDeviceMeasureData deviceMeasureData = new DeviceMeasureDataImp();
                    List<BloodPressureMode> list = deviceMeasureData.uploadingBloodPressureData(deviceId, userId, "0");
                    Logger.myLog("requstUpgradeData success:no upgrade1" + list + "userId：" + userId + " deviceId：" + deviceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    BloodPressureMode oxygenMode;
                    BPInfo info;
                    for (int i = 0; i < list.size(); i++) {
                        oxygenMode = list.get(i);
                        info = new BPInfo();
                        info.setDpValue(oxygenMode.getDiastolicBloodPressure());
                        info.setDeviceId(oxygenMode.getDeviceId());
                        info.setTimestamp(oxygenMode.getTimestamp());
                        info.setUserId(oxygenMode.getUserId());
                        info.setSpValue(oxygenMode.getSystolicBloodPressure());
                        upgradeList.add(info);
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
                        InitCommonParms<List<BPInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                        BaseUrl baseUrl = new BaseUrl();

                        if(DeviceTypeUtil.isContainW556OrW812B(deviceId)) {                            return (Observable<Integer>) RetrofitClient.getInstance().postW526(parInitCommonParms
                                    .setPostBody(!(App.appType() == App.httpType)).setParms(upgradeList).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.BLOODPRESSUREMODE_UPGRADE).getPostBody());
                        }else {
                            return (Observable<Integer>) RetrofitClient.getInstance().postW81(parInitCommonParms
                                    .setPostBody(!(App.appType() == App.httpType)).setParms(upgradeList).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.BLOODPRESSUREMODE_UPGRADE).getPostBody());
                        }
                    }
                }


            }

            @Override
            public void saveRemoteSource(Integer remoteSource) {

            }
        }.getAsObservable();
    }


    public static Observable<List<BPInfo>> requstNumBPData(String deviceId, String userId, int num) {
        return new NetworkBoundResource<List<BPInfo>>() {
            @Override
            public Observable<List<BPInfo>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<BPInfo>> getNoCacheData() {
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
            public Observable<List<BPInfo>> getRemoteSource() {
                InitCommonParms<List<BPInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = String.valueOf(num);
                baseUrl.deviceid = deviceId;
                if(DeviceTypeUtil.isContainW556OrW812B(deviceId)) {                    return (Observable<List<BPInfo>>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.BLOODPRESSURE_NUM_DATA).getPostBody());
                }else {
                    return (Observable<List<BPInfo>>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.BLOODPRESSURE_NUM_DATA).getPostBody());
                }
            }

            @Override
            public void saveRemoteSource(List<BPInfo> remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<List<BPInfo>> requstPageNumBPData(String deviceId, String userId, int offset) {
        return new NetworkBoundResource<List<BPInfo>>() {
            @Override
            public Observable<List<BPInfo>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<BPInfo>> getNoCacheData() {
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
            public Observable<List<BPInfo>> getRemoteSource() {
                InitCommonParms<List<BPInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = String.valueOf(offset);
                baseUrl.deviceid = deviceId;
                if(DeviceTypeUtil.isContainW556OrW812B(deviceId)) {                    return (Observable<List<BPInfo>>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.BLOODPRESSURE_PAGE_NUM_DATA).getPostBody());
                }else {
                    return (Observable<List<BPInfo>>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.BLOODPRESSURE_PAGE_NUM_DATA).getPostBody());
                }
            }

            @Override
            public void saveRemoteSource(List<BPInfo> remoteSource) {

            }
        }.getAsObservable();
    }


    public static List<F18DialBean>  getF18DialCenterData(){

        List<F18DialBean> list = new ArrayList<>();

        new NetworkBoundResource<List<F18DialBean>>(){

            @Override
            public Observable<List<F18DialBean>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<F18DialBean>> getNoCacheData() {
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
            public Observable<List<F18DialBean>> getRemoteSource() {
                InitCommonParms<List<F18DialBean>, BaseUrl, BaseDbPar> initCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.object = 7018;
                return (Observable<List<F18DialBean>>) RetrofitClient.getInstance().post(initCommonParms
                        .setPostBody
                                (!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.GET_F18_DEVICE_DIAL_LIST).getPostBody());

            }

            @Override
            public void saveRemoteSource(List<F18DialBean> remoteSource) {
                list.clear();
                list.addAll(remoteSource);
            }
        }.getAsObservable();
        return list;
    }
}
