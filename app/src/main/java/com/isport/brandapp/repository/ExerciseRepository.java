package com.isport.brandapp.repository;


import com.google.gson.Gson;
import com.isport.blelibrary.db.table.w811w814.W81DeviceExerciseData;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.device.bracelet.braceletModel.DeviceMeasureDataImp;
import com.isport.brandapp.device.bracelet.braceletModel.IDeviceMeasureData;
import com.isport.brandapp.net.RetrofitClient;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.InitCommonParms;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.ExerciseInfo;
import com.isport.brandapp.wu.bean.PractiseRecordInfo;
import com.isport.brandapp.wu.bean.PractiseTimesInfo;

import java.util.ArrayList;
import java.util.List;

import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


public class ExerciseRepository {

    private static final String TAG = "ExerciseRepository";


    public static Observable<Integer> requestTodayExerciseData(int deviceType, String userId, Long time, String deviceId) {
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
                InitCommonParms<List<BPInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = String.valueOf(time);
                baseUrl.deviceid = deviceId;

                Logger.myLog(TAG,"requestTodayExerciseData:" + userId + ",time:" + time + ",deviceId:" + deviceId);

                if (DeviceTypeUtil.isContainW55X(deviceType)) {
                    return (Observable<Integer>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.W81_TODAY_EXERCISE_ALLTIME).getPostBody());
                } else {
                    return (Observable<Integer>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.W81_TODAY_EXERCISE_ALLTIME).getPostBody());
                }
            }

            @Override
            public void saveRemoteSource(Integer remoteSource) {

            }
        }.getAsObservable();
    }


    public static Observable<Integer> requstUpgradeExerciseData(int devicetype, String deviceId, String userId) {


        Logger.myLog(TAG,"----上传W560锻炼数据="+devicetype+"\n"+deviceId+"\n"+userId);

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
                IDeviceMeasureData deviceMeasureData = new DeviceMeasureDataImp();


                /**
                 * [
                 {
                 "aveRate": "string",
                 "createTime": "2019-11-19T09:48:56.572Z",
                 "dateStr": "string",
                 "deviceId": "string",
                 "endTimestamp": "2019-11-19T09:48:56.572Z",
                 "exerciseType": "string",
                 "extend1": "string",
                 "extend2": "string",
                 "extend3": "string",
                 "heartRateDetailArray": "string",
                 "startTimestamp": "2019-11-19T09:48:56.572Z",
                 "totalCalories": "string",
                 "totalDistance": "string",
                 "totalSteps": "string",
                 "userId": 0,
                 "vaildTimeLength": "string",
                 "wristbandTrainingId": 0
                 }
                 ]
                 */
                List<W81DeviceExerciseData> list = deviceMeasureData.uploadingExerciseData(deviceId, userId, "0");
                  if(list != null && !list.isEmpty()){

                  }

                   //Logger.myLog("---requstUpgradeExerciseData--list:" + list.size());
                W81DeviceExerciseData exerciseData;
                ExerciseInfo info;
                List<ExerciseInfo> upgradeList = new ArrayList<>();

                try {
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            info = new ExerciseInfo();
                            exerciseData = list.get(i);

                            Logger.myLog(TAG,"---update锻炼数据="+new Gson().toJson(exerciseData));
//                            if (App.getDeviceBindTime() == 0) {
//                                continue;
//                            }
//                            if ((App.getDeviceBindTime() - exerciseData.getStartTimestamp() > 0)) {
//                                continue;
//                            }

                            if(Integer.parseInt(exerciseData.getTotalSteps()) >=0 && exerciseData.getEndTimestamp() > exerciseData.getStartTimestamp()){
                                info.setAveRate(exerciseData.getAvgHr());
                                info.setDeviceId(exerciseData.getDeviceId());
                                info.setUserId(exerciseData.getUserId());
                                info.setDateStr(exerciseData.getDateStr());
                                info.setExerciseType(exerciseData.getExerciseType());
                                info.setEndTimestamp(exerciseData.getEndTimestamp());
                                //info.setEndTimestamp(1);
                                // info.setStartTimestamp(1);
                                info.setStartTimestamp(exerciseData.getStartTimestamp());
                                info.setHeartRateDetailArray(exerciseData.getHrArray());
                                info.setTotalCalories(exerciseData.getTotalCalories());
                                info.setTotalDistance(exerciseData.getTotalDistance());
                                info.setTotalSteps(exerciseData.getTotalSteps());
                                info.setVaildTimeLength(exerciseData.getVaildTimeLength());

                                //添加W560系列详细计步和距离，卡路里
                                info.setHeartDetailArray(exerciseData.getHrArray());
                                info.setStepDetailArray(exerciseData.getStepArray());
                                info.setDistanceDetailArray(exerciseData.getDistanceArray());
                                info.setCaloriesDetailArray(exerciseData.getCalorieArray());

                                upgradeList.add(info);
                            }

                            // info
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    Logger.myLog(TAG,"---22--requstUpgradeExerciseData:" + upgradeList.size());
                    if (upgradeList.size() == 0) {
                        return Observable.create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                                emitter.onNext(-1);
                                emitter.onComplete();
                            }
                        });
                    } else {
                        InitCommonParms<List<ExerciseInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                        BaseUrl baseUrl = new BaseUrl();
                        Logger.myLog(TAG,"-----333===requstUpgradeExerciseData:" + upgradeList.size());
                        if (DeviceTypeUtil.isContainW55X(devicetype) ) {
                            return (Observable<Integer>) RetrofitClient.getInstance().postW526(parInitCommonParms
                                    .setPostBody(!(App.appType() == App.httpType)).setParms(upgradeList).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.EEXERCISE_UPGRADE).getPostBody());
                        } else {
                            return (Observable<Integer>) RetrofitClient.getInstance().postW81(parInitCommonParms
                                    .setPostBody(!(App.appType() == App.httpType)).setParms(upgradeList).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.EEXERCISE_UPGRADE).getPostBody());
                        }
                    }
                }

            }

            @Override
            public void saveRemoteSource(Integer remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<List<PractiseRecordInfo>> requstSportRecordPageData(int deviceType, String deviceId, String userId, int type, int page) {
        return new NetworkBoundResource<List<PractiseRecordInfo>>() {
            @Override
            public Observable<List<PractiseRecordInfo>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<PractiseRecordInfo>> getNoCacheData() {
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
            public Observable<List<PractiseRecordInfo>> getRemoteSource() {
                InitCommonParms<List<PractiseRecordInfo>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.deviceid = deviceId;
                baseUrl.dataType = String.valueOf(type);
                baseUrl.extend1 = String.valueOf(page);
                if (DeviceTypeUtil.isContainW55X(deviceType)) {
                    return (Observable<List<PractiseRecordInfo>>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.SPORTRECORD_PAGE_NUM_DATA).getPostBody());
                } else {
                    return (Observable<List<PractiseRecordInfo>>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.SPORTRECORD_PAGE_NUM_DATA).getPostBody());
                }
            }

            @Override
            public void saveRemoteSource(List<PractiseRecordInfo> remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<PractiseTimesInfo> requstTotalPractiseData(int deviceType, String deviceId, String userId, int type) {
        return new NetworkBoundResource<PractiseTimesInfo>() {
            @Override
            public Observable<PractiseTimesInfo> getFromDb() {
                return null;
            }

            @Override
            public Observable<PractiseTimesInfo> getNoCacheData() {
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
            public Observable<PractiseTimesInfo> getRemoteSource() {
                InitCommonParms<PractiseTimesInfo, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.deviceid = deviceId;
                baseUrl.dataType = String.valueOf(type);

                if (deviceType == JkConfiguration.DeviceType.Watch_W556) {
                    return (Observable<PractiseTimesInfo>) RetrofitClient.getInstance().postW526(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.W81_TOTAL_EXERCISE_TIMES).getPostBody());
                } else {
                    return (Observable<PractiseTimesInfo>) RetrofitClient.getInstance().postW81(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.W81_TOTAL_EXERCISE_TIMES).getPostBody());
                }
            }

            @Override
            public void saveRemoteSource(PractiseTimesInfo remoteSource) {

            }
        }.getAsObservable();
    }
}
