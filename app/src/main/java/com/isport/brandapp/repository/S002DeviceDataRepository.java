package com.isport.brandapp.repository;


import com.isport.blelibrary.db.action.s002.S002_DailyBriefAction;
import com.isport.blelibrary.db.action.s002.S002_DailySummaryAction;
import com.isport.blelibrary.db.action.s002.S002_SummaryAction;
import com.isport.blelibrary.db.parse.ChallengeItem;
import com.isport.blelibrary.db.parse.RopeDetail;
import com.isport.blelibrary.db.table.s002.DailyBrief;
import com.isport.blelibrary.db.table.s002.DailySummaries;
import com.isport.blelibrary.db.table.s002.Summary;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.home.bean.RopeDetailBean;
import com.isport.brandapp.net.RetrofitClient;
import com.isport.brandapp.ropeskipping.s002DataModel.IS002DeviceDataModel;
import com.isport.brandapp.util.InitCommonParms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;


public class S002DeviceDataRepository {

    private static final String TAG = "S002DeviceDataRepositor";


    public S002DeviceDataRepository() {
    }


    public static Observable<List<String>> requestsportDaysInMonth(String userId, String month) {
        return new NetworkBoundResource<List<String>>() {
            @Override
            public Observable<List<String>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<String>> getNoCacheData() {
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
            public Observable<List<String>> getRemoteSource() {


                //   Logger.myLog("requstUpgradeExerciseData:" + list);


                InitCommonParms<List<RopeDetail>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;
                baseUrl.extend1 = month;
                return (Observable<List<String>>) RetrofitClient.getInstance().postS002(parInitCommonParms
                        .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.sportDaysInMonth).getPostBody());

            }

            @Override
            public void saveRemoteSource(List<String> remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<List<DailySummaries>> requestDailySummaries(String userId, int data, String day, String summaryType) {
        return new NetworkBoundResource<List<DailySummaries>>() {
            @Override
            public Observable<List<DailySummaries>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<DailySummaries>> getNoCacheData() {
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
            public Observable<List<DailySummaries>> getRemoteSource() {


                //这里要去查数据库，如果有数据就不需要去

                S002_DailySummaryAction s002_dailyBriefAction = new S002_DailySummaryAction();


                //这只是一天的
                ArrayList<DailySummaries> list = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(data * 1000l));
                String strDate;
                int monthCount = 7;
                if (summaryType.equals("WEEK")) {
                    monthCount = 7;

                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    monthCount = DateUtil.getMonthOfDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);

                }
                for (int i = 0; i < monthCount; i++) {
                    if (i != 0) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    strDate = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd");
                    Logger.myLog("requestDailySummaries:strDate=" + strDate);
                    DailySummaries bean = s002_dailyBriefAction.findDailyBean(userId, strDate);
                    if (bean != null) {
                        list.add(bean);
                    }
                }
                Collections.reverse(list);

                if (list != null && list.size() > 0) {
                    return Observable.create(new ObservableOnSubscribe<List<DailySummaries>>() {
                        @Override
                        public void subscribe(ObservableEmitter<List<DailySummaries>> emitter) throws Exception {
                            list.get(0).setLocation(true);
                            emitter.onNext(list);
                            emitter.onComplete();
                        }
                    });

                } else {
                    //   Logger.myLog("requstUpgradeExerciseData:" + list);
                    InitCommonParms<List<RopeDetail>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                    BaseUrl baseUrl = new BaseUrl();
                    baseUrl.userid = userId;
                    baseUrl.extend1 = day;
                    baseUrl.extend2 = summaryType;
                    return (Observable<List<DailySummaries>>) RetrofitClient.getInstance().postS002(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.dailySummaries).getPostBody());
                }
            }

            @Override
            public void saveRemoteSource(List<DailySummaries> remoteSource) {

                if (remoteSource != null && remoteSource.size() > 0) {
                    if (!remoteSource.get(0).isLocation()) {
                        //保存数据
                        S002_DailySummaryAction s002_dailySummaryAction = new S002_DailySummaryAction();
                        s002_dailySummaryAction.saveDailySummary(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), remoteSource);
                    }
                }


            }
        }.getAsObservable();
    }

    public static Observable<List<DailyBrief>> requestDailyBrief(String userId, String day) {
        return new NetworkBoundResource<List<DailyBrief>>() {
            @Override
            public Observable<List<DailyBrief>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<DailyBrief>> getNoCacheData() {
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
            public Observable<List<DailyBrief>> getRemoteSource() {


                //   Logger.myLog("requstUpgradeExerciseData:" + list);


                S002_DailyBriefAction s002_dailyBriefAction = new S002_DailyBriefAction();
                List<DailyBrief> list = s002_dailyBriefAction.findDailyBrief(userId, day);
                Logger.myLog("requsetSummary+" + list);
                if (list != null && list.size() > 0) {
                    return Observable.create(new ObservableOnSubscribe<List<DailyBrief>>() {
                        @Override
                        public void subscribe(ObservableEmitter<List<DailyBrief>> emitter) throws Exception {
                            list.get(0).setIsLocation(true);
                            emitter.onNext(list);
                            emitter.onComplete();
                        }
                    });

                } else {


                    InitCommonParms<List<RopeDetail>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                    BaseUrl baseUrl = new BaseUrl();
                    baseUrl.userid = userId;
                    baseUrl.extend1 = day;
                    return (Observable<List<DailyBrief>>) RetrofitClient.getInstance().postS002(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.dailyBrief).getPostBody());

                }
            }

            @Override
            public void saveRemoteSource(List<DailyBrief> remoteSource) {

                if (remoteSource != null && remoteSource.size() > 0) {
                    if (!remoteSource.get(0).getIsLocation()) {
                        //保存数据
                        S002_DailyBriefAction s002_dailySummaryAction = new S002_DailyBriefAction();
                        s002_dailySummaryAction.saveDailyBrief(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), day, remoteSource);
                    }
                }

            }
        }.getAsObservable();
    }

    //day=2020-09-18&summaryType=ALL&userId=317
    public static Observable<Summary> requsetSummary(String userId, String day, String summaryType) {
        return new NetworkBoundResource<Summary>() {
            @Override
            public Observable<Summary> getFromDb() {
                return null;
            }

            @Override
            public Observable<Summary> getNoCacheData() {
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
            public Observable<Summary> getRemoteSource() {


                //   Logger.myLog("requstUpgradeExerciseData:" + list);

                S002_SummaryAction s002_dailyBriefAction = new S002_SummaryAction();
                Summary list = s002_dailyBriefAction.findSummary(userId, day, summaryType);

                Logger.myLog("requsetSummary+" + list);

                if (list != null) {
                    return Observable.create(new ObservableOnSubscribe<Summary>() {
                        @Override
                        public void subscribe(ObservableEmitter<Summary> emitter) throws Exception {
                            list.setIsLocation(true);
                            emitter.onNext(list);
                            emitter.onComplete();
                        }
                    });

                } else {
                    InitCommonParms<List<RopeDetail>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                    BaseUrl baseUrl = new BaseUrl();
                    baseUrl.userid = userId;
                    if(!summaryType.equals("ALL")){
                        baseUrl.extend1 = day;
                    }

                    baseUrl.extend2 = summaryType;
                    return (Observable<Summary>) RetrofitClient.getInstance().postS002(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ROPE_SUMMARY).getPostBody());

                }
            }

            @Override
            public void saveRemoteSource(Summary remoteSource) {
                if (remoteSource != null) {

                    Logger.myLog("saveSummary" + remoteSource + "day" + day + "summaryType=" + summaryType);
                    if (!remoteSource.getIsLocation()) {
                        //保存数据
                        S002_SummaryAction s002_dailySummaryAction = new S002_SummaryAction();
                        s002_dailySummaryAction.saveSummary(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), day, remoteSource, summaryType);
                    }
                }
            }
        }.

                getAsObservable();

    }


    public static Observable<String> requstUpgradeRopeData(String deviceId, String userId) {
        return new NetworkBoundResource<String>() {
            @Override
            public Observable<String> getFromDb() {
                return null;
            }

            @Override
            public Observable<String> getNoCacheData() {
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
            public Observable<String> getRemoteSource() {


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
                IS002DeviceDataModel deviceMeasureData = new IS002DeviceDataModel();
                List<RopeDetail> list = deviceMeasureData.getAllNoUpgradeS002DeviceDetailData(deviceId, userId, "0");
                Logger.myLog("requstUpgradeRopeData:" + list.size());
                //   Logger.myLog("requstUpgradeExerciseData:" + list);


                if (list.size() == 0) {
                    return Observable.create(new ObservableOnSubscribe<String>() {
                        @Override
                        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                            emitter.onNext("-1");
                            emitter.onComplete();
                        }
                    });
                } else {
                    InitCommonParms<List<RopeDetail>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                    BaseUrl baseUrl = new BaseUrl();
                    Logger.myLog("requstUpgradeRopeData:" + list.size());
                    return (Observable<String>) RetrofitClient.getInstance().postS002(parInitCommonParms
                            .setPostBody(!(App.appType() == App.httpType)).setParms(list).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.ROPE_UPGRADE_DATA).getPostBody());
                }

            }

            @Override
            public void saveRemoteSource(String remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<RopeDetailBean> requsetRopeUrl(String userId) {
        return new NetworkBoundResource<RopeDetailBean>() {
            @Override
            public Observable<RopeDetailBean> getFromDb() {
                return null;
            }

            @Override
            public Observable<RopeDetailBean> getNoCacheData() {
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
            public Observable<RopeDetailBean> getRemoteSource() {


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
                InitCommonParms<List<RopeDetail>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;

                return (Observable<RopeDetailBean>) RetrofitClient.getInstance().postS002(parInitCommonParms
                        .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.rope_detail_url).getPostBody());

            }

            @Override
            public void saveRemoteSource(RopeDetailBean remoteSource) {

            }
        }.getAsObservable();
    }
    public static Observable<String> requCourseUrl(String userId) {
        return new NetworkBoundResource<String>() {
            @Override
            public Observable<String> getFromDb() {
                return null;
            }

            @Override
            public Observable<String> getNoCacheData() {
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
            public Observable<String> getRemoteSource() {


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
                InitCommonParms<List<RopeDetail>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;

                return (Observable<String>) RetrofitClient.getInstance().postS002(parInitCommonParms
                        .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.rope_course_url).getPostBody());

            }

            @Override
            public void saveRemoteSource(String remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<String> requsetRopeChallegUrl(String userId) {
        return new NetworkBoundResource<String>() {
            @Override
            public Observable<String> getFromDb() {
                return null;
            }

            @Override
            public Observable<String> getNoCacheData() {
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
            public Observable<String> getRemoteSource() {


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
                InitCommonParms<List<RopeDetail>, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.userid = userId;

                return (Observable<String>) RetrofitClient.getInstance().postS002(parInitCommonParms
                        .setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.challeng).getPostBody());

            }

            @Override
            public void saveRemoteSource(String remoteSource) {

            }
        }.getAsObservable();
    }

    public static Observable<String> requstUserChallengReCords(String challengeItemId, String userId) {
        return new NetworkBoundResource<String>() {
            @Override
            public Observable<String> getFromDb() {
                return null;
            }

            @Override
            public Observable<String> getNoCacheData() {
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
            public Observable<String> getRemoteSource() {
                Logger.myLog(TAG,"-----getRemoteSource");

                /**
                 {
                 "challengeItemId": 1,
                 "userId": 317
                 }
                 */
                ChallengeItem challengeItem = new ChallengeItem();
                challengeItem.setChallengeItemId(challengeItemId);
                challengeItem.setUserId(userId);
                InitCommonParms<ChallengeItem, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();

                return (Observable<String>) RetrofitClient.getInstance().postS002(parInitCommonParms.setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setParms(challengeItem).setType(JkConfiguration.RequstType.challengRecords).getPostBody());

            }

            @Override
            public void saveRemoteSource(String remoteSource) {
                Logger.myLog(TAG,"-----saveRemoteSource="+remoteSource);
            }
        }.getAsObservable();
    }


    //获取排行
    public static  Observable<String> getRopeChallengeRank(String id){

        return new NetworkBoundResource<String>(){
            @Override
            public Observable<String> getFromDb() {
                return null;
            }

            @Override
            public Observable<String> getNoCacheData() {
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
            public Observable<String> getRemoteSource() {

                InitCommonParms<String, BaseUrl, BaseDbPar> parInitCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.extend2 = id;
                return (Observable<String>) RetrofitClient.getInstance().postS002(parInitCommonParms.setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setParms(id).setType(JkConfiguration.RequstType.rope_challenge_rank).getPostBody());
            }

            @Override
            public void saveRemoteSource(String remoteSource) {

            }
        }.getAsObservable();


    }


}
